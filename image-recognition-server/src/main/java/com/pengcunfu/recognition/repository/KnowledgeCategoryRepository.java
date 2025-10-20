package com.pcf.recognition.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pcf.recognition.entity.KnowledgeCategory;
import org.apache.ibatis.annotations.Mapper;

/**
 * 知识分类Mapper接口
 */
@Mapper
public interface KnowledgeCategoryRepository extends BaseMapper<KnowledgeCategory> {
    // BaseMapper已提供所有基础CRUD操作，无需写任何代码！
}
