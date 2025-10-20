package com.pengcunfu.recognition.request.admin;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 管理后台 - 缓存管理请求
 */
public class AdminCacheRequest {

    /**
     * 删除缓存请求
     */
    @Data
    public static class DeleteCacheRequest {
        @NotBlank(message = "缓存键不能为空")
        private String key;
    }

    /**
     * 清空缓存请求
     */
    @Data
    public static class ClearCacheRequest {
        private String pattern; // 缓存键模式，如 "user:*"
    }

    /**
     * 刷新热点数据请求
     */
    @Data
    public static class RefreshHotDataRequest {
        private String type; // 热点数据类型: posts, knowledge, users
    }
}
