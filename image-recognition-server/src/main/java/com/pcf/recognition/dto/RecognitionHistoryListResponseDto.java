package com.pcf.recognition.dto;

import lombok.Data;

import java.util.List;

/**
 * 识别历史列表响应DTO
 */
@Data
public class RecognitionHistoryListResponseDto {
    
    private List<RecognitionHistoryDto> data;
    
    private Long total;
    
    private Long pages;
    
    private Long current;
    
    private Long size;
}
