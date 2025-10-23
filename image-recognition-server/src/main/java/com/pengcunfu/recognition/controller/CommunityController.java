package com.pengcunfu.recognition.controller;

import com.pengcunfu.recognition.annotation.Role;
import com.pengcunfu.recognition.request.CommunityRequest;
import com.pengcunfu.recognition.response.ApiResponse;
import com.pengcunfu.recognition.response.CommunityResponse;
import com.pengcunfu.recognition.response.PageResponse;
import com.pengcunfu.recognition.security.SecurityContextHolder;
import com.pengcunfu.recognition.service.CommunityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 社区控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/community")
@RequiredArgsConstructor
public class CommunityController {

    private final CommunityService communityService;

    /**
     * 获取帖子列表
     */
    @GetMapping("/posts")
    public ApiResponse<PageResponse<CommunityResponse.PostInfo>> getPosts(
            CommunityRequest.QueryPostRequest request) {
        log.info("获取帖子列表: page={}, size={}, category={}, tag={}", 
            request.getPage(), request.getSize(), request.getCategory(), request.getTag());
        PageResponse<CommunityResponse.PostInfo> response = 
            communityService.getPosts(
                request.getPage(), 
                request.getSize(), 
                request.getCategory(), 
                request.getTag(),
                request.getSort()
            );
        return ApiResponse.success(response);
    }

    /**
     * 获取帖子详情
     */
    @GetMapping("/posts/{id}")
    public ApiResponse<CommunityResponse.PostInfo> getPostDetail(@PathVariable Long id) {
        log.info("获取帖子详情: id={}", id);
        CommunityResponse.PostInfo post = communityService.getPostDetail(id);
        return ApiResponse.success(post);
    }

    /**
     * 创建帖子
     */
    @Role("USER")
    @PostMapping("/posts")
    public ApiResponse<CommunityResponse.PostInfo> createPost(@Valid @RequestBody CommunityRequest.CreatePostRequest request) {
        Long userId = SecurityContextHolder.getCurrentUserId();
        log.info("创建帖子: userId={}", userId);
        CommunityResponse.PostInfo post = communityService.createPost(userId, request);
        return ApiResponse.success(post);
    }

    /**
     * 更新帖子
     */
    @Role("USER")
    @PutMapping("/posts/{id}")
    public ApiResponse<Void> updatePost(
            @PathVariable Long id,
            @Valid @RequestBody CommunityRequest.UpdatePostRequest request) {
        Long userId = SecurityContextHolder.getCurrentUserId();
        log.info("更新帖子: userId={}, postId={}", userId, id);
        communityService.updatePost(userId, id, request);
        return ApiResponse.success();
    }

    /**
     * 删除帖子
     */
    @Role("USER")
    @DeleteMapping("/posts/{id}")
    public ApiResponse<Void> deletePost(@PathVariable Long id) {
        Long userId = SecurityContextHolder.getCurrentUserId();
        log.info("删除帖子: userId={}, postId={}", userId, id);
        communityService.deletePost(userId, id);
        return ApiResponse.success();
    }

    /**
     * 点赞帖子
     */
    @Role("USER")
    @PostMapping("/posts/{id}/like")
    public ApiResponse<Void> likePost(@PathVariable Long id) {
        Long userId = SecurityContextHolder.getCurrentUserId();
        log.info("点赞帖子: userId={}, postId={}", userId, id);
        communityService.likePost(userId, id);
        return ApiResponse.success();
    }

    /**
     * 取消点赞帖子
     */
    @Role("USER")
    @DeleteMapping("/posts/{id}/like")
    public ApiResponse<Void> unlikePost(@PathVariable Long id) {
        Long userId = SecurityContextHolder.getCurrentUserId();
        log.info("取消点赞帖子: userId={}, postId={}", userId, id);
        communityService.unlikePost(userId, id);
        return ApiResponse.success();
    }

    /**
     * 收藏帖子
     */
    @Role("USER")
    @PostMapping("/posts/{id}/collect")
    public ApiResponse<Void> collectPost(@PathVariable Long id) {
        Long userId = SecurityContextHolder.getCurrentUserId();
        log.info("收藏帖子: userId={}, postId={}", userId, id);
        communityService.collectPost(userId, id);
        return ApiResponse.success();
    }

    /**
     * 取消收藏帖子
     */
    @Role("USER")
    @DeleteMapping("/posts/{id}/collect")
    public ApiResponse<Void> uncollectPost(@PathVariable Long id) {
        Long userId = SecurityContextHolder.getCurrentUserId();
        log.info("取消收藏帖子: userId={}, postId={}", userId, id);
        communityService.uncollectPost(userId, id);
        return ApiResponse.success();
    }

    /**
     * 获取帖子分类列表
     */
    @GetMapping("/categories")
    public ApiResponse<java.util.List<CommunityResponse.CategoryInfo>> getCategories() {
        log.info("获取帖子分类列表");
        java.util.List<CommunityResponse.CategoryInfo> categories = communityService.getCategories();
        return ApiResponse.success(categories);
    }

    /**
     * 获取帖子标签列表
     */
    @GetMapping("/tags")
    public ApiResponse<java.util.List<CommunityResponse.TagInfo>> getTags() {
        log.info("获取帖子标签列表");
        java.util.List<CommunityResponse.TagInfo> tags = communityService.getTags();
        return ApiResponse.success(tags);
    }

    /**
     * 获取当前用户发布的帖子列表
     */
    @Role("USER")
    @GetMapping("/my-posts")
    public ApiResponse<PageResponse<CommunityResponse.PostInfo>> getMyPosts(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Long userId = SecurityContextHolder.getCurrentUserId();
        log.info("获取用户发布的帖子: userId={}, page={}, size={}", userId, page, size);
        PageResponse<CommunityResponse.PostInfo> response = communityService.getPostsByAuthor(userId, page, size);
        return ApiResponse.success(response);
    }

    /**
     * 获取相关推荐帖子
     */
    @GetMapping("/posts/{id}/related")
    public ApiResponse<java.util.List<CommunityResponse.PostInfo>> getRelatedPosts(@PathVariable Long id) {
        log.info("获取相关推荐帖子: id={}", id);
        java.util.List<CommunityResponse.PostInfo> related = communityService.getRelatedPosts(id);
        return ApiResponse.success(related);
    }
}

