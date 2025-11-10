package com.pengcunfu.recognition.controller;

import com.pengcunfu.recognition.annotation.Role;
import com.pengcunfu.recognition.response.ApiResponse;
import com.pengcunfu.recognition.response.StatsResponse;
import com.pengcunfu.recognition.security.SecurityContextHolder;
import com.pengcunfu.recognition.service.StatsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 统计数据控制器（公共）
 */
@Slf4j
@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

    /**
     * 获取首页统计数据
     */
    @GetMapping("/overview")
    public ApiResponse<StatsResponse.SystemOverview> getOverview() {
        log.info("获取首页统计数据");
        StatsResponse.SystemOverview overview = statsService.getSystemOverview();
        return ApiResponse.success(overview);
    }

    /**
     * 获取分类统计
     */
    @GetMapping("/categories")
    public ApiResponse<StatsResponse.CategoryStats> getCategoryStats() {
        log.info("获取分类统计");
        StatsResponse.CategoryStats stats = statsService.getCategoryStats();
        return ApiResponse.success(stats);
    }

    /**
     * 获取VIP用户统计分析数据
     */
    @GetMapping("/vip/analytics")
    @Role("VIP")
    public ApiResponse<StatsResponse.VipAnalytics> getVipAnalytics(
            @RequestParam(value = "days", defaultValue = "30") Integer days) {
        Long userId = SecurityContextHolder.getCurrentUserId();
        log.info("获取VIP统计分析数据: userId={}, days={}", userId, days);
        StatsResponse.VipAnalytics analytics = statsService.getVipAnalytics(userId, days);
        return ApiResponse.success(analytics);
    }

    /**
     * 获取VIP用户识别趋势数据
     */
    @GetMapping("/vip/trends")
    @Role("VIP")
    public ApiResponse<StatsResponse.VipTrends> getVipTrends(
            @RequestParam(value = "days", defaultValue = "30") Integer days) {
        Long userId = SecurityContextHolder.getCurrentUserId();
        log.info("获取VIP识别趋势数据: userId={}, days={}", userId, days);
        StatsResponse.VipTrends trends = statsService.getVipTrends(userId, days);
        return ApiResponse.success(trends);
    }

    /**
     * 获取VIP用户分类分析数据
     */
    @GetMapping("/vip/categories")
    @Role("VIP")
    public ApiResponse<StatsResponse.VipCategoryAnalysis> getVipCategoryAnalysis(
            @RequestParam(value = "days", defaultValue = "30") Integer days) {
        Long userId = SecurityContextHolder.getCurrentUserId();
        log.info("获取VIP分类分析数据: userId={}, days={}", userId, days);
        StatsResponse.VipCategoryAnalysis analysis = statsService.getVipCategoryAnalysis(userId, days);
        return ApiResponse.success(analysis);
    }

    /**
     * 获取VIP用户性能分析数据
     */
    @GetMapping("/vip/performance")
    @Role("VIP")
    public ApiResponse<StatsResponse.VipPerformanceAnalysis> getVipPerformanceAnalysis(
            @RequestParam(value = "days", defaultValue = "30") Integer days) {
        Long userId = SecurityContextHolder.getCurrentUserId();
        log.info("获取VIP性能分析数据: userId={}, days={}", userId, days);
        StatsResponse.VipPerformanceAnalysis performance = statsService.getVipPerformanceAnalysis(userId, days);
        return ApiResponse.success(performance);
    }

    /**
     * 获取VIP用户智能建议
     */
    @GetMapping("/vip/suggestions")
    @Role("VIP")
    public ApiResponse<StatsResponse.VipSuggestions> getVipSuggestions() {
        Long userId = SecurityContextHolder.getCurrentUserId();
        log.info("获取VIP智能建议: userId={}", userId);
        StatsResponse.VipSuggestions suggestions = statsService.getVipSuggestions(userId);
        return ApiResponse.success(suggestions);
    }
}

