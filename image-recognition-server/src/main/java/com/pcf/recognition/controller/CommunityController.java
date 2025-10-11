package com.pcf.recognition.controller;

import com.pcf.recognition.dto.ApiResponse;
import com.pcf.recognition.dto.CreatePostRequest;
import com.pcf.recognition.dto.AddCommentRequest;
import com.pcf.recognition.service.CommunityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.*;

/**
 * 社区控制器
 * 提供社区帖子、评论、互动等功能
 */
@Tag(name = "社区", description = "社区帖子、评论、互动等接口")
@RestController
@RequestMapping("/api/v1/community")
@Slf4j
@RequiredArgsConstructor
public class CommunityController {
    
    private final CommunityService communityService;

    @Operation(summary = "获取帖子列表", description = "分页获取社区帖子列表")
    @GetMapping("/posts")
    // 公开接口，无需权限验证
    public ApiResponse<Map<String, Object>> getPosts(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "分类筛选") @RequestParam(required = false) String category,
            @Parameter(description = "排序方式") @RequestParam(defaultValue = "latest") String sort) {
        
        log.info("获取帖子列表请求: page={}, size={}, category={}, sort={}", page, size, category, sort);
        
        Map<String, Object> result = communityService.getPosts(page, size, category, sort);
        
        if ((Boolean) result.get("success")) {
            return ApiResponse.success(result, "获取帖子列表成功");
        } else {
            return ApiResponse.error((String) result.get("message"));
        }
    }

    @Operation(summary = "获取帖子详情", description = "获取单个帖子的详细信息")
    @GetMapping("/posts/{id}")
    // 公开接口，无需权限验证
    public ApiResponse<Map<String, Object>> getPostDetail(
            @Parameter(description = "帖子ID") @PathVariable Long id) {
        
        log.info("获取帖子详情请求: id={}", id);
        
        Map<String, Object> result = communityService.getPostDetail(id);
        
        if ((Boolean) result.get("success")) {
            return ApiResponse.success((Map<String, Object>) result.get("data"), "获取帖子详情成功");
        } else {
            return ApiResponse.error((String) result.get("message"));
        }
    }

    @Operation(summary = "发布帖子", description = "发布新的社区帖子")
    @PostMapping("/posts")
    @PreAuthorize("hasAnyRole('USER', 'VIP', 'ADMIN')")
    public ApiResponse<Map<String, Object>> createPost(
            @Parameter(description = "帖子发布请求") @Valid @RequestBody CreatePostRequest request,
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        log.info("发布帖子请求: title={}", request.getTitle());
        
        // 模拟从token中解析用户ID
        Long authorId = 1L;
        
        Map<String, Object> result = communityService.createPost(
            authorId, request.getTitle(), request.getContent(), 
            request.getCategory(), request.getTags()
        );
        
        if ((Boolean) result.get("success")) {
            Map<String, Object> data = new HashMap<>();
            data.put("postId", result.get("postId"));
            return ApiResponse.success(data, (String) result.get("message"));
        } else {
            return ApiResponse.error((String) result.get("message"));
        }
    }

    @Operation(summary = "获取帖子评论", description = "获取帖子的评论列表")
    @GetMapping("/posts/{id}/comments")
    // 公开接口，无需权限验证
    public ApiResponse<Map<String, Object>> getPostComments(
            @Parameter(description = "帖子ID") @PathVariable Long id,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size) {
        
        log.info("获取帖子评论请求: postId={}, page={}, size={}", id, page, size);
        
        Map<String, Object> result = communityService.getPostComments(id, page, size);
        
        if ((Boolean) result.get("success")) {
            return ApiResponse.success(result, "获取评论成功");
        } else {
            return ApiResponse.error((String) result.get("message"));
        }
    }

    @Operation(summary = "添加评论", description = "为帖子添加评论")
    @PostMapping("/posts/{id}/comments")
    @PreAuthorize("hasAnyRole('USER', 'VIP', 'ADMIN')")
    public ApiResponse<Map<String, Object>> addComment(
            @Parameter(description = "帖子ID") @PathVariable Long id,
            @Parameter(description = "评论请求") @Valid @RequestBody AddCommentRequest request,
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        log.info("添加评论请求: postId={}, content={}", id, request.getContent());
        
        // 模拟从token中解析用户ID
        Long authorId = 1L;
        
        Map<String, Object> result = communityService.addComment(
            id, authorId, request.getContent(), request.getParentId()
        );
        
        if ((Boolean) result.get("success")) {
            Map<String, Object> data = new HashMap<>();
            data.put("commentId", result.get("commentId"));
            return ApiResponse.success(data, (String) result.get("message"));
        } else {
            return ApiResponse.error((String) result.get("message"));
        }
    }

    @Operation(summary = "点赞帖子", description = "为帖子点赞")
    @PostMapping("/posts/{id}/like")
    @PreAuthorize("hasAnyRole('USER', 'VIP', 'ADMIN')")
    public ApiResponse<String> likePost(
            @Parameter(description = "帖子ID") @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        log.info("点赞帖子请求: postId={}", id);
        
        // 模拟从token中解析用户ID
        Long userId = 1L;
        
        Map<String, Object> result = communityService.likePost(id, userId);
        
        if ((Boolean) result.get("success")) {
            return ApiResponse.success(null, (String) result.get("message"));
        } else {
            return ApiResponse.error((String) result.get("message"));
        }
    }

}