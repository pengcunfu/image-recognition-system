package com.pengcunfu.recognition.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 缓存响应
 */
@Data
public class CacheResponse {

    /**
     * 缓存统计信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CacheStats {
        private Long totalKeys;
        private Long usedMemory;
        private Long maxMemory;
        private Double memoryUsageRate;
        private Long hitCount;
        private Long missCount;
        private Double hitRate;
    }

    /**
     * 缓存键信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CacheKeyInfo {
        private String key;
        private String type;
        private Long ttl;
        private Long size;
    }
}

