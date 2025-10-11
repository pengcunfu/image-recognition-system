package com.pcf.recognition.controller;

import com.pcf.recognition.dto.*;
import com.pcf.recognition.service.AuthService;
import com.pcf.recognition.util.JwtUtil;



import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * 用户认证控制器
 * 提供登录、注册、密码重置等功能
 */

@RestController
@RequestMapping("/api/v1/auth")
@Slf4j
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    private final JwtUtil jwtUtil;

    
    @PostMapping("/login")
    // 公开接口，无需权限验证
    public ApiResponse<LoginResponseDto> login(
            @Valid @RequestBody LoginRequest request) {
        
        try {
            LoginResponseDto result = authService.login(
                request.getUsername(), 
                request.getPassword(), 
                request.getCaptcha()
            );
            
            if (result.getSuccess()) {
                // 生成JWT Token
                String token = jwtUtil.generateToken(
                    result.getUser().getId(),
                    result.getUser().getUsername(),
                    result.getUser().getRole()
                );
                
                // 设置Token到响应中
                result.setToken(token);
                
                return ApiResponse.success(result, result.getMessage());
            } else {
                return ApiResponse.error(result.getMessage());
            }
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    
    @PostMapping("/register")
    // 公开接口，无需权限验证
    public ApiResponse<RegisterResponseDto> register(
            @Valid @RequestBody RegisterRequest request) {
        
        try {
            RegisterResponseDto result = authService.register(
                request.getUsername(),
                request.getEmail(),
                request.getPassword(),
                request.getCaptcha()
            );
            
            if (result.getSuccess()) {
                return ApiResponse.success(result, result.getMessage());
            } else {
                return ApiResponse.error(result.getMessage());
            }
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    
    @PostMapping("/forgot-password")
    // 公开接口，无需权限验证
    public ApiResponse<String> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {
        
        log.info("忘记密码请求: email={}", request.getEmail());
        
        // 模拟发送重置邮件
        return ApiResponse.success("密码重置邮件已发送到您的邮箱", "重置邮件发送成功");
    }

    
    @GetMapping("/captcha")
    // 公开接口，无需权限验证
    public ApiResponse<CaptchaResponseDto> getCaptcha() {
        CaptchaResponseDto result = authService.getCaptcha();
        return ApiResponse.success(result, "验证码获取成功");
    }

    
    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<String> logout(@RequestHeader(value = "Authorization", required = false) String token) {
        
        log.info("用户退出登录: token={}", token);
        
        // 模拟清除会话逻辑
        return ApiResponse.success("退出登录成功");
    }

    
    @GetMapping("/validate")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<TokenValidationResponseDto> validateToken(
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        if (token == null) {
            return ApiResponse.error("Token不能为空");
        }
        
        // 验证Token
        if (!jwtUtil.validateToken(token)) {
            return ApiResponse.error("Token无效或已过期");
        }
        
        // 从Token中获取用户信息
        Long userId = jwtUtil.getUserIdFromToken(token);
        String username = jwtUtil.getUsernameFromToken(token);
        String role = jwtUtil.getRoleFromToken(token);
        
        if (userId == null || username == null || role == null) {
            return ApiResponse.error("Token信息不完整");
        }
        
        // 构建用户信息（实际项目中应该从数据库获取最新信息）
        UserInfoDto userInfo = UserInfoDto.builder()
            .id(userId)
            .username(username)
            .name(username) // 简化处理，实际应从数据库获取
            .role(role)
            .build();
            
        TokenValidationResponseDto result = TokenValidationResponseDto.builder()
            .valid(true)
            .userInfo(userInfo)
            .build();
        
        return ApiResponse.success(result, "Token验证成功");
    }
    
    
    @PostMapping("/sms-code")
    // 公开接口，无需权限验证
    public ApiResponse<SmsCodeResponse> sendSmsCode(@RequestBody SmsCodeRequest request) {
        try {
            // 校验手机号格式
            if (!isValidPhoneNumber(request.getPhone())) {
                return ApiResponse.error("手机号格式不正确");
            }
            
            // 生成6位数字验证码
            String code = generateSmsCode();
            
            // 将验证码存储到Redis
            authService.storeSmsCode(request.getPhone(), code);
            
            // TODO: 集成短信服务商发送短信
            // 这里模拟发送成功
            log.info("发送短信验证码到手机号: {}, 验证码: {}", request.getPhone(), code);
            
            SmsCodeResponse response = new SmsCodeResponse();
            response.setPhone(maskPhoneNumber(request.getPhone()));
            response.setCodeExpiry(300); // 5分钟过期
            response.setSendTime(System.currentTimeMillis());
            
            return ApiResponse.success(response, "短信验证码发送成功");
        } catch (Exception e) {
            log.error("发送短信验证码失败", e);
            return ApiResponse.error("短信验证码发送失败");
        }
    }
    
    
    @PostMapping("/sms-code/verify")
    // 公开接口，无需权限验证
    public ApiResponse<Boolean> verifySmsCode(@RequestBody SmsCodeVerifyRequest request) {
        try {
            // 使用AuthService验证短信验证码
            boolean isValid = authService.verifySmsCode(request.getPhone(), request.getCode());
            
            if (isValid) {
                return ApiResponse.success(true, "验证码验证成功");
            } else {
                return ApiResponse.error("验证码错误或已过期");
            }
        } catch (Exception e) {
            log.error("验证短信验证码失败", e);
            return ApiResponse.error("验证码验证失败");
        }
    }
    
    /**
     * 验证手机号格式
     */
    private boolean isValidPhoneNumber(String phone) {
        return phone != null && phone.matches("^1[3-9]\\d{9}$");
    }
    
    /**
     * 生成6位数字验证码
     */
    private String generateSmsCode() {
        return String.format("%06d", (int)(Math.random() * 1000000));
    }
    
    /**
     * 手机号脱敏
     */
    private String maskPhoneNumber(String phone) {
        if (phone == null || phone.length() != 11) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(7);
    }

}
