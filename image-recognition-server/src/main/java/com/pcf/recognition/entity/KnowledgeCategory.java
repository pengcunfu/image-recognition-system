package com.pcf.recognition.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 知识分类实体
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("knowledge_categories")
public class KnowledgeCategory {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String name; // 分类名称，如"动物"
    
    private String key; // 分类键值，如"animal"
    
    private String description; // 分类描述
    
    private String image; // 分类图片URL
    
    private Integer itemCount = 0; // 该分类下的知识条目数量
    
    private Integer sortOrder = 0; // 排序顺序
    
    private CategoryStatus status = CategoryStatus.ACTIVE; // 分类状态
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
    
    // 分类状态枚举
    public enum CategoryStatus {
        ACTIVE("活跃"),
        INACTIVE("不活跃"),
        HIDDEN("隐藏");
        
        private final String description;
        
        CategoryStatus(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
}
