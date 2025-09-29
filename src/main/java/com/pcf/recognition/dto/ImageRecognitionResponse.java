package com.example.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.util.List;

/**
 * 图像识别响应 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageRecognitionResponse {
    
    /**
     * 识别是否成功
     */
    private Boolean success;
    
    /**
     * 错误信息 (失败时)
     */
    private String errorMessage;
    
    /**
     * 识别结果
     */
    private RecognitionData data;
    
    /**
     * 处理时间 (毫秒)
     */
    private Long processingTime;
    
    /**
     * 使用的token数量
     */
    private Integer tokenUsage;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecognitionData {
        /**
         * 物体类别
         */
        private String category;
        
        /**
         * 具体名称
         */
        private String name;
        
        /**
         * 主要颜色
         */
        private String color;
        
        /**
         * 形状特征
         */
        private String shape;
        
        /**
         * 材质纹理
         */
        private String material;
        
        /**
         * 关键属性列表
         */
        private List<String> attributes;
        
        /**
         * 置信度 (0-1之间)
         */
        private Double confidence;
        
        /**
         * 原始AI响应 (调试用)
         */
        private String rawResponse;
    }
}
