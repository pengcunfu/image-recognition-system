package com.pengcunfu.recognition.request.admin;

import lombok.Data;

/**
 * 管理后台 - 识别管理请求
 */
public class AdminRecognitionRequest {

    /**
     * 识别记录查询请求
     */
    @Data
    public static class QueryRecognitionRequest {
        private Integer page = 1;
        private Integer size = 10;
        private Long userId;
        private String category;
        private Integer status;
        private String startTime;
        private String endTime;
    }

    /**
     * 删除识别记录请求
     */
    @Data
    public static class DeleteRecognitionRequest {
        private String reason;
    }
}
