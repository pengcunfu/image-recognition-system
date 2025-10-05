package com.pcf.recognition.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 社区评论实体
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("community_comments")
public class CommunityComment {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long postId;
    
    private Long authorId;
    
    private Long parentId; // 父评论ID，null表示顶级评论
    
    private String content;
    
    private Integer likeCount = 0;
    
    private Integer replyCount = 0;
    
    private CommentStatus status = CommentStatus.PUBLISHED;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
    
    // 评论状态枚举
    public enum CommentStatus {
        PUBLISHED("已发布"),
        HIDDEN("已隐藏"),
        DELETED("已删除");
        
        private final String description;
        
        CommentStatus(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
}