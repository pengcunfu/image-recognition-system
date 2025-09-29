package com.pcf.recognition.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 欢迎页面控制器
 */
@RestController
@Tag(name = "系统信息", description = "系统基本信息API")
public class WelcomeController {

    /**
     * 系统信息接口
     */
    @Operation(summary = "获取系统信息", description = "获取图像识别系统的基本信息和API文档链接")
    @GetMapping("/info")
    public Map<String, Object> getSystemInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("name", "图像识别系统");
        info.put("version", "1.0.0");
        info.put("description", "基于火山引擎Doubao AI的通用图像识别系统");
        info.put("swagger_ui", "/swagger-ui.html");
        info.put("api_docs", "/v3/api-docs");
        info.put("timestamp", System.currentTimeMillis());
        return info;
    }
}
