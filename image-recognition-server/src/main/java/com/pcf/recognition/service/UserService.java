package com.pcf.recognition.service;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.pcf.recognition.dto.*;
import com.pcf.recognition.dto.AuthResponses.*;
import com.pcf.recognition.entity.User;
import com.pcf.recognition.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 用户服务
 * 处理用户信息管理相关业务逻辑
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;


    /**
     * 获取用户信息
     */
    public UserInfoDto getUserInfo(Long userId) {
        log.info("获取用户信息: userId={}", userId);

        User user = userRepository.selectById(userId);

        if (user == null) {
            return null; // 或抛出异常
        }

        return UserInfoDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .name(user.getName() != null ? user.getName() : user.getUsername())
                .phone(user.getPhone() != null ? user.getPhone() : "")
                .avatar(user.getAvatar() != null ? user.getAvatar() : "/api/v1/images/default-avatar.png")
                .bio(user.getBio() != null ? user.getBio() : "")
                .role(user.getRole().name().toLowerCase())
                .status(user.getStatus().name().toLowerCase())
                .vipLevel(user.getVipLevel())
                .lastLoginTime(user.getLastLoginTime())
                .createTime(user.getCreateTime())
                .build();
    }

    /**
     * 更新用户信息
     */
    public boolean updateUserInfo(Long userId, UserUpdateDto updateData) {
        log.info("更新用户信息: userId={}", userId);

        User user = userRepository.selectById(userId);

        if (user == null) {
            return false;
        }

        // 使用LambdaUpdateWrapper进行更新
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(User::getId, userId);

        if (updateData.getName() != null) {
            updateWrapper.set(User::getName, updateData.getName());
        }
        if (updateData.getPhone() != null) {
            updateWrapper.set(User::getPhone, updateData.getPhone());
        }
        if (updateData.getBio() != null) {
            updateWrapper.set(User::getBio, updateData.getBio());
        }
        if (updateData.getAvatar() != null) {
            updateWrapper.set(User::getAvatar, updateData.getAvatar());
        }

        return userRepository.update(null, updateWrapper) > 0;
    }

    /**
     * 修改密码
     */
    public boolean changePassword(Long userId, String oldPassword, String newPassword) {
        log.info("修改密码: userId={}", userId);

        User user = userRepository.selectById(userId);

        if (user == null) {
            return false;
        }

        // 验证旧密码
        if (!oldPassword.equals(user.getPassword())) {
            throw new IllegalArgumentException("原密码错误");
        }

        // 更新密码
        return userRepository.update(null,
                new LambdaUpdateWrapper<User>()
                        .eq(User::getId, userId)
                        .set(User::getPassword, newPassword) // 生产环境应加密
        ) > 0;
    }

    /**
     * 获取用户统计信息（合并重复方法）
     */
    public UserStatsDto getUserStats(Long userId) {
        log.info("获取用户统计信息: userId={}", userId);

        User user = userRepository.selectById(userId);

        if (user == null) {
            return null;
        }

        // 这里应该从相关表查询统计数据，暂时返回模拟数据
        return UserStatsDto.builder()
                .totalRecognitions(156)
                .successRecognitions(148)
                .failedRecognitions(8)
                .favoriteCount(23)
                .totalUploadSize(52428800L) // 字节
                .averageConfidence(92.5)
                .lastRecognitionTime(LocalDateTime.now().minusHours(2))
                .postCount(15)
                .commentCount(8)
                .viewCount(120)
                .likeCount(25)
                .recognitionCount(156) // 与totalRecognitions保持一致
                .build();
    }

    /**
     * 获取用户设置
     */
    public UserSettingsDto getUserSettings(Long userId) {
        log.info("获取用户设置: userId={}", userId);

        // 模拟用户设置数据
        return UserSettingsDto.builder()
                .theme("light")
                .language("zh-CN")
                .notifications(UserSettingsDto.NotificationSettings.builder()
                        .email(true)
                        .push(false)
                        .sms(false)
                        .build())
                .privacy(UserSettingsDto.PrivacySettings.builder()
                        .showProfile(true)
                        .showHistory(false)
                        .allowComments(true)
                        .build())
                .recognition(UserSettingsDto.RecognitionSettings.builder()
                        .autoSave(true)
                        .defaultConfidence(80)
                        .maxFileSize(10485760L) // 10MB
                        .build())
                .build();
    }

    /**
     * 更新用户设置
     */
    public boolean updateUserSettings(Long userId, UserSettingsDto settings) {
        log.info("更新用户设置: userId={}", userId);

        // 实际应该将设置保存到数据库，这里只是模拟
        log.info("用户设置已更新: {}", settings);

        return true; // 模拟更新成功
    }

    /**
     * 获取用户活动记录
     */
    public List<UserActivityDto> getUserActivities(Long userId, Integer limit) {
        log.info("获取用户活动记录: userId={}, limit={}", userId, limit);

        // 模拟活动记录，实际项目中应该从活动日志表查询
        List<UserActivityDto> activities = new ArrayList<>();

        activities.add(UserActivityDto.builder()
                .id(1L)
                .type("recognition")
                .description("识别了一张金毛犬的图片，置信度 95%")
                .createTime(LocalDateTime.now().minusHours(2))
                .timeDisplay("2小时前")
                .metadata(Map.of("result", "金毛犬", "confidence", 95))
                .build());

        activities.add(UserActivityDto.builder()
                .id(2L)
                .type("post")
                .description("发布了新帖子《AI识别技巧分享》")
                .createTime(LocalDateTime.now().minusDays(1))
                .timeDisplay("1天前")
                .metadata(Map.of("postTitle", "AI识别技巧分享", "postId", 123))
                .build());

        activities.add(UserActivityDto.builder()
                .id(3L)
                .type("like")
                .description("点赞了帖子《深度学习在图像识别中的应用》")
                .createTime(LocalDateTime.now().minusDays(2))
                .timeDisplay("2天前")
                .metadata(Map.of("postTitle", "深度学习在图像识别中的应用"))
                .build());

        // 取前limit个
        return activities.stream()
                .limit(limit)
                .toList();
    }

}