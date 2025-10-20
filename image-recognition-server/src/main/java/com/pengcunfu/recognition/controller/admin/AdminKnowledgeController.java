package com.pengcunfu.recognition.controller.admin;

import com.pengcunfu.recognition.request.admin.AdminKnowledgeRequest;
import com.pengcunfu.recognition.response.ApiResponse;
import com.pengcunfu.recognition.response.KnowledgeResponse;
import com.pengcunfu.recognition.response.PageResponse;
import com.pengcunfu.recognition.service.admin.AdminKnowledgeService;
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
public class AdminKnowledgeController {

    private final AdminKnowledgeService adminKnowledgeService;

    /**
     * 获取知识列表（管理员）
     */
    @GetMapping
    public ApiResponse<PageResponse<KnowledgeResponse.KnowledgeInfo>> getKnowledgeAdmin(
            AdminKnowledgeRequest.QueryKnowledgeRequest request) {
        log.info("管理员获取知识列表: page={}, size={}, status={}, keyword={}", 
                request.getPage(), request.getSize(), request.getStatus(), request.getKeyword());
        PageResponse<KnowledgeResponse.KnowledgeInfo> response = 
            adminKnowledgeService.getKnowledgeAdmin(
                request.getPage(), 
                request.getSize(), 
                request.getStatus(), 
                request.getKeyword()
            );
        return ApiResponse.success(response);
    }

    /**
     * 创建知识条目
     */
    @PostMapping
    public ApiResponse<Long> createKnowledge(
            @Valid @RequestBody AdminKnowledgeRequest.CreateKnowledgeRequest request) {
        log.info("创建知识条目: title={}", request.getTitle());
        Long knowledgeId = adminKnowledgeService.createKnowledge(request);
        return ApiResponse.success(knowledgeId);
    }

    /**
     * 更新知识条目
     */
    @PutMapping("/{knowledgeId}")
    public ApiResponse<Void> updateKnowledge(
            @PathVariable Long knowledgeId,
            @Valid @RequestBody AdminKnowledgeRequest.UpdateKnowledgeRequest request) {
        log.info("更新知识条目: knowledgeId={}", knowledgeId);
        adminKnowledgeService.updateKnowledge(knowledgeId, request);
        return ApiResponse.success();
    }

    /**
     * 删除知识条目
     */
    @DeleteMapping("/{knowledgeId}")
    public ApiResponse<Void> deleteKnowledge(@PathVariable Long knowledgeId) {
        log.info("删除知识条目: knowledgeId={}", knowledgeId);
        adminKnowledgeService.deleteKnowledge(knowledgeId);
        return ApiResponse.success();
    }
}

