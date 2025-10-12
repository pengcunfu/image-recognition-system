package com.pcf.recognition.service;

import com.pcf.recognition.util.RedisClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 邮件服务
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final RedisClient redisClient;

    private static final String EMAIL_CODE_KEY_PREFIX = "email_code:";
    private static final int EMAIL_CODE_EXPIRE_TIME = 5; // 5分钟过期

    /**
     * 发送邮箱验证码
     *
     * @param email 邮箱地址
     * @param type  验证码类型
     * @return 是否发送成功
     */
    public boolean sendEmailCode(String email, String type) {
        try {
            // 生成6位数字验证码
            String code = generateEmailCode();

            // 构建邮件内容
            String subject = getEmailSubject(type);
            String content = getEmailContent(code, type);

            // 发送邮件
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("图像识别系统 <huaqiwill@qq.com>");
            message.setTo(email);
            message.setSubject(subject);
            message.setText(content);

            mailSender.send(message);

            // 将验证码存储到Redis
            String redisKey = RedisClient.buildKey(EMAIL_CODE_KEY_PREFIX, type, email);
            redisClient.setWithExpire(redisKey, code, EMAIL_CODE_EXPIRE_TIME, TimeUnit.MINUTES);

            log.info("邮箱验证码发送成功: email={}, type={}, code={}", email, type, code);
            return true;

        } catch (Exception e) {
            log.error("发送邮箱验证码失败: email={}, type={}", email, type, e);
            return false;
        }
    }

    /**
     * 验证邮箱验证码
     *
     * @param email 邮箱地址
     * @param code  验证码
     * @param type  验证码类型
     * @return 是否验证成功
     */
    public boolean verifyEmailCode(String email, String code, String type) {
        log.info("验证邮箱验证码: email={}, type={}, code={}", email, type, code);

        if (email == null || code == null || type == null) {
            return false;
        }

        try {
            String redisKey = RedisClient.buildKey(EMAIL_CODE_KEY_PREFIX, type, email);
            String storedCode = redisClient.get(redisKey);

            if (storedCode == null || storedCode.isEmpty()) {
                log.warn("邮箱验证码不存在或已过期: email={}, type={}", email, type);
                return false;
            }

            boolean isValid = storedCode.equals(code.trim());
            if (isValid) {
                // 验证成功后删除验证码
                redisClient.delete(redisKey);
                log.info("邮箱验证码验证成功: email={}, type={}", email, type);
            } else {
                log.warn("邮箱验证码验证失败: email={}, type={}, expected={}, actual={}",
                        email, type, storedCode, code);
            }

            return isValid;

        } catch (Exception e) {
            log.error("验证邮箱验证码时发生异常: email={}, type={}", email, type, e);
            return false;
        }
    }

    /**
     * 生成6位数字验证码
     */
    private String generateEmailCode() {
        return String.format("%06d", (int) (Math.random() * 1000000));
    }

    /**
     * 获取邮件主题
     */
    private String getEmailSubject(String type) {
        switch (type) {
            case "register":
                return "【图像识别系统】注册验证码";
            case "forgot_password":
                return "【图像识别系统】找回密码验证码";
            case "change_email":
                return "【图像识别系统】更换邮箱验证码";
            default:
                return "【图像识别系统】验证码";
        }
    }

    /**
     * 获取邮件内容
     */
    private String getEmailContent(String code, String type) {
        String action;
        switch (type) {
            case "register":
                action = "注册账号";
                break;
            case "forgot_password":
                action = "找回密码";
                break;
            case "change_email":
                action = "更换邮箱";
                break;
            default:
                action = "验证操作";
        }

        return String.format(
                "您好！\n\n" +
                        "您正在进行%s操作，验证码为：%s\n\n" +
                        "验证码有效期为5分钟，请及时使用。\n" +
                        "如果这不是您的操作，请忽略此邮件。\n\n" +
                        "图像识别系统\n" +
                        "%s",
                action, code, java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        );
    }

    /**
     * 邮箱地址脱敏
     */
    public String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return email;
        }

        String[] parts = email.split("@");
        String localPart = parts[0];
        String domain = parts[1];

        if (localPart.length() <= 2) {
            return email;
        }

        String maskedLocal = localPart.charAt(0) + "***" + localPart.charAt(localPart.length() - 1);
        return maskedLocal + "@" + domain;
    }
}
