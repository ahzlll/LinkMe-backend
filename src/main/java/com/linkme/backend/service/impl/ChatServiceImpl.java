package com.linkme.backend.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.linkme.backend.chat.websocket.ChatWebSocketHandler;
import com.linkme.backend.controller.dto.ConversationResponse;
import com.linkme.backend.controller.dto.MessageResponse;
import com.linkme.backend.entity.Conversation;
import com.linkme.backend.entity.Message;
import com.linkme.backend.entity.User;
import com.linkme.backend.entity.Block;
import com.linkme.backend.mapper.ConversationMapper;
import com.linkme.backend.mapper.MessageMapper;
import com.linkme.backend.mapper.UserMapper;
import com.linkme.backend.mapper.BlockMapper;
import com.linkme.backend.service.ChatService;
import com.linkme.backend.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 聊天服务实现类
 * 
 * 功能描述：
 * - 实现聊天相关的业务逻辑处理
 * - 包括会话管理、消息发送、未读消息管理等功能
 * 
 * @author Ahz
 * @version 1.2.2
 */
@Service
public class ChatServiceImpl implements ChatService {

    @Autowired
    private ConversationMapper conversationMapper;

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private BlockMapper blockMapper;

    @Autowired
    private ChatWebSocketHandler webSocketHandler;

    @Autowired
    private NotificationService notificationService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @Transactional
    public Conversation createOrGetConversation(Integer userId1, Integer userId2) {
        // 确保user1Id < user2Id
        Integer user1Id = userId1 < userId2 ? userId1 : userId2;
        Integer user2Id = userId1 < userId2 ? userId2 : userId1;

        // 查询是否已存在会话
        Conversation existing = conversationMapper.selectByUserPair(user1Id, user2Id);
        if (existing != null) {
            return existing;
        }

        // 创建新会话
        Conversation conversation = new Conversation();
        conversation.setUser1Id(user1Id);
        conversation.setUser2Id(user2Id);
        conversation.setCreatedAt(LocalDateTime.now());

        conversationMapper.insert(conversation);
        return conversation;
    }

    @Override
    public ConversationResponse getConversationById(Integer conversationId, Integer userId) {
        Conversation conversation = conversationMapper.selectById(conversationId);
        if (conversation == null) {
            return null;
        }

        // 验证用户是否有权限访问该会话
        if (!conversation.getUser1Id().equals(userId) && !conversation.getUser2Id().equals(userId)) {
            return null;
        }

        ConversationResponse response = new ConversationResponse();
        response.setConversationId(conversationId);

        // 确定对方用户
        Integer otherUserId = conversation.getUser1Id().equals(userId)
                ? conversation.getUser2Id()
                : conversation.getUser1Id();
        response.setOtherUserId(otherUserId);

        // 获取对方用户信息
        User otherUser = userMapper.selectById(otherUserId);
        if (otherUser != null) {
            response.setOtherUserNickname(otherUser.getNickname());
            response.setOtherUserAvatar(otherUser.getAvatarUrl());
        }

        // 获取最后一条消息
        Message lastMessage = messageMapper.selectLatestByConversationId(conversationId);
        if (lastMessage != null) {
            response.setLastMessage(lastMessage.getContent());
            response.setLastMessageType(lastMessage.getContentType());
            response.setLastMessageTime(lastMessage.getCreatedAt());
        }

        // 获取未读消息数量
        int unreadCount = messageMapper.countUnreadByConversationId(conversationId, userId);
        response.setUnreadCount(unreadCount);

        response.setCreatedAt(conversation.getCreatedAt());

        // 设置当前用户的免打扰和置顶状态
        boolean isUser1 = conversation.getUser1Id().equals(userId);
        response.setIsMuted(isUser1 ? conversation.getUser1Muted() : conversation.getUser2Muted());
        response.setIsPinned(isUser1 ? conversation.getUser1Pinned() : conversation.getUser2Pinned());

        return response;
    }

