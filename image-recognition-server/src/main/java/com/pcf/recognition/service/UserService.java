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
    public UserListResponse getUserList(Integer page, Integer size, String keyword, Integer role, Integer status, String sortBy, String sortOrder) {
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
        
        // 角色筛选（使用枚举值）
        if (role != null) {
            try {
                User.UserRole userRole = User.UserRole.fromValue(role);
                queryWrapper.eq(User::getRole, userRole);
                log.info("添加角色筛选条件: role = {} ({})", role, userRole);
            } catch (IllegalArgumentException e) {
                log.warn("无效的角色值: {}", role);
            }
        }
        
        // 状态筛选（使用枚举值）
        if (status != null) {
            try {
                User.UserStatus userStatus = User.UserStatus.fromValue(status);
                queryWrapper.eq(User::getStatus, userStatus);
                log.info("添加状态筛选条件: status = {} ({})", status, userStatus);
            } catch (IllegalArgumentException e) {
                log.warn("无效的状态值: {}", status);
            }
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
        
        try {
            // 检查用户名是否已存在
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getUsername, request.getUsername())
                       .or().eq(User::getEmail, request.getEmail());
            
            User existingUser = userRepository.selectOne(queryWrapper);
            if (existingUser != null) {
                return CreateUserResponseDto.builder()
                        .success(false)
                        .message("用户名或邮箱已存在")
                        .userId(null)
                        .build();
            }
            
            // 创建新用户
            User newUser = new User();
            newUser.setUsername(request.getUsername());
            newUser.setEmail(request.getEmail());
            newUser.setPassword(request.getPassword()); // 生产环境应该加密
            newUser.setName(request.getName());
            newUser.setPhone(request.getPhone());
            newUser.setRole(User.UserRole.valueOf(request.getRole()));
            newUser.setStatus(User.UserStatus.valueOf(request.getStatus() != null ? request.getStatus() : "ACTIVE"));
            newUser.setVipLevel(request.getVipLevel());
            newUser.setCreateTime(LocalDateTime.now());
            newUser.setUpdateTime(LocalDateTime.now());
            
            // 插入数据库
            int result = userRepository.insert(newUser);
            
            if (result > 0) {
                return CreateUserResponseDto.builder()
                        .success(true)
                        .message("用户创建成功")
                        .userId(newUser.getId())
                        .build();
            } else {
                return CreateUserResponseDto.builder()
                        .success(false)
                        .message("用户创建失败")
                        .userId(null)
                        .build();
            }
        } catch (Exception e) {
            log.error("创建用户失败", e);
            return CreateUserResponseDto.builder()
                    .success(false)
                    .message("用户创建失败: " + e.getMessage())
                    .userId(null)
                    .build();
        }
    }

    /**
     * 更新用户（管理员）
     */
    public boolean updateUser(Long id, AdminUserUpdateRequest request) {
        log.info("管理员更新用户: id={}", id);
        
        try {
            // 检查用户是否存在
            User existingUser = userRepository.selectById(id);
            if (existingUser == null) {
                log.warn("用户不存在: id={}", id);
                return false;
            }
            
            // 如果要更新用户名或邮箱，检查是否与其他用户冲突
            if (request.getUsername() != null || request.getEmail() != null) {
                LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.ne(User::getId, id); // 排除当前用户
                
                if (request.getUsername() != null) {
                    queryWrapper.eq(User::getUsername, request.getUsername());
                }
                if (request.getEmail() != null) {
                    queryWrapper.or().eq(User::getEmail, request.getEmail());
                }
                
                User conflictUser = userRepository.selectOne(queryWrapper);
                if (conflictUser != null) {
                    log.warn("用户名或邮箱已被其他用户使用: username={}, email={}", 
                            request.getUsername(), request.getEmail());
                    return false;
                }
            }
            
            // 构建更新条件
            LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(User::getId, id);
            
            // 只更新非空字段
            if (request.getUsername() != null) {
                updateWrapper.set(User::getUsername, request.getUsername());
            }
            if (request.getEmail() != null) {
                updateWrapper.set(User::getEmail, request.getEmail());
            }
            if (request.getName() != null) {
                updateWrapper.set(User::getName, request.getName());
            }
            if (request.getPhone() != null) {
                updateWrapper.set(User::getPhone, request.getPhone());
            }
            if (request.getRole() != null) {
                updateWrapper.set(User::getRole, User.UserRole.valueOf(request.getRole()));
            }
            if (request.getStatus() != null) {
                updateWrapper.set(User::getStatus, User.UserStatus.valueOf(request.getStatus()));
            }
            if (request.getVipLevel() != null) {
                updateWrapper.set(User::getVipLevel, request.getVipLevel());
            }
            if (request.getVipExpireTime() != null) {
                updateWrapper.set(User::getVipExpireTime, request.getVipExpireTime());
            }
            
            // 更新时间
            updateWrapper.set(User::getUpdateTime, LocalDateTime.now());
            
            // 执行更新
            int result = userRepository.update(null, updateWrapper);
            
            return result > 0;
        } catch (Exception e) {
            log.error("更新用户失败: id={}", id, e);
            return false;
        }
    }

    /**
     * 删除用户（管理员）
     */
    public boolean deleteUser(Long id) {
        log.info("管理员删除用户: id={}", id);
        
        try {
            // 检查用户是否存在
            User existingUser = userRepository.selectById(id);
            if (existingUser == null) {
                log.warn("用户不存在: id={}", id);
                return false;
            }
            
            // 检查是否为管理员（防止删除管理员账户）
            if (User.UserRole.ADMIN.equals(existingUser.getRole())) {
                log.warn("不能删除管理员账户: id={}", id);
                return false;
            }
            
            // 执行删除（硬删除）
            int result = userRepository.deleteById(id);
            
            if (result > 0) {
                log.info("用户删除成功: id={}", id);
                return true;
            } else {
                log.warn("用户删除失败: id={}", id);
                return false;
            }
        } catch (Exception e) {
            log.error("删除用户失败: id={}", id, e);
            return false;
        }
    }

    /**
     * 更新用户状态（管理员）
     */
    public boolean updateUserStatus(Long id, String status) {
        log.info("管理员更新用户状态: id={}, status={}", id, status);
        
        try {
            // 检查用户是否存在
            User existingUser = userRepository.selectById(id);
            if (existingUser == null) {
                log.warn("用户不存在: id={}", id);
                return false;
            }
            
            // 验证状态值
            User.UserStatus userStatus;
            try {
                userStatus = User.UserStatus.valueOf(status);
            } catch (IllegalArgumentException e) {
                log.warn("无效的用户状态: status={}", status);
                return false;
            }
            
            // 检查是否为管理员（防止禁用管理员账户）
            if (User.UserRole.ADMIN.equals(existingUser.getRole()) && User.UserStatus.BANNED.equals(userStatus)) {
                log.warn("不能禁用管理员账户: id={}", id);
                return false;
            }
            
            // 更新用户状态
            LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(User::getId, id)
                        .set(User::getStatus, userStatus)
                        .set(User::getUpdateTime, LocalDateTime.now());
            
            int result = userRepository.update(null, updateWrapper);
            
            if (result > 0) {
                log.info("用户状态更新成功: id={}, status={}", id, status);
                return true;
            } else {
                log.warn("用户状态更新失败: id={}, status={}", id, status);
                return false;
            }
        } catch (Exception e) {
            log.error("更新用户状态失败: id={}, status={}", id, status, e);
            return false;
        }
    }

    /**
     * 重置用户密码（管理员）
     */
    public boolean resetUserPassword(Long id, String newPassword) {
        log.info("管理员重置用户密码: id={}", id);
        
        try {
            // 检查用户是否存在
            User existingUser = userRepository.selectById(id);
            if (existingUser == null) {
                log.warn("用户不存在: id={}", id);
                return false;
            }
            
            // 验证新密码
            if (newPassword == null || newPassword.trim().length() < 6) {
                log.warn("新密码长度不足: id={}", id);
                return false;
            }
            
            // 更新密码
            LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(User::getId, id)
                        .set(User::getPassword, newPassword) // 生产环境应该加密
                        .set(User::getUpdateTime, LocalDateTime.now());
            
            int result = userRepository.update(null, updateWrapper);
            
            if (result > 0) {
                log.info("用户密码重置成功: id={}", id);
                return true;
            } else {
                log.warn("用户密码重置失败: id={}", id);
                return false;
            }
        } catch (Exception e) {
            log.error("重置用户密码失败: id={}", id, e);
            return false;
        }
    }

    /**
     * 批量操作用户（管理员）
     */
    public BatchOperationResultDto batchUpdateUsers(List<Long> userIds, String action) {
        log.info("管理员批量操作用户: userIds={}, action={}", userIds, action);
        
        List<UserOperationResult> results = new ArrayList<>();
        int successCount = 0;
        int failedCount = 0;
        
        for (Long userId : userIds) {
            boolean success = false;
            String message = "";
            
            try {
                // 检查用户是否存在
                User existingUser = userRepository.selectById(userId);
                if (existingUser == null) {
                    message = "用户不存在";
                } else {
                    // 检查是否为管理员（防止对管理员进行某些操作）
                    if (User.UserRole.ADMIN.equals(existingUser.getRole()) && 
                        ("delete".equals(action) || "ban".equals(action))) {
                        message = "不能对管理员执行此操作";
                    } else {
                        // 根据操作类型执行相应操作
                        switch (action) {
                            case "delete":
                                success = userRepository.deleteById(userId) > 0;
                                message = success ? "删除成功" : "删除失败";
                                break;
                            case "activate":
                                LambdaUpdateWrapper<User> activateWrapper = new LambdaUpdateWrapper<>();
                                activateWrapper.eq(User::getId, userId)
                                              .set(User::getStatus, User.UserStatus.ACTIVE)
                                              .set(User::getUpdateTime, LocalDateTime.now());
                                success = userRepository.update(null, activateWrapper) > 0;
                                message = success ? "激活成功" : "激活失败";
                                break;
                            case "deactivate":
                                LambdaUpdateWrapper<User> deactivateWrapper = new LambdaUpdateWrapper<>();
                                deactivateWrapper.eq(User::getId, userId)
                                                .set(User::getStatus, User.UserStatus.INACTIVE)
                                                .set(User::getUpdateTime, LocalDateTime.now());
                                success = userRepository.update(null, deactivateWrapper) > 0;
                                message = success ? "停用成功" : "停用失败";
                                break;
                            case "ban":
                                LambdaUpdateWrapper<User> banWrapper = new LambdaUpdateWrapper<>();
                                banWrapper.eq(User::getId, userId)
                                         .set(User::getStatus, User.UserStatus.BANNED)
                                         .set(User::getUpdateTime, LocalDateTime.now());
                                success = userRepository.update(null, banWrapper) > 0;
                                message = success ? "禁用成功" : "禁用失败";
                                break;
                            default:
                                message = "不支持的操作类型";
                        }
                    }
                }
            } catch (Exception e) {
                log.error("批量操作用户失败: userId={}, action={}", userId, action, e);
                message = "操作异常: " + e.getMessage();
            }
            
            if (success) {
                successCount++;
            } else {
                failedCount++;
            }
            
            results.add(UserOperationResult.builder()
                    .userId(userId)
                    .success(success)
                    .message(message)
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
        
        try {
            // 统计总用户数
            Long totalUsers = userRepository.selectCount(null);
            
            // 统计各状态用户数
            Long activeUsers = userRepository.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getStatus, User.UserStatus.ACTIVE)
            );
            
            Long inactiveUsers = userRepository.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getStatus, User.UserStatus.INACTIVE)
            );
            
            Long bannedUsers = userRepository.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getStatus, User.UserStatus.BANNED)
            );
            
            // 统计各角色用户数
            Long vipUsers = userRepository.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getRole, User.UserRole.VIP)
            );
            
            Long adminUsers = userRepository.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getRole, User.UserRole.ADMIN)
            );
            
            // 统计今日注册用户数
            LocalDateTime todayStart = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
            Long todayRegistrations = userRepository.selectCount(
                new LambdaQueryWrapper<User>().ge(User::getCreateTime, todayStart)
            );
            
            // 统计本周注册用户数
            LocalDateTime weekStart = LocalDateTime.now().minusDays(7);
            Long weekRegistrations = userRepository.selectCount(
                new LambdaQueryWrapper<User>().ge(User::getCreateTime, weekStart)
            );
            
            // 统计本月注册用户数
            LocalDateTime monthStart = LocalDateTime.now().minusDays(30);
            Long monthRegistrations = userRepository.selectCount(
                new LambdaQueryWrapper<User>().ge(User::getCreateTime, monthStart)
            );
            
            return UserOverviewDto.builder()
                    .totalUsers(totalUsers)
                    .activeUsers(activeUsers)
                    .inactiveUsers(inactiveUsers)
                    .bannedUsers(bannedUsers)
                    .vipUsers(vipUsers)
                    .adminUsers(adminUsers)
                    .todayRegistrations(todayRegistrations)
                    .weekRegistrations(weekRegistrations)
                    .monthRegistrations(monthRegistrations)
                    .build();
        } catch (Exception e) {
            log.error("获取用户概览失败", e);
            // 如果查询失败，返回默认值
            return UserOverviewDto.builder()
                    .totalUsers(0L)
                    .activeUsers(0L)
                    .inactiveUsers(0L)
                    .bannedUsers(0L)
                    .vipUsers(0L)
                    .adminUsers(0L)
                    .todayRegistrations(0L)
                    .weekRegistrations(0L)
                    .monthRegistrations(0L)
                    .build();
        }
    }

    /**
     * 搜索用户（管理员）
     */
    public UserListResponse searchUsers(String keyword, Integer page, Integer size, Integer role, Integer status) {
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

    // ==================== VIP管理方法 ====================

    /**
     * 获取VIP用户列表（管理员）
     */
    public UserListResponse getVipUserList(Integer page, Integer size, String keyword, String level, String status) {
        log.info("管理员获取VIP用户列表: page={}, size={}, keyword={}, level={}, status={}", 
                page, size, keyword, level, status);
        
        // 构建查询条件
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        
        // 只查询VIP用户（角色为VIP）
        queryWrapper.eq(User::getRole, User.UserRole.VIP);
        
        // 关键词搜索（用户名、邮箱、姓名）
        if (keyword != null && !keyword.trim().isEmpty()) {
            queryWrapper.and(wrapper -> wrapper
                    .like(User::getUsername, keyword)
                    .or().like(User::getEmail, keyword)
                    .or().like(User::getName, keyword)
            );
        }
        
        // VIP等级筛选
        if (level != null && !level.trim().isEmpty()) {
            queryWrapper.eq(User::getVipLevel, Integer.parseInt(level));
        }
        
        // 状态筛选（这里需要根据VIP过期时间和用户状态综合判断）
        if (status != null && !status.trim().isEmpty()) {
            switch (status) {
                case "active":
                    queryWrapper.eq(User::getStatus, User.UserStatus.ACTIVE);
                    // 可以添加VIP未过期的条件
                    break;
                case "expired":
                    // 需要根据vipExpireTime判断
                    queryWrapper.lt(User::getVipExpireTime, LocalDateTime.now());
                    break;
                case "suspended":
                    queryWrapper.in(User::getStatus, User.UserStatus.INACTIVE, User.UserStatus.BANNED);
                    break;
            }
        }
        
        // 排序：按VIP等级倒序，创建时间倒序
        queryWrapper.orderByDesc(User::getVipLevel).orderByDesc(User::getCreateTime);
        
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
     * 获取VIP统计数据（管理员）
     */
    public VipStatsDto getVipStats() {
        log.info("管理员获取VIP统计数据");
        
        try {
            // 统计VIP总数（VIP等级大于0的用户）
            Long total = userRepository.selectCount(
                new LambdaQueryWrapper<User>().gt(User::getVipLevel, 0)
            );
            
            // 统计生效中的VIP（状态为ACTIVE且未过期）
            Long active = userRepository.selectCount(
                new LambdaQueryWrapper<User>()
                    .gt(User::getVipLevel, 0)
                    .eq(User::getStatus, User.UserStatus.ACTIVE)
                    .and(wrapper -> wrapper
                        .isNull(User::getVipExpireTime)
                        .or().gt(User::getVipExpireTime, LocalDateTime.now())
                    )
            );
            
            // 统计即将过期的VIP（7天内过期）
            LocalDateTime sevenDaysLater = LocalDateTime.now().plusDays(7);
            Long expiring = userRepository.selectCount(
                new LambdaQueryWrapper<User>()
                    .gt(User::getVipLevel, 0)
                    .eq(User::getStatus, User.UserStatus.ACTIVE)
                    .between(User::getVipExpireTime, LocalDateTime.now(), sevenDaysLater)
            );
            
            // 本月收入（模拟数据，实际应该从订单表统计）
            Long monthlyRevenue = 156789L;
            
            return VipStatsDto.builder()
                    .total(total)
                    .active(active)
                    .expiring(expiring)
                    .monthlyRevenue(monthlyRevenue)
                    .build();
        } catch (Exception e) {
            log.error("获取VIP统计数据失败", e);
            // 返回默认值
            return VipStatsDto.builder()
                    .total(0L)
                    .active(0L)
                    .expiring(0L)
                    .monthlyRevenue(0L)
                    .build();
        }
    }

    /**
     * 延期VIP（管理员）
     */
    public boolean extendVip(Long id, Integer days, String reason) {
        log.info("管理员延期VIP: id={}, days={}, reason={}", id, days, reason);
        
        try {
            User user = userRepository.selectById(id);
            if (user == null || user.getVipLevel() <= 0) {
                log.warn("用户不存在或不是VIP用户: id={}", id);
                return false;
            }
            
            LocalDateTime currentExpireTime = user.getVipExpireTime();
            LocalDateTime newExpireTime;
            
            if (currentExpireTime == null || currentExpireTime.isBefore(LocalDateTime.now())) {
                // 如果没有过期时间或已过期，从当前时间开始延期
                newExpireTime = LocalDateTime.now().plusDays(days);
            } else {
                // 如果还未过期，从当前过期时间延期
                newExpireTime = currentExpireTime.plusDays(days);
            }
            
            LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(User::getId, id)
                        .set(User::getVipExpireTime, newExpireTime)
                        .set(User::getUpdateTime, LocalDateTime.now());
            
            int result = userRepository.update(null, updateWrapper);
            
            if (result > 0) {
                log.info("VIP延期成功: id={}, newExpireTime={}", id, newExpireTime);
                return true;
            } else {
                log.warn("VIP延期失败: id={}", id);
                return false;
            }
        } catch (Exception e) {
            log.error("VIP延期失败: id={}", id, e);
            return false;
        }
    }

    /**
     * 升级VIP等级（管理员）
     */
    public boolean upgradeVip(Long id, Integer level, String reason) {
        log.info("管理员升级VIP: id={}, level={}, reason={}", id, level, reason);
        
        try {
            User user = userRepository.selectById(id);
            if (user == null) {
                log.warn("用户不存在: id={}", id);
                return false;
            }
            
            if (level <= user.getVipLevel()) {
                log.warn("新等级必须高于当前等级: id={}, currentLevel={}, newLevel={}", 
                        id, user.getVipLevel(), level);
                return false;
            }
            
            LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(User::getId, id)
                        .set(User::getVipLevel, level)
                        .set(User::getRole, User.UserRole.VIP)
                        .set(User::getUpdateTime, LocalDateTime.now());
            
            int result = userRepository.update(null, updateWrapper);
            
            if (result > 0) {
                log.info("VIP升级成功: id={}, newLevel={}", id, level);
                return true;
            } else {
                log.warn("VIP升级失败: id={}", id);
                return false;
            }
        } catch (Exception e) {
            log.error("VIP升级失败: id={}", id, e);
            return false;
        }
    }

    /**
     * 降级VIP等级（管理员）
     */
    public boolean downgradeVip(Long id, Integer level, String reason) {
        log.info("管理员降级VIP: id={}, level={}, reason={}", id, level, reason);
        
        try {
            User user = userRepository.selectById(id);
            if (user == null) {
                log.warn("用户不存在: id={}", id);
                return false;
            }
            
            if (level >= user.getVipLevel()) {
                log.warn("新等级必须低于当前等级: id={}, currentLevel={}, newLevel={}", 
                        id, user.getVipLevel(), level);
                return false;
            }
            
            LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(User::getId, id)
                        .set(User::getVipLevel, level)
                        .set(User::getUpdateTime, LocalDateTime.now());
            
            // 如果降级到0，改为普通用户
            if (level <= 0) {
                updateWrapper.set(User::getRole, User.UserRole.USER);
            }
            
            int result = userRepository.update(null, updateWrapper);
            
            if (result > 0) {
                log.info("VIP降级成功: id={}, newLevel={}", id, level);
                return true;
            } else {
                log.warn("VIP降级失败: id={}", id);
                return false;
            }
        } catch (Exception e) {
            log.error("VIP降级失败: id={}", id, e);
            return false;
        }
    }

    /**
     * 切换VIP状态（管理员）
     */
    public boolean toggleVipStatus(Long id, String action, String reason) {
        log.info("管理员切换VIP状态: id={}, action={}, reason={}", id, action, reason);
        
        try {
            User user = userRepository.selectById(id);
            if (user == null || user.getVipLevel() <= 0) {
                log.warn("用户不存在或不是VIP用户: id={}", id);
                return false;
            }
            
            User.UserStatus newStatus;
            switch (action) {
                case "suspend":
                    newStatus = User.UserStatus.INACTIVE;
                    break;
                case "resume":
                    newStatus = User.UserStatus.ACTIVE;
                    break;
                default:
                    log.warn("不支持的操作类型: action={}", action);
                    return false;
            }
            
            LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(User::getId, id)
                        .set(User::getStatus, newStatus)
                        .set(User::getUpdateTime, LocalDateTime.now());
            
            int result = userRepository.update(null, updateWrapper);
            
            if (result > 0) {
                log.info("VIP状态切换成功: id={}, newStatus={}", id, newStatus);
                return true;
            } else {
                log.warn("VIP状态切换失败: id={}", id);
                return false;
            }
        } catch (Exception e) {
            log.error("VIP状态切换失败: id={}", id, e);
            return false;
        }
    }

    /**
     * 撤销VIP（管理员）
     */
    public boolean revokeVip(Long id, String reason) {
        log.info("管理员撤销VIP: id={}, reason={}", id, reason);
        
        try {
            User user = userRepository.selectById(id);
            if (user == null || user.getVipLevel() <= 0) {
                log.warn("用户不存在或不是VIP用户: id={}", id);
                return false;
            }
            
            LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(User::getId, id)
                        .set(User::getVipLevel, 0)
                        .set(User::getRole, User.UserRole.USER)
                        .set(User::getVipExpireTime, null)
                        .set(User::getUpdateTime, LocalDateTime.now());
            
            int result = userRepository.update(null, updateWrapper);
            
            if (result > 0) {
                log.info("VIP撤销成功: id={}", id);
                return true;
            } else {
                log.warn("VIP撤销失败: id={}", id);
                return false;
            }
        } catch (Exception e) {
            log.error("VIP撤销失败: id={}", id, e);
            return false;
        }
    }

    /**
     * 重置VIP用量（管理员）
     */
    public boolean resetVipUsage(Long id, String reason) {
        log.info("管理员重置VIP用量: id={}, reason={}", id, reason);
        
        try {
            User user = userRepository.selectById(id);
            if (user == null || user.getVipLevel() <= 0) {
                log.warn("用户不存在或不是VIP用户: id={}", id);
                return false;
            }
            
            // 这里应该重置用户的使用量统计
            // 实际项目中需要操作使用量统计表
            // 目前只是模拟成功
            
            log.info("VIP用量重置成功: id={}", id);
            return true;
        } catch (Exception e) {
            log.error("VIP用量重置失败: id={}", id, e);
            return false;
        }
    }

}