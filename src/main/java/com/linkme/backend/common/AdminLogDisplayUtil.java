package com.linkme.backend.common;

import com.linkme.backend.controller.dto.AdminOperationLogResponse;
import com.linkme.backend.controller.dto.AuditLogResponse;
import com.linkme.backend.entity.AdminOperationLog;
import com.linkme.backend.entity.AuditLog;

/**
 * 管理端审核日志、操作日志的中文展示文案。
 */
public final class AdminLogDisplayUtil {

    private AdminLogDisplayUtil() {
    }

    public static AuditLogResponse toAuditLogResponse(AuditLog log) {
        if (log == null) {
            return null;
        }
        AuditLogResponse response = new AuditLogResponse();
        response.setId(log.getId());
        response.setUserId(log.getUserId());
        response.setUserNickname(log.getUserNickname());
        response.setContentType(log.getContentType());
        response.setContentTypeLabel(contentTypeLabel(log.getContentType()));
        response.setContentId(log.getContentId());
        response.setContent(log.getContent());
        response.setIsViolation(log.getIsViolation());
        response.setIsViolationLabel(violationLabel(log.getIsViolation()));
        response.setMatchedWords(log.getMatchedWords());
        response.setCategories(log.getCategories());
        response.setAuditResult(log.getAuditResult());
        response.setAuditResultLabel(auditResultLabel(log.getAuditResult()));
        response.setAuditorId(log.getAuditorId());
        response.setAuditorNickname(log.getAuditorNickname());
        response.setAuditRemark(log.getAuditRemark());
        response.setCreateTime(log.getCreateTime());
        response.setAuditTime(log.getAuditTime());
        response.setTargetSummary(buildContentTargetSummary(log.getContentType(), log.getContentId()));
        response.setSummary(buildAuditSummary(response));
        return response;
    }

    public static AdminOperationLogResponse toOperationLogResponse(AdminOperationLog log) {
        if (log == null) {
            return null;
        }
        AdminOperationLogResponse response = new AdminOperationLogResponse();
        response.setId(log.getId());
        response.setAdminId(log.getAdminId());
        response.setAdminNickname(log.getAdminNickname());
        response.setTargetUserId(log.getTargetUserId());
        response.setTargetUserNickname(log.getTargetUserNickname());
        response.setTargetId(log.getTargetId());
        response.setTargetType(log.getTargetType());
        response.setTargetTypeLabel(targetTypeLabel(log.getTargetType()));
        response.setAction(log.getAction());
        response.setActionLabel(actionLabel(log.getAction()));
        response.setReason(log.getReason());
        response.setDetail(log.getDetail());
        response.setCreateTime(log.getCreateTime());
        response.setTargetSummary(buildOperationTargetSummary(log));
        response.setSummary(buildOperationSummary(response));
        return response;
    }

    public static String contentTypeLabel(String contentType) {
        if (contentType == null || contentType.isBlank()) {
            return "未知内容";
        }
        return switch (contentType.trim().toLowerCase()) {
            case "post" -> "帖子";
            case "comment" -> "评论";
            case "message" -> "消息";
            default -> contentType;
        };
    }

    public static String auditResultLabel(Integer auditResult) {
        if (auditResult == null) {
            return "未知结果";
        }
        return switch (auditResult) {
            case AuditLog.RESULT_AUTO_PASS -> "自动通过";
            case AuditLog.RESULT_NEED_MANUAL -> "送人工审核";
            case AuditLog.RESULT_MANUAL_PASS -> "人工通过";
            case AuditLog.RESULT_MANUAL_REJECT -> "人工驳回";
            case AuditLog.RESULT_OFFLINE -> "下架/隐藏";
            default -> "未知结果";
        };
    }

    public static String violationLabel(Integer isViolation) {
        if (isViolation == null) {
            return "未知";
        }
        return isViolation == 1 ? "违规" : "正常";
    }

    public static String targetTypeLabel(Integer targetType) {
        if (targetType == null) {
            return "未知";
        }
        return switch (targetType) {
            case 0 -> "帖子";
            case 1 -> "评论";
            case 2 -> "用户";
            default -> "未知";
        };
    }

    public static String actionLabel(String action) {
        if (action == null || action.isBlank()) {
            return "未知操作";
        }
        return switch (action.trim().toUpperCase()) {
            case "WARN" -> "警告用户";
            case "RESTRICTED_POST" -> "限制发帖";
            case "RESTRICTED_COMMENT" -> "限制评论";
            case "TEMP_BANNED" -> "临时封禁";
            case "PERM_BANNED" -> "永久封禁";
            case "UNBAN" -> "解除封禁";
            case "HIDE_POST" -> "隐藏帖子";
            case "DELETE_POST" -> "删除帖子";
            case "APPROVE_POST" -> "通过帖子";
            case "REJECT_POST" -> "驳回帖子";
            case "HIDE_COMMENT" -> "隐藏评论";
            case "DELETE_COMMENT" -> "删除评论";
            case "APPROVE_COMMENT" -> "通过评论";
            case "REJECT_COMMENT" -> "驳回评论";
            case "DELETE_USER" -> "删除用户";
            default -> action;
        };
    }

    private static String buildContentTargetSummary(String contentType, Long contentId) {
        String typeLabel = contentTypeLabel(contentType);
        if (contentId == null) {
            return typeLabel;
        }
        return typeLabel + " #" + contentId;
    }

    private static String buildOperationTargetSummary(AdminOperationLog log) {
        if (log.getTargetType() != null && log.getTargetType() != 2 && log.getTargetId() != null) {
            return targetTypeLabel(log.getTargetType()) + " #" + log.getTargetId();
        }
        if (log.getTargetUserId() != null) {
            return formatUser(log.getTargetUserId(), log.getTargetUserNickname());
        }
        if (log.getTargetId() != null) {
            return "资源 #" + log.getTargetId();
        }
        return "未知目标";
    }

    private static String buildAuditSummary(AuditLogResponse response) {
        StringBuilder summary = new StringBuilder();
        summary.append(response.getAuditResultLabel());
        summary.append(" · ").append(response.getTargetSummary());
        summary.append(" · 发布者：").append(formatUser(response.getUserId(), response.getUserNickname()));
        if (response.getAuditorId() != null && response.getAuditorId() > 0) {
            summary.append(" · 审核员：").append(formatUser(response.getAuditorId(), response.getAuditorNickname()));
        } else {
            summary.append(" · 审核员：系统自动");
        }
        if (response.getAuditRemark() != null && !response.getAuditRemark().isBlank()) {
            summary.append(" · 备注：").append(response.getAuditRemark());
        }
        return summary.toString();
    }

    private static String buildOperationSummary(AdminOperationLogResponse response) {
        StringBuilder summary = new StringBuilder();
        summary.append(response.getActionLabel());
        summary.append(" · ").append(response.getTargetSummary());
        if (response.getTargetUserId() != null
                && response.getTargetType() != null
                && response.getTargetType() != 2) {
            summary.append(" · 作者：").append(formatUser(response.getTargetUserId(), response.getTargetUserNickname()));
        } else if (response.getTargetUserId() != null) {
            summary.append(" · ").append(formatUser(response.getTargetUserId(), response.getTargetUserNickname()));
        }
        if (response.getReason() != null && !response.getReason().isBlank()) {
            summary.append(" · 原因：").append(response.getReason());
        }
        return summary.toString();
    }

    private static String formatUser(Number userId, String nickname) {
        if (nickname != null && !nickname.isBlank()) {
            return nickname + "（ID:" + userId + "）";
        }
        if (userId != null) {
            return "用户 ID:" + userId;
        }
        return "未知用户";
    }
}
