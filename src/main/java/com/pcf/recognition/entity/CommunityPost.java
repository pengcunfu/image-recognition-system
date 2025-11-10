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

    // 帖子状态枚举（与MySQL ENUM对应：1-DRAFT, 2-PENDING, 3-PUBLISHED, 4-REJECTED, 5-HIDDEN, 6-DELETED）
    public enum PostStatus {
        DRAFT(1, "草稿"),
        PENDING(2, "待审核"),
        PUBLISHED(3, "已发布"),
        REJECTED(4, "已拒绝"),
        HIDDEN(5, "已隐藏"),
        DELETED(6, "已删除");

        private final int value;
        private final String description;

        PostStatus(int value, String description) {
            this.value = value;
            this.description = description;
        }

        public int getValue() {
            return value;
        }

        public String getDescription() {
            return description;
        }

        public static PostStatus fromValue(int value) {
            for (PostStatus status : values()) {
                if (status.value == value) {
                    return status;
                }
            }
            throw new IllegalArgumentException("Invalid status value: " + value);
        }
    }

    // 排序方式枚举
    public enum SortType {
        LATEST(0, "最新"),
        HOT(1, "最热"),
        TOP_FIRST(2, "置顶优先");

        private final int value;
        private final String description;

        SortType(int value, String description) {
            this.value = value;
            this.description = description;
        }

        public int getValue() {
            return value;
        }

        public String getDescription() {
            return description;
        }

        public static SortType fromValue(int value) {
            for (SortType type : values()) {
                if (type.value == value) {
                    return type;
                }
            }
            return LATEST; // 默认返回最新
        }
    }
}