package com.pcf.recognition.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 用户收藏实体
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("user_favorites")
public class UserFavorite {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long recognitionId;

    private String tags; // JSON格式存储用户自定义标签

    private String notes; // 用户备注

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;

    // MyBatis Plus不使用关联映射，在Service层处理关联查询
}
