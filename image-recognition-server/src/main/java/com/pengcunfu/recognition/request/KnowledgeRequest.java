package com.pengcunfu.recognition.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 知识库请求
 */
public class KnowledgeRequest {

    /**
     * 创建知识条目请求
     */
    @Data
    public static class CreateKnowledgeRequest {
        @NotBlank(message = "分类不能为空")
        private String category;

        @NotBlank(message = "标题不能为空")
        private String title;

        @NotBlank(message = "内容不能为空")
        private String content;

        private String coverImage;
        private String images;
        private String tags;
    }

    /**
     * 更新知识条目请求
     */
    @Data
    public static class UpdateKnowledgeRequest {
        private String category;
        private String title;
        private String content;
        private String coverImage;
        private String images;
        private String tags;
    }

    /**
     * 知识查询请求
     */
    @Data
    public static class QueryKnowledgeRequest {
        private Integer page = 1;
        private Integer size = 10;
        private String category;
        private String keyword;
        private String tag;
        // 管理员功能字段
        private Integer status;
        private String startTime;
        private String endTime;
    }

    /**
     * 审核知识条目请求（管理员功能）
     */
    @Data
    public static class AuditKnowledgeRequest {
        private Integer status; // 1-PUBLISHED, 2-REJECTED
        private String reason;
    }
}
