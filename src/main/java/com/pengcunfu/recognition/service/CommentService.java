package com.pengcunfu.recognition.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pengcunfu.recognition.constant.ErrorCode;
import com.pengcunfu.recognition.entity.Comment;
import com.pengcunfu.recognition.entity.CommunityPost;
import com.pengcunfu.recognition.entity.User;
import com.pengcunfu.recognition.entity.UserLike;
import com.pengcunfu.recognition.enums.CommentStatus;
import com.pengcunfu.recognition.enums.TargetType;
import com.pengcunfu.recognition.exception.BusinessException;
import com.pengcunfu.recognition.repository.CommentRepository;
import com.pengcunfu.recognition.repository.CommunityPostRepository;
import com.pengcunfu.recognition.repository.UserRepository;
import com.pengcunfu.recognition.repository.UserLikeRepository;
import com.pengcunfu.recognition.request.CommentRequest;
import com.pengcunfu.recognition.response.CommentResponse;
import com.pengcunfu.recognition.response.PageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

/**
 * 评论服务
 * 处理评论相关业务逻辑
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommunityPostRepository communityPostRepository;
    private final UserRepository userRepository;
    private final UserLikeRepository userLikeRepository;

    /**
     * 发表评论
     */
    @Transactional
    public CommentResponse.CommentInfo createComment(Long userId, CommentRequest.CreateCommentRequest request) {
        log.info("发表评论: userId={}, targetId={}, targetType={}",
                userId, request.getTargetId(), request.getTargetType());

        // 验证目标是否存在
        validateTarget(request.getTargetType(), request.getTargetId());

        // 创建评论（使用 Builder）
        Comment comment = Comment.builder()
                .userId(userId)
                .targetId(request.getTargetId())
                .targetType(request.getTargetType())
                .content(request.getContent())
                .parentId(request.getParentId())
                .status(CommentStatus.PUBLISHED.getValue())
                .likeCount(0)
                .build();

        commentRepository.insert(comment);

        // 更新帖子评论数（如果评论的是帖子）
        if (request.getTargetType() == TargetType.POST.getValue()) {
            CommunityPost post = communityPostRepository.selectById(request.getTargetId());
            if (post != null) {
                post.setCommentCount(post.getCommentCount() + 1);
                communityPostRepository.updateById(post);
            }
        }

        log.info("评论发表成功: userId={}, commentId={}", userId, comment.getId());

        return convertToCommentInfo(comment);
    }

    /**
     * 获取评论列表
     */
    public PageResponse<CommentResponse.CommentInfo> getComments(
            Integer targetType, Long targetId, Integer page, Integer size) {
        log.info("获取评论列表: targetType={}, targetId={}, page={}, size={}",
                targetType, targetId, page, size);

        Page<Comment> pageRequest = new Page<>(page, size);
        Page<Comment> pageResult = commentRepository.findCommentsByTarget(
                pageRequest, targetType, targetId, CommentStatus.PUBLISHED.getValue()
        );

        return PageResponse.<CommentResponse.CommentInfo>builder()
                .data(pageResult.getRecords().stream()
                        .map(this::convertToCommentInfo)
                        .collect(Collectors.toList()))
                .total(pageResult.getTotal())
                .page((int) pageResult.getCurrent())
                .size((int) pageResult.getSize())
                .pages((int) pageResult.getPages())
                .build();
    }

    /**
     * 获取评论的回复列表
     */
    public PageResponse<CommentResponse.CommentInfo> getReplies(Long commentId, Integer page, Integer size) {
        log.info("获取评论回复: commentId={}, page={}, size={}", commentId, page, size);

        Page<Comment> pageRequest = new Page<>(page, size);
        Page<Comment> pageResult = commentRepository.findRepliesByParent(
                pageRequest, commentId, CommentStatus.PUBLISHED.getValue()
        );

        return PageResponse.<CommentResponse.CommentInfo>builder()
                .data(pageResult.getRecords().stream()
                        .map(this::convertToCommentInfo)
                        .collect(Collectors.toList()))
                .total(pageResult.getTotal())
                .page((int) pageResult.getCurrent())
                .size((int) pageResult.getSize())
                .pages((int) pageResult.getPages())
                .build();
    }

    /**
     * 删除评论
     */
    @Transactional
    public void deleteComment(Long userId, Long commentId) {
        log.info("删除评论: userId={}, commentId={}", userId, commentId);

        Comment comment = commentRepository.selectById(commentId);

        if (comment == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "评论不存在");
        }

        if (!comment.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权删除该评论");
        }

        commentRepository.deleteById(commentId);

        // 更新帖子评论数
        if (comment.getTargetType() == TargetType.POST.getValue()) {
            CommunityPost post = communityPostRepository.selectById(comment.getTargetId());
            if (post != null && post.getCommentCount() > 0) {
                post.setCommentCount(post.getCommentCount() - 1);
                communityPostRepository.updateById(post);
            }
        }

        log.info("评论删除成功: userId={}, commentId={}", userId, commentId);
    }

    /**
     * 点赞评论
     */
    @Transactional
    public void likeComment(Long userId, Long commentId) {
        log.info("点赞评论: userId={}, commentId={}", userId, commentId);

        Comment comment = commentRepository.selectById(commentId);

        if (comment == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "评论不存在");
        }

        // 检查是否已点赞
        UserLike existingLike = userLikeRepository.findByUserAndTarget(
                userId, commentId, TargetType.COMMENT.getValue()
        );

        if (existingLike != null) {
            throw new BusinessException(ErrorCode.INVALID_PARAM, "已点赞过该评论");
        }

        // 创建点赞记录（使用 builder 模式）
        UserLike like = UserLike.builder()
                .userId(userId)
                .targetId(commentId)
                .targetType(TargetType.COMMENT.getValue())
                .build();

        userLikeRepository.insert(like);

        // 更新评论点赞数
        commentRepository.incrementLikeCount(commentId);

        log.info("点赞成功: userId={}, commentId={}", userId, commentId);
    }

    /**
     * 取消点赞
     */
    @Transactional
    public void unlikeComment(Long userId, Long commentId) {
        log.info("取消点赞: userId={}, commentId={}", userId, commentId);

        UserLike like = userLikeRepository.findByUserAndTarget(
                userId, commentId, TargetType.COMMENT.getValue()
        );

        if (like == null) {
            throw new BusinessException(ErrorCode.INVALID_PARAM, "未点赞该评论");
        }

        userLikeRepository.deleteByUserAndTarget(
                userId, commentId, TargetType.COMMENT.getValue()
        );

        // 更新评论点赞数
        commentRepository.decrementLikeCount(commentId);

        log.info("取消点赞成功: userId={}, commentId={}", userId, commentId);
    }

    /**
     * 验证目标是否存在
     */
    private void validateTarget(Integer targetType, Long targetId) {
        if (targetType == TargetType.POST.getValue()) {
            CommunityPost post = communityPostRepository.selectById(targetId);
            if (post == null) {
                throw new BusinessException(ErrorCode.NOT_FOUND, "帖子不存在");
            }
        }
        // 可以添加其他类型的验证
    }

    /**
     * 转换为评论信息 DTO
     */
    private CommentResponse.CommentInfo convertToCommentInfo(Comment comment) {
        // 获取用户信息
        User user = userRepository.selectById(comment.getUserId());

        return CommentResponse.CommentInfo.builder()
                .id(comment.getId())
                .userId(comment.getUserId())
                .username(user != null ? user.getUsername() : "未知用户")
                .avatar(user != null ? user.getAvatar() : null)
                .targetId(comment.getTargetId())
                .targetType(comment.getTargetType())
                .content(comment.getContent())
                .parentId(comment.getParentId())
                .likeCount(comment.getLikeCount())
                .status(comment.getStatus())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}

