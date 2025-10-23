package com.pengcunfu.recognition.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 知识表
 * 存储识别对象的知识信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("knowledge")
public class Knowledge {

    /**
     * 知识ID(主键)
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 分类名称(如"动物"、"植物"、"物品"、"建筑")
     */
    private String category;

    /**
     * 知识标题
     */
    private String title;

    /**
     * 知识内容
     */
    private String content;

    /**
     * 封面图片URL
     */
    private String coverImage;

    /**
     * 配图列表(JSON格式)
     */
    private String images;

    /**
     * 标签(逗号分隔,如"哺乳动物,宠物,家养")
     */
    private String tags;

    /**
     * 作者ID(关联users表)
     */
    private Long authorId;

    /**
     * 浏览次数
     */
    private Integer viewCount;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 收藏数
     */
    private Integer collectCount;

    /**
     * 评论数
     */
    private Integer commentCount;

    /**
     * 审核状态: 0-PENDING待审核, 1-PUBLISHED已发布, 2-REJECTED已拒绝
     */
    private Integer status;

    /**
     * 是否置顶: 0-否, 1-是
     */
    private Integer isTop;

    /**
     * 是否推荐: 0-否, 1-是
     */
    private Integer isFeatured;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}

