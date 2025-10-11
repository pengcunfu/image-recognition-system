package com.pcf.recognition.controller;

import com.pcf.recognition.dto.*;
import com.pcf.recognition.service.UserService;
import com.pcf.recognition.util.TokenUtil;



import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.*;

/**
 * 用户信息管理控制器
 * 提供用户资料、统计数据、收藏等功能
 */

@RestController
@RequestMapping("/api/v1/user")
@Slf4j
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    private final TokenUtil tokenUtil;

    
    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<UserInfoDto> getUserProfile(
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        log.info("获取用户信息请求");
        
        // 从token中解析用户ID
        Long userId = tokenUtil.getUserIdFromHeader(token);
        if (userId == null) {
            return ApiResponse.error("无效的Token");
        }
        
        UserInfoDto userInfo = userService.getUserInfo(userId);
        
        if (userInfo != null) {
            return ApiResponse.success(userInfo, "获取用户信息成功");
        } else {
            return ApiResponse.error("用户不存在");
        }
    }

    
    @PutMapping("/profile")
    @PreAuthorize("hasAnyRole('USER', 'VIP', 'ADMIN')")
    public ApiResponse<String> updateUserProfile(
            @Valid @RequestBody UserUpdateDto request,
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        log.info("更新用户信息请求: name={}", request.getName());
        
        // 从token中解析用户ID
        Long userId = tokenUtil.getUserIdFromHeader(token);
        if (userId == null) {
            return ApiResponse.error("无效的Token");
        }
        
        boolean success = userService.updateUserInfo(userId, request);
        
        if (success) {
            return ApiResponse.success(null, "用户信息更新成功");
        } else {
            return ApiResponse.error("用户不存在或更新失败");
        }
    }

    
    @GetMapping("/stats")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<UserStatsDto> getUserStats(
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        log.info("获取用户统计数据请求");
        
        // 从token中解析用户ID
        Long userId = tokenUtil.getUserIdFromHeader(token);
        if (userId == null) {
            return ApiResponse.error("无效的Token");
        }
        
        UserStatsDto stats = userService.getUserStats(userId);
        
        return ApiResponse.success(stats, "获取统计数据成功");
    }

    
    @PutMapping("/password")
    @PreAuthorize("hasAnyRole('USER', 'VIP', 'ADMIN')")
    public ApiResponse<String> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        log.info("用户修改密码请求");
        
        // 从token中解析用户ID
        Long userId = tokenUtil.getUserIdFromHeader(token);
        if (userId == null) {
            return ApiResponse.error("无效的Token");
        }
        
        try {
            boolean success = userService.changePassword(userId, request.getOldPassword(), request.getNewPassword());
            
            if (success) {
                return ApiResponse.success(null, "密码修改成功");
            } else {
                return ApiResponse.error("用户不存在");
            }
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    
    @GetMapping("/settings")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<UserSettingsDto> getUserSettings(
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        log.info("获取用户设置请求");
        
        // 从token中解析用户ID
        Long userId = tokenUtil.getUserIdFromHeader(token);
        if (userId == null) {
            return ApiResponse.error("无效的Token");
        }
        
        UserSettingsDto settings = userService.getUserSettings(userId);
        
        return ApiResponse.success(settings, "获取设置成功");
    }

    
    @PutMapping("/settings")
    @PreAuthorize("hasAnyRole('USER', 'VIP', 'ADMIN')")
    public ApiResponse<String> updateUserSettings(
            @Valid @RequestBody UserSettingsDto settings,
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        log.info("更新用户设置请求");
        
        // 从token中解析用户ID
        Long userId = tokenUtil.getUserIdFromHeader(token);
        if (userId == null) {
            return ApiResponse.error("无效的Token");
        }
        
        boolean success = userService.updateUserSettings(userId, settings);
        
        if (success) {
            return ApiResponse.success(null, "设置保存成功");
        } else {
            return ApiResponse.error("保存设置失败");
        }
    }
    
    
    @GetMapping("/statistics")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<UserStatsDto> getUserStatistics(
            @RequestHeader(value = "Authorization", required = false) String token) {
        log.info("获取用户统计数据请求");
        
        // 从token中解析用户ID
        Long userId = tokenUtil.getUserIdFromHeader(token);
        if (userId == null) {
            return ApiResponse.error("无效的Token");
        }
        
        UserStatsDto stats = userService.getUserStats(userId); // 使用统一的方法
        
        if (stats != null) {
            return ApiResponse.success(stats, "获取统计数据成功");
        } else {
            return ApiResponse.error("用户不存在");
        }
    }
    
    
    @GetMapping("/activities")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<List<UserActivityDto>> getUserActivities(
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestHeader(value = "Authorization", required = false) String token) {
        log.info("获取用户活动记录请求: limit={}", limit);
        
        // 从token中解析用户ID
        Long userId = tokenUtil.getUserIdFromHeader(token);
        if (userId == null) {
            return ApiResponse.error("无效的Token");
        }
        
        List<UserActivityDto> activities = userService.getUserActivities(userId, limit);
        
        return ApiResponse.success(activities, "获取活动记录成功");
    }

}