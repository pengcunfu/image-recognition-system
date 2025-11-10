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
         * 图片名称
         */
        private String imageName;

        /**
         * 图片大小(字节)
         */
        private Integer imageSize;

        /**
         * 图片宽度
         */
        private Integer imageWidth;

        /**
         * 图片高度
         */
        private Integer imageHeight;

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

    /**
     * 识别记录查询请求（管理员功能）
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
     * 删除识别记录请求（管理员功能）
     */
    @Data
    public static class DeleteRecognitionRequest {
        private String reason;
    }

    /**
     * 高级图像识别请求（VIP功能）
     */
    @Data
    public static class AdvancedRecognitionRequest {
        @NotNull(message = "图片不能为空")
        private String imageUrl;

        /**
         * 图片名称
         */
        private String imageName;

        /**
         * 图片大小(字节)
         */
        private Integer imageSize;

        /**
         * 图片宽度
         */
        private Integer imageWidth;

        /**
         * 图片高度
         */
        private Integer imageHeight;

        /**
         * 识别类型: 1-高级识别
         */
        private Integer recognitionType = 1;

        /**
         * 高级设置JSON字符串
         */
        private String settings;
    }

    /**
     * 批量高级识别请求（VIP功能）
     */
    @Data
    public static class BatchAdvancedRecognitionRequest {
        @NotNull(message = "图片列表不能为空")
        private String[] imageUrls;

        /**
         * 识别类型: 1-高级识别
         */
        private Integer recognitionType = 1;

        /**
         * 高级设置JSON字符串
         */
        private String settings;
    }
}
