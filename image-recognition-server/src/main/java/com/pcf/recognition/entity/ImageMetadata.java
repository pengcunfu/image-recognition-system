package com.pcf.recognition.entity;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;

/**
 * 图像元数据
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageMetadata {

    /**
     * 图像尺寸
     */
    private ImageSize imageSize;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;

    /**
     * 图像格式
     */
    private String format;

    /**
     * 处理时间（秒）
     */
    private Double processingTime;

    /**
     * 图像尺寸定义
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImageSize {
        private Integer width;
        private Integer height;
    }
}