    @Override
    public List<ConversationResponse> getConversationsByUserId(Integer userId, Integer page, Integer size) {
        int offset = (page - 1) * size;
        List<Conversation> conversations = conversationMapper.selectByUserId(userId, offset, size);

        return conversations.stream().map(conversation -> {
            Integer otherUserId = conversation.getUser1Id().equals(userId)
                    ? conversation.getUser2Id()
                    : conversation.getUser1Id();

            ConversationResponse response = new ConversationResponse();
            response.setConversationId(conversation.getConversationId());
            response.setOtherUserId(otherUserId);

            // 获取对方用户信息
            User otherUser = userMapper.selectById(otherUserId);
            if (otherUser != null) {
                response.setOtherUserNickname(otherUser.getNickname());
                response.setOtherUserAvatar(otherUser.getAvatarUrl());
            }

            // 获取最后一条消息
            Message lastMessage = messageMapper.selectLatestByConversationId(conversation.getConversationId());
            if (lastMessage != null) {
                response.setLastMessage(lastMessage.getContent());
                response.setLastMessageType(lastMessage.getContentType());
                response.setLastMessageTime(lastMessage.getCreatedAt());
            }

            // 获取未读消息数量
            int unreadCount = messageMapper.countUnreadByConversationId(conversation.getConversationId(), userId);
            response.setUnreadCount(unreadCount);

            response.setCreatedAt(conversation.getCreatedAt());

            // 设置当前用户的免打扰和置顶状态
            boolean isUser1 = conversation.getUser1Id().equals(userId);
            response.setIsMuted(isUser1 ? conversation.getUser1Muted() : conversation.getUser2Muted());
            response.setIsPinned(isUser1 ? conversation.getUser1Pinned() : conversation.getUser2Pinned());

            return response;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public MessageResponse sendMessage(Integer senderId, Integer conversationId, Integer receiverId,
            String contentType, String content) {
        // 如果conversationId为null，需要创建或获取会话
        if (conversationId == null) {
            if (receiverId == null) {
                throw new IllegalArgumentException("接收者ID不能为空");
            }
            Conversation conversation = createOrGetConversation(senderId, receiverId);
            conversationId = conversation.getConversationId();
        } else {
            // 验证会话是否存在且用户有权限
            Conversation conversation = conversationMapper.selectById(conversationId);
            if (conversation == null) {
                throw new IllegalArgumentException("会话不存在");
            }
            if (!conversation.getUser1Id().equals(senderId) && !conversation.getUser2Id().equals(senderId)) {
                throw new IllegalArgumentException("无权限访问该会话");
            }
            // 确定接收者
            receiverId = conversation.getUser1Id().equals(senderId)
                    ? conversation.getUser2Id()
                    : conversation.getUser1Id();
        }

        // 检查接收者是否屏蔽了发送者
        Block block = blockMapper.selectByBlockerAndBlocked(receiverId, senderId);
        boolean isBlocked = block != null; // 如果存在屏蔽关系，则被屏蔽
        System.out.println("=== 发送消息 ===");
        System.out.println("发送者: " + senderId + ", 接收者: " + receiverId + ", 会话ID: " + conversationId);
        System.out.println("是否被屏蔽: " + isBlocked + " (block记录: " + (block != null ? "存在" : "不存在") + ")");

        // 创建消息
        Message message = new Message();
        message.setConversationId(conversationId);
        message.setSenderId(senderId);
        message.setContentType(contentType != null ? contentType : "text");
        message.setContent(content);
        message.setIsRead(false);
        message.setCreatedAt(LocalDateTime.now());

        // 如果接收者屏蔽了发送者，标记消息对接收者永久隐藏
        if (isBlocked) {
            message.setHiddenForUserId(receiverId);
            System.out.println("消息将被标记为对用户 " + receiverId + " 隐藏");
        } else {
            System.out.println("消息不设置隐藏标记");
        }

        messageMapper.insert(message);
        System.out.println(
                "消息已插入数据库，消息ID: " + message.getMessageId() + ", hiddenForUserId: " + message.getHiddenForUserId());

        // 构建响应
        MessageResponse response = new MessageResponse();
        response.setMessageId(message.getMessageId());
        response.setConversationId(conversationId);
        response.setSenderId(senderId);
        response.setContentType(message.getContentType());
        response.setContent(content);
        response.setIsRead(false);
        response.setCreatedAt(message.getCreatedAt());

        // 获取发送者信息
        User sender = userMapper.selectById(senderId);
        if (sender != null) {
            response.setSenderNickname(sender.getNickname());
            response.setSenderAvatar(sender.getAvatarUrl());
        }

        // 通过WebSocket推送消息给接收者（如果在线且未屏蔽发送者）
        if (!isBlocked) {
            // 未被屏蔽，推送实时消息和通知
            try {
                // 构建WebSocket消息对象，添加type字段便于前端识别
                java.util.Map<String, Object> wsMessage = new java.util.HashMap<>();
                wsMessage.put("type", "chat");
                wsMessage.put("messageId", response.getMessageId());
                wsMessage.put("conversationId", conversationId);
                wsMessage.put("senderId", senderId);
                wsMessage.put("senderNickname", response.getSenderNickname());
                wsMessage.put("senderAvatar", response.getSenderAvatar());
                wsMessage.put("contentType", response.getContentType());
                wsMessage.put("content", content);
                wsMessage.put("isRead", false);
                // 将LocalDateTime转换为ISO 8601字符串格式
                wsMessage.put("createdAt", response.getCreatedAt().toString());

                String messageJson = objectMapper.writeValueAsString(wsMessage);
                webSocketHandler.sendMessageToUser(receiverId.toString(), messageJson);
                System.out.println("WebSocket消息已推送给用户: " + receiverId);
            } catch (Exception e) {
                System.err.println("WebSocket推送消息失败: " + e.getMessage());
                e.printStackTrace();
            }

            // 创建通知
            notificationService.createMessageNotification(receiverId, senderId, message.getMessageId(),
                    message.getContentType(), message.getContent());
        } else {
            System.out.println("用户 " + receiverId + " 已屏蔽用户 " + senderId + "，不推送实时消息和通知");
        }

        return response;
    }

    @Override
    public List<MessageResponse> getMessagesByConversationId(Integer conversationId, Integer userId,
            Integer page, Integer size) {
        System.out.println("=== 获取消息列表 ===");
        System.out.println("会话ID: " + conversationId + ", 用户ID: " + userId + ", 页码: " + page + ", 每页: " + size);

        // 验证会话权限
        Conversation conversation = conversationMapper.selectById(conversationId);
        if (conversation == null) {
            System.out.println("会话不存在，返回空列表");
            return new ArrayList<>();
        }
        if (!conversation.getUser1Id().equals(userId) && !conversation.getUser2Id().equals(userId)) {
            System.out.println("用户无权限访问该会话，返回空列表");
            return new ArrayList<>();
        }

        int offset = (page - 1) * size;
        List<Message> messages = messageMapper.selectByConversationId(conversationId, offset, size);
        System.out.println("从数据库查询到 " + messages.size() + " 条消息");

        // 过滤对当前用户隐藏的消息（屏蔽期间发送的消息永久隐藏）
        return messages.stream()
                .filter(message -> {
                    // 如果消息被标记为对当前用户隐藏，则过滤掉
                    if (message.getHiddenForUserId() != null && message.getHiddenForUserId().equals(userId)) {
                        System.out.println("消息 " + message.getMessageId() + " 对用户 " + userId + " 隐藏，已过滤");
                        return false; // 该消息对当前用户永久隐藏
                    }
                    return true; // 正常显示
                })
                .map(message -> {
                    MessageResponse response = new MessageResponse();
                    response.setMessageId(message.getMessageId());
                    response.setConversationId(message.getConversationId());
                    response.setSenderId(message.getSenderId());
                    response.setContentType(message.getContentType());
                    response.setContent(message.getContent());
                    response.setIsRead(message.getIsRead());
                    response.setCreatedAt(message.getCreatedAt());

                    // 获取发送者信息
                    User sender = userMapper.selectById(message.getSenderId());
                    if (sender != null) {
                        response.setSenderNickname(sender.getNickname());
                        response.setSenderAvatar(sender.getAvatarUrl());
                    }

                    return response;
                }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public boolean markMessagesAsRead(Integer conversationId, Integer userId) {
        // 验证会话权限
        Conversation conversation = conversationMapper.selectById(conversationId);
        if (conversation == null) {
            return false;
        }
        if (!conversation.getUser1Id().equals(userId) && !conversation.getUser2Id().equals(userId)) {
            return false;
        }

        int affected = messageMapper.markAsRead(conversationId, userId);
        return affected > 0;
    }

    @Override
    public int getUnreadCount(Integer conversationId, Integer userId) {
        return messageMapper.countUnreadByConversationId(conversationId, userId);
    }

    @Override
    public int getTotalUnreadCount(Integer userId) {
        List<Conversation> conversations = conversationMapper.selectByUserId(userId, 0, Integer.MAX_VALUE);
        int totalUnread = 0;
        for (Conversation conversation : conversations) {
            totalUnread += messageMapper.countUnreadByConversationId(conversation.getConversationId(), userId);
        }
        return totalUnread;
    }

    @Override
    public boolean setMuteStatus(Integer conversationId, Integer userId, Boolean muted) {
        try {
            Conversation conversation = conversationMapper.selectById(conversationId);
            if (conversation == null) {
                return false;
            }

            // 验证用户是否有权限
            if (!conversation.getUser1Id().equals(userId) && !conversation.getUser2Id().equals(userId)) {
                return false;
            }

            // 确定是user1还是user2
            boolean isUser1 = conversation.getUser1Id().equals(userId);
            if (isUser1) {
                conversation.setUser1Muted(muted);
            } else {
                conversation.setUser2Muted(muted);
            }

            int result = conversationMapper.update(conversation);
            return result > 0;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean setPinStatus(Integer conversationId, Integer userId, Boolean pinned) {
        try {
            Conversation conversation = conversationMapper.selectById(conversationId);
            if (conversation == null) {
                return false;
            }

            // 验证用户是否有权限
            if (!conversation.getUser1Id().equals(userId) && !conversation.getUser2Id().equals(userId)) {
                return false;
            }

            // 确定是user1还是user2
            boolean isUser1 = conversation.getUser1Id().equals(userId);
            if (isUser1) {
                conversation.setUser1Pinned(pinned);
            } else {
                conversation.setUser2Pinned(pinned);
            }

            int result = conversationMapper.update(conversation);
            return result > 0;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    @Transactional
    public boolean clearMessages(Integer conversationId, Integer userId) {
        try {
            Conversation conversation = conversationMapper.selectById(conversationId);
            if (conversation == null) {
                return false;
            }

            // 验证用户是否有权限
            if (!conversation.getUser1Id().equals(userId) && !conversation.getUser2Id().equals(userId)) {
                return false;
            }

            // 删除该会话的所有消息
            int result = messageMapper.deleteByConversationId(conversationId);
            return result >= 0; // >= 0 表示操作成功（即使没有消息可删除也返回true）
        } catch (Exception e) {
            return false;
        }
    }
}
