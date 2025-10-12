package com.pcf.recognition.dto;

import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 认证相关请求DTO集合
 * 包含登录、注册、密码重置、邮箱验证码等请求类
 */
public class AuthRequests {

    /**
     * 登录请求DTO
     */
    @Data
    public static class LoginRequest {
        @NotBlank(message = "用户名不能为空")
        private String username;

        @NotBlank(message = "密码不能为空")
        @Size(min = 6, message = "密码长度至少6位")
        private String password;

        private String captcha; // 验证码可以为空，某些情况下不需要验证码

        private boolean rememberMe = false;
    }

    /**
     * 注册请求DTO
     */
    @Data
    public static class RegisterRequest {
        @NotBlank(message = "用户名不能为空")
        @Size(min = 3, max = 20, message = "用户名长度必须在3-20位之间")
        @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "用户名只能包含字母、数字和下划线")
        private String username;

        @NotBlank(message = "邮箱不能为空")
        @Email(message = "邮箱格式不正确")
        private String email;

        @NotBlank(message = "密码不能为空")
        @Size(min = 6, message = "密码长度至少6位")
        private String password;

        @NotBlank(message = "确认密码不能为空")
        private String confirmPassword;

        private String captcha;

        @NotBlank(message = "邮箱验证码不能为空")
        @Pattern(regexp = "^\\d{6}$", message = "邮箱验证码必须为6位数字")
        private String emailCode;

        private boolean acceptTerms = false;
    }

    /**
     * 忘记密码请求DTO
     */
    @Data
    public static class ForgotPasswordRequest {
        @NotBlank(message = "邮箱不能为空")
        @Email(message = "邮箱格式不正确")
        private String email;

        @NotBlank(message = "邮箱验证码不能为空")
        @Pattern(regexp = "^\\d{6}$", message = "邮箱验证码必须为6位数字")
        private String emailCode;

        @NotBlank(message = "新密码不能为空")
        @Size(min = 6, message = "新密码长度至少6位")
        private String newPassword;

        @NotBlank(message = "确认密码不能为空")
        private String confirmPassword;
    }

    /**
     * 重置密码请求DTO
     */
    @Data
    public static class ResetPasswordRequest {
        @NotBlank(message = "邮箱不能为空")
        @Email(message = "邮箱格式不正确")
        private String email;

        @NotBlank(message = "新密码不能为空")
        @Size(min = 6, message = "密码长度至少6位")
        private String newPassword;

        @NotBlank(message = "确认密码不能为空")
        private String confirmPassword;

        @NotBlank(message = "邮箱验证码不能为空")
        @Pattern(regexp = "^\\d{6}$", message = "邮箱验证码必须为6位数字")
        private String emailCode;
    }

    /**
     * 邮箱验证码请求DTO
     */
    @Data
    public static class EmailCodeRequest {
        @NotBlank(message = "邮箱不能为空")
        @Email(message = "邮箱格式不正确")
        private String email;

        @NotBlank(message = "验证码类型不能为空")
        private String type; // register, forgot_password, change_email
    }

    /**
     * 修改密码请求DTO
     */
    @Data
    public static class ChangePasswordRequest {
        @NotBlank(message = "当前密码不能为空")
        private String currentPassword;

        @NotBlank(message = "新密码不能为空")
        @Size(min = 6, message = "新密码长度至少6位")
        private String newPassword;

        @NotBlank(message = "确认密码不能为空")
        private String confirmPassword;
    }

    /**
     * 更新用户资料请求DTO
     */
    @Data
    public static class UpdateProfileRequest {
        private String name;
        private String phone;
        private String avatar;
        private String bio;
    }
}
