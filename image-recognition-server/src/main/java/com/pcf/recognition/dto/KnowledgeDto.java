package com.pcf.recognition.dto;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 知识库相关DTO集合
 * 包含知识分类、知识条目、搜索、统计等相关的请求和响应类
 */
public class KnowledgeDto {

    /**
     * 知识分类DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KnowledgeCategoryDto {
        /**
         * 分类ID
         */
        private Long id;

        /**
         * 分类名称
         */
        private String name;

        /**
         * 分类键值
         */
        private String key;

        /**
         * 分类描述
         */
        private String description;

        /**
         * 分类图片
         */
        private String image;

        /**
         * 条目数量
         */
        private Integer itemCount;

        /**
         * 排序顺序
         */
        private Integer sortOrder;

        /**
         * 分类状态（1-ACTIVE, 2-INACTIVE, 3-HIDDEN）
         */
        private Integer status;

        /**
         * 创建时间
         */
        private LocalDateTime createTime;

        /**
         * 更新时间
         */
        private LocalDateTime updateTime;
    }

    /**
     * 知识条目DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KnowledgeItemDto {
        /**
         * 条目ID
         */
        private Long id;

        /**
         * 分类ID
         */
        private Long categoryId;

        /**
         * 条目名称
         */
        private String name;

        /**
         * 条目键值
         */
        private String key;

        /**
         * 学名
         */
        private String scientificName;

        /**
         * 描述
         */
        private String description;

        /**
         * 详细内容
         */
        private String content;

        /**
         * 图片列表
         */
        private String images;

        /**
         * 特征描述
         */
        private String characteristics;

        /**
         * 栖息地
         */
        private String habitat;

        /**
         * 生命周期
         */
        private String lifespan;

        /**
         * 相关条目
         */
        private String relatedItems;

        /**
         * 标签
         */
        private String tags;

        /**
         * 浏览量
         */
        private Integer viewCount;

        /**
         * 点赞数
         */
        private Integer likeCount;

        /**
         * 收藏数
         */
        private Integer favoriteCount;

        /**
         * 分享数
         */
        private Integer shareCount;

        /**
         * 难度等级
         */
        private Integer difficulty;

        /**
         * 排序顺序
         */
        private Integer sortOrder;

        /**
         * 状态
         */
        private String status;

        /**
         * 作者ID
         */
        private Long authorId;

        /**
         * 审核者ID
         */
        private Long reviewerId;

        /**
         * 审核时间
         */
        private LocalDateTime reviewTime;

        /**
         * 创建时间
         */
        private LocalDateTime createTime;

        /**
         * 更新时间
         */
        private LocalDateTime updateTime;
    }

    /**
     * 知识库分页查询结果DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KnowledgePageDto {
        /**
         * 知识条目列表
         */
        private List<KnowledgeItemDto> items;

        /**
         * 总数量
         */
        private Long total;

        /**
         * 总页数
         */
        private Long pages;

        /**
         * 当前页
         */
        private Long current;

        /**
         * 每页大小
         */
        private Long size;

        /**
         * 搜索关键词
         */
        private String keyword;

        /**
         * 分类
         */
        private String category;
    }

    /**
     * 知识库搜索结果DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KnowledgeSearchResultDto {
        /**
         * 搜索结果列表
         */
        private List<KnowledgeItemDto> items;

        /**
         * 总数量
         */
        private Integer total;

        /**
         * 当前页
         */
        private Integer page;

        /**
         * 每页大小
         */
        private Integer size;

        /**
         * 总页数
         */
        private Integer pages;

        /**
         * 搜索关键词
         */
        private String keyword;

        /**
         * 搜索分类
         */
        private String category;
    }

    /**
     * 知识库统计信息DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KnowledgeStatsDto {
        /**
         * 总分类数
         */
        private Long totalCategories;

        /**
         * 总条目数
         */
        private Long totalItems;

        /**
         * 总浏览量
         */
        private Long totalViews;

        /**
         * 月增长率
         */
        private Double monthlyGrowth;

        /**
         * 总点赞数
         */
        private Long totalLikes;

        /**
         * 总收藏数
         */
        private Long totalFavorites;

        /**
         * 平均难度
         */
        private Double averageDifficulty;
    }

    /**
     * 知识创建响应DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KnowledgeCreateResponseDto {
        /**
         * 是否成功
         */
        private Boolean success;

        /**
         * 响应消息
         */
        private String message;

        /**
         * 创建的ID
         */
        private Long id;
    }

    /**
     * 知识分类创建请求DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KnowledgeCategoryCreateRequest {
        @NotBlank(message = "分类名称不能为空")
        @Size(max = 100, message = "分类名称长度不能超过100个字符")
        private String name;

        @NotBlank(message = "分类键值不能为空")
        @Size(max = 50, message = "分类键值长度不能超过50个字符")
        @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "分类键值只能包含字母、数字、下划线和横线")
        private String key;

        @Size(max = 500, message = "分类描述长度不能超过500个字符")
        private String description;

        @Size(max = 500, message = "分类图片URL长度不能超过500个字符")
        private String image;

        @Min(value = 0, message = "排序顺序不能小于0")
        private Integer sortOrder = 0;
    }

    /**
     * 知识条目创建请求DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KnowledgeItemCreateRequest {
        @NotNull(message = "分类ID不能为空")
        private Long categoryId;

        @NotBlank(message = "条目名称不能为空")
        @Size(max = 200, message = "条目名称长度不能超过200个字符")
        private String name;

        @NotBlank(message = "条目键值不能为空")
        @Size(max = 100, message = "条目键值长度不能超过100个字符")
        @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "条目键值只能包含字母、数字、下划线和横线")
        private String key;

        @Size(max = 200, message = "学名长度不能超过200个字符")
        private String scientificName;

        @Size(max = 1000, message = "描述长度不能超过1000个字符")
        private String description;

        @NotBlank(message = "详细内容不能为空")
        private String content;

        private String images;
        private String characteristics;
        private String habitat;
        private String lifespan;
        private String relatedItems;
        private String tags;

        @Min(value = 1, message = "难度等级不能小于1")
        @Max(value = 5, message = "难度等级不能大于5")
        private Integer difficulty = 1;

        @Min(value = 0, message = "排序顺序不能小于0")
        private Integer sortOrder = 0;
    }

    /**
     * 知识分类更新请求DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryUpdateRequest {
        @Size(max = 100, message = "分类名称长度不能超过100个字符")
        private String name;

        @Size(max = 500, message = "描述长度不能超过500个字符")
        private String description;

        private String image;

        @Min(value = 0, message = "排序顺序不能小于0")
        private Integer sortOrder;

        private Integer status; // 1-ACTIVE, 2-INACTIVE, 3-HIDDEN
    }

    /**
     * 知识条目更新请求DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KnowledgeItemUpdateRequest {
        @Size(max = 200, message = "条目名称长度不能超过200个字符")
        private String name;

        @Size(max = 200, message = "学名长度不能超过200个字符")
        private String scientificName;

        @Size(max = 1000, message = "描述长度不能超过1000个字符")
        private String description;

        private String content;
        private String images;
        private String characteristics;
        private String habitat;
        private String lifespan;
        private String relatedItems;
        private String tags;

        @Min(value = 1, message = "难度等级不能小于1")
        @Max(value = 5, message = "难度等级不能大于5")
        private Integer difficulty;

        @Min(value = 0, message = "排序顺序不能小于0")
        private Integer sortOrder;

        @Pattern(regexp = "^(draft|published|archived)$", message = "状态值不正确")
        private String status;
    }

    /**
     * 知识搜索请求DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KnowledgeSearchRequest {
        @NotBlank(message = "搜索关键词不能为空")
        @Size(max = 100, message = "搜索关键词长度不能超过100个字符")
        private String keyword;

        private String category;

        @Min(value = 1, message = "页码不能小于1")
        private Integer page = 1;

        @Min(value = 1, message = "每页大小不能小于1")
        @Max(value = 100, message = "每页大小不能大于100")
        private Integer size = 20;

        private String sortBy = "createTime"; // 排序字段
        private String sortOrder = "desc"; // 排序方向

        @Min(value = 1, message = "难度等级不能小于1")
        @Max(value = 5, message = "难度等级不能大于5")
        private Integer difficulty; // 难度筛选

        private String status = "published"; // 状态筛选
    }

    /**
     * 知识条目列表响应DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KnowledgeListResponse {
        private List<KnowledgeItemDto> items;
        private long total;
        private int page;
        private int size;
        private int totalPages;
        private String keyword;
        private String category;
    }

    /**
     * 知识操作结果DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KnowledgeOperationResult {
        private Long itemId;
        private boolean success;
        private String message;
        private String operation; // like, unlike, favorite, unfavorite, view
    }
}
