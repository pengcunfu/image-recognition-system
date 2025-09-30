package com.pcf.recognition.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pcf.recognition.entity.RecognitionHistory;
import com.pcf.recognition.repository.RecognitionHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 识别历史服务
 * 处理图像识别历史记录相关业务逻辑
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class RecognitionHistoryService {
    
    private final RecognitionHistoryRepository recognitionHistoryRepository;

    /**
     * 获取识别历史列表（分页）
     */
    public Map<String, Object> getRecognitionHistory(Long userId, Integer page, Integer size, String category, String status) {
        log.info("获取识别历史: userId={}, page={}, size={}", userId, page, size);
        
        try {
            Page<RecognitionHistory> pageRequest = new Page<>(page, size);
            
            LambdaQueryWrapper<RecognitionHistory> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(RecognitionHistory::getUserId, userId);
            
            if (category != null && !category.isEmpty()) {
                queryWrapper.eq(RecognitionHistory::getCategory, category);
            }
            
            if (status != null && !status.isEmpty()) {
                queryWrapper.eq(RecognitionHistory::getStatus, RecognitionHistory.RecognitionStatus.valueOf(status.toUpperCase()));
            }
            
            queryWrapper.orderByDesc(RecognitionHistory::getCreateTime);
            
            Page<RecognitionHistory> result = recognitionHistoryRepository.selectPage(pageRequest, queryWrapper);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", result.getRecords());
            response.put("total", result.getTotal());
            response.put("pages", result.getPages());
            response.put("current", result.getCurrent());
            response.put("size", result.getSize());
            
            return response;
            
        } catch (Exception e) {
            log.error("获取识别历史失败", e);
            return createErrorResponse("获取识别历史失败");
        }
    }

    /**
     * 获取识别历史详情
     */
    public Map<String, Object> getRecognitionDetail(Long id, Long userId) {
        log.info("获取识别历史详情: id={}, userId={}", id, userId);
        
        try {
            RecognitionHistory history = recognitionHistoryRepository.selectOne(
                new LambdaQueryWrapper<RecognitionHistory>()
                    .eq(RecognitionHistory::getId, id)
                    .eq(RecognitionHistory::getUserId, userId)
            );
            
            if (history == null) {
                return createErrorResponse("记录不存在");
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", history);
            
            return response;
            
        } catch (Exception e) {
            log.error("获取识别历史详情失败", e);
            return createErrorResponse("获取详情失败");
        }
    }

    /**
     * 删除识别历史记录
     */
    public Map<String, Object> deleteRecognitionHistory(Long id, Long userId) {
        log.info("删除识别历史: id={}, userId={}", id, userId);
        
        try {
            // 验证记录是否属于当前用户
            RecognitionHistory history = recognitionHistoryRepository.selectOne(
                new LambdaQueryWrapper<RecognitionHistory>()
                    .eq(RecognitionHistory::getId, id)
                    .eq(RecognitionHistory::getUserId, userId)
            );
            
            if (history == null) {
                return createErrorResponse("记录不存在");
            }
            
            // 逻辑删除
            recognitionHistoryRepository.deleteById(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "删除成功");
            
            return response;
            
        } catch (Exception e) {
            log.error("删除识别历史失败", e);
            return createErrorResponse("删除失败");
        }
    }

    /**
     * 批量删除识别历史记录
     */
    public Map<String, Object> batchDeleteRecognitionHistory(List<Long> ids, Long userId) {
        log.info("批量删除识别历史: ids={}, userId={}", ids, userId);
        
        try {
            // 验证所有记录都属于当前用户
            Long count = recognitionHistoryRepository.selectCount(
                new LambdaQueryWrapper<RecognitionHistory>()
                    .in(RecognitionHistory::getId, ids)
                    .eq(RecognitionHistory::getUserId, userId)
            );
            
            if (count != ids.size()) {
                return createErrorResponse("部分记录不存在或无权限");
            }
            
            // 批量逻辑删除
            recognitionHistoryRepository.deleteBatchIds(ids);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", String.format("成功删除 %d 条记录", ids.size()));
            
            return response;
            
        } catch (Exception e) {
            log.error("批量删除识别历史失败", e);
            return createErrorResponse("批量删除失败");
        }
    }

    /**
     * 收藏/取消收藏识别记录
     */
    public Map<String, Object> toggleFavorite(Long id, Long userId) {
        log.info("切换收藏状态: id={}, userId={}", id, userId);
        
        try {
            RecognitionHistory history = recognitionHistoryRepository.selectOne(
                new LambdaQueryWrapper<RecognitionHistory>()
                    .eq(RecognitionHistory::getId, id)
                    .eq(RecognitionHistory::getUserId, userId)
            );
            
            if (history == null) {
                return createErrorResponse("记录不存在");
            }
            
            boolean newFavoriteStatus = !Boolean.TRUE.equals(history.getIsFavorite());
            
            recognitionHistoryRepository.update(null,
                new LambdaUpdateWrapper<RecognitionHistory>()
                    .eq(RecognitionHistory::getId, id)
                    .set(RecognitionHistory::getIsFavorite, newFavoriteStatus)
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", newFavoriteStatus ? "已收藏" : "已取消收藏");
            response.put("isFavorite", newFavoriteStatus);
            
            return response;
            
        } catch (Exception e) {
            log.error("切换收藏状态失败", e);
            return createErrorResponse("操作失败");
        }
    }

    /**
     * 获取识别统计信息
     */
    public Map<String, Object> getRecognitionStats(Long userId) {
        log.info("获取识别统计信息: userId={}", userId);
        
        try {
            // 总识别次数
            Long totalCount = recognitionHistoryRepository.selectCount(
                new LambdaQueryWrapper<RecognitionHistory>()
                    .eq(RecognitionHistory::getUserId, userId)
            );
            
            // 成功次数
            Long successCount = recognitionHistoryRepository.selectCount(
                new LambdaQueryWrapper<RecognitionHistory>()
                    .eq(RecognitionHistory::getUserId, userId)
                    .eq(RecognitionHistory::getStatus, RecognitionHistory.RecognitionStatus.SUCCESS)
            );
            
            // 收藏次数
            Long favoriteCount = recognitionHistoryRepository.selectCount(
                new LambdaQueryWrapper<RecognitionHistory>()
                    .eq(RecognitionHistory::getUserId, userId)
                    .eq(RecognitionHistory::getIsFavorite, true)
            );
            
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalCount", totalCount);
            stats.put("successCount", successCount);
            stats.put("failedCount", totalCount - successCount);
            stats.put("favoriteCount", favoriteCount);
            stats.put("successRate", totalCount > 0 ? (double) successCount / totalCount * 100 : 0);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("stats", stats);
            
            return response;
            
        } catch (Exception e) {
            log.error("获取识别统计信息失败", e);
            return createErrorResponse("获取统计信息失败");
        }
    }

    /**
     * 保存识别记录
     */
    public RecognitionHistory saveRecognitionHistory(Long userId, String originalFilename, String imageUrl, 
                                                   String result, String category, Integer confidence) {
        try {
            RecognitionHistory history = new RecognitionHistory();
            history.setUserId(userId);
            history.setOriginalFilename(originalFilename);
            history.setImageUrl(imageUrl);
            history.setResult(result);
            history.setCategory(category);
            history.setConfidence(confidence);
            history.setStatus(RecognitionHistory.RecognitionStatus.SUCCESS);
            history.setIsFavorite(false);
            
            recognitionHistoryRepository.insert(history);
            
            return history;
            
        } catch (Exception e) {
            log.error("保存识别记录失败", e);
            throw new RuntimeException("保存识别记录失败", e);
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
