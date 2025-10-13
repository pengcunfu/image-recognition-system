package com.pcf.recognition.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pcf.recognition.dto.ApiResponse;
import com.pcf.recognition.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 配置
 * 配置系统的安全策略和权限控制
 */
@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final ObjectMapper objectMapper;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.and()) // 启用CORS支持
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

                                // 知识库公开内容（只允许列表和搜索，详情需要权限）
                                "/api/v1/knowledge/categories",  // 只允许获取分类列表
                                "/api/v1/knowledge/items",
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
                )
                // 配置异常处理
                .exceptionHandling(exceptions -> exceptions
                        // 处理认证失败（401）
                        .authenticationEntryPoint((request, response, authException) -> {
                            log.warn("认证失败: {} - {}", request.getRequestURI(), authException.getMessage());
                            
                            response.setStatus(200); // 返回200状态码
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            response.setCharacterEncoding("UTF-8");
                            
                            ApiResponse<Object> apiResponse = ApiResponse.error(401, "认证失败，请先登录");
                            
                            String jsonResponse = objectMapper.writeValueAsString(apiResponse);
                            response.getWriter().write(jsonResponse);
                        })
                        // 处理权限不足（403）
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            log.warn("访问被拒绝: {} - {}", request.getRequestURI(), accessDeniedException.getMessage());
                            
                            response.setStatus(200); // 返回200状态码
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            response.setCharacterEncoding("UTF-8");
                            
                            ApiResponse<Object> apiResponse = ApiResponse.error(403, "访问被拒绝，权限不足");
                            
                            String jsonResponse = objectMapper.writeValueAsString(apiResponse);
                            response.getWriter().write(jsonResponse);
                        })
                )
                // 添加JWT认证过滤器
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
