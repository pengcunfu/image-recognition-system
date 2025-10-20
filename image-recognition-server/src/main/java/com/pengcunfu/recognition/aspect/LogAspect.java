package com.pengcunfu.recognition.aspect;

import com.pengcunfu.recognition.security.SecurityContextHolder;
import com.pengcunfu.recognition.util.IpUtil;
import com.pengcunfu.recognition.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 日志切面
 * 记录Controller方法的请求和响应日志
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LogAspect {

    private final JsonUtil jsonUtil;

    /**
     * 切入点：所有Controller
     */
    @Pointcut("execution(* com.pengcunfu.recognition.controller..*.*(..))")
    public void controllerPointcut() {
    }

    /**
     * 环绕通知
     */
    @Around("controllerPointcut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long startTime = System.currentTimeMillis();

        // 获取请求信息
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes != null ? attributes.getRequest() : null;

        // 获取方法信息
        MethodSignature signature = (MethodSignature) point.getSignature();
        String className = point.getTarget().getClass().getSimpleName();
        String methodName = signature.getName();

        // 记录请求日志
        if (request != null) {
            String ip = IpUtil.getClientIp(request);
            Long userId = SecurityContextHolder.getCurrentUserId();
            String uri = request.getRequestURI();
            String method = request.getMethod();
            Object[] args = point.getArgs();

            log.info("===> 请求开始: {}.{} - {} {} - IP: {} - UserId: {} - Args: {}", 
                    className, methodName, method, uri, ip, userId, jsonUtil.toJson(args));
        }

        // 执行方法
        Object result = null;
        try {
            result = point.proceed();
            
            // 记录响应日志
            long duration = System.currentTimeMillis() - startTime;
            log.info("<=== 请求结束: {}.{} - 耗时: {}ms - Result: {}", 
                    className, methodName, duration, jsonUtil.toJson(result));
            
            return result;
        } catch (Throwable e) {
            // 记录异常日志
            long duration = System.currentTimeMillis() - startTime;
            log.error("<=== 请求异常: {}.{} - 耗时: {}ms - Exception: {}", 
                    className, methodName, duration, e.getMessage(), e);
            throw e;
        }
    }
}

