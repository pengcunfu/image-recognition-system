package com.pcf.recognition.dto;

import lombok.Data;
import lombok.Builder;

/**
 * 知识库统计信息DTO
 */
@Data
@Builder
public class KnowledgeStatsDto {
    
    /** 总分类数 */
    private Long totalCategories;
    
    /** 总条目数 */
    private Long totalItems;
    
    /** 总浏览量 */
    private Long totalViews;
    
    /** 月增长率 */
    private Double monthlyGrowth;
    
    /** 总点赞数 */
    private Long totalLikes;
    
    /** 总收藏数 */
    private Long totalFavorites;
    
    /** 平均难度 */
    private Double averageDifficulty;
}
