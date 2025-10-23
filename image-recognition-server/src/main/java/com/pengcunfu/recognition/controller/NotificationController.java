package com.pengcunfu.recognition.controller;

import com.pengcunfu.recognition.annotation.Role;
import com.pengcunfu.recognition.response.ApiResponse;
import com.pengcunfu.recognition.response.NotificationResponse;
import com.pengcunfu.recognition.response.PageResponse;
import com.pengcunfu.recognition.security.SecurityContextHolder;
import com.pengcunfu.recognition.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * 通知控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * 建立SSE连接
     */
    @Role("USER")
    @GetMapping("/stream")
    public SseEmitter stream() {
        Long userId = SecurityContextHolder.getCurrentUserId();
        log.info("用户建立SSE连接: userId={}", userId);
        return notificationService.createConnection(userId);
    }

    /**
     * 获取未读通知数量
     */
    @Role("USER")
    @GetMapping("/unread-count")
    public ApiResponse<Long> getUnreadCount() {
        Long userId = SecurityContextHolder.getCurrentUserId();
        Long count = notificationService.getUnreadCount(userId);
        return ApiResponse.success(count);
    }

    /**
     * 获取通知列表
     */
    @Role("USER")
    @GetMapping
    public ApiResponse<PageResponse<NotificationResponse.NotificationInfo>> getNotifications(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer isRead) {
        Long userId = SecurityContextHolder.getCurrentUserId();
        PageResponse<NotificationResponse.NotificationInfo> response = 
            notificationService.getNotifications(userId, page, size, isRead);
        return ApiResponse.success(response);
    }

    /**
     * 标记为已读
     */
    @Role("USER")
    @PutMapping("/{notificationId}/read")
    public ApiResponse<Void> markAsRead(@PathVariable Long notificationId) {
        Long userId = SecurityContextHolder.getCurrentUserId();
        notificationService.markAsRead(userId, notificationId);
        return ApiResponse.success();
    }

    /**
     * 全部标记为已读
     */
    @Role("USER")
    @PutMapping("/read-all")
    public ApiResponse<Void> markAllAsRead() {
        Long userId = SecurityContextHolder.getCurrentUserId();
        notificationService.markAllAsRead(userId);
        return ApiResponse.success();
    }

    /**
     * 删除通知
     */
    @Role("USER")
    @DeleteMapping("/{notificationId}")
    public ApiResponse<Void> deleteNotification(@PathVariable Long notificationId) {
        Long userId = SecurityContextHolder.getCurrentUserId();
        notificationService.deleteNotification(userId, notificationId);
        return ApiResponse.success();
    }
}
