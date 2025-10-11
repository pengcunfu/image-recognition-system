package com.pcf.recognition.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 火山引擎配置类
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "volcengine")
public class VolcengineConfig {

    /**
     * 访问密钥ID
     */
    private String accessKeyId;

    /**
     * 访问密钥
     */
    private String secretAccessKey;

    /**
     * 地域
     */
    private String region = "cn-north-1";

    /**
     * 视觉识别配置
     */
    private VisualRecognition visualRecognition = new VisualRecognition();

    @Data
    public static class VisualRecognition {
        /**
         * 服务端点
         */
        private String endpoint = "https://visual.volcengineapi.com";

        /**
         * 服务名称
         */
        private String serviceName = "ImageX";

        /**
         * API版本
         */
        private String version = "2018-08-01";
    }
}
