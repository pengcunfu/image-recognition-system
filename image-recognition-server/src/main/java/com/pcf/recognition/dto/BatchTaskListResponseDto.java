package com.pcf.recognition.dto;

import com.pcf.recognition.entity.BatchRecognition;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 批量任务列表响应DTO
 */
@Data
@Builder
public class BatchTaskListResponseDto {
    
    /** 是否成功 */
    private Boolean success;
    
    /** 任务列表 */
    private List<BatchRecognition> data;
    
    /** 总记录数 */
    private Long total;
    
    /** 总页数 */
    private Long pages;
    
    /** 当前页码 */
    private Long current;
    
    /** 每页大小 */
    private Long size;
}
