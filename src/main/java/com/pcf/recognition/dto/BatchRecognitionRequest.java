package com.pcf.recognition.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.*;
import java.util.List;

/**
 * 批量图像识别请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BatchRecognitionRequest {
    
    /**
     * 图像文件列表
     */
    @NotEmpty(message = "图像文件列表不能为空")
    @Size(max = 20, message = "批量识别最多支持20个文件")
    private List<MultipartFile> files;
    
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
     * 每张图片最大返回结果数
     */
    @Min(value = 1, message = "最大结果数不能小于1")
    @Max(value = 10, message = "最大结果数不能大于10")
    private Integer maxResults = 5;
    
    /**
     * 批次名称
     */
    private String batchName;
}
