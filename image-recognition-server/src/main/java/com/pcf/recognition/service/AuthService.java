package com.pcf.recognition.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pcf.recognition.dto.*;
import com.pcf.recognition.entity.User;
import com.pcf.recognition.repository.UserRepository;
import com.wf.captcha.SpecCaptcha;
import com.wf.captcha.base.Captcha;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

/**
 * 认证服务
 * 处理用户登录、注册、密码管理等业务逻辑
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;

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
    public RegisterResponseDto register(String username, String email, String password, String captcha) {
        log.info("用户注册: username={}, email={}", username, email);
        
        try {
            // 检查用户名是否已存在
            User existingUser = userRepository.selectOne(
                new LambdaQueryWrapper<User>()
                    .eq(User::getUsername, username)
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
                    .eq(User::getEmail, email)
            );
            
            if (existingUser != null) {
                return RegisterResponseDto.builder()
                    .success(false)
                    .message("邮箱已被注册")
                    .build();
            }
            
            // 创建新用户
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setEmail(email);
            newUser.setPassword(password); // 生产环境应加密
            newUser.setName(username);
            newUser.setRole(User.UserRole.USER);
            newUser.setStatus(User.UserStatus.ACTIVE);
            newUser.setVipLevel(0);
            
            userRepository.insert(newUser);
            
            return RegisterResponseDto.builder()
                .success(true)
                .message("注册成功")
                .userId(newUser.getId())
                .build();
            
        } catch (Exception e) {
            log.error("注册失败", e);
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
            
            // 生成重置令牌（这里简单模拟）
            String resetToken = UUID.randomUUID().toString();
            
            // 实际应该发送邮件，这里只是模拟
            log.info("重置密码令牌已生成: {}", resetToken);
            
            return ForgotPasswordResponseDto.builder()
                .success(true)
                .message("重置密码邮件已发送")
                .resetToken(resetToken) // 仅用于测试
                .build();
            
        } catch (Exception e) {
            log.error("忘记密码处理失败", e);
            return ForgotPasswordResponseDto.builder()
                .success(false)
                .message("处理失败，请稍后重试")
                .build();
        }
    }

    /**
     * 获取验证码
     */
    public CaptchaResponseDto getCaptcha() {
        try {
            // 创建验证码对象，设置宽度、高度、验证码长度、干扰线数量
            SpecCaptcha specCaptcha = new SpecCaptcha(130, 48, 4);
            
            // 设置验证码类型为数字和字母混合
            specCaptcha.setCharType(Captcha.TYPE_DEFAULT);
            
            // 设置字体
            specCaptcha.setFont(Captcha.FONT_1);
            
            // 获取验证码文本
            String captchaText = specCaptcha.text().toLowerCase();
            
            // 将验证码图片转换为Base64
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            specCaptcha.out(outputStream);
            String captchaImage = "data:image/png;base64," + Base64.getEncoder().encodeToString(outputStream.toByteArray());
            
            // 生成验证码ID
            String captchaId = UUID.randomUUID().toString();
            
            log.info("验证码生成成功: captchaId={}, text={}", captchaId, captchaText);
            
            return CaptchaResponseDto.builder()
                .success(true)
                .captcha(captchaText)
                .captchaImage(captchaImage)
                .captchaId(captchaId)
                .build();
                
        } catch (IOException e) {
            log.error("生成验证码失败", e);
            return CaptchaResponseDto.builder()
                .success(false)
                .captcha(null)
                .captchaImage(null)
                .captchaId(null)
                .build();
        }
    }

    /**
     * 验证验证码
     */
    public boolean verifyCaptcha(String captchaId, String userInput) {
        // 实际项目中应该从Redis或Session中获取存储的验证码
        // 这里简化处理，实际使用时需要配合缓存存储验证码
        log.info("验证验证码: captchaId={}, userInput={}", captchaId, userInput);
        
        // TODO: 从缓存中获取验证码进行验证
        // String storedCaptcha = redisTemplate.opsForValue().get("captcha:" + captchaId);
        // return storedCaptcha != null && storedCaptcha.equalsIgnoreCase(userInput);
        
        // 临时返回true，实际项目中需要实现真正的验证逻辑
        return true;
    }

    /**
     * 用户退出登录
     */
    public OperationResultDto logout(String token) {
        log.info("用户退出登录: token={}", token);
        
        // 实际应该将token加入黑名单或清除会话
        
        return OperationResultDto.builder()
            .success(true)
            .message("退出登录成功")
            .build();
    }
}