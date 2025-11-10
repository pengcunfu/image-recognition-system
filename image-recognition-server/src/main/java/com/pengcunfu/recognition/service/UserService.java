package com.pengcunfu.recognition.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pengcunfu.recognition.constant.ErrorCode;
import com.pengcunfu.recognition.entity.User;
import com.pengcunfu.recognition.enums.UserRole;
import com.pengcunfu.recognition.enums.UserStatus;
import com.pengcunfu.recognition.exception.BusinessException;
import com.pengcunfu.recognition.repository.*;
import com.pengcunfu.recognition.request.UserRequest;
import com.pengcunfu.recognition.response.PageResponse;
import com.pengcunfu.recognition.response.UserResponse;
import com.pengcunfu.recognition.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * 用户服务
 * 处理用户信息管理、个人资料等业务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordUtil passwordUtil;
    private final PasswordEncoder passwordEncoder;
    private final RecognitionResultRepository recognitionResultRepository;
    private final CommunityPostRepository communityPostRepository;
    private final CommentRepository commentRepository;
    private final UserCollectRepository userCollectRepository;
    private final UserLikeRepository userLikeRepository;

    /**
     * 获取用户列表（使用 SQL 查询，管理员功能）
     */
    public PageResponse<UserResponse.UserInfo> getUsers(UserRequest.QueryUserRequest request) {
        log.info("管理员获取用户列表: page={}, size={}, status={}, role={}, keyword={}, sortBy={}, sortOrder={}", 
                request.getPage(), request.getSize(), request.getStatus(), request.getRole(),
                request.getKeyword(), request.getSortBy(), request.getSortOrder());

        Page<User> pageRequest = new Page<>(request.getPage(), request.getSize());
        Page<User> pageResult = userRepository.findUsersForAdmin(
            pageRequest, 
            request.getStatus(), 
            request.getRole(),
            request.getKeyword(),
            request.getSortBy(),
            request.getSortOrder()
        );

        return PageResponse.<UserResponse.UserInfo>builder()
                .data(pageResult.getRecords().stream()
                        .map(this::convertToUserInfo)
                        .collect(Collectors.toList()))
                .total(pageResult.getTotal())
                .page((int) pageResult.getCurrent())
                .size((int) pageResult.getSize())
                .pages((int) pageResult.getPages())
                .build();
    }

    /**
     * 获取用户信息
     */
    public UserResponse.UserInfo getUserInfo(Long userId) {
        log.info("获取用户信息: userId={}", userId);

        User user = userRepository.selectById(userId);

        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND, "用户不存在");
        }

        return convertToUserInfo(user);
    }

    /**
     * 更新用户信息
     */
    @Transactional
    public void updateUserInfo(Long userId, UserRequest.UpdateProfileRequest request) {
        log.info("更新用户信息: userId={}", userId);

        User user = userRepository.selectById(userId);

        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND, "用户不存在");
        }

        // 更新字段
        if (request.getNickname() != null) {
            user.setNickname(request.getNickname());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getAvatar() != null) {
            user.setAvatar(request.getAvatar());
        }
        if (request.getBio() != null) {
            user.setBio(request.getBio());
        }

        userRepository.updateById(user);

        log.info("用户信息更新成功: userId={}", userId);
    }

    /**
     * 修改密码
     */
    @Transactional
    public void changePassword(Long userId, UserRequest.ChangePasswordRequest request) {
        log.info("修改密码: userId={}", userId);

        User user = userRepository.selectById(userId);

        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND, "用户不存在");
        }

        // 验证旧密码
        if (!passwordUtil.matches(request.getOldPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_PARAM, "原密码错误");
        }

        // 更新密码
        user.setPassword(passwordUtil.encode(request.getNewPassword()));
        userRepository.updateById(user);

        log.info("密码修改成功: userId={}", userId);
    }

    /**
     * 创建用户（管理员功能）
     */
    @Transactional
    public Long createUser(UserRequest.CreateUserRequest request) {
        log.info("管理员创建用户: username={}, email={}", request.getUsername(), request.getEmail());

        // 检查用户名是否已存在
        User existingUser = userRepository.findByUsername(request.getUsername());
        if (existingUser != null) {
            throw new BusinessException(ErrorCode.INVALID_PARAM, "用户名已存在");
        }

        // 检查邮箱是否已存在
        existingUser = userRepository.findByEmail(request.getEmail());
        if (existingUser != null) {
            throw new BusinessException(ErrorCode.INVALID_PARAM, "邮箱已被使用");
        }

        // 检查角色：不允许创建管理员
        Integer role = request.getRole() != null ? request.getRole() : UserRole.USER.getValue();
        if (role.equals(UserRole.ADMIN.getValue())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "不允许创建管理员账户");
        }

        // 创建用户对象
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .phone(request.getPhone())
                .avatar(request.getAvatar())
                .role(role)
                .status(request.getStatus() != null ? request.getStatus() : UserStatus.ACTIVE.getValue())
                .build();

        userRepository.insert(user);

        log.info("用户创建成功: userId={}, username={}", user.getId(), user.getUsername());
        return user.getId();
    }

    /**
     * 更新用户信息（管理员功能）
     */
    @Transactional
    public void updateUser(Long userId, UserRequest.UpdateUserRequest request) {
        log.info("管理员更新用户信息: userId={}", userId);

        User user = userRepository.selectById(userId);

        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND, "用户不存在");
        }

        // 检查邮箱是否被其他用户使用
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            User existingUser = userRepository.findByEmail(request.getEmail());
            if (existingUser != null && !existingUser.getId().equals(userId)) {
                throw new BusinessException(ErrorCode.INVALID_PARAM, "邮箱已被使用");
            }
            user.setEmail(request.getEmail());
        }

        // 检查角色：不允许修改为管理员
        if (request.getRole() != null) {
            if (request.getRole().equals(UserRole.ADMIN.getValue())) {
                throw new BusinessException(ErrorCode.FORBIDDEN, "不允许将用户修改为管理员");
            }
            // 不允许将管理员降级
            if (user.getRole().equals(UserRole.ADMIN.getValue())) {
                throw new BusinessException(ErrorCode.FORBIDDEN, "不允许修改管理员的角色");
            }
            user.setRole(request.getRole());
        }

        // 更新其他字段
        if (request.getNickname() != null) {
            user.setNickname(request.getNickname());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getAvatar() != null) {
            user.setAvatar(request.getAvatar());
        }
        if (request.getStatus() != null) {
            user.setStatus(request.getStatus());
        }

        userRepository.updateById(user);

        log.info("用户信息更新成功: userId={}", userId);
    }

    /**
     * 更新用户状态（管理员功能）
     */
    @Transactional
    public void updateUserStatus(Long userId, Integer status) {
        log.info("更新用户状态: userId={}, status={}", userId, status);

        User user = userRepository.selectById(userId);

        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND, "用户不存在");
        }

        user.setStatus(status);
        userRepository.updateById(user);

        log.info("用户状态更新成功: userId={}, status={}", userId, status);
    }

    /**
     * 删除用户（管理员功能）
     */
    @Transactional
    public void deleteUser(Long userId) {
        log.info("删除用户: userId={}", userId);

        User user = userRepository.selectById(userId);

        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND, "用户不存在");
        }

        // 检查是否是管理员
        if (user.getRole().equals(UserRole.ADMIN.getValue())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "不能删除管理员账号");
        }

        userRepository.deleteById(userId);

        log.info("用户删除成功: userId={}", userId);
    }

    /**
     * 重置用户密码（管理员功能）
     */
    @Transactional
    public void resetUserPassword(Long userId, String newPassword) {
        log.info("重置用户密码: userId={}", userId);

        User user = userRepository.selectById(userId);

        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND, "用户不存在");
        }

        user.setPassword(passwordUtil.encode(newPassword));
        userRepository.updateById(user);

        log.info("用户密码重置成功: userId={}", userId);
    }

    /**
     * 获取用户统计信息（使用 SQL 查询）
     */
    public UserResponse.UserStats getUserStats(Long userId) {
        log.info("获取用户统计信息: userId={}", userId);

        User user = userRepository.selectById(userId);

        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND, "用户不存在");
        }

        // 使用 SQL 查询用户相关统计数据
        long recognitionCount = recognitionResultRepository.countByUserId(userId);
        long postCount = communityPostRepository.countByUserId(userId);
        long commentCount = commentRepository.countByUserId(userId);
        long collectCount = userCollectRepository.countByUserId(userId);
        long likeCount = userLikeRepository.countByUserId(userId);

        return UserResponse.UserStats.builder()
                    .userId(userId)
                .recognitionCount(recognitionCount)
                .postCount(postCount)
                .commentCount(commentCount)
                .collectCount(collectCount)
                .likeCount(likeCount)
                .build();
    }

    /**
     * 获取用户登录日志
     */
    public java.util.List<UserResponse.LoginLog> getUserLoginLogs(Long userId, Integer limit) {
        log.info("获取用户登录日志: userId={}, limit={}", userId, limit);

        User user = userRepository.selectById(userId);

        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND, "用户不存在");
        }

        // 解析JSON格式的登录日志
        if (user.getLoginLogs() == null || user.getLoginLogs().isEmpty()) {
            return new java.util.ArrayList<>();
        }

        try {
            com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
            objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
            
            java.util.List<UserResponse.LoginLog> allLogs = objectMapper.readValue(
                user.getLoginLogs(),
                objectMapper.getTypeFactory().constructCollectionType(
                    java.util.List.class, 
                    UserResponse.LoginLog.class
                )
            );
            
            // 如果指定了limit，返回最近的N条记录
            if (limit != null && limit > 0 && allLogs.size() > limit) {
                return allLogs.subList(0, limit);
            }
            
            return allLogs;
        } catch (Exception e) {
            log.error("解析登录日志失败: userId={}", userId, e);
            return new java.util.ArrayList<>();
        }
    }

    /**
     * 转换为用户信息 DTO
     */
    private UserResponse.UserInfo convertToUserInfo(User user) {
        // 计算 VIP 等级
        Integer vipLevel = 0;
        if (user.getVipExpireTime() != null && user.getVipExpireTime().isAfter(LocalDateTime.now())) {
            vipLevel = 1;
        }

        return UserResponse.UserInfo.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .email(user.getEmail())
                .nickname(user.getNickname())
                .phone(user.getPhone())
                        .avatar(user.getAvatar())
                .bio(user.getBio())
                .balance(user.getBalance())
                .role(user.getRole())
                .status(user.getStatus())
                .vipLevel(vipLevel)
                .vipExpireTime(user.getVipExpireTime())
                        .lastLoginTime(user.getLastLoginTime())
                .createdAt(user.getCreatedAt())
                    .build();
    }

    // ==================== VIP用户管理（管理员功能） ====================

    /**
     * 获取VIP用户列表（使用 SQL 查询）
     */
    public PageResponse<UserResponse.UserInfo> getVipUsers(Integer page, Integer size, Integer level, Integer status, String keyword) {
        log.info("获取VIP用户列表: page={}, size={}, level={}, status={}, keyword={}", page, size, level, status, keyword);
        
        // 使用 SQL 查询方式
        Page<User> pageRequest = new Page<>(page, size);
        Page<User> pageResult = userRepository.findVipUsersForAdmin(pageRequest, status, keyword);
        
        // 注意：level 参数暂时不使用，因为 User 实体中没有 vipLevel 字段
        // 如果需要支持 level 过滤，需要：
        // 1. 在 users 表中添加 vip_level 字段
        // 2. 在 User 实体中添加对应属性
        // 3. 在 SQL 查询中添加 vip_level 条件
        
        return PageResponse.<UserResponse.UserInfo>builder()
                .data(pageResult.getRecords().stream()
                        .map(this::convertToUserInfo)
                        .collect(Collectors.toList()))
                .total(pageResult.getTotal())
                .page((int) pageResult.getCurrent())
                .size((int) pageResult.getSize())
                .pages((int) pageResult.getPages())
                .build();
    }

    /**
     * 延长VIP时长
     */
    @Transactional
    public void extendVip(Long userId, Integer days, String reason) {
        log.info("延长VIP时长: userId={}, days={}, reason={}", userId, days, reason);
        
        User user = userRepository.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND, "用户不存在");
        }
        
        // 检查是否是VIP用户
        if (!user.getRole().equals(UserRole.VIP.getValue())) {
            throw new BusinessException(ErrorCode.INVALID_PARAM, "该用户不是VIP用户");
        }
        
        // 计算新的过期时间
        LocalDateTime newExpireTime;
        if (user.getVipExpireTime() == null || user.getVipExpireTime().isBefore(LocalDateTime.now())) {
            // 如果VIP已过期或未设置，从当前时间开始计算
            newExpireTime = LocalDateTime.now().plusDays(days);
        } else {
            // 如果VIP未过期，在原有基础上延长
            newExpireTime = user.getVipExpireTime().plusDays(days);
        }
        
        user.setVipExpireTime(newExpireTime);
        userRepository.updateById(user);
        
        log.info("VIP时长延长成功: userId={}, days={}, newExpireTime={}", userId, days, newExpireTime);
    }

    /**
     * 升级VIP等级
     */
    @Transactional
    public void upgradeVip(Long userId, Integer newLevel, String reason) {
        log.info("升级VIP等级: userId={}, newLevel={}, reason={}", userId, newLevel, reason);
        
        User user = userRepository.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND, "用户不存在");
        }
        
        // 检查是否是VIP用户
        if (!user.getRole().equals(UserRole.VIP.getValue())) {
            throw new BusinessException(ErrorCode.INVALID_PARAM, "该用户不是VIP用户");
        }
        
        // 注意：User实体中没有vipLevel字段
        // 如果需要支持VIP等级功能，需要在User实体中添加vipLevel字段
        // 目前只记录日志，不做实际操作
        
        log.info("VIP等级升级（暂未实现）: userId={}, newLevel={}, reason={}", userId, newLevel, reason);
    }

    /**
     * 降级VIP等级
     */
    @Transactional
    public void downgradeVip(Long userId, Integer newLevel, String reason) {
        log.info("降级VIP等级: userId={}, newLevel={}, reason={}", userId, newLevel, reason);
        
        User user = userRepository.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND, "用户不存在");
        }
        
        // 检查是否是VIP用户
        if (!user.getRole().equals(UserRole.VIP.getValue())) {
            throw new BusinessException(ErrorCode.INVALID_PARAM, "该用户不是VIP用户");
        }
        
        // 注意：User实体中没有vipLevel字段
        // 如果需要支持VIP等级功能，需要在User实体中添加vipLevel字段
        // 目前只记录日志，不做实际操作
        
        log.info("VIP等级降级（暂未实现）: userId={}, newLevel={}, reason={}", userId, newLevel, reason);
    }

    /**
     * 切换VIP状态（暂停/恢复）
     */
    @Transactional
    public void toggleVipStatus(Long userId, String statusAction, String reason) {
        log.info("切换VIP状态: userId={}, status={}, reason={}", userId, statusAction, reason);
        
        User user = userRepository.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND, "用户不存在");
        }
        
        // 检查是否是VIP用户
        if (!user.getRole().equals(UserRole.VIP.getValue())) {
            throw new BusinessException(ErrorCode.INVALID_PARAM, "该用户不是VIP用户");
        }
        
        if ("suspend".equals(statusAction)) {
            // 暂停VIP：将status设为0（INACTIVE）
            user.setStatus(0);
            log.info("VIP服务已暂停: userId={}", userId);
        } else if ("resume".equals(statusAction)) {
            // 恢复VIP：将status设为1（ACTIVE）
            user.setStatus(1);
            log.info("VIP服务已恢复: userId={}", userId);
        } else {
            throw new BusinessException(ErrorCode.INVALID_PARAM, "无效的状态操作: " + statusAction);
        }
        
        userRepository.updateById(user);
    }

    /**
     * 重置VIP使用量
     */
    @Transactional
    public void resetVipUsage(Long userId, String resetType, String reason) {
        log.info("重置VIP使用量: userId={}, resetType={}, reason={}", userId, resetType, reason);
        
        User user = userRepository.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND, "用户不存在");
        }
        
        // 检查是否是VIP用户
        if (!user.getRole().equals(UserRole.VIP.getValue())) {
            throw new BusinessException(ErrorCode.INVALID_PARAM, "该用户不是VIP用户");
        }
        
        // TODO: 根据resetType重置相应的使用量统计
        // 这里需要实现具体的使用量重置逻辑，可能需要额外的表来记录使用量
        // 例如：
        // - all: 重置所有使用量
        // - recognitions: 只重置识别次数
        // - batch: 只重置批量任务次数
        // - api: 只重置API调用次数
        
        log.info("VIP使用量已重置: userId={}, resetType={}", userId, resetType);
    }

    /**
     * 撤销VIP权限
     */
    @Transactional
    public void revokeVip(Long userId, String reason) {
        log.info("撤销VIP权限: userId={}, reason={}", userId, reason);
        
        User user = userRepository.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND, "用户不存在");
        }
        
        // 检查是否是VIP用户
        if (!user.getRole().equals(UserRole.VIP.getValue())) {
            throw new BusinessException(ErrorCode.INVALID_PARAM, "该用户不是VIP用户");
        }
        
        // 更新role从VIP(1)改为普通用户(0)
        user.setRole(UserRole.USER.getValue());
        // 清空vipExpireTime
        user.setVipExpireTime(null);
        
        userRepository.updateById(user);
        
        log.info("VIP权限已撤销: userId={}", userId);
    }

    // ==================== 用户余额管理（管理员功能） ====================

    /**
     * 更新用户余额（管理员功能）
     */
    @Transactional
    public void updateUserBalance(Long userId, UserRequest.UpdateBalanceRequest request) {
        log.info("管理员更新用户余额: userId={}, type={}, amount={}, reason={}", 
                userId, request.getType(), request.getAmount(), request.getReason());

        User user = userRepository.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND, "用户不存在");
        }

        if (request.getAmount() == null || request.getAmount().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ErrorCode.INVALID_PARAM, "金额必须大于0");
        }

        java.math.BigDecimal currentBalance = user.getBalance() != null ? user.getBalance() : java.math.BigDecimal.ZERO;
        java.math.BigDecimal newBalance;

        if ("add".equals(request.getType())) {
            // 充值
            newBalance = currentBalance.add(request.getAmount());
        } else if ("deduct".equals(request.getType())) {
            // 扣除
            newBalance = currentBalance.subtract(request.getAmount());
            if (newBalance.compareTo(java.math.BigDecimal.ZERO) < 0) {
                throw new BusinessException(ErrorCode.INVALID_PARAM, "余额不足，无法扣除");
            }
        } else {
            throw new BusinessException(ErrorCode.INVALID_PARAM, "无效的操作类型");
        }

        user.setBalance(newBalance);
        userRepository.updateById(user);

        log.info("用户余额更新成功: userId={}, oldBalance={}, newBalance={}, reason={}", 
                userId, currentBalance, newBalance, request.getReason());
    }

    /**
     * 获取用户收藏列表
     */
    public UserResponse.UserCollections getUserCollections(Long userId, Integer page, Integer size) {
        log.info("获取用户收藏列表: userId={}, page={}, size={}", userId, page, size);

        int offset = (page - 1) * size;

        // 获取收藏的识别结果
        java.util.List<UserResponse.CollectionItem> recognitions = userCollectRepository
                .findRecognitionsByUserId(userId, page, size, offset)
                .stream()
                .map(item -> UserResponse.CollectionItem.builder()
                        .id((Long) item.get("id"))
                        .type("recognition")
                        .title((String) item.get("title"))
                        .description((String) item.get("description"))
                        .imageUrl((String) item.get("imageUrl"))
                        .confidence(item.get("confidence") instanceof BigDecimal ? 
                            ((BigDecimal) item.get("confidence")).intValue() : 
                            (Integer) item.get("confidence"))
                        .createdAt((LocalDateTime) item.get("createdAt"))
                        .build())
                .collect(Collectors.toList());

        // 获取收藏的帖子
        java.util.List<UserResponse.CollectionItem> posts = userCollectRepository
                .findPostsByUserId(userId, page, size, offset)
                .stream()
                .map(item -> UserResponse.CollectionItem.builder()
                        .id((Long) item.get("id"))
                        .type("post")
                        .title((String) item.get("title"))
                        .description((String) item.get("content"))
                        .imageUrl((String) item.get("images"))
                        .likeCount(item.get("likeCount") instanceof BigDecimal ? 
                            ((BigDecimal) item.get("likeCount")).intValue() : 
                            (Integer) item.get("likeCount"))
                        .viewCount(item.get("viewCount") instanceof BigDecimal ? 
                            ((BigDecimal) item.get("viewCount")).intValue() : 
                            (Integer) item.get("viewCount"))
                        .createdAt((LocalDateTime) item.get("createdAt"))
                        .build())
                .collect(Collectors.toList());

        // 获取收藏的知识
        java.util.List<UserResponse.CollectionItem> knowledge = userCollectRepository
                .findKnowledgeByUserId(userId, page, size, offset)
                .stream()
                .map(item -> UserResponse.CollectionItem.builder()
                        .id((Long) item.get("id"))
                        .type("knowledge")
                        .title((String) item.get("title"))
                        .description((String) item.get("description"))
                        .imageUrl((String) item.get("coverImage"))
                        .likeCount(item.get("likeCount") instanceof BigDecimal ? 
                            ((BigDecimal) item.get("likeCount")).intValue() : 
                            (Integer) item.get("likeCount"))
                        .viewCount(item.get("viewCount") instanceof BigDecimal ? 
                            ((BigDecimal) item.get("viewCount")).intValue() : 
                            (Integer) item.get("viewCount"))
                        .createdAt((LocalDateTime) item.get("createdAt"))
                        .build())
                .collect(Collectors.toList());

        return UserResponse.UserCollections.builder()
                .recognitions(recognitions)
                .posts(posts)
                .knowledge(knowledge)
                .build();
    }

    /**
     * 获取用户点赞列表
     */
    public UserResponse.UserLikes getUserLikes(Long userId, Integer page, Integer size) {
        log.info("获取用户点赞列表: userId={}, page={}, size={}", userId, page, size);

        int offset = (page - 1) * size;

        // 获取点赞的帖子
        java.util.List<UserResponse.LikeItem> posts = userLikeRepository
                .findPostsByUserId(userId, page, size, offset)
                .stream()
                .map(item -> UserResponse.LikeItem.builder()
                        .id((Long) item.get("id"))
                        .type("post")
                        .title((String) item.get("title"))
                        .content((String) item.get("content"))
                        .author((String) item.get("author"))
                        .likeCount(item.get("likeCount") instanceof BigDecimal ? 
                            ((BigDecimal) item.get("likeCount")).intValue() : 
                            (Integer) item.get("likeCount"))
                        .createdAt((LocalDateTime) item.get("createdAt"))
                        .build())
                .collect(Collectors.toList());

        // 获取点赞的知识
        java.util.List<UserResponse.LikeItem> knowledge = userLikeRepository
                .findKnowledgeByUserId(userId, page, size, offset)
                .stream()
                .map(item -> UserResponse.LikeItem.builder()
                        .id((Long) item.get("id"))
                        .type("knowledge")
                        .title((String) item.get("title"))
                        .content((String) item.get("description"))
                        .author((String) item.get("author"))
                        .likeCount(item.get("likeCount") instanceof BigDecimal ? 
                            ((BigDecimal) item.get("likeCount")).intValue() : 
                            (Integer) item.get("likeCount"))
                        .createdAt((LocalDateTime) item.get("createdAt"))
                        .build())
                .collect(Collectors.toList());

        // 获取点赞的评论
        java.util.List<UserResponse.LikeItem> comments = userLikeRepository
                .findCommentsByUserId(userId, page, size, offset)
                .stream()
                .map(item -> UserResponse.LikeItem.builder()
                        .id((Long) item.get("id"))
                        .type("comment")
                        .title("") // 评论没有标题
                        .content((String) item.get("content"))
                        .author((String) item.get("author"))
                        .likeCount(item.get("likeCount") instanceof BigDecimal ? 
                            ((BigDecimal) item.get("likeCount")).intValue() : 
                            (Integer) item.get("likeCount"))
                        .createdAt((LocalDateTime) item.get("createdAt"))
                        .build())
                .collect(Collectors.toList());

        return UserResponse.UserLikes.builder()
                .posts(posts)
                .knowledge(knowledge)
                .comments(comments)
                .build();
    }
}

