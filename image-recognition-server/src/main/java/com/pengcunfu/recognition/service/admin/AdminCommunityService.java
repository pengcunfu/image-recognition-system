package com.pengcunfu.recognition.service.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pengcunfu.recognition.constant.ErrorCode;
import com.pengcunfu.recognition.entity.CommunityPost;
import com.pengcunfu.recognition.entity.User;
import com.pengcunfu.recognition.exception.BusinessException;
import com.pengcunfu.recognition.repository.CommunityPostRepository;
import com.pengcunfu.recognition.repository.UserRepository;
import com.pengcunfu.recognition.response.CommunityResponse;
import com.pengcunfu.recognition.response.PageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

/**
 * 管理员 - 社区管理服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminCommunityService {

    private final CommunityPostRepository communityPostRepository;
    private final UserRepository userRepository;

    /**
     * 获取帖子列表（管理员，使用 SQL 查询）
     */
    public PageResponse<CommunityResponse.PostInfo> getPostsAdmin(
            Integer page, Integer size, Integer status, String keyword) {
        log.info("管理员获取帖子列表: page={}, size={}, status={}, keyword={}", 
                page, size, status, keyword);

        Page<CommunityPost> pageRequest = new Page<>(page, size);
        Page<CommunityPost> pageResult = communityPostRepository.findPostsForAdmin(
                pageRequest, status, keyword);

        return PageResponse.<CommunityResponse.PostInfo>builder()
                .data(pageResult.getRecords().stream()
                        .map(this::convertToPostInfo)
                        .collect(Collectors.toList()))
                .total(pageResult.getTotal())
                .page((int) pageResult.getCurrent())
                .size((int) pageResult.getSize())
                .pages((int) pageResult.getPages())
                .build();
    }

    /**
     * 更新帖子状态
     */
    @Transactional
    public void updatePostStatus(Long postId, Integer status) {
        log.info("更新帖子状态: postId={}, status={}", postId, status);

        CommunityPost post = communityPostRepository.selectById(postId);

        if (post == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "帖子不存在");
        }

        post.setStatus(status);
        communityPostRepository.updateById(post);

        log.info("帖子状态更新成功: postId={}, status={}", postId, status);
    }

    /**
     * 删除帖子（管理员）
     */
    @Transactional
    public void deletePostAdmin(Long postId) {
        log.info("管理员删除帖子: postId={}", postId);

        CommunityPost post = communityPostRepository.selectById(postId);

        if (post == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "帖子不存在");
        }

        communityPostRepository.deleteById(postId);

        log.info("帖子删除成功: postId={}", postId);
    }

    /**
     * 置顶帖子
     */
    @Transactional
    public void togglePostTop(Long postId, Integer isTop) {
        log.info("置顶帖子: postId={}, isTop={}", postId, isTop);

        CommunityPost post = communityPostRepository.selectById(postId);

        if (post == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "帖子不存在");
        }

        post.setIsTop(isTop);
        communityPostRepository.updateById(post);

        log.info("帖子置顶更新成功: postId={}, isTop={}", postId, isTop);
    }

    /**
     * 转换为帖子信息 DTO
     */
    private CommunityResponse.PostInfo convertToPostInfo(CommunityPost post) {
        User author = userRepository.selectById(post.getUserId());
        
        return CommunityResponse.PostInfo.builder()
                .id(post.getId())
                .userId(post.getUserId())
                .authorId(post.getUserId())
                .authorName(author != null ? author.getUsername() : "未知用户")
                .username(author != null ? author.getUsername() : "未知用户")
                .authorAvatar(author != null ? author.getAvatar() : null)
                .avatar(author != null ? author.getAvatar() : null)
                .title(post.getTitle())
                .content(post.getContent())
                .category(post.getCategory())
                .tags(post.getTags())
                .images(post.getImages())
                .imageUrl(post.getImages())
                .recognitionId(post.getRecognitionId())
                .recognitionResultId(post.getRecognitionId())
                .status(post.getStatus())
                .viewCount(post.getViewCount())
                .likeCount(post.getLikeCount())
                .commentCount(post.getCommentCount())
                .isTop(post.getIsTop())
                .createdAt(post.getCreatedAt())
                .createTime(post.getCreatedAt())
                .build();
    }
}

