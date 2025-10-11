package com.pcf.recognition.controller;

import com.pcf.recognition.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Random;

/**
 * 验证码控制器
 */
@Tag(name = "验证码管理", description = "验证码生成和验证相关接口")
@RestController
@RequestMapping("/api/v1/captcha")
@Slf4j
public class CaptchaController {

    private static final String CAPTCHA_SESSION_KEY = "CAPTCHA_CODE";
    private static final int CAPTCHA_WIDTH = 120;
    private static final int CAPTCHA_HEIGHT = 40;
    private static final int CAPTCHA_LENGTH = 4;
    
    @Operation(summary = "获取验证码", description = "生成图片验证码，返回base64编码的图片数据和验证码ID")
    @GetMapping("/generate")
    // 公开接口，无需权限验证
    public ApiResponse<CaptchaResponse> getCaptcha(HttpSession session) {
        try {
            // 生成验证码
            String code = generateCaptchaCode();
            
            // 生成验证码图片
            BufferedImage image = createCaptchaImage(code);
            
            // 转换为base64
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            String base64Image = "data:image/png;base64," + Base64.getEncoder().encodeToString(baos.toByteArray());
            
            // 存储到session（实际项目中可以存储到Redis）
            session.setAttribute(CAPTCHA_SESSION_KEY, code.toUpperCase());
            
            CaptchaResponse response = new CaptchaResponse();
            response.setCaptchaId(session.getId());
            response.setImageData(base64Image);
            response.setExpiryTime(System.currentTimeMillis() + 5 * 60 * 1000); // 5分钟过期
            
            log.info("验证码生成成功，sessionId: {}, code: {}", session.getId(), code);
            
            return ApiResponse.success(response, "验证码生成成功");
        } catch (Exception e) {
            log.error("生成验证码失败", e);
            return ApiResponse.error("验证码生成失败");
        }
    }
    
    @Operation(summary = "验证验证码", description = "验证用户输入的验证码是否正确")
    @PostMapping("/captcha/verify")
    // 公开接口，无需权限验证
    public ApiResponse<Boolean> verifyCaptcha(@RequestBody CaptchaVerifyRequest request, HttpSession session) {
        try {
            String sessionCode = (String) session.getAttribute(CAPTCHA_SESSION_KEY);
            
            if (sessionCode == null) {
                return ApiResponse.error("验证码已过期，请重新获取");
            }
            
            if (!sessionCode.equalsIgnoreCase(request.getCode())) {
                return ApiResponse.error("验证码错误");
            }
            
            // 验证成功后清除session中的验证码
            session.removeAttribute(CAPTCHA_SESSION_KEY);
            
            log.info("验证码验证成功，sessionId: {}", session.getId());
            return ApiResponse.success(true, "验证码验证成功");
        } catch (Exception e) {
            log.error("验证验证码失败", e);
            return ApiResponse.error("验证码验证失败");
        }
    }
    
    /**
     * 生成随机验证码
     */
    private String generateCaptchaCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        
        for (int i = 0; i < CAPTCHA_LENGTH; i++) {
            code.append(chars.charAt(random.nextInt(chars.length())));
        }
        
        return code.toString();
    }
    
    /**
     * 创建验证码图片
     */
    private BufferedImage createCaptchaImage(String code) {
        BufferedImage image = new BufferedImage(CAPTCHA_WIDTH, CAPTCHA_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        
        // 设置抗锯齿
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        Random random = new Random();
        
        // 设置背景色
        g2d.setColor(new Color(240, 240, 240));
        g2d.fillRect(0, 0, CAPTCHA_WIDTH, CAPTCHA_HEIGHT);
        
        // 绘制干扰线
        g2d.setStroke(new BasicStroke(1.5f));
        for (int i = 0; i < 6; i++) {
            g2d.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
            int x1 = random.nextInt(CAPTCHA_WIDTH);
            int y1 = random.nextInt(CAPTCHA_HEIGHT);
            int x2 = random.nextInt(CAPTCHA_WIDTH);
            int y2 = random.nextInt(CAPTCHA_HEIGHT);
            g2d.drawLine(x1, y1, x2, y2);
        }
        
        // 绘制验证码字符
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        int charWidth = CAPTCHA_WIDTH / CAPTCHA_LENGTH;
        
        for (int i = 0; i < code.length(); i++) {
            // 随机颜色
            g2d.setColor(new Color(random.nextInt(150), random.nextInt(150), random.nextInt(150)));
            
            // 随机位置和角度
            int x = i * charWidth + random.nextInt(10) + 10;
            int y = 25 + random.nextInt(10);
            
            // 随机旋转
            double angle = (random.nextDouble() - 0.5) * 0.4;
            g2d.rotate(angle, x, y);
            g2d.drawString(String.valueOf(code.charAt(i)), x, y);
            g2d.rotate(-angle, x, y);
        }
        
        // 绘制干扰点
        for (int i = 0; i < 100; i++) {
            g2d.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
            g2d.fillOval(random.nextInt(CAPTCHA_WIDTH), random.nextInt(CAPTCHA_HEIGHT), 2, 2);
        }
        
        g2d.dispose();
        return image;
    }
    
    /**
     * 验证码响应
     */
    public static class CaptchaResponse {
        private String captchaId;
        private String imageData;
        private Long expiryTime;
        
        // Getters and Setters
        public String getCaptchaId() { return captchaId; }
        public void setCaptchaId(String captchaId) { this.captchaId = captchaId; }
        
        public String getImageData() { return imageData; }
        public void setImageData(String imageData) { this.imageData = imageData; }
        
        public Long getExpiryTime() { return expiryTime; }
        public void setExpiryTime(Long expiryTime) { this.expiryTime = expiryTime; }
    }
    
    /**
     * 验证码验证请求
     */
    public static class CaptchaVerifyRequest {
        private String code;
        private String captchaId;
        
        // Getters and Setters
        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
        
        public String getCaptchaId() { return captchaId; }
        public void setCaptchaId(String captchaId) { this.captchaId = captchaId; }
    }
}
