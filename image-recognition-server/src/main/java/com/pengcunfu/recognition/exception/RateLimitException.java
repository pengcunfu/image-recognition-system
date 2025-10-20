package com.pengcunfu.recognition.exception;

import com.pengcunfu.recognition.constant.ErrorCode;

/**
 * 限流异常
 * 用于API调用频率超限
 */
public class RateLimitException extends BusinessException {

    public RateLimitException(String message) {
        super(ErrorCode.TOO_MANY_REQUESTS, message);
    }

    public RateLimitException(Integer code, String message) {
        super(code, message);
    }
}

