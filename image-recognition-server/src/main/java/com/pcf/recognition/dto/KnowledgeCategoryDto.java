package com.pcf.recognition.dto;

import lombok.Data;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * 知识分类DTO
 */
@Data
@Builder
public class KnowledgeCategoryDto {
    
    /** 分类ID */
    private Long id;
    
    /** 分类名称 */
    private String name;
    
    /** 分类键值 */
    private String key;
    
    /** 分类描述 */
    private String description;
    
    /** 分类图片 */
    private String image;
    
    /** 条目数量 */
    private Integer itemCount;
    
    /** 排序顺序 */
    private Integer sortOrder;
    
    /** 分类状态 */
    private String status;
    
    /** 创建时间 */
    private LocalDateTime createTime;
    
    /** 更新时间 */
    private LocalDateTime updateTime;
}
