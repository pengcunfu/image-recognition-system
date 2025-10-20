package com.pengcunfu.recognition.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pengcunfu.recognition.constant.ErrorCode;
import com.pengcunfu.recognition.entity.CommunityPost;
import com.pengcunfu.recognition.entity.User;
import com.pengcunfu.recognition.entity.UserLike;
import com.pengcunfu.recognition.entity.UserCollect;
import com.pengcunfu.recognition.enums.PostStatus;
import com.pengcunfu.recognition.enums.TargetType;
import com.pengcunfu.recognition.exception.BusinessException;
import com.pengcunfu.recognition.repository.CommunityPostRepository;
import com.pengcunfu.recognition.repository.UserRepository;
import com.pengcunfu.recognition.repository.UserLikeRepository;
import com.pengcunfu.recognition.repository.UserCollectRepository;
import com.pengcunfu.recognition.request.CommunityRequest;
import com.pengcunfu.recognition.response.CommunityResponse;
import com.pengcunfu.recognition.response.PageResponse;
import com.pengcunfu.recognition.service.redis.HotDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

/**
 * 社区服务
 * 处理社区帖子相关业务逻辑
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommunityService {

    private final CommunityPostRepository communityPostRepository;
    private final UserRepository userRepository;
    private final UserLikeRepository userLikeRepository;
    private final UserCollectRepository userCollectRepository;
    private final HotDataService hotDataService;

    /**
     * 发布帖子
     */
    @Transactional
    public CommunityResponse.PostInfo createPost(Long userId, CommunityRequest.CreatePostRequest request) {
        log.info("发布帖子: userId={}, title={}", userId, request.getTitle());

        // 创建帖子（使用 builder 模式）
        CommunityPost post = CommunityPost.builder()
                .userId(userId)
                .title(request.getTitle())
                .content(request.getContent())
                .category(request.getCategory())
                .tags(request.getTags())
                .images(request.getImages())
                .recognitionId(request.getRecognitionId())
                .status(PostStatus.PUBLISHED.getValue())
                .viewCount(0)
                .likeCount(0)
                .commentCount(0)
                .isTop(0)
                .build();

        communityPostRepository.insert(post);

        log.info("帖子发布成功: userId={}, postId={}", userId, post.getId());

        return convertToPostInfo(post);
    }

    /**
     * 获取帖子列表
     */
    public PageResponse<CommunityResponse.PostInfo> getPosts(
            Integer page, Integer size, String category, String sort) {
        log.info("获取帖子列表: page={}, size={}, category={}, sort={}", page, size, category, sort);

        Page<CommunityPost> pageRequest = new Page<>(page, size);
        
        // 构建分类条件
        String categoryCondition = "";
        if (category != null && !category.isEmpty()) {
            categoryCondition = "AND category = '" + category + "'";
        }

        // 根据排序方式查询
        Page<CommunityPost> pageResult;
        if ("hot".equals(sort)) {
            pageResult = communityPostRepository.findPostsByHot(
                    pageRequest, PostStatus.PUBLISHED.getValue(), categoryCondition
            );
        } else {
            pageResult = communityPostRepository.findPostsByLatest(
                    pageRequest, PostStatus.PUBLISHED.getValue(), categoryCondition
            );
        }

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
     * 获取帖子详情
     */
    @Transactional
    public CommunityResponse.PostInfo getPostDetail(Long postId) {
        log.info("获取帖子详情: postId={}", postId);

        CommunityPost post = communityPostRepository.selectById(postId);

        if (post == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "帖子不存在");
        }

        // 增加浏览量（使用 SQL）
        communityPostRepository.incrementViewCount(postId);

        // 缓存热点数据
        hotDataService.incrementPostViewCount(postId);

        return convertToPostInfo(post);
    }

    /**
     * 更新帖子
     */
    @Transactional
    public void updatePost(Long userId, Long postId, CommunityRequest.UpdatePostRequest request) {
        log.info("更新帖子: userId={}, postId={}", userId, postId);

        CommunityPost post = communityPostRepository.selectById(postId);

        if (post == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "帖子不存在");
        }

        if (!post.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权修改该帖子");
        }

        // 更新字段
        if (request.getTitle() != null) {
            post.setTitle(request.getTitle());
        }
        if (request.getContent() != null) {
            post.setContent(request.getContent());
        }
        if (request.getCategory() != null) {
            post.setCategory(request.getCategory());
        }
        if (request.getTags() != null) {
            post.setTags(request.getTags());
        }

        communityPostRepository.updateById(post);

        log.info("帖子更新成功: userId={}, postId={}", userId, postId);
    }

    /**
     * 删除帖子
     */
    @Transactional
    public void deletePost(Long userId, Long postId) {
        log.info("删除帖子: userId={}, postId={}", userId, postId);

        CommunityPost post = communityPostRepository.selectById(postId);

        if (post == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "帖子不存在");
        }

        if (!post.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权删除该帖子");
        }

        communityPostRepository.deleteById(postId);

        log.info("帖子删除成功: userId={}, postId={}", userId, postId);
    }

    /**
     * 点赞帖子
     */
    @Transactional
    public void likePost(Long userId, Long postId) {
        log.info("点赞帖子: userId={}, postId={}", userId, postId);

        CommunityPost post = communityPostRepository.selectById(postId);

        if (post == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "帖子不存在");
        }

        // 检查是否已点赞
        UserLike existingLike = userLikeRepository.findByUserAndTarget(
                userId, postId, TargetType.POST.getValue()
        );

        if (existingLike != null) {
            throw new BusinessException(ErrorCode.INVALID_PARAM, "已点赞过该帖子");
        }

        // 创建点赞记录（使用 builder 模式）
        UserLike like = UserLike.builder()
                .userId(userId)
                .targetId(postId)
                .targetType(TargetType.POST.getValue())
                .build();

        userLikeRepository.insert(like);

        // 更新帖子点赞数（使用 SQL）
        communityPostRepository.incrementLikeCount(postId);

        log.info("点赞成功: userId={}, postId={}", userId, postId);
    }

    /**
     * 取消点赞
     */
    @Transactional
    public void unlikePost(Long userId, Long postId) {
        log.info("取消点赞: userId={}, postId={}", userId, postId);

        UserLike like = userLikeRepository.findByUserAndTarget(
                userId, postId, TargetType.POST.getValue()
        );

        if (like == null) {
            throw new BusinessException(ErrorCode.INVALID_PARAM, "未点赞该帖子");
        }

        userLikeRepository.deleteByUserAndTarget(
                userId, postId, TargetType.POST.getValue()
        );

        // 更新帖子点赞数（使用 SQL）
        communityPostRepository.decrementLikeCount(postId);

        log.info("取消点赞成功: userId={}, postId={}", userId, postId);
    }

    /**
     * 收藏帖子
     */
    @Transactional
    public void collectPost(Long userId, Long postId) {
        log.info("收藏帖子: userId={}, postId={}", userId, postId);

        CommunityPost post = communityPostRepository.selectById(postId);

        if (post == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "帖子不存在");
        }

        // 检查是否已收藏
        UserCollect existingCollect = userCollectRepository.findByUserAndTarget(
                userId, postId, TargetType.POST.getValue()
        );

        if (existingCollect != null) {
            throw new BusinessException(ErrorCode.INVALID_PARAM, "已收藏过该帖子");
        }

        // 创建收藏记录（使用 builder 模式）
        UserCollect collect = UserCollect.builder()
                .userId(userId)
                .targetId(postId)
                .targetType(TargetType.POST.getValue())
                .build();

        userCollectRepository.insert(collect);

        log.info("收藏成功: userId={}, postId={}", userId, postId);
    }

    /**
     * 取消收藏
     */
    @Transactional
    public void uncollectPost(Long userId, Long postId) {
        log.info("取消收藏: userId={}, postId={}", userId, postId);

        UserCollect collect = userCollectRepository.findByUserAndTarget(
                userId, postId, TargetType.POST.getValue()
        );

        if (collect == null) {
            throw new BusinessException(ErrorCode.INVALID_PARAM, "未收藏该帖子");
        }

        userCollectRepository.deleteByUserAndTarget(
                userId, postId, TargetType.POST.getValue()
        );

        log.info("取消收藏成功: userId={}, postId={}", userId, postId);
    }

    /**
     * 转换为帖子信息 DTO
     */
    private CommunityResponse.PostInfo convertToPostInfo(CommunityPost post) {
        // 获取作者信息
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
