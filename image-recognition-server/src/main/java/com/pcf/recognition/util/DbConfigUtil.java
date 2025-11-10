package com.pcf.recognition.util;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pcf.recognition.entity.DbConfig;
import com.pcf.recognition.repository.DbConfigRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Database Configuration Utility
 * Provides static methods for get/set operations on database configurations
 */
@Slf4j
@Component
public class DbConfigUtil {

    @Autowired
    private DbConfigRepository dbConfigRepository;

    private static DbConfigRepository repository;

    /**
     * Initialize static repository reference after bean construction
     */
    @PostConstruct
    public void init() {
        repository = this.dbConfigRepository;
    }

    /**
     * Get configuration value by key
     *
     * @param configKey configuration key
     * @return configuration value, or null if not found
     */
    public static String get(String configKey) {
        try {
            if (repository == null) {
                log.error("DbConfigRepository not initialized");
                return null;
            }

            DbConfig config = repository.selectOne(
                new LambdaQueryWrapper<DbConfig>()
                    .eq(DbConfig::getConfigKey, configKey)
                    .eq(DbConfig::getIsActive, true)
            );
            
            if (config == null) {
                log.warn("Configuration not found for key: {}", configKey);
                return null;
            }
            
            return config.getConfigValue();
        } catch (Exception e) {
            log.error("Error getting configuration for key: {}", configKey, e);
            return null;
        }
    }

    /**
     * Set configuration value
     *
     * @param configKey configuration key
     * @param configValue configuration value
     * @return true if successful, false otherwise
     */
    public static boolean set(String configKey, String configValue) {
        try {
            if (repository == null) {
                log.error("DbConfigRepository not initialized");
                return false;
            }

            DbConfig existingConfig = repository.selectOne(
                new LambdaQueryWrapper<DbConfig>()
                    .eq(DbConfig::getConfigKey, configKey)
            );
            
            if (existingConfig != null) {
                // Update existing configuration
                existingConfig.setConfigValue(configValue);
                existingConfig.setIsEncrypted(false);
                return repository.updateById(existingConfig) > 0;
            } else {
                // Create new configuration
                DbConfig newConfig = DbConfig.builder()
                    .configKey(configKey)
                    .configValue(configValue)
                    .isEncrypted(false)
                    .isActive(true)
                    .build();
                return repository.insert(newConfig) > 0;
            }
        } catch (Exception e) {
            log.error("Error setting configuration for key: {}", configKey, e);
            return false;
        }
    }
}

