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
        private String bio;
        private java.math.BigDecimal balance;
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

    /**
     * 用户收藏列表
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserCollections {
        private java.util.List<CollectionItem> recognitions;
        private java.util.List<CollectionItem> posts;
        private java.util.List<CollectionItem> knowledge;
    }

    /**
     * 收藏项
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CollectionItem {
        private Long id;
        private String type; // recognition, post, knowledge
        private String title;
        private String description;
        private String imageUrl;
        private Integer confidence; // 仅识别结果有
        private Integer likeCount; // 帖子/知识有
        private Integer viewCount; // 帖子/知识有
        private LocalDateTime createdAt;
    }

    /**
     * 用户点赞列表
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserLikes {
        private java.util.List<LikeItem> posts;
        private java.util.List<LikeItem> knowledge;
        private java.util.List<LikeItem> comments;
    }

    /**
     * 点赞项
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LikeItem {
        private Long id;
        private String type; // post, knowledge, comment
        private String title;
        private String content;
        private String author;
        private Integer likeCount;
        private LocalDateTime createdAt;
    }
}
