package com.linkme.backend.controller.dto;

import lombok.Data;

/**
 * 消息请求DTO
 * 
 * 功能描述：
 * - 用于接收客户端发送的消息请求
 * - 包含消息内容、类型等信息
 * 
 * @author Ahz
 * @version 1.2
 */
@Data
public class MessageRequest {
    /**
     * 会话ID
     */
    private Integer conversationId;
    
    /**
     * 接收者ID（可选，如果提供则用于创建新会话）
     */
    private Integer receiverId;
    
    /**
     * 内容类型 - text/image/video/file
     */
    private String contentType;
    
    /**
     * 消息内容
     */
    private String content;
}

