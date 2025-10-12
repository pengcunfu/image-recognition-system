package com.pcf.recognition.controller;

import com.pcf.recognition.dto.*;
import com.pcf.recognition.service.RecognitionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;

/**
 * Doubao图像识别控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/doubao/image")
@RequiredArgsConstructor
@Tag(name = "Doubao AI识别", description = "基于火山引擎Doubao AI模型的图像识别API")
public class RecognitionController {

    private final RecognitionService doubaoService;

    /**
     * 通过文件上传进行图像识别
     */
    @Operation(
            summary = "Doubao AI图像识别 (文件上传)",
            description = "使用Doubao AI模型识别上传的图片，支持自定义提示词和参数"
    )
    @PostMapping(value = "/recognize/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ImageRecognitionResponse>> recognizeByUpload(
            @Parameter(description = "要识别的图片文件", required = true)
            @RequestParam("file") MultipartFile file,
            @Parameter(description = "自定义提示词")
            @RequestParam(value = "customPrompt", required = false) String customPrompt,
            @Parameter(description = "是否详细分析")
            @RequestParam(value = "detailedAnalysis", required = false, defaultValue = "false") Boolean detailedAnalysis,
            @Parameter(description = "最大Token数量")
            @RequestParam(value = "maxTokens", required = false, defaultValue = "500") Integer maxTokens,
            @Parameter(description = "温度参数 (0.0-1.0)")
            @RequestParam(value = "temperature", required = false, defaultValue = "0.1") Double temperature) {

        try {
            // 验证文件
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(
                        ApiResponse.error("上传文件不能为空")
                );
            }

            // 转换为Base64
            String imageBase64 = Base64.getEncoder().encodeToString(file.getBytes());

            // 构建请求
            ImageRecognitionRequest request = new ImageRecognitionRequest();
            request.setImageBase64(imageBase64);
            request.setCustomPrompt(customPrompt);
            request.setDetailedAnalysis(detailedAnalysis);
            request.setMaxTokens(maxTokens);
            request.setTemperature(temperature);

            // 调用识别服务
            ImageRecognitionResponse response = doubaoService.recognizeImage(request);

            if (response.getSuccess()) {
                return ResponseEntity.ok(ApiResponse.success(response, "图像识别成功"));
            } else {
                return ResponseEntity.internalServerError().body(
                        ApiResponse.error("识别失败: " + response.getErrorMessage())
                );
            }

        } catch (Exception e) {
            log.error("文件上传识别失败", e);
            return ResponseEntity.internalServerError().body(
                    ApiResponse.error("服务器内部错误: " + e.getMessage())
            );
        }
    }

    /**
     * 通过JSON请求进行图像识别
     */
    @Operation(
            summary = "Doubao AI图像识别 (JSON请求)",
            description = "使用JSON格式的请求进行图像识别，支持Base64编码或图片URL"
    )
    @PostMapping("/recognize/json")
    public ResponseEntity<ApiResponse<ImageRecognitionResponse>> recognizeByJson(
            @Parameter(description = "图像识别请求参数", required = true)
            @RequestBody ImageRecognitionRequest request) {

        try {
            // 验证请求
            if (request.getImageBase64() == null && request.getImageUrl() == null) {
                return ResponseEntity.badRequest().body(
                        ApiResponse.error("必须提供imageBase64或imageUrl")
                );
            }

            // 调用识别服务
            ImageRecognitionResponse response = doubaoService.recognizeImage(request);

            if (response.getSuccess()) {
                return ResponseEntity.ok(ApiResponse.success(response, "图像识别成功"));
            } else {
                return ResponseEntity.internalServerError().body(
                        ApiResponse.error("识别失败: " + response.getErrorMessage())
                );
            }

        } catch (Exception e) {
            log.error("JSON请求识别失败", e);
            return ResponseEntity.internalServerError().body(
                    ApiResponse.error("服务器内部错误: " + e.getMessage())
            );
        }
    }

    /**
     * 测试Doubao连接
     */
    @Operation(summary = "测试Doubao AI连接", description = "测试与火山引擎Doubao AI服务的连接状态")
    @GetMapping("/test")
    public ResponseEntity<ApiResponse<DoubaoConnectionTestResponseDto>> testConnection() {
        try {
            boolean connected = doubaoService.testConnection();
            DoubaoConnectionTestResponseDto result = new DoubaoConnectionTestResponseDto();
            result.setConnected(connected);
            result.setMessage(connected ? "连接成功" : "连接失败");
            result.setTimestamp(System.currentTimeMillis());

            if (connected) {
                return ResponseEntity.ok(ApiResponse.success(result, "Doubao连接测试成功"));
            } else {
                return ResponseEntity.internalServerError().body(
                        ApiResponse.error("Doubao连接失败", result)
                );
            }

        } catch (Exception e) {
            log.error("连接测试失败", e);
            DoubaoConnectionTestResponseDto result = new DoubaoConnectionTestResponseDto();
            result.setConnected(false);
            result.setError(e.getMessage());
            result.setTimestamp(System.currentTimeMillis());

            return ResponseEntity.internalServerError().body(
                    ApiResponse.error("连接测试异常: " + e.getMessage(), result)
            );
        }
    }

    /**
     * 获取服务状态
     */
    @Operation(summary = "获取Doubao服务状态", description = "获取Doubao图像识别服务的运行状态信息")
    @GetMapping("/status")
    public ResponseEntity<ApiResponse<DoubaoServiceStatusResponseDto>> getStatus() {
        DoubaoServiceStatusResponseDto status = new DoubaoServiceStatusResponseDto();
        status.setService("DoubaoImageRecognitionService");
        status.setStatus("running");
        status.setTimestamp(System.currentTimeMillis());
        status.setVersion("1.0.0");

        return ResponseEntity.ok(ApiResponse.success(status, "服务状态正常"));
    }
}
