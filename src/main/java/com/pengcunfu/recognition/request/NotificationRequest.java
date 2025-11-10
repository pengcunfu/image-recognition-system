package com.pengcunfu.recognition.request;

import lombok.Data;

/**
 * 通知请求
 */
public class NotificationRequest {

    /**
     * 通知查询请求
     */
    @Data
    public static class QueryNotificationRequest {
        private Integer page = 1;
        private Integer size = 10;
        private Integer type;
        private Boolean unreadOnly = false;
    }
}
