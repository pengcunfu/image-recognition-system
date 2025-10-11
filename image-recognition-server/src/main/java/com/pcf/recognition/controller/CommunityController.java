package com.pcf.recognition.controller;

import com.pcf.recognition.dto.*;
import com.pcf.recognition.service.CommunityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

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
    public ApiResponse<PostListResponseDto> getPosts(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "分类筛选") @RequestParam(required = false) String category,
            @Parameter(description = "排序方式") @RequestParam(defaultValue = "latest") String sort) {

        log.info("获取帖子列表请求: page={}, size={}, category={}, sort={}", page, size, category, sort);

        try {
            PostListResponseDto result = communityService.getPosts(page, size, category, sort);
            return ApiResponse.success(result, "获取帖子列表成功");
        } catch (Exception e) {
            log.error("获取帖子列表失败", e);
            return ApiResponse.error("获取帖子列表失败");
        }
    }

    @Operation(summary = "获取帖子详情", description = "获取单个帖子的详细信息")
    @GetMapping("/posts/{id}")
    // 公开接口，无需权限验证
    public ApiResponse<PostDto> getPostDetail(
            @Parameter(description = "帖子ID") @PathVariable Long id) {

        log.info("获取帖子详情请求: id={}", id);

        try {
            PostDetailResponseDto result = communityService.getPostDetail(id);
            return ApiResponse.success(result.getPost(), "获取帖子详情成功");
        } catch (Exception e) {
            log.error("获取帖子详情失败", e);
            return ApiResponse.error("帖子不存在或获取失败");
        }
    }

    @Operation(summary = "发布帖子", description = "发布新的社区帖子")
    @PostMapping("/posts")
    @PreAuthorize("hasAnyRole('USER', 'VIP', 'ADMIN')")
    public ApiResponse<PostCreateResponseDto> createPost(
            @Parameter(description = "帖子发布请求") @Valid @RequestBody CreatePostRequest request,
            @RequestHeader(value = "Authorization", required = false) String token) {

        log.info("发布帖子请求: title={}", request.getTitle());

        try {
            // 模拟从token中解析用户ID
            Long authorId = 1L;

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

    @Operation(summary = "获取帖子评论", description = "获取帖子的评论列表")
    @GetMapping("/posts/{id}/comments")
    // 公开接口，无需权限验证
    public ApiResponse<CommentListResponseDto> getPostComments(
            @Parameter(description = "帖子ID") @PathVariable Long id,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size) {

        log.info("获取帖子评论请求: postId={}, page={}, size={}", id, page, size);

        try {
            CommentListResponseDto result = communityService.getPostComments(id, page, size);
            return ApiResponse.success(result, "获取评论成功");
        } catch (Exception e) {
            log.error("获取帖子评论失败", e);
            return ApiResponse.error("获取评论失败");
        }
    }

    @Operation(summary = "添加评论", description = "为帖子添加评论")
    @PostMapping("/posts/{id}/comments")
    @PreAuthorize("hasAnyRole('USER', 'VIP', 'ADMIN')")
    public ApiResponse<CommentCreateResponseDto> addComment(
            @Parameter(description = "帖子ID") @PathVariable Long id,
            @Parameter(description = "评论请求") @Valid @RequestBody AddCommentRequest request,
            @RequestHeader(value = "Authorization", required = false) String token) {

        log.info("添加评论请求: postId={}, content={}", id, request.getContent());

        try {
            // 模拟从token中解析用户ID
            Long authorId = 1L;

            CommentCreateResponseDto result = communityService.addComment(
                    id, authorId, request.getContent(), request.getParentId()
            );

            return ApiResponse.success(result, "评论发布成功");
        } catch (Exception e) {
            log.error("添加评论失败", e);
            return ApiResponse.error("发布评论失败");
        }
    }

    @Operation(summary = "点赞帖子", description = "为帖子点赞")
    @PostMapping("/posts/{id}/like")
    @PreAuthorize("hasAnyRole('USER', 'VIP', 'ADMIN')")
    public ApiResponse<Void> likePost(
            @Parameter(description = "帖子ID") @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String token) {

        log.info("点赞帖子请求: postId={}", id);

        try {
            // 模拟从token中解析用户ID
            Long userId = 1L;

            communityService.likePost(id, userId);

            return ApiResponse.success(null, "点赞成功");
        } catch (Exception e) {
            log.error("点赞帖子失败", e);
            return ApiResponse.error("点赞失败");
        }
    }

}