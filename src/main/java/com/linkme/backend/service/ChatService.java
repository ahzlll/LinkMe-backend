package com.linkme.backend.service;

import com.linkme.backend.controller.dto.ConversationResponse;
import com.linkme.backend.controller.dto.MessageResponse;
import com.linkme.backend.entity.Conversation;

import java.util.List;

/**
 * 聊天服务接口
 * 
 * 功能描述：
 * - 提供聊天相关的业务逻辑处理
 * - 包括会话管理、消息发送、未读消息管理等功能
 * 
 * @author Ahz
 * @version 1.2
 */
public interface ChatService {
    
    /**
     * 创建或获取会话
     * 
     * @param userId1 用户1ID
     * @param userId2 用户2ID
     * @return 会话信息
     */
    Conversation createOrGetConversation(Integer userId1, Integer userId2);
    
    /**
     * 根据会话ID获取会话详情
     * 
     * @param conversationId 会话ID
     * @param userId 当前用户ID（用于确定对方用户）
     * @return 会话响应信息
     */
    ConversationResponse getConversationById(Integer conversationId, Integer userId);
    
    /**
     * 根据用户ID获取会话列表
     * 
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页数量
     * @return 会话列表
     */
    List<ConversationResponse> getConversationsByUserId(Integer userId, Integer page, Integer size);
    
    /**
     * 发送消息
     * 
     * @param senderId 发送者ID
     * @param conversationId 会话ID（可为null，如果提供receiverId则自动创建或获取会话）
     * @param receiverId 接收者ID
     * @param contentType 内容类型（text/image/video/voice/file）
     * @param content 消息内容
     * @return 消息响应信息
     */
    MessageResponse sendMessage(Integer senderId, Integer conversationId, Integer receiverId, 
                                String contentType, String content);
    
    /**
     * 根据会话ID获取消息列表
     * 
     * @param conversationId 会话ID
     * @param userId 当前用户ID（用于权限验证）
     * @param page 页码
     * @param size 每页数量
     * @return 消息列表
     */
    List<MessageResponse> getMessagesByConversationId(Integer conversationId, Integer userId, 
                                                      Integer page, Integer size);
    
    /**
     * 标记消息为已读
     * 
     * @param conversationId 会话ID
     * @param userId 用户ID（接收者）
     * @return 是否成功
     */
    boolean markMessagesAsRead(Integer conversationId, Integer userId);
    
    /**
     * 获取会话未读消息数量
     * 
     * @param conversationId 会话ID
     * @param userId 用户ID（接收者）
     * @return 未读消息数量
     */
    int getUnreadCount(Integer conversationId, Integer userId);
    
    /**
     * 获取用户总未读消息数量
     * 
     * @param userId 用户ID
     * @return 总未读消息数量
     */
    int getTotalUnreadCount(Integer userId);
}

