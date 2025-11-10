package com.pcf.recognition.service;

import com.pcf.recognition.util.DoubaoUtil;
import com.pcf.recognition.util.TosUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 图像识别服务
 * 整合TOS上传和豆包视觉识别功能
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ImageRecognitionService {
    
    private final TosUtil tosUtil;
    private final DoubaoUtil doubaoUtil;
    
    /**
     * 上传并识别图像
     * 
     * @param file 图像文件
     * @param prompt 识别提示词（可选）
     * @return 识别结果，包含图像URL和识别内容
     */
    public Map<String, Object> uploadAndRecognizeImage(MultipartFile file, String prompt) throws IOException {
        log.info("开始上传并识别图像: filename={}, size={}", file.getOriginalFilename(), file.getSize());
        
        // 1. 上传图像到TOS
        String imageUrl = tosUtil.uploadFile(file, "recognition");
        log.info("图像上传成功: url={}", imageUrl);
        
        // 2. 调用豆包识别
        String recognition;
        if (prompt != null && !prompt.isEmpty()) {
            recognition = doubaoUtil.recognizeImage(imageUrl, prompt);
        } else {
            recognition = doubaoUtil.recognizeImageWithDefaultPrompt(imageUrl);
        }
        log.info("图像识别完成");
        
        // 3. 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("imageUrl", imageUrl);
        result.put("recognition", recognition);
        result.put("filename", file.getOriginalFilename());
        result.put("size", file.getSize());
        result.put("success", true);
        
        return result;
    }
    
    /**
     * 批量上传并识别图像
     * 
     * @param files 图像文件列表
     * @param prompt 识别提示词（可选）
     * @return 识别结果列表
     */
    public List<Map<String, Object>> uploadAndRecognizeImages(List<MultipartFile> files, String prompt) {
        log.info("开始批量上传并识别图像，数量: {}", files.size());
        
        List<Map<String, Object>> results = new ArrayList<>();
        
        for (MultipartFile file : files) {
            try {
                Map<String, Object> result = uploadAndRecognizeImage(file, prompt);
                results.add(result);
            } catch (Exception e) {
                log.error("处理图像失败: filename={}", file.getOriginalFilename(), e);
                
                Map<String, Object> errorResult = new HashMap<>();
                errorResult.put("filename", file.getOriginalFilename());
                errorResult.put("success", false);
                errorResult.put("error", e.getMessage());
                results.add(errorResult);
            }
        }
        
        return results;
    }
    
    /**
     * 仅识别已有图像URL
     * 
     * @param imageUrl 图像URL
     * @param prompt 识别提示词（可选）
     * @return 识别结果
     */
    public Map<String, Object> recognizeExistingImage(String imageUrl, String prompt) {
        log.info("识别已有图像: url={}", imageUrl);
        
        String recognition;
        if (prompt != null && !prompt.isEmpty()) {
            recognition = doubaoUtil.recognizeImage(imageUrl, prompt);
        } else {
            recognition = doubaoUtil.recognizeImageWithDefaultPrompt(imageUrl);
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("imageUrl", imageUrl);
        result.put("recognition", recognition);
        result.put("success", true);
        
        return result;
    }
    
    /**
     * 识别特定对象
     * 
     * @param file 图像文件
     * @param targetObject 目标对象
     * @return 识别结果
     */
    public Map<String, Object> recognizeSpecificObject(MultipartFile file, String targetObject) throws IOException {
        log.info("识别特定对象: filename={}, target={}", file.getOriginalFilename(), targetObject);
        
        // 1. 上传图像
        String imageUrl = tosUtil.uploadFile(file, "recognition");
        
        // 2. 识别特定对象
        String recognition = doubaoUtil.recognizeSpecificObject(imageUrl, targetObject);
        
        // 3. 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("imageUrl", imageUrl);
        result.put("recognition", recognition);
        result.put("targetObject", targetObject);
        result.put("filename", file.getOriginalFilename());
        result.put("success", true);
        
        return result;
    }
    
    /**
     * 删除图像
     * 
     * @param imageUrl 图像URL
     */
    public void deleteImage(String imageUrl) {
        log.info("删除图像: url={}", imageUrl);
        
        String key = tosUtil.extractKeyFromUrl(imageUrl);
        if (key != null) {
            tosUtil.deleteFile(key);
        } else {
            log.warn("无法从URL提取对象key: url={}", imageUrl);
        }
    }
}

