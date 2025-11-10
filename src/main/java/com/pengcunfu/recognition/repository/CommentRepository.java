package com.pengcunfu.recognition.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pengcunfu.recognition.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 评论Repository
 */
@Mapper
public interface CommentRepository extends BaseMapper<Comment> {

    /**
     * 分页查询评论列表（一级评论）
     */
    @Select("""
            SELECT * FROM comments
            WHERE target_type = #{targetType} AND target_id = #{targetId}
            AND status = #{status} AND parent_id IS NULL
            ORDER BY created_at DESC
            """)
    Page<Comment> findCommentsByTarget(
            Page<Comment> page,
            @Param("targetType") Integer targetType,
            @Param("targetId") Long targetId,
            @Param("status") Integer status
    );

    /**
     * 分页查询回复列表
     */
    @Select("""
            SELECT * FROM comments
            WHERE parent_id = #{parentId} AND status = #{status}
            ORDER BY created_at ASC
            """)
    Page<Comment> findRepliesByParent(
            Page<Comment> page,
            @Param("parentId") Long parentId,
            @Param("status") Integer status
    );

    /**
     * 增加点赞数
     */
    @Update("""
            UPDATE comments SET like_count = like_count + 1
            WHERE id = #{commentId}
            """)
    int incrementLikeCount(@Param("commentId") Long commentId);

    /**
     * 减少点赞数
     */
    @Update("""
            UPDATE comments SET like_count = like_count - 1
            WHERE id = #{commentId} AND like_count > 0
            """)
    int decrementLikeCount(@Param("commentId") Long commentId);

    /**
     * 统计用户发布的评论数
     */
    @Select("""
            SELECT COUNT(*) FROM comments
            WHERE user_id = #{userId}
            """)
    long countByUserId(@Param("userId") Long userId);
}

