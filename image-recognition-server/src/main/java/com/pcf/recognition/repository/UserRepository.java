package com.pcf.recognition.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pcf.recognition.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户Repository接口 - 基于MyBatis Plus
 */
@Mapper
public interface UserRepository extends BaseMapper<User> {
    // BaseMapper已提供基础CRUD操作：
    // - insert(T entity)
    // - deleteById(Serializable id)  
    // - updateById(T entity)
    // - selectById(Serializable id)
    // - selectList(Wrapper<T> queryWrapper)
    // - selectPage(IPage<T> page, Wrapper<T> queryWrapper)
    // 等等...
}