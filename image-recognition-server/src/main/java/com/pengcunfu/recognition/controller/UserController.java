package com.pengcunfu.recognition.controller;

import com.pengcunfu.recognition.annotation.Role;
import com.pengcunfu.recognition.request.UserRequest;
import com.pengcunfu.recognition.response.ApiResponse;
import com.pengcunfu.recognition.response.UserResponse;
import com.pengcunfu.recognition.security.SecurityContextHolder;
import com.pengcunfu.recognition.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 用户控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Role("USER")
public class UserController {

    private final UserService userService;

    /**
     * 获取当前用户信息
     */
    @GetMapping("/profile")
    public ApiResponse<UserResponse.UserInfo> getProfile() {
        Long userId = SecurityContextHolder.getCurrentUserId();
        log.info("获取用户信息: userId={}", userId);
        UserResponse.UserInfo userInfo = userService.getUserInfo(userId);
        return ApiResponse.success(userInfo);
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/profile")
    public ApiResponse<Void> updateProfile(
            @Valid @RequestBody UserRequest.UpdateProfileRequest request) {
        Long userId = SecurityContextHolder.getCurrentUserId();
        log.info("更新用户信息: userId={}", userId);
        userService.updateUserInfo(userId, request);
        return ApiResponse.success();
    }

    /**
     * 修改密码
     */
    @PostMapping("/change-password")
    public ApiResponse<Void> changePassword(
            @Valid @RequestBody UserRequest.ChangePasswordRequest request) {
        Long userId = SecurityContextHolder.getCurrentUserId();
        log.info("修改密码: userId={}", userId);
        userService.changePassword(userId, request);
        return ApiResponse.success();
    }

    /**
     * 获取用户统计信息
     */
    @GetMapping("/stats")
    public ApiResponse<UserResponse.UserStats> getUserStats() {
        Long userId = SecurityContextHolder.getCurrentUserId();
        log.info("获取用户统计: userId={}", userId);
        UserResponse.UserStats stats = userService.getUserStats(userId);
        return ApiResponse.success(stats);
    }

    /**
     * 获取用户收藏列表
     */
    @GetMapping("/collects")
    public ApiResponse<UserResponse.UserCollections> getUserCollections(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Long userId = SecurityContextHolder.getCurrentUserId();
        log.info("获取用户收藏列表: userId={}, page={}, size={}", userId, page, size);
        UserResponse.UserCollections collections = userService.getUserCollections(userId, page, size);
        return ApiResponse.success(collections);
    }

    /**
     * 获取用户点赞列表
     */
    @GetMapping("/likes")
    public ApiResponse<UserResponse.UserLikes> getUserLikes(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Long userId = SecurityContextHolder.getCurrentUserId();
        log.info("获取用户点赞列表: userId={}, page={}, size={}", userId, page, size);
        UserResponse.UserLikes likes = userService.getUserLikes(userId, page, size);
        return ApiResponse.success(likes);
    }

    /**
     * 更新用户余额（管理员功能）
     */
    @PostMapping("/{userId}/balance")
    @Role("ADMIN")
    public ApiResponse<Void> updateUserBalance(
            @PathVariable Long userId,
            @Valid @RequestBody UserRequest.UpdateBalanceRequest request) {
        log.info("管理员更新用户余额: userId={}, type={}, amount={}", userId, request.getType(), request.getAmount());
        userService.updateUserBalance(userId, request);
        return ApiResponse.success();
    }
}

