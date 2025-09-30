package com.pcf.recognition.controller;

import com.pcf.recognition.dto.ApiResponse;
import com.pcf.recognition.service.RecognitionHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 识别历史记录控制器
 * 提供识别历史查询、管理等功能
 */
@Tag(name = "识别历史", description = "识别历史记录查询、管理等接口")
@RestController
@RequestMapping("/api/v1/recognition/history")
@Slf4j
@RequiredArgsConstructor
public class RecognitionHistoryController {
    
    private final RecognitionHistoryService recognitionHistoryService;

    @Operation(summary = "获取识别历史列表", description = "分页获取用户的识别历史记录")
    @GetMapping
    public ApiResponse<Map<String, Object>> getRecognitionHistory(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "分类筛选") @RequestParam(required = false) String category,
            @Parameter(description = "状态筛选") @RequestParam(required = false) String status,
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        log.info("获取识别历史列表请求: page={}, size={}", page, size);
        
        // 模拟从token中解析用户ID
        Long userId = 1L;
        
        Map<String, Object> result = recognitionHistoryService.getRecognitionHistory(userId, page, size, category, status);
        
        if ((Boolean) result.get("success")) {
            return ApiResponse.success(result, "获取识别历史成功");
        } else {
            return ApiResponse.error((String) result.get("message"));
        }
    }

    @Operation(summary = "获取识别历史详情", description = "获取单个识别记录的详细信息")
    @GetMapping("/{id}")
    public ApiResponse<Map<String, Object>> getRecognitionDetail(
            @Parameter(description = "记录ID") @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        log.info("获取识别历史详情请求: id={}", id);
        
        // 模拟从token中解析用户ID
        Long userId = 1L;
        
        Map<String, Object> result = recognitionHistoryService.getRecognitionDetail(id, userId);
        
        if ((Boolean) result.get("success")) {
            return ApiResponse.success((Map<String, Object>) result.get("data"), "获取详情成功");
        } else {
            return ApiResponse.error((String) result.get("message"));
        }
    }

    @Operation(summary = "删除识别历史记录", description = "删除单个识别历史记录")
    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteRecognitionHistory(
            @Parameter(description = "记录ID") @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        log.info("删除识别历史请求: id={}", id);
        
        // 模拟从token中解析用户ID
        Long userId = 1L;
        
        Map<String, Object> result = recognitionHistoryService.deleteRecognitionHistory(id, userId);
        
        if ((Boolean) result.get("success")) {
            return ApiResponse.success(null, (String) result.get("message"));
        } else {
            return ApiResponse.error((String) result.get("message"));
        }
    }

    @Operation(summary = "批量删除识别历史记录", description = "批量删除多个识别历史记录")
    @DeleteMapping("/batch")
    public ApiResponse<String> batchDeleteRecognitionHistory(
            @Parameter(description = "记录ID列表") @RequestBody List<Long> ids,
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        log.info("批量删除识别历史请求: ids={}", ids);
        
        // 模拟从token中解析用户ID
        Long userId = 1L;
        
        Map<String, Object> result = recognitionHistoryService.batchDeleteRecognitionHistory(ids, userId);
        
        if ((Boolean) result.get("success")) {
            return ApiResponse.success(null, (String) result.get("message"));
        } else {
            return ApiResponse.error((String) result.get("message"));
        }
    }

    @Operation(summary = "收藏/取消收藏", description = "切换识别记录的收藏状态")
    @PostMapping("/{id}/favorite")
    public ApiResponse<Map<String, Object>> toggleFavorite(
            @Parameter(description = "记录ID") @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        log.info("切换收藏状态请求: id={}", id);
        
        // 模拟从token中解析用户ID
        Long userId = 1L;
        
        Map<String, Object> result = recognitionHistoryService.toggleFavorite(id, userId);
        
        if ((Boolean) result.get("success")) {
            Map<String, Object> data = new HashMap<>();
            data.put("isFavorite", result.get("isFavorite"));
            return ApiResponse.success(data, (String) result.get("message"));
        } else {
            return ApiResponse.error((String) result.get("message"));
        }
    }

    @Operation(summary = "获取识别统计信息", description = "获取用户的识别统计数据")
    @GetMapping("/stats")
    public ApiResponse<Map<String, Object>> getRecognitionStats(
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        log.info("获取识别统计信息请求");
        
        // 模拟从token中解析用户ID
        Long userId = 1L;
        
        Map<String, Object> result = recognitionHistoryService.getRecognitionStats(userId);
        
        if ((Boolean) result.get("success")) {
            return ApiResponse.success((Map<String, Object>) result.get("stats"), "获取统计信息成功");
        } else {
            return ApiResponse.error((String) result.get("message"));
        }
    }
}