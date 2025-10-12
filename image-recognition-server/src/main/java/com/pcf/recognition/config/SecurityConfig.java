package com.pcf.recognition.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 配置
 * 配置系统的安全策略和权限控制
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authz -> authz
                        // 公开访问的接口（无需认证）
                        .requestMatchers(
                                // 根路径和首页
                                "/",
                                "/index.html",
                                "/home",

                                // 认证相关接口
                                "/api/v1/auth/login",
                                "/api/v1/auth/register",
                                "/api/v1/auth/forgot-password",
                                "/api/v1/auth/reset-password",
                                "/api/v1/auth/captcha",
                                "/api/v1/auth/sms-code",
                                "/api/v1/auth/sms-code/verify",
                                "/api/v1/auth/email-code",

                                // 验证码接口
                                "/api/v1/captcha/**",

                                // 首页和健康检查
                                "/api/v1/index/**",
                                "/api/v1/info",
                                "/api/v1/health",
                                "/api/v1/welcome/**",
                                "/api/v1/recognition/health",
                                "/api/v1/recognition/modes",

                                // 知识库公开内容
                                "/api/v1/knowledge/categories",
                                "/api/v1/knowledge/items",
                                "/api/v1/knowledge/items/*",
                                "/api/v1/knowledge/search",
                                "/api/v1/knowledge/popular",
                                "/api/v1/knowledge/latest",
                                "/api/v1/knowledge/stats",

                                // 社区公开内容
                                "/api/v1/community/posts",
                                "/api/v1/community/posts/*/comments",

                                // 文件访问
                                "/api/v1/files/download/**",
                                "/api/v1/files/image/**",

                                // Swagger文档
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/webjars/**",

                                // 静态资源
                                "/static/**",
                                "/public/**",
                                "/favicon.ico"
                        ).permitAll()

                        // 需要认证但不限制角色的接口
                        .requestMatchers(
                                "/api/v1/auth/logout",
                                "/api/v1/auth/validate",
                                "/api/v1/user/profile",
                                "/api/v1/user/settings",
                                "/api/v1/user/statistics",
                                "/api/v1/user/activities"
                        ).authenticated()

                        // 普通用户及以上可访问的接口
                        .requestMatchers(
                                "/api/v1/recognition/image",
                                "/api/v1/user/**",
                                "/api/v1/community/posts/*/like",
                                "/api/v1/knowledge/*/like"
                        ).hasAnyRole("USER", "VIP", "ADMIN")

                        // VIP用户及以上可访问的接口
                        .requestMatchers(
                                "/api/v1/recognition/batch/**",
                                "/api/v1/batch/**",
                                "/api/v1/doubao/**"
                        ).hasAnyRole("VIP", "ADMIN")

                        // 管理员专用接口
                        .requestMatchers(
                                "/api/v1/admin/**",
                                "/api/v1/management/**"
                        ).hasRole("ADMIN")

                        // 其他所有请求都需要认证
                        .anyRequest().authenticated()
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
