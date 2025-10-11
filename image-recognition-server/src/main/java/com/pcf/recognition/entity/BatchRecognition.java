package com.pcf.recognition.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 批量识别任务实体
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("batch_recognitions")
public class BatchRecognition {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String taskName;

    private String description;

    private Integer totalCount = 0; // 总图片数量

    private Integer processedCount = 0; // 已处理数量

    private Integer successCount = 0; // 成功数量

    private Integer failedCount = 0; // 失败数量

    private BatchStatus status = BatchStatus.PENDING;

    private Integer progress = 0; // 进度百分比

    private Long totalSize = 0L; // 总文件大小（字节）

    private Integer processingTime; // 处理耗时（秒）

    private String errorMessage; // 错误信息

    private LocalDateTime startTime; // 开始处理时间

    private LocalDateTime endTime; // 结束处理时间

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;

    // 批量任务状态枚举
    public enum BatchStatus {
        PENDING("等待中"),
        PROCESSING("处理中"),
        COMPLETED("已完成"),
        FAILED("失败"),
        CANCELLED("已取消");

        private final String description;

        BatchStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}