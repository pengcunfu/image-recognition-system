package com.pcf.recognition.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pcf.recognition.entity.CommunityPost;
import com.pcf.recognition.entity.CommunityComment;
import com.pcf.recognition.repository.CommunityPostRepository;
import com.pcf.recognition.repository.CommunityCommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public Map<String, Object> getPosts(Integer page, Integer size, String category, String sort) {
        log.info("获取帖子列表: page={}, size={}, category={}", page, size, category);
        
        try {
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
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", result.getRecords());
            response.put("total", result.getTotal());
            response.put("pages", result.getPages());
            response.put("current", result.getCurrent());
            response.put("size", result.getSize());
            
            return response;
            
        } catch (Exception e) {
            log.error("获取帖子列表失败", e);
            return createErrorResponse("获取帖子列表失败");
        }
    }

    /**
     * 获取帖子详情
     */
    public Map<String, Object> getPostDetail(Long postId) {
        log.info("获取帖子详情: postId={}", postId);
        
        try {
            CommunityPost post = communityPostRepository.selectById(postId);
            
            if (post == null) {
                return createErrorResponse("帖子不存在");
            }
            
            // 增加浏览量
            communityPostRepository.update(null,
                new LambdaUpdateWrapper<CommunityPost>()
                    .eq(CommunityPost::getId, postId)
                    .setSql("view_count = view_count + 1")
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", post);
            
            return response;
            
        } catch (Exception e) {
            log.error("获取帖子详情失败", e);
            return createErrorResponse("获取帖子详情失败");
        }
    }

    /**
     * 创建帖子
     */
    public Map<String, Object> createPost(Long authorId, String title, String content, String category, String tags) {
        log.info("创建帖子: authorId={}, title={}", authorId, title);
        
        try {
            CommunityPost post = new CommunityPost();
            post.setTitle(title);
            post.setContent(content);
            post.setAuthorId(authorId);
            post.setCategory(category);
            post.setTags(tags);
            post.setStatus(CommunityPost.PostStatus.PUBLISHED);
            
            communityPostRepository.insert(post);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "帖子发布成功");
            response.put("postId", post.getId());
            
            return response;
            
        } catch (Exception e) {
            log.error("创建帖子失败", e);
            return createErrorResponse("发布帖子失败");
        }
    }

    /**
     * 获取帖子评论
     */
    public Map<String, Object> getPostComments(Long postId, Integer page, Integer size) {
        log.info("获取帖子评论: postId={}, page={}, size={}", postId, page, size);
        
        try {
            Page<CommunityComment> pageRequest = new Page<>(page, size);
            
            LambdaQueryWrapper<CommunityComment> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(CommunityComment::getPostId, postId)
                      .eq(CommunityComment::getStatus, CommunityComment.CommentStatus.PUBLISHED)
                      .orderByAsc(CommunityComment::getCreateTime);
            
            Page<CommunityComment> result = communityCommentRepository.selectPage(pageRequest, queryWrapper);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", result.getRecords());
            response.put("total", result.getTotal());
            response.put("pages", result.getPages());
            response.put("current", result.getCurrent());
            response.put("size", result.getSize());
            
            return response;
            
        } catch (Exception e) {
            log.error("获取帖子评论失败", e);
            return createErrorResponse("获取评论失败");
        }
    }

    /**
     * 添加评论
     */
    public Map<String, Object> addComment(Long postId, Long authorId, String content, Long parentId) {
        log.info("添加评论: postId={}, authorId={}", postId, authorId);
        
        try {
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
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "评论发布成功");
            response.put("commentId", comment.getId());
            
            return response;
            
        } catch (Exception e) {
            log.error("添加评论失败", e);
            return createErrorResponse("发布评论失败");
        }
    }

    /**
     * 点赞帖子
     */
    public Map<String, Object> likePost(Long postId, Long userId) {
        log.info("点赞帖子: postId={}, userId={}", postId, userId);
        
        try {
            // 增加点赞数
            communityPostRepository.update(null,
                new LambdaUpdateWrapper<CommunityPost>()
                    .eq(CommunityPost::getId, postId)
                    .setSql("like_count = like_count + 1")
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "点赞成功");
            
            return response;
            
        } catch (Exception e) {
            log.error("点赞帖子失败", e);
            return createErrorResponse("点赞失败");
        }
    }
    
    /**
     * 创建错误响应
     */
    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        return response;
    }
}