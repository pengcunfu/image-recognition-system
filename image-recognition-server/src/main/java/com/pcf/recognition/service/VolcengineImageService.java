package com.pcf.recognition.service;

import com.pcf.recognition.config.ImageRecognitionConfig;
import com.pcf.recognition.config.VolcengineConfig;
import com.pcf.recognition.entity.ImageMetadata;
import com.pcf.recognition.entity.RecognitionItem;
import com.pcf.recognition.entity.RecognitionResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 火山引擎图像识别服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VolcengineImageService {
    
    private final VolcengineConfig volcengineConfig;
    private final ImageRecognitionConfig imageConfig;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient = HttpClient.newHttpClient();
    
    /**
     * 单张图像识别
     */
    public RecognitionResult recognizeImage(MultipartFile file, String mode, Double confidence, Integer maxResults, List<String> tags, Long userId) {
        try {
            // 验证文件
            validateFile(file);
            
            // 构建请求
            String endpoint = volcengineConfig.getVisualRecognition().getEndpoint();
            String action = "ClassifyImage"; // 火山引擎图像分类API
            
            // 准备请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("image_base64", Base64.encodeBase64String(file.getBytes()));
            requestBody.put("image_url", ""); // 使用base64时为空
            requestBody.put("scene", mapModeToScene(mode));
            requestBody.put("min_score", confidence);
            requestBody.put("max_num", maxResults);
            
            // 发送请求
            String response = sendRequest(endpoint, action, requestBody);
            
            // 解析响应
            return parseRecognitionResponse(response, file, mode, confidence, maxResults, tags, userId);
            
        } catch (Exception e) {
            log.error("图像识别失败", e);
            throw new RuntimeException("图像识别失败: " + e.getMessage());
        }
    }
    
    /**
     * 验证上传文件
     */
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }
        
        // 检查文件大小
        if (!imageConfig.isValidFileSize(file.getSize())) {
            throw new IllegalArgumentException("文件大小超出限制");
        }
        
        // 检查文件格式
        String filename = file.getOriginalFilename();
        if (filename == null) {
            throw new IllegalArgumentException("文件名不能为空");
        }
        
        String extension = filename.substring(filename.lastIndexOf(".") + 1);
        if (!imageConfig.isSupportedFormat(extension)) {
            throw new IllegalArgumentException("不支持的文件格式: " + extension);
        }
    }
    
    /**
     * 映射识别模式到火山引擎场景
     */
    private String mapModeToScene(String mode) {
        switch (mode.toLowerCase()) {
            case "animal":
                return "animal";
            case "plant":
                return "plant";
            case "food":
                return "food";
            case "scene":
                return "scene";
            case "general":
            default:
                return "general";
        }
    }
    
    /**
     * 发送HTTP请求到火山引擎
     */
    private String sendRequest(String endpoint, String action, Map<String, Object> requestBody) throws Exception {
        String serviceName = volcengineConfig.getVisualRecognition().getServiceName();
        String region = volcengineConfig.getRegion();
        String version = volcengineConfig.getVisualRecognition().getVersion();
        
        // 构建请求URL
        String url = endpoint + "/?Action=" + action + "&Version=" + version;
        
        // 构建请求体JSON
        String jsonBody = objectMapper.writeValueAsString(requestBody);
        
        // 生成签名
        Map<String, String> headers = generateHeaders(serviceName, region, jsonBody);
        
        // 构建HTTP请求
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody, StandardCharsets.UTF_8))
                .header("Content-Type", "application/json");
        
        // 添加认证头
        headers.forEach(requestBuilder::header);
        
        HttpRequest request = requestBuilder.build();
        
        // 发送请求
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() != 200) {
            throw new RuntimeException("火山引擎API调用失败，状态码: " + response.statusCode() + ", 响应: " + response.body());
        }
        
        return response.body();
    }
    
    /**
     * 生成火山引擎API认证头
     */
    private Map<String, String> generateHeaders(String serviceName, String region, String requestBody) throws Exception {
        String accessKeyId = volcengineConfig.getAccessKeyId();
        String secretAccessKey = volcengineConfig.getSecretAccessKey();
        
        // 时间戳
        String timestamp = LocalDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'"));
        String date = timestamp.substring(0, 8);
        
        // 构建签名
        String credentialScope = date + "/" + region + "/" + serviceName + "/request";
        
        Map<String, String> headers = new HashMap<>();
        headers.put("X-Date", timestamp);
        headers.put("Authorization", "HMAC-SHA256 Credential=" + accessKeyId + "/" + credentialScope);
        
        return headers;
    }
    
    /**
     * 解析火山引擎响应
     */
    private RecognitionResult parseRecognitionResponse(String responseJson, MultipartFile file, String mode, 
                                                      Double confidence, Integer maxResults, List<String> tags, Long userId) {
        try {
            JsonNode rootNode = objectMapper.readTree(responseJson);
            
            // 生成识别ID
            String recognitionId = "rec_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8);
            
            // 解析识别结果
            List<RecognitionItem> results = new ArrayList<>();
            
            // 检查响应是否成功
            if (rootNode.has("ResponseMetadata") && rootNode.get("ResponseMetadata").has("Error")) {
                throw new RuntimeException("火山引擎API返回错误: " + rootNode.get("ResponseMetadata").get("Error").get("Message").asText());
            }
            
            // 解析结果数据
            if (rootNode.has("Result") && rootNode.get("Result").has("data")) {
                JsonNode dataArray = rootNode.get("Result").get("data");
                
                for (JsonNode item : dataArray) {
                    RecognitionItem recognitionItem = RecognitionItem.builder()
                            .label(item.has("class_name") ? item.get("class_name").asText() : "未知")
                            .confidence(item.has("score") ? item.get("score").asDouble() : 0.0)
                            .category(getCategoryFromLabel(item.has("class_name") ? item.get("class_name").asText() : ""))
                            .description(generateDescription(item.has("class_name") ? item.get("class_name").asText() : ""))
                            .build();
                    
                    results.add(recognitionItem);
                }
            }
            
            // 如果没有识别结果，创建一个默认结果
            if (results.isEmpty()) {
                results.add(RecognitionItem.builder()
                        .label("无法识别")
                        .confidence(0.0)
                        .category("未知")
                        .description("未能识别出图像内容")
                        .build());
            }
            
            // 构建元数据
            ImageMetadata metadata = ImageMetadata.builder()
                    .imageSize(ImageMetadata.ImageSize.builder()
                            .width(0) // 火山引擎响应中获取
                            .height(0)
                            .build())
                    .fileSize(file.getSize())
                    .format(getFileExtension(file.getOriginalFilename()))
                    .processingTime(1.0) // 模拟处理时间
                    .build();
            
            // 构建识别结果
            return RecognitionResult.builder()
                    .recognitionId(recognitionId)
                    .userId(userId)
                    .imageUrl("") // 需要实现文件存储服务
                    .originalFileName(file.getOriginalFilename())
                    .mode(mode)
                    .confidence(confidence)
                    .maxResults(maxResults)
                    .results(results)
                    .metadata(metadata)
                    .status("completed")
                    .isFavorite(false)
                    .tags(tags != null ? tags : new ArrayList<>())
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
                    
        } catch (Exception e) {
            log.error("解析识别响应失败", e);
            throw new RuntimeException("解析识别响应失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据标签获取分类
     */
    private String getCategoryFromLabel(String label) {
        if (label.contains("狗") || label.contains("猫") || label.contains("动物")) {
            return "动物";
        } else if (label.contains("花") || label.contains("树") || label.contains("植物")) {
            return "植物";
        } else if (label.contains("食物") || label.contains("菜") || label.contains("果")) {
            return "食物";
        } else {
            return "其他";
        }
    }
    
    /**
     * 生成描述
     */
    private String generateDescription(String label) {
        return "识别结果: " + label;
    }
    
    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        if (filename != null && filename.contains(".")) {
            return filename.substring(filename.lastIndexOf(".") + 1).toUpperCase();
        }
        return "UNKNOWN";
    }
}
