package com.pcf.recognition.controller;

import com.pcf.recognition.dto.ApiResponse;
import com.pcf.recognition.entity.RecognitionResult;
import com.pcf.recognition.service.FileStorageService;
import com.pcf.recognition.service.VolcengineImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 图像识别API控制器
 */
@Slf4j
@RestController
@RequestMapping("/recognition")
@RequiredArgsConstructor
@Validated
public class ImageRecognitionController {
    
    private final VolcengineImageService volcengineImageService;
    private final FileStorageService fileStorageService;
    
    /**
     * 单张图像识别
     */
    @PostMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<RecognitionResult> recognizeImage(
            @RequestParam("file") @NotNull MultipartFile file,
            @RequestParam(value = "mode", defaultValue = "general") String mode,
            @RequestParam(value = "confidence", defaultValue = "0.5") Double confidence,
            @RequestParam(value = "maxResults", defaultValue = "5") Integer maxResults,
            @RequestParam(value = "tags", required = false) List<String> tags) {
        
        try {
            log.info("开始处理图像识别请求: 文件名={}, 模式={}, 置信度={}", 
                    file.getOriginalFilename(), mode, confidence);
            
            // 模拟用户ID（实际项目中应从JWT token中获取）
            Long userId = 1L;
            
            // 调用火山引擎识别服务
            RecognitionResult result = volcengineImageService.recognizeImage(
                    file, mode, confidence, maxResults, tags, userId);
            
            // 存储图像文件
            String imageUrl = fileStorageService.storeFile(file);
            result.setImageUrl(imageUrl);
            
            log.info("图像识别完成: 识别ID={}, 结果数量={}", 
                    result.getRecognitionId(), result.getResults().size());
            
            return ApiResponse.success("识别成功", result);
            
        } catch (Exception e) {
            log.error("图像识别失败: {}", e.getMessage(), e);
            return ApiResponse.error("图像识别失败: " + e.getMessage());
        }
    }
    
    /**
     * 批量图像识别
     */
    @PostMapping(value = "/batch", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<Map<String, Object>> batchRecognizeImages(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam(value = "mode", defaultValue = "general") String mode,
            @RequestParam(value = "confidence", defaultValue = "0.5") Double confidence,
            @RequestParam(value = "maxResults", defaultValue = "5") Integer maxResults,
            @RequestParam(value = "batchName", required = false) String batchName) {
        
        try {
            log.info("开始处理批量图像识别请求: 文件数量={}, 模式={}", files.size(), mode);
            
            // 验证批量数量限制
            if (files.size() > 20) {
                return ApiResponse.error(400, "批量识别最多支持20个文件");
            }
            
            // 模拟用户ID
            Long userId = 1L;
            
            // 生成批次ID
            String batchId = "batch_" + System.currentTimeMillis();
            
            List<RecognitionResult> results = new ArrayList<>();
            List<Map<String, Object>> processedFiles = new ArrayList<>();
            
            int successCount = 0;
            int failedCount = 0;
            
            // 处理每个文件
            for (int i = 0; i < files.size(); i++) {
                MultipartFile file = files.get(i);
                Map<String, Object> fileResult = new HashMap<>();
                fileResult.put("fileName", file.getOriginalFilename());
                fileResult.put("index", i);
                
                try {
                    // 识别图像
                    RecognitionResult result = volcengineImageService.recognizeImage(
                            file, mode, confidence, maxResults, null, userId);
                    
                    // 存储文件
                    String imageUrl = fileStorageService.storeFile(file);
                    result.setImageUrl(imageUrl);
                    
                    results.add(result);
                    fileResult.put("status", "success");
                    fileResult.put("recognitionId", result.getRecognitionId());
                    fileResult.put("results", result.getResults());
                    
                    successCount++;
                    
                } catch (Exception e) {
                    log.error("处理文件失败: {}, 错误: {}", file.getOriginalFilename(), e.getMessage());
                    fileResult.put("status", "failed");
                    fileResult.put("error", e.getMessage());
                    
                    failedCount++;
                }
                
                processedFiles.add(fileResult);
            }
            
            // 构建响应
            Map<String, Object> response = new HashMap<>();
            response.put("batchId", batchId);
            response.put("status", "completed");
            response.put("totalFiles", files.size());
            response.put("successFiles", successCount);
            response.put("failedFiles", failedCount);
            response.put("results", processedFiles);
            response.put("batchName", batchName);
            
            log.info("批量识别完成: 批次ID={}, 总数={}, 成功={}, 失败={}", 
                    batchId, files.size(), successCount, failedCount);
            
            return ApiResponse.success("批量识别完成", response);
            
        } catch (Exception e) {
            log.error("批量识别失败: {}", e.getMessage(), e);
            return ApiResponse.error("批量识别失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取支持的识别模式
     */
    @GetMapping("/modes")
    public ApiResponse<List<Map<String, Object>>> getSupportedModes() {
        List<Map<String, Object>> modes = new ArrayList<>();
        
        // 通用识别
        Map<String, Object> general = new HashMap<>();
        general.put("id", "general");
        general.put("name", "通用识别");
        general.put("description", "适用于各种类型的图像识别");
        modes.add(general);
        
        // 动物识别
        Map<String, Object> animal = new HashMap<>();
        animal.put("id", "animal");
        animal.put("name", "动物识别");
        animal.put("description", "专门识别各种动物");
        modes.add(animal);
        
        // 植物识别
        Map<String, Object> plant = new HashMap<>();
        plant.put("id", "plant");
        plant.put("name", "植物识别");
        plant.put("description", "识别花卉、树木等植物");
        modes.add(plant);
        
        // 食物识别
        Map<String, Object> food = new HashMap<>();
        food.put("id", "food");
        food.put("name", "食物识别");
        food.put("description", "识别各种食物和菜品");
        modes.add(food);
        
        // 场景识别
        Map<String, Object> scene = new HashMap<>();
        scene.put("id", "scene");
        scene.put("name", "场景识别");
        scene.put("description", "识别场景、地标和建筑");
        modes.add(scene);
        
        return ApiResponse.success(modes);
    }
    
    /**
     * 健康检查
     */
    @GetMapping("/health")
    public ApiResponse<Map<String, Object>> healthCheck() {
        Map<String, Object> status = new HashMap<>();
        status.put("status", "healthy");
        status.put("service", "image-recognition");
        status.put("timestamp", System.currentTimeMillis());
        status.put("version", "1.0.0");
        
        return ApiResponse.success(status);
    }
}
