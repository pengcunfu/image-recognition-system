package com.pcf.recognition.dto;

import lombok.Builder;
import lombok.Data;

/**
 * 批量识别任务创建响应DTO
 */
@Data
@Builder
public class BatchTaskCreateResponseDto {
    
    /** 是否成功 */
    private Boolean success;
    
    /** 响应消息 */
    private String message;
    
    /** 任务ID */
    private Long taskId;
    
    /** 总图片数量 */
    private Integer totalCount;
}
