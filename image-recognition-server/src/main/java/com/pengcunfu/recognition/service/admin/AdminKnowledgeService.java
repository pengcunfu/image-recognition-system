package com.pengcunfu.recognition.service.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pengcunfu.recognition.constant.ErrorCode;
import com.pengcunfu.recognition.entity.Knowledge;
import com.pengcunfu.recognition.enums.KnowledgeStatus;
import com.pengcunfu.recognition.exception.BusinessException;
import com.pengcunfu.recognition.repository.KnowledgeRepository;
import com.pengcunfu.recognition.request.admin.AdminKnowledgeRequest;
import com.pengcunfu.recognition.response.KnowledgeResponse;
import com.pengcunfu.recognition.response.PageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

/**
 * 管理员 - 知识库管理服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminKnowledgeService {

    private final KnowledgeRepository knowledgeRepository;

    /**
     * 获取知识列表（管理员，使用 SQL 查询）
     */
    public PageResponse<KnowledgeResponse.KnowledgeInfo> getKnowledgeAdmin(
            Integer page, Integer size, Integer status, String keyword) {
        log.info("管理员获取知识列表: page={}, size={}, status={}, keyword={}", 
                page, size, status, keyword);

        Page<Knowledge> pageRequest = new Page<>(page, size);
        Page<Knowledge> pageResult = knowledgeRepository.findKnowledgeForAdmin(
                pageRequest, status, keyword);

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
     * 创建知识条目（使用 Builder 模式）
     */
    @Transactional
    public Long createKnowledge(AdminKnowledgeRequest.CreateKnowledgeRequest request) {
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
    public void updateKnowledge(Long knowledgeId, AdminKnowledgeRequest.UpdateKnowledgeRequest request) {
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
                .createTime(knowledge.getCreatedAt())
                .updateTime(knowledge.getUpdatedAt())
                .build();
    }
}

