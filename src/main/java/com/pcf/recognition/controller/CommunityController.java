package com.pcf.recognition.controller;

import com.pcf.recognition.dto.ApiResponse;
import com.pcf.recognition.service.CommunityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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

    // 请求数据类
    public static class CreatePostRequest {
        @NotBlank(message = "标题不能为空")
        @Size(max = 200, message = "标题长度不能超过200字符")
        private String title;
        
        @NotBlank(message = "内容不能为空")
        private String content;
        
        private String category;
        private String tags;

        // getters and setters
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public String getTags() { return tags; }
        public void setTags(String tags) { this.tags = tags; }
    }

    public static class AddCommentRequest {
        @NotBlank(message = "评论内容不能为空")
        private String content;
        
        private Long parentId;

        // getters and setters
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public Long getParentId() { return parentId; }
        public void setParentId(Long parentId) { this.parentId = parentId; }
    }
}