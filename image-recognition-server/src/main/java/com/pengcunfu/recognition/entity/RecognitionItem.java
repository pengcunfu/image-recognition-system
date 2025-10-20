package com.pcf.recognition.entity;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;

/**
 * 识别结果项
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecognitionItem {

    /**
     * 识别标签
     */
    private String label;

    /**
     * 置信度 (0-1)
     */
    private Double confidence;

    /**
     * 分类
     */
    private String category;

    /**
     * 描述
     */
    private String description;

    /**
     * 边界框
     */
    private BoundingBox boundingBox;

    /**
     * 边界框定义
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BoundingBox {
        private Integer x;
        private Integer y;
        private Integer width;
        private Integer height;
    }
}
