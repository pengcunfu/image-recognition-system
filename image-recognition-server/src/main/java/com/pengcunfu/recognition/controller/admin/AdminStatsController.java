package com.pengcunfu.recognition.controller.admin;

import com.pengcunfu.recognition.annotation.Role;
import com.pengcunfu.recognition.response.ApiResponse;
import com.pengcunfu.recognition.response.PageResponse;
import com.pengcunfu.recognition.response.StatsResponse;
import com.pengcunfu.recognition.service.StatsService;
import com.pengcunfu.recognition.service.SystemLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * 管理后台 - 统计数据控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/stats")
@RequiredArgsConstructor
@Role("ADMIN")
public class AdminStatsController {

    private final StatsService statsService;
    private final SystemLogService systemLogService;

    /**
     * 获取系统概览
     */
    @GetMapping("/overview")
    public ApiResponse<StatsResponse.SystemOverview> getSystemOverview() {
        log.info("获取系统概览");
        StatsResponse.SystemOverview overview = statsService.getSystemOverview();
        return ApiResponse.success(overview);
    }

    /**
     * 获取趋势数据
     */
    @GetMapping("/trends")
    public ApiResponse<StatsResponse.TrendData> getTrendData(
            @RequestParam(value = "days", defaultValue = "7") Integer days) {
        log.info("获取趋势数据: days={}", days);
        StatsResponse.TrendData trends = statsService.getTrendData(days);
        return ApiResponse.success(trends);
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
     * 获取系统日志
     */
    @GetMapping("/logs")
    public ApiResponse<PageResponse<StatsResponse.SystemLogInfo>> getSystemLogs(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "module", required = false) String module,
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "startTime", required = false) 
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(value = "endTime", required = false) 
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        log.info("获取系统日志: page={}, size={}, module={}", page, size, module);
        PageResponse<StatsResponse.SystemLogInfo> response = 
            systemLogService.getLogs(page, size, module, userId, startTime, endTime);
        return ApiResponse.success(response);
    }
}

