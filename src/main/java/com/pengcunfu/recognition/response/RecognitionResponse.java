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
        private Integer imageSize;
        private Integer imageWidth;
        private Integer imageHeight;
        private Integer recognitionType;
        private Integer isAdvanced;
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

    /**
     * 识别统计数据
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecognitionStats {
        private Long total;              // 总识别次数
        private Long thisMonth;          // 本月识别次数
        private Double averageConfidence; // 平均置信度
        private Long favorites;          // 收藏数量（暂时返回0）
    }

    /**
     * VIP识别统计数据
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VipRecognitionStats {
        private Long totalRecognitions;   // 总识别次数
        private Long advancedRecognitions; // 高级识别次数
        private Double averageConfidence; // 平均置信度
        private Long categoryCount;       // 分类数量
        private Long tagCount;           // 标签数量
    }
}
