package com.pcf.recognition.dto;

import lombok.Data;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * 用户统计信息DTO
 */
@Data
@Builder
public class UserStatsDto {
    
    /** 总识别次数 */
    private Integer totalRecognitions;
    
    /** 识别次数（与totalRecognitions相同，保持兼容性） */
    private Integer recognitionCount;
    
    /** 成功识别次数 */
    private Integer successRecognitions;
    
    /** 失败识别次数 */
    private Integer failedRecognitions;
    
    /** 收藏数量 */
    private Integer favoriteCount;
    
    /** 总上传大小（字节） */
    private Long totalUploadSize;
    
    /** 平均置信度 */
    private Double averageConfidence;
    
    /** 最后识别时间 */
    private LocalDateTime lastRecognitionTime;
    
    /** 发帖数量 */
    private Integer postCount;
    
    /** 评论数量 */
    private Integer commentCount;
    
    /** 浏览量 */
    private Integer viewCount;
    
    /** 获赞数量 */
    private Integer likeCount;
}
