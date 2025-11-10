package com.pengcunfu.recognition.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pengcunfu.recognition.entity.UserLike;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 用户点赞Repository
 */
@Mapper
public interface UserLikeRepository extends BaseMapper<UserLike> {

    /**
     * 查询用户是否点赞
     */
    @Select("""
            SELECT * FROM user_likes
            WHERE user_id = #{userId} AND target_id = #{targetId} AND target_type = #{targetType}
            LIMIT 1
            """)
    UserLike findByUserAndTarget(
            @Param("userId") Long userId,
            @Param("targetId") Long targetId,
            @Param("targetType") Integer targetType
    );

    /**
     * 删除点赞记录
     */
    @Delete("""
            DELETE FROM user_likes
            WHERE user_id = #{userId} AND target_id = #{targetId} AND target_type = #{targetType}
            """)
    int deleteByUserAndTarget(
            @Param("userId") Long userId,
            @Param("targetId") Long targetId,
            @Param("targetType") Integer targetType
    );

    /**
     * 统计用户点赞数
     */
    @Select("""
            SELECT COUNT(*) FROM user_likes
            WHERE user_id = #{userId}
            """)
    long countByUserId(@Param("userId") Long userId);

    /**
     * 获取用户点赞的帖子
     */
    @Select("""
            SELECT cp.id, cp.title, cp.content, u.username as author,
                   cp.like_count as likeCount, cp.created_at as createdAt
            FROM user_likes ul
            JOIN community_posts cp ON ul.target_id = cp.id
            JOIN users u ON cp.user_id = u.id
            WHERE ul.user_id = #{userId} AND ul.target_type = 1
            ORDER BY ul.created_at DESC
            LIMIT #{size} OFFSET #{offset}
            """)
    java.util.List<java.util.Map<String, Object>> findPostsByUserId(
            @Param("userId") Long userId,
            @Param("page") Integer page,
            @Param("size") Integer size,
            @Param("offset") Integer offset
    );

    /**
     * 获取用户点赞的知识
     */
    @Select("""
            SELECT k.id, k.title, k.content, u.username as author,
                   k.like_count as likeCount, k.created_at as createdAt
            FROM user_likes ul
            JOIN knowledge k ON ul.target_id = k.id
            JOIN users u ON k.author_id = u.id
            WHERE ul.user_id = #{userId} AND ul.target_type = 2
            ORDER BY ul.created_at DESC
            LIMIT #{size} OFFSET #{offset}
            """)
    java.util.List<java.util.Map<String, Object>> findKnowledgeByUserId(
            @Param("userId") Long userId,
            @Param("page") Integer page,
            @Param("size") Integer size,
            @Param("offset") Integer offset
    );

    /**
     * 获取用户点赞的评论
     */
    @Select("""
            SELECT c.id, c.content, u.username as author,
                   c.like_count as likeCount, c.created_at as createdAt
            FROM user_likes ul
            JOIN comments c ON ul.target_id = c.id
            JOIN users u ON c.user_id = u.id
            WHERE ul.user_id = #{userId} AND ul.target_type = 3
            ORDER BY ul.created_at DESC
            LIMIT #{size} OFFSET #{offset}
            """)
    java.util.List<java.util.Map<String, Object>> findCommentsByUserId(
            @Param("userId") Long userId,
            @Param("page") Integer page,
            @Param("size") Integer size,
            @Param("offset") Integer offset
    );
}

