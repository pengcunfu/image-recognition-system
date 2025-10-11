package com.pcf.recognition.service;

import com.pcf.recognition.entity.User;
import com.pcf.recognition.entity.UserFavorite;
import com.pcf.recognition.entity.RecognitionHistory;
import com.pcf.recognition.repository.UserRepository;
import com.pcf.recognition.repository.UserFavoriteRepository;
import com.pcf.recognition.repository.RecognitionHistoryRepository;
import com.pcf.recognition.dto.UserInfoDto;
import com.pcf.recognition.dto.UserStatsDto;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 用户管理服务 - 专注于管理员级别的用户管理功能
 * 与UserService的区别：
 * - UserService：处理当前用户的个人信息管理
 * - UserManagementService：处理管理员对所有用户的管理操作
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserManagementService {

    private final UserRepository userRepository;
    private final UserFavoriteRepository favoriteRepository;
    private final RecognitionHistoryRepository historyRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 根据用户名或邮箱查找用户（管理员功能）
     */
    public Optional<User> findByUsernameOrEmail(String usernameOrEmail) {
        log.info("管理员查找用户: usernameOrEmail={}", usernameOrEmail);

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, usernameOrEmail)
                .or()
                .eq(User::getEmail, usernameOrEmail);

        User user = userRepository.selectOne(wrapper);
        return Optional.ofNullable(user);
    }

    /**
     * 创建新用户（管理员功能）
     */
    @Transactional
    public User createUser(String username, String email, String password, String name, User.UserRole role) {
        log.info("管理员创建用户: username={}, email={}, role={}", username, email, role);

        // 检查用户名是否已存在
        LambdaQueryWrapper<User> usernameWrapper = new LambdaQueryWrapper<>();
        usernameWrapper.eq(User::getUsername, username);
        if (userRepository.selectCount(usernameWrapper) > 0) {
            throw new IllegalArgumentException("用户名已存在");
        }

        // 检查邮箱是否已存在
        LambdaQueryWrapper<User> emailWrapper = new LambdaQueryWrapper<>();
        emailWrapper.eq(User::getEmail, email);
        if (userRepository.selectCount(emailWrapper) > 0) {
            throw new IllegalArgumentException("邮箱已被注册");
        }

        // 创建用户实体
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setName(name);
        user.setRole(role != null ? role : User.UserRole.USER);
        user.setStatus(User.UserStatus.ACTIVE);
        user.setVipLevel(role == User.UserRole.VIP ? 1 : 0);

        userRepository.insert(user);
        log.info("管理员创建用户成功: userId={}", user.getId());

        return user;
    }

    /**
     * 获取用户分页列表（管理员功能）
     */
    public Page<User> getUserList(int page, int size, User.UserRole role, User.UserStatus status) {
        log.info("管理员获取用户列表: page={}, size={}, role={}, status={}", page, size, role, status);

        Page<User> pageRequest = new Page<>(page, size);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();

        if (role != null) {
            wrapper.eq(User::getRole, role);
        }
        if (status != null) {
            wrapper.eq(User::getStatus, status);
        }

        wrapper.orderByDesc(User::getCreateTime);

        return userRepository.selectPage(pageRequest, wrapper);
    }

    /**
     * 更新用户状态（管理员功能）
     */
    @Transactional
    public boolean updateUserStatus(Long userId, User.UserStatus status) {
        log.info("管理员更新用户状态: userId={}, status={}", userId, status);

        User user = userRepository.selectById(userId);
        if (user == null) {
            return false;
        }

        user.setStatus(status);
        return userRepository.updateById(user) > 0;
    }

    /**
     * 更新用户角色（管理员功能）
     */
    @Transactional
    public boolean updateUserRole(Long userId, User.UserRole role) {
        log.info("管理员更新用户角色: userId={}, role={}", userId, role);

        User user = userRepository.selectById(userId);
        if (user == null) {
            return false;
        }

        user.setRole(role);
        // 如果设置为VIP，自动设置VIP等级
        if (role == User.UserRole.VIP && user.getVipLevel() == 0) {
            user.setVipLevel(1);
        }

        return userRepository.updateById(user) > 0;
    }

    /**
     * 重置用户密码（管理员功能）
     */
    @Transactional
    public boolean resetUserPassword(Long userId, String newPassword) {
        log.info("管理员重置用户密码: userId={}", userId);

        User user = userRepository.selectById(userId);
        if (user == null) {
            return false;
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        return userRepository.updateById(user) > 0;
    }

    /**
     * 获取系统统计数据（管理员功能）
     */
    public UserStatsDto getSystemStats() {
        log.info("管理员获取系统统计数据");

        // 总用户数
        Long totalUsers = userRepository.selectCount(null);

        // 活跃用户数
        LambdaQueryWrapper<User> activeWrapper = new LambdaQueryWrapper<>();
        activeWrapper.eq(User::getStatus, User.UserStatus.ACTIVE);
        Long activeUsers = userRepository.selectCount(activeWrapper);

        // VIP用户数
        LambdaQueryWrapper<User> vipWrapper = new LambdaQueryWrapper<>();
        vipWrapper.eq(User::getRole, User.UserRole.VIP);
        Long vipUsers = userRepository.selectCount(vipWrapper);

        // 总识别次数
        Long totalRecognitions = historyRepository.selectCount(null);

        return UserStatsDto.builder()
                .totalRecognitions(totalRecognitions.intValue())
                .recognitionCount(totalRecognitions.intValue())
                .postCount(totalUsers.intValue()) // 使用用户数作为模拟数据
                .commentCount((int) (totalUsers * 2)) // 模拟数据
                .favoriteCount((int) (totalRecognitions * 0.1)) // 模拟数据
                .viewCount((int) (totalRecognitions * 5)) // 模拟数据
                .likeCount((int) (totalRecognitions * 0.3)) // 模拟数据
                .successRecognitions((int) (totalRecognitions * 0.9)) // 模拟90%成功率
                .failedRecognitions((int) (totalRecognitions * 0.1)) // 模拟10%失败率
                .averageConfidence(87.5) // 模拟平均置信度
                .build();
    }

    /**
     * 删除用户（软删除，管理员功能）
     */
    @Transactional
    public boolean deleteUser(Long userId) {
        log.info("管理员删除用户: userId={}", userId);

        User user = userRepository.selectById(userId);
        if (user == null) {
            return false;
        }

        // 软删除
        return userRepository.deleteById(userId) > 0;
    }

    /**
     * 获取用户详细信息（管理员功能）
     */
    public UserInfoDto getUserDetail(Long userId) {
        log.info("管理员获取用户详情: userId={}", userId);

        User user = userRepository.selectById(userId);
        if (user == null) {
            return null;
        }

        return UserInfoDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .name(user.getName())
                .phone(user.getPhone())
                .avatar(user.getAvatar())
                .bio(user.getBio())
                .role(user.getRole().name().toLowerCase())
                .status(user.getStatus().name().toLowerCase())
                .vipLevel(user.getVipLevel())
                .lastLoginTime(user.getLastLoginTime())
                .createTime(user.getCreateTime())
                .build();
    }

}
