package com.pengcunfu.recognition.controller.admin;

import com.pengcunfu.recognition.request.admin.AdminUserRequest;
import com.pengcunfu.recognition.response.ApiResponse;
import com.pengcunfu.recognition.response.PageResponse;
import com.pengcunfu.recognition.response.UserResponse;
import com.pengcunfu.recognition.service.admin.AdminUserService;
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
public class AdminUserController {

    private final AdminUserService adminUserService;

    /**
     * 获取用户列表
     */
    @GetMapping
    public ApiResponse<PageResponse<UserResponse.UserInfo>> getUsers(
            AdminUserRequest.QueryUserRequest request) {
        log.info("管理员获取用户列表: page={}, size={}, status={}, keyword={}", 
                request.getPage(), request.getSize(), request.getStatus(), request.getKeyword());
        PageResponse<UserResponse.UserInfo> response = 
            adminUserService.getUsers(
                request.getPage(), 
                request.getSize(), 
                request.getStatus(), 
                request.getKeyword()
            );
        return ApiResponse.success(response);
    }

    /**
     * 更新用户状态
     */
    @PutMapping("/{userId}/status")
    public ApiResponse<Void> updateUserStatus(
            @PathVariable Long userId,
            @RequestParam Integer status) {
        log.info("更新用户状态: userId={}, status={}", userId, status);
        adminUserService.updateUserStatus(userId, status);
        return ApiResponse.success();
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{userId}")
    public ApiResponse<Void> deleteUser(@PathVariable Long userId) {
        log.info("删除用户: userId={}", userId);
        adminUserService.deleteUser(userId);
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
        adminUserService.resetUserPassword(userId, newPassword);
        return ApiResponse.success();
    }
}

