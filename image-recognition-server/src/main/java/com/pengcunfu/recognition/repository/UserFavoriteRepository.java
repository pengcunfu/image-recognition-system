package com.pcf.recognition.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pcf.recognition.entity.UserFavorite;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户收藏Repository接口 - 基于MyBatis Plus
 */
@Mapper
public interface UserFavoriteRepository extends BaseMapper<UserFavorite> {
    // BaseMapper提供所有基础CRUD操作
}
