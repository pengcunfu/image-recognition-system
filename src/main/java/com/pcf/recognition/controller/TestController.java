package com.pcf.recognition.controller;

import com.pcf.recognition.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 测试控制器
 */
@Slf4j
@RestController
@RequestMapping("/test")
public class TestController {
    
    /**
     * 系统状态检查
     */
    @GetMapping("/status")
    public ApiResponse<Map<String, Object>> getSystemStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("application", "image-recognition-system");
        status.put("status", "running");
        status.put("version", "1.0.0");
        status.put("timestamp", LocalDateTime.now());
        status.put("message", "抖音火山引擎图像识别系统正常运行");
        
        log.info("系统状态检查: 正常运行");
        return ApiResponse.success("系统运行正常", status);
    }
    
    /**
     * API连通性测试
     */
    @GetMapping("/ping")
    public ApiResponse<String> ping() {
        return ApiResponse.success("pong");
    }
}
