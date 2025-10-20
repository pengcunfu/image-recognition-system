package com.pengcunfu.recognition.exception;

import com.pengcunfu.recognition.constant.ErrorCode;

/**
 * 认证异常
 * 用于用户认证相关的异常
 */
public class AuthenticationException extends BusinessException {

    public AuthenticationException(String message) {
        super(ErrorCode.UNAUTHORIZED, message);
    }

    public AuthenticationException(Integer code, String message) {
        super(code, message);
    }
}

