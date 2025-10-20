package com.pcf.recognition.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Token工具类
 * 提供从请求头中获取用户信息的便捷方法
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TokenUtil {

    private final JwtUtil jwtUtil;

    /**
     * 从Authorization头中获取用户ID
     *
     * @param authorizationHeader Authorization请求头
     * @return 用户ID，如果获取失败返回null
     */
    public Long getUserIdFromHeader(String authorizationHeader) {
        if (authorizationHeader == null || authorizationHeader.trim().isEmpty()) {
            log.warn("Authorization头为空");
            return null;
        }

        try {
            return jwtUtil.getUserIdFromToken(authorizationHeader);
        } catch (Exception e) {
            log.error("从Authorization头获取用户ID失败: {}", authorizationHeader, e);
            return null;
        }
    }

    /**
     * 从Authorization头中获取用户名
     *
     * @param authorizationHeader Authorization请求头
     * @return 用户名，如果获取失败返回null
     */
    public String getUsernameFromHeader(String authorizationHeader) {
        if (authorizationHeader == null || authorizationHeader.trim().isEmpty()) {
            log.warn("Authorization头为空");
            return null;
        }

        try {
            return jwtUtil.getUsernameFromToken(authorizationHeader);
        } catch (Exception e) {
            log.error("从Authorization头获取用户名失败: {}", authorizationHeader, e);
            return null;
        }
    }

    /**
     * 从Authorization头中获取用户角色
     *
     * @param authorizationHeader Authorization请求头
     * @return 用户角色，如果获取失败返回null
     */
    public String getRoleFromHeader(String authorizationHeader) {
        if (authorizationHeader == null || authorizationHeader.trim().isEmpty()) {
            log.warn("Authorization头为空");
            return null;
        }

        try {
            return jwtUtil.getRoleFromToken(authorizationHeader);
        } catch (Exception e) {
            log.error("从Authorization头获取用户角色失败: {}", authorizationHeader, e);
            return null;
        }
    }

    /**
     * 验证Authorization头中的Token是否有效
     *
     * @param authorizationHeader Authorization请求头
     * @return 是否有效
     */
    public boolean validateTokenFromHeader(String authorizationHeader) {
        if (authorizationHeader == null || authorizationHeader.trim().isEmpty()) {
            return false;
        }

        try {
            return jwtUtil.validateToken(authorizationHeader);
        } catch (Exception e) {
            log.error("验证Authorization头中的Token失败: {}", authorizationHeader, e);
            return false;
        }
    }
}
