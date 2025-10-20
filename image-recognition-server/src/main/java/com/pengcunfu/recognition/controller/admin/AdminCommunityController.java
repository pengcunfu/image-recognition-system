package com.pengcunfu.recognition.controller.admin;

import com.pengcunfu.recognition.request.admin.AdminCommunityRequest;
import com.pengcunfu.recognition.response.ApiResponse;
import com.pengcunfu.recognition.response.CommunityResponse;
import com.pengcunfu.recognition.response.PageResponse;
import com.pengcunfu.recognition.service.admin.AdminCommunityService;
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
public class AdminCommunityController {

    private final AdminCommunityService adminCommunityService;

    /**
     * 获取帖子列表（管理员）
     */
    @GetMapping("/posts")
    public ApiResponse<PageResponse<CommunityResponse.PostInfo>> getPostsAdmin(
            AdminCommunityRequest.QueryPostRequest request) {
        log.info("管理员获取帖子列表: page={}, size={}, status={}, keyword={}", 
                request.getPage(), request.getSize(), request.getStatus(), request.getKeyword());
        PageResponse<CommunityResponse.PostInfo> response = 
            adminCommunityService.getPostsAdmin(
                request.getPage(), 
                request.getSize(), 
                request.getStatus(), 
                request.getKeyword()
            );
        return ApiResponse.success(response);
    }

    /**
     * 更新帖子状态
     */
    @PutMapping("/posts/{postId}/status")
    public ApiResponse<Void> updatePostStatus(
            @PathVariable Long postId,
            @RequestParam Integer status) {
        log.info("更新帖子状态: postId={}, status={}", postId, status);
        adminCommunityService.updatePostStatus(postId, status);
        return ApiResponse.success();
    }

    /**
     * 删除帖子（管理员）
     */
    @DeleteMapping("/posts/{postId}")
    public ApiResponse<Void> deletePostAdmin(@PathVariable Long postId) {
        log.info("管理员删除帖子: postId={}", postId);
        adminCommunityService.deletePostAdmin(postId);
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
        adminCommunityService.togglePostTop(postId, isTop);
        return ApiResponse.success();
    }
}

