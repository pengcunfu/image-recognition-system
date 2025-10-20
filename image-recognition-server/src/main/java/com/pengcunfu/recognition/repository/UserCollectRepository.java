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
}

