package com.pcf.recognition.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pcf.recognition.entity.User;
import com.pcf.recognition.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
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
    public Map<String, Object> login(String username, String password, String captcha) {
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
                return createErrorResponse("用户不存在");
            }
            
            if (user.getStatus() != User.UserStatus.ACTIVE) {
                return createErrorResponse("用户账户已被禁用");
            }
            
            // 简单密码验证（生产环境应使用加密）
            if (!password.equals(user.getPassword())) {
                return createErrorResponse("密码错误");
            }
            
            // 更新最后登录时间
            user.setLastLoginTime(LocalDateTime.now());
            userRepository.updateById(user);
            
            // 生成token（这里简单模拟）
            String token = "Bearer_" + UUID.randomUUID().toString().replace("-", "");
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "登录成功");
            response.put("token", token);
            response.put("user", Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "name", user.getName() != null ? user.getName() : user.getUsername(),
                "email", user.getEmail(),
                "role", user.getRole().name().toLowerCase(),
                "avatar", user.getAvatar() != null ? user.getAvatar() : "/api/v1/images/default-avatar.png",
                "vipLevel", user.getVipLevel()
            ));
            
            return response;
            
        } catch (Exception e) {
            log.error("登录失败", e);
            return createErrorResponse("登录失败，请稍后重试");
        }
    }

    /**
     * 用户注册
     */
    public Map<String, Object> register(String username, String email, String password, String captcha) {
        log.info("用户注册: username={}, email={}", username, email);
        
        try {
            // 检查用户名是否已存在
            User existingUser = userRepository.selectOne(
                new LambdaQueryWrapper<User>()
                    .eq(User::getUsername, username)
            );
            
            if (existingUser != null) {
                return createErrorResponse("用户名已存在");
            }
            
            // 检查邮箱是否已存在
            existingUser = userRepository.selectOne(
                new LambdaQueryWrapper<User>()
                    .eq(User::getEmail, email)
            );
            
            if (existingUser != null) {
                return createErrorResponse("邮箱已被注册");
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
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "注册成功");
            response.put("userId", newUser.getId());
            
            return response;
            
        } catch (Exception e) {
            log.error("注册失败", e);
            return createErrorResponse("注册失败，请稍后重试");
        }
    }

    /**
     * 忘记密码
     */
    public Map<String, Object> forgotPassword(String email) {
        log.info("忘记密码请求: email={}", email);
        
        try {
            // 检查邮箱是否存在
            User user = userRepository.selectOne(
                new LambdaQueryWrapper<User>()
                    .eq(User::getEmail, email)
            );
            
            if (user == null) {
                return createErrorResponse("邮箱不存在");
            }
            
            // 生成重置令牌（这里简单模拟）
            String resetToken = UUID.randomUUID().toString();
            
            // 实际应该发送邮件，这里只是模拟
            log.info("重置密码令牌已生成: {}", resetToken);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "重置密码邮件已发送");
            response.put("resetToken", resetToken); // 仅用于测试
            
            return response;
            
        } catch (Exception e) {
            log.error("忘记密码处理失败", e);
            return createErrorResponse("处理失败，请稍后重试");
        }
    }

    /**
     * 获取验证码
     */
    public Map<String, Object> getCaptcha() {
        // 生成简单的验证码（实际应该生成图片验证码）
        String captcha = String.valueOf((int) (Math.random() * 9000) + 1000);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("captcha", captcha);
        response.put("captchaImage", "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8/5+hHgAHggJ/PchI7wAAAABJRU5ErkJggg=="); // 占位图片
        
        return response;
    }

    /**
     * 用户退出登录
     */
    public Map<String, Object> logout(String token) {
        log.info("用户退出登录: token={}", token);
        
        // 实际应该将token加入黑名单或清除会话
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "退出登录成功");
        
        return response;
    }
    
    /**
     * 创建错误响应
     */
    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        return response;
    }
}