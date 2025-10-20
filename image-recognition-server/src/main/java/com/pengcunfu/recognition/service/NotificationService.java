package com.pengcunfu.recognition.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pengcunfu.recognition.constant.ErrorCode;
import com.pengcunfu.recognition.entity.Notification;
import com.pengcunfu.recognition.enums.NotificationType;
import com.pengcunfu.recognition.exception.BusinessException;
import com.pengcunfu.recognition.repository.NotificationRepository;
import com.pengcunfu.recognition.response.NotificationResponse;
import com.pengcunfu.recognition.response.PageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

/**
 * 通知服务
 * 处理系统通知、消息推送等业务逻辑
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    /**
     * 创建通知（使用 Builder 模式）
     */
    @Transactional
    public void createNotification(Long userId, NotificationType type, String title, String content, String linkUrl) {
        log.info("创建通知: userId={}, type={}, title={}", userId, type, title);

        Notification notification = Notification.builder()
                .userId(userId)
                .type(type.getValue())
                .title(title)
                .content(content)
                .linkUrl(linkUrl)
                .isRead(0) // 0-未读
                .build();

        notificationRepository.insert(notification);

        log.info("通知创建成功: userId={}, notificationId={}", userId, notification.getId());
    }

    /**
     * 获取用户通知列表（使用 SQL 查询）
     */
    public PageResponse<NotificationResponse.NotificationInfo> getUserNotifications(
            Long userId, Integer page, Integer size, Boolean isRead) {
        log.info("获取用户通知列表: userId={}, page={}, size={}, isRead={}", userId, page, size, isRead);

        Page<Notification> pageRequest = new Page<>(page, size);
        Integer isReadValue = isRead == null ? null : (isRead ? 1 : 0);
        
        Page<Notification> pageResult = notificationRepository.findByUserIdAndReadStatus(
                pageRequest, userId, isReadValue);

        return PageResponse.<NotificationResponse.NotificationInfo>builder()
                .data(pageResult.getRecords().stream()
                        .map(this::convertToNotificationInfo)
                        .collect(Collectors.toList()))
                .total(pageResult.getTotal())
                .page((int) pageResult.getCurrent())
                .size((int) pageResult.getSize())
                .pages((int) pageResult.getPages())
                .build();
    }

    /**
     * 标记通知为已读（使用 SQL 查询验证权限）
     */
    @Transactional
    public void markAsRead(Long userId, Long notificationId) {
        log.info("标记通知为已读: userId={}, notificationId={}", userId, notificationId);

        Notification notification = notificationRepository.findByIdAndUserId(notificationId, userId);

        if (notification == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "通知不存在或无权操作");
        }

        notification.setIsRead(1); // 1-已读
        notification.setReadTime(LocalDateTime.now());
        notificationRepository.updateById(notification);

        log.info("通知已标记为已读: userId={}, notificationId={}", userId, notificationId);
    }

    /**
     * 标记所有通知为已读（使用批量 SQL 更新）
     */
    @Transactional
    public void markAllAsRead(Long userId) {
        log.info("标记所有通知为已读: userId={}", userId);

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String readTime = now.format(formatter);
        
        int count = notificationRepository.markAllAsReadByUserId(userId, readTime);

        log.info("所有通知已标记为已读: userId={}, count={}", userId, count);
    }

    /**
     * 删除通知（使用 SQL 查询验证权限）
     */
    @Transactional
    public void deleteNotification(Long userId, Long notificationId) {
        log.info("删除通知: userId={}, notificationId={}", userId, notificationId);

        Notification notification = notificationRepository.findByIdAndUserId(notificationId, userId);

        if (notification == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "通知不存在或无权删除");
        }

        notificationRepository.deleteById(notificationId);

        log.info("通知删除成功: userId={}, notificationId={}", userId, notificationId);
    }

    /**
     * 获取未读通知数量（使用 SQL 查询）
     */
    public Long getUnreadCount(Long userId) {
        log.info("获取未读通知数量: userId={}", userId);

        return notificationRepository.countUnreadByUserId(userId);
    }

    /**
     * 转换为通知信息 DTO
     */
    private NotificationResponse.NotificationInfo convertToNotificationInfo(Notification notification) {
        return NotificationResponse.NotificationInfo.builder()
                .id(notification.getId())
                .userId(notification.getUserId())
                .type(notification.getType())
                .title(notification.getTitle())
                .content(notification.getContent())
                .linkUrl(notification.getLinkUrl())
                .senderId(notification.getSenderId())
                .isRead(notification.getIsRead())
                .readTime(notification.getReadTime())
                .createdAt(notification.getCreatedAt())
                .createTime(notification.getCreatedAt())
                .build();
    }
}

