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
        log.debug("JWT过滤器处理请求: {}", requestPath);

        try {
            // 从请求头中获取Token
            String token = extractTokenFromRequest(request);
            
            if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // 验证Token格式
                if (jwtUtil.validateToken(token)) {
                    // 验证Token是否在Redis中存在（检查是否已被注销）
                    if (authService.isTokenValidInRedis(token)) {
                        // 从Redis中获取用户信息
                        UserInfoDto userInfo = authService.getUserInfoFromRedis(token);
                        
                        if (userInfo != null) {
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
                            
                            log.debug("JWT认证成功: userId={}, username={}, role={}", 
                                    userInfo.getId(), userInfo.getUsername(), userInfo.getRole());
                        } else {
                            log.warn("从Redis获取用户信息失败: token={}", token);
                        }
                    } else {
                        log.warn("Token在Redis中不存在或已失效: token={}", token);
                    }
                } else {
                    log.warn("Token格式无效: token={}", token);
                }
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
        
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // 去掉 "Bearer " 前缀
        }
        
        // 也支持直接在Authorization头中传递Token（不带Bearer前缀）
        if (bearerToken != null && !bearerToken.trim().isEmpty()) {
            return bearerToken;
        }
        
        return null;
    }


    /**
     * 判断是否跳过JWT认证
     * 对于公开接口，可以跳过JWT认证
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        
        // 对于公开接口，跳过JWT认证
        return path.startsWith("/api/v1/auth/login") ||
               path.startsWith("/api/v1/auth/register") ||
               path.startsWith("/api/v1/auth/captcha") ||
               path.startsWith("/api/v1/auth/email-code") ||
               path.startsWith("/api/v1/auth/forgot-password") ||
               path.startsWith("/static/") ||
               path.startsWith("/public/") ||
               path.equals("/favicon.ico");
    }
}
