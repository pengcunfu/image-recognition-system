package com.pcf.recognition.controller;

import com.pcf.recognition.dto.*;
import com.pcf.recognition.service.RecognitionHistoryService;
import com.pcf.recognition.util.TokenUtil;



import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 识别历史记录控制器
 * 提供识别历史查询、管理等功能
 */

@RestController
@RequestMapping("/api/v1/recognition/history")
@Slf4j
@RequiredArgsConstructor
public class RecognitionHistoryController {
    
    private final RecognitionHistoryService recognitionHistoryService;
    private final TokenUtil tokenUtil;

    
    @GetMapping
    public ApiResponse<RecognitionHistoryListResponseDto> getRecognitionHistory(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status,
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        log.info("获取识别历史列表请求: page={}, size={}", page, size);
        
        try {
            // 从token中解析用户ID
            Long userId = tokenUtil.getUserIdFromHeader(token);
            if (userId == null) {
                return ApiResponse.error("无效的Token");
            }
            
            RecognitionHistoryListResponseDto result = recognitionHistoryService.getRecognitionHistory(userId, page, size, category, status);
            return ApiResponse.success(result, "获取识别历史成功");
        } catch (Exception e) {
            log.error("获取识别历史失败", e);
            return ApiResponse.error("获取识别历史失败");
        }
    }

    
    @GetMapping("/{id}")
    public ApiResponse<RecognitionHistoryDto> getRecognitionDetail(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        log.info("获取识别历史详情请求: id={}", id);
        
        try {
            // 从token中解析用户ID
            Long userId = tokenUtil.getUserIdFromHeader(token);
            if (userId == null) {
                return ApiResponse.error("无效的Token");
            }
            
            RecognitionHistoryDetailResponseDto result = recognitionHistoryService.getRecognitionDetail(id, userId);
            return ApiResponse.success(result.getHistory(), "获取详情成功");
        } catch (Exception e) {
            log.error("获取识别历史详情失败", e);
            return ApiResponse.error("记录不存在或获取失败");
        }
    }

    
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteRecognitionHistory(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        log.info("删除识别历史请求: id={}", id);
        
        try {
            // 从token中解析用户ID
            Long userId = tokenUtil.getUserIdFromHeader(token);
            if (userId == null) {
                return ApiResponse.error("无效的Token");
            }
            
            recognitionHistoryService.deleteRecognitionHistory(id, userId);
            return ApiResponse.success(null, "删除成功");
        } catch (Exception e) {
            log.error("删除识别历史失败", e);
            return ApiResponse.error("删除失败");
        }
    }

    
    @DeleteMapping("/batch")
    public ApiResponse<Void> batchDeleteRecognitionHistory(
            @RequestBody List<Long> ids,
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        log.info("批量删除识别历史请求: ids={}", ids);
        
        try {
            // 从token中解析用户ID
            Long userId = tokenUtil.getUserIdFromHeader(token);
            if (userId == null) {
                return ApiResponse.error("无效的Token");
            }
            
            recognitionHistoryService.batchDeleteRecognitionHistory(ids, userId);
            return ApiResponse.success(null, String.format("成功删除 %d 条记录", ids.size()));
        } catch (Exception e) {
            log.error("批量删除识别历史失败", e);
            return ApiResponse.error("批量删除失败");
        }
    }

    
    @PostMapping("/{id}/favorite")
    public ApiResponse<FavoriteToggleResponseDto> toggleFavorite(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        log.info("切换收藏状态请求: id={}", id);
        
        try {
            // 从token中解析用户ID
            Long userId = tokenUtil.getUserIdFromHeader(token);
            if (userId == null) {
                return ApiResponse.error("无效的Token");
            }
            
            FavoriteToggleResponseDto result = recognitionHistoryService.toggleFavorite(id, userId);
            String message = result.getIsFavorite() ? "已收藏" : "已取消收藏";
            return ApiResponse.success(result, message);
        } catch (Exception e) {
            log.error("切换收藏状态失败", e);
            return ApiResponse.error("操作失败");
        }
    }

    
    @GetMapping("/stats")
    public ApiResponse<RecognitionStatsDto> getRecognitionStats(
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        log.info("获取识别统计信息请求");
        
        try {
            // 从token中解析用户ID
            Long userId = tokenUtil.getUserIdFromHeader(token);
            if (userId == null) {
                return ApiResponse.error("无效的Token");
            }
            
            RecognitionStatsDto result = recognitionHistoryService.getRecognitionStats(userId);
            return ApiResponse.success(result, "获取统计信息成功");
        } catch (Exception e) {
            log.error("获取识别统计信息失败", e);
            return ApiResponse.error("获取统计信息失败");
        }
    }
}