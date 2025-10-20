package com.pengcunfu.recognition.controller;

import com.pengcunfu.recognition.response.ApiResponse;
import com.pengcunfu.recognition.response.StatsResponse;
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
}

