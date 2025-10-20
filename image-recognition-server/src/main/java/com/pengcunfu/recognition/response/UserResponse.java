package com.pengcunfu.recognition.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户响应DTO
 */
public class UserResponse {

    /**
     * 用户信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        private Long id;
        private String username;
        private String email;
        private String nickname;
        private String phone;
        private String avatar;
        private Integer role;
        private Integer status;
        private Integer vipLevel;
        private LocalDateTime vipExpireTime;
        private LocalDateTime lastLoginTime;
        private LocalDateTime createdAt;
    }

    /**
     * 登录响应
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginResponse {
        private String token;
        private UserInfo user;
    }

    /**
     * 注册响应
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegisterResponse {
        private Long userId;
        private String username;
        private String email;
    }

    /**
     * 用户统计信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserStats {
        private Long userId;
        private Long recognitionCount;
        private Long postCount;
        private Long commentCount;
        private Long collectCount;
        private Long likeCount;
    }
}
