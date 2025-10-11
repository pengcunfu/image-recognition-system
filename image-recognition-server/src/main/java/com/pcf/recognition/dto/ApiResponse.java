package com.pcf.recognition.dto;


import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * 统一API响应格式
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class ApiResponse<T> {
    
    /**
     * 响应码
     */
    
    private Integer code;
    
    /**
     * 响应消息
     */
    
    private String message;
    
    /**
     * 响应数据
     */
    
    private T data;
    
    /**
     * 时间戳
     */
    
    private LocalDateTime timestamp;
    
    /**
     * 成功响应
     */
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .code(200)
                .message("success")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    /**
     * 成功响应（带消息）
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .code(200)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    /**
     * 失败响应
     */
    public static <T> ApiResponse<T> error(Integer code, String message) {
        return ApiResponse.<T>builder()
                .code(code)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    /**
     * 失败响应（默认500错误码）
     */
    public static <T> ApiResponse<T> error(String message) {
        return error(500, message);
    }
    
    /**
     * 失败响应（带数据）
     */
    public static <T> ApiResponse<T> error(String message, T data) {
        return ApiResponse.<T>builder()
                .code(500)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    /**
     * 失败响应（带错误码和数据）
     */
    public static <T> ApiResponse<T> error(Integer code, String message, T data) {
        return ApiResponse.<T>builder()
                .code(code)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
