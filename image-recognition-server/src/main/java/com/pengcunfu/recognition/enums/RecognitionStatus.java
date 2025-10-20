package com.pengcunfu.recognition.enums;

import lombok.Getter;

/**
 * 识别状态枚举
 */
@Getter
public enum RecognitionStatus {
    
    /**
     * 待处理
     */
    PENDING(0, "待处理"),
    
    /**
     * 成功
     */
    SUCCESS(1, "成功"),
    
    /**
     * 失败
     */
    FAILED(2, "失败");

    private final Integer code;
    private final String description;

    RecognitionStatus(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 获取值（用于数据库存储）
     */
    public Integer getValue() {
        return this.code;
    }

    /**
     * 根据code获取枚举
     */
    public static RecognitionStatus fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (RecognitionStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid recognition status code: " + code);
    }

    /**
     * 判断是否为成功状态
     */
    public boolean isSuccess() {
        return this == SUCCESS;
    }

    /**
     * 判断是否为失败状态
     */
    public boolean isFailed() {
        return this == FAILED;
    }
}

