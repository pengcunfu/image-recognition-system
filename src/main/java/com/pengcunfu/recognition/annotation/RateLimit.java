package com.pengcunfu.recognition.annotation;

import java.lang.annotation.*;

/**
 * 限流注解
 * 用于方法级别的限流控制
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimit {

    /**
     * 限流次数
     */
    int count() default 100;

    /**
     * 限流时间窗口（秒）
     */
    int time() default 60;

    /**
     * 限流提示消息
     */
    String message() default "操作过于频繁，请稍后再试";
}

