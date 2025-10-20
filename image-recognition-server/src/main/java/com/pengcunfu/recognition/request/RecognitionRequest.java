package com.pengcunfu.recognition.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 识别请求
 */
public class RecognitionRequest {

    /**
     * 图像识别请求
     */
    @Data
    public static class ImageRecognitionRequest {
        @NotNull(message = "图片不能为空")
        private String imageUrl;

        /**
         * 识别类型: 0-QUICK快速识别, 1-DETAILED详细识别
         */
        private Integer recognitionType = 0;
    }

    /**
     * 批量识别请求
     */
    @Data
    public static class BatchRecognitionRequest {
        @NotNull(message = "图片列表不能为空")
        private String[] imageUrls;

        private Integer recognitionType = 0;
    }

    /**
     * 识别历史查询请求
     */
    @Data
    public static class HistoryQueryRequest {
        private Integer page = 1;
        private Integer size = 10;
        private String category;
        private Integer status;
        private String startTime;
        private String endTime;
    }
}
