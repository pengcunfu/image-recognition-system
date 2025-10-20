package com.pcf.recognition.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pcf.recognition.entity.CommunityComment;
import org.apache.ibatis.annotations.Mapper;

/**
 * 社区评论Repository接口 - 基于MyBatis Plus
 */
@Mapper
public interface CommunityCommentRepository extends BaseMapper<CommunityComment> {
    // BaseMapper提供所有基础CRUD操作
}
