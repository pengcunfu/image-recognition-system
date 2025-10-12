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
 * 识别相关DTO集合
 * 包含图像识别、识别历史、识别统计、收藏等相关的请求和响应类
 */
public class RecognitionDto {

    /**
     * 图像识别请求DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImageRecognitionRequest {
        /**
         * 图像的Base64编码数据
         */
        private String imageBase64;

        /**
         * 图像URL (可选, 与imageBase64二选一)
         */
        private String imageUrl;

        /**
         * 自定义提示词 (可选, 不提供则使用默认提示词)
         */
        private String customPrompt;

        /**
         * 是否详细分析 (默认false, true时会返回更多详细信息)
         */
        @Builder.Default
        private Boolean detailedAnalysis = false;

        /**
         * 最大token数 (可选, 默认500)
         */
        @Builder.Default
        private Integer maxTokens = 500;

        /**
         * 温度参数 (可选, 0.0-1.0, 默认0.1保证准确性)
         */
        @Builder.Default
        private Double temperature = 0.1;
    }

    /**
     * 图像识别响应DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImageRecognitionResponse {
        /**
         * 识别是否成功
         */
        private Boolean success;

        /**
         * 错误信息 (失败时)
         */
        private String errorMessage;

        /**
         * 识别结果
         */
        private RecognitionData data;

        /**
         * 处理时间 (毫秒)
         */
        private Long processingTime;

        /**
         * 使用的token数量
         */
        private Integer tokenUsage;

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class RecognitionData {
            /**
             * 物体类别
             */
            private String category;

            /**
             * 具体名称
             */
            private String name;

            /**
             * 主要颜色
             */
            private String color;

            /**
             * 形状特征
             */
            private String shape;

            /**
             * 材质纹理
             */
            private String material;

            /**
             * 关键属性列表
             */
            private List<String> attributes;

            /**
             * 置信度 (0-1之间)
             */
            private Double confidence;

            /**
             * 原始AI响应 (调试用)
             */
            private String rawResponse;
        }
    }

    /**
     * Doubao连接测试响应DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DoubaoConnectionTestResponseDto {
        private Boolean connected;
        private String message;
        private Long timestamp;
        private String error;
    }

    /**
     * Doubao服务状态响应DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DoubaoServiceStatusResponseDto {
        private String service;
        private String status;
        private Long timestamp;
        private String version;
    }

    /**
     * 识别历史记录DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecognitionHistoryDto {
        private Long id;
        private Long userId;
        private String originalFilename;
        private String imageUrl;
        private String thumbnailUrl;
        private String result;
        private String category;
        private Integer confidence;
        private Integer processingTime;
        private Integer imageWidth;
        private Integer imageHeight;
        private Long fileSize;
        private String tags;
        private String alternatives;
        private String attributes;
        private Boolean isFavorite;
        private String status;
        private String errorMessage;
        private LocalDateTime createTime;
        private LocalDateTime updateTime;
        
        // 扩展字段
        private String recognitionType; // 识别类型
        private Map<String, Object> metadata; // 元数据
        private List<String> relatedImages; // 相关图片
    }

    /**
     * 识别历史列表响应DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecognitionHistoryListResponseDto {
        private List<RecognitionHistoryDto> data;
        private Long total;
        private Long pages;
        private Long current;
        private Long size;
        private String category;
        private String status;
        private String sortBy;
        private String sortOrder;
    }

    /**
     * 识别历史详情响应DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecognitionHistoryDetailResponseDto {
        private RecognitionHistoryDto history;
        private List<RecognitionHistoryDto> relatedRecognitions; // 相关识别记录
        private Map<String, Object> analysisDetails; // 详细分析结果
    }

    /**
     * 收藏切换响应DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FavoriteToggleResponseDto {
        private Boolean isFavorite;
        private String message;
        private Long recognitionId;
    }

    /**
     * 识别统计信息DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecognitionStatsDto {
        private Long totalCount;
        private Long successCount;
        private Long failedCount;
        private Long favoriteCount;
        private Double successRate;
        
        // 扩展统计字段
        private Long todayCount; // 今日识别数
        private Long weekCount; // 本周识别数
        private Long monthCount; // 本月识别数
        private Map<String, Long> categoryStats; // 分类统计
        private Map<String, Long> monthlyStats; // 月度统计
        private Double averageConfidence; // 平均置信度
        private Long averageProcessingTime; // 平均处理时间
    }

    /**
     * 识别历史搜索请求DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecognitionHistorySearchRequest {
        @Size(max = 100, message = "搜索关键词长度不能超过100字符")
        private String keyword;

        private String category;
        private String status;
        private String recognitionType;

        @Min(value = 0, message = "最小置信度不能小于0")
        @Max(value = 100, message = "最大置信度不能大于100")
        private Integer minConfidence;

        @Min(value = 0, message = "最小置信度不能小于0")
        @Max(value = 100, message = "最大置信度不能大于100")
        private Integer maxConfidence;

        private LocalDateTime startTime;
        private LocalDateTime endTime;

        private Boolean isFavorite;

        @Min(value = 1, message = "页码不能小于1")
        private Integer page = 1;

        @Min(value = 1, message = "每页大小不能小于1")
        @Max(value = 100, message = "每页大小不能大于100")
        private Integer size = 20;

        private String sortBy = "createTime"; // 排序字段
        private String sortOrder = "desc"; // 排序方向
    }

    /**
     * 批量删除请求DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BatchDeleteRequest {
        @NotEmpty(message = "ID列表不能为空")
        @Size(max = 100, message = "批量删除最多支持100条记录")
        private List<Long> ids;

        private String reason; // 删除原因
    }

    /**
     * 批量操作响应DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BatchOperationResponseDto {
        private Integer totalCount; // 总数
        private Integer successCount; // 成功数
        private Integer failureCount; // 失败数
        private List<String> errors; // 错误信息列表
        private String message;
    }

    /**
     * 识别结果导出请求DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExportRequest {
        @NotEmpty(message = "导出ID列表不能为空")
        private List<Long> ids;

        @NotBlank(message = "导出格式不能为空")
        @Pattern(regexp = "^(excel|csv|json)$", message = "导出格式不正确")
        private String format;

        private Boolean includeImages = false; // 是否包含图片
        private List<String> fields; // 导出字段
    }

    /**
     * 识别结果导出响应DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExportResponseDto {
        private String downloadUrl;
        private String filename;
        private Long fileSize;
        private String format;
        private Integer recordCount;
        private LocalDateTime exportTime;
    }

    /**
     * 识别历史分析DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecognitionAnalysisDto {
        private Map<String, Long> categoryDistribution; // 分类分布
        private Map<String, Double> confidenceDistribution; // 置信度分布
        private Map<String, Long> timeDistribution; // 时间分布
        private List<String> topCategories; // 热门分类
        private Double averageAccuracy; // 平均准确率
        private Map<String, Object> trends; // 趋势数据
    }

    /**
     * 识别记录标签DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecognitionTagDto {
        private Long id;
        private String name;
        private String color;
        private String description;
        private Integer usageCount;
        private LocalDateTime createTime;
    }

    /**
     * 添加标签请求DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddTagRequest {
        @NotNull(message = "识别记录ID不能为空")
        private Long recognitionId;

        @NotEmpty(message = "标签列表不能为空")
        private List<String> tags;
    }

    /**
     * 识别记录评价DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecognitionRatingDto {
        private Long recognitionId;
        
        @Min(value = 1, message = "评分不能小于1")
        @Max(value = 5, message = "评分不能大于5")
        private Integer rating;
        
        @Size(max = 500, message = "评价内容长度不能超过500字符")
        private String comment;
        
        private LocalDateTime createTime;
    }

    /**
     * 识别记录分享DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecognitionShareDto {
        private String shareUrl;
        private String shareCode;
        private LocalDateTime expireTime;
        private Boolean isPublic;
        private String description;
    }
}
