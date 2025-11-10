package com.pengcunfu.recognition.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pengcunfu.recognition.entity.SystemLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;

/**
 * 系统日志Repository
 */
@Mapper
public interface SystemLogRepository extends BaseMapper<SystemLog> {

    /**
     * 分页查询系统日志（带条件）
     */
    @Select("""
            <script>
            SELECT * FROM system_logs
            WHERE 1=1
            <if test="module != null and module != ''">
                AND module = #{module}
            </if>
            <if test="userId != null">
                AND user_id = #{userId}
            </if>
            <if test="startTime != null">
                AND created_at >= #{startTime}
            </if>
            <if test="endTime != null">
                AND created_at &lt;= #{endTime}
            </if>
            ORDER BY created_at DESC
            </script>
            """)
    Page<SystemLog> findLogsWithConditions(
            Page<SystemLog> page,
            @Param("module") String module,
            @Param("userId") Long userId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );
}

