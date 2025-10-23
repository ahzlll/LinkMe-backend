package com.linkme.backend.chat.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * WebSocket聊天处理器
 * 
 * 功能描述：
 * - 处理WebSocket连接和消息传输
 * - 支持实时聊天功能
 * - 管理用户连接状态
 * 
 * @author Ahz
 * @version 1.0
 */
@Component
public class ChatWebSocketHandler implements WebSocketHandler {
    
    // 存储所有WebSocket连接
    private static final CopyOnWriteArraySet<WebSocketSession> sessions = new CopyOnWriteArraySet<>();
    
    // 存储用户ID和WebSocket会话的映射
    private static final ConcurrentHashMap<String, WebSocketSession> userSessions = new ConcurrentHashMap<>();
    
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        System.out.println("WebSocket连接建立: " + session.getId());
    }
    
    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        if (message instanceof TextMessage) {
            String payload = ((TextMessage) message).getPayload();
            System.out.println("收到消息: " + payload);
            
            // 这里可以添加消息处理逻辑
            // 比如解析消息类型、转发给其他用户等
            
            // 简单回显
            session.sendMessage(new TextMessage("服务器收到: " + payload));
        }
    }
    
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        System.err.println("WebSocket传输错误: " + exception.getMessage());
        sessions.remove(session);
        userSessions.values().removeIf(s -> s.equals(session));
    }
    
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        sessions.remove(session);
        userSessions.values().removeIf(s -> s.equals(session));
        System.out.println("WebSocket连接关闭: " + session.getId());
    }
    
    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
    
    /**
     * 向指定用户发送消息
     * 
     * @param userId 用户ID
     * @param message 消息内容
     */
    public void sendMessageToUser(String userId, String message) {
        WebSocketSession session = userSessions.get(userId);
        if (session != null && session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(message));
            } catch (IOException e) {
                System.err.println("发送消息失败: " + e.getMessage());
            }
        }
    }
    
    /**
     * 广播消息给所有连接的客户端
     * 
     * @param message 消息内容
     */
    public void broadcastMessage(String message) {
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(message));
                } catch (IOException e) {
                    System.err.println("广播消息失败: " + e.getMessage());
                }
            }
        }
    }
}
