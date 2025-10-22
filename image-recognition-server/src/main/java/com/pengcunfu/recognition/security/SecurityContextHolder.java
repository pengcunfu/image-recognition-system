package com.pengcunfu.recognition.security;

/**
 * 安全上下文持有者
 * 用于在当前线程中存储和获取当前登录用户信息
 */
public class SecurityContextHolder {

    private static final ThreadLocal<UserPrincipal> CONTEXT = new ThreadLocal<>();

    /**
     * 设置当前用户
     */
    public static void setCurrentUser(UserPrincipal userPrincipal) {
        CONTEXT.set(userPrincipal);
    }

    /**
     * 获取当前用户
     */
    public static UserPrincipal getCurrentUser() {
        return CONTEXT.get();
    }

    /**
     * 获取当前用户ID
     */
    public static Long getCurrentUserId() {
        UserPrincipal userPrincipal = getCurrentUser();
        return userPrincipal != null ? userPrincipal.getUserId() : null;
    }

    /**
     * 获取当前用户ID(允许为null,用于不需要认证的接口)
     */
    public static Long getCurrentUserIdOrNull() {
        UserPrincipal userPrincipal = getCurrentUser();
        return userPrincipal != null ? userPrincipal.getUserId() : null;
    }

    /**
     * 获取当前用户名
     */
    public static String getCurrentUsername() {
        UserPrincipal userPrincipal = getCurrentUser();
        return userPrincipal != null ? userPrincipal.getUsername() : null;
    }

    /**
     * 获取当前用户角色
     */
    public static Integer getCurrentUserRole() {
        UserPrincipal userPrincipal = getCurrentUser();
        return userPrincipal != null ? userPrincipal.getRole() : null;
    }

    /**
     * 清除当前用户
     */
    public static void clear() {
        CONTEXT.remove();
    }

    private SecurityContextHolder() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}

