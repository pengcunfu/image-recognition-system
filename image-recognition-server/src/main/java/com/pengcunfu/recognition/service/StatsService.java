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
}

