package com.pengcunfu.recognition.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pengcunfu.recognition.entity.Knowledge;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 知识Repository
 */
@Mapper
public interface KnowledgeRepository extends BaseMapper<Knowledge> {

    /**
     * 统计总知识数
     */
    @Select("SELECT COUNT(*) FROM knowledge")
    long countTotal();

    /**
     * 管理员分页查询知识（带状态和关键词过滤）
     */
    @Select("""
            <script>
            SELECT * FROM knowledge
            WHERE 1=1
            <if test="status != null">
                AND status = #{status}
            </if>
            <if test="category != null and category != ''">
                AND category = #{category}
            </if>
            <if test="tag != null and tag != ''">
                AND tags LIKE CONCAT('%', #{tag}, '%')
            </if>
            <if test="keyword != null and keyword != ''">
                AND (title LIKE CONCAT('%', #{keyword}, '%') OR content LIKE CONCAT('%', #{keyword}, '%'))
            </if>
            ORDER BY created_at DESC
            </script>
            """)
    Page<Knowledge> findKnowledgeForAdmin(
            Page<Knowledge> page,
            @Param("status") Integer status,
            @Param("category") String category,
            @Param("tag") String tag,
            @Param("keyword") String keyword
    );

    /**
     * 获取所有不同的分类
     */
    @Select("SELECT DISTINCT category FROM knowledge WHERE category IS NOT NULL AND category != '' ORDER BY category")
    java.util.List<String> findAllCategories();

    /**
     * 获取所有标签（需要在代码中进一步处理）
     */
    @Select("SELECT DISTINCT tags FROM knowledge WHERE tags IS NOT NULL AND tags != ''")
    java.util.List<String> findAllTagsRaw();
}

