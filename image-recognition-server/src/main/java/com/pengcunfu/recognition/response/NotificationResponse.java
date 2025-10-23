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
        private String typeName;
        private String title;
        private String content;
        private String data; // JSON格式的附加数据
        private String linkUrl;
        private Long senderId;
        private String senderName;
        private String senderAvatar;
        private Integer isRead;
        private LocalDateTime readTime;
        private LocalDateTime createdAt;
    }

    /**
     * 未读统计
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UnreadCount {
        private Long total;
        private Long system;
        private Long comment;
        private Long like;
        private Long collect;
    }
}
