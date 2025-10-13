package com.pcf.recognition.controller;

import com.pcf.recognition.dto.*;
import com.pcf.recognition.dto.AuthDto.*;
import com.pcf.recognition.dto.UserDto.*;
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
            @Valid @RequestBody UserUpdateRequest request,
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
            @Valid @RequestBody UserDto.ChangePasswordRequest request,
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

    // ==================== 管理员用户管理接口 ====================

    @GetMapping("/admin/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<UserListResponse> getUsers(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "createTime") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder) {
        
        log.info("管理员获取用户列表: page={}, size={}, keyword={}, role={}, status={}", 
                page, size, keyword, role, status);

        try {
            UserListResponse result = userService.getUserList(page, size, keyword, role, status, sortBy, sortOrder);
            return ApiResponse.success(result, "获取用户列表成功");
        } catch (Exception e) {
            log.error("获取用户列表失败", e);
            return ApiResponse.error("获取用户列表失败");
        }
    }

    @GetMapping("/admin/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<UserInfoDto> getUserDetail(@PathVariable Long id) {
        log.info("管理员获取用户详情: id={}", id);

        try {
            UserInfoDto userInfo = userService.getUserInfo(id);
            if (userInfo != null) {
                return ApiResponse.success(userInfo, "获取用户详情成功");
            } else {
                return ApiResponse.error("用户不存在");
            }
        } catch (Exception e) {
            log.error("获取用户详情失败", e);
            return ApiResponse.error("获取用户详情失败");
        }
    }

    @PostMapping("/admin/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<CreateUserResponseDto> createUser(@Valid @RequestBody AdminUserCreateRequest request) {
        log.info("管理员创建用户: username={}, email={}, role={}", 
                request.getUsername(), request.getEmail(), request.getRole());

        try {
            CreateUserResponseDto result = userService.createUser(request);
            if (result.getSuccess()) {
                return ApiResponse.success(result, "用户创建成功");
            } else {
                return ApiResponse.error(result.getMessage());
            }
        } catch (Exception e) {
            log.error("创建用户失败", e);
            return ApiResponse.error("创建用户失败: " + e.getMessage());
        }
    }

    @PutMapping("/admin/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> updateUser(@PathVariable Long id, @Valid @RequestBody AdminUserUpdateRequest request) {
        log.info("管理员更新用户: id={}", id);

        try {
            boolean success = userService.updateUser(id, request);
            if (success) {
                return ApiResponse.success(null, "用户更新成功");
            } else {
                return ApiResponse.error("用户不存在或更新失败");
            }
        } catch (Exception e) {
            log.error("更新用户失败", e);
            return ApiResponse.error("更新用户失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/admin/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> deleteUser(@PathVariable Long id) {
        log.info("管理员删除用户: id={}", id);

        try {
            boolean success = userService.deleteUser(id);
            if (success) {
                return ApiResponse.success(null, "用户删除成功");
            } else {
                return ApiResponse.error("用户不存在或删除失败");
            }
        } catch (Exception e) {
            log.error("删除用户失败", e);
            return ApiResponse.error("删除用户失败: " + e.getMessage());
        }
    }

    @PutMapping("/admin/users/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> toggleUserStatus(@PathVariable Long id, @RequestBody Map<String, String> request) {
        String status = request.get("status");
        log.info("管理员切换用户状态: id={}, status={}", id, status);

        try {
            boolean success = userService.updateUserStatus(id, status);
            if (success) {
                return ApiResponse.success(null, "用户状态更新成功");
            } else {
                return ApiResponse.error("用户不存在或状态更新失败");
            }
        } catch (Exception e) {
            log.error("更新用户状态失败", e);
            return ApiResponse.error("更新用户状态失败: " + e.getMessage());
        }
    }

    @PutMapping("/admin/users/{id}/password")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> resetPassword(@PathVariable Long id, @RequestBody Map<String, String> request) {
        String newPassword = request.get("newPassword");
        log.info("管理员重置用户密码: id={}", id);

        try {
            boolean success = userService.resetUserPassword(id, newPassword);
            if (success) {
                return ApiResponse.success(null, "密码重置成功");
            } else {
                return ApiResponse.error("用户不存在或密码重置失败");
            }
        } catch (Exception e) {
            log.error("重置用户密码失败", e);
            return ApiResponse.error("重置密码失败: " + e.getMessage());
        }
    }

    @PostMapping("/admin/users/batch")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<BatchOperationResultDto> batchUpdateUsers(@RequestBody BatchUserOperationRequest request) {
        log.info("管理员批量操作用户: action={}, userIds={}", request.getAction(), request.getUserIds());

        try {
            BatchOperationResultDto result = userService.batchUpdateUsers(request.getUserIds(), request.getAction());
            return ApiResponse.success(result, "批量操作完成");
        } catch (Exception e) {
            log.error("批量操作用户失败", e);
            return ApiResponse.error("批量操作失败: " + e.getMessage());
        }
    }

    @GetMapping("/admin/users/overview")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<UserOverviewDto> getUsersOverview() {
        log.info("管理员获取用户概览");

        try {
            UserOverviewDto overview = userService.getUsersOverview();
            return ApiResponse.success(overview, "获取用户概览成功");
        } catch (Exception e) {
            log.error("获取用户概览失败", e);
            return ApiResponse.error("获取用户概览失败");
        }
    }

    @GetMapping("/admin/users/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<UserListResponse> searchUsers(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status) {
        
        log.info("管理员搜索用户: keyword={}, page={}, size={}", keyword, page, size);

        try {
            UserListResponse result = userService.searchUsers(keyword, page, size, role, status);
            return ApiResponse.success(result, "搜索用户成功");
        } catch (Exception e) {
            log.error("搜索用户失败", e);
            return ApiResponse.error("搜索用户失败");
        }
    }

    @GetMapping("/admin/users/{id}/login-history")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<UserLoginHistoryDto> getUserLoginHistory(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        
        log.info("管理员获取用户登录历史: id={}, page={}, size={}", id, page, size);

        try {
            UserLoginHistoryDto history = userService.getUserLoginHistory(id, page, size);
            return ApiResponse.success(history, "获取登录历史成功");
        } catch (Exception e) {
            log.error("获取用户登录历史失败", e);
            return ApiResponse.error("获取登录历史失败");
        }
    }

}