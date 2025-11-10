package com.pengcunfu.recognition.util;

import com.pengcunfu.recognition.constant.FileConstants;
import com.pengcunfu.recognition.exception.FileException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.UUID;

/**
 * 文件工具类
 * 用于文件上传、删除等操作
 */
@Slf4j
@Component
public class FileUtil {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(FileConstants.DATE_PATH_FORMAT);

    /**
     * 上传文件
     */
    public String uploadFile(MultipartFile file, String uploadDir) {
        if (file == null || file.isEmpty()) {
            throw new FileException("文件为空");
        }

        // 验证文件大小
        if (file.getSize() > FileConstants.MAX_FILE_SIZE) {
            throw new FileException("文件大小超过限制");
        }

        // 获取原始文件名和扩展名
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new FileException("文件名为空");
        }

        String extension = getFileExtension(originalFilename);
        
        // 验证文件类型
        if (!isAllowedImageType(extension)) {
            throw new FileException("不支持的文件类型");
        }

        // 生成新文件名
        String newFilename = generateFilename(extension);

        // 生成日期路径
        String datePath = LocalDateTime.now().format(DATE_FORMATTER);
        String relativePath = datePath + FileConstants.FILE_SEPARATOR + newFilename;

        // 创建完整路径
        Path uploadPath = Paths.get(uploadDir, datePath);
        try {
            Files.createDirectories(uploadPath);
        } catch (IOException e) {
            log.error("创建目录失败: {}", uploadPath, e);
            throw new FileException("创建目录失败");
        }

        // 保存文件
        Path filePath = uploadPath.resolve(newFilename);
        try {
            file.transferTo(filePath.toFile());
            log.info("文件上传成功: {}", filePath);
            return relativePath;
        } catch (IOException e) {
            log.error("文件保存失败: {}", filePath, e);
            throw new FileException("文件保存失败");
        }
    }

    /**
     * 删除文件
     */
    public boolean deleteFile(String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                return file.delete();
            }
            return false;
        } catch (Exception e) {
            log.error("删除文件失败: {}", filePath, e);
            return false;
        }
    }

    /**
     * 获取文件扩展名
     */
    public String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        int lastDotIndex = filename.lastIndexOf('.');
        return lastDotIndex > 0 ? filename.substring(lastDotIndex + 1).toLowerCase() : "";
    }

    /**
     * 生成文件名
     */
    private String generateFilename(String extension) {
        return UUID.randomUUID().toString().replace("-", "") + "." + extension;
    }

    /**
     * 验证是否为允许的图片类型
     */
    public boolean isAllowedImageType(String extension) {
        return Arrays.asList(FileConstants.ALLOWED_IMAGE_EXTENSIONS).contains(extension.toLowerCase());
    }

    /**
     * 验证文件大小
     */
    public boolean isValidFileSize(long fileSize, long maxSize) {
        return fileSize > 0 && fileSize <= maxSize;
    }

    /**
     * 获取文件MIME类型
     */
    public String getFileMimeType(String filename) {
        try {
            Path path = Paths.get(filename);
            return Files.probeContentType(path);
        } catch (IOException e) {
            log.error("获取文件MIME类型失败: {}", filename, e);
            return "application/octet-stream";
        }
    }
}

