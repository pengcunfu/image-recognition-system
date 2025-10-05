package com.pcf.recognition.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pcf.recognition.entity.RecognitionHistory;
import org.apache.ibatis.annotations.Mapper;

/**
 * 识别历史Repository接口 - 基于MyBatis Plus
 */
@Mapper
public interface RecognitionHistoryRepository extends BaseMapper<RecognitionHistory> {
    // BaseMapper提供所有基础CRUD操作
}
