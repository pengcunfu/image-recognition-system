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
     * 获取用户未读通知数量
     */
    @Select("SELECT COUNT(*) FROM notifications WHERE user_id = #{userId} AND is_read = 0")
    Long countUnread(@Param("userId") Long userId);

    /**
     * 分页查询用户通知
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
    Page<Notification> findByUserId(
            Page<Notification> page,
            @Param("userId") Long userId,
            @Param("isRead") Integer isRead
    );

    /**
     * 标记所有通知为已读
     */
    @Update("UPDATE notifications SET is_read = 1, read_time = NOW() WHERE user_id = #{userId} AND is_read = 0")
    int markAllAsRead(@Param("userId") Long userId);

    /**
     * 删除用户所有通知
     */
    @Update("DELETE FROM notifications WHERE user_id = #{userId}")
    int deleteAllByUserId(@Param("userId") Long userId);
}
