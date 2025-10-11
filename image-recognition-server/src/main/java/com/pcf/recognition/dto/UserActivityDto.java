package com.pcf.recognition.dto;

import lombok.Data;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 用户活动记录DTO
 */
@Data
@Builder
public class UserActivityDto {
    
    /** 活动ID */
    private Long id;
    
    /** 活动类型 */
    private String type;
    
    /** 活动描述 */
    private String description;
    
    /** 活动时间 */
    private LocalDateTime createTime;
    
    /** 格式化的时间显示 */
    private String timeDisplay;
    
    /** 活动元数据 */
    private Map<String, Object> metadata;
}
