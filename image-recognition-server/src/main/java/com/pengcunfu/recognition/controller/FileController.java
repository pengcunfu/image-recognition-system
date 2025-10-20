package com.pengcunfu.recognition.controller;

import com.pengcunfu.recognition.response.ApiResponse;
import com.pengcunfu.recognition.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
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

    /**
     * 上传图片
     */
    @PostMapping("/upload/image")
    public ApiResponse<String> uploadImage(@RequestParam("file") MultipartFile file) {
        log.info("上传图片: filename={}", file.getOriginalFilename());
        String url = fileService.uploadImage(file);
        return ApiResponse.success(url);
    }

    /**
     * 批量上传图片
     */
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
    @PostMapping("/upload/file")
    public ApiResponse<String> uploadFile(@RequestParam("file") MultipartFile file) {
        log.info("上传文件: filename={}", file.getOriginalFilename());
        String url = fileService.uploadFile(file, "files");
        return ApiResponse.success(url);
    }

    /**
     * 删除文件
     */
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

