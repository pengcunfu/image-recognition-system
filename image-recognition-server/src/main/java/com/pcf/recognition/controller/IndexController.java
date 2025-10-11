package com.pcf.recognition.controller;

import com.pcf.recognition.dto.ApiResponse;
import com.pcf.recognition.dto.SystemInfoDto;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 首页控制器
 * 包含系统信息和欢迎页面功能
 */
@Controller

public class IndexController {

    /**
     * 根路径重定向到Swagger UI
     */
    @GetMapping("/")
    public String index() {
        return "redirect:/swagger-ui/index.html";
    }

    /**
     * 系统信息接口
     */
    
    @GetMapping("/api/v1/info")
    @ResponseBody
    public ApiResponse<SystemInfoDto> getSystemInfo() {
        SystemInfoDto systemInfo = SystemInfoDto.builder()
                .name("图像识别系统")
                .version("1.0.0")
                .description("基于火山引擎Doubao AI的通用图像识别系统")
                .swaggerUi("/swagger-ui.html")
                .apiDocs("/v3/api-docs")
                .timestamp(System.currentTimeMillis())
                .status("running")
                .author("PCF Team")
                .build();

        return ApiResponse.success(systemInfo, "获取系统信息成功");
    }

    /**
     * 系统健康检查接口
     */
    
    @GetMapping("/api/v1/health")
    @ResponseBody
    public ApiResponse<SystemInfoDto> healthCheck() {
        SystemInfoDto healthInfo = SystemInfoDto.builder()
                .name("图像识别系统")
                .version("1.0.0")
                .status("healthy")
                .timestamp(System.currentTimeMillis())
                .build();

        return ApiResponse.success(healthInfo, "系统运行正常");
    }

    /**
     * 欢迎接口
     */
    
    @GetMapping("/api/v1/welcome")
    @ResponseBody
    public ApiResponse<SystemInfoDto> welcome() {
        SystemInfoDto welcomeInfo = SystemInfoDto.builder()
                .name("图像识别系统")
                .description("欢迎使用基于火山引擎Doubao AI的图像识别系统！")
                .version("1.0.0")
                .swaggerUi("/swagger-ui.html")
                .timestamp(System.currentTimeMillis())
                .status("ready")
                .build();

        return ApiResponse.success(welcomeInfo, "欢迎使用图像识别系统");
    }
}
