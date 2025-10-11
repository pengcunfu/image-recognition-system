package com.pcf.recognition.dto;

import lombok.Data;
import lombok.Builder;

import java.util.List;

/**
 * 知识库搜索结果DTO
 */
@Data
@Builder
public class KnowledgeSearchResultDto {

    /**
     * 搜索结果列表
     */
    private List<KnowledgeItemDto> items;

    /**
     * 总数量
     */
    private Integer total;

    /**
     * 当前页
     */
    private Integer page;

    /**
     * 每页大小
     */
    private Integer size;

    /**
     * 总页数
     */
    private Integer pages;

    /**
     * 搜索关键词
     */
    private String keyword;

    /**
     * 搜索分类
     */
    private String category;
}
