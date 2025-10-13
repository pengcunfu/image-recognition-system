package com.pcf.recognition.security;

import com.pcf.recognition.dto.AuthDto.UserInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * 自定义用户主体
 * 实现Spring Security的UserDetails接口，用于存储用户认证信息
 */
@RequiredArgsConstructor
public class CustomUserPrincipal implements UserDetails {

    private final UserInfoDto userInfo;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + userInfo.getRole())
        );
    }

    @Override
    public String getPassword() {
        return null; // JWT认证不需要密码
    }

    @Override
    public String getUsername() {
        return userInfo.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !"BANNED".equals(userInfo.getStatus());
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return "ACTIVE".equals(userInfo.getStatus());
    }

    /**
     * 获取用户信息
     */
    public UserInfoDto getUserInfo() {
        return userInfo;
    }

    /**
     * 获取用户ID
     */
    public Long getUserId() {
        return userInfo.getId();
    }

    /**
     * 获取用户角色
     */
    public String getRole() {
        return userInfo.getRole();
    }
}
