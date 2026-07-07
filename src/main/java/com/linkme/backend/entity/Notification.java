package com.linkme.backend.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 通知实体。
 */
@Data
public class Notification {

    private Integer notificationId;

    private Integer userId;

    private String type;

    private Integer actorId;

    private Integer relatedId;

    private String relatedType;

    private String title;

    private String content;

    private LocalDateTime createdAt;

    private Boolean isRead;
}
