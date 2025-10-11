package com.pcf.recognition.controller;

import com.pcf.recognition.dto.*;
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
    public ApiResponse<BatchTaskCreateResponseDto> batchRecognize(
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

        BatchTaskCreateResponseDto result = batchRecognitionService.createBatchTask(
                userId, taskName, description, images
        );

        if (result.getSuccess()) {
            return ApiResponse.success(result, result.getMessage());
        } else {
            return ApiResponse.error(result.getMessage());
        }
    }

    @Operation(summary = "获取批量任务列表", description = "获取用户的批量识别任务列表")
    @GetMapping("/tasks")
    @PreAuthorize("hasAnyRole('VIP', 'ADMIN')")
    public ApiResponse<BatchTaskListResponseDto> getBatchTasks(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "状态筛选") @RequestParam(required = false) String status,
            @RequestHeader(value = "Authorization", required = false) String token) {

        log.info("获取批量任务列表请求: page={}, size={}, status={}", page, size, status);

        // 模拟从token中解析用户ID
        Long userId = 1L;

        BatchTaskListResponseDto result = batchRecognitionService.getBatchTasks(userId, page, size, status);

        if (result.getSuccess()) {
            return ApiResponse.success(result, "获取任务列表成功");
        } else {
            return ApiResponse.error("获取任务列表失败");
        }
    }

    @Operation(summary = "获取批量任务详情", description = "获取批量识别任务的详细信息")
    @GetMapping("/tasks/{id}")
    @PreAuthorize("hasAnyRole('VIP', 'ADMIN')")
    public ApiResponse<BatchTaskDetailDto> getBatchTaskDetail(
            @Parameter(description = "任务ID") @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String token) {

        log.info("获取批量任务详情请求: id={}", id);

        // 模拟从token中解析用户ID
        Long userId = 1L;

        return batchRecognitionService.getBatchTaskDetail(id, userId);
    }

    @Operation(summary = "删除批量任务", description = "删除批量识别任务")
    @DeleteMapping("/tasks/{id}")
    @PreAuthorize("hasAnyRole('VIP', 'ADMIN')")
    public ApiResponse<Void> deleteBatchTask(
            @Parameter(description = "任务ID") @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String token) {

        log.info("删除批量任务请求: id={}", id);

        // 模拟从token中解析用户ID
        Long userId = 1L;

        OperationResultDto result = batchRecognitionService.deleteBatchTask(id, userId);

        if (result.getSuccess()) {
            return ApiResponse.success(null, result.getMessage());
        } else {
            return ApiResponse.error(result.getMessage());
        }
    }

    @Operation(summary = "获取批量任务进度", description = "获取批量识别任务的进度信息")
    @GetMapping("/tasks/{id}/progress")
    @PreAuthorize("hasAnyRole('VIP', 'ADMIN')")
    public ApiResponse<BatchTaskProgressDto> getTaskProgress(
            @Parameter(description = "任务ID") @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String token) {

        log.info("获取批量任务进度请求: id={}", id);

        // 模拟从token中解析用户ID
        Long userId = 1L;

        ApiResponse<BatchTaskDetailDto> result = batchRecognitionService.getBatchTaskDetail(id, userId);

        if (result.getCode() == 200) {
            BatchTaskDetailDto taskData = result.getData();

            BatchTaskProgressDto progress = BatchTaskProgressDto.builder()
                    .taskId(id)
                    .status(taskData.getTask().getStatus().name())
                    .progress(taskData.getTask().getProgress())
                    .totalCount(taskData.getTask().getTotalCount())
                    .processedCount(taskData.getTask().getProcessedCount())
                    .successCount(taskData.getTask().getSuccessCount())
                    .failedCount(taskData.getTask().getFailedCount())
                    .build();

            return ApiResponse.success(progress, "获取进度成功");
        } else {
            return ApiResponse.error(result.getMessage());
        }
    }

    @Operation(summary = "获取批量任务统计", description = "获取用户的批量任务统计信息")
    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('VIP', 'ADMIN')")
    public ApiResponse<BatchTaskStatsDto> getBatchStats(
            @RequestHeader(value = "Authorization", required = false) String token) {

        log.info("获取批量任务统计请求");

        // 模拟从token中解析用户ID
        Long userId = 1L;

        BatchTaskStatsDto result = batchRecognitionService.getBatchStats(userId);

        return ApiResponse.success(result, "获取统计信息成功");
    }
}