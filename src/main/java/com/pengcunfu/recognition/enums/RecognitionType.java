package com.pengcunfu.recognition.enums;

import lombok.Getter;

/**
 * 识别类型枚举
 */
@Getter
public enum RecognitionType {
    
    /**
     * 快速识别
     */
    QUICK(0, "快速识别", "快速模式，返回基本识别信息"),
    
    /**
     * 详细识别
     */
    DETAILED(1, "详细识别", "详细模式，返回完整的识别信息和分析");

    private final Integer code;
    private final String description;
    private final String detail;

    RecognitionType(Integer code, String description, String detail) {
        this.code = code;
        this.description = description;
        this.detail = detail;
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
    public static RecognitionType fromCode(Integer code) {
        if (code == null) {
            return QUICK; // 默认快速识别
        }
        for (RecognitionType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return QUICK;
    }
}

