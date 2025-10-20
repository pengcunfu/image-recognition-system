package com.pengcunfu.recognition.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pengcunfu.recognition.entity.Notification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 通知Repository
 */
@Mapper
public interface NotificationRepository extends BaseMapper<Notification> {

    /**
     * 分页查询用户通知（带已读状态过滤）
     */
    @Select("""
            <script>
            SELECT * FROM notifications
            WHERE user_id = #{userId}
            <if test="isRead != null">
                AND is_read = #{isRead}
            </if>
            ORDER BY created_at DESC
            </script>
            """)
    Page<Notification> findByUserIdAndReadStatus(
            Page<Notification> page,
            @Param("userId") Long userId,
            @Param("isRead") Integer isRead
    );

    /**
     * 统计未读通知数量
     */
    @Select("""
            SELECT COUNT(*) FROM notifications
            WHERE user_id = #{userId} AND is_read = 0
            """)
    long countUnreadByUserId(@Param("userId") Long userId);

    /**
     * 批量标记用户所有未读通知为已读
     */
    @Update("""
            UPDATE notifications
            SET is_read = 1, read_time = #{readTime}
            WHERE user_id = #{userId} AND is_read = 0
            """)
    int markAllAsReadByUserId(
            @Param("userId") Long userId,
            @Param("readTime") String readTime
    );

    /**
     * 查询用户的通知（验证权限）
     */
    @Select("""
            SELECT * FROM notifications
            WHERE id = #{notificationId} AND user_id = #{userId}
            LIMIT 1
            """)
    Notification findByIdAndUserId(
            @Param("notificationId") Long notificationId,
            @Param("userId") Long userId
    );
}

