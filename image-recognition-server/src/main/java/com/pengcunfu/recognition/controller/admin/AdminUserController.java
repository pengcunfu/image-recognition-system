package com.pengcunfu.recognition.controller.admin;

import com.pengcunfu.recognition.annotation.Role;
import com.pengcunfu.recognition.request.UserRequest;
import com.pengcunfu.recognition.response.ApiResponse;
import com.pengcunfu.recognition.response.PageResponse;
import com.pengcunfu.recognition.response.UserResponse;
import com.pengcunfu.recognition.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 管理后台 - 用户管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@Role("ADMIN")
public class AdminUserController {

    private final UserService userService;

    /**
     * 获取用户列表
     */
    @GetMapping
    public ApiResponse<PageResponse<UserResponse.UserInfo>> getUsers(
            UserRequest.QueryUserRequest request) {
        log.info("管理员获取用户列表: page={}, size={}, status={}, role={}, keyword={}, sortBy={}, sortOrder={}", 
                request.getPage(), request.getSize(), request.getStatus(), request.getRole(), 
                request.getKeyword(), request.getSortBy(), request.getSortOrder());
        PageResponse<UserResponse.UserInfo> response = userService.getUsers(request);
        return ApiResponse.success(response);
    }

    /**
     * 创建用户
     */
    @PostMapping
    public ApiResponse<Long> createUser(@RequestBody UserRequest.CreateUserRequest request) {
        log.info("管理员创建用户: username={}, email={}, role={}", 
                request.getUsername(), request.getEmail(), request.getRole());
        Long userId = userService.createUser(request);
        return ApiResponse.success(userId);
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/{userId}")
    public ApiResponse<Void> updateUser(
            @PathVariable Long userId,
            @RequestBody UserRequest.UpdateUserRequest request) {
        log.info("管理员更新用户信息: userId={}", userId);
        userService.updateUser(userId, request);
        return ApiResponse.success();
    }

    /**
     * 更新用户状态
     */
    @PutMapping("/{userId}/status")
    public ApiResponse<Void> updateUserStatus(
            @PathVariable Long userId,
            @RequestParam Integer status) {
        log.info("更新用户状态: userId={}, status={}", userId, status);
        userService.updateUserStatus(userId, status);
        return ApiResponse.success();
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{userId}")
    public ApiResponse<Void> deleteUser(@PathVariable Long userId) {
        log.info("删除用户: userId={}", userId);
        userService.deleteUser(userId);
        return ApiResponse.success();
    }

    /**
     * 重置用户密码
     */
    @PostMapping("/{userId}/reset-password")
    public ApiResponse<Void> resetUserPassword(
            @PathVariable Long userId,
            @RequestParam String newPassword) {
        log.info("重置用户密码: userId={}", userId);
        userService.resetUserPassword(userId, newPassword);
        return ApiResponse.success();
    }
}

