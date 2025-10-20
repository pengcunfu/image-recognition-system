package com.pengcunfu.recognition.service.redis;

import com.pengcunfu.recognition.constant.CacheKeyPrefix;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 验证码服务
 * 管理邮箱验证码、图形验证码等
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VerificationCodeService {

    private final RedisService redisService;
    private static final Random RANDOM = new Random();

    /**
     * 生成6位数字验证码
     */
    public String generateCode() {
        return String.format("%06d", RANDOM.nextInt(1000000));
    }

    /**
     * 保存邮箱验证码
     */
    public void saveEmailCode(String email, String code) {
        String key = CacheKeyPrefix.EMAIL_CODE.getKey(email);
        redisService.set(key, code, CacheKeyPrefix.EMAIL_CODE.getExpireTime(), TimeUnit.SECONDS);
        log.info("保存邮箱验证码: email={}, expire={}s", email, CacheKeyPrefix.EMAIL_CODE.getExpireTime());
    }

    /**
     * 验证邮箱验证码
     */
    public boolean verifyEmailCode(String email, String code) {
        String key = CacheKeyPrefix.EMAIL_CODE.getKey(email);
        Object savedCode = redisService.get(key);
        
        if (savedCode == null) {
            log.warn("邮箱验证码不存在或已过期: email={}", email);
            return false;
        }

        boolean isValid = savedCode.toString().equals(code);
        if (isValid) {
            // 验证成功后删除验证码
            redisService.delete(key);
            log.info("邮箱验证码验证成功: email={}", email);
        } else {
            log.warn("邮箱验证码验证失败: email={}", email);
        }

        return isValid;
    }

    /**
     * 保存图形验证码
     */
    public void saveImageCaptcha(String sessionId, String code) {
        String key = CacheKeyPrefix.IMAGE_CAPTCHA.getKey(sessionId);
        redisService.set(key, code, CacheKeyPrefix.IMAGE_CAPTCHA.getExpireTime(), TimeUnit.SECONDS);
        log.debug("保存图形验证码: sessionId={}", sessionId);
    }

    /**
     * 验证图形验证码
     */
    public boolean verifyImageCaptcha(String sessionId, String code) {
        String key = CacheKeyPrefix.IMAGE_CAPTCHA.getKey(sessionId);
        Object savedCode = redisService.get(key);
        
        if (savedCode == null) {
            log.warn("图形验证码不存在或已过期: sessionId={}", sessionId);
            return false;
        }

        // 验证码不区分大小写
        boolean isValid = savedCode.toString().equalsIgnoreCase(code);
        if (isValid) {
            // 验证成功后删除验证码
            redisService.delete(key);
            log.debug("图形验证码验证成功: sessionId={}", sessionId);
        } else {
            log.warn("图形验证码验证失败: sessionId={}", sessionId);
        }

        return isValid;
    }

    /**
     * 检查验证码是否存在
     */
    public boolean isEmailCodeExists(String email) {
        String key = CacheKeyPrefix.EMAIL_CODE.getKey(email);
        Boolean exists = redisService.hasKey(key);
        return exists != null && exists;
    }

    /**
     * 获取验证码剩余过期时间（秒）
     */
    public Long getEmailCodeTTL(String email) {
        String key = CacheKeyPrefix.EMAIL_CODE.getKey(email);
        return redisService.getExpire(key, TimeUnit.SECONDS);
    }
}

