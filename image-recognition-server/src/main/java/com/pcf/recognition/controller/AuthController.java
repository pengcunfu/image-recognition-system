package com.pcf.recognition.controller;

import com.pcf.recognition.dto.*;
import com.pcf.recognition.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * 用户认证控制器
 * 提供登录、注册、密码重置等功能
 */
@Tag(name = "用户认证", description = "用户登录、注册、密码管理等认证相关接口")
@RestController
@RequestMapping("/api/v1/auth")
@Slf4j
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;

    @Operation(summary = "用户登录", description = "通过用户名/邮箱和密码进行登录认证")
    @PostMapping("/login")
    // 公开接口，无需权限验证
    public ApiResponse<LoginResponseDto> login(
            @Parameter(description = "登录请求数据") @Valid @RequestBody LoginRequest request) {
        
        try {
            LoginResponseDto result = authService.login(
                request.getUsername(), 
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

    @Operation(summary = "用户注册", description = "创建新用户账户")
    @PostMapping("/register")
    // 公开接口，无需权限验证
    public ApiResponse<RegisterResponseDto> register(
            @Parameter(description = "注册请求数据") @Valid @RequestBody RegisterRequest request) {
        
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

    @Operation(summary = "忘记密码", description = "通过邮箱重置密码")
    @PostMapping("/forgot-password")
    // 公开接口，无需权限验证
    public ApiResponse<String> forgotPassword(
            @Parameter(description = "邮箱地址") @Valid @RequestBody ForgotPasswordRequest request) {
        
        log.info("忘记密码请求: email={}", request.getEmail());
        
        // 模拟发送重置邮件
        return ApiResponse.success("密码重置邮件已发送到您的邮箱", "重置邮件发送成功");
    }

    @Operation(summary = "刷新验证码", description = "获取新的验证码")
    @GetMapping("/captcha")
    // 公开接口，无需权限验证
    public ApiResponse<CaptchaResponseDto> getCaptcha() {
        CaptchaResponseDto result = authService.getCaptcha();
        return ApiResponse.success(result, "验证码获取成功");
    }

    @Operation(summary = "退出登录", description = "用户登出，清除会话")
    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<String> logout(@RequestHeader(value = "Authorization", required = false) String token) {
        
        log.info("用户退出登录: token={}", token);
        
        // 模拟清除会话逻辑
        return ApiResponse.success("退出登录成功");
    }

    @Operation(summary = "验证Token", description = "验证用户token是否有效")
    @GetMapping("/validate")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<TokenValidationResponseDto> validateToken(
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        if (token == null || !token.startsWith("token_")) {
            return ApiResponse.error("Token无效");
        }
        
        // 模拟token验证
        UserInfoDto userInfo = UserInfoDto.builder()
            .id(1L)
            .username("user")
            .name("张三")
            .role("user")
            .build();
            
        TokenValidationResponseDto result = TokenValidationResponseDto.builder()
            .valid(true)
            .userInfo(userInfo)
            .build();
        
        return ApiResponse.success(result, "Token验证成功");
    }
    
    @Operation(summary = "发送短信验证码", description = "发送短信验证码到指定手机号")
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
            
            // TODO: 集成短信服务商发送短信
            // 这里模拟发送成功
            log.info("发送短信验证码到手机号: {}, 验证码: {}", request.getPhone(), code);
            
            // 实际项目中应该将验证码存储到Redis中，设置过期时间
            // redisTemplate.opsForValue().set("SMS_CODE:" + request.getPhone(), code, 5, TimeUnit.MINUTES);
            
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
    
    @Operation(summary = "验证短信验证码", description = "验证用户输入的短信验证码")
    @PostMapping("/sms-code/verify")
    // 公开接口，无需权限验证
    public ApiResponse<Boolean> verifySmsCode(@RequestBody SmsCodeVerifyRequest request) {
        try {
            // TODO: 从Redis中获取验证码进行验证
            // String storedCode = redisTemplate.opsForValue().get("SMS_CODE:" + request.getPhone());
            
            // 这里模拟验证逻辑
            String storedCode = "123456"; // 模拟存储的验证码
            
            if (storedCode == null) {
                return ApiResponse.error("验证码已过期或不存在");
            }
            
            if (!storedCode.equals(request.getCode())) {
                return ApiResponse.error("验证码错误");
            }
            
            // 验证成功后删除验证码
            // redisTemplate.delete("SMS_CODE:" + request.getPhone());
            
            log.info("短信验证码验证成功，手机号: {}", request.getPhone());
            return ApiResponse.success(true, "验证码验证成功");
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
