package com.pengcunfu.recognition.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 通知响应DTO
 */
public class NotificationResponse {

    /**
     * 通知信息（通用）
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NotificationInfo {
        private Long id;
        private Long userId;
        private Integer type;
        private String title;
        private String content;
        private String linkUrl;
        private Long senderId;
        private String senderName;
        private String senderAvatar;
        private Integer isRead;
        private Long relatedId; // 兼容字段
        private LocalDateTime readTime;
        private LocalDateTime createTime;
        private LocalDateTime createdAt;
    }
}
