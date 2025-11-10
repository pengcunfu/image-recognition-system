package com.pengcunfu.recognition.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 识别结果表
 * 存储图像识别的结果数据
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("recognition_results")
public class RecognitionResult {

    /**
     * 识别ID(主键)
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID(关联users表)
     */
    private Long userId;

    /**
     * 图片URL
     */
    private String imageUrl;

    /**
     * 图片名称
     */
    private String imageName;

    /**
     * 图片大小(字节)
     */
    private Integer imageSize;

    /**
     * 图片宽度(像素)
     */
    private Integer imageWidth;

    /**
     * 图片高度(像素)
     */
    private Integer imageHeight;

    /**
     * 识别类型: 0-QUICK快速识别, 1-DETAILED详细识别
     */
    private Integer recognitionType;

    /**
     * 是否为高级识别: 0-普通识别, 1-高级识别
     */
    private Integer isAdvanced;

    /**
     * 识别结果JSON
     */
    private String resultJson;

    /**
     * 主要分类
     */
    private String mainCategory;

    /**
     * 置信度(0-100)
     */
    private BigDecimal confidence;

    /**
     * 标签(逗号分隔)
     */
    private String tags;

    /**
     * 识别描述
     */
    private String description;

    /**
     * 处理耗时(毫秒)
     */
    private Integer processingTime;

    /**
     * 识别状态: 0-PENDING待处理, 1-SUCCESS成功, 2-FAILED失败
     */
    private Integer status;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
