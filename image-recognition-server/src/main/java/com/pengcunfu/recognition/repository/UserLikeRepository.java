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
}

