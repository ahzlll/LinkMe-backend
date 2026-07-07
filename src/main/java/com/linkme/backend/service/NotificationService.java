package com.linkme.backend.service;

import com.linkme.backend.controller.dto.NotificationResponse;
import com.linkme.backend.entity.Notification;

import java.util.List;

/**
 * 通知服务接口。
 */
public interface NotificationService {

    /**
     * 创建消息通知。
     */
    Notification createMessageNotification(Integer userId, Integer actorId, Integer messageId,
                                           String contentType, String content);

    /**
     * 创建通用通知。
     */
    Notification createNotification(Integer userId, String type, Integer actorId,
                                    Integer relatedId, String relatedType, String title, String content);

    /**
     * 按用户查ѯͨ知列表。
     */
    List<NotificationResponse> getNotificationsByUserId(Integer userId, Boolean isRead,
                                                        Integer page, Integer size);

    /**
     * 标记单条通知为已读。
     */
    boolean markAsRead(Integer notificationId, Integer userId);

    /**
     * 标记用户全部通知为已读。
     */
    int markAllAsRead(Integer userId);

    /**
     * 获取未读通知数量。
     */
    int getUnreadCount(Integer userId);

    /**
     * 删除通知。
     */
    boolean deleteNotification(Integer notificationId, Integer userId);
}
