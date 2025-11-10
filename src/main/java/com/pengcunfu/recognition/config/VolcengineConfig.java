package com.pengcunfu.recognition.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 火山引擎配置
 */
@Configuration
@ConfigurationProperties(prefix = "volcengine")
@Data
public class VolcengineConfig {
    
    private String accessKeyId;
    private String secretAccessKey;
    private String region;
    private TosConfig tos;
    
    @Data
    public static class TosConfig {
        private String endpoint;
        private String region;
        private String bucket;
        private Integer urlExpiration;
    }
}
