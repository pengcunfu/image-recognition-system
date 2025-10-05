package com.pcf.recognition.controller;

import com.pcf.recognition.dto.ApiResponse;
import com.pcf.recognition.service.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
@Tag(name = "文件管理", description = "文件上传、下载、预览等相关接口")
@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
@Slf4j
public class FileController {

    private final FileStorageService fileStorageService;

    @Operation(summary = "上传单个文件", description = "上传单个文件，支持图片、文档等格式")
    @PostMapping("/upload")
    public ApiResponse<FileStorageService.FileUploadResult> uploadFile(
            @Parameter(description = "要上传的文件", required = true)
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

    @Operation(summary = "批量上传文件", description = "批量上传多个文件")
    @PostMapping("/upload/batch")
    public ApiResponse<List<FileStorageService.FileUploadResult>> uploadFiles(
            @Parameter(description = "要上传的文件列表", required = true)
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

    @Operation(summary = "获取文件", description = "根据文件ID获取文件内容，支持在线预览")
    @GetMapping("/{fileId}")
    public ResponseEntity<Resource> getFile(
            @Parameter(description = "文件ID", required = true)
            @PathVariable String fileId,
            @Parameter(description = "是否作为附件下载", example = "false")
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

    @Operation(summary = "下载文件", description = "根据文件ID下载文件")
    @GetMapping("/{fileId}/download")
    public ResponseEntity<Resource> downloadFile(
            @Parameter(description = "文件ID", required = true)
            @PathVariable String fileId) {
        return getFile(fileId, true);
    }

    @Operation(summary = "预览文件", description = "根据文件ID预览文件（主要用于图片）")
    @GetMapping("/{fileId}/preview")
    public ResponseEntity<Resource> previewFile(
            @Parameter(description = "文件ID", required = true)
            @PathVariable String fileId) {
        return getFile(fileId, false);
    }

    @Operation(summary = "删除文件", description = "根据文件ID删除文件")
    @DeleteMapping("/{fileId}")
    public ApiResponse<Void> deleteFile(
            @Parameter(description = "文件ID", required = true)
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

    @Operation(summary = "获取文件信息", description = "根据文件ID获取文件的元信息")
    @GetMapping("/{fileId}/info")
    public ApiResponse<FileInfo> getFileInfo(
            @Parameter(description = "文件ID", required = true)
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
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public long getSize() { return size; }
        public void setSize(long size) { this.size = size; }
        
        public String getContentType() { return contentType; }
        public void setContentType(String contentType) { this.contentType = contentType; }
        
        public long getLastModified() { return lastModified; }
        public void setLastModified(long lastModified) { this.lastModified = lastModified; }
    }
}