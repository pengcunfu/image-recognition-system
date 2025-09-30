package com.pcf.recognition.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
    public Map<String, Object> createBatchTask(Long userId, String taskName, String description, MultipartFile[] images) {
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
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "批量识别任务创建成功");
            response.put("taskId", batchTask.getId());
            response.put("totalCount", images.length);
            
            return response;
            
        } catch (Exception e) {
            log.error("创建批量识别任务失败", e);
            return createErrorResponse("创建任务失败");
        }
    }

    /**
     * 获取批量任务列表
     */
    public Map<String, Object> getBatchTasks(Long userId, Integer page, Integer size, String status) {
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
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", result.getRecords());
            response.put("total", result.getTotal());
            response.put("pages", result.getPages());
            response.put("current", result.getCurrent());
            response.put("size", result.getSize());
            
            return response;
            
        } catch (Exception e) {
            log.error("获取批量任务列表失败", e);
            return createErrorResponse("获取任务列表失败");
        }
    }

    /**
     * 获取批量任务详情
     */
    public Map<String, Object> getBatchTaskDetail(Long taskId, Long userId) {
        log.info("获取批量任务详情: taskId={}, userId={}", taskId, userId);
        
        try {
            BatchRecognition task = batchRecognitionRepository.selectOne(
                new LambdaQueryWrapper<BatchRecognition>()
                    .eq(BatchRecognition::getId, taskId)
                    .eq(BatchRecognition::getUserId, userId)
            );
            
            if (task == null) {
                return createErrorResponse("任务不存在");
            }
            
            // 获取任务项
            List<BatchRecognitionItem> items = batchRecognitionItemRepository.selectList(
                new LambdaQueryWrapper<BatchRecognitionItem>()
                    .eq(BatchRecognitionItem::getBatchId, taskId)
                    .orderByAsc(BatchRecognitionItem::getCreateTime)
            );
            
            Map<String, Object> taskDetail = new HashMap<>();
            taskDetail.put("task", task);
            taskDetail.put("items", items);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", taskDetail);
            
            return response;
            
        } catch (Exception e) {
            log.error("获取批量任务详情失败", e);
            return createErrorResponse("获取任务详情失败");
        }
    }

    /**
     * 删除批量任务
     */
    public Map<String, Object> deleteBatchTask(Long taskId, Long userId) {
        log.info("删除批量任务: taskId={}, userId={}", taskId, userId);
        
        try {
            // 验证任务是否属于当前用户
            BatchRecognition task = batchRecognitionRepository.selectOne(
                new LambdaQueryWrapper<BatchRecognition>()
                    .eq(BatchRecognition::getId, taskId)
                    .eq(BatchRecognition::getUserId, userId)
            );
            
            if (task == null) {
                return createErrorResponse("任务不存在");
            }
            
            // 删除任务项
            batchRecognitionItemRepository.delete(
                new LambdaQueryWrapper<BatchRecognitionItem>()
                    .eq(BatchRecognitionItem::getBatchId, taskId)
            );
            
            // 删除任务
            batchRecognitionRepository.deleteById(taskId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "删除成功");
            
            return response;
            
        } catch (Exception e) {
            log.error("删除批量任务失败", e);
            return createErrorResponse("删除失败");
        }
    }

    /**
     * 更新任务进度
     */
    public Map<String, Object> updateTaskProgress(Long taskId, Integer processedCount, Integer successCount, Integer failedCount) {
        log.info("更新任务进度: taskId={}, processed={}, success={}, failed={}", taskId, processedCount, successCount, failedCount);
        
        try {
            BatchRecognition task = batchRecognitionRepository.selectById(taskId);
            
            if (task == null) {
                return createErrorResponse("任务不存在");
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
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "进度更新成功");
            response.put("progress", progress);
            
            return response;
            
        } catch (Exception e) {
            log.error("更新任务进度失败", e);
            return createErrorResponse("更新进度失败");
        }
    }

    /**
     * 获取批量任务统计
     */
    public Map<String, Object> getBatchStats(Long userId) {
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
            
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalTasks", totalTasks);
            stats.put("completedTasks", completedTasks);
            stats.put("processingTasks", processingTasks);
            stats.put("pendingTasks", totalTasks - completedTasks - processingTasks);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("stats", stats);
            
            return response;
            
        } catch (Exception e) {
            log.error("获取批量任务统计失败", e);
            return createErrorResponse("获取统计失败");
        }
    }
    
    /**
     * 创建错误响应
     */
    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        return response;
    }
}
