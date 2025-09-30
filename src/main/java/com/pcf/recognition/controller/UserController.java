package com.pcf.recognition.controller;

import com.pcf.recognition.dto.ApiResponse;
import com.pcf.recognition.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 用户信息管理控制器
 * 提供用户资料、统计数据、收藏等功能
 */
@Tag(name = "用户管理", description = "用户信息、统计数据、收藏管理等接口")
@RestController
@RequestMapping("/api/v1/user")
@Slf4j
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;

    @Operation(summary = "获取用户信息", description = "获取当前登录用户的详细信息")
    @GetMapping("/profile")
    public ApiResponse<Map<String, Object>> getUserProfile(
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        log.info("获取用户信息请求");
        
        // 模拟从token中解析用户ID
        Long userId = 1L;
        
        Map<String, Object> result = userService.getUserInfo(userId);
        
        if ((Boolean) result.get("success")) {
            return ApiResponse.success((Map<String, Object>) result.get("user"), "获取用户信息成功");
        } else {
            return ApiResponse.error((String) result.get("message"));
        }
    }

    @Operation(summary = "更新用户信息", description = "更新用户基本资料")
    @PutMapping("/profile")
    public ApiResponse<Map<String, Object>> updateUserProfile(
            @Parameter(description = "用户信息更新请求") @Valid @RequestBody UpdateProfileRequest request,
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        log.info("更新用户信息请求: name={}, email={}", request.getName(), request.getEmail());
        
        // 模拟从token中解析用户ID
        Long userId = 1L;
        
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("name", request.getName());
        updateData.put("phone", request.getPhone());
        updateData.put("bio", request.getBio());
        
        Map<String, Object> result = userService.updateUserInfo(userId, updateData);
        
        if ((Boolean) result.get("success")) {
            return ApiResponse.success(null, (String) result.get("message"));
        } else {
            return ApiResponse.error((String) result.get("message"));
        }
    }

    @Operation(summary = "获取用户统计数据", description = "获取用户的识别、收藏、讨论等统计信息")
    @GetMapping("/stats")
    public ApiResponse<Map<String, Object>> getUserStats(
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        log.info("获取用户统计数据请求");
        
        // 模拟从token中解析用户ID
        Long userId = 1L;
        
        Map<String, Object> result = userService.getUserStats(userId);
        
        if ((Boolean) result.get("success")) {
            return ApiResponse.success((Map<String, Object>) result.get("stats"), "获取统计数据成功");
        } else {
            return ApiResponse.error((String) result.get("message"));
        }
    }

    @Operation(summary = "修改密码", description = "修改用户登录密码")
    @PutMapping("/password")
    public ApiResponse<String> changePassword(
            @Parameter(description = "密码修改请求") @Valid @RequestBody ChangePasswordRequest request,
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        log.info("用户修改密码请求");
        
        // 模拟从token中解析用户ID
        Long userId = 1L;
        
        Map<String, Object> result = userService.changePassword(userId, request.getOldPassword(), request.getNewPassword());
        
        if ((Boolean) result.get("success")) {
            return ApiResponse.success(null, (String) result.get("message"));
        } else {
            return ApiResponse.error((String) result.get("message"));
        }
    }

    @Operation(summary = "获取用户设置", description = "获取用户个性化设置")
    @GetMapping("/settings")
    public ApiResponse<Map<String, Object>> getUserSettings(
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        log.info("获取用户设置请求");
        
        // 模拟从token中解析用户ID
        Long userId = 1L;
        
        Map<String, Object> result = userService.getUserSettings(userId);
        
        if ((Boolean) result.get("success")) {
            return ApiResponse.success((Map<String, Object>) result.get("settings"), "获取设置成功");
        } else {
            return ApiResponse.error((String) result.get("message"));
        }
    }

    @Operation(summary = "更新用户设置", description = "更新用户个性化设置")
    @PutMapping("/settings")
    public ApiResponse<String> updateUserSettings(
            @Parameter(description = "设置更新请求") @Valid @RequestBody Map<String, Object> settings,
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        log.info("更新用户设置请求");
        
        // 模拟从token中解析用户ID
        Long userId = 1L;
        
        Map<String, Object> result = userService.updateUserSettings(userId, settings);
        
        if ((Boolean) result.get("success")) {
            return ApiResponse.success(null, (String) result.get("message"));
        } else {
            return ApiResponse.error((String) result.get("message"));
        }
    }

    // 请求数据类
    public static class UpdateProfileRequest {
        @NotBlank(message = "姓名不能为空")
        private String name;
        private String phone;
        private String bio;

        // getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getBio() { return bio; }
        public void setBio(String bio) { this.bio = bio; }
    }

    public static class ChangePasswordRequest {
        @NotBlank(message = "原密码不能为空")
        private String oldPassword;
        @NotBlank(message = "新密码不能为空")
        private String newPassword;

        // getters and setters
        public String getOldPassword() { return oldPassword; }
        public void setOldPassword(String oldPassword) { this.oldPassword = oldPassword; }
        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    }
}