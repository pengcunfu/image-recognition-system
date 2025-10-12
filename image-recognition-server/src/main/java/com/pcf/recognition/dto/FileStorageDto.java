package com.pcf.recognition.dto;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.*;
import java.util.List;

/**
 * 文件相关DTO集合
 * 包含文件上传、下载、信息查询等相关的请求和响应类
 */
public class FileStorageDto {

    /**
     * 文件上传请求DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FileUploadRequest {
        @NotNull(message = "文件不能为空")
        private MultipartFile file;
        
        private String description; // 文件描述
        private String category; // 文件分类
        private List<String> tags; // 文件标签
    }

    /**
     * 批量文件上传请求DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BatchFileUploadRequest {
        @NotEmpty(message = "文件列表不能为空")
        @Size(max = 20, message = "批量上传最多支持20个文件")
        private List<MultipartFile> files;
        
        private String batchName; // 批次名称
        private String description; // 批次描述
        private String category; // 文件分类
    }

    /**
     * 文件上传结果DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FileUploadResult {
        private String id;
        private String originalName;
        private String filename;
        private long size;
        private String contentType;
        private long uploadTime;
        private String path;
        private String url;
        
        @Override
        public String toString() {
            return "FileUploadResult{" +
                    "id='" + id + '\'' +
                    ", originalName='" + originalName + '\'' +
                    ", filename='" + filename + '\'' +
                    ", size=" + size +
                    ", contentType='" + contentType + '\'' +
                    ", uploadTime=" + uploadTime +
                    ", path='" + path + '\'' +
                    ", url='" + url + '\'' +
                    '}';
        }
    }

    /**
     * 批量文件上传结果DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BatchFileUploadResult {
        private String batchId;
        private String batchName;
        private int totalFiles;
        private int successCount;
        private int failureCount;
        private List<FileUploadResult> successResults;
        private List<FileUploadError> errors;
        private long uploadTime;
    }

    /**
     * 文件上传错误DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FileUploadError {
        private String filename;
        private String errorMessage;
        private String errorCode;
    }

    /**
     * 文件信息DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FileInfo {
        private String id;
        private String name;
        private long size;
        private String contentType;
        private long lastModified;
        private long createTime;
        private String path;
        private String url;
        private String category;
        private List<String> tags;
        private String description;
        private String status; // active, deleted
    }

    /**
     * 文件删除请求DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FileDeleteRequest {
        @NotBlank(message = "文件ID不能为空")
        private String fileId;
        
        private boolean permanent = false; // 是否永久删除
        private String reason; // 删除原因
    }

    /**
     * 批量文件删除请求DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BatchFileDeleteRequest {
        @NotEmpty(message = "文件ID列表不能为空")
        private List<String> fileIds;
        
        private boolean permanent = false; // 是否永久删除
        private String reason; // 删除原因
    }

    /**
     * 文件删除结果DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FileDeleteResult {
        private String fileId;
        private boolean success;
        private String message;
    }

    /**
     * 文件查询请求DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FileQueryRequest {
        private String keyword; // 关键词搜索
        private String category; // 文件分类
        private List<String> tags; // 文件标签
        private String contentType; // 文件类型
        private Long minSize; // 最小文件大小
        private Long maxSize; // 最大文件大小
        private Long startTime; // 开始时间
        private Long endTime; // 结束时间
        
        @Min(value = 1, message = "页码不能小于1")
        private int page = 1;
        
        @Min(value = 1, message = "每页大小不能小于1")
        @Max(value = 100, message = "每页大小不能大于100")
        private int size = 20;
        
        private String sortBy = "uploadTime"; // 排序字段
        private String sortOrder = "desc"; // 排序方向
    }

    /**
     * 文件列表响应DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FileListResponse {
        private List<FileInfo> files;
        private long total;
        private int page;
        private int size;
        private int totalPages;
    }
}
