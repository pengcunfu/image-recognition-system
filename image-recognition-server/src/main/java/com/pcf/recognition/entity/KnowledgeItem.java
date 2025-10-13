package com.pcf.recognition.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 知识条目实体
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("knowledge_items")
public class KnowledgeItem {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long categoryId; // 关联分类ID

    private String name; // 名称，如"狗"

    @TableField("`key`") // key是MySQL保留关键字，需要用反引号
    private String key; // 键值，如"dog"

    private String scientificName; // 学名

    private String description; // 描述

    private String content; // 详细内容（富文本）

    private String images; // 图片URLs，JSON格式存储

    private String characteristics; // 特征，JSON格式存储

    private String habitat; // 栖息地/产地

    private String lifespan; // 生命周期/使用寿命

    private String relatedItems; // 相关条目IDs，JSON格式存储

    private String tags; // 标签，JSON格式存储

    private Integer viewCount = 0; // 浏览次数

    private Integer likeCount = 0; // 点赞次数

    private Integer favoriteCount = 0; // 收藏次数

    private Integer shareCount = 0; // 分享次数

    private Integer difficulty = 1; // 难度等级（1.0-5.0）

    private Integer sortOrder = 0; // 排序顺序

    private ItemStatus status = ItemStatus.PUBLISHED; // 条目状态

    private Long authorId; // 创建者ID

    private Long reviewerId; // 审核者ID

    private LocalDateTime reviewTime; // 审核时间

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;

    // 条目状态枚举
    public enum ItemStatus {
        DRAFT("草稿"),
        PENDING("待审核"),
        PUBLISHED("已发布"),
        REJECTED("已拒绝"),
        HIDDEN("已隐藏");

        private final String description;

        ItemStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}
