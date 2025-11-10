package com.pengcunfu.recognition.exception;

import com.pengcunfu.recognition.constant.ErrorCode;

/**
 * 文件异常
 * 用于文件上传、下载等操作的异常
 */
public class FileException extends BusinessException {

    public FileException(String message) {
        super(ErrorCode.FILE_UPLOAD_FAILED, message);
    }

    public FileException(Integer code, String message) {
        super(code, message);
    }
}

