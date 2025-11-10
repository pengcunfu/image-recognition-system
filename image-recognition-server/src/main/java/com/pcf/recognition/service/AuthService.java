package com.pcf.recognition.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pcf.recognition.dto.AuthDto.*;
import com.pcf.recognition.entity.User;
import com.pcf.recognition.repository.UserRepository;
import com.pcf.recognition.util.RedisClient;
import com.wf.captcha.SpecCaptcha;
import com.wf.captcha.base.Captcha;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * 认证服务
 * 处理用户登录、注册、密码管理等业务逻辑
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RedisClient redisClient;
    private final ObjectMapper objectMapper;

    // Redis key前缀
    private static final String CAPTCHA_KEY_PREFIX = "captcha:";
    private static final String SMS_CODE_KEY_PREFIX = "sms_code:";
    private static final String TOKEN_KEY_PREFIX = "token:";

    // 过期时间
    private static final long CAPTCHA_EXPIRE_TIME = 5; // 5分钟
    private static final long SMS_CODE_EXPIRE_TIME = 5; // 5分钟
    private static final long TOKEN_EXPIRE_TIME = 24 * 60; // 24小时（分钟）

    /**
     * 用户认证
     * 职责：验证用户凭据并返回用户信息，不包含Token生成等逻辑
     */
    public UserInfoDto authenticateUser(String username, String password) {
        log.info("用户认证: username={}", username);

        try {
            // 查找用户
            User user = userRepository.selectOne(
                    new LambdaQueryWrapper<User>()
                            .eq(User::getUsername, username)
                            .or()
                            .eq(User::getEmail, username)
            );

            if (user == null) {
                log.warn("用户不存在: username={}", username);
                return null;
            }

            if (user.getStatus() != User.UserStatus.ACTIVE) {
                log.warn("用户账户已被禁用: username={}", username);
                throw new RuntimeException("用户账户已被禁用");
            }

            // 简单密码验证（生产环境应使用加密）
            if (!password.equals(user.getPassword())) {
                log.warn("密码错误: username={}", username);
                return null;
            }

            // 更新最后登录时间
            user.setLastLoginTime(LocalDateTime.now());
            userRepository.updateById(user);

            // 构建用户信息DTO
            UserInfoDto userInfo = UserInfoDto.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .name(user.getName() != null ? user.getName() : user.getUsername())
                    .email(user.getEmail())
                    .role(user.getRole().name().toLowerCase())
                    .avatar(user.getAvatar() != null ? user.getAvatar() : "/api/v1/images/default-avatar.png")
                    .vipLevel(user.getVipLevel())
                    .lastLoginTime(user.getLastLoginTime())
                    .createTime(user.getCreateTime())
                    .build();

            log.info("用户认证成功: username={}, userId={}", username, user.getId());
            return userInfo;

        } catch (Exception e) {
            log.error("用户认证失败: username={}", username, e);
            throw new RuntimeException("认证失败: " + e.getMessage());
        }
    }

    /**
     * 检查用户名是否已存在
     * 职责：提供用户名唯一性检查服务
     */
    public boolean isUsernameExists(String username) {
        User existingUser = userRepository.selectOne(
                new LambdaQueryWrapper<User>()
                        .eq(User::getUsername, username)
        );
        return existingUser != null;
    }

    /**
     * 检查邮箱是否已存在
     * 职责：提供邮箱唯一性检查服务
     */
    public boolean isEmailExists(String email) {
        User existingUser = userRepository.selectOne(
                new LambdaQueryWrapper<User>()
                        .eq(User::getEmail, email)
        );
        return existingUser != null;
    }

    /**
     * 创建新用户
     * 职责：纯粹的用户创建业务逻辑，不包含验证
     */
    public Long createUser(RegisterRequest request) {
        log.info("创建新用户: username={}, email={}", request.getUsername(), request.getEmail());

        try {
            // 创建新用户对象
            User newUser = new User();
            newUser.setUsername(request.getUsername());
            newUser.setEmail(request.getEmail());
            newUser.setPassword(request.getPassword()); // 生产环境应加密
            newUser.setName(request.getUsername());
            newUser.setRole(User.UserRole.USER);
            newUser.setStatus(User.UserStatus.ACTIVE);
            newUser.setVipLevel(0);
            newUser.setCreateTime(LocalDateTime.now());
            newUser.setLastLoginTime(LocalDateTime.now());
            
            // 保存用户到数据库
            userRepository.insert(newUser);

            log.info("用户创建成功: username={}, email={}, userId={}",
                    request.getUsername(), request.getEmail(), newUser.getId());

            return newUser.getId();

        } catch (Exception e) {
            log.error("创建用户失败: username={}, email={}", request.getUsername(), request.getEmail(), e);
            throw new RuntimeException("创建用户失败: " + e.getMessage());
        }
    }

    /**
     * 更新用户密码
     * 职责：纯粹的密码更新业务逻辑，不包含验证
     */
    public boolean updateUserPassword(String email, String newPassword) {
        log.info("更新用户密码: email={}", email);

        try {
            // 查找用户
            User user = userRepository.selectOne(
                    new LambdaQueryWrapper<User>()
                            .eq(User::getEmail, email)
            );

            if (user == null) {
                log.error("用户不存在: email={}", email);
                throw new RuntimeException("用户不存在");
            }

            // 更新密码
            user.setPassword(newPassword); // 生产环境应加密
            int updateResult = userRepository.updateById(user);

            if (updateResult > 0) {
                log.info("密码更新成功: email={}, userId={}", email, user.getId());
                return true;
            } else {
                log.error("密码更新失败: email={}, userId={}", email, user.getId());
                return false;
            }

        } catch (Exception e) {
            log.error("更新密码失败: email={}", email, e);
            throw new RuntimeException("更新密码失败: " + e.getMessage());
        }
    }

    /**
     * 生成验证码图片
     */
    public SpecCaptcha generateCaptcha(String sessionId) {
        try {
            // 创建验证码对象，设置宽度、高度、验证码长度、干扰线数量
            SpecCaptcha specCaptcha = new SpecCaptcha(130, 48, 4);

            // 设置验证码类型为数字和字母混合
            specCaptcha.setCharType(Captcha.TYPE_DEFAULT);

            // 设置字体
            specCaptcha.setFont(Captcha.FONT_1);

            // 获取验证码文本
            String captchaText = specCaptcha.text().toLowerCase();

            // 使用sessionId作为key存储验证码，设置过期时间
            String redisKey = RedisClient.buildKey(CAPTCHA_KEY_PREFIX, sessionId);
            redisClient.setWithExpire(redisKey, captchaText, CAPTCHA_EXPIRE_TIME, TimeUnit.MINUTES);

            log.info("验证码生成成功: sessionId={}, text={}", sessionId, captchaText);

            return specCaptcha;

        } catch (Exception e) {
            log.error("生成验证码失败", e);
            throw new RuntimeException("验证码生成失败", e);
        }
    }

    /**
     * 验证验证码
     */
    public boolean verifyCaptcha(String sessionId, String userInput) {
        log.info("验证验证码: sessionId={}, userInput={}", sessionId, userInput);

        if (sessionId == null || userInput == null) {
            return false;
        }

        try {
            // 从Redis中获取存储的验证码
            String redisKey = RedisClient.buildKey(CAPTCHA_KEY_PREFIX, sessionId);
            String storedCaptcha = redisClient.get(redisKey);

            if (storedCaptcha == null || storedCaptcha.isEmpty()) {
                log.warn("验证码不存在或已过期: sessionId={}", sessionId);
                return false;
            }

            // 验证成功后删除验证码（防止重复使用）
            boolean isValid = storedCaptcha.equalsIgnoreCase(userInput.trim());
            if (isValid) {
                redisClient.delete(redisKey);
                log.info("验证码验证成功: sessionId={}", sessionId);
            } else {
                log.warn("验证码验证失败: sessionId={}, expected={}, actual={}", sessionId, storedCaptcha, userInput);
            }

            return isValid;
        } catch (Exception e) {
            log.error("验证验证码时发生异常: sessionId={}", sessionId, e);
            return false;
        }
    }

    /**
     * 存储短信验证码
     */
    public void storeSmsCode(String phone, String code) {
        String redisKey = RedisClient.buildKey(SMS_CODE_KEY_PREFIX, phone);
        redisClient.setWithExpire(redisKey, code, SMS_CODE_EXPIRE_TIME, TimeUnit.MINUTES);
        log.info("短信验证码已存储: phone={}, code={}", phone, code);
    }

    /**
     * 验证短信验证码
     */
    public boolean verifySmsCode(String phone, String userInput) {
        log.info("验证短信验证码: phone={}, userInput={}", phone, userInput);

        if (phone == null || userInput == null) {
            return false;
        }

        try {
            String redisKey = RedisClient.buildKey(SMS_CODE_KEY_PREFIX, phone);
            String storedCode = redisClient.get(redisKey);

            if (storedCode == null) {
                log.warn("短信验证码不存在或已过期: phone={}", phone);
                return false;
            }

            // 验证成功后删除验证码
            boolean isValid = storedCode.equals(userInput.trim());
            if (isValid) {
                redisClient.delete(redisKey);
                log.info("短信验证码验证成功: phone={}", phone);
            } else {
                log.warn("短信验证码验证失败: phone={}, expected={}, actual={}", phone, storedCode, userInput);
            }

            return isValid;
        } catch (Exception e) {
            log.error("验证短信验证码时发生异常: phone={}", phone, e);
            return false;
        }
    }


    /**
     * 将Token存储到Redis
     */
    public void storeTokenToRedis(String token, UserInfoDto userInfo) {
        try {
            String redisKey = RedisClient.buildKey(TOKEN_KEY_PREFIX, token);
            // 将用户信息转换为JSON格式存储
            String userInfoJson = objectMapper.writeValueAsString(userInfo);
            redisClient.setWithExpire(redisKey, userInfoJson, TOKEN_EXPIRE_TIME, TimeUnit.MINUTES);
            log.info("Token已存储到Redis: token={}, userId={}", token, userInfo.getId());
        } catch (Exception e) {
            log.error("存储Token到Redis失败: token={}", token, e);
        }
    }

    /**
     * 将Token存储到Redis（兼容旧版本）
     */
    @Deprecated
    public void storeTokenToRedis(String token, Long userId, String username, String role) {
        try {
            String redisKey = RedisClient.buildKey(TOKEN_KEY_PREFIX, token);
            // 存储token相关信息
            String tokenInfo = String.format("%d:%s:%s", userId, username, role);
            redisClient.setWithExpire(redisKey, tokenInfo, TOKEN_EXPIRE_TIME, TimeUnit.MINUTES);
            log.info("Token已存储到Redis: token={}, userId={}", token, userId);
        } catch (Exception e) {
            log.error("存储Token到Redis失败: token={}", token, e);
        }
    }

    /**
     * 从Redis中删除Token
     */
    public void deleteTokenFromRedis(String token) {
        try {
            String redisKey = RedisClient.buildKey(TOKEN_KEY_PREFIX, token);
            redisClient.delete(redisKey);
            log.info("Token已从Redis删除: token={}", token);
        } catch (Exception e) {
            log.error("从Redis删除Token失败: token={}", token, e);
        }
    }

    /**
     * 验证Token是否在Redis中存在
     */
    public boolean isTokenValidInRedis(String token) {
        try {
            String redisKey = RedisClient.buildKey(TOKEN_KEY_PREFIX, token);
            String tokenInfo = redisClient.get(redisKey);
            boolean isValid = tokenInfo != null && !tokenInfo.isEmpty();
            log.debug("Token验证结果: token={}, valid={}", token, isValid);
            return isValid;
        } catch (Exception e) {
            log.error("验证Token时发生异常: token={}", token, e);
            return false;
        }
    }

    /**
     * 从Redis中获取Token信息
     */
    public String getTokenInfoFromRedis(String token) {
        try {
            String redisKey = RedisClient.buildKey(TOKEN_KEY_PREFIX, token);
            return redisClient.get(redisKey);
        } catch (Exception e) {
            log.error("获取Token信息失败: token={}", token, e);
            return null;
        }
    }

    /**
     * 刷新Token过期时间
     */
    public void refreshTokenExpiry(String token) {
        try {
            String redisKey = RedisClient.buildKey(TOKEN_KEY_PREFIX, token);
            redisClient.expire(redisKey, TOKEN_EXPIRE_TIME, TimeUnit.MINUTES);
            log.debug("Token过期时间已刷新: token={}", token);
        } catch (Exception e) {
            log.error("刷新Token过期时间失败: token={}", token, e);
        }
    }

    /**
     * 从Redis中获取用户信息
     */
    public UserInfoDto getUserInfoFromRedis(String token) {
        try {
            String redisKey = RedisClient.buildKey(TOKEN_KEY_PREFIX, token);
            String userInfoJson = redisClient.get(redisKey);
            
            if (userInfoJson != null && !userInfoJson.isEmpty()) {
                // 尝试解析JSON格式的用户信息
                try {
                    return objectMapper.readValue(userInfoJson, UserInfoDto.class);
                } catch (Exception jsonException) {
                    // 如果JSON解析失败，可能是旧格式的数据，尝试解析旧格式
                    log.warn("JSON解析失败，尝试解析旧格式数据: token={}", token);
                    String[] parts = userInfoJson.split(":");
                    if (parts.length >= 3) {
                        return UserInfoDto.builder()
                                .id(Long.parseLong(parts[0]))
                                .username(parts[1])
                                .role(parts[2])
                                .name(parts[1]) // 使用用户名作为显示名称
                                .build();
                    }
                }
            }
            
            return null;
        } catch (Exception e) {
            log.error("从Redis获取用户信息失败: token={}", token, e);
            return null;
        }
    }
}