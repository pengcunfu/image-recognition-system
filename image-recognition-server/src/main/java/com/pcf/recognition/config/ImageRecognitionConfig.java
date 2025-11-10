package com.pcf.recognition.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;

import jakarta.annotation.PostConstruct;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * 图像识别配置类
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "image.recognition")
public class ImageRecognitionConfig {

    /**
     * 是否启用图像识别服务
     */
    private boolean enabled = true;

    /**
     * 最大批量处理数量
     */
    private int maxBatchSize = 20;

    /**
     * 支持的文件格式
     */
    private String supportedFormats = "jpg,jpeg,png,bmp,gif,webp";

    /**
     * 最大文件大小（字节）
     */
    private long maxFileSize = 10485760; // 10MB

    /**
     * 提示词配置
     */
    private Prompt prompt = new Prompt();

    /**
     * 提示词内容（从文件加载后存储）
     */
    private String defaultPromptContent;
    private String detailedPromptContent;

    /**
     * 获取支持的文件格式列表
     */
    public List<String> getSupportedFormatsList() {
        return Arrays.asList(supportedFormats.toLowerCase().split(","));
    }

    /**
     * 检查文件格式是否支持
     */
    public boolean isSupportedFormat(String format) {
        if (format == null) {
            return false;
        }
        return getSupportedFormatsList().contains(format.toLowerCase());
    }

    /**
     * 检查文件大小是否符合要求
     */
    public boolean isValidFileSize(long fileSize) {
        return fileSize > 0 && fileSize <= maxFileSize;
    }

    /**
     * 初始化方法，加载提示词文件
     */
    @PostConstruct
    public void init() {
        loadPromptFiles();
    }

    /**
     * 加载提示词文件
     */
    private void loadPromptFiles() {
        try {
            // 加载默认提示词文件
            if (prompt.getDefaultFile() != null) {
                String filePath = prompt.getDefaultFile().replace("classpath:", "");
                ClassPathResource defaultResource = new ClassPathResource(filePath);
                if (!defaultResource.exists()) {
                    throw new RuntimeException("默认提示词文件不存在: " + prompt.getDefaultFile());
                }
                defaultPromptContent = StreamUtils.copyToString(
                        defaultResource.getInputStream(),
                        StandardCharsets.UTF_8
                );
            }

            // 加载详细提示词文件
            if (prompt.getDetailedFile() != null) {
                String filePath = prompt.getDetailedFile().replace("classpath:", "");
                ClassPathResource detailedResource = new ClassPathResource(filePath);
                if (!detailedResource.exists()) {
                    throw new RuntimeException("详细提示词文件不存在: " + prompt.getDetailedFile());
                }
                detailedPromptContent = StreamUtils.copyToString(
                        detailedResource.getInputStream(),
                        StandardCharsets.UTF_8
                );
            }

        } catch (IOException e) {
            throw new RuntimeException("加载提示词文件失败", e);
        }
    }

    /**
     * 提示词配置内部类
     */
    @Data
    public static class Prompt {
        private String defaultFile;
        private String detailedFile;
    }
}
