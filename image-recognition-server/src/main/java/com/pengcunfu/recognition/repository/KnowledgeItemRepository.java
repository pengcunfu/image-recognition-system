package com.pcf.recognition.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pcf.recognition.entity.KnowledgeItem;
import org.apache.ibatis.annotations.Mapper;

/**
 * 知识条目Mapper接口
 */
@Mapper
public interface KnowledgeItemRepository extends BaseMapper<KnowledgeItem> {
    // BaseMapper已提供所有基础CRUD操作，无需写任何代码！
}
