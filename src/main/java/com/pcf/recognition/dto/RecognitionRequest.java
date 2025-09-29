package com.pcf.recognition.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.*;
import java.util.List;

/**
 * 图像识别请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecognitionRequest {
    
    /**
     * 图像文件
     */
    @NotNull(message = "图像文件不能为空")
    private MultipartFile file;
    
    /**
     * 识别模式
     */
    private String mode = "general";
    
    /**
     * 最小置信度阈值
     */
    @DecimalMin(value = "0.0", message = "置信度阈值不能小于0")
    @DecimalMax(value = "1.0", message = "置信度阈值不能大于1")
    private Double confidence = 0.5;
    
    /**
     * 最大返回结果数
     */
    @Min(value = 1, message = "最大结果数不能小于1")
    @Max(value = 10, message = "最大结果数不能大于10")
    private Integer maxResults = 5;
    
    /**
     * 自定义标签
     */
    private List<String> tags;
}
