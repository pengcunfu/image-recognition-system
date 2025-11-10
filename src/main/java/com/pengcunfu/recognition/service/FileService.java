package com.pengcunfu.recognition.service;

import com.pengcunfu.recognition.constant.ErrorCode;
import com.pengcunfu.recognition.constant.FileConstants;
import com.pengcunfu.recognition.exception.BusinessException;
import com.pengcunfu.recognition.exception.FileException;
import com.pengcunfu.recognition.util.FileUtil;
import com.pengcunfu.recognition.util.TosUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 文件服务
 * 处理文件上传、下载等业务逻辑
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

    private final TosUtil tosUtil;
    private final FileUtil fileUtil;

    @Value("${file.upload.path:./uploads}")
    private String uploadPath;

    @Value("${file.upload.use-tos:false}")
    private Boolean useTos;

    /**
     * 上传文件
     */
    public String uploadFile(MultipartFile file, String type) {
        log.info("上传文件: fileName={}, size={}, type={}", 
                file.getOriginalFilename(), file.getSize(), type);

        // 验证文件
        validateFile(file, type);

        try {
            // 生成文件名
            String extension = fileUtil.getFileExtension(file.getOriginalFilename());
            String fileName = UUID.randomUUID().toString().replace("-", "") + "." + extension;

            // 根据日期创建目录
            String dateDir = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            String relativePath = dateDir + "/" + fileName;

            if (useTos) {
                // 上传到TOS对象存储
                String url = tosUtil.uploadFile(file, type);
                log.info("文件上传成功(TOS): fileName={}, url={}", fileName, url);
                return url;
            } else {
                // 上传到本地文件系统
                String fullPath = uploadPath + "/" + type + "/" + relativePath;
                File destFile = new File(fullPath).getAbsoluteFile();
                
                // 确保目录存在
                if (!destFile.getParentFile().exists()) {
                    boolean created = destFile.getParentFile().mkdirs();
                    log.debug("创建目录: path={}, success={}", destFile.getParentFile().getAbsolutePath(), created);
                }

                file.transferTo(destFile);
                
                String url = "/api/v1/files/" + type + "/" + relativePath;
                log.info("文件上传成功(本地): fileName={}, path={}", fileName, destFile.getAbsolutePath());
                return url;
            }

        } catch (IOException e) {
            log.error("文件上传失败: fileName={}", file.getOriginalFilename(), e);
            throw new FileException("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 上传图片
     */
    public String uploadImage(MultipartFile file) {
        return uploadFile(file, "images");
    }

    /**
     * 上传头像
     */
    public String uploadAvatar(MultipartFile file) {
        return uploadFile(file, "avatars");
    }

    /**
     * 验证文件
     */
    private void validateFile(MultipartFile file, String type) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_PARAM, "文件不能为空");
        }

        // 验证文件大小
        long maxSize = "images".equals(type) || "avatars".equals(type) 
                ? FileConstants.MAX_IMAGE_SIZE 
                : FileConstants.MAX_FILE_SIZE;

        if (file.getSize() > maxSize) {
            throw new BusinessException(ErrorCode.INVALID_PARAM, 
                    String.format("文件大小不能超过 %dMB", maxSize / 1024 / 1024));
        }

        // 验证文件类型
        String fileName = file.getOriginalFilename();
        if (fileName == null) {
            throw new BusinessException(ErrorCode.INVALID_PARAM, "文件名不能为空");
        }

        String extension = fileUtil.getFileExtension(fileName);
        
        if ("images".equals(type) || "avatars".equals(type)) {
            if (!fileUtil.isAllowedImageType(extension)) {
                throw new BusinessException(ErrorCode.INVALID_PARAM, 
                        "不支持的图片格式，仅支持: " + String.join(", ", FileConstants.ALLOWED_IMAGE_EXTENSIONS));
            }
        }
    }

    /**
     * 删除文件
     */
    public void deleteFile(String fileUrl) {
        log.info("删除文件: fileUrl={}", fileUrl);

        try {
            if (useTos && fileUrl.startsWith("http")) {
                // 从TOS删除
                tosUtil.deleteFile(fileUrl);
                log.info("文件删除成功(TOS): fileUrl={}", fileUrl);
            } else {
                // 从本地删除
                String filePath = uploadPath + fileUrl.replace("/api/v1/files", "");
                File file = new File(filePath);
                if (file.exists() && file.delete()) {
                    log.info("文件删除成功(本地): filePath={}", filePath);
                }
            }
        } catch (Exception e) {
            log.error("文件删除失败: fileUrl={}", fileUrl, e);
            throw new FileException("文件删除失败: " + e.getMessage());
        }
    }
}

