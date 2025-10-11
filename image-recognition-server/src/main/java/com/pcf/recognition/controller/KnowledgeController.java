package com.pcf.recognition.controller;

import com.pcf.recognition.dto.*;
import com.pcf.recognition.service.KnowledgeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 知识库控制器
 * 提供知识库浏览、搜索等功能
 */
@Tag(name = "知识库", description = "知识库浏览、搜索等接口")
@RestController
@RequestMapping("/api/v1/knowledge")
@Slf4j
@RequiredArgsConstructor
public class KnowledgeController {
    
    private final KnowledgeService knowledgeService;

    @Operation(summary = "获取知识分类", description = "获取知识库的分类列表")
    @GetMapping("/categories")
    // 公开接口，无需权限验证
    public ApiResponse<List<KnowledgeCategoryDto>> getKnowledgeCategories() {
        
        log.info("获取知识分类请求");
        
        List<KnowledgeCategoryDto> result = knowledgeService.getKnowledgeCategories();
        
        return ApiResponse.success(result, "获取分类成功");
    }

    @Operation(summary = "获取知识条目列表", description = "获取指定分类的知识条目列表")
    @GetMapping("/items")
    // 公开接口，无需权限验证
    public ApiResponse<KnowledgePageDto> getKnowledgeItems(
            @Parameter(description = "分类") @RequestParam(required = false) String category,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "关键词搜索") @RequestParam(required = false) String keyword) {
        
        log.info("获取知识条目列表请求: category={}, page={}, size={}, keyword={}", category, page, size, keyword);
        
        KnowledgePageDto result = knowledgeService.getKnowledgeItems(category, page, size, keyword);
        
        return ApiResponse.success(result, "获取知识条目成功");
    }

    @Operation(summary = "获取知识条目详情", description = "获取单个知识条目的详细信息")
    @GetMapping("/items/{id}")
    // 公开接口，无需权限验证
    public ApiResponse<KnowledgeItemDto> getKnowledgeDetail(
            @Parameter(description = "知识条目ID") @PathVariable String id) {
        
        log.info("获取知识条目详情请求: id={}", id);
        
        KnowledgeItemDto result = knowledgeService.getKnowledgeDetail(id);
        
        if (result != null) {
            return ApiResponse.success(result, "获取详情成功");
        } else {
            return ApiResponse.error("知识条目不存在或已下架");
        }
    }

    @Operation(summary = "搜索知识条目", description = "根据关键词搜索知识条目")
    @GetMapping("/search")
    // 公开接口，无需权限验证
    public ApiResponse<KnowledgePageDto> searchKnowledge(
            @Parameter(description = "搜索关键词") @RequestParam String keyword,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size) {
        
        log.info("搜索知识条目请求: keyword={}, page={}, size={}", keyword, page, size);
        
        KnowledgePageDto result = knowledgeService.searchKnowledge(keyword, page, size);
        
        return ApiResponse.success(result, "搜索成功");
    }

    @Operation(summary = "获取热门知识", description = "获取最受欢迎的知识条目")
    @GetMapping("/popular")
    // 公开接口，无需权限验证
    public ApiResponse<List<KnowledgeItemDto>> getPopularKnowledge(
            @Parameter(description = "返回数量限制") @RequestParam(defaultValue = "10") int limit) {
        
        log.info("获取热门知识请求: limit={}", limit);
        
        List<KnowledgeItemDto> result = knowledgeService.getPopularKnowledge(limit);
        
        return ApiResponse.success(result, "获取热门知识成功");
    }

    @Operation(summary = "获取知识统计", description = "获取知识库的统计信息")
    @GetMapping("/stats")
    // 公开接口，无需权限验证
    public ApiResponse<KnowledgeStatsDto> getKnowledgeStats() {
        
        log.info("获取知识统计请求");
        
        KnowledgeStatsDto result = knowledgeService.getKnowledgeStats();
        
        return ApiResponse.success(result, "获取统计信息成功");
    }
    
    @Operation(summary = "高级搜索知识库", description = "根据关键词和分类搜索知识库内容")
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
    
    @Operation(summary = "获取最新知识", description = "获取最新发布的知识内容")
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
    
    @Operation(summary = "点赞知识内容", description = "用户点赞指定的知识内容")
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
    
    @Operation(summary = "取消点赞知识内容", description = "用户取消点赞指定的知识内容")
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