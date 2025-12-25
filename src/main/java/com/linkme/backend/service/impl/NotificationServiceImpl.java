package com.linkme.backend.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.linkme.backend.chat.websocket.ChatWebSocketHandler;
import com.linkme.backend.controller.dto.NotificationResponse;
import com.linkme.backend.entity.Notification;
import com.linkme.backend.entity.User;
import com.linkme.backend.mapper.NotificationMapper;
import com.linkme.backend.mapper.UserMapper;
import com.linkme.backend.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 通知服务实现类
 * 
 * 功能描述：
 * - 实现通知相关的业务逻辑处理
 * - 包括通知创建、查询、标记已读等功能
 * - 支持实时推送通知
 * 
 * @author Ahz
 * @version 1.2
 */
@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationMapper notificationMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ChatWebSocketHandler webSocketHandler;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @Transactional
    public Notification createMessageNotification(Integer userId, Integer actorId, Integer messageId,
            String contentType, String content) {
        // 获取发送者信息
        User actor = userMapper.selectById(actorId);
        String actorName = actor != null ? actor.getNickname() : "用户";

        // 根据内容类型生成标题和内容
        String title = actorName + " 发来了一条消息";
        String notificationContent = content;

        // 根据内容类型截取预览
        if ("image".equals(contentType)) {
            notificationContent = "[图片]";
        } else if ("video".equals(contentType)) {
            notificationContent = "[视频]";
        } else if ("voice".equals(contentType)) {
            notificationContent = "[语音]";
        } else if ("file".equals(contentType)) {
            notificationContent = "[文件]";
        } else if (content != null && content.length() > 50) {
            notificationContent = content.substring(0, 50) + "...";
        }

        return createNotification(userId, "message", actorId, messageId, "message", title, notificationContent);
    }

    @Override
    @Transactional
    public Notification createNotification(Integer userId, String type, Integer actorId,
            Integer relatedId, String relatedType, String title, String content) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setType(type);
        notification.setActorId(actorId);
        notification.setRelatedId(relatedId);
        notification.setRelatedType(relatedType);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setIsRead(false);
        notification.setCreatedAt(LocalDateTime.now());

        notificationMapper.insert(notification);

        // 通过WebSocket实时推送通知
        try {
            NotificationResponse response = convertToResponse(notification);

            // 手动构建推送消息，将LocalDateTime转换为字符串
            Map<String, Object> pushMessage = new HashMap<>();
            pushMessage.put("type", "notification");

            // 构建通知数据，避免LocalDateTime序列化问题
            Map<String, Object> notificationData = new HashMap<>();
            notificationData.put("notificationId", response.getNotificationId());
            notificationData.put("userId", response.getUserId());
            notificationData.put("type", response.getType());
            notificationData.put("actorId", response.getActorId());
            notificationData.put("actorNickname", response.getActorNickname());
            notificationData.put("actorAvatar", response.getActorAvatar());
            notificationData.put("relatedId", response.getRelatedId());
            notificationData.put("relatedType", response.getRelatedType());
            notificationData.put("title", response.getTitle());
            notificationData.put("content", response.getContent());
            notificationData.put("isRead", response.getIsRead());
            // 将LocalDateTime转换为ISO 8601字符串格式
            notificationData.put("createdAt", response.getCreatedAt().toString());

            pushMessage.put("data", notificationData);
            String pushJson = objectMapper.writeValueAsString(pushMessage);

            webSocketHandler.sendMessageToUser(userId.toString(), pushJson);
        } catch (Exception e) {
            System.err.println("WebSocket推送通知失败: " + e.getMessage());
        }

        return notification;
    }

    @Override
    public List<NotificationResponse> getNotificationsByUserId(Integer userId, Boolean isRead,
            Integer page, Integer size) {
        int offset = (page - 1) * size;
        List<Notification> notifications = notificationMapper.selectByUserId(userId, isRead, offset, size);

        return notifications.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public boolean markAsRead(Integer notificationId, Integer userId) {
        Notification notification = notificationMapper.selectById(notificationId);
        if (notification == null || !notification.getUserId().equals(userId)) {
            return false;
        }

        notification.setIsRead(true);
        return notificationMapper.update(notification) > 0;
    }

    @Override
    @Transactional
    public int markAllAsRead(Integer userId) {
        List<Notification> unreadNotifications = notificationMapper.selectByUserId(userId, false, 0, Integer.MAX_VALUE);
        int count = 0;
        for (Notification notification : unreadNotifications) {
            notification.setIsRead(true);
            if (notificationMapper.update(notification) > 0) {
                count++;
            }
        }
        return count;
    }

    @Override
    public int getUnreadCount(Integer userId) {
        return notificationMapper.countUnreadByUserId(userId);
    }

    @Override
    @Transactional
    public boolean deleteNotification(Integer notificationId, Integer userId) {
        Notification notification = notificationMapper.selectById(notificationId);
        if (notification == null || !notification.getUserId().equals(userId)) {
            return false;
        }

        return notificationMapper.deleteById(notificationId) > 0;
    }

    /**
     * 将Notification实体转换为NotificationResponse DTO
     */
    private NotificationResponse convertToResponse(Notification notification) {
        NotificationResponse response = new NotificationResponse();
        response.setNotificationId(notification.getNotificationId());
        response.setUserId(notification.getUserId());
        response.setType(notification.getType());
        response.setActorId(notification.getActorId());
        response.setRelatedId(notification.getRelatedId());
        response.setRelatedType(notification.getRelatedType());
        response.setTitle(notification.getTitle());
        response.setContent(notification.getContent());
        response.setIsRead(notification.getIsRead());
        response.setCreatedAt(notification.getCreatedAt());

        // 获取操作者信息
        if (notification.getActorId() != null) {
            User actor = userMapper.selectById(notification.getActorId());
            if (actor != null) {
                response.setActorNickname(actor.getNickname());
                response.setActorAvatar(actor.getAvatarUrl());
            }
        }

        return response;
    }
}
