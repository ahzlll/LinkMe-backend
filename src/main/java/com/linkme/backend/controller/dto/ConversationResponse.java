package com.linkme.backend.controller.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 会话响应DTO
 * 
 * 功能描述：
 * - 用于返回会话信息给客户端
 * - 包含会话的完整信息，包括对方用户信息和最后一条消息
 * 
 * @author Ahz
 * @version 1.2
 */
@Data
public class ConversationResponse {
    /**
     * 会话ID
     */
    private Integer conversationId;
    
    /**
     * 对方用户ID
     */
    private Integer otherUserId;
    
    /**
     * 对方用户昵称
     */
    private String otherUserNickname;
    
    /**
     * 对方用户头像
     */
    private String otherUserAvatar;
    
    /**
     * 最后一条消息内容
     */
    private String lastMessage;
    
    /**
     * 最后一条消息类型
     */
    private String lastMessageType;
    
    /**
     * 最后一条消息时间
     */
    private LocalDateTime lastMessageTime;
    
    /**
     * 未读消息数量
     */
    private Integer unreadCount;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 当前用户是否对此会话设置了免打扰
     */
    private Boolean isMuted;
    
    /**
     * 当前用户是否对此会话设置了置顶
     */
    private Boolean isPinned;
}

