package com.pengcunfu.recognition.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 统计数据响应DTO
 */
public class StatsResponse {

    /**
     * 系统概览
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SystemOverview {
        private Long totalUsers;
        private Long todayUsers;
        private Long activeUsers;
        private Long vipUsers;
        private Long totalRecognitions;
        private Long todayRecognitions;
        private Long totalPosts;
        private Long todayPosts;
        private Long totalKnowledge;
        private Long totalOrders;
        private Long todayOrders;
    }

    /**
     * 趋势数据
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TrendData {
        private List<String> dates;
        private List<Long> values;
        private Map<String, List<Long>> series; // 多系列数据支持（用户、帖子、识别等）
    }

    /**
     * 分类统计
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryStats {
        private Map<String, Long> categories;
        private Long total;
    }

    /**
     * 系统日志信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SystemLogInfo {
        private Long id;
        private Long userId;
        private String username;
        private String operation;
        private String method;
        private String params;
        private String result;
        private String ip;
        private Long duration;
        private String createTime;
        private java.time.LocalDateTime createdAt;
    }

    /**
     * 仪表板统计
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DashboardStats {
        private Long totalUsers;
        private Long todayUsers;
        private Long totalRecognitions;
        private Long todayRecognitions;
        private Long totalPosts;
        private Long todayPosts;
        private Long totalKnowledge;
    }

    /**
     * 用户统计
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserStats {
        private Long totalUsers;
        private Long activeUsers;
        private Long vipUsers;
        private Map<String, Long> roleDistribution;
        private Map<String, Long> statusDistribution;
    }

    /**
     * 识别统计
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecognitionStats {
        private Long totalRecognitions;
        private Long successCount;
        private Long failedCount;
        private Double successRate;
        private Map<String, Long> categoryDistribution;
        private Integer avgProcessingTime;
    }

    /**
     * 社区统计
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CommunityStats {
        private Long totalPosts;
        private Long totalComments;
        private Long totalLikes;
        private Long totalCollects;
        private Map<String, Long> categoryDistribution;
    }
}
