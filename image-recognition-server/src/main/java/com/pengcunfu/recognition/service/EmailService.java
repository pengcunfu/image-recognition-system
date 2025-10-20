package com.pengcunfu.recognition.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Random;

/**
 * 邮件服务
 * 处理邮件发送相关业务逻辑
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String from;

    @Value("${spring.application.name:图像识别系统}")
    private String appName;

    /**
     * 检查邮件服务是否已配置
     */
    private boolean isMailConfigured() {
        return from != null && !from.trim().isEmpty();
    }

    /**
     * 发送简单文本邮件
     */
    @Async
    public void sendSimpleEmail(String to, String subject, String content) {
        if (!isMailConfigured()) {
            log.warn("邮件服务未配置，跳过发送邮件: to={}, subject={}", to, subject);
            return;
        }

        try {
            log.info("发送简单邮件: to={}, subject={}", to, subject);

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content);

            mailSender.send(message);

            log.info("邮件发送成功: to={}", to);
        } catch (Exception e) {
            log.error("邮件发送失败: to={}, error={}", to, e.getMessage());
        }
    }

    /**
     * 发送HTML邮件
     */
    @Async
    public void sendHtmlEmail(String to, String subject, String htmlContent) {
        if (!isMailConfigured()) {
            log.warn("邮件服务未配置，跳过发送HTML邮件: to={}, subject={}", to, subject);
            return;
        }

        try {
            log.info("发送HTML邮件: to={}, subject={}", to, subject);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);

            log.info("HTML邮件发送成功: to={}", to);
        } catch (MessagingException e) {
            log.error("HTML邮件发送失败: to={}, error={}", to, e.getMessage());
        }
    }

    /**
     * 发送验证码邮件
     */
    @Async
    public void sendVerificationCode(String to, String code, String type) {
        if (!isMailConfigured()) {
            log.warn("邮件服务未配置，跳过发送验证码邮件: to={}, type={}", to, type);
            return;
        }

        try {
            log.info("发送验证码邮件: to={}, type={}", to, type);

            String subject = getVerificationSubject(type);
            String htmlContent = buildVerificationCodeHtml(code, type);

            sendHtmlEmail(to, subject, htmlContent);
        } catch (Exception e) {
            log.error("验证码邮件发送失败: to={}, error={}", to, e.getMessage());
        }
    }

    /**
     * 发送欢迎邮件
     */
    @Async
    public void sendWelcomeEmail(String to, String username) {
        if (!isMailConfigured()) {
            log.warn("邮件服务未配置，跳过发送欢迎邮件: to={}, username={}", to, username);
            return;
        }

        try {
            log.info("发送欢迎邮件: to={}, username={}", to, username);

            String subject = "欢迎加入" + appName;
            String htmlContent = buildWelcomeEmailHtml(username);

            sendHtmlEmail(to, subject, htmlContent);
        } catch (Exception e) {
            log.error("欢迎邮件发送失败: to={}, error={}", to, e.getMessage());
        }
    }

    /**
     * 发送密码重置邮件
     */
    @Async
    public void sendPasswordResetEmail(String to, String username, String code) {
        if (!isMailConfigured()) {
            log.warn("邮件服务未配置，跳过发送密码重置邮件: to={}, username={}", to, username);
            return;
        }

        try {
            log.info("发送密码重置邮件: to={}, username={}", to, username);

            String subject = appName + " - 密码重置";
            String htmlContent = buildPasswordResetHtml(username, code);

            sendHtmlEmail(to, subject, htmlContent);
        } catch (Exception e) {
            log.error("密码重置邮件发送失败: to={}, error={}", to, e.getMessage());
        }
    }

    /**
     * 发送系统通知邮件
     */
    @Async
    public void sendNotificationEmail(String to, String title, String content) {
        if (!isMailConfigured()) {
            log.warn("邮件服务未配置，跳过发送系统通知邮件: to={}, title={}", to, title);
            return;
        }

        try {
            log.info("发送系统通知邮件: to={}, title={}", to, title);

            String subject = appName + " - " + title;
            String htmlContent = buildNotificationEmailHtml(title, content);

            sendHtmlEmail(to, subject, htmlContent);
        } catch (Exception e) {
            log.error("系统通知邮件发送失败: to={}, error={}", to, e.getMessage());
        }
    }

    /**
     * 生成6位数字验证码
     */
    public String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }

    /**
     * 获取验证码邮件主题
     */
    private String getVerificationSubject(String type) {
        switch (type) {
            case "register":
                return appName + " - 注册验证码";
            case "reset":
                return appName + " - 密码重置验证码";
            case "bind":
                return appName + " - 邮箱绑定验证码";
            default:
                return appName + " - 验证码";
        }
    }

    /**
     * 构建验证码邮件HTML内容
     */
    private String buildVerificationCodeHtml(String code, String type) {
        try {
            String template = loadTemplate("email_template/verification-code.html");
            String purpose = getVerificationPurpose(type);
            
            return template
                    .replace("{{appName}}", appName)
                    .replace("{{code}}", code)
                    .replace("{{purpose}}", purpose);
        } catch (IOException e) {
            log.error("加载验证码邮件模板失败", e);
            throw new RuntimeException("邮件模板加载失败", e);
        }
    }

    /**
     * 构建欢迎邮件HTML内容
     */
    private String buildWelcomeEmailHtml(String username) {
        try {
            String template = loadTemplate("email_template/welcome.html");
            return template
                    .replace("{{appName}}", appName)
                    .replace("{{username}}", username);
        } catch (IOException e) {
            log.error("加载欢迎邮件模板失败", e);
            throw new RuntimeException("邮件模板加载失败", e);
        }
    }

    /**
     * 构建密码重置邮件HTML内容
     */
    private String buildPasswordResetHtml(String username, String code) {
        try {
            String template = loadTemplate("email_template/password-reset.html");
            return template
                    .replace("{{appName}}", appName)
                    .replace("{{username}}", username)
                    .replace("{{code}}", code);
        } catch (IOException e) {
            log.error("加载密码重置邮件模板失败", e);
            throw new RuntimeException("邮件模板加载失败", e);
        }
    }

    /**
     * 构建通知邮件HTML内容
     */
    private String buildNotificationEmailHtml(String title, String content) {
        try {
            String template = loadTemplate("email_template/notification.html");
            return template
                    .replace("{{appName}}", appName)
                    .replace("{{title}}", title)
                    .replace("{{content}}", content);
        } catch (IOException e) {
            log.error("加载通知邮件模板失败", e);
            throw new RuntimeException("邮件模板加载失败", e);
        }
    }

    /**
     * 加载邮件模板
     */
    private String loadTemplate(String templatePath) throws IOException {
        ClassPathResource resource = new ClassPathResource(templatePath);
        return StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
    }

    /**
     * 获取验证码用途描述
     */
    private String getVerificationPurpose(String type) {
        switch (type) {
            case "register":
                return "注册账号";
            case "reset":
                return "重置密码";
            case "bind":
                return "绑定邮箱";
            default:
                return "验证身份";
        }
    }
}

