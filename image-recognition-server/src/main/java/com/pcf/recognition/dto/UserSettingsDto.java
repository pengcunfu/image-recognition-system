package com.pcf.recognition.dto;

import lombok.Data;
import lombok.Builder;

import java.util.Map;

/**
 * 用户设置DTO
 */
@Data
@Builder
public class UserSettingsDto {
    
    /** 主题设置 */
    private String theme;
    
    /** 语言设置 */
    private String language;
    
    /** 通知设置 */
    private NotificationSettings notifications;
    
    /** 隐私设置 */
    private PrivacySettings privacy;
    
    /** 识别设置 */
    private RecognitionSettings recognition;
    
    @Data
    @Builder
    public static class NotificationSettings {
        private Boolean email;
        private Boolean push;
        private Boolean sms;
    }
    
    @Data
    @Builder
    public static class PrivacySettings {
        private Boolean showProfile;
        private Boolean showHistory;
        private Boolean allowComments;
    }
    
    @Data
    @Builder
    public static class RecognitionSettings {
        private Boolean autoSave;
        private Integer defaultConfidence;
        private Long maxFileSize;
    }
}
