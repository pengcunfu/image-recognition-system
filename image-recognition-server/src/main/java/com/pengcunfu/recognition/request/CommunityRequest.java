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

        private String tags;

        private String images;
    }

    /**
     * 更新帖子请求
     */
    @Data
    public static class UpdatePostRequest {
        private String title;
        private String content;
        private String category;
        private String tags;
        private String images;
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
    }
}
