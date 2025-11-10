package com.pcf.recognition.dto;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 社区相关DTO集合
 * 包含帖子、评论、互动等相关的请求和响应类
 */
public class CommunityDto {

    /**
     * 帖子DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostDto {
        private Long id;
        private String title;
        private String content;
        private Long authorId;
        private String authorName; // 作者姓名
        private String authorAvatar; // 作者头像
        private String category;
        private String tags;
        private String images;
        private Integer viewCount;
        private Integer likeCount;
        private Integer commentCount;
        private Integer shareCount;
        private Boolean isTop;
        private Boolean isFeatured;
        private String status;
        private LocalDateTime createTime;
        private LocalDateTime updateTime;
    }

    /**
     * 评论DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CommentDto {
        private Long id;
        private Long postId;
        private Long authorId;
        private String authorName; // 评论者姓名
        private String authorAvatar; // 评论者头像
        private Long parentId;
        private String content;
        private Integer likeCount;
        private Integer replyCount;
        private String status;
        private LocalDateTime createTime;
        private LocalDateTime updateTime;
        private List<CommentDto> replies; // 回复列表
    }

    /**
     * 帖子列表响应DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostListResponseDto {
        private List<PostDto> data;
        private Long total;
        private Long pages;
        private Long current;
        private Long size;
        private String category;
        private String sort;
    }

    /**
     * 帖子详情响应DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostDetailResponseDto {
        private PostDto post;
        private List<CommentDto> recentComments; // 最新评论
        private List<PostDto> relatedPosts; // 相关帖子
    }

    /**
     * 评论列表响应DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CommentListResponseDto {
        private List<CommentDto> data;
        private Long total;
        private Long pages;
        private Long current;
        private Long size;
    }

    /**
     * 帖子创建响应DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostCreateResponseDto {
        private Long postId;
        private String message;
        private Boolean success;
    }

    /**
     * 评论创建响应DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CommentCreateResponseDto {
        private Long commentId;
        private String message;
        private Boolean success;
    }

    /**
     * 创建帖子请求DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreatePostRequest {
        @NotBlank(message = "标题不能为空")
        @Size(max = 200, message = "标题长度不能超过200字符")
        private String title;

        @NotBlank(message = "内容不能为空")
        @Size(max = 10000, message = "内容长度不能超过10000字符")
        private String content;

        @Size(max = 50, message = "分类长度不能超过50字符")
        private String category;

        @Size(max = 200, message = "标签长度不能超过200字符")
        private String tags;

        private List<String> images; // 图片列表
    }

    /**
     * 添加评论请求DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddCommentRequest {
        @NotBlank(message = "评论内容不能为空")
        @Size(max = 1000, message = "评论内容长度不能超过1000字符")
        private String content;

        private Long parentId; // 父评论ID，用于回复
    }

    /**
     * 帖子更新请求DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdatePostRequest {
        @Size(max = 200, message = "标题长度不能超过200字符")
        private String title;

        @Size(max = 10000, message = "内容长度不能超过10000字符")
        private String content;

        @Size(max = 50, message = "分类长度不能超过50字符")
        private String category;

        @Size(max = 200, message = "标签长度不能超过200字符")
        private String tags;

        private List<String> images;

        @Pattern(regexp = "^(draft|published|hidden|deleted)$", message = "状态值不正确")
        private String status;
    }

    /**
     * 帖子搜索请求DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostSearchRequest {
        @Size(max = 100, message = "搜索关键词长度不能超过100字符")
        private String keyword;

        private String category;
        private String authorName;
        private String tags;

        @Min(value = 1, message = "页码不能小于1")
        private Integer page = 1;

        @Min(value = 1, message = "每页大小不能小于1")
        @Max(value = 100, message = "每页大小不能大于100")
        private Integer size = 20;

        private String sortBy = "createTime"; // 排序字段
        private String sortOrder = "desc"; // 排序方向

        private LocalDateTime startTime; // 开始时间
        private LocalDateTime endTime; // 结束时间
    }

    /**
     * 社区统计信息DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CommunityStatsDto {
        private Long totalPosts; // 总帖子数
        private Long totalComments; // 总评论数
        private Long totalUsers; // 总用户数
        private Long todayPosts; // 今日发帖数
        private Long todayComments; // 今日评论数
        private Long activeUsers; // 活跃用户数
        private List<CategoryStatsDto> categoryStats; // 分类统计
    }

    /**
     * 分类统计DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryStatsDto {
        private String category;
        private String categoryName;
        private Long postCount;
        private Long commentCount;
    }

    /**
     * 帖子互动请求DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostInteractionRequest {
        @NotNull(message = "帖子ID不能为空")
        private Long postId;

        @NotBlank(message = "操作类型不能为空")
        @Pattern(regexp = "^(like|unlike|favorite|unfavorite|share)$", message = "操作类型不正确")
        private String action;
    }

    /**
     * 评论互动请求DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CommentInteractionRequest {
        @NotNull(message = "评论ID不能为空")
        private Long commentId;

        @NotBlank(message = "操作类型不能为空")
        @Pattern(regexp = "^(like|unlike|report)$", message = "操作类型不正确")
        private String action;

        private String reason; // 举报原因
    }

    /**
     * 互动结果DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InteractionResultDto {
        private Boolean success;
        private String message;
        private String action;
        private Long targetId;
        private Integer newCount; // 新的计数（点赞数、收藏数等）
    }

    /**
     * 帖子管理请求DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostManagementRequest {
        @NotEmpty(message = "帖子ID列表不能为空")
        private List<Long> postIds;

        @NotBlank(message = "操作类型不能为空")
        @Pattern(regexp = "^(publish|hide|delete|top|untop|feature|unfeature)$", message = "操作类型不正确")
        private String action;

        private String reason; // 操作原因
    }

    /**
     * 热门帖子DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HotPostDto {
        private Long id;
        private String title;
        private String summary; // 摘要
        private String authorName;
        private String category;
        private Integer viewCount;
        private Integer likeCount;
        private Integer commentCount;
        private LocalDateTime createTime;
        private String thumbnail; // 缩略图
    }

    /**
     * 用户社区活动DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserCommunityActivityDto {
        private Long userId;
        private String username;
        private Integer postCount; // 发帖数
        private Integer commentCount; // 评论数
        private Integer likeCount; // 获赞数
        private Integer favoriteCount; // 收藏数
        private LocalDateTime lastActiveTime; // 最后活跃时间
        private String level; // 用户等级
        private Integer points; // 积分
    }
}
