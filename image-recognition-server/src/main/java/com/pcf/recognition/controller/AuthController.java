package com.pcf.recognition.controller;

import com.pcf.recognition.dto.*;
import com.pcf.recognition.service.AuthService;
import com.pcf.recognition.service.EmailService;
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
    public ApiResponse<LoginResponseDto> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest) {

        try {
            // 验证验证码
            if (request.getCaptcha() != null && !request.getCaptcha().trim().isEmpty()) {
                // 从Cookie中获取验证码会话ID
                String captchaSessionId = getCaptchaSessionIdFromCookie(httpRequest);
                if (captchaSessionId == null) {
                    return ApiResponse.error("验证码会话已过期，请重新获取验证码");
                }

                if (!authService.verifyCaptcha(captchaSessionId, request.getCaptcha())) {
                    return ApiResponse.error("验证码错误或已过期");
                }
            }

            LoginResponseDto result = authService.login(
                    request.getUsername(),
                    request.getPassword(),
                    request.getCaptcha()
            );

            if (result.getSuccess()) {
                // 生成JWT Token
                String token = jwtUtil.generateToken(
                        result.getUser().getId(),
                        result.getUser().getUsername(),
                        result.getUser().getRole()
                );

                // 将Token存储到Redis
                authService.storeTokenToRedis(
                        token,
                        result.getUser().getId(),
                        result.getUser().getUsername(),
                        result.getUser().getRole()
                );

                // 设置Token到响应中
                result.setToken(token);

                log.info("用户登录成功: username={}, userId={}",
                        result.getUser().getUsername(), result.getUser().getId());

                return ApiResponse.success(result, result.getMessage());
            } else {
                return ApiResponse.error(result.getMessage());
            }
        } catch (RuntimeException e) {
            log.error("登录处理异常", e);
            return ApiResponse.error(e.getMessage());
        }
    }


    @PostMapping("/register")
    // 公开接口，无需权限验证
    public ApiResponse<RegisterResponseDto> register(
            @Valid @RequestBody RegisterRequest request) {

        try {
            RegisterResponseDto result = authService.register(request);

            if (result.getSuccess()) {
                return ApiResponse.success(result, result.getMessage());
            } else {
                return ApiResponse.error(result.getMessage());
            }
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        }
    }


    @PostMapping("/forgot-password")
    // 公开接口，无需权限验证
    public ApiResponse<String> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {

        log.info("忘记密码请求: email={}", request.getEmail());

        // 模拟发送重置邮件
        return ApiResponse.success("密码重置邮件已发送到您的邮箱", "重置邮件发送成功");
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

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(imageBytes);

        } catch (Exception e) {
            log.error("生成验证码图片失败", e);
            return ResponseEntity.internalServerError().build();
        }
    }


    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<String> logout(@RequestHeader(value = "Authorization", required = false) String token) {

        log.info("用户退出登录请求: token={}", token);

        try {
            // 调用AuthService的logout方法，会从Redis删除token
            OperationResultDto result = authService.logout(token);

            if (result.getSuccess()) {
                return ApiResponse.success(result.getMessage());
            } else {
                return ApiResponse.error(result.getMessage());
            }
        } catch (Exception e) {
            log.error("退出登录处理异常", e);
            return ApiResponse.error("退出登录失败，请稍后重试");
        }
    }


    @GetMapping("/validate")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<TokenValidationResponseDto> validateToken(
            @RequestHeader(value = "Authorization", required = false) String token) {

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

            TokenValidationResponseDto result = TokenValidationResponseDto.builder()
                    .valid(true)
                    .userInfo(userInfo)
                    .build();

            return ApiResponse.success(result, "Token验证成功");
        } catch (Exception e) {
            log.error("Token验证异常", e);
            return ApiResponse.error("Token验证失败");
        }
    }


    @PostMapping("/sms-code")
    // 公开接口，无需权限验证
    public ApiResponse<SmsCodeResponse> sendSmsCode(@RequestBody SmsCodeRequest request) {
        try {
            // 校验手机号格式
            if (!isValidPhoneNumber(request.getPhone())) {
                return ApiResponse.error("手机号格式不正确");
            }

            // 生成6位数字验证码
            String code = generateSmsCode();

            // 将验证码存储到Redis
            authService.storeSmsCode(request.getPhone(), code);

            // TODO: 集成短信服务商发送短信
            // 这里模拟发送成功
            log.info("发送短信验证码到手机号: {}, 验证码: {}", request.getPhone(), code);

            SmsCodeResponse response = new SmsCodeResponse();
            response.setPhone(maskPhoneNumber(request.getPhone()));
            response.setCodeExpiry(300); // 5分钟过期
            response.setSendTime(System.currentTimeMillis());

            return ApiResponse.success(response, "短信验证码发送成功");
        } catch (Exception e) {
            log.error("发送短信验证码失败", e);
            return ApiResponse.error("短信验证码发送失败");
        }
    }


    @PostMapping("/sms-code/verify")
    // 公开接口，无需权限验证
    public ApiResponse<Boolean> verifySmsCode(@RequestBody SmsCodeVerifyRequest request) {
        try {
            // 使用AuthService验证短信验证码
            boolean isValid = authService.verifySmsCode(request.getPhone(), request.getCode());

            if (isValid) {
                return ApiResponse.success(true, "验证码验证成功");
            } else {
                return ApiResponse.error("验证码错误或已过期");
            }
        } catch (Exception e) {
            log.error("验证短信验证码失败", e);
            return ApiResponse.error("验证码验证失败");
        }
    }


    @PostMapping("/email-code")
    // 公开接口，无需权限验证
    public ApiResponse<EmailCodeResponse> sendEmailCode(@Valid @RequestBody EmailCodeRequest request) {
        try {
            // 校验邮箱格式已通过@Valid注解完成

            // 发送邮箱验证码
            String code = emailService.sendEmailCode(request.getEmail(), request.getType());

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


    /**
     * 验证手机号格式
     */
    private boolean isValidPhoneNumber(String phone) {
        return phone != null && phone.matches("^1[3-9]\\d{9}$");
    }

    /**
     * 生成6位数字验证码
     */
    private String generateSmsCode() {
        return String.format("%06d", (int) (Math.random() * 1000000));
    }

    /**
     * 手机号脱敏
     */
    private String maskPhoneNumber(String phone) {
        if (phone == null || phone.length() != 11) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(7);
    }

    /**
     * 获取客户端真实IP地址
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }

        String proxyClientIp = request.getHeader("Proxy-Client-IP");
        if (proxyClientIp != null && !proxyClientIp.isEmpty() && !"unknown".equalsIgnoreCase(proxyClientIp)) {
            return proxyClientIp;
        }

        String wlProxyClientIp = request.getHeader("WL-Proxy-Client-IP");
        if (wlProxyClientIp != null && !wlProxyClientIp.isEmpty() && !"unknown".equalsIgnoreCase(wlProxyClientIp)) {
            return wlProxyClientIp;
        }

        return request.getRemoteAddr();
    }

    /**
     * 从Cookie中获取验证码会话ID
     */
    private String getCaptchaSessionIdFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("CAPTCHA_SESSION_ID".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

}
