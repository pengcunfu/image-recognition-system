package com.pcf.recognition.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pcf.recognition.entity.BatchRecognitionItem;
import org.apache.ibatis.annotations.Mapper;

/**
 * 批量识别项Repository接口 - 基于MyBatis Plus
 */
@Mapper
public interface BatchRecognitionItemRepository extends BaseMapper<BatchRecognitionItem> {
    // BaseMapper提供所有基础CRUD操作
}
