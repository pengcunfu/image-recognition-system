package com.pcf.recognition.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pcf.recognition.dto.*;
import com.pcf.recognition.entity.BatchRecognition;
import com.pcf.recognition.entity.BatchRecognitionItem;
import com.pcf.recognition.repository.BatchRecognitionRepository;
import com.pcf.recognition.repository.BatchRecognitionItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 批量识别服务
 * 处理批量图像识别相关业务逻辑
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class BatchRecognitionService {
    
    private final BatchRecognitionRepository batchRecognitionRepository;
    private final BatchRecognitionItemRepository batchRecognitionItemRepository;

    /**
     * 创建批量识别任务
     */
    public BatchTaskCreateResponseDto createBatchTask(Long userId, String taskName, String description, MultipartFile[] images) {
        log.info("创建批量识别任务: userId={}, taskName={}, imageCount={}", userId, taskName, images.length);
        
        try {
            // 创建批量任务
            BatchRecognition batchTask = new BatchRecognition();
            batchTask.setUserId(userId);
            batchTask.setTaskName(taskName);
            batchTask.setDescription(description);
            batchTask.setTotalCount(images.length);
            batchTask.setStatus(BatchRecognition.BatchStatus.PENDING);
            batchTask.setStartTime(LocalDateTime.now());
            
            // 计算总文件大小
            long totalSize = Arrays.stream(images).mapToLong(MultipartFile::getSize).sum();
            batchTask.setTotalSize(totalSize);
            
            batchRecognitionRepository.insert(batchTask);
            
            // 创建批量项
            for (int i = 0; i < images.length; i++) {
                MultipartFile image = images[i];
                
                BatchRecognitionItem item = new BatchRecognitionItem();
                item.setBatchId(batchTask.getId());
                item.setOriginalFilename(image.getOriginalFilename());
                item.setFileSize(image.getSize());
                item.setStatus(BatchRecognitionItem.ItemStatus.PENDING);
                
                batchRecognitionItemRepository.insert(item);
            }
            
            return BatchTaskCreateResponseDto.builder()
                .success(true)
                .message("批量识别任务创建成功")
                .taskId(batchTask.getId())
                .totalCount(images.length)
                .build();
            
        } catch (Exception e) {
            log.error("创建批量识别任务失败", e);
            return BatchTaskCreateResponseDto.builder()
                .success(false)
                .message("创建任务失败")
                .build();
        }
    }

    /**
     * 获取批量任务列表
     */
    public BatchTaskListResponseDto getBatchTasks(Long userId, Integer page, Integer size, String status) {
        log.info("获取批量任务列表: userId={}, page={}, size={}", userId, page, size);
        
        try {
            Page<BatchRecognition> pageRequest = new Page<>(page, size);
            
            LambdaQueryWrapper<BatchRecognition> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(BatchRecognition::getUserId, userId);
            
            if (status != null && !status.isEmpty()) {
                queryWrapper.eq(BatchRecognition::getStatus, BatchRecognition.BatchStatus.valueOf(status.toUpperCase()));
            }
            
            queryWrapper.orderByDesc(BatchRecognition::getCreateTime);
            
            Page<BatchRecognition> result = batchRecognitionRepository.selectPage(pageRequest, queryWrapper);
            
            return BatchTaskListResponseDto.builder()
                .success(true)
                .data(result.getRecords())
                .total(result.getTotal())
                .pages(result.getPages())
                .current(result.getCurrent())
                .size(result.getSize())
                .build();
            
        } catch (Exception e) {
            log.error("获取批量任务列表失败", e);
            return BatchTaskListResponseDto.builder()
                .success(false)
                .build();
        }
    }

    /**
     * 获取批量任务详情
     */
    public ApiResponse<BatchTaskDetailDto> getBatchTaskDetail(Long taskId, Long userId) {
        log.info("获取批量任务详情: taskId={}, userId={}", taskId, userId);
        
        try {
            BatchRecognition task = batchRecognitionRepository.selectOne(
                new LambdaQueryWrapper<BatchRecognition>()
                    .eq(BatchRecognition::getId, taskId)
                    .eq(BatchRecognition::getUserId, userId)
            );
            
            if (task == null) {
                return ApiResponse.error("任务不存在");
            }
            
            // 获取任务项
            List<BatchRecognitionItem> items = batchRecognitionItemRepository.selectList(
                new LambdaQueryWrapper<BatchRecognitionItem>()
                    .eq(BatchRecognitionItem::getBatchId, taskId)
                    .orderByAsc(BatchRecognitionItem::getCreateTime)
            );
            
            BatchTaskDetailDto taskDetail = BatchTaskDetailDto.builder()
                .task(task)
                .items(items)
                .build();
            
            return ApiResponse.success(taskDetail, "获取任务详情成功");
            
        } catch (Exception e) {
            log.error("获取批量任务详情失败", e);
            return ApiResponse.error("获取任务详情失败");
        }
    }

    /**
     * 删除批量任务
     */
    public OperationResultDto deleteBatchTask(Long taskId, Long userId) {
        log.info("删除批量任务: taskId={}, userId={}", taskId, userId);
        
        try {
            // 验证任务是否属于当前用户
            BatchRecognition task = batchRecognitionRepository.selectOne(
                new LambdaQueryWrapper<BatchRecognition>()
                    .eq(BatchRecognition::getId, taskId)
                    .eq(BatchRecognition::getUserId, userId)
            );
            
            if (task == null) {
                return OperationResultDto.builder()
                    .success(false)
                    .message("任务不存在")
                    .build();
            }
            
            // 删除任务项
            batchRecognitionItemRepository.delete(
                new LambdaQueryWrapper<BatchRecognitionItem>()
                    .eq(BatchRecognitionItem::getBatchId, taskId)
            );
            
            // 删除任务
            batchRecognitionRepository.deleteById(taskId);
            
            return OperationResultDto.builder()
                .success(true)
                .message("删除成功")
                .build();
            
        } catch (Exception e) {
            log.error("删除批量任务失败", e);
            return OperationResultDto.builder()
                .success(false)
                .message("删除失败")
                .build();
        }
    }

    /**
     * 更新任务进度
     */
    public BatchTaskProgressUpdateDto updateTaskProgress(Long taskId, Integer processedCount, Integer successCount, Integer failedCount) {
        log.info("更新任务进度: taskId={}, processed={}, success={}, failed={}", taskId, processedCount, successCount, failedCount);
        
        try {
            BatchRecognition task = batchRecognitionRepository.selectById(taskId);
            
            if (task == null) {
                return BatchTaskProgressUpdateDto.builder()
                    .success(false)
                    .message("任务不存在")
                    .build();
            }
            
            // 计算进度百分比
            int progress = task.getTotalCount() > 0 ? (processedCount * 100 / task.getTotalCount()) : 0;
            
            // 更新任务状态
            LambdaUpdateWrapper<BatchRecognition> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(BatchRecognition::getId, taskId)
                        .set(BatchRecognition::getProcessedCount, processedCount)
                        .set(BatchRecognition::getSuccessCount, successCount)
                        .set(BatchRecognition::getFailedCount, failedCount)
                        .set(BatchRecognition::getProgress, progress);
            
            // 如果全部处理完成，更新状态和结束时间
            if (processedCount.equals(task.getTotalCount())) {
                updateWrapper.set(BatchRecognition::getStatus, BatchRecognition.BatchStatus.COMPLETED)
                           .set(BatchRecognition::getEndTime, LocalDateTime.now());
            } else {
                updateWrapper.set(BatchRecognition::getStatus, BatchRecognition.BatchStatus.PROCESSING);
            }
            
            batchRecognitionRepository.update(null, updateWrapper);
            
            return BatchTaskProgressUpdateDto.builder()
                .success(true)
                .message("进度更新成功")
                .progress(progress)
                .build();
            
        } catch (Exception e) {
            log.error("更新任务进度失败", e);
            return BatchTaskProgressUpdateDto.builder()
                .success(false)
                .message("更新进度失败")
                .build();
        }
    }

    /**
     * 获取批量任务统计
     */
    public BatchTaskStatsDto getBatchStats(Long userId) {
        log.info("获取批量任务统计: userId={}", userId);
        
        try {
            // 总任务数
            Long totalTasks = batchRecognitionRepository.selectCount(
                new LambdaQueryWrapper<BatchRecognition>()
                    .eq(BatchRecognition::getUserId, userId)
            );
            
            // 已完成任务数
            Long completedTasks = batchRecognitionRepository.selectCount(
                new LambdaQueryWrapper<BatchRecognition>()
                    .eq(BatchRecognition::getUserId, userId)
                    .eq(BatchRecognition::getStatus, BatchRecognition.BatchStatus.COMPLETED)
            );
            
            // 处理中任务数
            Long processingTasks = batchRecognitionRepository.selectCount(
                new LambdaQueryWrapper<BatchRecognition>()
                    .eq(BatchRecognition::getUserId, userId)
                    .eq(BatchRecognition::getStatus, BatchRecognition.BatchStatus.PROCESSING)
            );
            
            return BatchTaskStatsDto.builder()
                .totalTasks(totalTasks)
                .completedTasks(completedTasks)
                .processingTasks(processingTasks)
                .pendingTasks(totalTasks - completedTasks - processingTasks)
                .build();
            
        } catch (Exception e) {
            log.error("获取批量任务统计失败", e);
            return BatchTaskStatsDto.builder()
                .totalTasks(0L)
                .completedTasks(0L)
                .processingTasks(0L)
                .pendingTasks(0L)
                .build();
        }
    }
}
