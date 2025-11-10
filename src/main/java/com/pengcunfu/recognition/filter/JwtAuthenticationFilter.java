package com.pengcunfu.recognition.filter;

import com.pengcunfu.recognition.constant.JwtConstants;
import com.pengcunfu.recognition.security.UserPrincipal;
import com.pengcunfu.recognition.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                   HttpServletResponse response, 
                                   FilterChain filterChain) throws ServletException, IOException {
        try {
            // 从请求头中获取Token
            String token = extractTokenFromRequest(request);
            
            if (token != null && jwtUtil.validateToken(token)) {
                // 从Token中获取用户信息
                Long userId = jwtUtil.getUserIdFromToken(token);
                String username = jwtUtil.getUsernameFromToken(token);
                Integer role = jwtUtil.getRoleFromToken(token);

                if (userId != null && username != null) {
                    // 创建用户主体
                    UserPrincipal userPrincipal = new UserPrincipal(
                        userId,
                        username,
                        null, // password (不需要)
                        role,
                        0, // status: 0-ACTIVE
                        true // enabled
                    );

                    // 设置到自定义的SecurityContextHolder
                    com.pengcunfu.recognition.security.SecurityContextHolder.setCurrentUser(userPrincipal);

                    // 创建认证对象
                    UsernamePasswordAuthenticationToken authentication = 
                        new UsernamePasswordAuthenticationToken(
                            userPrincipal,
                            null,
                            userPrincipal.getAuthorities()
                        );
                    
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // 设置到Spring Security上下文
                    org.springframework.security.core.context.SecurityContextHolder.getContext().setAuthentication(authentication);
                    
                    log.debug("JWT认证成功: userId={}, username={}", userId, username);
                }
            }
        } catch (Exception e) {
            log.error("JWT认证过滤器异常", e);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 从请求头中提取Token
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(JwtConstants.TOKEN_HEADER);
        
        if (bearerToken != null && bearerToken.startsWith(JwtConstants.TOKEN_PREFIX)) {
            return bearerToken.substring(JwtConstants.TOKEN_PREFIX.length());
        }
        
        return null;
    }
}
