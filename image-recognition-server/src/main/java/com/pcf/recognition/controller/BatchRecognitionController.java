package com.pcf.recognition.controller;

import com.pcf.recognition.dto.*;
import com.pcf.recognition.dto.AuthDto.*;
import com.pcf.recognition.dto.BatchDto.*;
import com.pcf.recognition.service.BatchRecognitionService;
import com.pcf.recognition.util.TokenUtil;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 批量识别控制器
 * 提供批量图像识别功能
 */

@RestController
@RequestMapping("/api/v1/recognition/batch")
@Slf4j
@RequiredArgsConstructor
public class BatchRecognitionController {

    private final BatchRecognitionService batchRecognitionService;
    private final TokenUtil tokenUtil;


    @PostMapping("/recognize")
    @PreAuthorize("hasAnyRole('VIP', 'ADMIN')")
    public ApiResponse<BatchTaskCreateResponseDto> batchRecognize(@RequestParam("images") MultipartFile[] images, @RequestParam(required = false) String taskName, @RequestParam(required = false) String description, @RequestHeader(value = "Authorization", required = false) String token) {

        log.info("批量图像识别请求: 图片数量={}", images.length);

        // 从token中解析用户ID
        Long userId = tokenUtil.getUserIdFromHeader(token);
        if (userId == null) {
            return ApiResponse.error("无效的Token");
        }

        // 设置默认任务名称
        if (taskName == null || taskName.trim().isEmpty()) {
            taskName = "批量识别任务_" + System.currentTimeMillis();
        }

        BatchTaskCreateResponseDto result = batchRecognitionService.createBatchTask(userId, taskName, description, images);

        if (result.getSuccess()) {
            return ApiResponse.success(result, result.getMessage());
        } else {
            return ApiResponse.error(result.getMessage());
        }
    }


    @GetMapping("/tasks")
    @PreAuthorize("hasAnyRole('VIP', 'ADMIN')")
    public ApiResponse<BatchTaskListResponseDto> getBatchTasks(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(required = false) String status, @RequestHeader(value = "Authorization", required = false) String token) {

        log.info("获取批量任务列表请求: page={}, size={}, status={}", page, size, status);

        // 从token中解析用户ID
        Long userId = tokenUtil.getUserIdFromHeader(token);
        if (userId == null) {
            return ApiResponse.error("无效的Token");
        }

        BatchTaskListResponseDto result = batchRecognitionService.getBatchTasks(userId, page, size, status);

        if (result.getSuccess()) {
            return ApiResponse.success(result, "获取任务列表成功");
        } else {
            return ApiResponse.error("获取任务列表失败");
        }
    }


    @GetMapping("/tasks/{id}")
    @PreAuthorize("hasAnyRole('VIP', 'ADMIN')")
    public ApiResponse<BatchTaskDetailDto> getBatchTaskDetail(@PathVariable Long id, @RequestHeader(value = "Authorization", required = false) String token) {

        log.info("获取批量任务详情请求: id={}", id);

        // 从token中解析用户ID
        Long userId = tokenUtil.getUserIdFromHeader(token);
        if (userId == null) {
            return ApiResponse.error("无效的Token");
        }

        return batchRecognitionService.getBatchTaskDetail(id, userId);
    }


    @DeleteMapping("/tasks/{id}")
    @PreAuthorize("hasAnyRole('VIP', 'ADMIN')")
    public ApiResponse<Void> deleteBatchTask(@PathVariable Long id, @RequestHeader(value = "Authorization", required = false) String token) {

        log.info("删除批量任务请求: id={}", id);

        // 从token中解析用户ID
        Long userId = tokenUtil.getUserIdFromHeader(token);
        if (userId == null) {
            return ApiResponse.error("无效的Token");
        }

        OperationResultDto result = batchRecognitionService.deleteBatchTask(id, userId);

        if (result.getSuccess()) {
            return ApiResponse.success(null, result.getMessage());
        } else {
            return ApiResponse.error(result.getMessage());
        }
    }


    @GetMapping("/tasks/{id}/progress")
    @PreAuthorize("hasAnyRole('VIP', 'ADMIN')")
    public ApiResponse<BatchTaskProgressDto> getTaskProgress(@PathVariable Long id, @RequestHeader(value = "Authorization", required = false) String token) {

        log.info("获取批量任务进度请求: id={}", id);

        // 从token中解析用户ID
        Long userId = tokenUtil.getUserIdFromHeader(token);
        if (userId == null) {
            return ApiResponse.error("无效的Token");
        }

        ApiResponse<BatchTaskDetailDto> result = batchRecognitionService.getBatchTaskDetail(id, userId);

        if (result.getCode() == 200) {
            BatchTaskDetailDto taskData = result.getData();

            BatchTaskProgressDto progress = BatchTaskProgressDto.builder().taskId(id).status(taskData.getTask().getStatus().name()).progress(taskData.getTask().getProgress()).totalCount(taskData.getTask().getTotalCount()).processedCount(taskData.getTask().getProcessedCount()).successCount(taskData.getTask().getSuccessCount()).failedCount(taskData.getTask().getFailedCount()).build();

            return ApiResponse.success(progress, "获取进度成功");
        } else {
            return ApiResponse.error(result.getMessage());
        }
    }


    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('VIP', 'ADMIN')")
    public ApiResponse<BatchTaskStatsDto> getBatchStats(@RequestHeader(value = "Authorization", required = false) String token) {

        log.info("获取批量任务统计请求");

        // 从token中解析用户ID
        Long userId = tokenUtil.getUserIdFromHeader(token);
        if (userId == null) {
            return ApiResponse.error("无效的Token");
        }

        BatchTaskStatsDto result = batchRecognitionService.getBatchStats(userId);

        return ApiResponse.success(result, "获取统计信息成功");
    }
}