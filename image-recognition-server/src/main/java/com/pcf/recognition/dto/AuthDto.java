package com.pcf.recognition.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * 认证相关DTO集合
 * 包含登录、注册、密码重置、邮箱验证码等请求和响应类
 */
public class AuthDto {

    // ==================== 请求DTO ====================

    /**
     * 登录请求DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginRequest {
        @NotBlank(message = "用户名不能为空")
        private String username;

        @NotBlank(message = "密码不能为空")
        @Size(min = 6, message = "密码长度至少6位")
        private String password;

        private String captcha; // 验证码可以为空，某些情况下不需要验证码

        @Builder.Default
        private boolean rememberMe = false;
    }

    /**
     * 注册请求DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
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

        @NotBlank(message = "邮箱验证码不能为空")
        @Pattern(regexp = "^\\d{6}$", message = "邮箱验证码必须为6位数字")
        private String emailCode;
    }

    /**
     * 忘记密码请求DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
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
    }


    /**
     * 邮箱验证码请求DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
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
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
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
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateProfileRequest {
        private String name;
        private String phone;
        private String avatar;
        private String bio;
    }

    // ==================== 响应DTO ====================

    /**
     * 登录响应DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginResponseDto {
        /**
         * 是否成功
         */
        private Boolean success;

        /**
         * 响应消息
         */
        private String message;

        /**
         * 访问令牌
         */
        private String token;

        /**
         * 用户信息
         */
        private UserInfoDto user;
    }

    /**
     * 注册响应DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegisterResponseDto {
        /**
         * 是否成功
         */
        private Boolean success;

        /**
         * 响应消息
         */
        private String message;

        /**
         * 新用户ID
         */
        private Long userId;
    }

    /**
     * 邮箱验证码响应DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmailCodeResponse {
        private String email;
        private String maskedEmail; // 脱敏后的邮箱
        private Integer codeExpiry; // 验证码过期时间（秒）
        private Long sendTime; // 发送时间戳
    }

    /**
     * 短信验证码响应DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SmsCodeResponse {
        private String phone;
        private Integer codeExpiry;
        private Long sendTime;
        @Builder.Default
        private String status = "success";
    }

    /**
     * Token验证响应DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TokenValidationResponseDto {
        /**
         * Token是否有效
         */
        private Boolean valid;

        /**
         * 用户信息
         */
        private UserInfoDto userInfo;
    }

    /**
     * 操作结果响应DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OperationResultDto {
        /**
         * 是否成功
         */
        private Boolean success;

        /**
         * 响应消息
         */
        private String message;
    }

    /**
     * 用户信息DTO（用于返回给前端）
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfoDto {
        /**
         * 用户ID
         */
        private Long id;

        /**
         * 用户名
         */
        private String username;

        /**
         * 邮箱
         */
        private String email;

        /**
         * 真实姓名
         */
        private String name;

        /**
         * 手机号
         */
        private String phone;

        /**
         * 头像URL
         */
        private String avatar;

        /**
         * 个人简介
         */
        private String bio;

        /**
         * 用户角色
         */
        private String role;

        /**
         * 用户状态
         */
        private String status;

        /**
         * VIP等级
         */
        private Integer vipLevel;

        /**
         * 最后登录时间
         */
        private LocalDateTime lastLoginTime;

        /**
         * 创建时间
         */
        private LocalDateTime createTime;
    }
}
