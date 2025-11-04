package com.linkme.backend.controller.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 通知响应DTO
 * 
 * 功能描述：
 * - 用于返回通知信息给客户端
 * - 包含通知的完整信息，包括操作者信息
 * 
 * @author Ahz
 * @version 1.2.1
 */
@Data
public class NotificationResponse {
    /**
     * 通知ID
     */
    private Integer notificationId;
    
    /**
     * 用户ID（接收者）
     */
    private Integer userId;
    
    /**
     * 通知类型（message/follow/heart/like/comment/match）
     */
    private String type;
    
    /**
     * 操作者ID
     */
    private Integer actorId;
    
    /**
     * 操作者昵称
     */
    private String actorNickname;
    
    /**
     * 操作者头像
     */
    private String actorAvatar;
    
    /**
     * 关联ID
     */
    private Integer relatedId;
    
    /**
     * 关联类型
     */
    private String relatedType;
    
    /**
     * 通知标题
     */
    private String title;
    
    /**
     * 通知内容
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

