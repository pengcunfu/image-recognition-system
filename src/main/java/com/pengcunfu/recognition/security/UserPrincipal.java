package com.pengcunfu.recognition.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * 用户认证主体
 * 实现Spring Security的UserDetails接口
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPrincipal implements UserDetails {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 用户角色
     */
    private Integer role;

    /**
     * 账号状态
     */
    private Integer status;

    /**
     * 是否启用
     */
    private boolean enabled;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 根据角色返回权限
        String authority = "ROLE_USER";
        if (role != null) {
            if (role == 2) {
                authority = "ROLE_ADMIN";
            } else if (role == 1) {
                authority = "ROLE_VIP";
            }
        }
        return Collections.singletonList(new SimpleGrantedAuthority(authority));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // status: 0-ACTIVE, 1-BANNED, 2-INACTIVE
        return status != null && status != 1;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        // status: 0-ACTIVE
        return status != null && status == 0;
    }
}

