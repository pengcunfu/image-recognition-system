package com.pengcunfu.recognition.controller;

import com.pengcunfu.recognition.annotation.Role;
import com.pengcunfu.recognition.request.NotificationRequest;
import com.pengcunfu.recognition.response.ApiResponse;
import com.pengcunfu.recognition.response.NotificationResponse;
import com.pengcunfu.recognition.response.PageResponse;
import com.pengcunfu.recognition.security.SecurityContextHolder;
import com.pengcunfu.recognition.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 通知控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Role("USER")
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * 获取通知列表
     */
    @GetMapping
    public ApiResponse<PageResponse<NotificationResponse.NotificationInfo>> getNotifications(
            NotificationRequest.QueryNotificationRequest request) {
        Long userId = SecurityContextHolder.getCurrentUserId();
        log.info("获取通知列表: userId={}, page={}, size={}", 
            userId, request.getPage(), request.getSize());
        PageResponse<NotificationResponse.NotificationInfo> response = 
            notificationService.getUserNotifications(
                userId, 
                request.getPage(), 
                request.getSize(), 
                request.getUnreadOnly()
            );
        return ApiResponse.success(response);
    }

    /**
     * 获取未读通知数量
     */
    @GetMapping("/unread-count")
    public ApiResponse<Long> getUnreadCount() {
        Long userId = SecurityContextHolder.getCurrentUserId();
        log.info("获取未读通知数量: userId={}", userId);
        Long count = notificationService.getUnreadCount(userId);
        return ApiResponse.success(count);
    }

    /**
     * 标记通知为已读
     */
    @PostMapping("/{id}/read")
    public ApiResponse<Void> markAsRead(@PathVariable Long id) {
        Long userId = SecurityContextHolder.getCurrentUserId();
        log.info("标记通知已读: userId={}, notificationId={}", userId, id);
        notificationService.markAsRead(userId, id);
        return ApiResponse.success();
    }

    /**
     * 标记所有通知为已读
     */
    @PostMapping("/read-all")
    public ApiResponse<Void> markAllAsRead() {
        Long userId = SecurityContextHolder.getCurrentUserId();
        log.info("标记所有通知已读: userId={}", userId);
        notificationService.markAllAsRead(userId);
        return ApiResponse.success();
    }

    /**
     * 删除通知
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteNotification(@PathVariable Long id) {
        Long userId = SecurityContextHolder.getCurrentUserId();
        log.info("删除通知: userId={}, notificationId={}", userId, id);
        notificationService.deleteNotification(userId, id);
        return ApiResponse.success();
    }
}

