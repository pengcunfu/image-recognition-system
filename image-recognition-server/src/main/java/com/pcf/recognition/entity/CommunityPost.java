package com.pcf.recognition.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 社区帖子实体
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("community_posts")
public class CommunityPost {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;

    private String content;

    private Long authorId;

    private String category;

    private String tags; // JSON格式存储标签数组

    private String images; // JSON格式存储图片URL数组

    private Integer viewCount = 0;

    private Integer likeCount = 0;

    private Integer commentCount = 0;

    private Integer shareCount = 0;

    private Boolean isTop = false;

    private Boolean isFeatured = false;

    private PostStatus status = PostStatus.PUBLISHED;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;

    // 帖子状态枚举
    public enum PostStatus {
        DRAFT("草稿"),
        PUBLISHED("已发布"),
        HIDDEN("已隐藏"),
        DELETED("已删除");

        private final String description;

        PostStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}