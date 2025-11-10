package com.pcf.recognition.controller;

import com.pcf.recognition.dto.*;
import com.pcf.recognition.dto.CommunityDto.*;
import com.pcf.recognition.service.CommunityService;
import com.pcf.recognition.util.TokenUtil;


import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * 社区控制器
 * 提供社区帖子、评论、互动等功能
 */

@RestController
@RequestMapping("/api/v1/community")
@Slf4j
@RequiredArgsConstructor
public class CommunityController {

    private final CommunityService communityService;
    private final TokenUtil tokenUtil;


    @GetMapping("/posts")
    // 公开接口，无需权限验证
    public ApiResponse<PostListResponseDto> getPosts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "latest") String sort) {

        log.info("获取帖子列表请求: page={}, size={}, category={}, sort={}", page, size, category, sort);

        try {
            PostListResponseDto result = communityService.getPosts(page, size, category, sort);
            return ApiResponse.success(result, "获取帖子列表成功");
        } catch (Exception e) {
            log.error("获取帖子列表失败", e);
            return ApiResponse.error("获取帖子列表失败");
        }
    }


    @GetMapping("/posts/{id}")
    // 公开接口，无需权限验证
    public ApiResponse<PostDetailResponseDto> getPostDetail(
            @PathVariable Long id) {

        log.info("获取帖子详情请求: id={}", id);

        try {
            PostDetailResponseDto result = communityService.getPostDetail(id);
            return ApiResponse.success(result, "获取帖子详情成功");
        } catch (Exception e) {
            log.error("获取帖子详情失败", e);
            return ApiResponse.error("帖子不存在或获取失败");
        }
    }


    @PostMapping("/posts")
    @PreAuthorize("hasAnyRole('USER', 'VIP', 'ADMIN')")
    public ApiResponse<PostCreateResponseDto> createPost(
            @Valid @RequestBody CreatePostRequest request,
            @RequestHeader(value = "Authorization", required = false) String token) {

        log.info("发布帖子请求: title={}", request.getTitle());

        try {
            // 从token中解析用户ID
            Long authorId = tokenUtil.getUserIdFromHeader(token);
            if (authorId == null) {
                return ApiResponse.error("无效的Token");
            }

            PostCreateResponseDto result = communityService.createPost(
                    authorId, request.getTitle(), request.getContent(),
                    request.getCategory(), request.getTags()
            );

            return ApiResponse.success(result, "帖子发布成功");
        } catch (Exception e) {
            log.error("发布帖子失败", e);
            return ApiResponse.error("发布帖子失败");
        }
    }


    @GetMapping("/posts/{id}/comments")
    // 公开接口，无需权限验证
    public ApiResponse<CommentListResponseDto> getPostComments(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {

        log.info("获取帖子评论请求: postId={}, page={}, size={}", id, page, size);

        try {
            CommentListResponseDto result = communityService.getPostComments(id, page, size);
            return ApiResponse.success(result, "获取评论成功");
        } catch (Exception e) {
            log.error("获取帖子评论失败", e);
            return ApiResponse.error("获取评论失败");
        }
    }


    @PostMapping("/posts/{id}/comments")
    @PreAuthorize("hasAnyRole('USER', 'VIP', 'ADMIN')")
    public ApiResponse<CommentCreateResponseDto> addComment(
            @PathVariable Long id,
            @Valid @RequestBody AddCommentRequest request,
            @RequestHeader(value = "Authorization", required = false) String token) {

        log.info("添加评论请求: postId={}, content={}", id, request.getContent());

        try {
            // 从token中解析用户ID
            Long authorId = tokenUtil.getUserIdFromHeader(token);
            if (authorId == null) {
                return ApiResponse.error("无效的Token");
            }

            CommentCreateResponseDto result = communityService.addComment(
                    id, authorId, request.getContent(), request.getParentId()
            );

            return ApiResponse.success(result, "评论发布成功");
        } catch (Exception e) {
            log.error("添加评论失败", e);
            return ApiResponse.error("发布评论失败");
        }
    }


    @PostMapping("/posts/{id}/like")
    @PreAuthorize("hasAnyRole('USER', 'VIP', 'ADMIN')")
    public ApiResponse<Void> likePost(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String token) {

        log.info("点赞帖子请求: postId={}", id);

        try {
            // 从token中解析用户ID
            Long userId = tokenUtil.getUserIdFromHeader(token);
            if (userId == null) {
                return ApiResponse.error("无效的Token");
            }

            communityService.likePost(id, userId);

            return ApiResponse.success(null, "点赞成功");
        } catch (Exception e) {
            log.error("点赞帖子失败", e);
            return ApiResponse.error("点赞失败");
        }
    }


    // ==================== 管理员接口 ====================

    /**
     * 管理员获取所有帖子（包括所有状态）
     */
    @GetMapping("/admin/posts")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<PostListResponseDto> getAdminPosts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int sort) {

        log.info("管理员获取帖子列表: page={}, size={}, category={}, status={}, keyword={}, sort={}", 
                 page, size, category, status, keyword, sort);

        try {
            PostListResponseDto result = communityService.getAdminPosts(page, size, category, status, keyword, sort);
            return ApiResponse.success(result, "获取帖子列表成功");
        } catch (Exception e) {
            log.error("获取帖子列表失败", e);
            return ApiResponse.error("获取帖子列表失败: " + e.getMessage());
        }
    }

    /**
     * 审核通过帖子
     */
    @PostMapping("/admin/posts/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> approvePost(@PathVariable Long id) {
        log.info("审核通过帖子请求: postId={}", id);

        try {
            communityService.approvePost(id);
            return ApiResponse.success(null, "帖子审核通过");
        } catch (Exception e) {
            log.error("审核帖子失败", e);
            return ApiResponse.error("审核失败");
        }
    }

    /**
     * 拒绝帖子发布
     */
    @PostMapping("/admin/posts/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> rejectPost(
            @PathVariable Long id,
            @RequestBody(required = false) java.util.Map<String, String> body) {
        
        String reason = body != null ? body.get("reason") : null;
        log.info("拒绝帖子发布请求: postId={}, reason={}", id, reason);

        try {
            communityService.rejectPost(id, reason);
            return ApiResponse.success(null, "已拒绝发布该帖子");
        } catch (Exception e) {
            log.error("拒绝帖子失败", e);
            return ApiResponse.error("操作失败");
        }
    }

    /**
     * 置顶/取消置顶帖子
     */
    @PutMapping("/admin/posts/{id}/top")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> toggleTopPost(
            @PathVariable Long id,
            @RequestBody java.util.Map<String, Boolean> body) {
        
        Boolean isTop = body.get("isTop");
        log.info("置顶帖子请求: postId={}, isTop={}", id, isTop);

        try {
            communityService.toggleTopPost(id, isTop);
            return ApiResponse.success(null, isTop ? "帖子已置顶" : "已取消置顶");
        } catch (Exception e) {
            log.error("置顶帖子失败", e);
            return ApiResponse.error("操作失败");
        }
    }

    /**
     * 隐藏/显示帖子
     */
    @PutMapping("/admin/posts/{id}/visibility")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> togglePostVisibility(
            @PathVariable Long id,
            @RequestBody java.util.Map<String, Boolean> body) {
        
        Boolean isHidden = body.get("isHidden");
        log.info("切换帖子可见性请求: postId={}, isHidden={}", id, isHidden);

        try {
            communityService.togglePostVisibility(id, isHidden);
            return ApiResponse.success(null, isHidden ? "帖子已隐藏" : "帖子已显示");
        } catch (Exception e) {
            log.error("切换帖子可见性失败", e);
            return ApiResponse.error("操作失败");
        }
    }

    /**
     * 删除帖子（管理员）
     */
    @DeleteMapping("/admin/posts/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> deletePostByAdmin(@PathVariable Long id) {
        log.info("管理员删除帖子请求: postId={}", id);

        try {
            communityService.deletePost(id);
            return ApiResponse.success(null, "帖子已删除");
        } catch (Exception e) {
            log.error("删除帖子失败", e);
            return ApiResponse.error("删除失败");
        }
    }

    /**
     * 收藏帖子
     */
    @PostMapping("/posts/{id}/collect")
    @PreAuthorize("hasAnyRole('USER', 'VIP', 'ADMIN')")
    public ApiResponse<Void> collectPost(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        log.info("收藏帖子请求: postId={}", id);
        
        try {
            Long userId = tokenUtil.getUserIdFromHeader(token);
            if (userId == null) {
                return ApiResponse.error("无效的Token");
            }

            communityService.collectPost(id, userId);
            return ApiResponse.success(null, "收藏成功");
        } catch (Exception e) {
            log.error("收藏帖子失败", e);
            return ApiResponse.error("收藏失败");
        }
    }

    /**
     * 取消收藏帖子
     */
    @DeleteMapping("/posts/{id}/collect")
    @PreAuthorize("hasAnyRole('USER', 'VIP', 'ADMIN')")
    public ApiResponse<Void> uncollectPost(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        log.info("取消收藏帖子请求: postId={}", id);
        
        try {
            Long userId = tokenUtil.getUserIdFromHeader(token);
            if (userId == null) {
                return ApiResponse.error("无效的Token");
            }

            communityService.uncollectPost(id, userId);
            return ApiResponse.success(null, "取消收藏成功");
        } catch (Exception e) {
            log.error("取消收藏帖子失败", e);
            return ApiResponse.error("取消收藏失败");
        }
    }

    /**
     * 举报帖子
     */
    @PostMapping("/posts/{id}/report")
    @PreAuthorize("hasAnyRole('USER', 'VIP', 'ADMIN')")
    public ApiResponse<Void> reportPost(
            @PathVariable Long id,
            @RequestBody ReportRequest request,
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        log.info("举报帖子请求: postId={}, type={}", id, request.getType());
        
        try {
            Long userId = tokenUtil.getUserIdFromHeader(token);
            if (userId == null) {
                return ApiResponse.error("无效的Token");
            }

            communityService.reportPost(id, userId, request.getType(), request.getDescription());
            return ApiResponse.success(null, "举报成功，我们会尽快处理");
        } catch (Exception e) {
            log.error("举报帖子失败", e);
            return ApiResponse.error("举报失败");
        }
    }

    /**
     * 获取相关推荐帖子
     */
    @GetMapping("/posts/{id}/related")
    public ApiResponse<List<PostDto>> getRelatedPosts(@PathVariable Long id) {
        
        log.info("获取相关推荐帖子请求: postId={}", id);
        
        try {
            List<PostDto> relatedPosts = communityService.getRelatedPosts(id, 3);
            return ApiResponse.success(relatedPosts, "获取相关推荐成功");
        } catch (Exception e) {
            log.error("获取相关推荐失败", e);
            return ApiResponse.error("获取相关推荐失败");
        }
    }

    /**
     * 举报请求类
     */
    @Data
    public static class ReportRequest {
        @NotBlank(message = "举报类型不能为空")
        private String type; // spam, abuse, inappropriate, other
        
        @NotBlank(message = "举报说明不能为空")
        @Size(max = 500, message = "举报说明不能超过500字")
        private String description;
    }

}