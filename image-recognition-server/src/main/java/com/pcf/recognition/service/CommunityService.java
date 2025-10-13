package com.pcf.recognition.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pcf.recognition.dto.CommunityDto.*;
import com.pcf.recognition.entity.CommunityPost;
import com.pcf.recognition.entity.CommunityComment;
import com.pcf.recognition.repository.CommunityPostRepository;
import com.pcf.recognition.repository.CommunityCommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

/**
 * 社区服务
 * 处理社区帖子、评论、互动等业务逻辑
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CommunityService {

    private final CommunityPostRepository communityPostRepository;
    private final CommunityCommentRepository communityCommentRepository;

    /**
     * 获取帖子列表（分页）
     */
    public PostListResponseDto getPosts(Integer page, Integer size, String category, String sort) {
        log.info("获取帖子列表: page={}, size={}, category={}", page, size, category);

        Page<CommunityPost> pageRequest = new Page<>(page, size);

        LambdaQueryWrapper<CommunityPost> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CommunityPost::getStatus, CommunityPost.PostStatus.PUBLISHED);

        if (category != null && !category.isEmpty()) {
            queryWrapper.eq(CommunityPost::getCategory, category);
        }

        // 排序
        if ("hot".equals(sort)) {
            queryWrapper.orderByDesc(CommunityPost::getLikeCount);
        } else if ("latest".equals(sort)) {
            queryWrapper.orderByDesc(CommunityPost::getCreateTime);
        } else {
            queryWrapper.orderByDesc(CommunityPost::getCreateTime);
        }

        Page<CommunityPost> result = communityPostRepository.selectPage(pageRequest, queryWrapper);

        PostListResponseDto response = new PostListResponseDto();
        response.setData(result.getRecords().stream()
                .map(this::convertToPostDto)
                .collect(Collectors.toList()));
        response.setTotal(result.getTotal());
        response.setPages(result.getPages());
        response.setCurrent(result.getCurrent());
        response.setSize(result.getSize());

        return response;
    }

    /**
     * 获取帖子详情
     */
    public PostDetailResponseDto getPostDetail(Long postId) {
        log.info("获取帖子详情: postId={}", postId);

        CommunityPost post = communityPostRepository.selectById(postId);

        if (post == null) {
            throw new RuntimeException("帖子不存在");
        }

        // 增加浏览量
        communityPostRepository.update(null,
                new LambdaUpdateWrapper<CommunityPost>()
                        .eq(CommunityPost::getId, postId)
                        .setSql("view_count = view_count + 1")
        );

        PostDetailResponseDto response = new PostDetailResponseDto();
        response.setPost(convertToPostDto(post));

        return response;
    }

    /**
     * 创建帖子
     */
    public PostCreateResponseDto createPost(Long authorId, String title, String content, String category, String tags) {
        log.info("创建帖子: authorId={}, title={}", authorId, title);

        CommunityPost post = new CommunityPost();
        post.setTitle(title);
        post.setContent(content);
        post.setAuthorId(authorId);
        post.setCategory(category);
        post.setTags(tags);
        post.setStatus(CommunityPost.PostStatus.PUBLISHED);

        communityPostRepository.insert(post);

        PostCreateResponseDto response = new PostCreateResponseDto();
        response.setPostId(post.getId());

        return response;
    }

    /**
     * 获取帖子评论
     */
    public CommentListResponseDto getPostComments(Long postId, Integer page, Integer size) {
        log.info("获取帖子评论: postId={}, page={}, size={}", postId, page, size);

        Page<CommunityComment> pageRequest = new Page<>(page, size);

        LambdaQueryWrapper<CommunityComment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CommunityComment::getPostId, postId)
                .eq(CommunityComment::getStatus, CommunityComment.CommentStatus.PUBLISHED)
                .orderByAsc(CommunityComment::getCreateTime);

        Page<CommunityComment> result = communityCommentRepository.selectPage(pageRequest, queryWrapper);

        CommentListResponseDto response = new CommentListResponseDto();
        response.setData(result.getRecords().stream()
                .map(this::convertToCommentDto)
                .collect(Collectors.toList()));
        response.setTotal(result.getTotal());
        response.setPages(result.getPages());
        response.setCurrent(result.getCurrent());
        response.setSize(result.getSize());

        return response;
    }

    /**
     * 添加评论
     */
    public CommentCreateResponseDto addComment(Long postId, Long authorId, String content, Long parentId) {
        log.info("添加评论: postId={}, authorId={}", postId, authorId);

        CommunityComment comment = new CommunityComment();
        comment.setPostId(postId);
        comment.setAuthorId(authorId);
        comment.setContent(content);
        comment.setParentId(parentId);
        comment.setStatus(CommunityComment.CommentStatus.PUBLISHED);

        communityCommentRepository.insert(comment);

        // 更新帖子评论数
        communityPostRepository.update(null,
                new LambdaUpdateWrapper<CommunityPost>()
                        .eq(CommunityPost::getId, postId)
                        .setSql("comment_count = comment_count + 1")
        );

        CommentCreateResponseDto response = new CommentCreateResponseDto();
        response.setCommentId(comment.getId());

        return response;
    }

    /**
     * 点赞帖子
     */
    public void likePost(Long postId, Long userId) {
        log.info("点赞帖子: postId={}, userId={}", postId, userId);

        // 增加点赞数
        communityPostRepository.update(null,
                new LambdaUpdateWrapper<CommunityPost>()
                        .eq(CommunityPost::getId, postId)
                        .setSql("like_count = like_count + 1")
        );
    }

    // ==================== 管理员方法 ====================

    /**
     * 管理员获取所有帖子（包括所有状态）
     */
    public PostListResponseDto getAdminPosts(int page, int size, String category, String status, String keyword, String sort) {
        log.info("管理员获取帖子列表: page={}, size={}, category={}, status={}, keyword={}, sort={}", 
                 page, size, category, status, keyword, sort);

        Page<CommunityPost> pageRequest = new Page<>(page, size);

        LambdaQueryWrapper<CommunityPost> queryWrapper = new LambdaQueryWrapper<>();
        
        // 管理员可以看到所有状态的帖子，但可以按状态筛选
        if (status != null && !status.isEmpty()) {
            try {
                CommunityPost.PostStatus postStatus = CommunityPost.PostStatus.valueOf(status.toUpperCase());
                queryWrapper.eq(CommunityPost::getStatus, postStatus);
            } catch (IllegalArgumentException e) {
                log.warn("无效的状态值: {}", status);
            }
        }

        if (category != null && !category.isEmpty()) {
            queryWrapper.eq(CommunityPost::getCategory, category);
        }

        // 关键词搜索（搜索标题和内容）
        if (keyword != null && !keyword.isEmpty()) {
            queryWrapper.and(wrapper -> wrapper
                .like(CommunityPost::getTitle, keyword)
                .or()
                .like(CommunityPost::getContent, keyword)
            );
        }

        // 排序
        if ("hot".equals(sort)) {
            queryWrapper.orderByDesc(CommunityPost::getLikeCount);
        } else if ("latest".equals(sort)) {
            queryWrapper.orderByDesc(CommunityPost::getCreateTime);
        } else {
            // 默认：置顶优先，然后按创建时间倒序
            queryWrapper.orderByDesc(CommunityPost::getIsTop)
                       .orderByDesc(CommunityPost::getCreateTime);
        }

        Page<CommunityPost> result = communityPostRepository.selectPage(pageRequest, queryWrapper);

        PostListResponseDto response = new PostListResponseDto();
        response.setData(result.getRecords().stream()
                .map(this::convertToPostDto)
                .collect(Collectors.toList()));
        response.setTotal(result.getTotal());
        response.setPages(result.getPages());
        response.setCurrent(result.getCurrent());
        response.setSize(result.getSize());

        return response;
    }

    /**
     * 审核通过帖子
     */
    public void approvePost(Long postId) {
        log.info("审核通过帖子: postId={}", postId);
        
        CommunityPost post = communityPostRepository.selectById(postId);
        if (post == null) {
            throw new RuntimeException("帖子不存在");
        }
        
        post.setStatus(CommunityPost.PostStatus.PUBLISHED);
        communityPostRepository.updateById(post);
    }

    /**
     * 拒绝帖子发布
     */
    public void rejectPost(Long postId, String reason) {
        log.info("拒绝帖子发布: postId={}, reason={}", postId, reason);
        
        CommunityPost post = communityPostRepository.selectById(postId);
        if (post == null) {
            throw new RuntimeException("帖子不存在");
        }
        
        post.setStatus(CommunityPost.PostStatus.REJECTED);
        communityPostRepository.updateById(post);
    }

    /**
     * 置顶/取消置顶帖子
     */
    public void toggleTopPost(Long postId, Boolean isTop) {
        log.info("置顶帖子: postId={}, isTop={}", postId, isTop);
        
        CommunityPost post = communityPostRepository.selectById(postId);
        if (post == null) {
            throw new RuntimeException("帖子不存在");
        }
        
        post.setIsTop(isTop);
        communityPostRepository.updateById(post);
    }

    /**
     * 切换帖子可见性
     */
    public void togglePostVisibility(Long postId, Boolean isHidden) {
        log.info("切换帖子可见性: postId={}, isHidden={}", postId, isHidden);
        
        CommunityPost post = communityPostRepository.selectById(postId);
        if (post == null) {
            throw new RuntimeException("帖子不存在");
        }
        
        if (isHidden) {
            post.setStatus(CommunityPost.PostStatus.HIDDEN);
        } else {
            post.setStatus(CommunityPost.PostStatus.PUBLISHED);
        }
        communityPostRepository.updateById(post);
    }

    /**
     * 删除帖子
     */
    public void deletePost(Long postId) {
        log.info("删除帖子: postId={}", postId);
        
        CommunityPost post = communityPostRepository.selectById(postId);
        if (post == null) {
            throw new RuntimeException("帖子不存在");
        }
        
        communityPostRepository.deleteById(postId);
    }

    /**
     * 转换帖子实体为DTO
     */
    private PostDto convertToPostDto(CommunityPost post) {
        PostDto dto = new PostDto();
        BeanUtils.copyProperties(post, dto);
        dto.setStatus(post.getStatus().name());
        return dto;
    }

    /**
     * 转换评论实体为DTO
     */
    private CommentDto convertToCommentDto(CommunityComment comment) {
        CommentDto dto = new CommentDto();
        BeanUtils.copyProperties(comment, dto);
        dto.setStatus(comment.getStatus().name());
        return dto;
    }
}