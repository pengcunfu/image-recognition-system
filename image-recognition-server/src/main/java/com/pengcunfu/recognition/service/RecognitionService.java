package com.pengcunfu.recognition.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pengcunfu.recognition.constant.ErrorCode;
import com.pengcunfu.recognition.entity.RecognitionResult;
import com.pengcunfu.recognition.entity.User;
import com.pengcunfu.recognition.enums.RecognitionStatus;
import com.pengcunfu.recognition.enums.RecognitionType;
import com.pengcunfu.recognition.exception.BusinessException;
import com.pengcunfu.recognition.exception.RateLimitException;
import com.pengcunfu.recognition.repository.RecognitionResultRepository;
import com.pengcunfu.recognition.repository.UserRepository;
import com.pengcunfu.recognition.request.RecognitionRequest;
import com.pengcunfu.recognition.response.PageResponse;
import com.pengcunfu.recognition.response.RecognitionResponse;
import com.pengcunfu.recognition.service.redis.RateLimitService;
import com.pengcunfu.recognition.util.DoubaoUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 图像识别服务
 * 处理图像识别相关业务逻辑
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RecognitionService {

    private final RecognitionResultRepository recognitionResultRepository;
    private final UserRepository userRepository;
    private final RateLimitService rateLimitService;
    private final DoubaoUtil doubaoUtil;

    /**
     * 执行图像识别
     */
    @Transactional
    public RecognitionResponse.RecognitionInfo recognizeImage(Long userId, RecognitionRequest.ImageRecognitionRequest request) {
        log.info("执行图像识别: userId={}, imageUrl={}", userId, request.getImageUrl());

        // 检查用户
        User user = userRepository.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND, "用户不存在");
        }

        // VIP用户限流检查（更宽松）
        boolean isVip = user.getVipExpireTime() != null && user.getVipExpireTime().isAfter(LocalDateTime.now());
        int maxCount = isVip ? 100 : 10;
        int timeWindow = 3600; // 1小时

        if (!rateLimitService.isRecognitionAllowed(userId, maxCount, timeWindow)) {
            throw new RateLimitException("识别次数已达上限，请稍后再试");
        }

        // 创建识别记录（使用 Builder 模式）
        RecognitionResult result = RecognitionResult.builder()
                .userId(userId)
                .imageUrl(request.getImageUrl())
                .recognitionType(request.getRecognitionType() != null && request.getRecognitionType() == 1
                        ? RecognitionType.DETAILED.getValue() 
                        : RecognitionType.QUICK.getValue())
                .status(RecognitionStatus.PENDING.getValue())
                .build();

        recognitionResultRepository.insert(result);

        try {
            // 调用 AI 识别服务
            boolean isDetailed = request.getRecognitionType() != null && request.getRecognitionType() == 1;
            String prompt = isDetailed 
                    ? "请详细分析这张图片，包括主要对象、特征、场景、颜色等详细信息" 
                    : "请快速识别这张图片中的主要对象";
            String aiResult = doubaoUtil.recognizeImage(
                    request.getImageUrl(),
                    prompt
            );

            // 更新识别结果（简化处理，实际应解析 AI 返回的 JSON）
            result.setResultJson(aiResult);
            result.setMainCategory("未分类"); // 从 AI 结果中解析
            result.setConfidence(new java.math.BigDecimal("0.95")); // 从 AI 结果中解析
            result.setTags("标签1,标签2"); // 从 AI 结果中解析
            result.setDescription("识别描述"); // 从 AI 结果中解析
            result.setStatus(RecognitionStatus.SUCCESS.getValue());
            result.setProcessingTime(100); // 计算实际耗时

            recognitionResultRepository.updateById(result);

            log.info("图像识别成功: userId={}, resultId={}, category={}", 
                    userId, result.getId(), result.getMainCategory());

            return convertToRecognitionInfo(result);

        } catch (Exception e) {
            log.error("图像识别失败: userId={}, resultId={}", userId, result.getId(), e);

            // 更新为失败状态
            result.setStatus(RecognitionStatus.FAILED.getValue());
            result.setErrorMessage(e.getMessage());
            recognitionResultRepository.updateById(result);

            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "图像识别失败: " + e.getMessage());
        }
    }

    /**
     * 获取识别历史列表
     */
    public PageResponse<RecognitionResponse.RecognitionInfo> getRecognitionHistory(
            Long userId, Integer page, Integer size) {
        log.info("获取识别历史: userId={}, page={}, size={}", userId, page, size);

        Page<RecognitionResult> pageRequest = new Page<>(page, size);
        Page<RecognitionResult> pageResult = recognitionResultRepository.findByUserId(pageRequest, userId);

        return PageResponse.<RecognitionResponse.RecognitionInfo>builder()
                .data(pageResult.getRecords().stream()
                        .map(this::convertToRecognitionInfo)
                        .collect(Collectors.toList()))
                .total(pageResult.getTotal())
                .page((int) pageResult.getCurrent())
                .size((int) pageResult.getSize())
                .pages((int) pageResult.getPages())
                    .build();
    }

    /**
     * 获取识别详情
     */
    public RecognitionResponse.RecognitionInfo getRecognitionDetail(Long userId, Long resultId) {
        log.info("获取识别详情: userId={}, resultId={}", userId, resultId);

        RecognitionResult result = recognitionResultRepository.findByIdAndUserId(resultId, userId);

        if (result == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "识别记录不存在或无权访问");
        }

        return convertToRecognitionInfo(result);
    }

    /**
     * 删除识别记录
     */
    @Transactional
    public void deleteRecognitionResult(Long userId, Long resultId) {
        log.info("删除识别记录: userId={}, resultId={}", userId, resultId);

        RecognitionResult result = recognitionResultRepository.findByIdAndUserId(resultId, userId);

        if (result == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "识别记录不存在或无权删除");
        }

        recognitionResultRepository.deleteById(resultId);

        log.info("识别记录删除成功: userId={}, resultId={}", userId, resultId);
    }

    /**
     * 转换为识别信息 DTO
     */
    private RecognitionResponse.RecognitionInfo convertToRecognitionInfo(RecognitionResult result) {
        return RecognitionResponse.RecognitionInfo.builder()
                .id(result.getId())
                .userId(result.getUserId())
                .imageUrl(result.getImageUrl())
                .imageName(result.getImageName())
                .mainCategory(result.getMainCategory())
                .category(result.getMainCategory())
                .confidence(result.getConfidence())
                .tags(result.getTags())
                .description(result.getDescription())
                .recognitionType(result.getRecognitionType())
                .resultJson(result.getResultJson())
                .status(result.getStatus())
                .errorMessage(result.getErrorMessage())
                .processingTime(result.getProcessingTime())
                .createdAt(result.getCreatedAt())
                .build();
    }

    /**
     * 批量图像识别
     */
    @Transactional
    public List<RecognitionResponse.RecognitionInfo> batchRecognizeImages(Long userId, RecognitionRequest.BatchRecognitionRequest request) {
        log.info("执行批量图像识别: userId={}, imageCount={}", userId, request.getImageUrls().length);

        // 检查用户
        User user = userRepository.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND, "用户不存在");
        }

        // 检查批量识别限流（1分钟内最多5次）
        if (!rateLimitService.isApiAllowed(userId, "batch_recognition", 5, 60)) {
            throw new RateLimitException("批量识别操作过于频繁，请稍后再试");
        }

        List<RecognitionResponse.RecognitionInfo> results = new ArrayList<>();
        
        // 逐个处理图片
        for (String imageUrl : request.getImageUrls()) {
            try {
                // 创建单个识别请求
                RecognitionRequest.ImageRecognitionRequest singleRequest = new RecognitionRequest.ImageRecognitionRequest();
                singleRequest.setImageUrl(imageUrl);
                singleRequest.setRecognitionType(request.getRecognitionType());
                
                // 执行识别
                RecognitionResponse.RecognitionInfo result = recognizeImage(userId, singleRequest);
                results.add(result);

        } catch (Exception e) {
                log.error("批量识别中单张图片识别失败: userId={}, imageUrl={}, error={}", 
                    userId, imageUrl, e.getMessage(), e);
                
                // 创建失败记录
                RecognitionResult failedResult = RecognitionResult.builder()
                        .userId(userId)
                        .imageUrl(imageUrl)
                        .recognitionType(request.getRecognitionType())
                        .status(RecognitionStatus.FAILED.getCode())
                        .errorMessage("识别失败: " + e.getMessage())
                        .createdAt(LocalDateTime.now())
                    .build();
                
                recognitionResultRepository.insert(failedResult);
                
                results.add(RecognitionResponse.RecognitionInfo.builder()
                        .id(failedResult.getId())
                        .userId(userId)
                        .imageUrl(imageUrl)
                        .recognitionType(request.getRecognitionType())
                        .status(RecognitionStatus.FAILED.getCode())
                        .errorMessage("识别失败: " + e.getMessage())
                        .createdAt(failedResult.getCreatedAt())
                        .build());
            }
        }
        
        log.info("批量图像识别完成: userId={}, totalCount={}, successCount={}", 
            userId, results.size(), 
            results.stream().filter(r -> r.getStatus() == RecognitionStatus.SUCCESS.getCode()).count());
        
        return results;
    }

    // ==================== 管理员方法 ====================

    /**
     * 获取识别记录列表（管理员）
     */
    public PageResponse<RecognitionResponse.RecognitionInfo> getRecordsAdmin(
            Integer page, Integer size, Integer status, Long userId, String keyword) {
        log.info("管理员获取识别记录列表: page={}, size={}, status={}, userId={}, keyword={}",
                page, size, status, userId, keyword);

        Page<RecognitionResult> resultPage = new Page<>(page, size);

        // 构建查询条件
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<RecognitionResult> queryWrapper =
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();

        if (status != null) {
            queryWrapper.eq(RecognitionResult::getStatus, status);
        }

        if (userId != null) {
            queryWrapper.eq(RecognitionResult::getUserId, userId);
        }

        if (keyword != null && !keyword.isEmpty()) {
            queryWrapper.like(RecognitionResult::getMainCategory, keyword);
        }

        queryWrapper.orderByDesc(RecognitionResult::getCreatedAt);

        Page<RecognitionResult> result = recognitionResultRepository.selectPage(resultPage, queryWrapper);

        return PageResponse.<RecognitionResponse.RecognitionInfo>builder()
                .data(result.getRecords().stream()
                        .map(this::convertToRecognitionInfo)
                        .collect(Collectors.toList()))
                .total(result.getTotal())
                .page((int) result.getCurrent())
                .size((int) result.getSize())
                .build();
    }

    /**
     * 删除识别记录（管理员）
     */
    @Transactional
    public void deleteRecord(Long recordId) {
        log.info("管理员删除识别记录: recordId={}", recordId);

        RecognitionResult record = recognitionResultRepository.selectById(recordId);
        if (record == null) {
            throw new BusinessException(ErrorCode.INVALID_PARAM, "识别记录不存在");
        }

        recognitionResultRepository.deleteById(recordId);
        log.info("识别记录删除成功: recordId={}", recordId);
    }

    /**
     * 批量删除识别记录（管理员）
     */
    @Transactional
    public void batchDeleteRecords(List<Long> ids) {
        log.info("管理员批量删除识别记录: count={}", ids.size());

        if (ids == null || ids.isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_PARAM, "删除列表不能为空");
        }

        // 逐个删除
        ids.forEach(id -> recognitionResultRepository.deleteById(id));
        log.info("批量删除识别记录成功: count={}", ids.size());
    }
}
