package com.linkme.backend.controller.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminOperationLogResponse {
    private Long id;
    private Integer adminId;
    private String adminNickname;
    private Integer targetUserId;
    private String targetUserNickname;
    private Long targetId;
    private Integer targetType;
    private String targetTypeLabel;
    private String action;
    private String actionLabel;
    private String reason;
    private String detail;
    private LocalDateTime createTime;
    /** 例如：评论 #12 / 用户 ID:3 */
    private String targetSummary;
    /** 一行式中文说明，前端可直接展示 */
    private String summary;
}
