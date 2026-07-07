package com.linkme.backend.controller.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 通知响应 DTO。
 */
@Data
public class NotificationResponse {

    private Integer notificationId;

    private Integer userId;

    private String type;

    private Integer actorId;

    private String actorNickname;

    private String actorAvatar;

    private Integer relatedId;

    private String relatedType;

    private String title;

    private String content;

    private Boolean isRead;

    private LocalDateTime createdAt;
}
