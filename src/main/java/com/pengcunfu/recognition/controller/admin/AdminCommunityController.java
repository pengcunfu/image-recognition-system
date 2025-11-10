package com.pengcunfu.recognition.controller.admin;

import com.pengcunfu.recognition.annotation.Role;
import com.pengcunfu.recognition.request.CommunityRequest;
import com.pengcunfu.recognition.response.ApiResponse;
import com.pengcunfu.recognition.response.CommunityResponse;
import com.pengcunfu.recognition.response.PageResponse;
import com.pengcunfu.recognition.service.CommunityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 管理后台 - 社区管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/community")
@RequiredArgsConstructor
@Role("ADMIN")
public class AdminCommunityController {

    private final CommunityService communityService;

    /**
     * 获取帖子列表（管理员）
     */
    @GetMapping("/posts")
    public ApiResponse<PageResponse<CommunityResponse.PostInfo>> getPostsAdmin(
            CommunityRequest.QueryPostRequest request) {
        log.info("管理员获取帖子列表: page={}, size={}, status={}, keyword={}", 
                request.getPage(), request.getSize(), request.getStatus(), request.getKeyword());
        PageResponse<CommunityResponse.PostInfo> response = 
            communityService.getPostsAdmin(
                request.getPage(), 
                request.getSize(), 
                request.getStatus(), 
                request.getKeyword()
            );
        return ApiResponse.success(response);
    }

    /**
     * 获取帖子详情（管理员）
     */
    @GetMapping("/posts/{postId}")
    public ApiResponse<CommunityResponse.PostInfo> getPostDetailAdmin(@PathVariable Long postId) {
        log.info("管理员获取帖子详情: postId={}", postId);
        CommunityResponse.PostInfo response = communityService.getPostDetail(postId);
        return ApiResponse.success(response);
    }

    /**
     * 创建帖子（管理员）
     */
    @PostMapping("/posts")
    public ApiResponse<Long> createPostAdmin(@RequestBody CommunityRequest.CreatePostRequest request) {
        log.info("管理员创建帖子: title={}, category={}", request.getTitle(), request.getCategory());
        Long postId = communityService.createPostAdmin(request);
        return ApiResponse.success(postId);
    }

    /**
     * 更新帖子（管理员）
     */
    @PutMapping("/posts/{postId}")
    public ApiResponse<Void> updatePostAdmin(
            @PathVariable Long postId,
            @RequestBody CommunityRequest.UpdatePostRequest request) {
        log.info("管理员更新帖子: postId={}, title={}", postId, request.getTitle());
        communityService.updatePostAdmin(postId, request);
        return ApiResponse.success();
    }

    /**
     * 更新帖子状态
     */
    @PutMapping("/posts/{postId}/status")
    public ApiResponse<Void> updatePostStatus(
            @PathVariable Long postId,
            @RequestParam Integer status) {
        log.info("更新帖子状态: postId={}, status={}", postId, status);
        communityService.updatePostStatus(postId, status);
        return ApiResponse.success();
    }

    /**
     * 删除帖子（管理员）
     */
    @DeleteMapping("/posts/{postId}")
    public ApiResponse<Void> deletePostAdmin(@PathVariable Long postId) {
        log.info("管理员删除帖子: postId={}", postId);
        communityService.deletePostAdmin(postId);
        return ApiResponse.success();
    }

    /**
     * 置顶/取消置顶帖子
     */
    @PutMapping("/posts/{postId}/top")
    public ApiResponse<Void> togglePostTop(
            @PathVariable Long postId,
            @RequestParam Integer isTop) {
        log.info("置顶帖子: postId={}, isTop={}", postId, isTop);
        communityService.togglePostTop(postId, isTop);
        return ApiResponse.success();
    }
}

