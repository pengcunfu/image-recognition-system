package com.pcf.recognition.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pcf.recognition.entity.DbConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

/**
 * Database Configuration Repository
 */
@Mapper
public interface DbConfigRepository extends BaseMapper<DbConfig> {

    /**
     * Get configuration value by key
     *
     * @param configKey configuration key
     * @return configuration value
     */
    @Select("SELECT config_value FROM db_config WHERE config_key = #{configKey} AND is_active = 1 AND deleted = 0")
    String getConfigValue(@Param("configKey") String configKey);

    /**
     * Get all active configurations as Map
     *
     * @return Map of configuration key-value pairs
     */
    @Select("SELECT config_key, config_value FROM db_config WHERE is_active = 1 AND deleted = 0")
    List<Map<String, String>> getAllActiveConfigs();

    /**
     * Get configurations by category
     *
     * @param category configuration category
     * @return List of configurations
     */
    @Select("SELECT * FROM db_config WHERE category = #{category} AND is_active = 1 AND deleted = 0")
    List<DbConfig> getConfigsByCategory(@Param("category") String category);

    /**
     * Update configuration value by key
     *
     * @param configKey configuration key
     * @param configValue new configuration value
     * @return number of affected rows
     */
    @Update("UPDATE db_config SET config_value = #{configValue}, update_time = NOW() WHERE config_key = #{configKey} AND deleted = 0")
    int updateConfigValue(@Param("configKey") String configKey, @Param("configValue") String configValue);

    /**
     * Check if configuration key exists
     *
     * @param configKey configuration key
     * @return true if exists, false otherwise
     */
    @Select("SELECT COUNT(*) > 0 FROM db_config WHERE config_key = #{configKey} AND deleted = 0")
    boolean existsByConfigKey(@Param("configKey") String configKey);

    /**
     * Get encrypted configurations
     *
     * @return List of encrypted configurations
     */
    @Select("SELECT * FROM db_config WHERE is_encrypted = 1 AND is_active = 1 AND deleted = 0")
    List<DbConfig> getEncryptedConfigs();
}
