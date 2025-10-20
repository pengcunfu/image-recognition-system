package com.pengcunfu.recognition.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pengcunfu.recognition.entity.RecognitionResult;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.time.LocalDateTime;
/**
 * 识别结果Repository
 */
@Mapper
public interface RecognitionResultRepository extends BaseMapper<RecognitionResult> {

    /**
     * 分页查询用户识别历史
     */
    @Select("""
            SELECT * FROM recognition_results
            WHERE user_id = #{userId}
            ORDER BY created_at DESC
            """)
    Page<RecognitionResult> findByUserId(
            Page<RecognitionResult> page,
            @Param("userId") Long userId
    );

    /**
     * 查询用户的识别记录
     */
    @Select("""
            SELECT * FROM recognition_results
            WHERE id = #{resultId} AND user_id = #{userId}
            LIMIT 1
            """)
    RecognitionResult findByIdAndUserId(
            @Param("resultId") Long resultId,
            @Param("userId") Long userId
    );

    /**
     * 统计总识别数
     */
    @Select("SELECT COUNT(*) FROM recognition_results")
    long countTotal();

    /**
     * 统计今日识别数
     */
    @Select("""
            SELECT COUNT(*) FROM recognition_results
            WHERE created_at >= #{startTime}
            """)
    long countByCreatedAtAfter(@Param("startTime") LocalDateTime startTime);

    /**
     * 按日期统计识别趋势
     */
    @Select("""
            SELECT DATE(created_at) as date, COUNT(*) as count
            FROM recognition_results
            WHERE created_at >= #{startDate}
            GROUP BY DATE(created_at)
            ORDER BY date
            """)
    java.util.List<java.util.Map<String, Object>> countRecognitionTrendByDate(@Param("startDate") LocalDateTime startDate);

    /**
     * 统计热门分类
     */
    @Select("""
            SELECT main_category as category, COUNT(*) as count
            FROM recognition_results
            WHERE main_category IS NOT NULL AND main_category != ''
            GROUP BY main_category
            ORDER BY count DESC
            LIMIT #{limit}
            """)
    java.util.List<java.util.Map<String, Object>> countTopCategories(@Param("limit") int limit);

    /**
     * 统计用户识别次数
     */
    @Select("""
            SELECT COUNT(*) FROM recognition_results
            WHERE user_id = #{userId}
            """)
    long countByUserId(@Param("userId") Long userId);
}

