package com.pengcunfu.recognition.controller.admin;

import com.pengcunfu.recognition.annotation.Role;
import com.pengcunfu.recognition.response.ApiResponse;
import com.pengcunfu.recognition.response.PageResponse;
import com.pengcunfu.recognition.response.RecognitionResponse;
import com.pengcunfu.recognition.service.RecognitionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理后台 - 识别记录管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/recognition")
@RequiredArgsConstructor
@Role("ADMIN")
public class AdminRecognitionController {

    private final RecognitionService recognitionService;

    /**
     * 获取识别记录列表（管理员）
     */
    @GetMapping("/records")
    public ApiResponse<PageResponse<RecognitionResponse.RecognitionInfo>> getRecognitionRecords(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "keyword", required = false) String keyword) {
        log.info("管理员获取识别记录列表: page={}, size={}, status={}, userId={}, keyword={}",
                page, size, status, userId, keyword);
        PageResponse<RecognitionResponse.RecognitionInfo> response =
            recognitionService.getRecordsAdmin(page, size, status, userId, keyword);
        return ApiResponse.success(response);
    }

    /**
     * 删除识别记录
     */
    @DeleteMapping("/records/{recordId}")
    public ApiResponse<Void> deleteRecognitionRecord(@PathVariable Long recordId) {
        log.info("删除识别记录: recordId={}", recordId);
        recognitionService.deleteRecord(recordId);
        return ApiResponse.success();
    }

    /**
     * 批量删除识别记录
     */
    @PostMapping("/records/batch-delete")
    public ApiResponse<Void> batchDeleteRecognitionRecords(@RequestBody List<Long> ids) {
        log.info("批量删除识别记录: count={}", ids.size());
        recognitionService.batchDeleteRecords(ids);
        return ApiResponse.success();
    }
}

