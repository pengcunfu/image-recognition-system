package com.pcf.recognition.dto;

import lombok.Data;
import lombok.Builder;

import java.util.List;

/**
 * 知识库分页查询结果DTO
 */
@Data
@Builder
public class KnowledgePageDto {
    
    /** 知识条目列表 */
    private List<KnowledgeItemDto> items;
    
    /** 总数量 */
    private Long total;
    
    /** 总页数 */
    private Long pages;
    
    /** 当前页 */
    private Long current;
    
    /** 每页大小 */
    private Long size;
    
    /** 搜索关键词 */
    private String keyword;
    
    /** 分类 */
    private String category;
}
