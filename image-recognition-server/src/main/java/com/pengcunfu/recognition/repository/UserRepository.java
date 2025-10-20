package com.pengcunfu.recognition.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pengcunfu.recognition.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;

/**
 * 用户Repository
 */
@Mapper
public interface UserRepository extends BaseMapper<User> {

    /**
     * 根据用户名或邮箱查询用户
     */
    @Select("""
            SELECT * FROM users
            WHERE username = #{usernameOrEmail} OR email = #{usernameOrEmail}
            LIMIT 1
            """)
    User findByUsernameOrEmail(@Param("usernameOrEmail") String usernameOrEmail);

    /**
     * 根据用户名查询用户
     */
    @Select("""
            SELECT * FROM users
            WHERE username = #{username}
            LIMIT 1
            """)
    User findByUsername(@Param("username") String username);

    /**
     * 根据邮箱查询用户
     */
    @Select("""
            SELECT * FROM users
            WHERE email = #{email}
            LIMIT 1
            """)
    User findByEmail(@Param("email") String email);

    /**
     * 更新最后登录时间
     */
    @Update("""
            UPDATE users
            SET last_login_time = #{lastLoginTime}, updated_at = NOW()
            WHERE id = #{userId}
            """)
    int updateLastLoginTime(@Param("userId") Long userId, @Param("lastLoginTime") String lastLoginTime);

    /**
     * 统计总用户数
     */
    @Select("SELECT COUNT(*) FROM users")
    long countTotal();

    /**
     * 统计今日新增用户数
     */
    @Select("""
            SELECT COUNT(*) FROM users
            WHERE created_at >= #{startTime}
            """)
    long countByCreatedAtAfter(@Param("startTime") LocalDateTime startTime);

    /**
     * 统计活跃用户数（最近登录）
     */
    @Select("""
            SELECT COUNT(*) FROM users
            WHERE last_login_time >= #{startTime}
            """)
    long countByLastLoginAfter(@Param("startTime") LocalDateTime startTime);

    /**
     * 统计VIP用户数
     */
    @Select("""
            SELECT COUNT(*) FROM users
            WHERE vip_expire_time IS NOT NULL
            AND vip_expire_time >= #{now}
            """)
    long countVipUsers(@Param("now") LocalDateTime now);

    /**
     * 按日期统计用户增长趋势
     */
    @Select("""
            SELECT DATE(created_at) as date, COUNT(*) as count
            FROM users
            WHERE created_at >= #{startDate}
            GROUP BY DATE(created_at)
            ORDER BY date
            """)
    java.util.List<java.util.Map<String, Object>> countUserTrendByDate(@Param("startDate") LocalDateTime startDate);

    /**
     * 管理员分页查询用户（带状态和关键词过滤）
     */
    @Select("""
            <script>
            SELECT * FROM users
            WHERE 1=1
            <if test="status != null">
                AND status = #{status}
            </if>
            <if test="keyword != null and keyword != ''">
                AND (username LIKE CONCAT('%', #{keyword}, '%') 
                OR email LIKE CONCAT('%', #{keyword}, '%') 
                OR nickname LIKE CONCAT('%', #{keyword}, '%'))
            </if>
            ORDER BY created_at DESC
            </script>
            """)
    com.baomidou.mybatisplus.extension.plugins.pagination.Page<User> findUsersForAdmin(
            com.baomidou.mybatisplus.extension.plugins.pagination.Page<User> page,
            @Param("status") Integer status,
            @Param("keyword") String keyword
    );
}
