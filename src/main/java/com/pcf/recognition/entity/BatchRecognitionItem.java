package com.pcf.recognition.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 批量识别项实体
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("batch_recognition_items")
public class BatchRecognitionItem {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long batchId; // 关联批量任务ID
    
    private String originalFilename;
    
    private String imageUrl;
    
    private String thumbnailUrl;
    
    private String result; // 识别结果JSON
    
    private String category;
    
    private Integer confidence;
    
    private Integer processingTime; // 单个处理耗时（毫秒）
    
    private Long fileSize; // 文件大小（字节）
    
    private Integer imageWidth;
    
    private Integer imageHeight;
    
    private ItemStatus status = ItemStatus.PENDING;
    
    private String errorMessage; // 错误信息
    
    private LocalDateTime processedTime; // 处理完成时间
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
    
    // 批量项状态枚举
    public enum ItemStatus {
        PENDING("等待中"),
        PROCESSING("处理中"),
        SUCCESS("成功"),
        FAILED("失败"),
        SKIPPED("已跳过");
        
        private final String description;
        
        ItemStatus(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
}