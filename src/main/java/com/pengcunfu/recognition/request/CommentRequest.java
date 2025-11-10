package com.pengcunfu.recognition.request;


import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 评论请求
 */
public class CommentRequest {

    /**
     * 添加评论请求
     */
    @Data
    public static class AddCommentRequest {
        @NotNull(message = "目标类型不能为空")
        private Integer targetType; // 0-POST, 1-KNOWLEDGE

        @NotNull(message = "目标ID不能为空")
        private Long targetId;

        private Long parentId;

        @NotBlank(message = "评论内容不能为空")
        private String content;
    }

    /**
     * 创建评论请求（等同于添加评论请求）
     */
    @Data
    public static class CreateCommentRequest {
        @NotNull(message = "目标类型不能为空")
        private Integer targetType;

        @NotNull(message = "目标ID不能为空")
        private Long targetId;

        private Long parentId;

        @NotBlank(message = "评论内容不能为空")
        private String content;
    }

    /**
     * 更新评论请求
     */
    @Data
    public static class UpdateCommentRequest {
        @NotBlank(message = "评论内容不能为空")
        private String content;
    }

    /**
     * 评论查询请求
     */
    @Data
    public static class QueryCommentRequest {
        @NotNull(message = "目标类型不能为空")
        private Integer targetType; // 0-POST, 1-KNOWLEDGE
        
        @NotNull(message = "目标ID不能为空")
        private Long targetId;
        
        private Integer page = 1;
        private Integer size = 10;
    }
}
