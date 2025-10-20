package com.pengcunfu.recognition.service;

import com.pengcunfu.recognition.constant.ErrorCode;
import com.pengcunfu.recognition.constant.JwtConstants;
import com.pengcunfu.recognition.entity.User;
import com.pengcunfu.recognition.enums.UserRole;
import com.pengcunfu.recognition.enums.UserStatus;
import com.pengcunfu.recognition.exception.AuthenticationException;
import com.pengcunfu.recognition.exception.BusinessException;
import com.pengcunfu.recognition.repository.UserRepository;
import com.pengcunfu.recognition.request.AuthRequest;
import com.pengcunfu.recognition.response.UserResponse;
import com.pengcunfu.recognition.security.UserPrincipal;
import com.pengcunfu.recognition.service.redis.SessionService;
import com.pengcunfu.recognition.service.redis.RateLimitService;
import com.pengcunfu.recognition.service.redis.VerificationCodeService;
import com.pengcunfu.recognition.util.JwtUtil;
import com.pengcunfu.recognition.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 认证服务
 * 处理用户登录、注册、登出等认证业务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final SessionService sessionService;
    private final RateLimitService rateLimitService;
    private final VerificationCodeService verificationCodeService;
    private final PasswordUtil passwordUtil;
    private final EmailService emailService;

    /**
     * 用户登录
     */
    @Transactional
    public UserResponse.LoginResponse login(AuthRequest.LoginRequest request) {
        String username = request.getUsername();
        String password = request.getPassword();

        log.info("用户登录: username={}", username);

        // 检查账号是否被锁定
        if (rateLimitService.isAccountLocked(username)) {
            Long lockTime = rateLimitService.getAccountLockTTL(username);
            throw new AuthenticationException(
                    String.format("账号已被锁定，请在 %d 秒后重试", lockTime)
            );
        }

        // 检查登录失败次数
        if (!rateLimitService.checkLoginFailCount(username, 5)) {
            rateLimitService.lockAccount(username, 1800); // 锁定30分钟
            throw new AuthenticationException("登录失败次数过多，账号已被锁定30分钟");
        }

        // 查询用户（支持用户名或邮箱登录）
        User user = userRepository.findByUsernameOrEmail(username);

        if (user == null) {
            rateLimitService.recordLoginFail(username, 1800);
            throw new AuthenticationException("用户名或密码错误");
        }

        // 检查账号状态
        if (!user.getStatus().equals(UserStatus.ACTIVE.getValue())) {
            throw new AuthenticationException("账号已被禁用");
        }

        // 验证密码
        if (!passwordUtil.matches(password, user.getPassword())) {
            rateLimitService.recordLoginFail(username, 1800);
            log.warn("密码错误: username={}", username);
            throw new AuthenticationException("用户名或密码错误");
        }

        // 清除登录失败记录
        rateLimitService.clearLoginFailCount(username);

        // 更新最后登录时间
        LocalDateTime now = LocalDateTime.now();
        String lastLoginTime = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        userRepository.updateLastLoginTime(user.getId(), lastLoginTime);

        // 生成 Token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());

        // 保存 Token 到 Redis
        sessionService.saveToken(user.getId(), token, JwtConstants.EXPIRATION / 1000);

        // 缓存用户信息
        UserPrincipal userPrincipal = new UserPrincipal(
                user.getId(),
                user.getUsername(),
                null,
                user.getRole(),
                user.getStatus(),
                true
        );
        sessionService.cacheUserInfo(user.getId(), userPrincipal);

        log.info("用户登录成功: username={}, userId={}", username, user.getId());

        return UserResponse.LoginResponse.builder()
                .token(token)
                .user(convertToUserInfo(user))
                .build();
    }

    /**
     * 用户注册
     */
    @Transactional
    public UserResponse.RegisterResponse register(AuthRequest.RegisterRequest request) {
        String username = request.getUsername();
        String email = request.getEmail();
        String password = request.getPassword();
        String emailCode = request.getEmailCode();

        log.info("用户注册: username={}, email={}", username, email);

        // 验证邮箱验证码
        if (!verificationCodeService.verifyEmailCode(email, emailCode)) {
            throw new BusinessException(ErrorCode.INVALID_PARAM, "邮箱验证码错误或已过期");
        }

        // 检查用户名是否已存在
        if (userRepository.findByUsername(username) != null) {
            throw new BusinessException(ErrorCode.INVALID_PARAM, "用户名已存在");
        }

        // 检查邮箱是否已注册
        if (userRepository.findByEmail(email) != null) {
            throw new BusinessException(ErrorCode.INVALID_PARAM, "邮箱已被注册");
        }

        // 创建用户
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordUtil.encode(password));
        user.setNickname(username); // 默认昵称为用户名
        user.setRole(UserRole.USER.getValue());
        user.setStatus(UserStatus.ACTIVE.getValue());

        userRepository.insert(user);

        log.info("用户注册成功: username={}, email={}, userId={}", username, email, user.getId());

        return UserResponse.RegisterResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }

    /**
     * 用户登出
     */
    public void logout(Long userId) {
        log.info("用户登出: userId={}", userId);

        // 删除 Token
        sessionService.removeToken(userId);

        // 删除缓存的用户信息
        sessionService.removeCachedUserInfo(userId);

        log.info("用户登出成功: userId={}", userId);
    }

    /**
     * 发送邮箱验证码
     */
    public void sendEmailCode(String email) {
        log.info("发送邮箱验证码: email={}", email);

        // 检查验证码是否已存在（防止频繁发送）
        if (verificationCodeService.isEmailCodeExists(email)) {
            Long ttl = verificationCodeService.getEmailCodeTTL(email);
            if (ttl > 240) { // 如果剩余时间超过4分钟，拒绝发送
                throw new BusinessException(ErrorCode.INVALID_PARAM,
                        "验证码已发送，请稍后再试");
            }
        }

        // 生成验证码
        String code = verificationCodeService.generateCode();

        // 保存验证码到 Redis
        verificationCodeService.saveEmailCode(email, code);

        // TODO: 发送邮件（接入邮件服务）
        log.info("邮箱验证码: email={}, code={}", email, code);
    }

    /**
     * 忘记密码 - 重置密码
     */
    @Transactional
    public void resetPassword(AuthRequest.ResetPasswordRequest request) {
        String email = request.getEmail();
        String emailCode = request.getEmailCode();
        String newPassword = request.getNewPassword();

        log.info("重置密码: email={}", email);

        // 验证邮箱验证码
        if (!verificationCodeService.verifyEmailCode(email, emailCode)) {
            throw new BusinessException(ErrorCode.INVALID_PARAM, "邮箱验证码错误或已过期");
        }

        // 查询用户
        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND, "邮箱未注册");
        }

        // 更新密码
        user.setPassword(passwordUtil.encode(newPassword));
        userRepository.updateById(user);

        // 清除该用户的所有会话
        sessionService.removeToken(user.getId());
        sessionService.removeCachedUserInfo(user.getId());

        log.info("密码重置成功: email={}, userId={}", email, user.getId());
    }

    /**
     * 刷新 Token
     */
    public UserResponse.LoginResponse refreshToken(Long userId) {
        log.info("刷新Token: userId={}", userId);

        // 查询用户
        User user = userRepository.selectById(userId);

        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND, "用户不存在");
        }

        if (!user.getStatus().equals(UserStatus.ACTIVE.getValue())) {
            throw new AuthenticationException("账号已被禁用");
        }

        // 生成新 Token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());

        // 保存新 Token 到 Redis
        sessionService.saveToken(user.getId(), token, JwtConstants.EXPIRATION / 1000);

        log.info("Token刷新成功: userId={}", userId);

        return UserResponse.LoginResponse.builder()
                .token(token)
                .user(convertToUserInfo(user))
                .build();
    }

    /**
     * 发送验证码
     */
    public void sendVerificationCode(String email, String type) {
        log.info("发送验证码: email={}, type={}", email, type);

        // 验证邮箱格式
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new BusinessException(ErrorCode.INVALID_PARAM, "邮箱格式不正确");
        }

        // 根据类型验证邮箱是否存在
        if ("register".equals(type)) {
            // 注册时，邮箱不能已存在
            User existUser = userRepository.findByEmail(email);
            if (existUser != null) {
                throw new BusinessException(ErrorCode.INVALID_PARAM, "该邮箱已被注册");
            }
        } else if ("reset".equals(type) || "bind".equals(type)) {
            // 重置密码或绑定时，邮箱必须存在
            User existUser = userRepository.findByEmail(email);
            if (existUser == null) {
                throw new BusinessException(ErrorCode.USER_NOT_FOUND, "该邮箱未注册");
            }
        }

        // 生成6位验证码
        String code = emailService.generateVerificationCode();

        // 保存验证码到 Redis（5分钟有效期）
        verificationCodeService.saveEmailCode(email, code);

        // 发送验证码邮件
        emailService.sendVerificationCode(email, code, type);

        log.info("验证码发送成功: email={}, type={}", email, type);
    }

    /**
     * 忘记密码 - 验证码验证并重置密码
     */
    @Transactional
    public void forgotPassword(String email, String code, String newPassword) {
        log.info("忘记密码: email={}", email);

        // 验证邮箱格式
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new BusinessException(ErrorCode.INVALID_PARAM, "邮箱格式不正确");
        }

        // 验证验证码
        if (!verificationCodeService.verifyEmailCode(email, code)) {
            throw new BusinessException(ErrorCode.INVALID_PARAM, "验证码错误或已过期");
        }

        // 查询用户
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND, "该邮箱未注册");
        }

        // 更新密码
        user.setPassword(passwordUtil.encode(newPassword));
        userRepository.updateById(user);

        log.info("密码重置成功: email={}, userId={}", email, user.getId());
    }

    /**
     * 转换为用户信息 DTO
     */
    private UserResponse.UserInfo convertToUserInfo(User user) {
        // 计算VIP等级
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
