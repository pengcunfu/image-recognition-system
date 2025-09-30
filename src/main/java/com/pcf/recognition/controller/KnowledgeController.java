package com.pcf.recognition.controller;

import com.pcf.recognition.dto.ApiResponse;
import com.pcf.recognition.service.KnowledgeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public ApiResponse<List<Map<String, Object>>> getKnowledgeCategories() {
        
        log.info("获取知识分类请求");
        
        Map<String, Object> result = knowledgeService.getKnowledgeCategories();
        
        if ((Boolean) result.get("success")) {
            return ApiResponse.success((List<Map<String, Object>>) result.get("data"), "获取分类成功");
        } else {
            return ApiResponse.error((String) result.get("message"));
        }
    }

    @Operation(summary = "获取知识条目列表", description = "获取指定分类的知识条目列表")
    @GetMapping("/items")
    public ApiResponse<Map<String, Object>> getKnowledgeItems(
            @Parameter(description = "分类") @RequestParam(required = false) String category,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "关键词搜索") @RequestParam(required = false) String keyword) {
        
        log.info("获取知识条目列表请求: category={}, page={}, size={}, keyword={}", category, page, size, keyword);
        
        Map<String, Object> result = knowledgeService.getKnowledgeItems(category, page, size, keyword);
        
        if ((Boolean) result.get("success")) {
            return ApiResponse.success(result, "获取知识条目成功");
        } else {
            return ApiResponse.error((String) result.get("message"));
        }
    }

    @Operation(summary = "获取知识条目详情", description = "获取单个知识条目的详细信息")
    @GetMapping("/items/{id}")
    public ApiResponse<Map<String, Object>> getKnowledgeDetail(
            @Parameter(description = "知识条目ID") @PathVariable String id) {
        
        log.info("获取知识条目详情请求: id={}", id);
        
        Map<String, Object> result = knowledgeService.getKnowledgeDetail(id);
        
        if ((Boolean) result.get("success")) {
            return ApiResponse.success((Map<String, Object>) result.get("data"), "获取详情成功");
        } else {
            return ApiResponse.error((String) result.get("message"));
        }
    }

    @Operation(summary = "搜索知识条目", description = "根据关键词搜索知识条目")
    @GetMapping("/search")
    public ApiResponse<Map<String, Object>> searchKnowledge(
            @Parameter(description = "搜索关键词") @RequestParam String keyword,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size) {
        
        log.info("搜索知识条目请求: keyword={}, page={}, size={}", keyword, page, size);
        
        Map<String, Object> result = knowledgeService.searchKnowledge(keyword, page, size);
        
        if ((Boolean) result.get("success")) {
            return ApiResponse.success(result, "搜索成功");
        } else {
            return ApiResponse.error((String) result.get("message"));
        }
    }

    @Operation(summary = "获取热门知识", description = "获取最受欢迎的知识条目")
    @GetMapping("/popular")
    public ApiResponse<List<Map<String, Object>>> getPopularKnowledge(
            @Parameter(description = "返回数量限制") @RequestParam(defaultValue = "10") int limit) {
        
        log.info("获取热门知识请求: limit={}", limit);
        
        Map<String, Object> result = knowledgeService.getPopularKnowledge(limit);
        
        if ((Boolean) result.get("success")) {
            return ApiResponse.success((List<Map<String, Object>>) result.get("data"), "获取热门知识成功");
        } else {
            return ApiResponse.error((String) result.get("message"));
        }
    }

    @Operation(summary = "获取知识统计", description = "获取知识库的统计信息")
    @GetMapping("/stats")
    public ApiResponse<Map<String, Object>> getKnowledgeStats() {
        
        log.info("获取知识统计请求");
        
        Map<String, Object> result = knowledgeService.getKnowledgeStats();
        
        if ((Boolean) result.get("success")) {
            return ApiResponse.success((Map<String, Object>) result.get("stats"), "获取统计信息成功");
        } else {
            return ApiResponse.error((String) result.get("message"));
        }
    }
}