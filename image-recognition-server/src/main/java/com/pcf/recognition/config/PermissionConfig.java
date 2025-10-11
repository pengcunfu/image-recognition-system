package com.pcf.recognition.config;

/**
 * 权限配置常量
 * 定义系统中的各种权限常量
 */
public class PermissionConfig {
    
    // 角色权限
    public static final String ROLE_USER = "ROLE_USER";
    public static final String ROLE_VIP = "ROLE_VIP";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    
    // 权限表达式
    public static final String HAS_USER_ROLE = "hasRole('USER')";
    public static final String HAS_VIP_ROLE = "hasRole('VIP')";
    public static final String HAS_ADMIN_ROLE = "hasRole('ADMIN')";
    
    // 组合权限
    public static final String HAS_USER_OR_VIP = "hasRole('USER') or hasRole('VIP')";
    public static final String HAS_VIP_OR_ADMIN = "hasRole('VIP') or hasRole('ADMIN')";
    public static final String HAS_ANY_ROLE = "hasRole('USER') or hasRole('VIP') or hasRole('ADMIN')";
    
    // 特殊权限
    public static final String IS_AUTHENTICATED = "isAuthenticated()";
    public static final String PERMIT_ALL = "permitAll()";
    
    // 资源权限
    public static final String CAN_ACCESS_BATCH = "hasRole('VIP') or hasRole('ADMIN')";
    public static final String CAN_MANAGE_USERS = "hasRole('ADMIN')";
    public static final String CAN_ACCESS_ADVANCED = "hasRole('VIP') or hasRole('ADMIN')";
}
