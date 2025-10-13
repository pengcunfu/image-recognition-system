package com.pcf.recognition.service;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.pcf.recognition.dto.AuthDto.*;
import com.pcf.recognition.dto.UserDto.*;
import com.pcf.recognition.entity.User;
import com.pcf.recognition.repository.UserRepository;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
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
    public boolean updateUserInfo(Long userId, UserUpdateRequest updateData) {
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

    // ==================== 管理员用户管理方法 ====================

    /**
     * 获取用户列表（管理员）
     */
    public UserListResponse getUserList(Integer page, Integer size, String keyword, String role, String status, String sortBy, String sortOrder) {
        log.info("管理员获取用户列表: page={}, size={}, keyword={}, role={}, status={}", page, size, keyword, role, status);
        
        // 构建查询条件
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        
        // 关键词搜索（用户名、邮箱、姓名）
        if (keyword != null && !keyword.trim().isEmpty()) {
            queryWrapper.and(wrapper -> wrapper
                    .like(User::getUsername, keyword)
                    .or().like(User::getEmail, keyword)
                    .or().like(User::getName, keyword)
            );
        }
        
        // 角色筛选
        if (role != null && !role.trim().isEmpty()) {
            queryWrapper.eq(User::getRole, role);
        }
        
        // 状态筛选
        if (status != null && !status.trim().isEmpty()) {
            queryWrapper.eq(User::getStatus, status);
        }
        
        // 排序
        if ("createTime".equals(sortBy)) {
            if ("desc".equals(sortOrder)) {
                queryWrapper.orderByDesc(User::getCreateTime);
            } else {
                queryWrapper.orderByAsc(User::getCreateTime);
            }
        } else if ("updateTime".equals(sortBy)) {
            if ("desc".equals(sortOrder)) {
                queryWrapper.orderByDesc(User::getUpdateTime);
            } else {
                queryWrapper.orderByAsc(User::getUpdateTime);
            }
        } else if ("lastLoginTime".equals(sortBy)) {
            if ("desc".equals(sortOrder)) {
                queryWrapper.orderByDesc(User::getLastLoginTime);
            } else {
                queryWrapper.orderByAsc(User::getLastLoginTime);
            }
        } else {
            // 默认按创建时间倒序
            queryWrapper.orderByDesc(User::getCreateTime);
        }
        
        // 分页查询
        Page<User> pageRequest = new Page<>(page, size);
        Page<User> userPage = userRepository.selectPage(pageRequest, queryWrapper);
        
        // 转换为DTO
        List<UserListItem> users = userPage.getRecords().stream()
                .<UserListItem>map(user -> UserListItem.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .name(user.getName())
                        .email(user.getEmail())
                        .avatar(user.getAvatar())
                        .role(user.getRole().toString())
                        .status(user.getStatus().toString())
                        .vipLevel(user.getVipLevel())
                        .createTime(user.getCreateTime())
                        .lastLoginTime(user.getLastLoginTime())
                        .build())
                .collect(Collectors.toList());
        
        return UserListResponse.builder()
                .users(users)
                .total(userPage.getTotal())
                .page((int) userPage.getCurrent())
                .size((int) userPage.getSize())
                .totalPages((int) userPage.getPages())
                .build();
    }

    /**
     * 创建用户（管理员）
     */
    public CreateUserResponseDto createUser(AdminUserCreateRequest request) {
        log.info("管理员创建用户: username={}", request.getUsername());
        
        // 模拟创建用户逻辑
        // 实际项目中应该检查用户名是否已存在，然后插入数据库
        
        return CreateUserResponseDto.builder()
                .success(true)
                .message("用户创建成功")
                .userId(System.currentTimeMillis()) // 模拟生成的用户ID
                .build();
    }

    /**
     * 更新用户（管理员）
     */
    public boolean updateUser(Long id, AdminUserUpdateRequest request) {
        log.info("管理员更新用户: id={}", id);
        
        // 模拟更新用户逻辑
        // 实际项目中应该根据ID查找用户并更新相应字段
        
        return true; // 模拟更新成功
    }

    /**
     * 删除用户（管理员）
     */
    public boolean deleteUser(Long id) {
        log.info("管理员删除用户: id={}", id);
        
        // 模拟删除用户逻辑
        // 实际项目中应该软删除或硬删除用户
        
        return true; // 模拟删除成功
    }

    /**
     * 更新用户状态（管理员）
     */
    public boolean updateUserStatus(Long id, String status) {
        log.info("管理员更新用户状态: id={}, status={}", id, status);
        
        // 模拟更新用户状态逻辑
        // 实际项目中应该根据ID查找用户并更新状态
        
        return true; // 模拟更新成功
    }

    /**
     * 重置用户密码（管理员）
     */
    public boolean resetUserPassword(Long id, String newPassword) {
        log.info("管理员重置用户密码: id={}", id);
        
        // 模拟重置密码逻辑
        // 实际项目中应该加密新密码并更新到数据库
        
        return true; // 模拟重置成功
    }

    /**
     * 批量操作用户（管理员）
     */
    public BatchOperationResultDto batchUpdateUsers(List<Long> userIds, String action) {
        log.info("管理员批量操作用户: userIds={}, action={}", userIds, action);
        
        // 模拟批量操作逻辑
        List<UserOperationResult> results = new ArrayList<>();
        int successCount = 0;
        int failedCount = 0;
        
        for (Long userId : userIds) {
            boolean success = Math.random() > 0.1; // 模拟90%成功率
            if (success) {
                successCount++;
            } else {
                failedCount++;
            }
            
            results.add(UserOperationResult.builder()
                    .userId(userId)
                    .success(success)
                    .message(success ? "操作成功" : "操作失败")
                    .build());
        }
        
        return BatchOperationResultDto.builder()
                .totalCount(userIds.size())
                .successCount(successCount)
                .failedCount(failedCount)
                .results(results)
                .build();
    }

    /**
     * 获取用户概览（管理员）
     */
    public UserOverviewDto getUsersOverview() {
        log.info("管理员获取用户概览");
        
        // 模拟用户概览数据
        // 实际项目中应该从数据库统计各种用户数据
        
        return UserOverviewDto.builder()
                .totalUsers(1000L)
                .activeUsers(850L)
                .inactiveUsers(100L)
                .bannedUsers(50L)
                .vipUsers(200L)
                .adminUsers(10L)
                .todayRegistrations(15L)
                .weekRegistrations(120L)
                .monthRegistrations(500L)
                .build();
    }

    /**
     * 搜索用户（管理员）
     */
    public UserListResponse searchUsers(String keyword, Integer page, Integer size, String role, String status) {
        log.info("管理员搜索用户: keyword={}, page={}, size={}", keyword, page, size);
        
        // 模拟搜索用户逻辑
        // 实际项目中应该根据关键词在用户名、邮箱、姓名等字段中搜索
        
        return getUserList(page, size, keyword, role, status, "createTime", "desc");
    }

    /**
     * 获取用户登录历史（管理员）
     */
    public UserLoginHistoryDto getUserLoginHistory(Long id, Integer page, Integer size) {
        log.info("管理员获取用户登录历史: id={}, page={}, size={}", id, page, size);
        
        // 模拟登录历史数据
        List<UserLoginHistoryDto.LoginRecord> records = new ArrayList<>();
        
        for (int i = 0; i < size; i++) {
            records.add(UserLoginHistoryDto.LoginRecord.builder()
                    .id((long) i + 1)
                    .loginTime(LocalDateTime.now().minusDays(i))
                    .loginIp("192.168.1." + (100 + i))
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .location("北京市")
                    .success(i % 10 != 0) // 模拟偶尔登录失败
                    .build());
        }
        
        return UserLoginHistoryDto.builder()
                .records(records)
                .total(50L) // 模拟总记录数
                .page(page)
                .size(size)
                .build();
    }

}