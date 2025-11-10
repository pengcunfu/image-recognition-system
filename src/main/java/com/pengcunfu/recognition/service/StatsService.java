package com.pengcunfu.recognition.service;

import com.pengcunfu.recognition.repository.*;
import com.pengcunfu.recognition.response.StatsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 统计服务
 * 处理数据统计、分析等业务逻辑
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StatsService {

    private final UserRepository userRepository;
    private final RecognitionResultRepository recognitionResultRepository;
    private final CommunityPostRepository communityPostRepository;
    private final KnowledgeRepository knowledgeRepository;
    private final VipOrderRepository vipOrderRepository;

    /**
     * 获取系统概览统计
     */
    public StatsResponse.SystemOverview getSystemOverview() {
        log.info("获取系统概览统计");

        LocalDateTime today = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime lastWeek = today.minusDays(7);

        // 用户统计（使用 SQL）
        long totalUsers = userRepository.countTotal();
        long todayUsers = userRepository.countByCreatedAtAfter(today);
        long activeUsers = userRepository.countByLastLoginAfter(lastWeek);
        long vipUsers = userRepository.countVipUsers(LocalDateTime.now());

        // 识别统计（使用 SQL）
        long totalRecognitions = recognitionResultRepository.countTotal();
        long todayRecognitions = recognitionResultRepository.countByCreatedAtAfter(today);

        // 社区统计（使用 SQL）
        long totalPosts = communityPostRepository.countTotal();
        long todayPosts = communityPostRepository.countByCreatedAtAfter(today);

        // 知识库统计（使用 SQL）
        long totalKnowledge = knowledgeRepository.countTotal();

        // 订单统计（使用 SQL）
        long totalOrders = vipOrderRepository.countTotal();
        long todayOrders = vipOrderRepository.countByCreatedAtAfter(today);

        return StatsResponse.SystemOverview.builder()
                .totalUsers(totalUsers)
                .todayUsers(todayUsers)
                .activeUsers(activeUsers)
                .vipUsers(vipUsers)
                .totalRecognitions(totalRecognitions)
                .todayRecognitions(todayRecognitions)
                .totalPosts(totalPosts)
                .todayPosts(todayPosts)
                .totalKnowledge(totalKnowledge)
                .totalOrders(totalOrders)
                .todayOrders(todayOrders)
                .build();
    }

    /**
     * 获取用户增长趋势
     */
    public StatsResponse.TrendData getUserTrend(Integer days) {
        log.info("获取用户增长趋势: days={}", days);

        if (days == null || days <= 0) {
            days = 7; // 默认7天
        }

        LocalDateTime startDate = LocalDateTime.now().minusDays(days);
        List<Map<String, Object>> trendData = userRepository.countUserTrendByDate(startDate);

        // 转换为前端需要的格式
        List<String> dates = new ArrayList<>();
        List<Long> values = new ArrayList<>();

        for (Map<String, Object> data : trendData) {
            Object dateObj = data.get("date");
            Object countObj = data.get("count");
            
            if (dateObj != null && countObj != null) {
                dates.add(dateObj.toString());
                values.add(((Number) countObj).longValue());
            }
        }

        return StatsResponse.TrendData.builder()
                .dates(dates)
                .values(values)
                .build();
    }

    /**
     * 获取识别趋势
     */
    public StatsResponse.TrendData getRecognitionTrend(Integer days) {
        log.info("获取识别趋势: days={}", days);

        if (days == null || days <= 0) {
            days = 7; // 默认7天
        }

        LocalDateTime startDate = LocalDateTime.now().minusDays(days);
        List<Map<String, Object>> trendData = recognitionResultRepository.countRecognitionTrendByDate(startDate);

        // 转换为前端需要的格式
        List<String> dates = new ArrayList<>();
        List<Long> values = new ArrayList<>();

        for (Map<String, Object> data : trendData) {
            Object dateObj = data.get("date");
            Object countObj = data.get("count");
            
            if (dateObj != null && countObj != null) {
                dates.add(dateObj.toString());
                values.add(((Number) countObj).longValue());
            }
        }

        return StatsResponse.TrendData.builder()
                .dates(dates)
                .values(values)
                .build();
    }

    /**
     * 获取综合趋势数据（管理后台）
     * 包含用户、帖子、识别、订单等多个维度的趋势
     */
    public StatsResponse.TrendData getTrendData(Integer days) {
        log.info("获取综合趋势数据: days={}", days);

        if (days == null || days <= 0) {
            days = 7; // 默认7天
        }

        LocalDateTime startDate = LocalDateTime.now().minusDays(days);

        // 获取各个维度的趋势数据
        List<Map<String, Object>> userTrendData = userRepository.countUserTrendByDate(startDate);
        List<Map<String, Object>> recognitionTrendData = recognitionResultRepository.countRecognitionTrendByDate(startDate);
        List<Map<String, Object>> postTrendData = communityPostRepository.countPostTrendByDate(startDate);
        List<Map<String, Object>> orderTrendData = vipOrderRepository.countOrderTrendByDate(startDate);

        // 生成日期列表（最近 N 天）
        List<String> dates = new ArrayList<>();
        LocalDate endDate = LocalDate.now();
        for (int i = days - 1; i >= 0; i--) {
            dates.add(endDate.minusDays(i).toString());
        }

        // 构建多系列数据
        Map<String, List<Long>> series = new HashMap<>();
        series.put("users", extractSeriesData(userTrendData, dates));
        series.put("recognitions", extractSeriesData(recognitionTrendData, dates));
        series.put("posts", extractSeriesData(postTrendData, dates));
        series.put("orders", extractSeriesData(orderTrendData, dates));

        return StatsResponse.TrendData.builder()
                .dates(dates)
                .series(series)
                .build();
    }

    /**
     * 从数据库查询结果中提取系列数据
     * 将数据按日期对齐，缺失的日期填充为 0
     */
    private List<Long> extractSeriesData(List<Map<String, Object>> trendData, List<String> dates) {
        // 先将查询结果转换为 Map<日期, 数量>
        Map<String, Long> dataMap = new HashMap<>();
        for (Map<String, Object> data : trendData) {
            Object dateObj = data.get("date");
            Object countObj = data.get("count");
            
            if (dateObj != null && countObj != null) {
                dataMap.put(dateObj.toString(), ((Number) countObj).longValue());
            }
        }

        // 按照日期列表顺序填充数据，缺失的填 0
        List<Long> values = new ArrayList<>();
        for (String date : dates) {
            values.add(dataMap.getOrDefault(date, 0L));
        }
        
        return values;
    }

    /**
     * 获取热门分类统计
     */
    public StatsResponse.CategoryStats getCategoryStats() {
        log.info("获取热门分类统计");

        // 获取 Top 10 热门分类
        List<Map<String, Object>> categoryData = recognitionResultRepository.countTopCategories(10);

        // 转换为前端需要的格式（Map<String, Long>）
        Map<String, Long> categories = new HashMap<>();
        long total = 0L;

        for (Map<String, Object> data : categoryData) {
            Object categoryObj = data.get("category");
            Object countObj = data.get("count");
            
            if (categoryObj != null && countObj != null) {
                String category = categoryObj.toString();
                Long count = ((Number) countObj).longValue();
                categories.put(category, count);
                total += count;
            }
        }

        return StatsResponse.CategoryStats.builder()
                .categories(categories)
                .total(total)
                .build();
    }

    /**
     * 获取用户角色分布统计
     */
    public StatsResponse.UserRoleStats getUserRoleStats() {
        log.info("获取用户角色分布统计");

        // 统计各角色用户数量
        long normalUsers = userRepository.countByRole(0); // 普通用户
        long vipUsers = userRepository.countByRole(1);    // VIP用户
        long adminUsers = userRepository.countByRole(2);  // 管理员
        long totalUsers = normalUsers + vipUsers + adminUsers;

        return StatsResponse.UserRoleStats.builder()
                .normalUsers(normalUsers)
                .vipUsers(vipUsers)
                .adminUsers(adminUsers)
                .totalUsers(totalUsers)
                .build();
    }

    /**
     * 获取识别分类统计
     */
    public StatsResponse.RecognitionCategoryStats getRecognitionCategoryStats() {
        log.info("获取识别分类统计");

        // 获取识别分类统计数据
        List<Map<String, Object>> categoryData = recognitionResultRepository.countByMainCategory();
        long totalRecognitions = recognitionResultRepository.countTotal();

        List<StatsResponse.RecognitionCategoryItem> categories = new ArrayList<>();
        
        for (Map<String, Object> data : categoryData) {
            Object categoryObj = data.get("category");
            Object countObj = data.get("count");
            
            if (categoryObj != null && countObj != null) {
                String category = categoryObj.toString();
                Long count = ((Number) countObj).longValue();
                Double percentage = totalRecognitions > 0 ? (count * 100.0 / totalRecognitions) : 0.0;
                
                categories.add(StatsResponse.RecognitionCategoryItem.builder()
                        .category(category)
                        .count(count)
                        .percentage(percentage)
                        .build());
            }
        }

        return StatsResponse.RecognitionCategoryStats.builder()
                .categories(categories)
                .totalRecognitions(totalRecognitions)
                .build();
    }

    /**
     * 获取VIP订单统计
     */
    public StatsResponse.VipOrderStats getVipOrderStats() {
        log.info("获取VIP订单统计");

        // 获取VIP订单统计数据
        List<Map<String, Object>> orderData = vipOrderRepository.countByPlanType();
        long totalOrders = 0;
        double totalRevenue = 0.0;

        List<StatsResponse.VipOrderItem> orders = new ArrayList<>();
        
        // 先计算总数
        for (Map<String, Object> data : orderData) {
            Object countObj = data.get("orderCount");
            Object amountObj = data.get("totalAmount");
            
            if (countObj != null) {
                totalOrders += ((Number) countObj).longValue();
            }
            if (amountObj != null) {
                totalRevenue += ((Number) amountObj).doubleValue();
            }
        }

        // 构建订单项列表
        for (Map<String, Object> data : orderData) {
            Object planTypeObj = data.get("planType");
            Object countObj = data.get("orderCount");
            Object amountObj = data.get("totalAmount");
            
            if (planTypeObj != null && countObj != null && amountObj != null) {
                Integer planType = ((Number) planTypeObj).intValue();
                Long orderCount = ((Number) countObj).longValue();
                Double amount = ((Number) amountObj).doubleValue();
                Double percentage = totalOrders > 0 ? (orderCount * 100.0 / totalOrders) : 0.0;
                
                String planName = getPlanTypeName(planType);
                
                orders.add(StatsResponse.VipOrderItem.builder()
                        .planType(planType)
                        .planName(planName)
                        .orderCount(orderCount)
                        .totalAmount(amount)
                        .percentage(percentage)
                        .build());
            }
        }

        return StatsResponse.VipOrderStats.builder()
                .orders(orders)
                .totalOrders(totalOrders)
                .totalRevenue(totalRevenue)
                .build();
    }

    /**
     * 获取每日识别统计（最近30天）
     */
    public StatsResponse.DailyRecognitionStats getDailyRecognitionStats() {
        log.info("获取每日识别统计");

        LocalDateTime startDate = LocalDateTime.now().minusDays(30);
        List<Map<String, Object>> dailyData = recognitionResultRepository.countRecognitionTrendByDate(startDate);

        List<StatsResponse.DailyRecognitionItem> data = new ArrayList<>();
        long totalCount = 0;
        
        for (Map<String, Object> item : dailyData) {
            Object dateObj = item.get("date");
            Object countObj = item.get("count");
            
            if (dateObj != null && countObj != null) {
                String date = dateObj.toString();
                Long count = ((Number) countObj).longValue();
                totalCount += count;
                
                data.add(StatsResponse.DailyRecognitionItem.builder()
                        .date(date)
                        .count(count)
                        .build());
            }
        }

        int totalDays = data.size();
        double avgDaily = totalDays > 0 ? (double) totalCount / totalDays : 0.0;

        return StatsResponse.DailyRecognitionStats.builder()
                .data(data)
                .totalDays(totalDays)
                .avgDaily(avgDaily)
                .build();
    }

    /**
     * 获取套餐类型名称
     */
    private String getPlanTypeName(Integer planType) {
        switch (planType) {
            case 0:
                return "月度会员";
            case 1:
                return "季度会员";
            case 2:
                return "年度会员";
            default:
                return "未知套餐";
        }
    }

    /**
     * 获取VIP用户统计分析数据
     */
    public StatsResponse.VipAnalytics getVipAnalytics(Long userId, Integer days) {
        log.info("获取VIP统计分析数据: userId={}, days={}", userId, days);

        LocalDateTime startDate = LocalDateTime.now().minusDays(days);
        LocalDateTime previousStartDate = startDate.minusDays(days);

        // 当前周期数据
        List<Map<String, Object>> currentData = recognitionResultRepository.getVipUserStats(userId, startDate, LocalDateTime.now());
        // 上一周期数据（用于计算增长率）
        List<Map<String, Object>> previousData = recognitionResultRepository.getVipUserStats(userId, previousStartDate, startDate);

        Long totalRecognitions = 0L;
        Double totalAccuracy = 0.0;
        Long totalTime = 0L;
        Long count = 0L;

        for (Map<String, Object> item : currentData) {
            totalRecognitions++;
            Object confidenceObj = item.get("confidence");
            Object timeObj = item.get("processing_time");
            
            if (confidenceObj != null) {
                Double confidence = ((Number) confidenceObj).doubleValue();
                totalAccuracy += confidence;
            }
            if (timeObj != null) {
                totalTime += ((Number) timeObj).longValue();
            }
            count++;
        }

        Double averageAccuracy = count > 0 ? (totalAccuracy / count) * 100 : 0.0;
        Integer avgProcessTime = count > 0 ? (int) (totalTime / count) : 0;

        // 计算上一周期数据
        Long previousRecognitions = (long) previousData.size();
        Double growthRate = previousRecognitions > 0 
            ? ((double) (totalRecognitions - previousRecognitions) / previousRecognitions) * 100 
            : 0.0;

        // 估算节省成本（假设每次识别节省0.5元）
        Double costSaved = totalRecognitions * 0.5;

        return StatsResponse.VipAnalytics.builder()
                .totalRecognitions(totalRecognitions)
                .averageAccuracy(averageAccuracy)
                .avgProcessTime(avgProcessTime)
                .costSaved(costSaved)
                .growthRate(growthRate)
                .accuracyImprovement(2.1) // 模拟数据
                .speedImprovement(15.2) // 模拟数据
                .daysAnalyzed(days)
                .build();
    }

    /**
     * 获取VIP用户识别趋势数据
     */
    public StatsResponse.VipTrends getVipTrends(Long userId, Integer days) {
        log.info("获取VIP识别趋势数据: userId={}, days={}", userId, days);

        LocalDateTime startDate = LocalDateTime.now().minusDays(days);
        List<Map<String, Object>> trendData = recognitionResultRepository.getVipUserDailyTrends(userId, startDate);

        List<StatsResponse.VipTrendPoint> dailyTrends = new ArrayList<>();
        List<String> dates = new ArrayList<>();
        List<Long> recognitionCounts = new ArrayList<>();
        List<Double> accuracyTrends = new ArrayList<>();
        List<Integer> timeTrends = new ArrayList<>();

        for (Map<String, Object> item : trendData) {
            String date = item.get("date").toString();
            Long count = ((Number) item.get("count")).longValue();
            Double accuracy = item.get("avg_confidence") != null 
                ? ((Number) item.get("avg_confidence")).doubleValue() * 100 
                : 0.0;
            Integer avgTime = item.get("avg_time") != null 
                ? ((Number) item.get("avg_time")).intValue() 
                : 0;

            dailyTrends.add(StatsResponse.VipTrendPoint.builder()
                    .date(date)
                    .recognitions(count)
                    .accuracy(accuracy)
                    .avgTime(avgTime)
                    .build());

            dates.add(date);
            recognitionCounts.add(count);
            accuracyTrends.add(accuracy);
            timeTrends.add(avgTime);
        }

        return StatsResponse.VipTrends.builder()
                .dailyTrends(dailyTrends)
                .dates(dates)
                .recognitionCounts(recognitionCounts)
                .accuracyTrends(accuracyTrends)
                .timeTrends(timeTrends)
                .build();
    }

    /**
     * 获取VIP用户分类分析数据
     */
    public StatsResponse.VipCategoryAnalysis getVipCategoryAnalysis(Long userId, Integer days) {
        log.info("获取VIP分类分析数据: userId={}, days={}", userId, days);

        LocalDateTime startDate = LocalDateTime.now().minusDays(days);
        List<Map<String, Object>> categoryData = recognitionResultRepository.getVipUserCategoryStats(userId, startDate);

        List<StatsResponse.VipCategoryItem> categories = new ArrayList<>();
        Long totalCount = 0L;
        String topCategory = "";
        String mostAccurateCategory = "";
        String fastestCategory = "";
        Long maxCount = 0L;
        Double maxAccuracy = 0.0;
        Integer minTime = Integer.MAX_VALUE;

        for (Map<String, Object> item : categoryData) {
            String category = item.get("main_category").toString();
            Long count = ((Number) item.get("count")).longValue();
            Double avgAccuracy = item.get("avg_confidence") != null 
                ? ((Number) item.get("avg_confidence")).doubleValue() * 100 
                : 0.0;
            Integer avgTime = item.get("avg_time") != null 
                ? ((Number) item.get("avg_time")).intValue() 
                : 0;

            totalCount += count;

            // 找出最多、最准确、最快的分类
            if (count > maxCount) {
                maxCount = count;
                topCategory = category;
            }
            if (avgAccuracy > maxAccuracy) {
                maxAccuracy = avgAccuracy;
                mostAccurateCategory = category;
            }
            if (avgTime < minTime && avgTime > 0) {
                minTime = avgTime;
                fastestCategory = category;
            }

            categories.add(StatsResponse.VipCategoryItem.builder()
                    .category(category)
                    .count(count)
                    .avgAccuracy(avgAccuracy)
                    .avgTime(avgTime)
                    .percentage(0.0) // 稍后计算
                    .build());
        }

        // 计算百分比
        for (StatsResponse.VipCategoryItem item : categories) {
            if (totalCount > 0) {
                item.setPercentage((double) item.getCount() / totalCount * 100);
            }
        }

        return StatsResponse.VipCategoryAnalysis.builder()
                .categories(categories)
                .topCategory(topCategory)
                .mostAccurateCategory(mostAccurateCategory)
                .fastestCategory(fastestCategory)
                .build();
    }

    /**
     * 获取VIP用户性能分析数据
     */
    public StatsResponse.VipPerformanceAnalysis getVipPerformanceAnalysis(Long userId, Integer days) {
        log.info("获取VIP性能分析数据: userId={}, days={}", userId, days);

        LocalDateTime startDate = LocalDateTime.now().minusDays(days);
        LocalDateTime midDate = LocalDateTime.now().minusDays(days / 2);

        // 获取前半期和后半期数据
        List<Map<String, Object>> recentData = recognitionResultRepository.getVipUserStats(userId, midDate, LocalDateTime.now());
        List<Map<String, Object>> previousData = recognitionResultRepository.getVipUserStats(userId, startDate, midDate);

        // 计算当前性能
        Double currentAccuracy = calculateAverageAccuracy(recentData);
        Integer currentAvgTime = calculateAverageTime(recentData);

        // 计算之前性能
        Double previousAccuracy = calculateAverageAccuracy(previousData);
        Integer previousAvgTime = calculateAverageTime(previousData);

        // 计算趋势
        Double accuracyTrend = currentAccuracy - previousAccuracy;
        Double speedTrend = previousAvgTime > 0 
            ? ((double) (previousAvgTime - currentAvgTime) / previousAvgTime) * 100 
            : 0.0;

        // 获取性能历史数据
        List<Map<String, Object>> historyData = recognitionResultRepository.getVipUserDailyTrends(userId, startDate);
        List<StatsResponse.VipPerformancePoint> performanceHistory = new ArrayList<>();

        for (Map<String, Object> item : historyData) {
            String date = item.get("date").toString();
            Double accuracy = item.get("avg_confidence") != null 
                ? ((Number) item.get("avg_confidence")).doubleValue() * 100 
                : 0.0;
            Integer avgTime = item.get("avg_time") != null 
                ? ((Number) item.get("avg_time")).intValue() 
                : 0;
            Long count = ((Number) item.get("count")).longValue();

            performanceHistory.add(StatsResponse.VipPerformancePoint.builder()
                    .date(date)
                    .accuracy(accuracy)
                    .avgTime(avgTime)
                    .count(count)
                    .build());
        }

        return StatsResponse.VipPerformanceAnalysis.builder()
                .currentAccuracy(currentAccuracy)
                .previousAccuracy(previousAccuracy)
                .currentAvgTime(currentAvgTime)
                .previousAvgTime(previousAvgTime)
                .accuracyTrend(accuracyTrend)
                .speedTrend(speedTrend)
                .performanceHistory(performanceHistory)
                .build();
    }

    /**
     * 获取VIP用户智能建议
     */
    public StatsResponse.VipSuggestions getVipSuggestions(Long userId) {
        log.info("获取VIP智能建议: userId={}", userId);

        List<StatsResponse.VipSuggestionItem> suggestions = new ArrayList<>();

        // 基于用户数据生成智能建议
        LocalDateTime startDate = LocalDateTime.now().minusDays(30);
        List<Map<String, Object>> userData = recognitionResultRepository.getVipUserStats(userId, startDate, LocalDateTime.now());

        // 分析用户使用模式
        Double avgAccuracy = calculateAverageAccuracy(userData);
        Integer avgTime = calculateAverageTime(userData);
        Long totalRecognitions = (long) userData.size();

        // 建议1: 图片预处理优化
        if (avgTime > 5000) { // 如果平均处理时间超过5秒
            suggestions.add(StatsResponse.VipSuggestionItem.builder()
                    .id("preprocessing")
                    .type("performance")
                    .title("优化图片预处理")
                    .description("建议在上传前对图片进行压缩和格式优化，可提升处理速度25%")
                    .impact("速度提升25%")
                    .icon("fas fa-bolt")
                    .priority(1)
                    .applicable(true)
                    .build());
        }

        // 建议2: 错峰使用
        suggestions.add(StatsResponse.VipSuggestionItem.builder()
                .id("schedule")
                .type("efficiency")
                .title("错峰使用")
                .description("建议在10-12点和15-17点使用，可获得更快的响应速度")
                .impact("响应时间减少30%")
                .icon("fas fa-clock")
                .priority(2)
                .applicable(true)
                .build());

        // 建议3: 批量处理
        if (totalRecognitions > 50) { // 如果识别次数较多
            suggestions.add(StatsResponse.VipSuggestionItem.builder()
                    .id("batch")
                    .type("cost")
                    .title("批量处理")
                    .description("建议使用批量识别功能，可降低单次识别成本40%")
                    .impact("成本降低40%")
                    .icon("fas fa-layer-group")
                    .priority(1)
                    .applicable(true)
                    .build());
        }

        // 建议4: 高级识别模式
        if (avgAccuracy < 95.0) { // 如果准确率较低
            suggestions.add(StatsResponse.VipSuggestionItem.builder()
                    .id("advanced_mode")
                    .type("accuracy")
                    .title("使用高级识别模式")
                    .description("建议使用超精度识别模式，可将准确率提升至99%以上")
                    .impact("准确率提升5%")
                    .icon("fas fa-search-plus")
                    .priority(1)
                    .applicable(true)
                    .build());
        }

        int highPrioritySuggestions = (int) suggestions.stream()
                .mapToInt(s -> s.getPriority() == 1 ? 1 : 0)
                .sum();

        return StatsResponse.VipSuggestions.builder()
                .suggestions(suggestions)
                .totalSuggestions(suggestions.size())
                .highPrioritySuggestions(highPrioritySuggestions)
                .build();
    }

    /**
     * 计算平均准确率
     */
    private Double calculateAverageAccuracy(List<Map<String, Object>> data) {
        if (data.isEmpty()) return 0.0;

        double totalAccuracy = 0.0;
        int count = 0;

        for (Map<String, Object> item : data) {
            Object confidenceObj = item.get("confidence");
            if (confidenceObj != null) {
                totalAccuracy += ((Number) confidenceObj).doubleValue();
                count++;
            }
        }

        return count > 0 ? (totalAccuracy / count) * 100 : 0.0;
    }

    /**
     * 计算平均处理时间
     */
    private Integer calculateAverageTime(List<Map<String, Object>> data) {
        if (data.isEmpty()) return 0;

        long totalTime = 0;
        int count = 0;

        for (Map<String, Object> item : data) {
            Object timeObj = item.get("processing_time");
            if (timeObj != null) {
                totalTime += ((Number) timeObj).longValue();
                count++;
            }
        }

        return count > 0 ? (int) (totalTime / count) : 0;
    }
}

