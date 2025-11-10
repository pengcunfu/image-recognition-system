package com.pcf.recognition.dto;

import com.pcf.recognition.entity.BatchRecognition;
import com.pcf.recognition.entity.BatchRecognitionItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 批量识别相关DTO集合
 * 包含批量任务创建、列表、详情、进度、统计等相关的请求和响应类
 */
public class BatchDto {

    /**
     * 批量识别任务创建响应DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BatchTaskCreateResponseDto {
        /**
         * 是否成功
         */
        private Boolean success;

        /**
         * 响应消息
         */
        private String message;

        /**
         * 任务ID
         */
        private Long taskId;

        /**
         * 总图片数量
         */
        private Integer totalCount;
    }

    /**
     * 批量任务列表响应DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BatchTaskListResponseDto {
        /**
         * 是否成功
         */
        private Boolean success;

        /**
         * 任务列表
         */
        private List<BatchRecognition> data;

        /**
         * 总记录数
         */
        private Long total;
        
        /**
         * 总页数
         */
        private Long pages;

        /**
         * 当前页码
         */
        private Long current;

        /**
         * 每页大小
         */
        private Long size;
    }

    /**
     * 批量任务详情DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BatchTaskDetailDto {
        /**
         * 批量任务信息
         */
        private BatchRecognition task;

        /**
         * 任务项列表
         */
        private List<BatchRecognitionItem> items;
    }

    /**
     * 批量任务进度DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BatchTaskProgressDto {
        /**
         * 任务ID
         */
        private Long taskId;

        /**
         * 任务状态
         */
        private String status;

        /**
         * 进度百分比
         */
        private Integer progress;

        /**
         * 总数量
         */
        private Integer totalCount;

        /**
         * 已处理数量
         */
        private Integer processedCount;

        /**
         * 成功数量
         */
        private Integer successCount;

        /**
         * 失败数量
         */
        private Integer failedCount;
    }

    /**
     * 批量任务统计DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BatchTaskStatsDto {
        /**
         * 总任务数
         */
        private Long totalTasks;

        /**
         * 已完成任务数
         */
        private Long completedTasks;

        /**
         * 处理中任务数
         */
        private Long processingTasks;

        /**
         * 待处理任务数
         */
        private Long pendingTasks;
    }

    /**
     * 批量识别任务创建请求DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BatchTaskCreateRequest {
        /**
         * 任务名称
         */
        private String taskName;

        /**
         * 任务描述
         */
        private String description;

        /**
         * 自定义提示词
         */
        private String customPrompt;

        /**
         * 是否详细分析
         */
        @Builder.Default
        private Boolean detailedAnalysis = false;
    }

    /**
     * 批量任务查询请求DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BatchTaskQueryRequest {
        /**
         * 页码
         */
        @Builder.Default
        private Integer page = 1;

        /**
         * 每页大小
         */
        @Builder.Default
        private Integer size = 10;

        /**
         * 任务状态
         */
        private String status;

        /**
         * 开始时间
         */
        private String startTime;

        /**
         * 结束时间
         */
        private String endTime;
    }

    /**
     * 批量任务操作结果DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BatchTaskOperationResult {
        /**
         * 是否成功
         */
        private Boolean success;

        /**
         * 操作消息
         */
        private String message;

        /**
         * 受影响的任务数量
         */
        private Integer affectedCount;
    }

    /**
     * 批量识别响应DTO (用于ImageRecognitionController)
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BatchRecognitionResponseDto {
        private String batchId;
        private String status;
        private Integer totalFiles;
        private Integer successFiles;
        private Integer failedFiles;
        private List<BatchFileResultDto> results;
        private String batchName;
    }

    /**
     * 批量文件处理结果DTO (用于ImageRecognitionController)
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BatchFileResultDto {
        private String fileName;
        private Integer index;
        private String status;
        private String recognitionId;
        private List<com.pcf.recognition.entity.RecognitionItem> results;
        private String imageUrl;
        private String error;
    }

    /**
     * 批量任务进度更新响应DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BatchTaskProgressUpdateDto {
        /**
         * 是否成功
         */
        private Boolean success;

        /**
         * 响应消息
         */
        private String message;

        /**
         * 进度百分比
         */
        private Integer progress;
    }
}
