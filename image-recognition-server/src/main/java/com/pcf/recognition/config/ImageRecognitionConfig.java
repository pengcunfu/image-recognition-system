package com.pcf.recognition.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

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
}
