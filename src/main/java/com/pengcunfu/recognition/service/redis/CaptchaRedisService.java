package com.pengcunfu.recognition.service.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 验证码Redis服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CaptchaRedisService {

    private final StringRedisTemplate stringRedisTemplate;

    /**
     * Redis键前缀
     */
    private static final String CAPTCHA_KEY_PREFIX = "captcha:";

    /**
     * 验证码有效期（分钟）
     */
    private static final long CAPTCHA_EXPIRE_MINUTES = 5;

    /**
     * 保存验证码
     */
    public void saveCaptcha(String captchaId, String captchaCode) {
        String key = CAPTCHA_KEY_PREFIX + captchaId;
        stringRedisTemplate.opsForValue().set(key, captchaCode, CAPTCHA_EXPIRE_MINUTES, TimeUnit.MINUTES);
        log.debug("保存验证码到Redis: key={}, code={}", key, captchaCode);
    }

    /**
     * 获取验证码
     */
    public String getCaptcha(String captchaId) {
        String key = CAPTCHA_KEY_PREFIX + captchaId;
        return stringRedisTemplate.opsForValue().get(key);
    }

    /**
     * 删除验证码
     */
    public void deleteCaptcha(String captchaId) {
        String key = CAPTCHA_KEY_PREFIX + captchaId;
        stringRedisTemplate.delete(key);
        log.debug("删除验证码: key={}", key);
    }
}

