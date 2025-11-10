package com.pcf.recognition.controller;

import com.pcf.recognition.dto.ApiResponse;
import com.pcf.recognition.dto.SystemInfoDto;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 首页控制器
 * 提供系统基本信息
 */
@RestController
public class IndexController {

    /**
     * 系统信息接口
     * 合并了系统信息、健康检查和欢迎信息
     */
    @GetMapping("/")
    public ApiResponse<SystemInfoDto> index() {
        SystemInfoDto systemInfo = SystemInfoDto.builder()
                .name("图像识别系统")
                .version("1.0.0")
                .description("欢迎使用基于火山引擎Doubao AI的通用图像识别系统！")
                .timestamp(System.currentTimeMillis())
                .status("healthy")
                .author("PCF Team")
                .build();

        return ApiResponse.success(systemInfo, "系统运行正常，欢迎使用图像识别系统");
    }

    /**
     * 处理 favicon.ico 请求
     */
    @GetMapping(value = "/favicon.ico", produces = "image/x-icon")
    public ResponseEntity<Resource> favicon() {
        Resource resource = new ClassPathResource("static/favicon.png");
        if (resource.exists()) {
            return ResponseEntity.ok()
                    .contentType(MediaType.valueOf("image/x-icon"))
                    .body(resource);
        }
        return ResponseEntity.notFound().build();
    }
}
