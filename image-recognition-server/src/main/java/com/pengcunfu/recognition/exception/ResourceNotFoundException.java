package com.pengcunfu.recognition.exception;

import com.pengcunfu.recognition.constant.ErrorCode;

/**
 * 资源不存在异常
 */
public class ResourceNotFoundException extends BusinessException {

    public ResourceNotFoundException(String message) {
        super(ErrorCode.NOT_FOUND, message);
    }

    public ResourceNotFoundException(Integer code, String message) {
        super(code, message);
    }
}

