package com.linkme.backend.config;

import com.linkme.backend.common.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.net.URI;
import java.util.Map;

/**
 * WebSocket认证拦截器
 * 
 * 功能描述：
 * - 在WebSocket握手时进行JWT认证
 * - 从查询参数中获取token并验证用户身份
 * - 将用户ID存储到session attributes中供后续使用
 * 
 * @author Ahz
 * @version 1.2.2
 */
@Component
public class WebSocketAuthInterceptor implements HandshakeInterceptor {
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Override
    public boolean beforeHandshake(@NonNull ServerHttpRequest request, 
                                   @NonNull ServerHttpResponse response,
                                   @NonNull WebSocketHandler wsHandler,
                                   @NonNull Map<String, Object> attributes) throws Exception {
        // 从查询参数中获取token
        URI uri = request.getURI();
        String query = uri.getQuery();
        
        if (query == null || query.isEmpty()) {
            System.err.println("WebSocket握手失败: 未提供token");
            return false;
        }
        
        // 解析查询参数，获取token
        String token = null;
        String[] params = query.split("&");
        for (String param : params) {
            if (param.startsWith("token=")) {
                token = param.substring(6); // "token=" 长度为6
                break;
            }
        }
        
        if (token == null || token.isEmpty()) {
            System.err.println("WebSocket握手失败: token为空");
            return false;
        }
        
        try {
            // 验证token并获取用户ID
            Integer userId = jwtUtil.getUserIdFromToken(token);
            if (userId == null) {
                System.err.println("WebSocket握手失败: token无效");
                return false;
            }
            
            // 将用户ID存储到session attributes中
            attributes.put("userId", userId.toString());
            System.out.println("WebSocket认证成功: 用户ID=" + userId);
            return true;
        } catch (Exception e) {
            System.err.println("WebSocket握手失败: token验证异常 - " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public void afterHandshake(@NonNull ServerHttpRequest request,
                              @NonNull ServerHttpResponse response,
                              @NonNull WebSocketHandler wsHandler,
                              @Nullable Exception exception) {
        // 握手后的处理（通常不需要）
    }
}

