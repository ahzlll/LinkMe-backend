package com.linkme.backend.controller.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 消息响应DTO
 * 
 * 功能描述：
 * - 用于返回消息信息给客户端
 * - 包含消息的完整信息
 * 
 * @author Ahz
 * @version 1.2
 */
@Data
public class MessageResponse {
    /**
     * 消息ID
     */
    private Integer messageId;
    
    /**
     * 会话ID
     */
    private Integer conversationId;
    
    /**
     * 发送者ID
     */
    private Integer senderId;
    
    /**
     * 发送者昵称
     */
    private String senderNickname;
    
    /**
     * 发送者头像
     */
    private String senderAvatar;
    
    /**
     * 内容类型
     */
    private String contentType;
    
    /**
     * 消息内容
     */
    private String content;
    
    /**
     * 是否已读
     */
    private Boolean isRead;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}

