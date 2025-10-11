package com.pcf.recognition.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 文件存储服务
 */
@Service
@Slf4j
public class FileStorageService {

    @Value("${app.file.upload-dir:uploads}")
    private String uploadDir;

    @Value("${app.file.max-size:10485760}") // 10MB
    private long maxFileSize;

    /**
     * 存储文件 - 为了兼容性保留的方法名
     */
    public String storeFile(MultipartFile file) throws IOException {
        FileUploadResult result = uploadFile(file);
        return result.getUrl();
    }
    
    /**
     * 上传文件
     */
    public FileUploadResult uploadFile(MultipartFile file) throws IOException {
        // 验证文件
        validateFile(file);
        
        // 创建上传目录
        Path uploadPath = createUploadDirectory();
        
        // 生成文件名
        String filename = generateFileName(file.getOriginalFilename());
        
        // 保存文件
        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        // 构建返回结果
        FileUploadResult result = new FileUploadResult();
        result.setId(UUID.randomUUID().toString());
        result.setOriginalName(file.getOriginalFilename());
        result.setFilename(filename);
        result.setSize(file.getSize());
        result.setContentType(file.getContentType());
        result.setUploadTime(System.currentTimeMillis());
        result.setPath(filePath.toString());
        result.setUrl("/api/v1/files/" + result.getId());
        
        log.info("文件上传成功: {}", result);
        return result;
    }
    
    /**
     * 获取文件路径
     */
    public Path getFilePath(String fileId) {
        // 这里应该从数据库根据fileId查询实际文件路径
        // 暂时简化处理
        return Paths.get(uploadDir, fileId);
    }
    
    /**
     * 删除文件
     */
    public boolean deleteFile(String fileId) {
        try {
            Path filePath = getFilePath(fileId);
            return Files.deleteIfExists(filePath);
        } catch (IOException e) {
            log.error("删除文件失败: {}", fileId, e);
            return false;
        }
    }
    
    /**
     * 验证文件
     */
    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }
        
        if (file.getSize() > maxFileSize) {
            throw new IllegalArgumentException("文件大小超过限制: " + (maxFileSize / 1024 / 1024) + "MB");
        }
        
        // 验证文件类型
        String contentType = file.getContentType();
        if (contentType == null || !isAllowedContentType(contentType)) {
            throw new IllegalArgumentException("不支持的文件类型: " + contentType);
        }
    }
    
    /**
     * 检查是否为允许的文件类型
     */
    private boolean isAllowedContentType(String contentType) {
        return contentType.startsWith("image/") || 
               contentType.equals("application/pdf") ||
               contentType.startsWith("text/") ||
               contentType.equals("application/json");
    }
    
    /**
     * 创建上传目录
     */
    private Path createUploadDirectory() throws IOException {
        // 按日期创建子目录
        String dateDir = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        Path uploadPath = Paths.get(uploadDir, dateDir);
        
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        return uploadPath;
    }
    
    /**
     * 生成唯一文件名
     */
    private String generateFileName(String originalFilename) {
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        
        return UUID.randomUUID().toString() + extension;
    }
    
    /**
     * 文件上传结果
     */
    public static class FileUploadResult {
        private String id;
        private String originalName;
        private String filename;
        private long size;
        private String contentType;
        private long uploadTime;
        private String path;
        private String url;
        
        // Getters and Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        
        public String getOriginalName() { return originalName; }
        public void setOriginalName(String originalName) { this.originalName = originalName; }
        
        public String getFilename() { return filename; }
        public void setFilename(String filename) { this.filename = filename; }
        
        public long getSize() { return size; }
        public void setSize(long size) { this.size = size; }
        
        public String getContentType() { return contentType; }
        public void setContentType(String contentType) { this.contentType = contentType; }
        
        public long getUploadTime() { return uploadTime; }
        public void setUploadTime(long uploadTime) { this.uploadTime = uploadTime; }
        
        public String getPath() { return path; }
        public void setPath(String path) { this.path = path; }
        
        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }
        
        @Override
        public String toString() {
            return "FileUploadResult{" +
                    "id='" + id + '\'' +
                    ", originalName='" + originalName + '\'' +
                    ", filename='" + filename + '\'' +
                    ", size=" + size +
                    ", contentType='" + contentType + '\'' +
                    '}';
        }
    }
}