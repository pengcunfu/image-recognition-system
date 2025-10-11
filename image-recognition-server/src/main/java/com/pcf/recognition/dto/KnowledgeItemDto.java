package com.pcf.recognition.dto;

import lombok.Data;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * 知识条目DTO
 */
@Data
@Builder
public class KnowledgeItemDto {

    /**
     * 条目ID
     */
    private Long id;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 条目名称
     */
    private String name;

    /**
     * 条目键值
     */
    private String key;

    /**
     * 学名
     */
    private String scientificName;

    /**
     * 描述
     */
    private String description;

    /**
     * 详细内容
     */
    private String content;

    /**
     * 图片列表
     */
    private String images;

    /**
     * 特征描述
     */
    private String characteristics;

    /**
     * 栖息地
     */
    private String habitat;

    /**
     * 生命周期
     */
    private String lifespan;

    /**
     * 相关条目
     */
    private String relatedItems;

    /**
     * 标签
     */
    private String tags;

    /**
     * 浏览量
     */
    private Integer viewCount;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 收藏数
     */
    private Integer favoriteCount;

    /**
     * 分享数
     */
    private Integer shareCount;

    /**
     * 难度等级
     */
    private Integer difficulty;

    /**
     * 排序顺序
     */
    private Integer sortOrder;

    /**
     * 状态
     */
    private String status;

    /**
     * 作者ID
     */
    private Long authorId;

    /**
     * 审核者ID
     */
    private Long reviewerId;

    /**
     * 审核时间
     */
    private LocalDateTime reviewTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
