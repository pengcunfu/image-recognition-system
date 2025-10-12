package com.pcf.recognition.dto;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 用户相关DTO集合
 * 包含用户信息、设置、统计、活动记录等相关的请求和响应类
 */
public class UserDto {

    /**
     * 用户信息更新请求DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserUpdateRequest {
        /**
         * 真实姓名
         */
        @Size(max = 50, message = "姓名长度不能超过50个字符")
        private String name;

        /**
         * 手机号
         */
        @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
        private String phone;

        /**
         * 个人简介
         */
        @Size(max = 500, message = "个人简介长度不能超过500个字符")
        private String bio;

        /**
         * 头像URL
         */
        @Size(max = 500, message = "头像URL长度不能超过500个字符")
        private String avatar;
    }

    /**
     * 修改密码请求DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChangePasswordRequest {
        @NotBlank(message = "原密码不能为空")
        private String oldPassword;

        @NotBlank(message = "新密码不能为空")
        @Size(min = 6, max = 20, message = "密码长度必须在6-20个字符之间")
        private String newPassword;

        @NotBlank(message = "确认密码不能为空")
        private String confirmPassword;
    }

    /**
     * 用户统计信息DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserStatsDto {
        /**
         * 总识别次数
         */
        private Integer totalRecognitions;

        /**
         * 识别次数（与totalRecognitions相同，保持兼容性）
         */
        private Integer recognitionCount;

        /**
         * 成功识别次数
         */
        private Integer successRecognitions;

        /**
         * 失败识别次数
         */
        private Integer failedRecognitions;

        /**
         * 收藏数量
         */
        private Integer favoriteCount;

        /**
         * 总上传大小（字节）
         */
        private Long totalUploadSize;

        /**
         * 平均置信度
         */
        private Double averageConfidence;

        /**
         * 最后识别时间
         */
        private LocalDateTime lastRecognitionTime;

        /**
         * 发帖数量
         */
        private Integer postCount;

        /**
         * 评论数量
         */
        private Integer commentCount;

        /**
         * 浏览量
         */
        private Integer viewCount;

        /**
         * 获赞数量
         */
        private Integer likeCount;
    }

    /**
     * 用户设置DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserSettingsDto {
        /**
         * 主题设置
         */
        private String theme;

        /**
         * 语言设置
         */
        private String language;

        /**
         * 通知设置
         */
        private NotificationSettings notifications;

        /**
         * 隐私设置
         */
        private PrivacySettings privacy;

        /**
         * 识别设置
         */
        private RecognitionSettings recognition;

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class NotificationSettings {
            private Boolean email;
            private Boolean push;
            private Boolean sms;
        }

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class PrivacySettings {
            private Boolean showProfile;
            private Boolean showHistory;
            private Boolean allowComments;
        }

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class RecognitionSettings {
            private Boolean autoSave;
            private Integer defaultConfidence;
            private Long maxFileSize;
        }
    }

    /**
     * 用户活动记录DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserActivityDto {
        /**
         * 活动ID
         */
        private Long id;

        /**
         * 活动类型
         */
        private String type;

        /**
         * 活动描述
         */
        private String description;

        /**
         * 活动时间
         */
        private LocalDateTime createTime;

        /**
         * 格式化的时间显示
         */
        private String timeDisplay;

        /**
         * 活动元数据
         */
        private Map<String, Object> metadata;
    }

    /**
     * 用户资料响应DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserProfileResponse {
        private Long id;
        private String username;
        private String email;
        private String name;
        private String phone;
        private String avatar;
        private String bio;
        private String role;
        private String status;
        private Integer vipLevel;
        private LocalDateTime lastLoginTime;
        private LocalDateTime createTime;
        private UserStatsDto stats;
    }

    /**
     * 用户列表项DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserListItem {
        private Long id;
        private String username;
        private String name;
        private String email;
        private String avatar;
        private String role;
        private String status;
        private Integer vipLevel;
        private LocalDateTime createTime;
        private LocalDateTime lastLoginTime;
    }

    /**
     * 用户搜索请求DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserSearchRequest {
        private String keyword; // 关键词搜索（用户名、姓名、邮箱）
        private String role; // 角色筛选
        private String status; // 状态筛选
        private Integer vipLevel; // VIP等级筛选
        private LocalDateTime startTime; // 注册开始时间
        private LocalDateTime endTime; // 注册结束时间

        @Min(value = 1, message = "页码不能小于1")
        private int page = 1;

        @Min(value = 1, message = "每页大小不能小于1")
        @Max(value = 100, message = "每页大小不能大于100")
        private int size = 20;

        private String sortBy = "createTime"; // 排序字段
        private String sortOrder = "desc"; // 排序方向
    }

    /**
     * 用户列表响应DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserListResponse {
        private List<UserListItem> users;
        private long total;
        private int page;
        private int size;
        private int totalPages;
    }

    /**
     * 用户状态更新请求DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserStatusUpdateRequest {
        @NotNull(message = "用户ID不能为空")
        private Long userId;

        @NotBlank(message = "状态不能为空")
        @Pattern(regexp = "^(active|inactive|banned)$", message = "状态值不正确")
        private String status;

        private String reason; // 状态变更原因
    }

    /**
     * 批量用户操作请求DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BatchUserOperationRequest {
        @NotEmpty(message = "用户ID列表不能为空")
        private List<Long> userIds;

        @NotBlank(message = "操作类型不能为空")
        private String operation; // activate, deactivate, ban, delete

        private String reason; // 操作原因
    }

    /**
     * 用户操作结果DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserOperationResult {
        private Long userId;
        private boolean success;
        private String message;
    }
}
