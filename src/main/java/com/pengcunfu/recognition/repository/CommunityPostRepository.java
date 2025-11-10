package com.pengcunfu.recognition.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pengcunfu.recognition.entity.CommunityPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import java.time.LocalDateTime;
/**
 * 社区帖子Repository
 */
@Mapper
public interface CommunityPostRepository extends BaseMapper<CommunityPost> {

    /**
     * 分页查询帖子（按热度排序）
     */
    @Select("""
            SELECT * FROM community_posts
            WHERE status = #{status}
            ${categoryCondition}
            ${tagCondition}
            ORDER BY like_count DESC, view_count DESC
            """)
    Page<CommunityPost> findPostsByHot(
            Page<CommunityPost> page,
            @Param("status") Integer status,
            @Param("categoryCondition") String categoryCondition,
            @Param("tagCondition") String tagCondition
    );

    /**
     * 获取热门帖子（按访问量排序，用于热门帖子展示）
     */
    @Select("""
            SELECT * FROM community_posts
            WHERE status = #{status}
            ORDER BY view_count DESC, created_at DESC
            LIMIT #{limit}
            """)
    java.util.List<CommunityPost> findHotPostsByViews(
            @Param("status") Integer status,
            @Param("limit") Integer limit
    );

    /**
     * 分页查询帖子（按最新排序）
     */
    @Select("""
            SELECT * FROM community_posts
            WHERE status = #{status}
            ${categoryCondition}
            ${tagCondition}
            ORDER BY is_top DESC, created_at DESC
            """)
    Page<CommunityPost> findPostsByLatest(
            Page<CommunityPost> page,
            @Param("status") Integer status,
            @Param("categoryCondition") String categoryCondition,
            @Param("tagCondition") String tagCondition
    );

    /**
     * 增加浏览数
     */
    @Update("""
            UPDATE community_posts SET view_count = view_count + 1
            WHERE id = #{postId}
            """)
    int incrementViewCount(@Param("postId") Long postId);

    /**
     * 增加点赞数
     */
    @Update("""
            UPDATE community_posts SET like_count = like_count + 1
            WHERE id = #{postId}
            """)
    int incrementLikeCount(@Param("postId") Long postId);

    /**
     * 减少点赞数
     */
    @Update("""
            UPDATE community_posts SET like_count = like_count - 1
            WHERE id = #{postId} AND like_count > 0
            """)
    int decrementLikeCount(@Param("postId") Long postId);

    /**
     * 增加评论数
     */
    @Update("""
            UPDATE community_posts SET comment_count = comment_count + 1
            WHERE id = #{postId}
            """)
    int incrementCommentCount(@Param("postId") Long postId);

    /**
     * 减少评论数
     */
    @Update("""
            UPDATE community_posts SET comment_count = comment_count - 1
            WHERE id = #{postId} AND comment_count > 0
            """)
    int decrementCommentCount(@Param("postId") Long postId);

    /**
     * 统计总帖子数
     */
    @Select("SELECT COUNT(*) FROM community_posts")
    long countTotal();

    /**
     * 统计今日帖子数
     */
    @Select("""
            SELECT COUNT(*) FROM community_posts
            WHERE created_at >= #{startTime}
            """)
    long countByCreatedAtAfter(@Param("startTime") LocalDateTime startTime);

    /**
     * 统计用户发布的帖子数
     */
    @Select("""
            SELECT COUNT(*) FROM community_posts
            WHERE user_id = #{userId}
            """)
    long countByUserId(@Param("userId") Long userId);

    /**
     * 管理员分页查询帖子（带状态和关键词过滤）
     */
    @Select("""
            <script>
            SELECT * FROM community_posts
            WHERE 1=1
            <if test="status != null">
                AND status = #{status}
            </if>
            <if test="keyword != null and keyword != ''">
                AND (title LIKE CONCAT('%', #{keyword}, '%') OR content LIKE CONCAT('%', #{keyword}, '%'))
            </if>
            ORDER BY created_at DESC
            </script>
            """)
    Page<CommunityPost> findPostsForAdmin(
            Page<CommunityPost> page,
            @Param("status") Integer status,
            @Param("keyword") String keyword
    );

    /**
     * 按日期统计帖子发布趋势
     */
    @Select("""
            SELECT DATE(created_at) as date, COUNT(*) as count
            FROM community_posts
            WHERE created_at >= #{startDate}
            GROUP BY DATE(created_at)
            ORDER BY date
            """)
    java.util.List<java.util.Map<String, Object>> countPostTrendByDate(@Param("startDate") LocalDateTime startDate);

    /**
     * 获取所有分类及其帖子数量
     */
    @Select("""
            SELECT 
                category as name,
                COUNT(*) as count,
                '' as description
            FROM community_posts
            WHERE status = #{status}
            AND category IS NOT NULL
            AND category != ''
            GROUP BY category
            ORDER BY count DESC
            """)
    java.util.List<com.pengcunfu.recognition.response.CommunityResponse.CategoryInfo> getCategories(@Param("status") Integer status);

    /**
     * 获取所有标签(需要在Java层面处理,因为tags是逗号分隔的字符串)
     * 这里先查询所有已发布帖子的tags字段
     */
    @Select("""
            SELECT tags
            FROM community_posts
            WHERE status = #{status}
            AND tags IS NOT NULL
            AND tags != ''
            """)
    java.util.List<String> getAllTags(@Param("status") Integer status);

    /**
     * 分页查询指定用户发布的帖子(按创建时间倒序)
     */
    @Select("""
            SELECT * FROM community_posts
            WHERE user_id = #{userId}
            ORDER BY created_at DESC
            """)
    Page<CommunityPost> findPostsByAuthor(
            Page<CommunityPost> page,
            @Param("userId") Long userId
    );

    /**
     * 查询同分类的相关帖子（排除当前帖子，按创建时间倒序）
     */
    @Select("""
            SELECT * FROM community_posts
            WHERE category = #{category}
            AND id != #{excludeId}
            AND status = #{status}
            ORDER BY created_at DESC
            LIMIT 3
            """)
    java.util.List<CommunityPost> findRelatedPostsByCategory(
            @Param("category") String category,
            @Param("excludeId") Long excludeId,
            @Param("status") Integer status
    );
}

