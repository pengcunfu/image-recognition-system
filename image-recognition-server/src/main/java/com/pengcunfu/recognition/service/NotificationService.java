package com.pengcunfu.recognition.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pengcunfu.recognition.constant.ErrorCode;
import com.pengcunfu.recognition.entity.Notification;
import com.pengcunfu.recognition.entity.User;
import com.pengcunfu.recognition.enums.NotificationType;
import com.pengcunfu.recognition.exception.BusinessException;
import com.pengcunfu.recognition.repository.NotificationRepository;
import com.pengcunfu.recognition.repository.UserRepository;
import com.pengcunfu.recognition.response.NotificationResponse;
import com.pengcunfu.recognition.response.PageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 通知服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 存储用户的SSE连接
    private final Map<Long, SseEmitter> sseEmitters = new ConcurrentHashMap<>();

    /**
     * 创建SSE连接
     */
    public SseEmitter createConnection(Long userId) {
        log.info("创建SSE连接: userId={}", userId);

        // 设置超时时间为30分钟
        SseEmitter emitter = new SseEmitter(30 * 60 * 1000L);

        // 移除旧连接
        SseEmitter oldEmitter = sseEmitters.remove(userId);
        if (oldEmitter != null) {
            try {
                oldEmitter.complete();
            } catch (Exception e) {
                log.warn("关闭旧SSE连接失败", e);
            }
        }

        // 保存新连接
        sseEmitters.put(userId, emitter);

        // 设置完成回调
        emitter.onCompletion(() -> {
            log.info("SSE连接完成: userId={}", userId);
            sseEmitters.remove(userId);
        });

        // 设置超时回调
        emitter.onTimeout(() -> {
            log.info("SSE连接超时: userId={}", userId);
            sseEmitters.remove(userId);
        });

        // 设置错误回调
        emitter.onError(throwable -> {
            log.error("SSE连接错误: userId={}", userId, throwable);
            sseEmitters.remove(userId);
        });

        // 发送连接成功消息
        try {
            emitter.send(SseEmitter.event()
                    .name("connected")
                    .data("Connected successfully"));
        } catch (IOException e) {
            log.error("发送连接成功消息失败", e);
        }

        return emitter;
    }

    /**
     * 发送通知
     */
    @Transactional
    public void sendNotification(Long userId, NotificationType type, String title, String content) {
        sendNotification(userId, type, title, content, null, null, null);
    }

    /**
     * 发送通知（完整版）
     */
    @Transactional
    public void sendNotification(Long userId, NotificationType type, String title, String content,
                                   String linkUrl, Long senderId, Map<String, Object> data) {
        try {
            // 构建通知对象
            Notification notification = Notification.builder()
                    .userId(userId)
                    .type(type.getValue())
                    .title(title)
                    .content(content)
                    .linkUrl(linkUrl)
                    .senderId(senderId)
                    .isRead(0)
                    .build();

            // 序列化附加数据
            if (data != null && !data.isEmpty()) {
                notification.setData(objectMapper.writeValueAsString(data));
            }

            // 保存到数据库
            notificationRepository.insert(notification);

            log.info("通知创建成功: userId={}, type={}, title={}", userId, type, title);

            // 通过SSE推送给用户
            pushToUser(userId, notification);

        } catch (Exception e) {
            log.error("发送通知失败: userId={}, type={}", userId, type, e);
        }
    }

    /**
     * 通过SSE推送通知
     */
    private void pushToUser(Long userId, Notification notification) {
        SseEmitter emitter = sseEmitters.get(userId);
        if (emitter != null) {
            try {
                NotificationResponse.NotificationInfo info = convertToNotificationInfo(notification);
                emitter.send(SseEmitter.event()
                        .name("notification")
                        .data(info));
                log.info("通知推送成功: userId={}, notificationId={}", userId, notification.getId());
            } catch (IOException e) {
                log.error("推送通知失败: userId={}", userId, e);
                sseEmitters.remove(userId);
            }
        }
    }

    /**
     * 获取用户未读通知数量
     */
    public Long getUnreadCount(Long userId) {
        return notificationRepository.countUnread(userId);
    }

    /**
     * 获取用户通知列表
     */
    public PageResponse<NotificationResponse.NotificationInfo> getNotifications(
            Long userId, Integer page, Integer size, Integer isRead) {
        log.info("获取通知列表: userId={}, page={}, size={}, isRead={}", userId, page, size, isRead);

        Page<Notification> pageRequest = new Page<>(page, size);
        Page<Notification> pageResult = notificationRepository.findByUserId(pageRequest, userId, isRead);

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
     * 标记为已读
     */
    @Transactional
    public void markAsRead(Long userId, Long notificationId) {
        log.info("标记通知为已读: userId={}, notificationId={}", userId, notificationId);

        Notification notification = notificationRepository.selectById(notificationId);
        if (notification == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "通知不存在");
        }

        if (!notification.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权操作该通知");
        }

        if (notification.getIsRead() == 0) {
            notification.setIsRead(1);
            notification.setReadTime(LocalDateTime.now());
            notificationRepository.updateById(notification);

            // 推送未读数更新
            pushUnreadCountUpdate(userId);
        }
    }

    /**
     * 全部标记为已读
     */
    @Transactional
    public void markAllAsRead(Long userId) {
        log.info("全部标记为已读: userId={}", userId);
        int count = notificationRepository.markAllAsRead(userId);
        log.info("已标记{}条通知为已读", count);

        // 推送未读数更新
        pushUnreadCountUpdate(userId);
    }

    /**
     * 删除通知
     */
    @Transactional
    public void deleteNotification(Long userId, Long notificationId) {
        log.info("删除通知: userId={}, notificationId={}", userId, notificationId);

        Notification notification = notificationRepository.selectById(notificationId);
        if (notification == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "通知不存在");
        }

        if (!notification.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权操作该通知");
        }

        notificationRepository.deleteById(notificationId);

        // 推送未读数更新
        pushUnreadCountUpdate(userId);
    }

    /**
     * 推送未读数更新
     */
    private void pushUnreadCountUpdate(Long userId) {
        SseEmitter emitter = sseEmitters.get(userId);
        if (emitter != null) {
            try {
                Long unreadCount = getUnreadCount(userId);
                emitter.send(SseEmitter.event()
                        .name("unread_count")
                        .data(unreadCount));
            } catch (IOException e) {
                log.error("推送未读数失败: userId={}", userId, e);
                sseEmitters.remove(userId);
            }
        }
    }

    /**
     * 转换为通知信息DTO
     */
    private NotificationResponse.NotificationInfo convertToNotificationInfo(Notification notification) {
        // 获取发送者信息
        String senderName = null;
        String senderAvatar = null;
        if (notification.getSenderId() != null) {
            User sender = userRepository.selectById(notification.getSenderId());
            if (sender != null) {
                senderName = sender.getNickname() != null ? sender.getNickname() : sender.getUsername();
                senderAvatar = sender.getAvatar();
            }
        }

        // 获取类型名称
        NotificationType type = NotificationType.fromValue(notification.getType());
        String typeName = type != null ? type.getDescription() : "未知";

        return NotificationResponse.NotificationInfo.builder()
                .id(notification.getId())
                .userId(notification.getUserId())
                .type(notification.getType())
                .typeName(typeName)
                .title(notification.getTitle())
                .content(notification.getContent())
                .data(notification.getData())
                .linkUrl(notification.getLinkUrl())
                .senderId(notification.getSenderId())
                .senderName(senderName)
                .senderAvatar(senderAvatar)
                .isRead(notification.getIsRead())
                .readTime(notification.getReadTime())
                .createdAt(notification.getCreatedAt())
                .build();
    }

    /**
     * 关闭用户的SSE连接
     */
    public void closeConnection(Long userId) {
        SseEmitter emitter = sseEmitters.remove(userId);
        if (emitter != null) {
            try {
                emitter.complete();
                log.info("关闭SSE连接: userId={}", userId);
            } catch (Exception e) {
                log.error("关闭SSE连接失败: userId={}", userId, e);
            }
        }
    }
}
