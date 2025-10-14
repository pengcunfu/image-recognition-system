package com.pcf.recognition.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pcf.recognition.entity.DbConfig;
import com.pcf.recognition.repository.DbConfigRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Database Configuration Service
 * Handles sensitive configuration parameters stored in database
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class DbConfigService {

    private final DbConfigRepository dbConfigRepository;
    
    // Simple encryption key for demonstration - in production, use proper key management
    private static final String ENCRYPTION_KEY = "MySecretKey12345";
    private static final String ALGORITHM = "AES";

    /**
     * Get configuration value by key
     *
     * @param configKey configuration key
     * @return configuration value (decrypted if encrypted)
     */
    public String getConfigValue(String configKey) {
        try {
            DbConfig config = dbConfigRepository.selectOne(
                new LambdaQueryWrapper<DbConfig>()
                    .eq(DbConfig::getConfigKey, configKey)
                    .eq(DbConfig::getIsActive, true)
            );
            
            if (config == null) {
                log.warn("Configuration not found for key: {}", configKey);
                return null;
            }
            
            String value = config.getConfigValue();
            if (config.getIsEncrypted() && StringUtils.hasText(value)) {
                try {
                    value = decrypt(value);
                } catch (Exception e) {
                    log.error("Failed to decrypt configuration for key: {}", configKey, e);
                    return null;
                }
            }
            
            return value;
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
     * @param encrypt whether to encrypt the value
     * @return true if successful, false otherwise
     */
    public boolean setConfigValue(String configKey, String configValue, boolean encrypt) {
        try {
            String valueToStore = configValue;
            if (encrypt && StringUtils.hasText(configValue)) {
                try {
                    valueToStore = encrypt(configValue);
                } catch (Exception e) {
                    log.error("Failed to encrypt configuration for key: {}", configKey, e);
                    return false;
                }
            }
            
            DbConfig existingConfig = dbConfigRepository.selectOne(
                new LambdaQueryWrapper<DbConfig>()
                    .eq(DbConfig::getConfigKey, configKey)
            );
            
            if (existingConfig != null) {
                // Update existing configuration
                existingConfig.setConfigValue(valueToStore);
                existingConfig.setIsEncrypted(encrypt);
                return dbConfigRepository.updateById(existingConfig) > 0;
            } else {
                // Create new configuration
                DbConfig newConfig = DbConfig.builder()
                    .configKey(configKey)
                    .configValue(valueToStore)
                    .isEncrypted(encrypt)
                    .isActive(true)
                    .build();
                return dbConfigRepository.insert(newConfig) > 0;
            }
        } catch (Exception e) {
            log.error("Error setting configuration for key: {}", configKey, e);
            return false;
        }
    }

    /**
     * Get all active configurations as Map
     *
     * @return Map of configuration key-value pairs (decrypted)
     */
    public Map<String, String> getAllActiveConfigs() {
        Map<String, String> configMap = new HashMap<>();
        try {
            List<DbConfig> configs = dbConfigRepository.selectList(
                new LambdaQueryWrapper<DbConfig>()
                    .eq(DbConfig::getIsActive, true)
            );
            
            for (DbConfig config : configs) {
                String value = config.getConfigValue();
                if (config.getIsEncrypted() && StringUtils.hasText(value)) {
                    try {
                        value = decrypt(value);
                    } catch (Exception e) {
                        log.error("Failed to decrypt configuration for key: {}", config.getConfigKey(), e);
                        continue;
                    }
                }
                configMap.put(config.getConfigKey(), value);
            }
        } catch (Exception e) {
            log.error("Error getting all active configurations", e);
        }
        return configMap;
    }

    /**
     * Get configurations by category
     *
     * @param category configuration category
     * @return Map of configuration key-value pairs for the category
     */
    public Map<String, String> getConfigsByCategory(String category) {
        Map<String, String> configMap = new HashMap<>();
        try {
            List<DbConfig> configs = dbConfigRepository.selectList(
                new LambdaQueryWrapper<DbConfig>()
                    .eq(DbConfig::getCategory, category)
                    .eq(DbConfig::getIsActive, true)
            );
            
            for (DbConfig config : configs) {
                String value = config.getConfigValue();
                if (config.getIsEncrypted() && StringUtils.hasText(value)) {
                    try {
                        value = decrypt(value);
                    } catch (Exception e) {
                        log.error("Failed to decrypt configuration for key: {}", config.getConfigKey(), e);
                        continue;
                    }
                }
                configMap.put(config.getConfigKey(), value);
            }
        } catch (Exception e) {
            log.error("Error getting configurations for category: {}", category, e);
        }
        return configMap;
    }

    /**
     * Delete configuration by key
     *
     * @param configKey configuration key
     * @return true if successful, false otherwise
     */
    public boolean deleteConfig(String configKey) {
        try {
            return dbConfigRepository.delete(
                new LambdaQueryWrapper<DbConfig>()
                    .eq(DbConfig::getConfigKey, configKey)
            ) > 0;
        } catch (Exception e) {
            log.error("Error deleting configuration for key: {}", configKey, e);
            return false;
        }
    }

    /**
     * Check if configuration key exists
     *
     * @param configKey configuration key
     * @return true if exists, false otherwise
     */
    public boolean existsConfigKey(String configKey) {
        try {
            return dbConfigRepository.selectCount(
                new LambdaQueryWrapper<DbConfig>()
                    .eq(DbConfig::getConfigKey, configKey)
            ) > 0;
        } catch (Exception e) {
            log.error("Error checking existence of configuration key: {}", configKey, e);
            return false;
        }
    }

    /**
     * Initialize default sensitive configurations
     * This method should be called on application startup
     */
    public void initializeDefaultConfigs() {
        log.info("Initializing default sensitive configurations...");
        
        // Check and create default configurations if they don't exist
        Map<String, Object[]> defaultConfigs = new HashMap<>();
        defaultConfigs.put("spring.mail.username", new Object[]{"YOUR_EMAIL_USERNAME", "Email username for SMTP", "mail", false});
        defaultConfigs.put("spring.mail.password", new Object[]{"YOUR_EMAIL_PASSWORD", "Email password or authorization code", "mail", true});
        defaultConfigs.put("volcengine.access-key-id", new Object[]{"YOUR_VOLCENGINE_ACCESS_KEY_ID", "Volcengine Access Key ID", "volcengine", true});
        defaultConfigs.put("volcengine.secret-access-key", new Object[]{"YOUR_VOLCENGINE_SECRET_ACCESS_KEY", "Volcengine Secret Access Key", "volcengine", true});
        defaultConfigs.put("jwt.secret", new Object[]{"YOUR_JWT_SECRET_KEY", "JWT secret key for token signing", "jwt", true});
        defaultConfigs.put("doubao.api.key", new Object[]{"YOUR_DOUBAO_API_KEY", "Doubao AI API key", "doubao", true});
        
        for (Map.Entry<String, Object[]> entry : defaultConfigs.entrySet()) {
            String key = entry.getKey();
            Object[] values = entry.getValue();
            String defaultValue = (String) values[0];
            String description = (String) values[1];
            String category = (String) values[2];
            boolean encrypt = (Boolean) values[3];
            
            if (!existsConfigKey(key)) {
                try {
                    DbConfig config = DbConfig.builder()
                        .configKey(key)
                        .configValue(encrypt ? encrypt(defaultValue) : defaultValue)
                        .description(description)
                        .category(category)
                        .isEncrypted(encrypt)
                        .isActive(true)
                        .build();
                    
                    dbConfigRepository.insert(config);
                    log.info("Created default configuration: {}", key);
                } catch (Exception e) {
                    log.error("Failed to create default configuration: {}", key, e);
                }
            }
        }
        
        log.info("Default sensitive configurations initialization completed.");
    }

    /**
     * Simple AES encryption
     */
    private String encrypt(String plainText) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(ENCRYPTION_KEY.getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    /**
     * Simple AES decryption
     */
    private String decrypt(String encryptedText) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(ENCRYPTION_KEY.getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }
}
