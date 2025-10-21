package com.pengcunfu.recognition.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 识别响应DTO
 */
public class RecognitionResponse {

    /**
     * 识别信息（通用）
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecognitionInfo {
        private Long id;
        private Long userId;
        private String imageUrl;
        private String imageName;
        private Integer recognitionType;
        private String resultJson;
        private String mainCategory;
        private String category; // 兼容字段
        private String objectName; // 兼容字段
        private BigDecimal confidence;
        private String tags;
        private String attributes; // 兼容字段
        private String description;
        private Integer processingTime;
        private Integer status;
        private String errorMessage;
        private LocalDateTime createdAt;
    }
}
