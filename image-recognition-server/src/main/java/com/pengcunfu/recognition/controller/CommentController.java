package com.pengcunfu.recognition.controller;

import com.pengcunfu.recognition.request.CommentRequest;
import com.pengcunfu.recognition.response.ApiResponse;
import com.pengcunfu.recognition.response.CommentResponse;
import com.pengcunfu.recognition.response.PageResponse;
import com.pengcunfu.recognition.security.SecurityContextHolder;
import com.pengcunfu.recognition.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 评论控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    /**
     * 获取评论列表
     */
    @GetMapping
    public ApiResponse<PageResponse<CommentResponse.CommentInfo>> getComments(
            CommentRequest.QueryCommentRequest request) {
        log.info("获取评论列表: targetType={}, targetId={}, page={}, size={}", 
            request.getTargetType(), request.getTargetId(), request.getPage(), request.getSize());
        PageResponse<CommentResponse.CommentInfo> response = 
            commentService.getComments(
                request.getTargetType(), 
                request.getTargetId(), 
                request.getPage(), 
                request.getSize()
            );
        return ApiResponse.success(response);
    }

    /**
     * 创建评论
     */
    @PostMapping
    public ApiResponse<CommentResponse.CommentInfo> createComment(
            @Valid @RequestBody CommentRequest.CreateCommentRequest request) {
        Long userId = SecurityContextHolder.getCurrentUserId();
        log.info("创建评论: userId={}, targetType={}, targetId={}", 
            userId, request.getTargetType(), request.getTargetId());
        CommentResponse.CommentInfo comment = commentService.createComment(userId, request);
        return ApiResponse.success(comment);
    }

    /**
     * 删除评论
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteComment(@PathVariable Long id) {
        Long userId = SecurityContextHolder.getCurrentUserId();
        log.info("删除评论: userId={}, commentId={}", userId, id);
        commentService.deleteComment(userId, id);
        return ApiResponse.success();
    }

    /**
     * 点赞评论
     */
    @PostMapping("/{id}/like")
    public ApiResponse<Void> likeComment(@PathVariable Long id) {
        Long userId = SecurityContextHolder.getCurrentUserId();
        log.info("点赞评论: userId={}, commentId={}", userId, id);
        commentService.likeComment(userId, id);
        return ApiResponse.success();
    }

    /**
     * 取消点赞评论
     */
    @DeleteMapping("/{id}/like")
    public ApiResponse<Void> unlikeComment(@PathVariable Long id) {
        Long userId = SecurityContextHolder.getCurrentUserId();
        log.info("取消点赞评论: userId={}, commentId={}", userId, id);
        commentService.unlikeComment(userId, id);
        return ApiResponse.success();
    }
}

