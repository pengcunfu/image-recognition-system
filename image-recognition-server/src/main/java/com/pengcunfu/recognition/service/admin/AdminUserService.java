package com.pengcunfu.recognition.service.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pengcunfu.recognition.constant.ErrorCode;
import com.pengcunfu.recognition.entity.User;
import com.pengcunfu.recognition.enums.UserRole;
import com.pengcunfu.recognition.exception.BusinessException;
import com.pengcunfu.recognition.repository.UserRepository;
import com.pengcunfu.recognition.response.PageResponse;
import com.pengcunfu.recognition.response.UserResponse;
import com.pengcunfu.recognition.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * 管理员 - 用户管理服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final UserRepository userRepository;
    private final PasswordUtil passwordUtil;

    /**
     * 获取用户列表（使用 SQL 查询）
     */
    public PageResponse<UserResponse.UserInfo> getUsers(
            Integer page, Integer size, Integer status, String keyword) {
        log.info("管理员获取用户列表: page={}, size={}, status={}, keyword={}", 
                page, size, status, keyword);

        Page<User> pageRequest = new Page<>(page, size);
        Page<User> pageResult = userRepository.findUsersForAdmin(pageRequest, status, keyword);

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
     * 更新用户状态
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
     * 删除用户
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
     * 重置用户密码
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
                .role(user.getRole())
                .status(user.getStatus())
                .vipLevel(vipLevel)
                .vipExpireTime(user.getVipExpireTime())
                .lastLoginTime(user.getLastLoginTime())
                .createdAt(user.getCreatedAt())
                .build();
    }
}

