package com.pengcunfu.recognition.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pengcunfu.recognition.annotation.Role;
import com.pengcunfu.recognition.constant.ErrorCode;
import com.pengcunfu.recognition.entity.CommunityPost;
import com.pengcunfu.recognition.entity.Knowledge;
import com.pengcunfu.recognition.entity.RecognitionResult;
import com.pengcunfu.recognition.entity.User;
import com.pengcunfu.recognition.exception.BusinessException;
import com.pengcunfu.recognition.repository.CommunityPostRepository;
import com.pengcunfu.recognition.repository.KnowledgeRepository;
import com.pengcunfu.recognition.repository.RecognitionResultRepository;
import com.pengcunfu.recognition.repository.UserRepository;
import com.pengcunfu.recognition.response.ApiResponse;
import com.pengcunfu.recognition.service.FileService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.text.DecimalFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

/**
 * 管理员文件管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/files")
@RequiredArgsConstructor
public class AdminFileController {

    private final FileService fileService;
    private final UserRepository userRepository;
    private final CommunityPostRepository communityPostRepository;
    private final KnowledgeRepository knowledgeRepository;
    private final RecognitionResultRepository recognitionResultRepository;

    @Value("${file.upload.path:./uploads}")
    private String uploadPath;

    /**
     * 获取文件列表（分页）
     */
    @Role("ADMIN")
    @GetMapping
    public ApiResponse<Page<FileInfo>> getFiles(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String keyword) {
        log.info("获取文件列表: page={}, size={}, type={}, keyword={}", page, size, type, keyword);

        try {
            // 确定搜索目录
            String searchDir = uploadPath;
            if (type != null && !type.isEmpty()) {
                searchDir = uploadPath + "/" + type;
            }

            File dir = new File(searchDir);
            if (!dir.exists() || !dir.isDirectory()) {
                return ApiResponse.success(new Page<>(page, size));
            }

            // 收集所有文件
            List<FileInfo> allFiles = new ArrayList<>();
            collectFiles(dir, uploadPath, allFiles, keyword);

            // 按修改时间降序排序
            allFiles.sort((f1, f2) -> f2.getModifiedTime().compareTo(f1.getModifiedTime()));

            // 手动分页
            int start = (page - 1) * size;
            int end = Math.min(start + size, allFiles.size());
            List<FileInfo> pageFiles = start < allFiles.size() ? allFiles.subList(start, end) : new ArrayList<>();

            // 检查文件引用情况
            for (FileInfo fileInfo : pageFiles) {
                checkFileUsage(fileInfo);
            }

            Page<FileInfo> result = new Page<>(page, size);
            result.setRecords(pageFiles);
            result.setTotal(allFiles.size());

            return ApiResponse.success(result);

        } catch (Exception e) {
            log.error("获取文件列表失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "获取文件列表失败: " + e.getMessage());
        }
    }

    /**
     * 递归收集文件
     */
    private void collectFiles(File dir, String basePath, List<FileInfo> fileList, String keyword) {
        File[] files = dir.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                collectFiles(file, basePath, fileList, keyword);
            } else {
                try {
                    // 跳过非图片文件
                    String name = file.getName().toLowerCase();
                    if (!name.endsWith(".jpg") && !name.endsWith(".jpeg") && 
                        !name.endsWith(".png") && !name.endsWith(".gif") && 
                        !name.endsWith(".webp") && !name.endsWith(".bmp")) {
                        continue;
                    }

                    // 关键字过滤
                    if (keyword != null && !keyword.isEmpty() && 
                        !file.getName().toLowerCase().contains(keyword.toLowerCase())) {
                        continue;
                    }

                    FileInfo fileInfo = new FileInfo();
                    fileInfo.setName(file.getName());
                    fileInfo.setSize(file.length());
                    fileInfo.setSizeFormatted(formatFileSize(file.length()));
                    
                    // 获取文件属性
                    LocalDateTime modifiedTime = LocalDateTime.ofInstant(
                            Instant.ofEpochMilli(file.lastModified()), 
                            ZoneId.systemDefault());
                    fileInfo.setModifiedTime(modifiedTime);

                    // 计算相对路径和URL
                    String relativePath = file.getAbsolutePath()
                            .substring(new File(basePath).getAbsolutePath().length())
                            .replace("\\", "/");
                    if (relativePath.startsWith("/")) {
                        relativePath = relativePath.substring(1);
                    }
                    fileInfo.setPath(relativePath);
                    fileInfo.setUrl("/api/v1/files/" + relativePath);

                    // 确定类型
                    if (relativePath.startsWith("images/")) {
                        fileInfo.setType("images");
                    } else if (relativePath.startsWith("avatars/")) {
                        fileInfo.setType("avatars");
                    } else {
                        fileInfo.setType("other");
                    }

                    fileList.add(fileInfo);
                } catch (Exception e) {
                    log.warn("读取文件属性失败: {}", file.getAbsolutePath(), e);
                }
            }
        }
    }

    /**
     * 检查文件使用情况
     */
    private void checkFileUsage(FileInfo fileInfo) {
        String url = fileInfo.getUrl();
        List<String> usedBy = new ArrayList<>();

        // 检查用户头像
        QueryWrapper<User> userQuery = new QueryWrapper<>();
        userQuery.eq("avatar", url);
        long userCount = userRepository.selectCount(userQuery);
        if (userCount > 0) {
            usedBy.add("用户头像(" + userCount + ")");
        }

        // 检查社区帖子
        QueryWrapper<CommunityPost> postQuery = new QueryWrapper<>();
        postQuery.like("images", url);
        long postCount = communityPostRepository.selectCount(postQuery);
        if (postCount > 0) {
            usedBy.add("社区帖子(" + postCount + ")");
        }

        // 检查知识库
        QueryWrapper<Knowledge> knowledgeQuery = new QueryWrapper<>();
        knowledgeQuery.like("cover_image", url)
                .or()
                .like("images", url);
        long knowledgeCount = knowledgeRepository.selectCount(knowledgeQuery);
        if (knowledgeCount > 0) {
            usedBy.add("知识库(" + knowledgeCount + ")");
        }

        // 检查识别记录
        QueryWrapper<RecognitionResult> recordQuery = new QueryWrapper<>();
        recordQuery.eq("image_url", url);
        long recordCount = recognitionResultRepository.selectCount(recordQuery);
        if (recordCount > 0) {
            usedBy.add("识别记录(" + recordCount + ")");
        }

        fileInfo.setUsedBy(usedBy);
        fileInfo.setInUse(!usedBy.isEmpty());
    }

    /**
     * 格式化文件大小
     */
    private String formatFileSize(long size) {
        if (size <= 0) return "0 B";
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    /**
     * 上传文件
     */
    @Role("ADMIN")
    @PostMapping("/upload")
    public ApiResponse<String> uploadFile(@RequestParam("file") MultipartFile file) {
        log.info("管理员上传文件: filename={}", file.getOriginalFilename());
        String url = fileService.uploadImage(file);
        return ApiResponse.success(url);
    }

    /**
     * 删除文件
     */
    @Role("ADMIN")
    @DeleteMapping
    public ApiResponse<Void> deleteFile(@RequestParam("url") String url) {
        log.info("管理员删除文件: url={}", url);

        // 创建临时FileInfo检查使用情况
        FileInfo fileInfo = new FileInfo();
        fileInfo.setUrl(url);
        checkFileUsage(fileInfo);

        if (fileInfo.getInUse()) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, 
                    "文件正在使用中，无法删除。使用位置: " + String.join(", ", fileInfo.getUsedBy()));
        }

        fileService.deleteFile(url);
        return ApiResponse.success();
    }

    /**
     * 批量删除文件
     */
    @Role("ADMIN")
    @DeleteMapping("/batch")
    public ApiResponse<Map<String, Object>> batchDeleteFiles(@RequestBody Map<String, List<String>> request) {
        List<String> urls = request.get("urls");
        log.info("管理员批量删除文件: count={}", urls.size());

        int successCount = 0;
        int failCount = 0;
        List<String> failedFiles = new ArrayList<>();

        for (String url : urls) {
            try {
                FileInfo fileInfo = new FileInfo();
                fileInfo.setUrl(url);
                checkFileUsage(fileInfo);

                if (fileInfo.getInUse()) {
                    failCount++;
                    failedFiles.add(url + " (使用中: " + String.join(", ", fileInfo.getUsedBy()) + ")");
                } else {
                    fileService.deleteFile(url);
                    successCount++;
                }
            } catch (Exception e) {
                log.error("删除文件失败: url={}", url, e);
                failCount++;
                failedFiles.add(url + " (错误: " + e.getMessage() + ")");
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("successCount", successCount);
        result.put("failCount", failCount);
        result.put("failedFiles", failedFiles);

        return ApiResponse.success(result);
    }

    /**
     * 获取文件统计信息
     */
    @Role("ADMIN")
    @GetMapping("/stats")
    public ApiResponse<FileStats> getFileStats() {
        log.info("获取文件统计信息");

        try {
            File dir = new File(uploadPath);
            if (!dir.exists()) {
                return ApiResponse.success(new FileStats());
            }

            FileStats stats = new FileStats();
            calculateStats(dir, stats);
            stats.setTotalSizeFormatted(formatFileSize(stats.getTotalSize()));

            return ApiResponse.success(stats);

        } catch (Exception e) {
            log.error("获取文件统计信息失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "获取文件统计信息失败");
        }
    }

    /**
     * 递归计算统计信息
     */
    private void calculateStats(File dir, FileStats stats) {
        File[] files = dir.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                calculateStats(file, stats);
            } else {
                String name = file.getName().toLowerCase();
                if (name.endsWith(".jpg") || name.endsWith(".jpeg") || 
                    name.endsWith(".png") || name.endsWith(".gif") || 
                    name.endsWith(".webp") || name.endsWith(".bmp")) {
                    stats.setImageCount(stats.getImageCount() + 1);
                    stats.setTotalSize(stats.getTotalSize() + file.length());
                }
            }
        }
    }

    /**
     * 文件信息DTO
     */
    @Data
    public static class FileInfo {
        private String name;
        private String path;
        private String url;
        private String type;
        private Long size;
        private String sizeFormatted;
        private LocalDateTime modifiedTime;
        private Boolean inUse = false;
        private List<String> usedBy = new ArrayList<>();
    }

    /**
     * 文件统计DTO
     */
    @Data
    public static class FileStats {
        private Integer imageCount = 0;
        private Long totalSize = 0L;
        private String totalSizeFormatted;
    }
}

