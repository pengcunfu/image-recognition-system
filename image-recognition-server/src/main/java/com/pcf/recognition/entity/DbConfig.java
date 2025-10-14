package com.pcf.recognition.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Database Configuration Entity
 * Used to store sensitive configuration parameters in database
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("db_config")
public class DbConfig {

    /**
     * Primary key ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * Configuration key (e.g., spring.mail.username, volcengine.access-key-id)
     */
    @TableField("config_key")
    private String configKey;

    /**
     * Configuration value
     */
    @TableField("config_value")
    private String configValue;

    /**
     * Configuration description
     */
    @TableField("description")
    private String description;

    /**
     * Whether this configuration is encrypted
     */
    @TableField("is_encrypted")
    @Builder.Default
    private Boolean isEncrypted = false;

    /**
     * Configuration category (e.g., mail, volcengine, jwt, doubao)
     */
    @TableField("category")
    private String category;

    /**
     * Whether this configuration is active
     */
    @TableField("is_active")
    @Builder.Default
    private Boolean isActive = true;

    /**
     * Creation time
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * Update time
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * Logical delete flag
     */
    @TableLogic
    @TableField("deleted")
    @Builder.Default
    private Integer deleted = 0;
}
