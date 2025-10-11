package com.pcf.recognition.dto;

import com.pcf.recognition.entity.BatchRecognition;
import com.pcf.recognition.entity.BatchRecognitionItem;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 批量任务详情DTO
 */
@Data
@Builder
public class BatchTaskDetailDto {

    /**
     * 批量任务信息
     */
    private BatchRecognition task;

    /**
     * 任务项列表
     */
    private List<BatchRecognitionItem> items;
}
