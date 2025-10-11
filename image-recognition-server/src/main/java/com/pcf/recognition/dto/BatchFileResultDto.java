package com.pcf.recognition.dto;

import com.pcf.recognition.entity.RecognitionItem;
import lombok.Data;

import java.util.List;

/**
 * 批量文件处理结果DTO
 */
@Data
public class BatchFileResultDto {
    
    private String fileName;
    
    private Integer index;
    
    private String status;
    
    private String recognitionId;
    
    private List<RecognitionItem> results;
    
    private String imageUrl;
    
    private String error;
}
