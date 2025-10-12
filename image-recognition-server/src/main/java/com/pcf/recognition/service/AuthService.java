package com.pcf.recognition.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pcf.recognition.dto.*;
import com.pcf.recognition.entity.User;
import com.pcf.recognition.repository.UserRepository;
import com.wf.captcha.SpecCaptcha;
import com.wf.captcha.base.Captcha;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;
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
    private final StringRedisTemplate stringRedisTemplate;
    private final EmailService emailService;

    // Redis key前缀
    private static final String CAPTCHA_KEY_PREFIX = "captcha:";
    private static final String SMS_CODE_KEY_PREFIX = "sms_code:";
    private static final String TOKEN_KEY_PREFIX = "token:";

    // 过期时间
    private static final long CAPTCHA_EXPIRE_TIME = 5; // 5分钟
    private static final long SMS_CODE_EXPIRE_TIME = 5; // 5分钟
    private static final long TOKEN_EXPIRE_TIME = 24 * 60; // 24小时（分钟）

    /**
     * 用户登录
     */
    public LoginResponseDto login(String username, String password, String captcha) {
        log.info("用户登录验证: username={}", username);

        try {
            // 查找用户
            User user = userRepository.selectOne(
                    new LambdaQueryWrapper<User>()
                            .eq(User::getUsername, username)
                            .or()
                            .eq(User::getEmail, username)
            );

            if (user == null) {
                return LoginResponseDto.builder()
                        .success(false)
                        .message("用户不存在")
                        .build();
            }

            if (user.getStatus() != User.UserStatus.ACTIVE) {
                return LoginResponseDto.builder()
                        .success(false)
                        .message("用户账户已被禁用")
                        .build();
            }

            // 简单密码验证（生产环境应使用加密）
            if (!password.equals(user.getPassword())) {
                return LoginResponseDto.builder()
                        .success(false)
                        .message("密码错误")
                        .build();
            }

            // 更新最后登录时间
            user.setLastLoginTime(LocalDateTime.now());
            userRepository.updateById(user);

            // 生成token（这里简单模拟）
            String token = "Bearer_" + UUID.randomUUID().toString().replace("-", "");

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

            return LoginResponseDto.builder()
                    .success(true)
                    .message("登录成功")
                    .token(token)
                    .user(userInfo)
                    .build();

        } catch (Exception e) {
            log.error("登录失败", e);
            return LoginResponseDto.builder()
                    .success(false)
                    .message("登录失败，请稍后重试")
                    .build();
        }
    }

    /**
     * 用户注册
     */
    public RegisterResponseDto register(RegisterRequest request) {
        log.info("用户注册: username={}, email={}", request.getUsername(), request.getEmail());

        try {
            // 验证邮箱验证码
            if (request.getEmailCode() != null && !request.getEmailCode().trim().isEmpty()) {
                if (!emailService.verifyEmailCode(request.getEmail(), request.getEmailCode(), "register")) {
                    return RegisterResponseDto.builder()
                            .success(false)
                            .message("邮箱验证码错误或已过期")
                            .build();
                }
            }

            // 验证密码确认
            if (!request.getPassword().equals(request.getConfirmPassword())) {
                return RegisterResponseDto.builder()
                        .success(false)
                        .message("两次输入的密码不一致")
                        .build();
            }

            // 检查用户名是否已存在
            User existingUser = userRepository.selectOne(
                    new LambdaQueryWrapper<User>()
                            .eq(User::getUsername, request.getUsername())
            );

            if (existingUser != null) {
                return RegisterResponseDto.builder()
                        .success(false)
                        .message("用户名已存在")
                        .build();
            }

            // 检查邮箱是否已存在
            existingUser = userRepository.selectOne(
                    new LambdaQueryWrapper<User>()
                            .eq(User::getEmail, request.getEmail())
            );

            if (existingUser != null) {
                return RegisterResponseDto.builder()
                        .success(false)
                        .message("邮箱已被注册")
                        .build();
            }

            // 创建新用户
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

            userRepository.insert(newUser);

            log.info("用户注册成功: username={}, email={}, userId={}", 
                    request.getUsername(), request.getEmail(), newUser.getId());

            return RegisterResponseDto.builder()
                    .success(true)
                    .message("注册成功")
                    .userId(newUser.getId())
                    .build();

        } catch (Exception e) {
            log.error("注册失败: username={}, email={}", request.getUsername(), request.getEmail(), e);
            return RegisterResponseDto.builder()
                    .success(false)
                    .message("注册失败，请稍后重试")
                    .build();
        }
    }

    /**
     * 忘记密码
     */
    public ForgotPasswordResponseDto forgotPassword(String email) {
        log.info("忘记密码请求: email={}", email);

        try {
            // 检查邮箱是否存在
            User user = userRepository.selectOne(
                    new LambdaQueryWrapper<User>()
                            .eq(User::getEmail, email)
            );

            if (user == null) {
                return ForgotPasswordResponseDto.builder()
                        .success(false)
                        .message("邮箱不存在")
                        .build();
            }

            // 发送邮箱验证码
            try {
                emailService.sendEmailCode(email, "reset_password");
                log.info("忘记密码验证码发送成功: email={}", email);
                
                return ForgotPasswordResponseDto.builder()
                        .success(true)
                        .message("重置密码验证码已发送到您的邮箱")
                        .build();
            } catch (Exception e) {
                log.error("发送重置密码验证码失败: email={}", email, e);
                return ForgotPasswordResponseDto.builder()
                        .success(false)
                        .message("发送验证码失败，请稍后重试")
                        .build();
            }

        } catch (Exception e) {
            log.error("忘记密码处理失败", e);
            return ForgotPasswordResponseDto.builder()
                    .success(false)
                    .message("处理失败，请稍后重试")
                    .build();
        }
    }

    /**
     * 重置密码
     */
    public OperationResultDto resetPassword(String email, String newPassword, String emailCode) {
        log.info("重置密码请求: email={}", email);

        try {
            // 验证邮箱验证码
            if (emailCode == null || emailCode.trim().isEmpty()) {
                return OperationResultDto.builder()
                        .success(false)
                        .message("邮箱验证码不能为空")
                        .build();
            }

            if (!emailService.verifyEmailCode(email, emailCode, "reset_password")) {
                return OperationResultDto.builder()
                        .success(false)
                        .message("邮箱验证码错误或已过期")
                        .build();
            }

            // 检查邮箱是否存在
            User user = userRepository.selectOne(
                    new LambdaQueryWrapper<User>()
                            .eq(User::getEmail, email)
            );

            if (user == null) {
                return OperationResultDto.builder()
                        .success(false)
                        .message("邮箱不存在")
                        .build();
            }

            // 更新密码
            user.setPassword(newPassword); // 生产环境应加密
            userRepository.updateById(user);

            log.info("密码重置成功: email={}, userId={}", email, user.getId());

            return OperationResultDto.builder()
                    .success(true)
                    .message("密码重置成功")
                    .build();

        } catch (Exception e) {
            log.error("重置密码失败: email={}", email, e);
            return OperationResultDto.builder()
                    .success(false)
                    .message("重置密码失败，请稍后重试")
                    .build();
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
            String redisKey = CAPTCHA_KEY_PREFIX + sessionId;
            stringRedisTemplate.opsForValue().set(redisKey, captchaText, CAPTCHA_EXPIRE_TIME, TimeUnit.MINUTES);

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
            String redisKey = CAPTCHA_KEY_PREFIX + sessionId;
            String storedCaptcha = stringRedisTemplate.opsForValue().get(redisKey);

            if (storedCaptcha == null || storedCaptcha.isEmpty()) {
                log.warn("验证码不存在或已过期: sessionId={}", sessionId);
                return false;
            }

            // 验证成功后删除验证码（防止重复使用）
            boolean isValid = storedCaptcha.equalsIgnoreCase(userInput.trim());
            if (isValid) {
                stringRedisTemplate.delete(redisKey);
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
        String redisKey = SMS_CODE_KEY_PREFIX + phone;
        stringRedisTemplate.opsForValue().set(redisKey, code, SMS_CODE_EXPIRE_TIME, TimeUnit.MINUTES);
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
            String redisKey = SMS_CODE_KEY_PREFIX + phone;
            String storedCode = stringRedisTemplate.opsForValue().get(redisKey);

            if (storedCode == null) {
                log.warn("短信验证码不存在或已过期: phone={}", phone);
                return false;
            }

            // 验证成功后删除验证码
            boolean isValid = storedCode.equals(userInput.trim());
            if (isValid) {
                stringRedisTemplate.delete(redisKey);
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
     * 用户退出登录
     */
    public OperationResultDto logout(String token) {
        log.info("用户退出登录: token={}", token);

        // 从Redis中删除token
        if (token != null && !token.isEmpty()) {
            deleteTokenFromRedis(token);
        }

        return OperationResultDto.builder()
                .success(true)
                .message("退出登录成功")
                .build();
    }

    /**
     * 将Token存储到Redis
     */
    public void storeTokenToRedis(String token, Long userId, String username, String role) {
        try {
            String redisKey = TOKEN_KEY_PREFIX + token;
            // 存储token相关信息
            String tokenInfo = String.format("%d:%s:%s", userId, username, role);
            stringRedisTemplate.opsForValue().set(redisKey, tokenInfo, TOKEN_EXPIRE_TIME, TimeUnit.MINUTES);
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
            String redisKey = TOKEN_KEY_PREFIX + token;
            stringRedisTemplate.delete(redisKey);
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
            String redisKey = TOKEN_KEY_PREFIX + token;
            String tokenInfo = stringRedisTemplate.opsForValue().get(redisKey);
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
            String redisKey = TOKEN_KEY_PREFIX + token;
            return stringRedisTemplate.opsForValue().get(redisKey);
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
            String redisKey = TOKEN_KEY_PREFIX + token;
            stringRedisTemplate.expire(redisKey, TOKEN_EXPIRE_TIME, TimeUnit.MINUTES);
            log.debug("Token过期时间已刷新: token={}", token);
        } catch (Exception e) {
            log.error("刷新Token过期时间失败: token={}", token, e);
        }
    }
}