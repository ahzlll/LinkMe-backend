package com.linkme.backend.config;

import com.linkme.backend.common.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * JWT拦截器
 * 
 * 功能描述：
 * - 拦截需要认证的请求
 * - 验证JWT token的有效性
 * 
 * @author Ahz
 * @version 1.1
 */
@Component
public class JwtInterceptor implements HandlerInterceptor {
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        // 如果是静态资源处理器，直接放行
        if (handler instanceof ResourceHttpRequestHandler) {
            return true;
        }
        
        String path = request.getRequestURI();
        
        // 处理 OPTIONS 请求（CORS 预检请求）
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        
        // 规范化路径处理
        if (path == null) {
            path = "";
        }
        
        // 规范化路径：移除尾部斜杠（除了根路径）
        if (path.length() > 1 && path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        
        // 直接放行 Swagger/OpenAPI 相关路径和根路径（双重保险）
        // 检查根路径：可以是 "/" 或空字符串
        if (path.isEmpty() || path.equals("/") ||
                path.startsWith("/swagger-ui") ||
                path.startsWith("/v3/api-docs") ||
                path.startsWith("/swagger-resources") ||
                path.startsWith("/webjars") ||
                path.equals("/swagger-ui.html") ||
                path.equals("/favicon.ico") ||
                path.startsWith("/error")) {
            return true;
        }
        
        // 获取请求头中的token
        String token = request.getHeader("Authorization");
        
        if (token == null || !token.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"未提供有效的认证token\"}");
            return false;
        }
        
        // 提取token
        token = token.substring(7);
        
        try {
            // 验证token
            if (jwtUtil.isTokenExpired(token)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setCharacterEncoding("UTF-8");
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"code\":401,\"message\":\"token已过期\"}");
                return false;
            }
            
            // 将用户信息存储到请求中
            Integer userId = jwtUtil.getUserIdFromToken(token);
            String username = jwtUtil.getUsernameFromToken(token);
            request.setAttribute("userId", userId);
            request.setAttribute("username", username);
            
            return true;
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"token无效\"}");
            return false;
        }
    }
}