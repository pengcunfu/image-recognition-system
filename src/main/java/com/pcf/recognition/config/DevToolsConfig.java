package com.pcf.recognition.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * 开发工具配置类
 * 用于配置热重载和开发环境特定的功能
 */
@Configuration
@Profile("dev")
public class DevToolsConfig {

    /**
     * 开发环境信息Bean
     */
    @Bean
    @ConditionalOnProperty(name = "spring.devtools.restart.enabled", havingValue = "true", matchIfMissing = false)
    public String devInfo() {
        return "Development mode with hot reload enabled - " + System.currentTimeMillis();
    }
}
