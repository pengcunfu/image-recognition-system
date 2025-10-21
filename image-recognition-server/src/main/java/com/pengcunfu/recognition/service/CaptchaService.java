package com.pengcunfu.recognition.service;

import com.pengcunfu.recognition.service.redis.CaptchaRedisService;
import com.wf.captcha.SpecCaptcha;
import com.wf.captcha.base.Captcha;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 验证码服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CaptchaService {

    private final CaptchaRedisService captchaRedisService;
    
    /**
     * 验证码宽度
     */
    private static final int WIDTH = 130;
    
    /**
     * 验证码高度
     */
    private static final int HEIGHT = 48;
    
    /**
     * 验证码字符长度
     */
    private static final int CODE_LENGTH = 4;

    /**
     * 生成验证码
     */
    public Map<String, String> generateCaptcha() {
        // 生成唯一标识
        String captchaId = UUID.randomUUID().toString().replace("-", "");
        
        // 每次创建新的验证码实例，确保验证码随机生成
        SpecCaptcha captcha = new SpecCaptcha(WIDTH, HEIGHT, CODE_LENGTH);
        captcha.setCharType(Captcha.TYPE_DEFAULT);
        
        // 获取验证码文本
        String captchaText = captcha.text();
        
        // 获取验证码图片Base64
        String captchaImage = captcha.toBase64();
        
        // 保存验证码到Redis，5分钟有效期
        captchaRedisService.saveCaptcha(captchaId, captchaText.toLowerCase());
        
        log.info("生成验证码: captchaId={}, text={}", captchaId, captchaText);
        
        Map<String, String> result = new HashMap<>();
        result.put("captchaId", captchaId);
        result.put("captchaImage", captchaImage);
        return result;
    }

    /**
     * 验证验证码
     */
    public boolean verifyCaptcha(String captchaId, String captchaCode) {
        if (captchaId == null || captchaCode == null) {
            return false;
        }
        
        String savedCode = captchaRedisService.getCaptcha(captchaId);
        if (savedCode == null) {
            log.warn("验证码不存在或已过期: captchaId={}", captchaId);
            return false;
        }
        
        // 验证码不区分大小写
        boolean isValid = savedCode.equalsIgnoreCase(captchaCode);
        
        if (isValid) {
            // 验证成功后删除验证码
            captchaRedisService.deleteCaptcha(captchaId);
            log.info("验证码验证成功: captchaId={}", captchaId);
        } else {
            log.warn("验证码验证失败: captchaId={}, expected={}, actual={}", 
                captchaId, savedCode, captchaCode);
        }
        
        return isValid;
    }
}

