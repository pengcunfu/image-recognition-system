package com.pengcunfu.recognition.config;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;

/**
 * .env 文件配置加载器
 * 在 Spring Boot 启动前加载 .env 文件中的环境变量
 */
@Slf4j
public class DotenvConfig implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        try {
            // 尝试加载 .env 文件
            Dotenv dotenv = Dotenv.configure()
                    .directory("./") // 从项目根目录加载
                    .ignoreIfMissing() // 如果文件不存在则忽略
                    .load();

            // 将 .env 文件中的配置加载到 Spring 环境中
            ConfigurableEnvironment environment = applicationContext.getEnvironment();
            Map<String, Object> dotenvMap = new HashMap<>();
            
            dotenv.entries().forEach(entry -> {
                String key = entry.getKey();
                String value = entry.getValue();
                dotenvMap.put(key, value);
                log.debug("加载环境变量: {} = {}", key, value.replaceAll(".", "*")); // 隐藏敏感信息
            });

            if (!dotenvMap.isEmpty()) {
                environment.getPropertySources().addFirst(new MapPropertySource("dotenv", dotenvMap));
                log.info("成功加载 .env 文件，共 {} 个环境变量", dotenvMap.size());
            } else {
                log.info("未找到 .env 文件或文件为空，将使用系统环境变量或配置文件默认值");
            }
        } catch (Exception e) {
            log.warn(".env 文件加载失败，将使用系统环境变量或配置文件默认值: {}", e.getMessage());
        }
    }
}

