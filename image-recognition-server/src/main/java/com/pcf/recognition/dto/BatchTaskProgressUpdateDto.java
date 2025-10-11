package com.pcf.recognition.dto;

import lombok.Builder;
import lombok.Data;

/**
 * 批量任务进度更新响应DTO
 */
@Data
@Builder
public class BatchTaskProgressUpdateDto {
    
    /** 是否成功 */
    private Boolean success;
    
    /** 响应消息 */
    private String message;
    
    /** 进度百分比 */
    private Integer progress;
}
