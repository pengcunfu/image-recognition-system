package com.pengcunfu.recognition.aspect;

import com.pengcunfu.recognition.annotation.Role;
import com.pengcunfu.recognition.constant.ErrorCode;
import com.pengcunfu.recognition.entity.User;
import com.pengcunfu.recognition.enums.UserRole;
import com.pengcunfu.recognition.exception.BusinessException;
import com.pengcunfu.recognition.repository.UserRepository;
import com.pengcunfu.recognition.security.SecurityContextHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 角色权限切面
 * 处理 @Role 注解的权限验证
 */
@Slf4j
@Aspect
@Order(2) // 在认证之后执行
@Component
@RequiredArgsConstructor
public class RoleAspect {

    private final UserRepository userRepository;

    /**
     * 方法级别的角色验证
     */
    @Before("@annotation(com.pengcunfu.recognition.annotation.Role)")
    public void checkMethodRole(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Role roleAnnotation = method.getAnnotation(Role.class);
        
        if (roleAnnotation != null) {
            checkRole(roleAnnotation);
        }
    }

    /**
     * 类级别的角色验证
     */
    @Before("@within(com.pengcunfu.recognition.annotation.Role) && !@annotation(com.pengcunfu.recognition.annotation.Role)")
    public void checkClassRole(JoinPoint joinPoint) {
        Class<?> targetClass = joinPoint.getTarget().getClass();
        Role roleAnnotation = targetClass.getAnnotation(Role.class);
        
        if (roleAnnotation != null) {
            checkRole(roleAnnotation);
        }
    }

    /**
     * 执行角色验证
     */
    private void checkRole(Role roleAnnotation) {
        // 获取当前用户ID
        Long userId = SecurityContextHolder.getCurrentUserId();
        
        if (userId == null) {
            log.warn("角色验证失败: 用户未登录");
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "请先登录");
        }

        // 获取用户信息
        User user = userRepository.selectById(userId);
        if (user == null) {
            log.warn("角色验证失败: 用户不存在, userId={}", userId);
            throw new BusinessException(ErrorCode.USER_NOT_FOUND, "用户不存在");
        }

        // 获取需要的角色
        String[] requiredRoles = roleAnnotation.value();
        if (requiredRoles.length == 0) {
            log.warn("@Role 注解未指定任何角色");
            return;
        }

        // 获取用户当前角色
        Integer userRoleCode = user.getRole();
        UserRole userRole = UserRole.fromCode(userRoleCode);
        
        if (userRole == null) {
            log.warn("角色验证失败: 用户角色无效, userId={}, roleCode={}", userId, userRoleCode);
            throw new BusinessException(ErrorCode.FORBIDDEN, roleAnnotation.message());
        }

        // 验证角色
        boolean hasPermission = checkPermission(userRole, requiredRoles, roleAnnotation.requireAll());
        
        if (!hasPermission) {
            log.warn("角色验证失败: userId={}, userRole={}, requiredRoles={}, requireAll={}", 
                    userId, userRole.name(), Arrays.toString(requiredRoles), roleAnnotation.requireAll());
            throw new BusinessException(ErrorCode.FORBIDDEN, roleAnnotation.message());
        }

        log.debug("角色验证通过: userId={}, userRole={}, requiredRoles={}", 
                userId, userRole.name(), Arrays.toString(requiredRoles));
    }

    /**
     * 检查权限
     */
    private boolean checkPermission(UserRole userRole, String[] requiredRoles, boolean requireAll) {
        Set<String> userRoleSet = new HashSet<>();
        
        // 构建用户拥有的角色集合
        // USER 角色
        if (userRole == UserRole.USER || userRole == UserRole.VIP || userRole == UserRole.ADMIN) {
            userRoleSet.add("USER");
        }
        
        // VIP 角色
        if (userRole == UserRole.VIP || userRole == UserRole.ADMIN) {
            userRoleSet.add("VIP");
        }
        
        // ADMIN 角色
        if (userRole == UserRole.ADMIN) {
            userRoleSet.add("ADMIN");
        }

        // 如果需要所有角色（AND）
        if (requireAll) {
            for (String requiredRole : requiredRoles) {
                if (!userRoleSet.contains(requiredRole.toUpperCase())) {
                    return false;
                }
            }
            return true;
        } else {
            // 只需要任一角色（OR）
            for (String requiredRole : requiredRoles) {
                if (userRoleSet.contains(requiredRole.toUpperCase())) {
                    return true;
                }
            }
            return false;
        }
    }
}

