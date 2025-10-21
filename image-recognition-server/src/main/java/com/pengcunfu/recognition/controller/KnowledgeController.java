package com.pengcunfu.recognition.controller;

import com.pengcunfu.recognition.annotation.Role;
import com.pengcunfu.recognition.request.KnowledgeRequest;
import com.pengcunfu.recognition.response.ApiResponse;
import com.pengcunfu.recognition.response.KnowledgeResponse;
import com.pengcunfu.recognition.response.PageResponse;
import com.pengcunfu.recognition.security.SecurityContextHolder;
import com.pengcunfu.recognition.service.KnowledgeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 知识库控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/knowledge")
@RequiredArgsConstructor
public class KnowledgeController {

    private final KnowledgeService knowledgeService;

    /**
     * 获取知识列表
     */
    @GetMapping
    public ApiResponse<PageResponse<KnowledgeResponse.KnowledgeInfo>> getKnowledgeList(
            KnowledgeRequest.QueryKnowledgeRequest request) {
        log.info("获取知识列表: page={}, size={}, category={}", 
            request.getPage(), request.getSize(), request.getCategory());
        PageResponse<KnowledgeResponse.KnowledgeInfo> response = 
            knowledgeService.getKnowledgeList(
                request.getPage(), 
                request.getSize(), 
                request.getCategory(), 
                request.getKeyword()
            );
        return ApiResponse.success(response);
    }

    /**
     * 获取知识详情
     */
    @GetMapping("/{id}")
    public ApiResponse<KnowledgeResponse.KnowledgeInfo> getKnowledgeDetail(@PathVariable Long id) {
        log.info("获取知识详情: id={}", id);
        KnowledgeResponse.KnowledgeInfo knowledge = knowledgeService.getKnowledgeDetail(id);
        return ApiResponse.success(knowledge);
    }

    /**
     * 点赞知识
     */
    @Role("USER")
    @PostMapping("/{id}/like")
    public ApiResponse<Void> likeKnowledge(@PathVariable Long id) {
        Long userId = SecurityContextHolder.getCurrentUserId();
        log.info("点赞知识: userId={}, knowledgeId={}", userId, id);
        knowledgeService.likeKnowledge(userId, id);
        return ApiResponse.success();
    }

    /**
     * 取消点赞知识
     */
    @Role("USER")
    @DeleteMapping("/{id}/like")
    public ApiResponse<Void> unlikeKnowledge(@PathVariable Long id) {
        Long userId = SecurityContextHolder.getCurrentUserId();
        log.info("取消点赞知识: userId={}, knowledgeId={}", userId, id);
        knowledgeService.unlikeKnowledge(userId, id);
        return ApiResponse.success();
    }

    /**
     * 收藏知识
     */
    @Role("USER")
    @PostMapping("/{id}/collect")
    public ApiResponse<Void> collectKnowledge(@PathVariable Long id) {
        Long userId = SecurityContextHolder.getCurrentUserId();
        log.info("收藏知识: userId={}, knowledgeId={}", userId, id);
        knowledgeService.collectKnowledge(userId, id);
        return ApiResponse.success();
    }

    /**
     * 取消收藏知识
     */
    @Role("USER")
    @DeleteMapping("/{id}/collect")
    public ApiResponse<Void> uncollectKnowledge(@PathVariable Long id) {
        Long userId = SecurityContextHolder.getCurrentUserId();
        log.info("取消收藏知识: userId={}, knowledgeId={}", userId, id);
        knowledgeService.uncollectKnowledge(userId, id);
        return ApiResponse.success();
    }

    /**
     * 获取所有分类
     */
    @GetMapping("/categories")
    public ApiResponse<java.util.List<String>> getAllCategories() {
        log.info("获取所有分类");
        java.util.List<String> categories = knowledgeService.getAllCategories();
        return ApiResponse.success(categories);
    }

    /**
     * 获取所有标签
     */
    @GetMapping("/tags")
    public ApiResponse<java.util.List<String>> getAllTags() {
        log.info("获取所有标签");
        java.util.List<String> tags = knowledgeService.getAllTags();
        return ApiResponse.success(tags);
    }
}

