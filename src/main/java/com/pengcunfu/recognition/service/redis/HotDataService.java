package com.pengcunfu.recognition.service.redis;

import com.pengcunfu.recognition.constant.RedisConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 热点数据服务
 * 管理热门帖子、热门知识等热点数据
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HotDataService {

    private final RedisService redisService;

    /**
     * 增加帖子浏览次数
     */
    public void incrementPostViewCount(Long postId) {
        String key = RedisConstants.POST_VIEW_COUNT_KEY + postId;
        redisService.increment(key);
    }

    /**
     * 获取帖子浏览次数
     */
    public Long getPostViewCount(Long postId) {
        String key = RedisConstants.POST_VIEW_COUNT_KEY + postId;
        Object count = redisService.get(key);
        return count != null ? Long.valueOf(count.toString()) : 0L;
    }

    /**
     * 添加热门帖子
     */
    public void addHotPost(Long postId, double score) {
        redisService.zAdd(RedisConstants.HOT_POSTS_KEY, postId, score);
        log.debug("添加热门帖子: postId={}, score={}", postId, score);
    }

    /**
     * 获取热门帖子列表
     */
    public Set<Object> getHotPosts(int limit) {
        return redisService.zReverseRange(RedisConstants.HOT_POSTS_KEY, 0, limit - 1);
    }

    /**
     * 移除热门帖子
     */
    public void removeHotPost(Long postId) {
        redisService.zRemove(RedisConstants.HOT_POSTS_KEY, postId);
        log.debug("移除热门帖子: postId={}", postId);
    }

    /**
     * 添加热门知识
     */
    public void addHotKnowledge(Long knowledgeId, double score) {
        redisService.zAdd(RedisConstants.HOT_KNOWLEDGE_KEY, knowledgeId, score);
        log.debug("添加热门知识: knowledgeId={}, score={}", knowledgeId, score);
    }

    /**
     * 获取热门知识列表
     */
    public Set<Object> getHotKnowledge(int limit) {
        return redisService.zReverseRange(RedisConstants.HOT_KNOWLEDGE_KEY, 0, limit - 1);
    }

    /**
     * 移除热门知识
     */
    public void removeHotKnowledge(Long knowledgeId) {
        redisService.zRemove(RedisConstants.HOT_KNOWLEDGE_KEY, knowledgeId);
        log.debug("移除热门知识: knowledgeId={}", knowledgeId);
    }

    /**
     * 缓存帖子信息
     */
    public void cachePostInfo(Long postId, Object postInfo) {
        String key = RedisConstants.POST_INFO_KEY + postId;
        redisService.set(key, postInfo, RedisConstants.DEFAULT_EXPIRE_TIME, TimeUnit.SECONDS);
        log.debug("缓存帖子信息: postId={}", postId);
    }

    /**
     * 获取缓存的帖子信息
     */
    public Object getCachedPostInfo(Long postId) {
        String key = RedisConstants.POST_INFO_KEY + postId;
        return redisService.get(key);
    }

    /**
     * 删除缓存的帖子信息
     */
    public void removeCachedPostInfo(Long postId) {
        String key = RedisConstants.POST_INFO_KEY + postId;
        redisService.delete(key);
        log.debug("删除缓存帖子信息: postId={}", postId);
    }

    /**
     * 缓存知识信息
     */
    public void cacheKnowledgeInfo(Long knowledgeId, Object knowledgeInfo) {
        String key = RedisConstants.KNOWLEDGE_INFO_KEY + knowledgeId;
        redisService.set(key, knowledgeInfo, RedisConstants.DEFAULT_EXPIRE_TIME, TimeUnit.SECONDS);
        log.debug("缓存知识信息: knowledgeId={}", knowledgeId);
    }

    /**
     * 获取缓存的知识信息
     */
    public Object getCachedKnowledgeInfo(Long knowledgeId) {
        String key = RedisConstants.KNOWLEDGE_INFO_KEY + knowledgeId;
        return redisService.get(key);
    }

    /**
     * 删除缓存的知识信息
     */
    public void removeCachedKnowledgeInfo(Long knowledgeId) {
        String key = RedisConstants.KNOWLEDGE_INFO_KEY + knowledgeId;
        redisService.delete(key);
        log.debug("删除缓存知识信息: knowledgeId={}", knowledgeId);
    }

    /**
     * 刷新热点数据
     */
    public void refreshHotData() {
        // 清除旧的热点数据
        redisService.delete(RedisConstants.HOT_POSTS_KEY);
        redisService.delete(RedisConstants.HOT_KNOWLEDGE_KEY);
        log.info("热点数据已刷新");
    }
}

