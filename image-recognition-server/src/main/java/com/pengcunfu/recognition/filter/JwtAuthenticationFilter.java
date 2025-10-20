package com.pcf.recognition.filter;

import com.pcf.recognition.dto.AuthDto.UserInfoDto;
import com.pcf.recognition.security.CustomUserPrincipal;
import com.pcf.recognition.service.AuthService;
import com.pcf.recognition.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT认证过滤器
 * 从请求头中提取JWT Token并验证，设置Spring Security上下文
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final AuthService authService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        String requestPath = request.getRequestURI();
        log.info("JWT过滤器处理请求: {} [{}]", requestPath, request.getMethod());
        
        // 打印所有请求头用于调试
        java.util.Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            if ("authorization".equalsIgnoreCase(headerName)) {
                log.info("请求头 {}: {}", headerName, headerValue);
            }
        }

        try {
            // 从请求头中获取Token
            String token = extractTokenFromRequest(request);
            
            if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                log.info("发现Token，开始验证: {}", token.substring(0, Math.min(20, token.length())) + "...");
                
                // 验证Token格式
                if (jwtUtil.validateToken(token)) {
                    log.info("Token格式验证通过");
                    
                    // 验证Token是否在Redis中存在（检查是否已被注销）
                    if (authService.isTokenValidInRedis(token)) {
                        log.info("Token在Redis中有效");
                        
                        // 从Redis中获取用户信息
                        UserInfoDto userInfo = authService.getUserInfoFromRedis(token);
                        
                        if (userInfo != null) {
                            log.info("从Redis获取用户信息成功: userId={}, username={}, role={}", 
                                    userInfo.getId(), userInfo.getUsername(), userInfo.getRole());
                            
                            // 创建自定义用户主体
                            CustomUserPrincipal userPrincipal = new CustomUserPrincipal(userInfo);
                            
                            // 创建认证对象
                            UsernamePasswordAuthenticationToken authentication = 
                                new UsernamePasswordAuthenticationToken(
                                    userPrincipal, // principal
                                    null, // credentials
                                    userPrincipal.getAuthorities() // authorities
                                );
                            
                            // 设置请求详情
                            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                            
                            // 设置到Spring Security上下文
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                            
                            log.info("JWT认证成功，权限: {}", userPrincipal.getAuthorities());
                        } else {
                            log.warn("从Redis获取用户信息失败: token={}", token.substring(0, Math.min(10, token.length())) + "...");
                        }
                    } else {
                        log.warn("Token在Redis中不存在或已失效");
                    }
                } else {
                    log.warn("Token格式无效");
                }
            } else if (token == null) {
                log.info("请求中没有找到Token");
            } else {
                log.info("SecurityContext中已存在认证信息");
            }
        } catch (Exception e) {
            log.error("JWT认证过程中发生异常: path={}", requestPath, e);
            // 不抛出异常，让请求继续，由Spring Security处理未认证的情况
        }

        // 继续过滤链
        filterChain.doFilter(request, response);
    }

    /**
     * 从请求中提取Token
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        
        log.info("Authorization头内容: {}", bearerToken);
        
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring(7); // 去掉 "Bearer " 前缀
            log.info("提取到Bearer Token: {}...", token.substring(0, Math.min(20, token.length())));
            return token;
        }
        
        // 也支持直接在Authorization头中传递Token（不带Bearer前缀）
        if (bearerToken != null && !bearerToken.trim().isEmpty()) {
            log.info("提取到直接Token: {}...", bearerToken.substring(0, Math.min(20, bearerToken.length())));
            return bearerToken;
        }
        
        log.info("未找到Authorization头或头为空");
        return null;
    }


    /**
     * 判断是否跳过JWT认证
     * 只跳过真正不需要任何认证的公开接口
     * 权限控制应该由SecurityConfig和@PreAuthorize注解处理
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();
        
        // CORS预检请求不需要JWT认证
        if ("OPTIONS".equalsIgnoreCase(method)) {
            log.info("跳过CORS预检请求: {} [{}]", path, method);
            return true;
        }
        
        // 只跳过完全公开的接口，让SecurityConfig处理权限控制
        return path.startsWith("/static/") ||
               path.startsWith("/public/") ||
               path.startsWith("/swagger-ui/") ||
               path.startsWith("/v3/api-docs/") ||
               path.equals("/favicon.ico") ||
               path.equals("/error");
    }
}
