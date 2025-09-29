package com.pcf.recognition.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Doubao AI 配置类
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "doubao.api")
public class DoubaoConfig {

    /**
     * API密钥
     */
    private String key;

    /**
     * API基础URL
     */
    private String baseUrl;

    /**
     * 模型名称
     */
    private String model;

    /**
     * 请求超时时间(毫秒)
     */
    private Integer timeout = 30000;

    /**
     * 最大重试次数
     */
    private Integer maxRetries = 3;
}
