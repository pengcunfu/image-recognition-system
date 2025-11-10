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

    /**
     * 用户角色统计
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserRoleStats {
        private Long normalUsers;
        private Long vipUsers;
        private Long adminUsers;
        private Long totalUsers;
    }

    /**
     * 识别分类统计项
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecognitionCategoryItem {
        private String category;
        private Long count;
        private Double percentage;
    }

    /**
     * 识别分类统计
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecognitionCategoryStats {
        private List<RecognitionCategoryItem> categories;
        private Long totalRecognitions;
    }

    /**
     * VIP订单统计项
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VipOrderItem {
        private Integer planType;
        private String planName;
        private Long orderCount;
        private Double totalAmount;
        private Double percentage;
    }

    /**
     * VIP订单统计
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VipOrderStats {
        private List<VipOrderItem> orders;
        private Long totalOrders;
        private Double totalRevenue;
    }

    /**
     * 每日识别统计项
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyRecognitionItem {
        private String date;
        private Long count;
    }

    /**
     * 每日识别统计
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyRecognitionStats {
        private List<DailyRecognitionItem> data;
        private Integer totalDays;
        private Double avgDaily;
    }

    /**
     * VIP用户统计分析
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VipAnalytics {
        private Long totalRecognitions;
        private Double averageAccuracy;
        private Integer avgProcessTime;
        private Double costSaved;
        private Double growthRate;
        private Double accuracyImprovement;
        private Double speedImprovement;
        private Integer daysAnalyzed;
    }

    /**
     * VIP趋势数据点
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VipTrendPoint {
        private String date;
        private Long recognitions;
        private Double accuracy;
        private Integer avgTime;
    }

    /**
     * VIP识别趋势
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VipTrends {
        private List<VipTrendPoint> dailyTrends;
        private List<String> dates;
        private List<Long> recognitionCounts;
        private List<Double> accuracyTrends;
        private List<Integer> timeTrends;
    }

    /**
     * VIP分类分析项
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VipCategoryItem {
        private String category;
        private Long count;
        private Double avgAccuracy;
        private Integer avgTime;
        private Double percentage;
    }

    /**
     * VIP分类分析
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VipCategoryAnalysis {
        private List<VipCategoryItem> categories;
        private String topCategory;
        private String mostAccurateCategory;
        private String fastestCategory;
    }

    /**
     * VIP性能分析
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VipPerformanceAnalysis {
        private Double currentAccuracy;
        private Double previousAccuracy;
        private Integer currentAvgTime;
        private Integer previousAvgTime;
        private Double accuracyTrend;
        private Double speedTrend;
        private List<VipPerformancePoint> performanceHistory;
    }

    /**
     * VIP性能数据点
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VipPerformancePoint {
        private String date;
        private Double accuracy;
        private Integer avgTime;
        private Long count;
    }

    /**
     * VIP智能建议项
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VipSuggestionItem {
        private String id;
        private String type;
        private String title;
        private String description;
        private String impact;
        private String icon;
        private Integer priority;
        private Boolean applicable;
    }

    /**
     * VIP智能建议
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VipSuggestions {
        private List<VipSuggestionItem> suggestions;
        private Integer totalSuggestions;
        private Integer highPrioritySuggestions;
    }
}
