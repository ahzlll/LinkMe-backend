package com.linkme.backend.service;

import com.linkme.backend.controller.dto.NotificationResponse;
import com.linkme.backend.entity.Notification;

import java.util.List;

/**
 * 通知服务接口
 * 
 * 功能描述：
 * - 提供通知相关的业务逻辑处理
 * - 包括通知创建、查询、标记已读等功能
 * - 支持多种通知类型（消息、关注、点赞、评论等）
 * 
 * @author Ahz
 * @version 1.2.1
 */
public interface NotificationService {
    
    /**
     * 创建消息通知
     * 
     * @param userId 接收者ID
     * @param actorId 发送者ID
     * @param messageId 消息ID
     * @param contentType 消息内容类型（text/image/video/voice/file）
     * @param content 消息内容
     * @return 通知信息
     */
    Notification createMessageNotification(Integer userId, Integer actorId, Integer messageId, 
                                          String contentType, String content);
    
    /**
     * 创建通知
     * 
     * @param userId 接收者ID
     * @param type 通知类型（message/follow/heart/like/comment/match）
     * @param actorId 操作者ID
     * @param relatedId 关联实体ID
     * @param relatedType 关联实体类型
     * @param title 通知标题
     * @param content 通知内容
     * @return 通知信息
     */
    Notification createNotification(Integer userId, String type, Integer actorId, 
                                  Integer relatedId, String relatedType, String title, String content);
    
    /**
     * 根据用户ID获取通知列表
     * 
     * @param userId 用户ID
     * @param isRead 是否已读（可选，null表示全部）
     * @param page 页码
     * @param size 每页数量
     * @return 通知列表
     */
    List<NotificationResponse> getNotificationsByUserId(Integer userId, Boolean isRead, 
                                                       Integer page, Integer size);
    
    /**
     * 标记通知为已读
     * 
     * @param notificationId 通知ID
     * @param userId 用户ID（用于权限验证）
     * @return 是否成功
     */
    boolean markAsRead(Integer notificationId, Integer userId);
    
    /**
     * 标记所有通知为已读
     * 
     * @param userId 用户ID
     * @return 成功标记的数量
     */
    int markAllAsRead(Integer userId);
    
    /**
     * 获取用户未读通知数量
     * 
     * @param userId 用户ID
     * @return 未读通知数量
     */
    int getUnreadCount(Integer userId);
    
    /**
     * 删除通知
     * 
     * @param notificationId 通知ID
     * @param userId 用户ID（用于权限验证）
     * @return 是否成功
     */
    boolean deleteNotification(Integer notificationId, Integer userId);
}

