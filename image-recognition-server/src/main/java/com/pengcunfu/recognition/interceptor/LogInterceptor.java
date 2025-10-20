package com.pengcunfu.recognition.interceptor;

import com.pengcunfu.recognition.security.SecurityContextHolder;
import com.pengcunfu.recognition.util.IpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 日志拦截器
 * 记录请求日志
 */
@Slf4j
@Component
public class LogInterceptor implements HandlerInterceptor {

    private static final String START_TIME = "startTime";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 记录请求开始时间
        request.setAttribute(START_TIME, System.currentTimeMillis());

        // 获取请求信息
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String ip = IpUtil.getClientIp(request);
        Long userId = SecurityContextHolder.getCurrentUserId();

        log.info("请求开始 - Method: {}, URI: {}, IP: {}, UserId: {}", method, uri, ip, userId);

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        // 可以在这里处理响应前的逻辑
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 计算请求处理时间
        Long startTime = (Long) request.getAttribute(START_TIME);
        if (startTime != null) {
            long duration = System.currentTimeMillis() - startTime;
            String method = request.getMethod();
            String uri = request.getRequestURI();
            int status = response.getStatus();

            log.info("请求结束 - Method: {}, URI: {}, Status: {}, Duration: {}ms", method, uri, status, duration);
        }

        // 清除SecurityContextHolder
        SecurityContextHolder.clear();

        // 如果有异常，记录异常日志
        if (ex != null) {
            log.error("请求异常 - URI: {}", request.getRequestURI(), ex);
        }
    }
}

