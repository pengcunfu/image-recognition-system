package com.pcf.recognition.entity;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 图像识别结果实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecognitionResult {

    /**
     * 识别ID
     */
    private String recognitionId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 图像URL
     */
    private String imageUrl;

    /**
     * 原始文件名
     */
    private String originalFileName;

    /**
     * 识别模式
     */
    private String mode;

    /**
     * 置信度阈值
     */
    private Double confidence;

    /**
     * 最大结果数量
     */
    private Integer maxResults;

    /**
     * 识别结果列表
     */
    private List<RecognitionItem> results;

    /**
     * 元数据信息
     */
    private ImageMetadata metadata;

    /**
     * 处理状态
     */
    private String status;

    /**
     * 是否收藏
     */
    private Boolean isFavorite;

    /**
     * 标签列表
     */
    private List<String> tags;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
