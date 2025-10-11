package com.pcf.recognition.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * ObjectMapper配置
 */
@Configuration
public class ObjectMapperConfig {

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        // 注册时间模块
        mapper.registerModule(new JavaTimeModule());

        // 配置属性命名策略
        mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

        return mapper;
    }
}
