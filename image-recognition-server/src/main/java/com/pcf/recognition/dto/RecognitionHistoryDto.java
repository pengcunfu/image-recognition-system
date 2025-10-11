package com.pcf.recognition.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 识别历史记录DTO
 */
@Data
public class RecognitionHistoryDto {
    
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
}
