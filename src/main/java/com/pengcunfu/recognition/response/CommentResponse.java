package com.pengcunfu.recognition.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 评论响应DTO
 */
public class CommentResponse {

    /**
     * 评论信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CommentInfo {
        private Long id;
        private Long userId;
        private String username;
        private String avatar;
        private Integer targetType;
        private Long targetId;
        private Long parentId;
        private String content;
        private Integer likeCount;
        private Integer replyCount;
        private Integer status;
        private Boolean isLiked;
        private List<CommentInfo> replies;
        private LocalDateTime createdAt;
    }
}
