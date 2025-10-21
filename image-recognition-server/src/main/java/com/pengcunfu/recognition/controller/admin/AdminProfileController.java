package com.pengcunfu.recognition.controller.admin;

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
 * 管理后台 - 管理员个人资料控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/profile")
@RequiredArgsConstructor
@Role("ADMIN")
public class AdminProfileController {

    private final UserService userService;

    /**
     * 获取当前管理员信息
     */
    @GetMapping
    public ApiResponse<UserResponse.UserInfo> getAdminProfile() {
        Long adminId = SecurityContextHolder.getCurrentUserId();
        log.info("获取管理员信息: adminId={}", adminId);
        UserResponse.UserInfo profile = userService.getUserInfo(adminId);
        return ApiResponse.success(profile);
    }

    /**
     * 更新管理员个人资料
     */
    @PutMapping
    public ApiResponse<Void> updateAdminProfile(
            @Valid @RequestBody UserRequest.UpdateProfileRequest request) {
        Long adminId = SecurityContextHolder.getCurrentUserId();
        log.info("更新管理员个人资料: adminId={}", adminId);
        userService.updateUserInfo(adminId, request);
        return ApiResponse.success();
    }

    /**
     * 更新管理员头像
     */
    @PutMapping("/avatar")
    public ApiResponse<String> updateAvatar(@RequestParam String avatarUrl) {
        Long adminId = SecurityContextHolder.getCurrentUserId();
        log.info("更新管理员头像: adminId={}", adminId);
        
        // 创建更新请求只更新头像
        UserRequest.UpdateProfileRequest request = new UserRequest.UpdateProfileRequest();
        request.setAvatar(avatarUrl);
        userService.updateUserInfo(adminId, request);
        
        return ApiResponse.success(avatarUrl);
    }

    /**
     * 修改密码
     */
    @PutMapping("/password")
    public ApiResponse<Void> changePassword(
            @Valid @RequestBody UserRequest.ChangePasswordRequest request) {
        Long adminId = SecurityContextHolder.getCurrentUserId();
        log.info("管理员修改密码: adminId={}", adminId);
        userService.changePassword(adminId, request);
        return ApiResponse.success();
    }
}

