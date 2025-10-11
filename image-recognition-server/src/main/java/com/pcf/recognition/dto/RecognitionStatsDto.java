package com.pcf.recognition.dto;

import lombok.Data;

/**
 * 识别统计信息DTO
 */
@Data
public class RecognitionStatsDto {

    private Long totalCount;

    private Long successCount;

    private Long failedCount;

    private Long favoriteCount;

    private Double successRate;
}
