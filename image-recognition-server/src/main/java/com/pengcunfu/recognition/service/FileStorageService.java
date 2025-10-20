package com.pcf.recognition.service;

import com.pcf.recognition.dto.FileStorageDto.FileUploadResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.UUID;

/**
 * 文件存储服务
 */
@Service
@Slf4j
public class FileStorageService {

    private final String uploadDir;

    @Value("${app.file.max-size:10485760}") // 10MB
    private long maxFileSize;

    public FileStorageService(@Value("${file.upload-dir:./uploads}") String uploadDir) {
        this.uploadDir = uploadDir;
        try {
            Files.createDirectories(Paths.get(uploadDir));
            log.info("文件上传目录: {}", uploadDir);
        } catch (IOException e) {
            log.error("创建上传目录失败: {}", uploadDir, e);
        }
    }

    public String getUploadDir() {
        return uploadDir;
    }

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
        log.info("上传文件: originalName={}, size={}", file.getOriginalFilename(), file.getSize());

        // 创建日期子目录
        String datePath = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
        Path uploadPath = Paths.get(uploadDir, datePath);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
            log.info("创建目录: {}", uploadPath);
        }

        // 生成文件名
        String filename = generateFileName(file.getOriginalFilename());

        // 保存文件
        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // 构建访问url（带日期子目录）
        String relativePath = Paths.get(datePath, filename).toString().replace("\\", "/");

        // 构建返回结果
        FileUploadResult result = FileUploadResult.builder()
                .id(UUID.randomUUID().toString())
                .originalName(file.getOriginalFilename())
                .filename(filename)
                .size(file.getSize())
                .contentType(file.getContentType())
                .uploadTime(System.currentTimeMillis())
                .path(filePath.toString())
                .url("/api/v1/files/" + relativePath)
                .build();

        log.info("文件上传成功: path={}, url={}", result.getPath(), result.getUrl());
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
}