package com.pcf.recognition.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pcf.recognition.entity.BatchRecognition;
import org.apache.ibatis.annotations.Mapper;

/**
 * 批量识别Repository接口 - 基于MyBatis Plus
 */
@Mapper
public interface BatchRecognitionRepository extends BaseMapper<BatchRecognition> {
    // BaseMapper提供所有基础CRUD操作
}
