package com.pcf.recognition.controller;

import com.pcf.recognition.dto.*;
import com.pcf.recognition.dto.AuthResponses.*;
import com.pcf.recognition.dto.KnowledgeDto.*;
import com.pcf.recognition.service.KnowledgeService;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 知识库控制器
 * 提供知识库浏览、搜索等功能
 */

@RestController
@RequestMapping("/api/v1/knowledge")
@Slf4j
@RequiredArgsConstructor
public class KnowledgeController {

    private final KnowledgeService knowledgeService;


    @GetMapping("/categories")
    // 公开接口，无需权限验证
    public ApiResponse<List<KnowledgeCategoryDto>> getKnowledgeCategories() {

        log.info("获取知识分类请求");

        List<KnowledgeCategoryDto> result = knowledgeService.getKnowledgeCategories();

        return ApiResponse.success(result, "获取分类成功");
    }


    @GetMapping("/items")
    // 公开接口，无需权限验证
    public ApiResponse<KnowledgePageDto> getKnowledgeItems(
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String keyword) {

        log.info("获取知识条目列表请求: category={}, page={}, size={}, keyword={}", category, page, size, keyword);

        KnowledgePageDto result = knowledgeService.getKnowledgeItems(category, page, size, keyword);

        return ApiResponse.success(result, "获取知识条目成功");
    }


    @GetMapping("/items/{id}")
    // 公开接口，无需权限验证
    public ApiResponse<KnowledgeItemDto> getKnowledgeDetail(
            @PathVariable String id) {

        log.info("获取知识条目详情请求: id={}", id);

        KnowledgeItemDto result = knowledgeService.getKnowledgeDetail(id);

        if (result != null) {
            return ApiResponse.success(result, "获取详情成功");
        } else {
            return ApiResponse.error("知识条目不存在或已下架");
        }
    }


    @GetMapping("/search")
    // 公开接口，无需权限验证
    public ApiResponse<KnowledgePageDto> searchKnowledge(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {

        log.info("搜索知识条目请求: keyword={}, page={}, size={}", keyword, page, size);

        KnowledgePageDto result = knowledgeService.searchKnowledge(keyword, page, size);

        return ApiResponse.success(result, "搜索成功");
    }


    @GetMapping("/popular")
    // 公开接口，无需权限验证
    public ApiResponse<List<KnowledgeItemDto>> getPopularKnowledge(
            @RequestParam(defaultValue = "10") int limit) {

        log.info("获取热门知识请求: limit={}", limit);

        List<KnowledgeItemDto> result = knowledgeService.getPopularKnowledge(limit);

        return ApiResponse.success(result, "获取热门知识成功");
    }


    @GetMapping("/stats")
    // 公开接口，无需权限验证
    public ApiResponse<KnowledgeStatsDto> getKnowledgeStats() {

        log.info("获取知识统计请求");

        KnowledgeStatsDto result = knowledgeService.getKnowledgeStats();

        return ApiResponse.success(result, "获取统计信息成功");
    }


    @GetMapping("/advanced-search")
    // 公开接口，无需权限验证
    public ApiResponse<KnowledgeSearchResultDto> advancedSearchKnowledge(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String category) {
        try {
            log.info("高级搜索知识库: keyword={}, page={}, size={}, category={}", keyword, page, size, category);

            KnowledgeSearchResultDto result = knowledgeService.searchKnowledge(keyword, page, size, category);

            return ApiResponse.success(result, "搜索完成");
        } catch (Exception e) {
            log.error("知识库搜索失败", e);
            return ApiResponse.error("搜索失败");
        }
    }


    @GetMapping("/latest")
    // 公开接口，无需权限验证
    public ApiResponse<List<KnowledgeItemDto>> getLatestKnowledge(
            @RequestParam(defaultValue = "10") Integer limit) {
        try {
            log.info("获取最新知识: limit={}", limit);

            List<KnowledgeItemDto> result = knowledgeService.getLatestKnowledge(limit);

            return ApiResponse.success(result, "获取最新知识成功");
        } catch (Exception e) {
            log.error("获取最新知识失败", e);
            return ApiResponse.error("获取最新知识失败");
        }
    }


    @PostMapping("/{itemId}/like")
    @PreAuthorize("hasAnyRole('USER', 'VIP', 'ADMIN')")
    public ApiResponse<Void> likeKnowledgeItem(@PathVariable Long itemId) {
        try {
            log.info("点赞知识内容: itemId={}", itemId);

            OperationResultDto result = knowledgeService.likeKnowledgeItem(itemId);

            if (result.getSuccess()) {
                return ApiResponse.success(null, result.getMessage());
            } else {
                return ApiResponse.error(result.getMessage());
            }
        } catch (Exception e) {
            log.error("点赞知识内容失败", e);
            return ApiResponse.error("点赞失败");
        }
    }


    @DeleteMapping("/{itemId}/like")
    @PreAuthorize("hasAnyRole('USER', 'VIP', 'ADMIN')")
    public ApiResponse<Void> unlikeKnowledgeItem(@PathVariable Long itemId) {
        try {
            log.info("取消点赞知识内容: itemId={}", itemId);

            OperationResultDto result = knowledgeService.unlikeKnowledgeItem(itemId);

            if (result.getSuccess()) {
                return ApiResponse.success(null, result.getMessage());
            } else {
                return ApiResponse.error(result.getMessage());
            }
        } catch (Exception e) {
            log.error("取消点赞知识内容失败", e);
            return ApiResponse.error("取消点赞失败");
        }
    }
}