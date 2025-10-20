package com.pengcunfu.recognition.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pengcunfu.recognition.filter.JwtAuthenticationFilter;
import com.pengcunfu.recognition.response.ApiResponse;
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
                .cors(cors -> cors.and())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authz -> authz
                        // 公开访问的接口（无需认证）
                        .requestMatchers(
                                // 认证相关接口
                                "/api/v1/auth/login",
                                "/api/v1/auth/register",
                                "/api/v1/auth/forgot-password",
                                "/api/v1/auth/send-email-code",
                                
                                // 健康检查和系统信息
                                "/api/v1/health",
                                "/actuator/health",
                                
                                // 知识库公开接口
                                "/api/v1/knowledge",
                                "/api/v1/knowledge/categories",
                                "/api/v1/knowledge/search",
                                
                                // 社区公开接口（列表查看）
                                "/api/v1/community/posts",
                                "/api/v1/community/posts/*/comments",
                                
                                // 文件访问
                                "/api/v1/files/**",
                                "/files/**",
                                
                                // Swagger文档
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/webjars/**",
                                
                                // 静态资源
                                "/static/**",
                                "/favicon.ico"
                        ).permitAll()
                        
                        // 所有API接口都需要认证
                        .requestMatchers("/api/v1/**").authenticated()
                        
                        // 其他所有请求都需要认证
                        .anyRequest().authenticated()
                )
                // 配置异常处理
                .exceptionHandling(exceptions -> exceptions
                        // 处理认证失败（401）
                        .authenticationEntryPoint((request, response, authException) -> {
                            log.warn("认证失败: {} - {}", request.getRequestURI(), authException.getMessage());
                            
                            response.setStatus(200);
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            response.setCharacterEncoding("UTF-8");
                            
                            ApiResponse<Object> apiResponse = ApiResponse.error(401, "认证失败，请先登录");
                            response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
                        })
                        // 处理权限不足（403）
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            log.warn("访问被拒绝: {} - {}", request.getRequestURI(), accessDeniedException.getMessage());
                            
                            response.setStatus(200);
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            response.setCharacterEncoding("UTF-8");
                            
                            ApiResponse<Object> apiResponse = ApiResponse.error(403, "访问被拒绝，权限不足");
                            response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
                        })
                )
                // 添加JWT认证过滤器
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * 密码编码器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
