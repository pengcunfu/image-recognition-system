package com.pengcunfu.recognition.enums;

import lombok.Getter;

/**
 * 系统日志状态枚举
 */
@Getter
public enum SystemLogStatus {
    
    /**
     * 成功
     */
    SUCCESS(0, "成功"),
    
    /**
     * 失败
     */
    FAILED(1, "失败");

    private final Integer code;
    private final String description;

    SystemLogStatus(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 根据code获取枚举
     */
    public static SystemLogStatus fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (SystemLogStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid system log status code: " + code);
    }
}

