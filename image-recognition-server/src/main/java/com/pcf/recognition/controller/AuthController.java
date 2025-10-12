package com.pcf.recognition.controller;

import com.pcf.recognition.dto.*;
import com.pcf.recognition.dto.AuthDto.*;
import com.pcf.recognition.service.AuthService;
import com.pcf.recognition.service.EmailService;
import com.pcf.recognition.util.AuthUtil;
import com.pcf.recognition.util.JwtUtil;
import com.wf.captcha.base.Captcha;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Cookie;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

/**
 * 用户认证控制器
 * 提供登录、注册、密码重置等功能
 */
@RestController
@RequestMapping("/api/v1/auth")
@Slf4j
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final EmailService emailService;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    // 公开接口，无需权限验证
    public ApiResponse<LoginResponseDto> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        try {
            // 1. 验证验证码（Controller层负责验证逻辑）
            if (request.getCaptcha() != null && !request.getCaptcha().trim().isEmpty()) {
                // 从Cookie中获取验证码会话ID
                String captchaSessionId = AuthUtil.getCaptchaSessionIdFromCookie(httpRequest);
                if (captchaSessionId == null) {
                    return ApiResponse.error("验证码会话已过期，请重新获取验证码");
                }

                if (!authService.verifyCaptcha(captchaSessionId, request.getCaptcha())) {
                    return ApiResponse.error("验证码错误或已过期");
                }
            }

            // 2. 调用Service层进行用户认证（Service层只负责核心业务逻辑）
            UserInfoDto userInfo = authService.authenticateUser(request.getUsername(), request.getPassword());
            
            if (userInfo == null) {
                return ApiResponse.error("用户名或密码错误");
            }

            // 3. 生成JWT Token（Controller层负责Token管理）
            String token = jwtUtil.generateToken(userInfo.getId(), userInfo.getUsername(), userInfo.getRole());

            // 4. 将Token存储到Redis（Controller层负责Token存储）
            authService.storeTokenToRedis(token, userInfo.getId(), userInfo.getUsername(), userInfo.getRole());

            // 5. 构建登录响应
            LoginResponseDto result = LoginResponseDto.builder()
                    .token(token)
                    .user(userInfo)
                    .build();

            log.info("用户登录成功: username={}, userId={}", userInfo.getUsername(), userInfo.getId());

            return ApiResponse.success(result, "登录成功");

        } catch (RuntimeException e) {
            log.error("登录处理异常", e);
            return ApiResponse.error(e.getMessage());
        }
    }


    @PostMapping("/register")
    // 公开接口，无需权限验证
    public ApiResponse<Void> register(@Valid @RequestBody RegisterRequest request) {
        try {
            // 1. 验证邮箱验证码（Controller层负责验证逻辑）
            if (request.getEmailCode() != null && !request.getEmailCode().trim().isEmpty()) {
                if (!emailService.verifyEmailCode(request.getEmail(), request.getEmailCode(), "register")) {
                    return ApiResponse.error("邮箱验证码错误或已过期");
                }
            }

            // 2. 检查用户名是否已存在（Controller层负责业务验证）
            if (authService.isUsernameExists(request.getUsername())) {
                return ApiResponse.error("用户名已存在");
            }

            // 3. 检查邮箱是否已存在（Controller层负责业务验证）
            if (authService.isEmailExists(request.getEmail())) {
                return ApiResponse.error("邮箱已被注册");
            }

            // 4. 调用Service层创建用户（Service层只负责核心业务逻辑）
            Long userId = authService.createUser(request);

            if (userId > 0) {
                return ApiResponse.success(null, "注册成功");
            } else {
                return ApiResponse.error("注册失败");
            }
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        }
    }


    @PostMapping("/forgot-password")
    // 公开接口，无需权限验证
    public ApiResponse<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        log.info("忘记密码请求: email={}", request.getEmail());
        try {
            // 1. 验证邮箱验证码（Controller层负责验证逻辑）
            if (request.getEmailCode() != null && !request.getEmailCode().trim().isEmpty()) {
                if (!emailService.verifyEmailCode(request.getEmail(), request.getEmailCode(), "reset_password")) {
                    return ApiResponse.error("邮箱验证码错误或已过期");
                }
            }

            // 2. 检查邮箱是否存在（Controller层负责业务验证）
            if (!authService.isEmailExists(request.getEmail())) {
                return ApiResponse.error("邮箱不存在");
            }

            // 3. 调用Service层更新密码（Service层只负责核心业务逻辑）
            boolean success = authService.updateUserPassword(request.getEmail(), request.getNewPassword());

            if (success) {
                return ApiResponse.success(null, "密码重置成功");
            } else {
                return ApiResponse.error("密码重置失败");
            }
        } catch (RuntimeException e) {
            log.error("忘记密码处理异常", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/captcha")
    // 公开接口，无需权限验证
    public ResponseEntity<byte[]> getCaptcha(HttpServletRequest request, HttpServletResponse response) {
        try {
            // 生成UUID作为验证码会话标识
            String captchaSessionId = UUID.randomUUID().toString();

            // 生成验证码图片
            Captcha captcha = authService.generateCaptcha(captchaSessionId);

            // 将验证码图片输出为字节数组
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            captcha.out(outputStream);
            byte[] imageBytes = outputStream.toByteArray();

            // 创建Cookie存储验证码会话ID
            Cookie captchaCookie = new Cookie("CAPTCHA_SESSION_ID", captchaSessionId);
            captchaCookie.setHttpOnly(true); // 防止XSS攻击
            captchaCookie.setSecure(false); // 开发环境设为false，生产环境应设为true
            captchaCookie.setPath("/"); // 整个应用可用
            captchaCookie.setMaxAge(5 * 60); // 5分钟过期，与验证码过期时间一致
            response.addCookie(captchaCookie);

            // 设置响应头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            headers.set("Cache-Control", "no-cache, no-store, must-revalidate");
            headers.set("Pragma", "no-cache");
            headers.set("Expires", "0");

            log.info("验证码图片生成成功: captchaSessionId={}", captchaSessionId);

            return ResponseEntity.ok().headers(headers).body(imageBytes);

        } catch (Exception e) {
            log.error("生成验证码图片失败", e);
            return ResponseEntity.internalServerError().build();
        }
    }


    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<Void> logout(@RequestHeader(value = "Authorization", required = false) String token) {
        log.info("用户退出登录请求: token={}", token);
        try {
            // 直接从Redis删除token（Controller层处理简单的Token管理）
            if (token != null && !token.isEmpty()) {
                authService.deleteTokenFromRedis(token);
                log.info("用户退出登录成功: token={}", token);
                return ApiResponse.success(null, "退出登录成功");
            } else {
                return ApiResponse.error("Token不能为空");
            }
        } catch (Exception e) {
            log.error("退出登录处理异常", e);
            return ApiResponse.error("退出登录失败，请稍后重试");
        }
    }


    @GetMapping("/validate")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<UserInfoDto> validateToken(@RequestHeader(value = "Authorization", required = false) String token) {

        if (token == null) {
            return ApiResponse.error("Token不能为空");
        }

        try {
            // 首先验证JWT Token格式
            if (!jwtUtil.validateToken(token)) {
                return ApiResponse.error("Token无效或已过期");
            }

            // 验证Token是否在Redis中存在（检查是否已被注销）
            if (!authService.isTokenValidInRedis(token)) {
                return ApiResponse.error("Token已失效，请重新登录");
            }

            // 从Token中获取用户信息
            Long userId = jwtUtil.getUserIdFromToken(token);
            String username = jwtUtil.getUsernameFromToken(token);
            String role = jwtUtil.getRoleFromToken(token);

            if (userId == null || username == null || role == null) {
                return ApiResponse.error("Token信息不完整");
            }

            // 刷新Token过期时间（活跃用户延长会话）
            authService.refreshTokenExpiry(token);

            // 构建用户信息（实际项目中应该从数据库获取最新信息）
            UserInfoDto userInfo = UserInfoDto.builder()
                    .id(userId)
                    .username(username)
                    .name(username) // 简化处理，实际应从数据库获取
                    .role(role)
                    .build();

            return ApiResponse.success(userInfo, "Token验证成功");
        } catch (Exception e) {
            log.error("Token验证异常", e);
            return ApiResponse.error("Token验证失败");
        }
    }


    @PostMapping("/email-code")
    // 公开接口，无需权限验证
    public ApiResponse<EmailCodeResponse> sendEmailCode(@Valid @RequestBody EmailCodeRequest request) {
        try {
            // 校验邮箱格式已通过@Valid注解完成

            // 发送邮箱验证码
            emailService.sendEmailCode(request.getEmail(), request.getType());

            // 构建响应
            EmailCodeResponse response = new EmailCodeResponse();
            response.setEmail(request.getEmail());
            response.setMaskedEmail(emailService.maskEmail(request.getEmail()));
            response.setCodeExpiry(300); // 5分钟过期
            response.setSendTime(System.currentTimeMillis());

            log.info("邮箱验证码发送成功: email={}, type={}", request.getEmail(), request.getType());
            return ApiResponse.success(response, "邮箱验证码发送成功");

        } catch (Exception e) {
            log.error("发送邮箱验证码失败: email={}, type={}", request.getEmail(), request.getType(), e);
            return ApiResponse.error("邮箱验证码发送失败，请稍后重试");
        }
    }
}
