package com.pcf.recognition.dto;

import lombok.Builder;
import lombok.Data;

/**
 * 批量任务统计DTO
 */
@Data
@Builder
public class BatchTaskStatsDto {

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
