package com.pcf.recognition.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pcf.recognition.dto.CommunityDto.*;
import com.pcf.recognition.entity.CommunityPost;
import com.pcf.recognition.entity.CommunityComment;
import com.pcf.recognition.entity.User;
import com.pcf.recognition.repository.CommunityPostRepository;
import com.pcf.recognition.repository.CommunityCommentRepository;
import com.pcf.recognition.repository.UserRepository;
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
    private final UserRepository userRepository;

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
    public PostListResponseDto getAdminPosts(int page, int size, String category, Integer status, String keyword, int sort) {
        log.info("管理员获取帖子列表: page={}, size={}, category={}, status={}, keyword={}, sort={}", 
                 page, size, category, status, keyword, sort);

        Page<CommunityPost> pageRequest = new Page<>(page, size);

        LambdaQueryWrapper<CommunityPost> queryWrapper = new LambdaQueryWrapper<>();
        
        // 管理员可以看到所有状态的帖子，但可以按状态筛选
        if (status != null) {
            try {
                CommunityPost.PostStatus postStatus = CommunityPost.PostStatus.fromValue(status);
                log.info("添加状态筛选条件: status = {} ({})", status, postStatus);
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
            queryWrapper.and(wrapper -> 
                wrapper.like(CommunityPost::getTitle, keyword)
                       .or()
                       .like(CommunityPost::getContent, keyword)
            );
        }

        // 排序
        CommunityPost.SortType sortType = CommunityPost.SortType.fromValue(sort);
        switch (sortType) {
            case HOT:
                queryWrapper.orderByDesc(CommunityPost::getLikeCount);
                break;
            case TOP_FIRST:
                queryWrapper.orderByDesc(CommunityPost::getIsTop)
                           .orderByDesc(CommunityPost::getCreateTime);
                break;
            case LATEST:
            default:
                queryWrapper.orderByDesc(CommunityPost::getCreateTime);
                break;
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
        
        // 获取作者信息
        if (post.getAuthorId() != null) {
            User author = userRepository.selectById(post.getAuthorId());
            if (author != null) {
                dto.setAuthorName(author.getName() != null ? author.getName() : author.getUsername());
                dto.setAuthorAvatar(author.getAvatar() != null ? author.getAvatar() : "/api/v1/files/default-avatar.png");
            }
        }
        
        return dto;
    }

    /**
     * 转换评论实体为DTO
     */
    private CommentDto convertToCommentDto(CommunityComment comment) {
        CommentDto dto = new CommentDto();
        BeanUtils.copyProperties(comment, dto);
        dto.setStatus(comment.getStatus().name());
        
        // 获取评论者信息
        if (comment.getAuthorId() != null) {
            User author = userRepository.selectById(comment.getAuthorId());
            if (author != null) {
                dto.setAuthorName(author.getName() != null ? author.getName() : author.getUsername());
                dto.setAuthorAvatar(author.getAvatar() != null ? author.getAvatar() : "/api/v1/files/default-avatar.png");
            }
        }
        
        return dto;
    }

    /**
     * 收藏帖子
     */
    public void collectPost(Long postId, Long userId) {
        log.info("收藏帖子: postId={}, userId={}", postId, userId);
        
        // 检查帖子是否存在
        CommunityPost post = communityPostRepository.selectById(postId);
        if (post == null) {
            throw new RuntimeException("帖子不存在");
        }
        
        // TODO: 实现收藏逻辑（需要创建收藏表）
        // 暂时只记录日志
        log.info("用户 {} 收藏了帖子 {}", userId, postId);
    }

    /**
     * 取消收藏帖子
     */
    public void uncollectPost(Long postId, Long userId) {
        log.info("取消收藏帖子: postId={}, userId={}", postId, userId);
        
        // TODO: 实现取消收藏逻辑（需要创建收藏表）
        // 暂时只记录日志
        log.info("用户 {} 取消收藏了帖子 {}", userId, postId);
    }

    /**
     * 举报帖子
     */
    public void reportPost(Long postId, Long userId, String type, String description) {
        log.info("举报帖子: postId={}, userId={}, type={}, description={}", postId, userId, type, description);
        
        // 检查帖子是否存在
        CommunityPost post = communityPostRepository.selectById(postId);
        if (post == null) {
            throw new RuntimeException("帖子不存在");
        }
        
        // TODO: 实现举报逻辑（需要创建举报表）
        // 暂时只记录日志
        log.warn("用户 {} 举报了帖子 {}，类型：{}，描述：{}", userId, postId, type, description);
    }

    /**
     * 获取相关推荐帖子
     */
    public java.util.List<PostDto> getRelatedPosts(Long postId, int limit) {
        log.info("获取相关推荐帖子: postId={}, limit={}", postId, limit);
        
        // 获取当前帖子
        CommunityPost currentPost = communityPostRepository.selectById(postId);
        if (currentPost == null) {
            throw new RuntimeException("帖子不存在");
        }
        
        // 查询相同分类的其他已发布帖子
        LambdaQueryWrapper<CommunityPost> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CommunityPost::getStatus, CommunityPost.PostStatus.PUBLISHED)
                    .ne(CommunityPost::getId, postId); // 排除当前帖子
        
        // 如果有分类，优先推荐同分类的
        if (currentPost.getCategory() != null && !currentPost.getCategory().isEmpty()) {
            queryWrapper.eq(CommunityPost::getCategory, currentPost.getCategory());
        }
        
        // 按点赞数排序，获取热门帖子
        queryWrapper.orderByDesc(CommunityPost::getLikeCount)
                    .orderByDesc(CommunityPost::getCreateTime)
                    .last("LIMIT " + limit);
        
        java.util.List<CommunityPost> relatedPosts = communityPostRepository.selectList(queryWrapper);
        
        return relatedPosts.stream()
                .map(this::convertToPostDto)
                .collect(Collectors.toList());
    }
}