package com.linkme.backend.controller.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AuditLogResponse {
    private Long id;
    private Long userId;
    private String userNickname;
    private String contentType;
    private String contentTypeLabel;
    private Long contentId;
    private String content;
    private Integer isViolation;
    private String isViolationLabel;
    private String matchedWords;
    private String categories;
    private Integer auditResult;
    private String auditResultLabel;
    private Long auditorId;
    private String auditorNickname;
    private String auditRemark;
    private LocalDateTime createTime;
    private LocalDateTime auditTime;
    /** 例如：帖子 #12 */
    private String targetSummary;
    /** 一行式中文说明，前端可直接展示 */
    private String summary;
}
