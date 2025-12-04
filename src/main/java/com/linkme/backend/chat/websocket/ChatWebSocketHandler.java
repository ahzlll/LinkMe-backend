package com.linkme.backend.chat.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket聊天处理器
 * 
 * 功能描述：
 * - 处理WebSocket连接和消息传输
 * - 支持实时聊天功能
 * - 管理用户连接状态
 * 
 * @author Ahz
 * @version 1.2.2
 */
@Component
public class ChatWebSocketHandler implements WebSocketHandler {
    
    // 存储用户ID和WebSocket会话的映射
    private static final ConcurrentHashMap<String, WebSocketSession> userSessions = new ConcurrentHashMap<>();
    
    // 存储会话ID和用户ID的映射（用于反向查找）
    private static final ConcurrentHashMap<WebSocketSession, String> sessionUsers = new ConcurrentHashMap<>();
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) throws Exception {
        // 从session attributes中获取用户ID（由拦截器设置）
        String userId = (String) session.getAttributes().get("userId");
        if (userId != null) {
            // 如果用户已经存在连接，关闭旧连接
            WebSocketSession oldSession = userSessions.get(userId);
            if (oldSession != null && oldSession.isOpen()) {
                try {
                    oldSession.close();
                } catch (IOException e) {
                    System.err.println("关闭旧连接失败: " + e.getMessage());
                }
            }
            
            userSessions.put(userId, session);
            sessionUsers.put(session, userId);
            System.out.println("WebSocket连接建立: 用户ID=" + userId + ", 会话ID=" + session.getId());
            
            // 发送连接成功消息
            Map<String, Object> welcomeMsg = Map.of(
                "type", "connected",
                "message", "连接成功",
                "userId", userId
            );
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(welcomeMsg)));
        } else {
            System.err.println("WebSocket连接建立失败: 未找到用户ID");
            session.close();
        }
    }
    
    @Override
    public void handleMessage(@NonNull WebSocketSession session, @NonNull WebSocketMessage<?> message) throws Exception {
        if (message instanceof TextMessage) {
            String payload = ((TextMessage) message).getPayload();
            System.out.println("收到WebSocket消息: " + payload);
            
            try {
                // 解析消息
                @SuppressWarnings("unchecked")
                Map<String, Object> messageData = objectMapper.readValue(payload, Map.class);
                String type = (String) messageData.get("type");
                
                if ("ping".equals(type)) {
                    // 心跳检测
                    Map<String, Object> pongMsg = Map.of("type", "pong");
                    session.sendMessage(new TextMessage(objectMapper.writeValueAsString(pongMsg)));
                } else if ("message".equals(type)) {
                    // 消息处理逻辑（如果需要通过WebSocket直接发送消息，可以在这里处理）
                    // 目前消息发送主要通过HTTP API，这里可以用于其他类型的实时通信
                    System.out.println("收到消息类型: " + type);
                }
            } catch (Exception e) {
                System.err.println("处理WebSocket消息失败: " + e.getMessage());
                Map<String, Object> errorMsg = Map.of(
                    "type", "error",
                    "message", "消息格式错误"
                );
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(errorMsg)));
            }
        }
    }
    
    @Override
    public void handleTransportError(@NonNull WebSocketSession session, @NonNull Throwable exception) throws Exception {
        String userId = sessionUsers.get(session);
        System.err.println("WebSocket传输错误: 用户ID=" + userId + ", 错误=" + exception.getMessage());
        removeSession(session);
    }
    
    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus closeStatus) throws Exception {
        String userId = sessionUsers.get(session);
        System.out.println("WebSocket连接关闭: 用户ID=" + userId + ", 会话ID=" + session.getId());
        removeSession(session);
    }
    
    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
    
    /**
     * 移除会话
     */
    private void removeSession(WebSocketSession session) {
        String userId = sessionUsers.remove(session);
        if (userId != null) {
            userSessions.remove(userId);
        }
    }
    
    /**
     * 向指定用户发送消息
     * 
     * @param userId 用户ID
     * @param message 消息内容（JSON字符串）
     */
    public void sendMessageToUser(String userId, String message) {
        WebSocketSession session = userSessions.get(userId);
        if (session != null && session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(message));
            } catch (IOException e) {
                System.err.println("发送消息失败: 用户ID=" + userId + ", 错误=" + e.getMessage());
                // 如果发送失败，移除会话
                removeSession(session);
            }
        }
    }
    
    /**
     * 检查用户是否在线
     * 
     * @param userId 用户ID
     * @return 是否在线
     */
    public boolean isUserOnline(String userId) {
        WebSocketSession session = userSessions.get(userId);
        return session != null && session.isOpen();
    }
    
    /**
     * 获取在线用户数量
     * 
     * @return 在线用户数量
     */
    public int getOnlineUserCount() {
        return (int) userSessions.values().stream()
                .filter(WebSocketSession::isOpen)
                .count();
    }
}

