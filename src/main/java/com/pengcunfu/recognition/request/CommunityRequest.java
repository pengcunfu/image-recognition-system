package com.pengcunfu.recognition.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 社区请求
 */
public class CommunityRequest {

    /**
     * 创建帖子请求
     */
    @Data
    public static class CreatePostRequest {
        @NotBlank(message = "标题不能为空")
        @Size(max = 200, message = "标题长度不能超过200个字符")
        private String title;

        private String content;

        private Long recognitionId;

        private String category;

        private java.util.List<String> tags;

        private java.util.List<String> images;
        
        private Integer status; // 帖子状态: 0-草稿, 1-已发布, 2-待审核, 4-已隐藏
    }

    /**
     * 更新帖子请求
     */
    @Data
    public static class UpdatePostRequest {
        private String title;
        private String content;
        private String category;
        private java.util.List<String> tags;
        private java.util.List<String> images;
        private Integer status; // 帖子状态
    }

    /**
     * 帖子查询请求
     */
    @Data
    public static class QueryPostRequest {
        private Integer page = 1;
        private Integer size = 10;
        private String category;
        private String tag;
        private String sort = "latest"; // latest, hot, top
        private String keyword;
        // 管理员功能字段
        private Integer status;
        private String startTime;
        private String endTime;
    }

    /**
     * 审核帖子请求（管理员功能）
     */
    @Data
    public static class AuditPostRequest {
        private Integer status; // 1-PUBLISHED, 2-REJECTED
        private String reason;
    }

    /**
     * 置顶帖子请求（管理员功能）
     */
    @Data
    public static class TopPostRequest {
        private Integer isTop; // 0-取消置顶, 1-置顶
    }

    /**
     * 精选帖子请求（管理员功能）
     */
    @Data
    public static class FeaturePostRequest {
        private Integer isFeatured; // 0-取消精选, 1-精选
    }

    /**
     * 评论查询请求（管理员功能）
     */
    @Data
    public static class QueryCommentRequest {
        private Integer page = 1;
        private Integer size = 10;
        private Long userId;
        private Integer targetType;
        private Long targetId;
        private String startTime;
        private String endTime;
    }

    /**
     * 删除评论请求（管理员功能）
     */
    @Data
    public static class DeleteCommentRequest {
        private String reason;
    }
}
