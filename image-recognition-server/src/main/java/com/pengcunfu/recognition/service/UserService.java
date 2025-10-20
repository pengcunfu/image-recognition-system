package com.pengcunfu.recognition.service;

import com.pengcunfu.recognition.constant.ErrorCode;
import com.pengcunfu.recognition.entity.User;
import com.pengcunfu.recognition.exception.BusinessException;
import com.pengcunfu.recognition.repository.*;
import com.pengcunfu.recognition.request.UserRequest;
import com.pengcunfu.recognition.response.UserResponse;
import com.pengcunfu.recognition.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

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
    private final RecognitionResultRepository recognitionResultRepository;
    private final CommunityPostRepository communityPostRepository;
    private final CommentRepository commentRepository;
    private final UserCollectRepository userCollectRepository;
    private final UserLikeRepository userLikeRepository;

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

