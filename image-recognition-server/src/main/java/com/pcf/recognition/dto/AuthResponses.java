package com.pcf.recognition.dto;

import lombok.Data;
import lombok.Builder;
import java.time.LocalDateTime;

/**
 * 认证相关响应DTO集合
 * 包含登录、注册、密码重置、邮箱验证码、Token验证等响应类
 */
public class AuthResponses {

    /**
     * 登录响应DTO
     */
    @Data
    @Builder
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
    public static class SmsCodeResponse {
        private String phone;
        private Integer codeExpiry;
        private Long sendTime;
        private String status = "success";
    }

    /**
     * Token验证响应DTO
     */
    @Data
    @Builder
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
