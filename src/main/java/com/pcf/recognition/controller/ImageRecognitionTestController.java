package com.pcf.recognition.controller;

import com.pcf.recognition.dto.ApiResponse;
import com.pcf.recognition.dto.ImageRecognitionRequest;
import com.pcf.recognition.dto.ImageRecognitionResponse;
import com.pcf.recognition.service.DoubaoImageRecognitionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 图像识别测试控制器
 * 用于快速测试和演示功能
 */
@Slf4j
@RestController
@RequestMapping("/test/image-recognition")
@RequiredArgsConstructor
public class ImageRecognitionTestController {
    
    private final DoubaoImageRecognitionService doubaoService;
    
    /**
     * 测试识别网络图像 - 花卉示例
     */
    @GetMapping("/test-flower")
    public ResponseEntity<ApiResponse<ImageRecognitionResponse>> testFlowerRecognition() {
        log.info("开始测试花卉识别...");
        
        try {
            // 使用一个公开的花卉图像URL进行测试
            ImageRecognitionRequest request = new ImageRecognitionRequest();
            request.setImageUrl("https://images.unsplash.com/photo-1490750967868-88aa4486c946?w=800&h=600&fit=crop");
            request.setDetailedAnalysis(false);
            request.setMaxTokens(500);
            request.setTemperature(0.1);
            
            ImageRecognitionResponse response = doubaoService.recognizeImage(request);
            
            if (response.getSuccess()) {
                log.info("花卉识别成功: {}", response.getData());
                return ResponseEntity.ok(ApiResponse.success(response, "花卉识别测试成功"));
            } else {
                log.error("花卉识别失败: {}", response.getErrorMessage());
                return ResponseEntity.internalServerError().body(
                        ApiResponse.error("花卉识别失败: " + response.getErrorMessage())
                );
            }
            
        } catch (Exception e) {
            log.error("花卉识别测试异常", e);
            return ResponseEntity.internalServerError().body(
                    ApiResponse.error("测试异常: " + e.getMessage())
            );
        }
    }
    
    /**
     * 测试识别动物图像
     */
    @GetMapping("/test-animal")
    public ResponseEntity<ApiResponse<ImageRecognitionResponse>> testAnimalRecognition() {
        log.info("开始测试动物识别...");
        
        try {
            ImageRecognitionRequest request = new ImageRecognitionRequest();
            request.setImageUrl("https://images.unsplash.com/photo-1574158622682-e40e69881006?w=800&h=600&fit=crop");
            request.setDetailedAnalysis(false);
            request.setMaxTokens(500);
            request.setTemperature(0.1);
            
            ImageRecognitionResponse response = doubaoService.recognizeImage(request);
            
            if (response.getSuccess()) {
                return ResponseEntity.ok(ApiResponse.success(response, "动物识别测试成功"));
            } else {
                return ResponseEntity.internalServerError().body(
                        ApiResponse.error("动物识别失败: " + response.getErrorMessage())
                );
            }
            
        } catch (Exception e) {
            log.error("动物识别测试异常", e);
            return ResponseEntity.internalServerError().body(
                    ApiResponse.error("测试异常: " + e.getMessage())
            );
        }
    }
    
    /**
     * 测试自定义提示词
     */
    @PostMapping("/test-custom-prompt")
    public ResponseEntity<ApiResponse<ImageRecognitionResponse>> testCustomPrompt(
            @RequestParam("imageUrl") String imageUrl,
            @RequestParam("prompt") String customPrompt) {
        
        log.info("开始测试自定义提示词: {}", customPrompt);
        
        try {
            ImageRecognitionRequest request = new ImageRecognitionRequest();
            request.setImageUrl(imageUrl);
            request.setCustomPrompt(customPrompt);
            request.setMaxTokens(600);
            request.setTemperature(0.2);
            
            ImageRecognitionResponse response = doubaoService.recognizeImage(request);
            
            if (response.getSuccess()) {
                return ResponseEntity.ok(ApiResponse.success(response, "自定义提示词测试成功"));
            } else {
                return ResponseEntity.internalServerError().body(
                        ApiResponse.error("识别失败: " + response.getErrorMessage())
                );
            }
            
        } catch (Exception e) {
            log.error("自定义提示词测试异常", e);
            return ResponseEntity.internalServerError().body(
                    ApiResponse.error("测试异常: " + e.getMessage())
            );
        }
    }
    
    /**
     * 批量测试多种图像类型
     */
    @GetMapping("/test-batch")
    public ResponseEntity<ApiResponse<Map<String, Object>>> testBatchRecognition() {
        log.info("开始批量识别测试...");
        
        Map<String, Object> results = new HashMap<>();
        
        // 测试图像列表
        Map<String, String> testImages = new HashMap<>();
        testImages.put("flower", "https://images.unsplash.com/photo-1490750967868-88aa4486c946?w=400&h=400&fit=crop");
        testImages.put("car", "https://images.unsplash.com/photo-1494976388531-d1058494cdd8?w=400&h=400&fit=crop");
        testImages.put("food", "https://images.unsplash.com/photo-1565299624946-b28f40a0ca4b?w=400&h=400&fit=crop");
        
        for (Map.Entry<String, String> entry : testImages.entrySet()) {
            try {
                log.info("测试识别: {}", entry.getKey());
                
                ImageRecognitionRequest request = new ImageRecognitionRequest();
                request.setImageUrl(entry.getValue());
                request.setDetailedAnalysis(false);
                request.setMaxTokens(400);
                request.setTemperature(0.1);
                
                ImageRecognitionResponse response = doubaoService.recognizeImage(request);
                
                Map<String, Object> itemResult = new HashMap<>();
                itemResult.put("success", response.getSuccess());
                itemResult.put("processingTime", response.getProcessingTime());
                
                if (response.getSuccess()) {
                    itemResult.put("category", response.getData().getCategory());
                    itemResult.put("name", response.getData().getName());
                    itemResult.put("confidence", response.getData().getConfidence());
                } else {
                    itemResult.put("error", response.getErrorMessage());
                }
                
                results.put(entry.getKey(), itemResult);
                
                // 避免API调用过于频繁
                Thread.sleep(1000);
                
            } catch (Exception e) {
                log.error("批量测试项目失败: {}", entry.getKey(), e);
                Map<String, Object> errorResult = new HashMap<>();
                errorResult.put("success", false);
                errorResult.put("error", e.getMessage());
                results.put(entry.getKey(), errorResult);
            }
        }
        
        return ResponseEntity.ok(ApiResponse.success(results, "批量识别测试完成"));
    }
    
    /**
     * 获取测试配置信息
     */
    @GetMapping("/config")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getTestConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("service", "DoubaoImageRecognitionService");
        config.put("availableTests", new String[]{
                "/test-flower - 测试花卉识别",
                "/test-animal - 测试动物识别", 
                "/test-custom-prompt - 测试自定义提示词",
                "/test-batch - 批量测试"
        });
        config.put("description", "通用图像识别系统测试接口");
        config.put("version", "1.0.0");
        
        return ResponseEntity.ok(ApiResponse.success(config, "测试配置信息"));
    }
}
