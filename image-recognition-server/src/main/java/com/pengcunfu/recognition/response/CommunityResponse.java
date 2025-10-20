package com.pengcunfu.recognition.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 社区响应DTO
 */
public class CommunityResponse {

    /**
     * 帖子信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostInfo {
        private Long id;
        private Long authorId;
        private Long userId;
        private String authorName;
        private String username;
        private String authorAvatar;
        private String avatar;
        private String title;
        private String content;
        private String category;
        private String tags;
        private String imageUrl;
        private String images;
        private Long recognitionResultId;
        private Long recognitionId;
        private Integer status;
        private Integer viewCount;
        private Integer likeCount;
        private Integer commentCount;
        private Integer isTop;
        private Boolean isLiked;
        private Boolean isCollected;
        private LocalDateTime createTime;
        private LocalDateTime createdAt;
    }
}
