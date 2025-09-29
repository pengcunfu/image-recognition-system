package com.example.demo.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 文件存储服务
 */
@Slf4j
@Service
public class FileStorageService {
    
    @Value("${file.upload.path:uploads}")
    private String uploadPath;
    
    @Value("${server.port:8080}")
    private String serverPort;
    
    /**
     * 存储文件并返回访问URL
     */
    public String storeFile(MultipartFile file) {
        try {
            // 创建上传目录
            Path uploadDir = Paths.get(uploadPath);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }
            
            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".") 
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : "";
            
            String datePrefix = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String uniqueFilename = datePrefix + "_" + UUID.randomUUID().toString() + extension;
            
            // 保存文件
            Path targetLocation = uploadDir.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), targetLocation);
            
            // 返回访问URL
            return "http://localhost:" + serverPort + "/api/v1/files/" + uniqueFilename;
            
        } catch (IOException e) {
            log.error("文件存储失败: {}", e.getMessage());
            throw new RuntimeException("文件存储失败", e);
        }
    }
    
    /**
     * 删除文件
     */
    public boolean deleteFile(String filename) {
        try {
            Path filePath = Paths.get(uploadPath, filename);
            return Files.deleteIfExists(filePath);
        } catch (IOException e) {
            log.error("文件删除失败: {}", e.getMessage());
            return false;
        }
    }
}
