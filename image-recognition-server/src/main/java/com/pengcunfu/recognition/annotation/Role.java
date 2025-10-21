package com.pengcunfu.recognition.annotation;

import java.lang.annotation.*;

/**
 * 角色权限注解
 * 用于方法级别的角色权限控制
 * 
 * 使用示例：
 * - @Role("ADMIN") - 需要管理员权限
 * - @Role({"ADMIN", "VIP"}) - 需要管理员或VIP权限
 * - @Role(value = "ADMIN", requireAll = true) - 需要管理员权限
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Role {

    /**
     * 需要的角色
     * 可以是单个角色或多个角色
     * 角色名称：USER, VIP, ADMIN
     */
    String[] value() default {};

    /**
     * 是否需要满足所有角色
     * true: 需要拥有所有指定的角色（AND）
     * false: 拥有任一指定角色即可（OR）
     * 默认为 false
     */
    boolean requireAll() default false;

    /**
     * 权限验证失败时的提示消息
     */
    String message() default "权限不足，无法访问";
}

