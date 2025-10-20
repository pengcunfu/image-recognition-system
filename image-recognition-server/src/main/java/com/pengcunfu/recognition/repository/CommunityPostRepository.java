package com.pcf.recognition.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pcf.recognition.entity.CommunityPost;
import org.apache.ibatis.annotations.Mapper;

/**
 * 社区帖子Repository接口 - 基于MyBatis Plus
 */
@Mapper
public interface CommunityPostRepository extends BaseMapper<CommunityPost> {
    // BaseMapper提供所有基础CRUD操作
}
