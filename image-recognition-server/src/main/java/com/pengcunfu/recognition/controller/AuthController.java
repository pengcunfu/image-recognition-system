package com.pengcunfu.recognition.controller;

import com.pengcunfu.recognition.annotation.Role;
import com.pengcunfu.recognition.request.AuthRequest;
import com.pengcunfu.recognition.response.ApiResponse;
import com.pengcunfu.recognition.response.UserResponse;
import com.pengcunfu.recognition.service.AuthService;
import com.pengcunfu.recognition.service.CaptchaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

/**
 * 认证控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final CaptchaService captchaService;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public ApiResponse<UserResponse.LoginResponse> login(
            @Valid @RequestBody AuthRequest.LoginRequest request) {
        log.info("用户登录: username={}", request.getUsername());
        UserResponse.LoginResponse response = authService.login(request);
        return ApiResponse.success(response);
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public ApiResponse<UserResponse.RegisterResponse> register(
            @Valid @RequestBody AuthRequest.RegisterRequest request) {
        log.info("用户注册: username={}", request.getUsername());
        UserResponse.RegisterResponse response = authService.register(request);
        return ApiResponse.success(response);
    }

    /**
     * 退出登录
     */
    @Role("USER")
    @PostMapping("/logout")
    public ApiResponse<Void> logout() {
        Long userId = com.pengcunfu.recognition.security.SecurityContextHolder.getCurrentUserId();
        log.info("用户退出登录: userId={}", userId);
        authService.logout(userId);
        return ApiResponse.success();
    }

    /**
     * 刷新Token
     */
    @Role("USER")
    @PostMapping("/refresh")
    public ApiResponse<UserResponse.LoginResponse> refreshToken() {
        Long userId = com.pengcunfu.recognition.security.SecurityContextHolder.getCurrentUserId();
        log.info("刷新Token: userId={}", userId);
        UserResponse.LoginResponse response = authService.refreshToken(userId);
        return ApiResponse.success(response);
    }

    /**
     * 发送验证码
     */
    @PostMapping("/send-code")
    public ApiResponse<Void> sendVerificationCode(
            @RequestParam String email,
            @RequestParam String type) {
        log.info("发送验证码: email={}, type={}", email, type);
        authService.sendVerificationCode(email, type);
        return ApiResponse.success();
    }

    /**
     * 忘记密码
     */
    @PostMapping("/forgot-password")
    public ApiResponse<Void> forgotPassword(
            @RequestParam String email,
            @RequestParam String code,
            @RequestParam String newPassword) {
        log.info("忘记密码: email={}", email);
        authService.forgotPassword(email, code, newPassword);
        return ApiResponse.success();
    }

    /**
     * 重置密码
     */
    @PostMapping("/reset-password")
    public ApiResponse<Void> resetPassword(
            @Valid @RequestBody AuthRequest.ResetPasswordRequest request) {
        log.info("重置密码");
        authService.resetPassword(request);
        return ApiResponse.success();
    }

    /**
     * 获取图形验证码
     */
    @GetMapping("/captcha")
    public ApiResponse<Map<String, String>> getCaptcha() {
        log.info("获取图形验证码");
        Map<String, String> captcha = captchaService.generateCaptcha();
        return ApiResponse.success(captcha);
    }
}

