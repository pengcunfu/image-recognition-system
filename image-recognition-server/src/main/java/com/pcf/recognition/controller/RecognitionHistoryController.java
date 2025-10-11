package com.pcf.recognition.controller;

import com.pcf.recognition.dto.*;
import com.pcf.recognition.service.RecognitionHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ApiResponse<RecognitionHistoryListResponseDto> getRecognitionHistory(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "分类筛选") @RequestParam(required = false) String category,
            @Parameter(description = "状态筛选") @RequestParam(required = false) String status,
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        log.info("获取识别历史列表请求: page={}, size={}", page, size);
        
        try {
            // 模拟从token中解析用户ID
            Long userId = 1L;
            
            RecognitionHistoryListResponseDto result = recognitionHistoryService.getRecognitionHistory(userId, page, size, category, status);
            return ApiResponse.success(result, "获取识别历史成功");
        } catch (Exception e) {
            log.error("获取识别历史失败", e);
            return ApiResponse.error("获取识别历史失败");
        }
    }

    @Operation(summary = "获取识别历史详情", description = "获取单个识别记录的详细信息")
    @GetMapping("/{id}")
    public ApiResponse<RecognitionHistoryDto> getRecognitionDetail(
            @Parameter(description = "记录ID") @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        log.info("获取识别历史详情请求: id={}", id);
        
        try {
            // 模拟从token中解析用户ID
            Long userId = 1L;
            
            RecognitionHistoryDetailResponseDto result = recognitionHistoryService.getRecognitionDetail(id, userId);
            return ApiResponse.success(result.getHistory(), "获取详情成功");
        } catch (Exception e) {
            log.error("获取识别历史详情失败", e);
            return ApiResponse.error("记录不存在或获取失败");
        }
    }

    @Operation(summary = "删除识别历史记录", description = "删除单个识别历史记录")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteRecognitionHistory(
            @Parameter(description = "记录ID") @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        log.info("删除识别历史请求: id={}", id);
        
        try {
            // 模拟从token中解析用户ID
            Long userId = 1L;
            
            recognitionHistoryService.deleteRecognitionHistory(id, userId);
            return ApiResponse.success(null, "删除成功");
        } catch (Exception e) {
            log.error("删除识别历史失败", e);
            return ApiResponse.error("删除失败");
        }
    }

    @Operation(summary = "批量删除识别历史记录", description = "批量删除多个识别历史记录")
    @DeleteMapping("/batch")
    public ApiResponse<Void> batchDeleteRecognitionHistory(
            @Parameter(description = "记录ID列表") @RequestBody List<Long> ids,
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        log.info("批量删除识别历史请求: ids={}", ids);
        
        try {
            // 模拟从token中解析用户ID
            Long userId = 1L;
            
            recognitionHistoryService.batchDeleteRecognitionHistory(ids, userId);
            return ApiResponse.success(null, String.format("成功删除 %d 条记录", ids.size()));
        } catch (Exception e) {
            log.error("批量删除识别历史失败", e);
            return ApiResponse.error("批量删除失败");
        }
    }

    @Operation(summary = "收藏/取消收藏", description = "切换识别记录的收藏状态")
    @PostMapping("/{id}/favorite")
    public ApiResponse<FavoriteToggleResponseDto> toggleFavorite(
            @Parameter(description = "记录ID") @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        log.info("切换收藏状态请求: id={}", id);
        
        try {
            // 模拟从token中解析用户ID
            Long userId = 1L;
            
            FavoriteToggleResponseDto result = recognitionHistoryService.toggleFavorite(id, userId);
            String message = result.getIsFavorite() ? "已收藏" : "已取消收藏";
            return ApiResponse.success(result, message);
        } catch (Exception e) {
            log.error("切换收藏状态失败", e);
            return ApiResponse.error("操作失败");
        }
    }

    @Operation(summary = "获取识别统计信息", description = "获取用户的识别统计数据")
    @GetMapping("/stats")
    public ApiResponse<RecognitionStatsDto> getRecognitionStats(
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        log.info("获取识别统计信息请求");
        
        try {
            // 模拟从token中解析用户ID
            Long userId = 1L;
            
            RecognitionStatsDto result = recognitionHistoryService.getRecognitionStats(userId);
            return ApiResponse.success(result, "获取统计信息成功");
        } catch (Exception e) {
            log.error("获取识别统计信息失败", e);
            return ApiResponse.error("获取统计信息失败");
        }
    }
}