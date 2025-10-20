package com.pengcunfu.recognition.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 知识库响应DTO
 */
public class KnowledgeResponse {

    /**
     * 知识信息（通用）
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KnowledgeInfo {
        private Long id;
        private String name;
        private String title;
        private String category;
        private String description;
        private String content;
        private String detail;
        private String imageUrl;
        private String coverImage;
        private String images;
        private String tags;
        private Long authorId;
        private String authorName;
        private Integer viewCount;
        private Integer likeCount;
        private Integer collectCount;
        private Integer commentCount;
        private Boolean isLiked;
        private Boolean isCollected;
        private Integer status;
        private LocalDateTime createTime;
        private LocalDateTime updateTime;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    /**
     * 知识详情
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KnowledgeDetail {
        private Long id;
        private String category;
        private String title;
        private String content;
        private String coverImage;
        private String images;
        private String tags;
        private Long authorId;
        private String authorName;
        private Integer viewCount;
        private Integer likeCount;
        private Integer collectCount;
        private Integer commentCount;
        private Boolean isLiked;
        private Boolean isCollected;
        private Integer status;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    /**
     * 知识列表项
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KnowledgeListItem {
        private Long id;
        private String category;
        private String title;
        private String coverImage;
        private String tags;
        private Integer viewCount;
        private Integer likeCount;
        private LocalDateTime createdAt;
    }

    /**
     * 知识分类
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryInfo {
        private String category;
        private Long count;
        private String description;
    }
}
