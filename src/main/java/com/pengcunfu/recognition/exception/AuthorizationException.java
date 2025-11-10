package com.pengcunfu.recognition.exception;

import com.pengcunfu.recognition.constant.ErrorCode;

/**
 * 授权异常
 * 用于权限不足的异常
 */
public class AuthorizationException extends BusinessException {

    public AuthorizationException(String message) {
        super(ErrorCode.FORBIDDEN, message);
    }

    public AuthorizationException(Integer code, String message) {
        super(code, message);
    }
}

