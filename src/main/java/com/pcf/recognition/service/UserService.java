package com.pcf.recognition.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.pcf.recognition.entity.User;
import com.pcf.recognition.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
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
    public Map<String, Object> getUserInfo(Long userId) {
        log.info("获取用户信息: userId={}", userId);
        
        try {
            User user = userRepository.selectById(userId);
            
            if (user == null) {
                return createErrorResponse("用户不存在");
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("user", Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "email", user.getEmail(),
                "name", user.getName() != null ? user.getName() : user.getUsername(),
                "phone", user.getPhone() != null ? user.getPhone() : "",
                "avatar", user.getAvatar() != null ? user.getAvatar() : "/api/v1/images/default-avatar.png",
                "bio", user.getBio() != null ? user.getBio() : "",
                "role", user.getRole().name().toLowerCase(),
                "status", user.getStatus().name().toLowerCase(),
                "vipLevel", user.getVipLevel(),
                "lastLoginTime", user.getLastLoginTime(),
                "createTime", user.getCreateTime()
            ));
            
            return response;
            
        } catch (Exception e) {
            log.error("获取用户信息失败", e);
            return createErrorResponse("获取用户信息失败");
        }
    }

    /**
     * 更新用户信息
     */
    public Map<String, Object> updateUserInfo(Long userId, Map<String, Object> updateData) {
        log.info("更新用户信息: userId={}", userId);
        
        try {
            User user = userRepository.selectById(userId);
            
            if (user == null) {
                return createErrorResponse("用户不存在");
            }
            
            // 使用LambdaUpdateWrapper进行更新
            LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(User::getId, userId);
            
            if (updateData.containsKey("name")) {
                updateWrapper.set(User::getName, (String) updateData.get("name"));
            }
            if (updateData.containsKey("phone")) {
                updateWrapper.set(User::getPhone, (String) updateData.get("phone"));
            }
            if (updateData.containsKey("bio")) {
                updateWrapper.set(User::getBio, (String) updateData.get("bio"));
            }
            if (updateData.containsKey("avatar")) {
                updateWrapper.set(User::getAvatar, (String) updateData.get("avatar"));
            }
            
            userRepository.update(null, updateWrapper);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "用户信息更新成功");
            
            return response;
            
        } catch (Exception e) {
            log.error("更新用户信息失败", e);
            return createErrorResponse("更新用户信息失败");
        }
    }

    /**
     * 修改密码
     */
    public Map<String, Object> changePassword(Long userId, String oldPassword, String newPassword) {
        log.info("修改密码: userId={}", userId);
        
        try {
            User user = userRepository.selectById(userId);
            
            if (user == null) {
                return createErrorResponse("用户不存在");
            }
            
            // 验证旧密码
            if (!oldPassword.equals(user.getPassword())) {
                return createErrorResponse("原密码错误");
            }
            
            // 更新密码
            userRepository.update(null, 
                new LambdaUpdateWrapper<User>()
                    .eq(User::getId, userId)
                    .set(User::getPassword, newPassword) // 生产环境应加密
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "密码修改成功");
            
            return response;
            
        } catch (Exception e) {
            log.error("修改密码失败", e);
            return createErrorResponse("修改密码失败");
        }
    }

    /**
     * 获取用户统计信息
     */
    public Map<String, Object> getUserStats(Long userId) {
        log.info("获取用户统计信息: userId={}", userId);
        
        try {
            // 这里应该从相关表查询统计数据，暂时返回模拟数据
            Map<String, Object> stats = Map.of(
                "totalRecognitions", 156,
                "successRecognitions", 148,
                "failedRecognitions", 8,
                "favoriteCount", 23,
                "totalUploadSize", 52428800L, // 字节
                "averageConfidence", 92.5,
                "lastRecognitionTime", LocalDateTime.now().minusHours(2)
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("stats", stats);
            
            return response;
            
        } catch (Exception e) {
            log.error("获取用户统计信息失败", e);
            return createErrorResponse("获取统计信息失败");
        }
    }

    /**
     * 获取用户设置
     */
    public Map<String, Object> getUserSettings(Long userId) {
        log.info("获取用户设置: userId={}", userId);
        
        try {
            // 模拟用户设置数据
            Map<String, Object> settings = Map.of(
                "theme", "light",
                "language", "zh-CN",
                "notifications", Map.of(
                    "email", true,
                    "push", false,
                    "sms", false
                ),
                "privacy", Map.of(
                    "showProfile", true,
                    "showHistory", false,
                    "allowComments", true
                ),
                "recognition", Map.of(
                    "autoSave", true,
                    "defaultConfidence", 80,
                    "maxFileSize", 10485760 // 10MB
                )
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("settings", settings);
            
            return response;
            
        } catch (Exception e) {
            log.error("获取用户设置失败", e);
            return createErrorResponse("获取用户设置失败");
        }
    }

    /**
     * 更新用户设置
     */
    public Map<String, Object> updateUserSettings(Long userId, Map<String, Object> settings) {
        log.info("更新用户设置: userId={}", userId);
        
        try {
            // 实际应该将设置保存到数据库，这里只是模拟
            log.info("用户设置已更新: {}", settings);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "设置保存成功");
            
            return response;
            
        } catch (Exception e) {
            log.error("更新用户设置失败", e);
            return createErrorResponse("保存设置失败");
        }
    }
    
    /**
     * 创建错误响应
     */
    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        return response;
    }
}