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

    /**
     * 统计用户在指定时间后的识别次数
     */
    @Select("""
            SELECT COUNT(*) FROM recognition_results
            WHERE user_id = #{userId} AND created_at >= #{startTime}
            """)
    long countByUserIdAndCreatedAtAfter(
            @Param("userId") Long userId,
            @Param("startTime") LocalDateTime startTime
    );

    /**
     * 获取用户的平均置信度
     */
    @Select("""
            SELECT AVG(confidence) FROM recognition_results
            WHERE user_id = #{userId} AND confidence IS NOT NULL
            """)
    Double getAverageConfidenceByUserId(@Param("userId") Long userId);

    /**
     * 查询同分类的识别记录（排除指定ID）
     */
    @Select("""
            SELECT * FROM recognition_results
            WHERE user_id = #{userId}
              AND main_category = #{mainCategory}
              AND id != #{excludeId}
            ORDER BY created_at DESC
            LIMIT 3
            """)
    java.util.List<RecognitionResult> findByUserIdAndMainCategoryAndIdNotOrderByCreatedAtDesc(
            @Param("userId") Long userId,
            @Param("mainCategory") String mainCategory,
            @Param("excludeId") Long excludeId
    );

    /**
     * 按主分类统计识别数量
     */
    @Select("""
            SELECT main_category as category, COUNT(*) as count
            FROM recognition_results
            WHERE main_category IS NOT NULL AND main_category != ''
            GROUP BY main_category
            ORDER BY count DESC
            """)
    java.util.List<java.util.Map<String, Object>> countByMainCategory();

    /**
     * 获取VIP用户统计数据（指定时间段）
     */
    @Select("""
            SELECT r.*, u.role
            FROM recognition_results r
            LEFT JOIN users u ON r.user_id = u.id
            WHERE r.user_id = #{userId} 
            AND r.created_at >= #{startDate}
            AND r.status = 1
            ORDER BY r.created_at DESC
            """)
    java.util.List<java.util.Map<String, Object>> getVipUserStatsFromDate(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate
    );

    /**
     * 获取VIP用户统计数据（指定时间段范围）
     */
    @Select("""
            SELECT r.*, u.role
            FROM recognition_results r
            LEFT JOIN users u ON r.user_id = u.id
            WHERE r.user_id = #{userId} 
            AND r.created_at >= #{startDate}
            AND r.created_at < #{endDate}
            AND r.status = 1
            ORDER BY r.created_at DESC
            """)
    java.util.List<java.util.Map<String, Object>> getVipUserStats(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    /**
     * 获取VIP用户每日趋势数据
     */
    @Select("""
            SELECT 
                DATE(r.created_at) as date,
                COUNT(*) as count,
                AVG(r.confidence) as avg_confidence,
                AVG(r.processing_time) as avg_time
            FROM recognition_results r
            LEFT JOIN users u ON r.user_id = u.id
            WHERE r.user_id = #{userId} 
            AND r.created_at >= #{startDate}
            AND r.status = 1
            GROUP BY DATE(r.created_at)
            ORDER BY date DESC
            """)
    java.util.List<java.util.Map<String, Object>> getVipUserDailyTrends(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate
    );

    /**
     * 获取VIP用户分类统计数据
     */
    @Select("""
            SELECT 
                r.main_category,
                COUNT(*) as count,
                AVG(r.confidence) as avg_confidence,
                AVG(r.processing_time) as avg_time
            FROM recognition_results r
            LEFT JOIN users u ON r.user_id = u.id
            WHERE r.user_id = #{userId} 
            AND r.created_at >= #{startDate}
            AND r.status = 1
            AND r.main_category IS NOT NULL 
            AND r.main_category != ''
            GROUP BY r.main_category
            ORDER BY count DESC
            """)
    java.util.List<java.util.Map<String, Object>> getVipUserCategoryStats(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate
    );

    /**
     * 根据用户ID和识别类型统计数量
     */
    @Select("SELECT COUNT(*) FROM recognition_results WHERE user_id = #{userId} AND recognition_type = #{recognitionType}")
    Long countByUserIdAndRecognitionType(@Param("userId") Long userId, @Param("recognitionType") Integer recognitionType);

    /**
     * 根据用户ID和是否高级识别统计数量
     */
    @Select("SELECT COUNT(*) FROM recognition_results WHERE user_id = #{userId} AND is_advanced = #{isAdvanced}")
    Long countByUserIdAndIsAdvanced(@Param("userId") Long userId, @Param("isAdvanced") Integer isAdvanced);

    /**
     * 统计用户的不同分类数量
     */
    @Select("SELECT COUNT(DISTINCT main_category) FROM recognition_results WHERE user_id = #{userId} AND main_category IS NOT NULL AND main_category != ''")
    Long countDistinctCategoriesByUserId(@Param("userId") Long userId);

    /**
     * 统计用户的不同标签数量
     */
    @Select("""
            SELECT COUNT(DISTINCT tag_item) FROM (
                SELECT TRIM(SUBSTRING_INDEX(SUBSTRING_INDEX(tags, ',', numbers.n), ',', -1)) as tag_item
                FROM recognition_results
                CROSS JOIN (
                    SELECT 1 n UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5
                    UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9 UNION ALL SELECT 10
                ) numbers
                WHERE user_id = #{userId} 
                AND tags IS NOT NULL 
                AND tags != ''
                AND CHAR_LENGTH(tags) - CHAR_LENGTH(REPLACE(tags, ',', '')) >= numbers.n - 1
            ) tag_split
            WHERE tag_item != ''
            """)
    Long countDistinctTagsByUserId(@Param("userId") Long userId);
}

