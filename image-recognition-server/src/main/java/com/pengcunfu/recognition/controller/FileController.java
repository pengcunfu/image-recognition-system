package com.pengcunfu.recognition.controller;

import com.pengcunfu.recognition.annotation.Role;
import com.pengcunfu.recognition.response.ApiResponse;
import com.pengcunfu.recognition.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @Value("${file.upload.path:./uploads}")
    private String uploadPath;

    /**
     * 上传图片
     */
    @Role("USER")
    @PostMapping("/upload/image")
    public ApiResponse<String> uploadImage(@RequestParam("file") MultipartFile file) {
        log.info("上传图片: filename={}", file.getOriginalFilename());
        String url = fileService.uploadImage(file);
        return ApiResponse.success(url);
    }

    /**
     * 批量上传图片
     */
    @Role("USER")
    @PostMapping("/upload/images")
    public ApiResponse<List<String>> uploadImages(@RequestParam("files") MultipartFile[] files) {
        log.info("批量上传图片: count={}", files.length);
        List<String> urls = new ArrayList<>();
        for (MultipartFile file : files) {
            String url = fileService.uploadImage(file);
            urls.add(url);
        }
        return ApiResponse.success(urls);
    }

    /**
     * 上传文件
     */
    @Role("USER")
    @PostMapping("/upload/file")
    public ApiResponse<String> uploadFile(@RequestParam("file") MultipartFile file) {
        log.info("上传文件: filename={}", file.getOriginalFilename());
        String url = fileService.uploadFile(file, "files");
        return ApiResponse.success(url);
    }

    /**
     * 删除文件
     */
    @Role("USER")
    @DeleteMapping
    public ApiResponse<Void> deleteFile(@RequestParam("url") String url) {
        log.info("删除文件: url={}", url);
        fileService.deleteFile(url);
        return ApiResponse.success();
    }

    /**
     * 下载文件
     */
    @GetMapping("/download")
    public void downloadFile(
            @RequestParam("url") String url,
            HttpServletResponse response) throws IOException {
        log.info("下载文件: url={}", url);
        // TODO: 实现文件下载逻辑
    }
}

/**
 * 文件访问控制器
 * 处理 /api/v1/files/** 路径的文件访问
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
class FileAccessController {

    @Value("${file.upload.path:./uploads}")
    private String uploadPath;

    /**
     * 访问文件
     * 例如: /api/v1/files/images/2025/10/20/xxx.jpg
     */
    @GetMapping("/**")
    public ResponseEntity<Resource> getFile(HttpServletRequest request) {
        try {
            // 获取完整请求路径
            String requestPath = request.getRequestURI();
            // 移除 /api/v1/files/ 前缀
            String filePath = requestPath.substring("/api/v1/files/".length());
            
            log.debug("访问文件: requestPath={}, filePath={}", requestPath, filePath);
            
            // 构建完整文件路径
            Path fullPath = Paths.get(uploadPath).toAbsolutePath().normalize().resolve(filePath);
            File file = fullPath.toFile();
            
            log.debug("文件绝对路径: {}, 存在: {}", fullPath, file.exists());
            
            if (!file.exists() || !file.isFile()) {
                log.warn("文件不存在: {}", fullPath);
                return ResponseEntity.notFound().build();
            }
            
            // 安全检查：确保文件在上传目录内
            Path uploadDir = Paths.get(uploadPath).toAbsolutePath().normalize();
            if (!fullPath.startsWith(uploadDir)) {
                log.warn("非法访问路径: {}", fullPath);
                return ResponseEntity.badRequest().build();
            }
            
            Resource resource = new FileSystemResource(file);
            
            // 根据文件扩展名设置 Content-Type
            String contentType = Files.probeContentType(fullPath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }
            
            log.info("文件访问成功: path={}, size={}, type={}", filePath, file.length(), contentType);
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getName() + "\"")
                    .body(resource);
                    
        } catch (Exception e) {
            log.error("文件访问失败", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}

