package com.pengcunfu.recognition.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
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
    private final com.pengcunfu.recognition.repository.KnowledgeRepository knowledgeRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    // 提示词缓存
    private String quickPrompt;
    private String detailedPrompt;
    
    /**
     * 加载提示词文件
     */
    private String loadPrompt(String filename) {
        try {
            ClassPathResource resource = new ClassPathResource("prompts/" + filename);
            return StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("加载提示词文件失败: {}", filename, e);
            return "";
        }
    }
    
    /**
     * 获取快速识别提示词
     */
    private String getQuickPrompt() {
        if (quickPrompt == null) {
            quickPrompt = loadPrompt("image-recognition-prompt.txt");
        }
        return quickPrompt;
    }
    
    /**
     * 获取详细识别提示词
     */
    private String getDetailedPrompt() {
        if (detailedPrompt == null) {
            detailedPrompt = loadPrompt("image-recognition-detailed-prompt.txt");
        }
        return detailedPrompt;
    }
    
    /**
     * 从AI返回结果中提取JSON
     */
    private String extractJson(String aiResult) {
        if (aiResult == null || aiResult.isEmpty()) {
            return "{}";
        }
        
        // 尝试找到JSON代码块
        int jsonStart = aiResult.indexOf("```json");
        if (jsonStart != -1) {
            jsonStart = aiResult.indexOf("\n", jsonStart) + 1;
            int jsonEnd = aiResult.indexOf("```", jsonStart);
            if (jsonEnd != -1) {
                return aiResult.substring(jsonStart, jsonEnd).trim();
            }
        }
        
        // 尝试找到大括号包裹的JSON
        int braceStart = aiResult.indexOf("{");
        int braceEnd = aiResult.lastIndexOf("}");
        if (braceStart != -1 && braceEnd != -1 && braceEnd > braceStart) {
            return aiResult.substring(braceStart, braceEnd + 1).trim();
        }
        
        // 直接返回原始结果
        return aiResult.trim();
    }

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
                .imageName(request.getImageName())
                .imageSize(request.getImageSize())
                .imageWidth(request.getImageWidth())
                .imageHeight(request.getImageHeight())
                .recognitionType(request.getRecognitionType() != null && request.getRecognitionType() == 1
                        ? RecognitionType.DETAILED.getValue() 
                        : RecognitionType.QUICK.getValue())
                .status(RecognitionStatus.PENDING.getValue())
                .build();

        recognitionResultRepository.insert(result);

        try {
            long startTime = System.currentTimeMillis();
            
            // 调用 AI 识别服务，使用预定义的提示词
            boolean isDetailed = request.getRecognitionType() != null && request.getRecognitionType() == 1;
            String prompt = isDetailed ? getDetailedPrompt() : getQuickPrompt();
            
            log.info("使用提示词类型: {}", isDetailed ? "详细" : "快速");
            String aiResult = doubaoUtil.recognizeImage(request.getImageUrl(), prompt);
            
            // 提取JSON格式结果
            String jsonResult = extractJson(aiResult);
            log.info("提取的JSON结果: {}", jsonResult);
            
            // 解析 AI 返回的 JSON
            String category = "未分类";
            String name = "未知";
            BigDecimal confidence = new BigDecimal("0.0");
            String tags = "";
            String description = "";
            
            try {
                JsonNode jsonNode = objectMapper.readTree(jsonResult);
                
                // 提取主类别和名称
                if (jsonNode.has("category")) {
                    category = jsonNode.get("category").asText();
                }
                if (jsonNode.has("name")) {
                    name = jsonNode.get("name").asText();
                }
                
                // 提取置信度
                if (jsonNode.has("confidence")) {
                    double conf = jsonNode.get("confidence").asDouble();
                    confidence = new BigDecimal(String.valueOf(conf));
                }
                
                // 提取标签
                if (jsonNode.has("attributes")) {
                    JsonNode attributesNode = jsonNode.get("attributes");
                    if (attributesNode.isArray()) {
                        List<String> tagList = new ArrayList<>();
                        attributesNode.forEach(node -> tagList.add(node.asText()));
                        tags = String.join(",", tagList);
                    }
                }
                
                // 提取描述信息
                StringBuilder desc = new StringBuilder();
                if (jsonNode.has("color")) {
                    desc.append("颜色: ").append(jsonNode.get("color").asText()).append("; ");
                }
                if (jsonNode.has("shape")) {
                    desc.append("形状: ").append(jsonNode.get("shape").asText()).append("; ");
                }
                if (jsonNode.has("material")) {
                    desc.append("材质: ").append(jsonNode.get("material").asText()).append("; ");
                }
                
                // 详细识别模式下的额外信息
                if (isDetailed) {
                    if (jsonNode.has("background")) {
                        desc.append("背景: ").append(jsonNode.get("background").asText()).append("; ");
                    }
                    if (jsonNode.has("environment")) {
                        desc.append("环境: ").append(jsonNode.get("environment").asText()).append("; ");
                    }
                    if (jsonNode.has("lighting")) {
                        desc.append("光线: ").append(jsonNode.get("lighting").asText()).append("; ");
                    }
                }
                
                description = desc.toString();
                
            } catch (Exception e) {
                log.error("解析AI返回的JSON失败", e);
                // 如果解析失败，使用默认值，但保留原始JSON
            }
            
            long processingTime = System.currentTimeMillis() - startTime;

            // 更新识别结果
            result.setResultJson(jsonResult);
            result.setMainCategory(name); // 使用识别出的具体名称作为主类别
            result.setConfidence(confidence);
            result.setTags(tags);
            result.setDescription(description);
            result.setStatus(RecognitionStatus.SUCCESS.getValue());
            result.setProcessingTime((int) processingTime);

            recognitionResultRepository.updateById(result);

            log.info("图像识别成功: userId={}, resultId={}, category={}, name={}, confidence={}, time={}ms", 
                    userId, result.getId(), category, name, confidence, processingTime);

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
     * 获取识别统计数据
     */
    public RecognitionResponse.RecognitionStats getRecognitionStats(Long userId) {
        log.info("获取识别统计数据: userId={}", userId);

        // 获取总识别次数
        Long total = recognitionResultRepository.countByUserId(userId);

        // 获取本月识别次数
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        Long thisMonth = recognitionResultRepository.countByUserIdAndCreatedAtAfter(userId, startOfMonth);

        // 计算平均置信度
        Double averageConfidence = recognitionResultRepository.getAverageConfidenceByUserId(userId);
        if (averageConfidence == null) {
            averageConfidence = 0.0;
        } else {
            // 转换为百分比
            averageConfidence = averageConfidence * 100;
        }

        // 收藏数量（暂时返回0，等待收藏功能实现）
        Long favorites = 0L;

        return RecognitionResponse.RecognitionStats.builder()
                .total(total)
                .thisMonth(thisMonth)
                .averageConfidence(averageConfidence)
                .favorites(favorites)
                .build();
    }

    /**
     * 获取相关识别记录（同分类）
     */
    public List<RecognitionResponse.RecognitionInfo> getRelatedRecognitions(Long userId, Long id) {
        log.info("获取相关识别记录: userId={}, id={}", userId, id);

        // 获取当前识别记录
        RecognitionResult current = recognitionResultRepository.selectById(id);
        if (current == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "识别记录不存在");
        }

        // 验证权限
        if (!current.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权访问此识别记录");
        }

        // 获取同分类的识别记录（排除当前记录，最多3条）
        List<RecognitionResult> related = recognitionResultRepository.findByUserIdAndMainCategoryAndIdNotOrderByCreatedAtDesc(
                userId, current.getMainCategory(), id
        );

        // 限制返回数量为3条
        return related.stream()
                .limit(3)
                .map(this::convertToRecognitionInfo)
                .collect(Collectors.toList());
    }

    /**
     * 分享识别结果到知识库
     */
    @Transactional
    public Long shareToKnowledge(Long userId, Long recognitionId) {
        log.info("分享识别结果到知识库: userId={}, recognitionId={}", userId, recognitionId);

        // 获取识别记录
        RecognitionResult recognition = recognitionResultRepository.selectById(recognitionId);
        if (recognition == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "识别记录不存在");
        }

        // 验证权限
        if (!recognition.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权分享此识别记录");
        }

        // 检查识别状态
        if (recognition.getStatus() != RecognitionStatus.SUCCESS.getValue()) {
            throw new BusinessException(ErrorCode.INVALID_PARAM, "只能分享识别成功的记录");
        }

        // 创建知识条目
        com.pengcunfu.recognition.entity.Knowledge knowledge = com.pengcunfu.recognition.entity.Knowledge.builder()
                .category(recognition.getMainCategory() != null ? recognition.getMainCategory() : "其他")
                .title(recognition.getMainCategory() != null ? recognition.getMainCategory() : "未知")
                .content(recognition.getDescription() != null ? recognition.getDescription() : "")
                .coverImage(recognition.getImageUrl())
                .tags(recognition.getTags())
                .authorId(userId)
                .viewCount(0)
                .likeCount(0)
                .collectCount(0)
                .commentCount(0)
                .status(com.pengcunfu.recognition.enums.KnowledgeStatus.PENDING.getValue()) // 待审核
                .build();

        // 保存到知识库
        knowledgeRepository.insert(knowledge);

        log.info("识别结果已分享到知识库: userId={}, recognitionId={}, knowledgeId={}", 
                userId, recognitionId, knowledge.getId());

        return knowledge.getId();
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
                .imageSize(result.getImageSize())
                .imageWidth(result.getImageWidth())
                .imageHeight(result.getImageHeight())
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
