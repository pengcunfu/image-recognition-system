package com.pengcunfu.recognition.request.admin;

import lombok.Data;

/**
 * 管理后台 - 社区管理请求
 */
public class AdminCommunityRequest {

    /**
     * 帖子查询请求
     */
    @Data
    public static class QueryPostRequest {
        private Integer page = 1;
        private Integer size = 10;
        private String keyword;
        private String category;
        private Integer status;
        private String startTime;
        private String endTime;
    }

    /**
     * 审核帖子请求
     */
    @Data
    public static class AuditPostRequest {
        private Integer status; // 1-PUBLISHED, 2-REJECTED
        private String reason;
    }

    /**
     * 置顶帖子请求
     */
    @Data
    public static class TopPostRequest {
        private Integer isTop; // 0-取消置顶, 1-置顶
    }

    /**
     * 精选帖子请求
     */
    @Data
    public static class FeaturePostRequest {
        private Integer isFeatured; // 0-取消精选, 1-精选
    }

    /**
     * 评论查询请求
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
     * 删除评论请求
     */
    @Data
    public static class DeleteCommentRequest {
        private String reason;
    }
}
