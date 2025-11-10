package com.pengcunfu.recognition.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 社区帖子表
 * 存储用户发布的社区帖子
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("community_posts")
public class CommunityPost {

    /**
     * 帖子ID(主键)
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID(关联users表)
     */
    private Long userId;

    /**
     * 关联识别结果ID(关联recognition_results表)
     */
    private Long recognitionId;

    /**
     * 帖子标题
     */
    private String title;

    /**
     * 帖子内容
     */
    private String content;

    /**
     * 图片URL列表(JSON格式)
     */
    private String images;

    /**
     * 帖子分类(如"动物识别"、"植物识别")
     */
    private String category;

    /**
     * 标签(逗号分隔)
     */
    private String tags;

    /**
     * 浏览次数
     */
    private Integer viewCount;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 评论数
     */
    private Integer commentCount;

    /**
     * 收藏数
     */
    private Integer collectCount;

    /**
     * 是否置顶: 0-否, 1-是
     */
    private Integer isTop;

    /**
     * 是否精选: 0-否, 1-是
     */
    private Integer isFeatured;

    /**
     * 帖子状态: 0-PENDING待审核, 1-PUBLISHED已发布, 2-REJECTED已拒绝, 3-DELETED已删除
     */
    private Integer status;

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
