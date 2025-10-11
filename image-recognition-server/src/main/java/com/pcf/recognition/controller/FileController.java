package com.pcf.recognition.controller;

import com.pcf.recognition.dto.ApiResponse;
import com.pcf.recognition.service.FileStorageService;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

/**
 * 文件管理控制器
 */

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
@Slf4j
public class FileController {

    private final FileStorageService fileStorageService;


    @PostMapping("/upload")
    @PreAuthorize("hasAnyRole('USER', 'VIP', 'ADMIN')")
    public ApiResponse<FileStorageService.FileUploadResult> uploadFile(

            @RequestParam("file") MultipartFile file) {
        try {
            FileStorageService.FileUploadResult result = fileStorageService.uploadFile(file);
            return ApiResponse.success(result, "文件上传成功");
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            log.error("文件上传失败", e);
            return ApiResponse.error("文件上传失败");
        }
    }


    @PostMapping("/upload/batch")
    @PreAuthorize("hasAnyRole('VIP', 'ADMIN')")
    public ApiResponse<List<FileStorageService.FileUploadResult>> uploadFiles(

            @RequestParam("files") MultipartFile[] files) {
        try {
            List<FileStorageService.FileUploadResult> results = Arrays.stream(files)
                    .map(file -> {
                        try {
                            return fileStorageService.uploadFile(file);
                        } catch (Exception e) {
                            log.error("文件上传失败: {}", file.getOriginalFilename(), e);
                            return null;
                        }
                    })
                    .filter(result -> result != null)
                    .toList();

            return ApiResponse.success(results, "批量上传完成，成功" + results.size() + "个文件");
        } catch (Exception e) {
            log.error("批量文件上传失败", e);
            return ApiResponse.error("批量文件上传失败");
        }
    }


    @GetMapping("/{fileId}")
    // 公开接口，无需权限验证
    public ResponseEntity<Resource> getFile(

            @PathVariable String fileId,

            @RequestParam(defaultValue = "false") boolean download) {
        try {
            Path filePath = fileStorageService.getFilePath(fileId);
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }

            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            ResponseEntity.BodyBuilder builder = ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType));

            if (download) {
                builder.header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + filePath.getFileName().toString() + "\"");
            } else {
                // 对于图片等文件，设置为内联显示
                if (contentType.startsWith("image/")) {
                    builder.header(HttpHeaders.CONTENT_DISPOSITION, "inline");
                }
            }

            return builder.body(resource);
        } catch (Exception e) {
            log.error("获取文件失败: {}", fileId, e);
            return ResponseEntity.internalServerError().build();
        }
    }


    @GetMapping("/{fileId}/download")
    // 公开接口，无需权限验证
    public ResponseEntity<Resource> downloadFile(

            @PathVariable String fileId) {
        return getFile(fileId, true);
    }


    @GetMapping("/{fileId}/preview")
    // 公开接口，无需权限验证
    public ResponseEntity<Resource> previewFile(

            @PathVariable String fileId) {
        return getFile(fileId, false);
    }


    @DeleteMapping("/{fileId}")
    @PreAuthorize("hasAnyRole('USER', 'VIP', 'ADMIN')")
    public ApiResponse<Void> deleteFile(

            @PathVariable String fileId) {
        try {
            boolean deleted = fileStorageService.deleteFile(fileId);

            if (deleted) {
                return ApiResponse.success(null, "文件删除成功");
            } else {
                return ApiResponse.error("文件不存在或删除失败");
            }
        } catch (Exception e) {
            log.error("文件删除失败: {}", fileId, e);
            return ApiResponse.error("文件删除失败");
        }
    }


    @GetMapping("/{fileId}/info")
    @PreAuthorize("hasAnyRole('USER', 'VIP', 'ADMIN')")
    public ApiResponse<FileInfo> getFileInfo(

            @PathVariable String fileId) {
        try {
            Path filePath = fileStorageService.getFilePath(fileId);

            if (!Files.exists(filePath)) {
                return ApiResponse.error("文件不存在");
            }

            FileInfo fileInfo = new FileInfo();
            fileInfo.setId(fileId);
            fileInfo.setName(filePath.getFileName().toString());
            fileInfo.setSize(Files.size(filePath));
            fileInfo.setContentType(Files.probeContentType(filePath));
            fileInfo.setLastModified(Files.getLastModifiedTime(filePath).toMillis());

            return ApiResponse.success(fileInfo, "获取文件信息成功");
        } catch (Exception e) {
            log.error("获取文件信息失败: {}", fileId, e);
            return ApiResponse.error("获取文件信息失败");
        }
    }

    /**
     * 文件信息类
     */
    public static class FileInfo {
        private String id;
        private String name;
        private long size;
        private String contentType;
        private long lastModified;

        // Getters and Setters
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public long getSize() {
            return size;
        }

        public void setSize(long size) {
            this.size = size;
        }

        public String getContentType() {
            return contentType;
        }

        public void setContentType(String contentType) {
            this.contentType = contentType;
        }

        public long getLastModified() {
            return lastModified;
        }

        public void setLastModified(long lastModified) {
            this.lastModified = lastModified;
        }
    }
}