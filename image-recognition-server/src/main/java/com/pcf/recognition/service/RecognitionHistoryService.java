package com.pcf.recognition.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pcf.recognition.dto.RecognitionDto.*;
import com.pcf.recognition.entity.RecognitionHistory;
import com.pcf.recognition.repository.RecognitionHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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
    public RecognitionHistoryListResponseDto getRecognitionHistory(Long userId, Integer page, Integer size, String category, String status) {
        log.info("获取识别历史: userId={}, page={}, size={}", userId, page, size);

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

        RecognitionHistoryListResponseDto response = new RecognitionHistoryListResponseDto();
        response.setData(result.getRecords().stream()
                .map(this::convertToRecognitionHistoryDto)
                .collect(Collectors.toList()));
        response.setTotal(result.getTotal());
        response.setPages(result.getPages());
        response.setCurrent(result.getCurrent());
        response.setSize(result.getSize());

        return response;
    }

    /**
     * 获取识别历史详情
     */
    public RecognitionHistoryDetailResponseDto getRecognitionDetail(Long id, Long userId) {
        log.info("获取识别历史详情: id={}, userId={}", id, userId);

        RecognitionHistory history = recognitionHistoryRepository.selectOne(
                new LambdaQueryWrapper<RecognitionHistory>()
                        .eq(RecognitionHistory::getId, id)
                        .eq(RecognitionHistory::getUserId, userId)
        );

        if (history == null) {
            throw new RuntimeException("记录不存在");
        }

        RecognitionHistoryDetailResponseDto response = new RecognitionHistoryDetailResponseDto();
        response.setHistory(convertToRecognitionHistoryDto(history));

        return response;
    }

    /**
     * 删除识别历史记录
     */
    public void deleteRecognitionHistory(Long id, Long userId) {
        log.info("删除识别历史: id={}, userId={}", id, userId);

        // 验证记录是否属于当前用户
        RecognitionHistory history = recognitionHistoryRepository.selectOne(
                new LambdaQueryWrapper<RecognitionHistory>()
                        .eq(RecognitionHistory::getId, id)
                        .eq(RecognitionHistory::getUserId, userId)
        );

        if (history == null) {
            throw new RuntimeException("记录不存在");
        }

        // 逻辑删除
        recognitionHistoryRepository.deleteById(id);
    }

    /**
     * 批量删除识别历史记录
     */
    public void batchDeleteRecognitionHistory(List<Long> ids, Long userId) {
        log.info("批量删除识别历史: ids={}, userId={}", ids, userId);

        // 验证所有记录都属于当前用户
        Long count = recognitionHistoryRepository.selectCount(
                new LambdaQueryWrapper<RecognitionHistory>()
                        .in(RecognitionHistory::getId, ids)
                        .eq(RecognitionHistory::getUserId, userId)
        );

        if (count != ids.size()) {
            throw new RuntimeException("部分记录不存在或无权限");
        }

        // 批量逻辑删除
        recognitionHistoryRepository.deleteBatchIds(ids);
    }

    /**
     * 收藏/取消收藏识别记录
     */
    public FavoriteToggleResponseDto toggleFavorite(Long id, Long userId) {
        log.info("切换收藏状态: id={}, userId={}", id, userId);

        RecognitionHistory history = recognitionHistoryRepository.selectOne(
                new LambdaQueryWrapper<RecognitionHistory>()
                        .eq(RecognitionHistory::getId, id)
                        .eq(RecognitionHistory::getUserId, userId)
        );

        if (history == null) {
            throw new RuntimeException("记录不存在");
        }

        boolean newFavoriteStatus = !Boolean.TRUE.equals(history.getIsFavorite());

        recognitionHistoryRepository.update(null,
                new LambdaUpdateWrapper<RecognitionHistory>()
                        .eq(RecognitionHistory::getId, id)
                        .set(RecognitionHistory::getIsFavorite, newFavoriteStatus)
        );

        FavoriteToggleResponseDto response = new FavoriteToggleResponseDto();
        response.setIsFavorite(newFavoriteStatus);

        return response;
    }

    /**
     * 获取识别统计信息
     */
    public RecognitionStatsDto getRecognitionStats(Long userId) {
        log.info("获取识别统计信息: userId={}", userId);

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

        RecognitionStatsDto stats = new RecognitionStatsDto();
        stats.setTotalCount(totalCount);
        stats.setSuccessCount(successCount);
        stats.setFailedCount(totalCount - successCount);
        stats.setFavoriteCount(favoriteCount);
        stats.setSuccessRate(totalCount > 0 ? (double) successCount / totalCount * 100 : 0);

        return stats;
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
     * 转换识别历史实体为DTO
     */
    private RecognitionHistoryDto convertToRecognitionHistoryDto(RecognitionHistory history) {
        RecognitionHistoryDto dto = new RecognitionHistoryDto();
        BeanUtils.copyProperties(history, dto);
        dto.setStatus(history.getStatus().name());
        return dto;
    }
}
