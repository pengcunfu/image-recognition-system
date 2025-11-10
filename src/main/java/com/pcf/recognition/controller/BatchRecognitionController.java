package com.pcf.recognition.controller;

import com.pcf.recognition.dto.*;
import com.pcf.recognition.dto.BatchDto.*;
import com.pcf.recognition.service.ImageRecognitionService;
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

    private final ImageRecognitionService imageRecognitionService;
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

        try {
            // 转换为List<MultipartFile>
            java.util.List<MultipartFile> fileList = java.util.Arrays.asList(images);
            
            // 调用图像识别服务进行批量处理
            imageRecognitionService.uploadAndRecognizeImages(fileList, null);
            
            // 构建响应
            BatchTaskCreateResponseDto result = BatchTaskCreateResponseDto.builder()
                    .success(true)
                    .message("批量识别任务完成")
                    .taskId(System.currentTimeMillis()) // 生成任务ID
                    .totalCount(images.length)
                    .build();

            return ApiResponse.success(result, "批量识别完成");
            
        } catch (Exception e) {
            log.error("批量识别失败", e);
            return ApiResponse.error("批量识别失败: " + e.getMessage());
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

        // 简化实现，返回空列表
        BatchTaskListResponseDto result = BatchTaskListResponseDto.builder()
                .success(true)
                .data(new java.util.ArrayList<>())
                .total(0L)
                .pages(0L)
                .current((long) page)
                .size((long) size)
                .build();

        return ApiResponse.success(result, "获取任务列表成功");
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

        return ApiResponse.error("任务不存在");
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

        return ApiResponse.error("任务不存在");
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

        return ApiResponse.error("任务不存在");
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

        // 简化实现，返回空统计
        BatchTaskStatsDto result = BatchTaskStatsDto.builder()
                .totalTasks(0L)
                .completedTasks(0L)
                .processingTasks(0L)
                .pendingTasks(0L)
                .build();

        return ApiResponse.success(result, "获取统计信息成功");
    }
}