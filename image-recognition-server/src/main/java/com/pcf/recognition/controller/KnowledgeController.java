package com.pcf.recognition.controller;

import com.pcf.recognition.dto.*;
import com.pcf.recognition.dto.AuthDto.*;
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
    public ApiResponse<List<KnowledgeCategoryDto>> getKnowledgeCategories(
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String keyword) {

        log.info("获取知识分类请求: status={}, keyword={}", status, keyword);

        List<KnowledgeCategoryDto> result = knowledgeService.getKnowledgeCategories(status, keyword);

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

    // ==================== 分类管理接口 ====================

    @PostMapping("/categories")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<KnowledgeCreateResponseDto> createCategory(@RequestBody KnowledgeCategoryCreateRequest request) {
        try {
            log.info("创建知识分类: name={}, key={}", request.getName(), request.getKey());

            KnowledgeCreateResponseDto result = knowledgeService.createCategory(
                    request.getName(), 
                    request.getKey(), 
                    request.getDescription(), 
                    request.getImage()
            );

            if (result.getSuccess()) {
                return ApiResponse.success(result, result.getMessage());
            } else {
                return ApiResponse.error(result.getMessage());
            }
        } catch (Exception e) {
            log.error("创建知识分类失败", e);
            return ApiResponse.error("创建分类失败");
        }
    }

    @PutMapping("/categories/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<KnowledgeCreateResponseDto> updateCategory(
            @PathVariable Long id, 
            @RequestBody CategoryUpdateRequest request) {
        try {
            log.info("更新知识分类: id={}, name={}, status={}", id, request.getName(), request.getStatus());

            boolean success = knowledgeService.updateCategory(
                id, 
                request.getName(), 
                request.getDescription(), 
                request.getImage(), 
                request.getSortOrder(), 
                request.getStatus()
            );

            if (success) {
                KnowledgeCreateResponseDto result = KnowledgeCreateResponseDto.builder()
                        .success(true)
                        .message("分类更新成功")
                        .id(id)
                        .build();
                return ApiResponse.success(result, "分类更新成功");
            } else {
                return ApiResponse.error("分类不存在或更新失败");
            }
        } catch (Exception e) {
            log.error("更新知识分类失败", e);
            return ApiResponse.error("更新分类失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/categories/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> deleteCategory(@PathVariable Long id) {
        try {
            log.info("删除知识分类: id={}", id);

            boolean success = knowledgeService.deleteCategory(id);
            
            if (success) {
                return ApiResponse.success(null, "分类删除成功");
            } else {
                return ApiResponse.error("分类不存在");
            }
        } catch (Exception e) {
            log.error("删除知识分类失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/categories/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<KnowledgeCategoryDto> getCategoryDetail(@PathVariable Long id) {
        try {
            log.info("获取知识分类详情: id={}", id);

            // 这里应该调用service的获取详情方法，暂时返回空
            return ApiResponse.error("获取分类详情功能开发中");
        } catch (Exception e) {
            log.error("获取知识分类详情失败", e);
            return ApiResponse.error("获取分类详情失败");
        }
    }
}