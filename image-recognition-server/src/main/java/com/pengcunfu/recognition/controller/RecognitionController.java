package com.pengcunfu.recognition.controller;

import com.pengcunfu.recognition.annotation.Role;
import com.pengcunfu.recognition.constant.ErrorCode;
import com.pengcunfu.recognition.exception.BusinessException;
import com.pengcunfu.recognition.request.RecognitionRequest;
import com.pengcunfu.recognition.response.ApiResponse;
import com.pengcunfu.recognition.response.PageResponse;
import com.pengcunfu.recognition.response.RecognitionResponse;
import com.pengcunfu.recognition.security.SecurityContextHolder;
import com.pengcunfu.recognition.service.RecognitionService;
import com.pengcunfu.recognition.util.TosUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * 图像识别控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/recognition")
@RequiredArgsConstructor
@Role("USER")
public class RecognitionController {

    private final RecognitionService recognitionService;
    private final TosUtil tosUtil;

    /**
     * 图像识别（文件直接上传到 TOS）
     */
    @PostMapping("/recognize")
    public ApiResponse<RecognitionResponse.RecognitionInfo> recognize(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "recognitionType", required = false, defaultValue = "0") Integer recognitionType) {
        Long userId = SecurityContextHolder.getCurrentUserId();
        log.info("图像识别: userId={}, fileName={}, recognitionType={}", userId, file.getOriginalFilename(), recognitionType);
        
        try {
            // 验证文件
            if (file.isEmpty()) {
                throw new BusinessException(ErrorCode.INVALID_PARAM, "上传文件为空");
            }
            
            // 验证文件类型
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new BusinessException(ErrorCode.INVALID_PARAM, "只支持上传图片文件");
            }
            
            // 验证文件大小（10MB）
            if (file.getSize() > 10 * 1024 * 1024) {
                throw new BusinessException(ErrorCode.INVALID_PARAM, "文件大小不能超过 10MB");
            }
            
            // 获取图片尺寸
            Integer imageWidth = null;
            Integer imageHeight = null;
            try {
                BufferedImage image = ImageIO.read(new ByteArrayInputStream(file.getBytes()));
                if (image != null) {
                    imageWidth = image.getWidth();
                    imageHeight = image.getHeight();
                    log.info("图片尺寸: {}x{}", imageWidth, imageHeight);
                }
            } catch (Exception e) {
                log.warn("获取图片尺寸失败: {}", e.getMessage());
            }
            
            // 直接上传到 TOS
            String imageUrl = tosUtil.uploadFile(file, "recognition");
            log.info("文件上传成功: userId={}, imageUrl={}", userId, imageUrl);
            
            // 构建识别请求
            RecognitionRequest.ImageRecognitionRequest request = new RecognitionRequest.ImageRecognitionRequest();
            request.setRecognitionType(recognitionType);
            request.setImageUrl(imageUrl);
            request.setImageSize((int) file.getSize());
            request.setImageWidth(imageWidth);
            request.setImageHeight(imageHeight);
            request.setImageName(file.getOriginalFilename());
            
            // 执行识别
            RecognitionResponse.RecognitionInfo result = recognitionService.recognizeImage(userId, request);
            return ApiResponse.success(result);
        } catch (IOException e) {
            log.error("文件上传失败: userId={}, error={}", userId, e.getMessage(), e);
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "文件上传失败: " + e.getMessage());
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("图像识别失败: userId={}, error={}", userId, e.getMessage(), e);
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "图像识别失败: " + e.getMessage());
        }
    }

    /**
     * 获取识别历史列表
     */
    @GetMapping("/history")
    public ApiResponse<PageResponse<RecognitionResponse.RecognitionInfo>> getHistory(
            RecognitionRequest.HistoryQueryRequest request) {
        Long userId = SecurityContextHolder.getCurrentUserId();
        log.info("获取识别历史: userId={}, page={}, size={}", 
            userId, request.getPage(), request.getSize());
        PageResponse<RecognitionResponse.RecognitionInfo> response = 
            recognitionService.getRecognitionHistory(userId, request.getPage(), request.getSize());
        return ApiResponse.success(response);
    }

    /**
     * 获取识别结果详情
     */
    @GetMapping("/{id}")
    public ApiResponse<RecognitionResponse.RecognitionInfo> getDetail(@PathVariable Long id) {
        Long userId = SecurityContextHolder.getCurrentUserId();
        log.info("获取识别详情: userId={}, id={}", userId, id);
        RecognitionResponse.RecognitionInfo result = recognitionService.getRecognitionDetail(userId, id);
        return ApiResponse.success(result);
    }

    /**
     * 删除识别记录
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteRecognition(@PathVariable Long id) {
        Long userId = SecurityContextHolder.getCurrentUserId();
        log.info("删除识别记录: userId={}, id={}", userId, id);
        recognitionService.deleteRecognitionResult(userId, id);
        return ApiResponse.success();
    }

    /**
     * 获取识别统计数据
     */
    @GetMapping("/stats")
    public ApiResponse<RecognitionResponse.RecognitionStats> getStats() {
        Long userId = SecurityContextHolder.getCurrentUserId();
        log.info("获取识别统计数据: userId={}", userId);
        RecognitionResponse.RecognitionStats stats = recognitionService.getRecognitionStats(userId);
        return ApiResponse.success(stats);
    }

    /**
     * 获取VIP识别统计数据
     */
    @GetMapping("/vip-stats")
    @Role("VIP")
    public ApiResponse<RecognitionResponse.VipRecognitionStats> getVipStats() {
        Long userId = SecurityContextHolder.getCurrentUserId();
        log.info("获取VIP识别统计数据: userId={}", userId);
        RecognitionResponse.VipRecognitionStats stats = recognitionService.getVipRecognitionStats(userId);
        return ApiResponse.success(stats);
    }

    /**
     * 获取相关识别记录（同分类）
     */
    @GetMapping("/{id}/related")
    public ApiResponse<java.util.List<RecognitionResponse.RecognitionInfo>> getRelatedRecognitions(@PathVariable Long id) {
        Long userId = SecurityContextHolder.getCurrentUserId();
        log.info("获取相关识别记录: userId={}, id={}", userId, id);
        java.util.List<RecognitionResponse.RecognitionInfo> related = recognitionService.getRelatedRecognitions(userId, id);
        return ApiResponse.success(related);
    }

    /**
     * 分享识别结果到知识库
     */
    @PostMapping("/{id}/share-to-knowledge")
    public ApiResponse<Long> shareToKnowledge(@PathVariable Long id) {
        Long userId = SecurityContextHolder.getCurrentUserId();
        log.info("分享识别结果到知识库: userId={}, recognitionId={}", userId, id);
        Long knowledgeId = recognitionService.shareToKnowledge(userId, id);
        return ApiResponse.success(knowledgeId);
    }

    /**
     * 高级图像识别（VIP功能）
     */
    @PostMapping("/advanced-recognize")
    @Role("VIP")
    public ApiResponse<RecognitionResponse.RecognitionInfo> advancedRecognize(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "settings", required = false) String settings) {
        Long userId = SecurityContextHolder.getCurrentUserId();
        log.info("高级图像识别: userId={}, fileName={}, settings={}", userId, file.getOriginalFilename(), settings);
        
        try {
            // 验证文件
            if (file.isEmpty()) {
                throw new BusinessException(ErrorCode.INVALID_PARAM, "上传文件为空");
            }
            
            // 验证文件类型
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new BusinessException(ErrorCode.INVALID_PARAM, "只支持上传图片文件");
            }
            
            // VIP用户支持更大文件（20MB）
            if (file.getSize() > 20 * 1024 * 1024) {
                throw new BusinessException(ErrorCode.INVALID_PARAM, "文件大小不能超过 20MB");
            }
            
            // 获取图片尺寸
            Integer imageWidth = null;
            Integer imageHeight = null;
            try {
                BufferedImage image = ImageIO.read(new ByteArrayInputStream(file.getBytes()));
                if (image != null) {
                    imageWidth = image.getWidth();
                    imageHeight = image.getHeight();
                    log.info("图片尺寸: {}x{}", imageWidth, imageHeight);
                }
            } catch (Exception e) {
                log.warn("获取图片尺寸失败: {}", e.getMessage());
            }
            
            // 上传到 TOS
            String imageUrl = tosUtil.uploadFile(file, "advanced-recognition");
            log.info("高级识别文件上传成功: userId={}, imageUrl={}", userId, imageUrl);
            
            // 构建高级识别请求
            RecognitionRequest.AdvancedRecognitionRequest request = new RecognitionRequest.AdvancedRecognitionRequest();
            request.setRecognitionType(1); // 高级识别类型
            request.setImageUrl(imageUrl);
            request.setImageSize((int) file.getSize());
            request.setImageWidth(imageWidth);
            request.setImageHeight(imageHeight);
            request.setImageName(file.getOriginalFilename());
            request.setSettings(settings);
            
            // 执行高级识别
            RecognitionResponse.RecognitionInfo result = recognitionService.advancedRecognizeImage(userId, request);
            return ApiResponse.success(result);
        } catch (IOException e) {
            log.error("高级识别文件上传失败: userId={}, error={}", userId, e.getMessage(), e);
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "文件上传失败: " + e.getMessage());
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("高级图像识别失败: userId={}, error={}", userId, e.getMessage(), e);
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "高级图像识别失败: " + e.getMessage());
        }
    }

    /**
     * 批量高级图像识别（VIP功能）
     */
    @PostMapping("/batch-advanced-recognize")
    @Role("VIP")
    public ApiResponse<java.util.List<RecognitionResponse.RecognitionInfo>> batchAdvancedRecognize(
            @RequestParam("files") MultipartFile[] files,
            @RequestParam(value = "settings", required = false) String settings) {
        Long userId = SecurityContextHolder.getCurrentUserId();
        log.info("批量高级图像识别: userId={}, fileCount={}, settings={}", userId, files.length, settings);
        
        try {
            // 验证文件数量
            if (files == null || files.length == 0) {
                throw new BusinessException(ErrorCode.INVALID_PARAM, "上传文件不能为空");
            }
            
            // VIP用户支持更多文件
            if (files.length > 20) {
                throw new BusinessException(ErrorCode.INVALID_PARAM, "批量高级识别最多支持20张图片");
            }
            
            // 上传所有文件到TOS
            String[] imageUrls = new String[files.length];
            for (int i = 0; i < files.length; i++) {
                MultipartFile file = files[i];
                
                // 验证文件
                if (file.isEmpty()) {
                    throw new BusinessException(ErrorCode.INVALID_PARAM, "文件 " + (i + 1) + " 为空");
                }
                
                // 验证文件类型
                String contentType = file.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    throw new BusinessException(ErrorCode.INVALID_PARAM, "文件 " + (i + 1) + " 不是图片文件");
                }
                
                // VIP用户支持更大文件（20MB）
                if (file.getSize() > 20 * 1024 * 1024) {
                    throw new BusinessException(ErrorCode.INVALID_PARAM, "文件 " + (i + 1) + " 大小不能超过 20MB");
                }
                
                // 上传到TOS
                imageUrls[i] = tosUtil.uploadFile(file, "advanced-recognition");
                log.info("高级识别文件 {} 上传成功: userId={}, imageUrl={}", i + 1, userId, imageUrls[i]);
            }
            
            // 构建批量高级识别请求
            RecognitionRequest.BatchAdvancedRecognitionRequest request = new RecognitionRequest.BatchAdvancedRecognitionRequest();
            request.setRecognitionType(1); // 高级识别类型
            request.setImageUrls(imageUrls);
            request.setSettings(settings);
            
            // 执行批量高级识别
            java.util.List<RecognitionResponse.RecognitionInfo> results = 
                recognitionService.batchAdvancedRecognizeImages(userId, request);
            
            return ApiResponse.success(results);
        } catch (IOException e) {
            log.error("批量高级识别文件上传失败: userId={}, error={}", userId, e.getMessage(), e);
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "文件上传失败: " + e.getMessage());
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("批量高级图像识别失败: userId={}, error={}", userId, e.getMessage(), e);
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "批量高级图像识别失败: " + e.getMessage());
        }
    }

    /**
     * 批量图像识别（上传多个文件）
     */
    @PostMapping("/batch-recognize")
    public ApiResponse<java.util.List<RecognitionResponse.RecognitionInfo>> batchRecognize(
            @RequestParam("files") MultipartFile[] files,
            @RequestParam(value = "recognitionType", required = false, defaultValue = "0") Integer recognitionType) {
        Long userId = SecurityContextHolder.getCurrentUserId();
        log.info("批量图像识别: userId={}, fileCount={}, recognitionType={}", userId, files.length, recognitionType);
        
        try {
            // 验证文件数量
            if (files == null || files.length == 0) {
                throw new BusinessException(ErrorCode.INVALID_PARAM, "上传文件不能为空");
            }
            
            if (files.length > 10) {
                throw new BusinessException(ErrorCode.INVALID_PARAM, "批量识别最多支持10张图片");
            }
            
            // 上传所有文件到TOS
            String[] imageUrls = new String[files.length];
            for (int i = 0; i < files.length; i++) {
                MultipartFile file = files[i];
                
                // 验证文件
                if (file.isEmpty()) {
                    throw new BusinessException(ErrorCode.INVALID_PARAM, "文件 " + (i + 1) + " 为空");
                }
                
                // 验证文件类型
                String contentType = file.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    throw new BusinessException(ErrorCode.INVALID_PARAM, "文件 " + (i + 1) + " 不是图片文件");
                }
                
                // 验证文件大小（10MB）
                if (file.getSize() > 10 * 1024 * 1024) {
                    throw new BusinessException(ErrorCode.INVALID_PARAM, "文件 " + (i + 1) + " 大小不能超过 10MB");
                }
                
                // 上传到TOS
                imageUrls[i] = tosUtil.uploadFile(file, "recognition");
                log.info("文件 {} 上传成功: userId={}, imageUrl={}", i + 1, userId, imageUrls[i]);
            }
            
            // 构建批量识别请求
            RecognitionRequest.BatchRecognitionRequest request = new RecognitionRequest.BatchRecognitionRequest();
            request.setRecognitionType(recognitionType);
            request.setImageUrls(imageUrls);
            
            // 执行批量识别
            java.util.List<RecognitionResponse.RecognitionInfo> results = 
                recognitionService.batchRecognizeImages(userId, request);
            
            return ApiResponse.success(results);
        } catch (IOException e) {
            log.error("批量文件上传失败: userId={}, error={}", userId, e.getMessage(), e);
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "文件上传失败: " + e.getMessage());
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("批量图像识别失败: userId={}, error={}", userId, e.getMessage(), e);
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "批量图像识别失败: " + e.getMessage());
        }
    }
}

