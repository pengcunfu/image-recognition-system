package com.pengcunfu.recognition.controller.admin;

import com.pengcunfu.recognition.annotation.Role;
import com.pengcunfu.recognition.request.KnowledgeRequest;
import com.pengcunfu.recognition.response.ApiResponse;
import com.pengcunfu.recognition.response.KnowledgeResponse;
import com.pengcunfu.recognition.response.PageResponse;
import com.pengcunfu.recognition.service.KnowledgeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 管理后台 - 知识库管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/knowledge")
@RequiredArgsConstructor
@Role("ADMIN")
public class AdminKnowledgeController {

    private final KnowledgeService knowledgeService;

    /**
     * 获取知识列表（管理员）
     */
    @GetMapping
    public ApiResponse<PageResponse<KnowledgeResponse.KnowledgeInfo>> getKnowledgeAdmin(
            KnowledgeRequest.QueryKnowledgeRequest request) {
        log.info("管理员获取知识列表: page={}, size={}, status={}, category={}, tag={}, keyword={}", 
                request.getPage(), request.getSize(), request.getStatus(), 
                request.getCategory(), request.getTag(), request.getKeyword());
        PageResponse<KnowledgeResponse.KnowledgeInfo> response = 
            knowledgeService.getKnowledgeAdmin(
                request.getPage(), 
                request.getSize(), 
                request.getStatus(),
                request.getCategory(),
                request.getTag(),
                request.getKeyword()
            );
        return ApiResponse.success(response);
    }

    /**
     * 获取所有分类
     */
    @GetMapping("/categories")
    public ApiResponse<java.util.List<String>> getCategories() {
        log.info("获取所有知识分类");
        java.util.List<String> categories = knowledgeService.getAllCategories();
        return ApiResponse.success(categories);
    }

    /**
     * 获取所有标签
     */
    @GetMapping("/tags")
    public ApiResponse<java.util.List<String>> getTags() {
        log.info("获取所有知识标签");
        java.util.List<String> tags = knowledgeService.getAllTags();
        return ApiResponse.success(tags);
    }

    /**
     * 创建知识条目
     */
    @PostMapping
    public ApiResponse<Long> createKnowledge(
            @Valid @RequestBody KnowledgeRequest.CreateKnowledgeRequest request) {
        log.info("创建知识条目: title={}", request.getTitle());
        Long knowledgeId = knowledgeService.createKnowledge(request);
        return ApiResponse.success(knowledgeId);
    }

    /**
     * 更新知识条目
     */
    @PutMapping("/{knowledgeId}")
    public ApiResponse<Void> updateKnowledge(
            @PathVariable Long knowledgeId,
            @Valid @RequestBody KnowledgeRequest.UpdateKnowledgeRequest request) {
        log.info("更新知识条目: knowledgeId={}", knowledgeId);
        knowledgeService.updateKnowledge(knowledgeId, request);
        return ApiResponse.success();
    }

    /**
     * 审核通过知识条目
     */
    @PutMapping("/{knowledgeId}/approve")
    public ApiResponse<Void> approveKnowledge(@PathVariable Long knowledgeId) {
        log.info("审核通过知识条目: knowledgeId={}", knowledgeId);
        knowledgeService.approveKnowledge(knowledgeId);
        return ApiResponse.success();
    }

    /**
     * 审核拒绝知识条目
     */
    @PutMapping("/{knowledgeId}/reject")
    public ApiResponse<Void> rejectKnowledge(@PathVariable Long knowledgeId) {
        log.info("审核拒绝知识条目: knowledgeId={}", knowledgeId);
        knowledgeService.rejectKnowledge(knowledgeId);
        return ApiResponse.success();
    }

    /**
     * 置顶/取消置顶知识条目
     */
    @PutMapping("/{knowledgeId}/top")
    public ApiResponse<Void> toggleKnowledgeTop(
            @PathVariable Long knowledgeId,
            @RequestParam Integer isTop) {
        log.info("切换知识置顶状态: knowledgeId={}, isTop={}", knowledgeId, isTop);
        knowledgeService.toggleTop(knowledgeId, isTop);
        return ApiResponse.success();
    }

    /**
     * 推荐/取消推荐知识条目
     */
    @PutMapping("/{knowledgeId}/featured")
    public ApiResponse<Void> toggleKnowledgeFeatured(
            @PathVariable Long knowledgeId,
            @RequestParam Integer isFeatured) {
        log.info("切换知识推荐状态: knowledgeId={}, isFeatured={}", knowledgeId, isFeatured);
        knowledgeService.toggleFeatured(knowledgeId, isFeatured);
        return ApiResponse.success();
    }

    /**
     * 删除知识条目
     */
    @DeleteMapping("/{knowledgeId}")
    public ApiResponse<Void> deleteKnowledge(@PathVariable Long knowledgeId) {
        log.info("删除知识条目: knowledgeId={}", knowledgeId);
        knowledgeService.deleteKnowledge(knowledgeId);
        return ApiResponse.success();
    }
}

