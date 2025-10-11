package com.pcf.recognition.dto;

import lombok.Builder;
import lombok.Data;

/**
 * 批量任务进度DTO
 */
@Data
@Builder
public class BatchTaskProgressDto {

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
