package com.pengcunfu.recognition.aspect;

import com.pengcunfu.recognition.annotation.RateLimit;
import com.pengcunfu.recognition.constant.MessageConstants;
import com.pengcunfu.recognition.exception.RateLimitException;
import com.pengcunfu.recognition.security.SecurityContextHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 限流切面
 * 基于Redis实现的分布式限流
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RateLimitAspect {

    private final RedisTemplate<String, Object> redisTemplate;

    @Around("@annotation(rateLimit)")
    public Object around(ProceedingJoinPoint point, RateLimit rateLimit) throws Throwable {
        // 获取方法签名
        MethodSignature signature = (MethodSignature) point.getSignature();
        String methodName = signature.getMethod().getName();

        // 获取当前用户ID
        Long userId = SecurityContextHolder.getCurrentUserId();
        if (userId == null) {
            userId = 0L; // 未登录用户使用0
        }

        // 构建限流键
        String key = "rate_limit:" + methodName + ":" + userId;

        // 获取当前计数
        Integer count = (Integer) redisTemplate.opsForValue().get(key);
        
        if (count == null) {
            // 第一次访问，设置计数为1
            redisTemplate.opsForValue().set(key, 1, rateLimit.time(), TimeUnit.SECONDS);
        } else if (count < rateLimit.count()) {
            // 未达到限流阈值，增加计数
            redisTemplate.opsForValue().increment(key);
        } else {
            // 达到限流阈值，抛出异常
            log.warn("限流触发: method={}, userId={}, count={}", methodName, userId, count);
            throw new RateLimitException(MessageConstants.OPERATION_TOO_FREQUENT);
        }

        return point.proceed();
    }
}

