package com.pcf.recognition.dto;

import lombok.Data;

import java.util.List;

/**
 * 批量识别响应DTO
 */
@Data
public class BatchRecognitionResponseDto {
    
    private String batchId;
    
    private String status;
    
    private Integer totalFiles;
    
    private Integer successFiles;
    
    private Integer failedFiles;
    
    private List<BatchFileResultDto> results;
    
    private String batchName;
}
