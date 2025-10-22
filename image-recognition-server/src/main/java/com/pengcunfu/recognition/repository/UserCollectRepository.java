package com.pengcunfu.recognition.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pengcunfu.recognition.entity.UserCollect;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 用户收藏Repository
 */
@Mapper
public interface UserCollectRepository extends BaseMapper<UserCollect> {

    /**
     * 查询用户是否收藏
     */
    @Select("""
            SELECT * FROM user_collects
            WHERE user_id = #{userId} AND target_id = #{targetId} AND target_type = #{targetType}
            LIMIT 1
            """)
    UserCollect findByUserAndTarget(
            @Param("userId") Long userId,
            @Param("targetId") Long targetId,
            @Param("targetType") Integer targetType
    );

    /**
     * 删除收藏记录
     */
    @Delete("""
            DELETE FROM user_collects
            WHERE user_id = #{userId} AND target_id = #{targetId} AND target_type = #{targetType}
            """)
    int deleteByUserAndTarget(
            @Param("userId") Long userId,
            @Param("targetId") Long targetId,
            @Param("targetType") Integer targetType
    );

    /**
     * 统计用户收藏数
     */
    @Select("""
            SELECT COUNT(*) FROM user_collects
            WHERE user_id = #{userId}
            """)
    long countByUserId(@Param("userId") Long userId);

    /**
     * 获取用户收藏的识别结果
     */
    @Select("""
            SELECT rr.id, rr.main_category as title, rr.description, rr.image_url as imageUrl,
                   rr.confidence, rr.created_at as createdAt
            FROM user_collects uc
            JOIN recognition_results rr ON uc.target_id = rr.id
            WHERE uc.user_id = #{userId} AND uc.target_type = 0
            ORDER BY uc.created_at DESC
            LIMIT #{size} OFFSET #{offset}
            """)
    java.util.List<java.util.Map<String, Object>> findRecognitionsByUserId(
            @Param("userId") Long userId,
            @Param("page") Integer page,
            @Param("size") Integer size,
            @Param("offset") Integer offset
    );

    /**
     * 获取用户收藏的帖子
     */
    @Select("""
            SELECT cp.id, cp.title, cp.content, cp.images,
                   cp.like_count as likeCount, cp.view_count as viewCount, cp.created_at as createdAt
            FROM user_collects uc
            JOIN community_posts cp ON uc.target_id = cp.id
            WHERE uc.user_id = #{userId} AND uc.target_type = 1
            ORDER BY uc.created_at DESC
            LIMIT #{size} OFFSET #{offset}
            """)
    java.util.List<java.util.Map<String, Object>> findPostsByUserId(
            @Param("userId") Long userId,
            @Param("page") Integer page,
            @Param("size") Integer size,
            @Param("offset") Integer offset
    );

    /**
     * 获取用户收藏的知识
     */
    @Select("""
            SELECT k.id, k.title, k.content as description, k.cover_image as coverImage,
                   k.like_count as likeCount, k.view_count as viewCount, k.created_at as createdAt
            FROM user_collects uc
            JOIN knowledge k ON uc.target_id = k.id
            WHERE uc.user_id = #{userId} AND uc.target_type = 2
            ORDER BY uc.created_at DESC
            LIMIT #{size} OFFSET #{offset}
            """)
    java.util.List<java.util.Map<String, Object>> findKnowledgeByUserId(
            @Param("userId") Long userId,
            @Param("page") Integer page,
            @Param("size") Integer size,
            @Param("offset") Integer offset
    );
}

