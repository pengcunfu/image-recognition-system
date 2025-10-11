package com.pcf.recognition.controller;

import com.pcf.recognition.dto.ApiResponse;
import com.pcf.recognition.service.BatchRecognitionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

/**
 * 批量识别控制器
 * 提供批量图像识别功能
 */
@Tag(name = "批量识别", description = "批量图像识别相关接口")
@RestController
@RequestMapping("/api/v1/recognition/batch")
@Slf4j
@RequiredArgsConstructor
public class BatchRecognitionController {
    
    private final BatchRecognitionService batchRecognitionService;

    @Operation(summary = "批量图像识别", description = "同时上传多张图片进行批量识别")
    @PostMapping("/recognize")
    @PreAuthorize("hasAnyRole('VIP', 'ADMIN')")
    public ApiResponse<Map<String, Object>> batchRecognize(
            @Parameter(description = "图片文件列表") @RequestParam("images") MultipartFile[] images,
            @Parameter(description = "任务名称") @RequestParam(required = false) String taskName,
            @Parameter(description = "任务描述") @RequestParam(required = false) String description,
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        log.info("批量图像识别请求: 图片数量={}", images.length);
        
        // 模拟从token中解析用户ID
        Long userId = 1L;
        
        // 设置默认任务名称
        if (taskName == null || taskName.trim().isEmpty()) {
            taskName = "批量识别任务_" + System.currentTimeMillis();
        }
        
        Map<String, Object> result = batchRecognitionService.createBatchTask(
            userId, taskName, description, images
        );
        
        if ((Boolean) result.get("success")) {
            return ApiResponse.success(result, (String) result.get("message"));
        } else {
            return ApiResponse.error((String) result.get("message"));
        }
    }

    @Operation(summary = "获取批量任务列表", description = "获取用户的批量识别任务列表")
    @GetMapping("/tasks")
    @PreAuthorize("hasAnyRole('VIP', 'ADMIN')")
    public ApiResponse<Map<String, Object>> getBatchTasks(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "状态筛选") @RequestParam(required = false) String status,
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        log.info("获取批量任务列表请求: page={}, size={}, status={}", page, size, status);
        
        // 模拟从token中解析用户ID
        Long userId = 1L;
        
        Map<String, Object> result = batchRecognitionService.getBatchTasks(userId, page, size, status);
        
        if ((Boolean) result.get("success")) {
            return ApiResponse.success(result, "获取任务列表成功");
        } else {
            return ApiResponse.error((String) result.get("message"));
        }
    }

    @Operation(summary = "获取批量任务详情", description = "获取批量识别任务的详细信息")
    @GetMapping("/tasks/{id}")
    @PreAuthorize("hasAnyRole('VIP', 'ADMIN')")
    public ApiResponse<Map<String, Object>> getBatchTaskDetail(
            @Parameter(description = "任务ID") @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        log.info("获取批量任务详情请求: id={}", id);
        
        // 模拟从token中解析用户ID
        Long userId = 1L;
        
        Map<String, Object> result = batchRecognitionService.getBatchTaskDetail(id, userId);
        
        if ((Boolean) result.get("success")) {
            return ApiResponse.success((Map<String, Object>) result.get("data"), "获取任务详情成功");
        } else {
            return ApiResponse.error((String) result.get("message"));
        }
    }

    @Operation(summary = "删除批量任务", description = "删除批量识别任务")
    @DeleteMapping("/tasks/{id}")
    @PreAuthorize("hasAnyRole('VIP', 'ADMIN')")
    public ApiResponse<String> deleteBatchTask(
            @Parameter(description = "任务ID") @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        log.info("删除批量任务请求: id={}", id);
        
        // 模拟从token中解析用户ID
        Long userId = 1L;
        
        Map<String, Object> result = batchRecognitionService.deleteBatchTask(id, userId);
        
        if ((Boolean) result.get("success")) {
            return ApiResponse.success(null, (String) result.get("message"));
        } else {
            return ApiResponse.error((String) result.get("message"));
        }
    }

    @Operation(summary = "获取批量任务进度", description = "获取批量识别任务的进度信息")
    @GetMapping("/tasks/{id}/progress")
    @PreAuthorize("hasAnyRole('VIP', 'ADMIN')")
    public ApiResponse<Map<String, Object>> getTaskProgress(
            @Parameter(description = "任务ID") @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        log.info("获取批量任务进度请求: id={}", id);
        
        // 模拟从token中解析用户ID
        Long userId = 1L;
        
        Map<String, Object> result = batchRecognitionService.getBatchTaskDetail(id, userId);
        
        if ((Boolean) result.get("success")) {
            Map<String, Object> taskData = (Map<String, Object>) result.get("data");
            Map<String, Object> task = (Map<String, Object>) taskData.get("task");
            
            Map<String, Object> progress = new HashMap<>();
            progress.put("taskId", id);
            progress.put("status", task.get("status"));
            progress.put("progress", task.get("progress"));
            progress.put("totalCount", task.get("totalCount"));
            progress.put("processedCount", task.get("processedCount"));
            progress.put("successCount", task.get("successCount"));
            progress.put("failedCount", task.get("failedCount"));
            
            return ApiResponse.success(progress, "获取进度成功");
        } else {
            return ApiResponse.error((String) result.get("message"));
        }
    }

    @Operation(summary = "获取批量任务统计", description = "获取用户的批量任务统计信息")
    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('VIP', 'ADMIN')")
    public ApiResponse<Map<String, Object>> getBatchStats(
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        log.info("获取批量任务统计请求");
        
        // 模拟从token中解析用户ID
        Long userId = 1L;
        
        Map<String, Object> result = batchRecognitionService.getBatchStats(userId);
        
        if ((Boolean) result.get("success")) {
            return ApiResponse.success((Map<String, Object>) result.get("stats"), "获取统计信息成功");
        } else {
            return ApiResponse.error((String) result.get("message"));
        }
    }
}