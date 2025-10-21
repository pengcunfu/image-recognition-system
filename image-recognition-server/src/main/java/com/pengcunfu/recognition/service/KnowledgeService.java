package com.pengcunfu.recognition.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pengcunfu.recognition.constant.ErrorCode;
import com.pengcunfu.recognition.entity.Knowledge;
import com.pengcunfu.recognition.entity.UserLike;
import com.pengcunfu.recognition.entity.UserCollect;
import com.pengcunfu.recognition.enums.KnowledgeStatus;
import com.pengcunfu.recognition.enums.TargetType;
import com.pengcunfu.recognition.exception.BusinessException;
import com.pengcunfu.recognition.repository.KnowledgeRepository;
import com.pengcunfu.recognition.repository.UserLikeRepository;
import com.pengcunfu.recognition.repository.UserCollectRepository;
import com.pengcunfu.recognition.request.KnowledgeRequest;
import com.pengcunfu.recognition.response.KnowledgeResponse;
import com.pengcunfu.recognition.response.PageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

/**
 * 知识库服务
 * 处理知识条目相关业务逻辑
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KnowledgeService {

    private final KnowledgeRepository knowledgeRepository;
    private final UserLikeRepository userLikeRepository;
    private final UserCollectRepository userCollectRepository;

    /**
     * 获取知识列表
     */
    public PageResponse<KnowledgeResponse.KnowledgeInfo> getKnowledgeList(
            Integer page, Integer size, String category, String keyword) {
        log.info("获取知识列表: page={}, size={}, category={}, keyword={}", page, size, category, keyword);

        Page<Knowledge> pageRequest = new Page<>(page, size);

        LambdaQueryWrapper<Knowledge> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Knowledge::getStatus, KnowledgeStatus.PUBLISHED.getValue());

        if (category != null && !category.isEmpty()) {
            queryWrapper.eq(Knowledge::getCategory, category);
        }

        if (keyword != null && !keyword.isEmpty()) {
            queryWrapper.and(wrapper -> wrapper
                    .like(Knowledge::getTitle, keyword)
                    .or()
                    .like(Knowledge::getContent, keyword)
                    .or()
                    .like(Knowledge::getTags, keyword));
        }

        queryWrapper.orderByDesc(Knowledge::getCreatedAt);

        Page<Knowledge> pageResult = knowledgeRepository.selectPage(pageRequest, queryWrapper);

        return PageResponse.<KnowledgeResponse.KnowledgeInfo>builder()
                .data(pageResult.getRecords().stream()
                        .map(this::convertToKnowledgeInfo)
                        .collect(Collectors.toList()))
                .total(pageResult.getTotal())
                .page((int) pageResult.getCurrent())
                .size((int) pageResult.getSize())
                .pages((int) pageResult.getPages())
                .build();
    }

    /**
     * 获取知识列表（管理员，使用 SQL 查询）
     */
    public PageResponse<KnowledgeResponse.KnowledgeInfo> getKnowledgeAdmin(
            Integer page, Integer size, Integer status, String category, String tag, String keyword) {
        log.info("管理员获取知识列表: page={}, size={}, status={}, category={}, tag={}, keyword={}", 
                page, size, status, category, tag, keyword);

        Page<Knowledge> pageRequest = new Page<>(page, size);
        Page<Knowledge> pageResult = knowledgeRepository.findKnowledgeForAdmin(
                pageRequest, status, category, tag, keyword);

        return PageResponse.<KnowledgeResponse.KnowledgeInfo>builder()
                .data(pageResult.getRecords().stream()
                        .map(this::convertToKnowledgeInfo)
                        .collect(Collectors.toList()))
                .total(pageResult.getTotal())
                .page((int) pageResult.getCurrent())
                .size((int) pageResult.getSize())
                .pages((int) pageResult.getPages())
                .build();
    }

    /**
     * 获取所有分类
     */
    public java.util.List<String> getAllCategories() {
        log.info("获取所有知识分类");
        return knowledgeRepository.findAllCategories();
    }

    /**
     * 获取所有标签
     */
    public java.util.List<String> getAllTags() {
        log.info("获取所有知识标签");
        
        // 获取所有标签原始数据
        java.util.List<String> rawTags = knowledgeRepository.findAllTagsRaw();
        
        // 处理标签：拆分逗号分隔的标签，去重
        java.util.Set<String> tagSet = new java.util.HashSet<>();
        for (String tags : rawTags) {
            if (tags != null && !tags.isEmpty()) {
                String[] tagArray = tags.split(",");
                for (String tag : tagArray) {
                    String trimmedTag = tag.trim();
                    if (!trimmedTag.isEmpty()) {
                        tagSet.add(trimmedTag);
                    }
                }
            }
        }
        
        // 转换为列表并排序
        java.util.List<String> tagList = new java.util.ArrayList<>(tagSet);
        java.util.Collections.sort(tagList);
        
        return tagList;
    }

    /**
     * 获取知识详情
     */
    @Transactional
    public KnowledgeResponse.KnowledgeInfo getKnowledgeDetail(Long knowledgeId) {
        log.info("获取知识详情: knowledgeId={}", knowledgeId);

        Knowledge knowledge = knowledgeRepository.selectById(knowledgeId);

        if (knowledge == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "知识条目不存在");
        }

        // 增加浏览量
        knowledge.setViewCount(knowledge.getViewCount() + 1);
        knowledgeRepository.updateById(knowledge);

        return convertToKnowledgeInfo(knowledge);
    }

    /**
     * 创建知识条目（使用 Builder 模式）
     */
    @Transactional
    public Long createKnowledge(KnowledgeRequest.CreateKnowledgeRequest request) {
        log.info("创建知识条目: title={}", request.getTitle());

        Knowledge knowledge = Knowledge.builder()
                .title(request.getTitle())
                .category(request.getCategory())
                .content(request.getContent())
                .coverImage(request.getCoverImage())
                .images(request.getImages())
                .tags(request.getTags())
                .status(KnowledgeStatus.PUBLISHED.getValue())
                .viewCount(0)
                .likeCount(0)
                .collectCount(0)
                .commentCount(0)
                .build();

        knowledgeRepository.insert(knowledge);

        log.info("知识条目创建成功: id={}", knowledge.getId());
        return knowledge.getId();
    }

    /**
     * 更新知识条目
     */
    @Transactional
    public void updateKnowledge(Long knowledgeId, KnowledgeRequest.UpdateKnowledgeRequest request) {
        log.info("更新知识条目: knowledgeId={}", knowledgeId);

        Knowledge knowledge = knowledgeRepository.selectById(knowledgeId);

        if (knowledge == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "知识条目不存在");
        }

        if (request.getTitle() != null) {
            knowledge.setTitle(request.getTitle());
        }
        if (request.getCategory() != null) {
            knowledge.setCategory(request.getCategory());
        }
        if (request.getContent() != null) {
            knowledge.setContent(request.getContent());
        }
        if (request.getCoverImage() != null) {
            knowledge.setCoverImage(request.getCoverImage());
        }
        if (request.getImages() != null) {
            knowledge.setImages(request.getImages());
        }
        if (request.getTags() != null) {
            knowledge.setTags(request.getTags());
        }

        knowledgeRepository.updateById(knowledge);

        log.info("知识条目更新成功: knowledgeId={}", knowledgeId);
    }

    /**
     * 删除知识条目
     */
    @Transactional
    public void deleteKnowledge(Long knowledgeId) {
        log.info("删除知识条目: knowledgeId={}", knowledgeId);

        Knowledge knowledge = knowledgeRepository.selectById(knowledgeId);

        if (knowledge == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "知识条目不存在");
        }

        knowledgeRepository.deleteById(knowledgeId);

        log.info("知识条目删除成功: knowledgeId={}", knowledgeId);
    }

    /**
     * 点赞知识
     */
    @Transactional
    public void likeKnowledge(Long userId, Long knowledgeId) {
        log.info("点赞知识: userId={}, knowledgeId={}", userId, knowledgeId);

        Knowledge knowledge = knowledgeRepository.selectById(knowledgeId);

        if (knowledge == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "知识条目不存在");
        }

        // 检查是否已点赞
        UserLike existingLike = userLikeRepository.selectOne(
                new LambdaQueryWrapper<UserLike>()
                        .eq(UserLike::getUserId, userId)
                        .eq(UserLike::getTargetId, knowledgeId)
                        .eq(UserLike::getTargetType, TargetType.KNOWLEDGE.getValue())
        );

        if (existingLike != null) {
            throw new BusinessException(ErrorCode.INVALID_PARAM, "已点赞过该知识");
        }

        // 创建点赞记录
        UserLike like = new UserLike();
        like.setUserId(userId);
        like.setTargetId(knowledgeId);
        like.setTargetType(TargetType.KNOWLEDGE.getValue());

        userLikeRepository.insert(like);

        // 更新知识点赞数
        knowledge.setLikeCount(knowledge.getLikeCount() + 1);
        knowledgeRepository.updateById(knowledge);

        log.info("点赞成功: userId={}, knowledgeId={}", userId, knowledgeId);
    }

    /**
     * 取消点赞
     */
    @Transactional
    public void unlikeKnowledge(Long userId, Long knowledgeId) {
        log.info("取消点赞: userId={}, knowledgeId={}", userId, knowledgeId);

        UserLike like = userLikeRepository.selectOne(
                new LambdaQueryWrapper<UserLike>()
                        .eq(UserLike::getUserId, userId)
                        .eq(UserLike::getTargetId, knowledgeId)
                        .eq(UserLike::getTargetType, TargetType.KNOWLEDGE.getValue())
        );

        if (like == null) {
            throw new BusinessException(ErrorCode.INVALID_PARAM, "未点赞该知识");
        }

        userLikeRepository.deleteById(like.getId());

        // 更新知识点赞数
        Knowledge knowledge = knowledgeRepository.selectById(knowledgeId);
        if (knowledge != null && knowledge.getLikeCount() > 0) {
            knowledge.setLikeCount(knowledge.getLikeCount() - 1);
            knowledgeRepository.updateById(knowledge);
        }

        log.info("取消点赞成功: userId={}, knowledgeId={}", userId, knowledgeId);
    }

    /**
     * 收藏知识
     */
    @Transactional
    public void collectKnowledge(Long userId, Long knowledgeId) {
        log.info("收藏知识: userId={}, knowledgeId={}", userId, knowledgeId);

        Knowledge knowledge = knowledgeRepository.selectById(knowledgeId);

        if (knowledge == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "知识条目不存在");
        }

        // 检查是否已收藏
        UserCollect existingCollect = userCollectRepository.selectOne(
                new LambdaQueryWrapper<UserCollect>()
                        .eq(UserCollect::getUserId, userId)
                        .eq(UserCollect::getTargetId, knowledgeId)
                        .eq(UserCollect::getTargetType, TargetType.KNOWLEDGE.getValue())
        );

        if (existingCollect != null) {
            throw new BusinessException(ErrorCode.INVALID_PARAM, "已收藏过该知识");
        }

        // 创建收藏记录
        UserCollect collect = new UserCollect();
        collect.setUserId(userId);
        collect.setTargetId(knowledgeId);
        collect.setTargetType(TargetType.KNOWLEDGE.getValue());

        userCollectRepository.insert(collect);

        // 更新知识收藏数
        knowledge.setCollectCount(knowledge.getCollectCount() + 1);
        knowledgeRepository.updateById(knowledge);

        log.info("收藏成功: userId={}, knowledgeId={}", userId, knowledgeId);
    }

    /**
     * 取消收藏
     */
    @Transactional
    public void uncollectKnowledge(Long userId, Long knowledgeId) {
        log.info("取消收藏: userId={}, knowledgeId={}", userId, knowledgeId);

        UserCollect collect = userCollectRepository.selectOne(
                new LambdaQueryWrapper<UserCollect>()
                        .eq(UserCollect::getUserId, userId)
                        .eq(UserCollect::getTargetId, knowledgeId)
                        .eq(UserCollect::getTargetType, TargetType.KNOWLEDGE.getValue())
        );

        if (collect == null) {
            throw new BusinessException(ErrorCode.INVALID_PARAM, "未收藏该知识");
        }

        userCollectRepository.deleteById(collect.getId());

        // 更新知识收藏数
        Knowledge knowledge = knowledgeRepository.selectById(knowledgeId);
        if (knowledge != null && knowledge.getCollectCount() > 0) {
            knowledge.setCollectCount(knowledge.getCollectCount() - 1);
            knowledgeRepository.updateById(knowledge);
        }

        log.info("取消收藏成功: userId={}, knowledgeId={}", userId, knowledgeId);
    }

    /**
     * 转换为知识信息 DTO
     */
    private KnowledgeResponse.KnowledgeInfo convertToKnowledgeInfo(Knowledge knowledge) {
        return KnowledgeResponse.KnowledgeInfo.builder()
                .id(knowledge.getId())
                .title(knowledge.getTitle())
                .name(knowledge.getTitle())
                .category(knowledge.getCategory())
                .content(knowledge.getContent())
                .description(knowledge.getContent())
                .detail(knowledge.getContent())
                .coverImage(knowledge.getCoverImage())
                .imageUrl(knowledge.getCoverImage())
                .images(knowledge.getImages())
                .tags(knowledge.getTags())
                .authorId(knowledge.getAuthorId())
                .viewCount(knowledge.getViewCount())
                .likeCount(knowledge.getLikeCount())
                .collectCount(knowledge.getCollectCount())
                .commentCount(knowledge.getCommentCount())
                .status(knowledge.getStatus())
                .createdAt(knowledge.getCreatedAt())
                .updatedAt(knowledge.getUpdatedAt())
                    .build();
        }
    }

