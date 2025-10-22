package com.pengcunfu.recognition.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 发布帖子
     */
    @Transactional
    public CommunityResponse.PostInfo createPost(Long userId, CommunityRequest.CreatePostRequest request) {
        log.info("发布帖子: userId={}, title={}", userId, request.getTitle());

        // 将标签列表转换为逗号分隔的字符串
        String tagsStr = request.getTags() != null && !request.getTags().isEmpty()
                ? String.join(",", request.getTags())
                : null;

        // 将图片列表转换为JSON数组字符串
        String imagesJson = null;
        if (request.getImages() != null && !request.getImages().isEmpty()) {
            try {
                imagesJson = objectMapper.writeValueAsString(request.getImages());
            } catch (JsonProcessingException e) {
                log.error("图片列表转换为JSON失败", e);
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "图片数据处理失败");
            }
        }

        // 创建帖子（使用 builder 模式）
        CommunityPost post = CommunityPost.builder()
                .userId(userId)
                .title(request.getTitle())
                .content(request.getContent())
                .category(request.getCategory())
                .tags(tagsStr)
                .images(imagesJson)
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
            String tagsStr = !request.getTags().isEmpty() 
                    ? String.join(",", request.getTags()) 
                    : null;
            post.setTags(tagsStr);
        }
        if (request.getImages() != null) {
            try {
                String imagesJson = !request.getImages().isEmpty() 
                        ? objectMapper.writeValueAsString(request.getImages()) 
                        : null;
                post.setImages(imagesJson);
            } catch (JsonProcessingException e) {
                log.error("图片列表转换为JSON失败", e);
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "图片数据处理失败");
            }
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
     * 创建帖子（管理员）
     */
    @Transactional
    public Long createPostAdmin(CommunityRequest.CreatePostRequest request) {
        log.info("管理员创建帖子: title={}", request.getTitle());

        // 从自定义SecurityContextHolder获取当前用户ID
        Long userId = com.pengcunfu.recognition.security.SecurityContextHolder.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "未登录或登录已过期");
        }

        // 将标签列表转换为逗号分隔的字符串
        String tagsStr = request.getTags() != null && !request.getTags().isEmpty()
                ? String.join(",", request.getTags())
                : null;

        // 将图片列表转换为JSON数组字符串
        String imagesJson = null;
        if (request.getImages() != null && !request.getImages().isEmpty()) {
            try {
                imagesJson = objectMapper.writeValueAsString(request.getImages());
            } catch (JsonProcessingException e) {
                log.error("图片列表转换为JSON失败", e);
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "图片数据处理失败");
            }
        }

        // 创建帖子
        CommunityPost post = CommunityPost.builder()
                .userId(userId)
                .title(request.getTitle())
                .content(request.getContent())
                .category(request.getCategory())
                .tags(tagsStr)
                .images(imagesJson)
                .status(request.getStatus() != null ? request.getStatus() : 1) // 默认已发布
                .isTop(0)
                .isFeatured(0)
                .viewCount(0)
                .likeCount(0)
                .commentCount(0)
                .collectCount(0)
                .build();

        communityPostRepository.insert(post);

        log.info("帖子创建成功: postId={}, userId={}", post.getId(), userId);
        return post.getId();
    }

    /**
     * 更新帖子（管理员）
     */
    @Transactional
    public void updatePostAdmin(Long postId, CommunityRequest.UpdatePostRequest request) {
        log.info("管理员更新帖子: postId={}", postId);

        CommunityPost post = communityPostRepository.selectById(postId);

        if (post == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "帖子不存在");
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
            String tagsStr = !request.getTags().isEmpty() 
                    ? String.join(",", request.getTags()) 
                    : null;
            post.setTags(tagsStr);
        }
        if (request.getImages() != null) {
            try {
                String imagesJson = !request.getImages().isEmpty() 
                        ? objectMapper.writeValueAsString(request.getImages()) 
                        : null;
                post.setImages(imagesJson);
            } catch (JsonProcessingException e) {
                log.error("图片列表转换为JSON失败", e);
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "图片数据处理失败");
            }
        }
        if (request.getStatus() != null) {
            post.setStatus(request.getStatus());
        }

        communityPostRepository.updateById(post);

        log.info("帖子更新成功: postId={}", postId);
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
     * 获取帖子分类列表
     */
    public java.util.List<CommunityResponse.CategoryInfo> getCategories() {
        log.info("获取帖子分类列表");

        // 从数据库中获取所有不同的分类及其数量
        java.util.List<CommunityResponse.CategoryInfo> categories = communityPostRepository.getCategories(PostStatus.PUBLISHED.getValue());

        log.info("获取到 {} 个分类", categories.size());
        return categories;
    }

    /**
     * 获取帖子标签列表
     */
    public java.util.List<CommunityResponse.TagInfo> getTags() {
        log.info("获取帖子标签列表");

        // 从数据库中获取所有已发布帖子的tags字段
        java.util.List<String> allTagStrings = communityPostRepository.getAllTags(PostStatus.PUBLISHED.getValue());

        // 使用Map统计每个标签的出现次数
        java.util.Map<String, Long> tagCountMap = new java.util.HashMap<>();

        for (String tagString : allTagStrings) {
            if (tagString != null && !tagString.trim().isEmpty()) {
                // 按逗号分隔标签
                String[] tags = tagString.split(",");
                for (String tag : tags) {
                    String trimmedTag = tag.trim();
                    if (!trimmedTag.isEmpty()) {
                        tagCountMap.put(trimmedTag, tagCountMap.getOrDefault(trimmedTag, 0L) + 1);
                    }
                }
            }
        }

        // 转换为TagInfo列表并按数量降序排序
        java.util.List<CommunityResponse.TagInfo> tagInfos = tagCountMap.entrySet().stream()
                .map(entry -> CommunityResponse.TagInfo.builder()
                        .name(entry.getKey())
                        .count(entry.getValue())
                        .build())
                .sorted((a, b) -> Long.compare(b.getCount(), a.getCount()))
                .collect(java.util.stream.Collectors.toList());

        log.info("获取到 {} 个标签", tagInfos.size());
        return tagInfos;
    }

    /**
     * 转换为帖子信息 DTO
     */
    private CommunityResponse.PostInfo convertToPostInfo(CommunityPost post) {
        // 获取作者信息
        User author = userRepository.selectById(post.getUserId());
        
        // 优先显示昵称,如果没有则显示用户名
        String displayName = "未知用户";
        if (author != null) {
            displayName = (author.getNickname() != null && !author.getNickname().isEmpty()) 
                ? author.getNickname() 
                : author.getUsername();
        }

        return CommunityResponse.PostInfo.builder()
                .id(post.getId())
                .userId(post.getUserId())
                .authorId(post.getUserId())
                .authorName(displayName)
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
                .build();
    }

    /**
     * 获取指定用户发布的帖子列表
     */
    public PageResponse<CommunityResponse.PostInfo> getPostsByAuthor(Long userId, Integer page, Integer size) {
        log.info("获取用户发布的帖子: userId={}, page={}, size={}", userId, page, size);

        Page<CommunityPost> pageRequest = new Page<>(page, size);
        
        // 使用SQL查询该用户发布的帖子(按创建时间倒序)
        Page<CommunityPost> pageResult = communityPostRepository.findPostsByAuthor(pageRequest, userId);

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
}
