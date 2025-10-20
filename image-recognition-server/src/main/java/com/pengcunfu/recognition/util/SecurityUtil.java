package com.pcf.recognition.util;

import com.pcf.recognition.dto.AuthDto.UserInfoDto;
import com.pcf.recognition.security.CustomUserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Spring Security 工具类
 * 提供获取当前登录用户信息的便捷方法
 */
public class SecurityUtil {

    /**
     * 获取当前登录用户的认证信息
     */
    public static Authentication getCurrentAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * 获取当前登录用户的用户主体
     */
    public static CustomUserPrincipal getCurrentUserPrincipal() {
        Authentication authentication = getCurrentAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserPrincipal) {
            return (CustomUserPrincipal) authentication.getPrincipal();
        }
        return null;
    }

    /**
     * 获取当前登录用户信息
     */
    public static UserInfoDto getCurrentUser() {
        CustomUserPrincipal userPrincipal = getCurrentUserPrincipal();
        return userPrincipal != null ? userPrincipal.getUserInfo() : null;
    }

    /**
     * 获取当前登录用户ID
     */
    public static Long getCurrentUserId() {
        CustomUserPrincipal userPrincipal = getCurrentUserPrincipal();
        return userPrincipal != null ? userPrincipal.getUserId() : null;
    }

    /**
     * 获取当前登录用户名
     */
    public static String getCurrentUsername() {
        CustomUserPrincipal userPrincipal = getCurrentUserPrincipal();
        return userPrincipal != null ? userPrincipal.getUsername() : null;
    }

    /**
     * 获取当前登录用户角色
     */
    public static String getCurrentUserRole() {
        CustomUserPrincipal userPrincipal = getCurrentUserPrincipal();
        return userPrincipal != null ? userPrincipal.getUserInfo().getRole() : null;
    }

    /**
     * 判断当前用户是否已登录
     */
    public static boolean isAuthenticated() {
        Authentication authentication = getCurrentAuthentication();
        return authentication != null && authentication.isAuthenticated() 
               && authentication.getPrincipal() instanceof CustomUserPrincipal;
    }

    /**
     * 判断当前用户是否为管理员
     */
    public static boolean isAdmin() {
        String role = getCurrentUserRole();
        return role != null && "ADMIN".equalsIgnoreCase(role);
    }

    /**
     * 判断当前用户是否为VIP或管理员
     */
    public static boolean isVipOrAdmin() {
        String role = getCurrentUserRole();
        return role != null && ("VIP".equalsIgnoreCase(role) || "ADMIN".equalsIgnoreCase(role));
    }

    /**
     * 判断当前用户是否有指定角色
     */
    public static boolean hasRole(String role) {
        String currentRole = getCurrentUserRole();
        return role != null && currentRole != null && role.equalsIgnoreCase(currentRole);
    }

    /**
     * 判断当前用户是否有任意一个指定角色
     */
    public static boolean hasAnyRole(String... roles) {
        String currentRole = getCurrentUserRole();
        if (currentRole == null) {
            return false;
        }
        
        for (String role : roles) {
            if (role != null && role.equalsIgnoreCase(currentRole)) {
                return true;
            }
        }
        return false;
    }
}
