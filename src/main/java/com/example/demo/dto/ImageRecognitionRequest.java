package com.example.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 图像识别请求 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageRecognitionRequest {
    
    /**
     * 图像的Base64编码数据
     */
    private String imageBase64;
    
    /**
     * 图像URL (可选, 与imageBase64二选一)
     */
    private String imageUrl;
    
    /**
     * 自定义提示词 (可选, 不提供则使用默认提示词)
     */
    private String customPrompt;
    
    /**
     * 是否详细分析 (默认false, true时会返回更多详细信息)
     */
    private Boolean detailedAnalysis = false;
    
    /**
     * 最大token数 (可选, 默认500)
     */
    private Integer maxTokens = 500;
    
    /**
     * 温度参数 (可选, 0.0-1.0, 默认0.1保证准确性)
     */
    private Double temperature = 0.1;
}
