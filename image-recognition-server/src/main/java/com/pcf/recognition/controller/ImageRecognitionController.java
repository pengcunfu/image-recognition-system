package com.pcf.recognition.controller;

import com.pcf.recognition.dto.*;
import com.pcf.recognition.entity.RecognitionResult;
import com.pcf.recognition.service.FileStorageService;
import com.pcf.recognition.service.VolcengineImageService;
import com.pcf.recognition.util.TokenUtil;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 图像识别API控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/recognition")
@RequiredArgsConstructor
@Validated

public class ImageRecognitionController {

    private final VolcengineImageService volcengineImageService;
    private final FileStorageService fileStorageService;
    private final TokenUtil tokenUtil;

    @PostMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('USER', 'VIP', 'ADMIN')")
    public ApiResponse<RecognitionResult> recognizeImage(

            @RequestParam("file") @NotNull MultipartFile file,
            @RequestParam(value = "mode", defaultValue = "general") String mode,
            @RequestParam(value = "confidence", defaultValue = "0.5") Double confidence,

            @RequestParam(value = "maxResults", defaultValue = "5") Integer maxResults,

            @RequestParam(value = "tags", required = false) List<String> tags,
            @RequestHeader(value = "Authorization", required = false) String token) {

        try {
            log.info("开始处理图像识别请求: 文件名={}, 模式={}, 置信度={}",
                    file.getOriginalFilename(), mode, confidence);

            // 从token中获取用户ID
            Long userId = tokenUtil.getUserIdFromHeader(token);
            if (userId == null) {
                return ApiResponse.error("无效的Token");
            }

            // 调用火山引擎识别服务
            RecognitionResult result = volcengineImageService.recognizeImage(
                    file, mode, confidence, maxResults, tags, userId);

            // 存储图像文件
            String imageUrl = fileStorageService.storeFile(file);
            result.setImageUrl(imageUrl);

            log.info("图像识别完成: 识别ID={}, 结果数量={}",
                    result.getRecognitionId(), result.getResults().size());

            return ApiResponse.success(result, "识别成功");

        } catch (Exception e) {
            log.error("图像识别失败: {}", e.getMessage(), e);
            return ApiResponse.error("图像识别失败: " + e.getMessage());
        }
    }


    @PostMapping(value = "/batch", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('VIP', 'ADMIN')")
    public ApiResponse<BatchRecognitionResponseDto> batchRecognizeImages(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam(value = "mode", defaultValue = "general") String mode,
            @RequestParam(value = "confidence", defaultValue = "0.5") Double confidence,

            @RequestParam(value = "maxResults", defaultValue = "5") Integer maxResults,

            @RequestParam(value = "batchName", required = false) String batchName,
            @RequestHeader(value = "Authorization", required = false) String token) {

        try {
            log.info("开始处理批量图像识别请求: 文件数量={}, 模式={}", files.size(), mode);

            // 验证批量数量限制
            if (files.size() > 20) {
                return ApiResponse.error(400, "批量识别最多支持20个文件");
            }

            // 从token中获取用户ID
            Long userId = tokenUtil.getUserIdFromHeader(token);
            if (userId == null) {
                return ApiResponse.error("无效的Token");
            }

            // 生成批次ID
            String batchId = "batch_" + System.currentTimeMillis();

            List<BatchFileResultDto> processedFiles = new ArrayList<>();

            int successCount = 0;
            int failedCount = 0;

            // 处理每个文件
            for (int i = 0; i < files.size(); i++) {
                MultipartFile file = files.get(i);
                BatchFileResultDto fileResult = new BatchFileResultDto();
                fileResult.setFileName(file.getOriginalFilename());
                fileResult.setIndex(i);

                try {
                    // 识别图像
                    RecognitionResult result = volcengineImageService.recognizeImage(
                            file, mode, confidence, maxResults, null, userId);

                    // 存储文件
                    String imageUrl = fileStorageService.storeFile(file);
                    result.setImageUrl(imageUrl);

                    fileResult.setStatus("success");
                    fileResult.setRecognitionId(result.getRecognitionId());
                    fileResult.setResults(result.getResults());
                    fileResult.setImageUrl(imageUrl);

                    successCount++;

                } catch (Exception e) {
                    log.error("处理文件失败: {}, 错误: {}", file.getOriginalFilename(), e.getMessage());
                    fileResult.setStatus("failed");
                    fileResult.setError(e.getMessage());

                    failedCount++;
                }

                processedFiles.add(fileResult);
            }

            // 构建响应
            BatchRecognitionResponseDto response = new BatchRecognitionResponseDto();
            response.setBatchId(batchId);
            response.setStatus("completed");
            response.setTotalFiles(files.size());
            response.setSuccessFiles(successCount);
            response.setFailedFiles(failedCount);
            response.setResults(processedFiles);
            response.setBatchName(batchName);

            log.info("批量识别完成: 批次ID={}, 总数={}, 成功={}, 失败={}",
                    batchId, files.size(), successCount, failedCount);

            return ApiResponse.success(response, "批量识别完成");

        } catch (Exception e) {
            log.error("批量识别失败: {}", e.getMessage(), e);
            return ApiResponse.error("批量识别失败: " + e.getMessage());
        }
    }

    /**
     * 获取支持的识别模式
     */

    @GetMapping("/modes")
    // 公开接口，无需权限验证
    public ApiResponse<List<RecognitionModeDto>> getSupportedModes() {
        List<RecognitionModeDto> modes = new ArrayList<>();

        // 通用识别
        RecognitionModeDto general = new RecognitionModeDto();
        general.setId("general");
        general.setName("通用识别");
        general.setDescription("适用于各种类型的图像识别");
        modes.add(general);

        // 动物识别
        RecognitionModeDto animal = new RecognitionModeDto();
        animal.setId("animal");
        animal.setName("动物识别");
        animal.setDescription("专门识别各种动物");
        modes.add(animal);

        // 植物识别
        RecognitionModeDto plant = new RecognitionModeDto();
        plant.setId("plant");
        plant.setName("植物识别");
        plant.setDescription("识别花卉、树木等植物");
        modes.add(plant);

        // 食物识别
        RecognitionModeDto food = new RecognitionModeDto();
        food.setId("food");
        food.setName("食物识别");
        food.setDescription("识别各种食物和菜品");
        modes.add(food);

        // 场景识别
        RecognitionModeDto scene = new RecognitionModeDto();
        scene.setId("scene");
        scene.setName("场景识别");
        scene.setDescription("识别场景、地标和建筑");
        modes.add(scene);

        return ApiResponse.success(modes);
    }

    /**
     * 健康检查
     */

    @GetMapping("/health")
    // 公开接口，无需权限验证
    public ApiResponse<HealthCheckResponseDto> healthCheck() {
        HealthCheckResponseDto status = new HealthCheckResponseDto();
        status.setStatus("healthy");
        status.setService("image-recognition");
        status.setTimestamp(System.currentTimeMillis());
        status.setVersion("1.0.0");

        return ApiResponse.success(status);
    }
}
