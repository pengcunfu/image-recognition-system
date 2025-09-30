package com.pcf.recognition.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 识别历史记录实体
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("recognition_history")
public class RecognitionHistory {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private String originalFilename;
    
    private String imageUrl;
    
    private String thumbnailUrl;
    
    private String result;
    
    private String category;
    
    private Integer confidence;
    
    private Integer processingTime;
    
    private Integer imageWidth;
    
    private Integer imageHeight;
    
    private Long fileSize;
    
    private String tags; // JSON格式存储标签数组
    
    private String alternatives; // JSON格式存储其他可能结果
    
    private String attributes; // JSON格式存储详细属性
    
    private Boolean isFavorite = false;
    
    private RecognitionStatus status = RecognitionStatus.SUCCESS;
    
    private String errorMessage;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
    
    // MyBatis Plus不使用关联映射，在Service层处理关联查询
    
    // 识别状态枚举
    public enum RecognitionStatus {
        SUCCESS("成功"),
        FAILED("失败"),
        PROCESSING("处理中");
        
        private final String description;
        
        RecognitionStatus(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
}
