package com.pcf.recognition.dto;

import lombok.Data;

/**
 * Doubao服务状态响应DTO
 */
@Data
public class DoubaoServiceStatusResponseDto {
    
    private String service;
    
    private String status;
    
    private Long timestamp;
    
    private String version;
}
