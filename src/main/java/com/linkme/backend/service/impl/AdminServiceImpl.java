package com.linkme.backend.service.impl;

import com.linkme.backend.common.AccountStatusUtil;
import com.linkme.backend.common.AdminLogDisplayUtil;
import com.linkme.backend.controller.dto.AdminOperationLogResponse;
import com.linkme.backend.controller.dto.AuditLogResponse;
import com.linkme.backend.controller.dto.ContentModerateRequest;
import com.linkme.backend.controller.dto.UserPunishRequest;
import com.linkme.backend.entity.AdminOperationLog;
import com.linkme.backend.entity.AuditLog;
import com.linkme.backend.entity.Comment;
import com.linkme.backend.entity.Message;
import com.linkme.backend.entity.Post;
import com.linkme.backend.entity.User;
import com.linkme.backend.mapper.AdminOperationLogMapper;
import com.linkme.backend.mapper.AuditLogMapper;
import com.linkme.backend.mapper.CommentMapper;
import com.linkme.backend.mapper.MessageMapper;
import com.linkme.backend.mapper.PostMapper;
import com.linkme.backend.mapper.UserMapper;
import com.linkme.backend.service.AdminService;
import com.linkme.backend.service.NotificationService;
import com.linkme.backend.service.PostService;
import com.linkme.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private UserService userService;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PostMapper postMapper;
    @Autowired
    private PostService postService;
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private MessageMapper messageMapper;
    @Autowired
    private AuditLogMapper auditLogMapper;
    @Autowired
    private AdminOperationLogMapper adminOperationLogMapper;
    @Autowired
    private NotificationService notificationService;

    @Override
    public boolean isAdmin(Integer userId) {
        User user = userService.getUserById(userId);
        return AccountStatusUtil.isAdminRole(user);
    }

    @Override
    public List<User> listUsers(int page, int size, String keyword, String role, String accountStatus) {
        int offset = (Math.max(page, 1) - 1) * size;
        return userMapper.selectForAdmin(offset, size, keyword, role, accountStatus)
                .stream()
                .map(this::sanitize)
                .collect(Collectors.toList());
    }

    @Override
    public User getUserDetail(Integer userId) {
        return sanitize(userService.getUserById(userId));
    }

    @Override
    public String punishUser(Integer adminId, Integer targetUserId, UserPunishRequest request) {
        if (adminId.equals(targetUserId)) {
            throw new IllegalArgumentException("Cannot punish the current admin account");
        }

        String action = request.getAction() == null ? "" : request.getAction().trim().toLowerCase();
        String reason = request.getReason();
        LocalDateTime banUntil = null;
        String accountStatus;

        switch (action) {
            case "warn":
                accountStatus = "warned";
                banUntil = resolveBanUntil(action, request.getBanDays(), 7);
                break;
            case "restricted_post":
                accountStatus = "restricted_post";
                banUntil = resolveBanUntil(action, request.getBanDays(), 7);
                break;
            case "restricted_comment":
                accountStatus = "restricted_comment";
                banUntil = resolveBanUntil(action, request.getBanDays(), 7);
                break;
            case "temp_banned":
                accountStatus = "temp_banned";
                banUntil = resolveBanUntil(action, request.getBanDays(), 7);
                break;
            case "perm_banned":
                accountStatus = "perm_banned";
                break;
            default:
                throw new IllegalArgumentException("Unsupported punishment action");
        }

        int affected = userMapper.updateAccountStatus(targetUserId, accountStatus, banUntil, reason);
        if (affected <= 0) {
            throw new IllegalArgumentException("Punishment failed, user may not exist");
        }

        sendPunishmentNotification(adminId, targetUserId, accountStatus, reason, banUntil);
        safeLogAdminOp(adminId, targetUserId, null, 2, action.toUpperCase(), reason, null);
        return "Punishment applied";
    }

    @Override
    public String unbanUser(Integer adminId, Integer targetUserId, String reason) {
        int affected = userMapper.updateAccountStatus(targetUserId, "normal", null, reason);
        if (affected <= 0) {
            throw new IllegalArgumentException("Unban failed, user may not exist");
        }

        sendUnbanNotification(adminId, targetUserId, reason);
        safeLogAdminOp(adminId, targetUserId, null, 2, "UNBAN", reason, null);
        return "User unbanned";
    }

    @Override
    public List<Post> listPosts(int page, int size) {
        int offset = (Math.max(page, 1) - 1) * size;
        return postMapper.selectAllForAdmin(offset, size);
    }

    @Override
    public String moderatePost(Integer adminId, Integer postId, ContentModerateRequest request) {
        Post post = postMapper.selectByIdAny(postId);
        if (post == null) {
            throw new IllegalArgumentException("Post not found");
        }
        return applyContentAction(adminId, postId, 0, post.getUserId(), post.getContent(), request, true);
    }

    @Override
    public List<Comment> listComments(int page, int size) {
        int offset = (Math.max(page, 1) - 1) * size;
        return commentMapper.selectAllForAdmin(offset, size);
    }

    @Override
    public String moderateComment(Integer adminId, Integer commentId, ContentModerateRequest request) {
        Comment comment = commentMapper.selectById(commentId);
        if (comment == null) {
            throw new IllegalArgumentException("Comment not found");
        }
        return applyContentAction(adminId, commentId.longValue(), 1, comment.getUserId(), comment.getContent(), request, false);
    }

    @Override
    public boolean deleteMessage(Integer adminId, Integer messageId) {
        Message message = messageMapper.selectById(messageId);
        if (message == null) {
            throw new IllegalArgumentException("Message not found");
        }

        int affected = messageMapper.deleteById(messageId);
        if (affected > 0) {
            AuditLog audit = new AuditLog();
            audit.setUserId(message.getSenderId().longValue());
            audit.setContentType("message");
            audit.setContentId(messageId.longValue());
            audit.setContent(truncateAuditContent(message.getContent()));
            audit.setIsViolation(1);
            audit.setAuditResult(AuditLog.RESULT_OFFLINE);
            audit.setAuditorId(adminId.longValue());
            audit.setAuditRemark("Admin deleted private message");
            audit.setCreateTime(LocalDateTime.now());
            audit.setAuditTime(LocalDateTime.now());
            auditLogMapper.insert(audit);
            safeLogAdminOp(adminId, message.getSenderId(), messageId.longValue(), 3,
                    "DELETE_MESSAGE", "Admin deleted private message", null);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteUser(Integer adminId, Integer userId) {
        if (adminId.equals(userId)) {
            throw new IllegalArgumentException("Cannot delete the current admin account");
        }
        boolean ok = userService.deleteUser(userId);
        if (ok) {
            safeLogAdminOp(adminId, userId, null, 2, "DELETE_USER", null, null);
        }
        return ok;
    }

    @Override
    public Map<String, Integer> stats() {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("users", userService.getUserCount());
        stats.put("posts", postService.getPostCount());
        return stats;
    }

    @Override
    public List<AuditLogResponse> listAuditLogs(int page, int size) {
        int offset = (Math.max(page, 1) - 1) * size;
        return auditLogMapper.selectRecent(offset, size).stream()
                .map(AdminLogDisplayUtil::toAuditLogResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<AdminOperationLogResponse> listOperationLogs(int page, int size) {
        int offset = (Math.max(page, 1) - 1) * size;
        return adminOperationLogMapper.selectRecent(offset, size).stream()
                .map(AdminLogDisplayUtil::toOperationLogResponse)
                .collect(Collectors.toList());
    }

    private String applyContentAction(Integer adminId, long targetId, int targetType, Integer authorId,
                                      String content, ContentModerateRequest request, boolean isPost) {
        String action = request.getAction() == null ? "" : request.getAction().trim().toLowerCase();
        String reason = request.getReason();
        String moderationStatus;
        String adminAction;
        int auditResult;
        int isViolation;

        switch (action) {
            case "hide":
                moderationStatus = "hidden";
                adminAction = isPost ? "HIDE_POST" : "HIDE_COMMENT";
                auditResult = AuditLog.RESULT_OFFLINE;
                isViolation = 1;
                break;
            case "delete":
                moderationStatus = "deleted";
                adminAction = isPost ? "DELETE_POST" : "DELETE_COMMENT";
                auditResult = AuditLog.RESULT_OFFLINE;
                isViolation = 1;
                break;
            case "approve":
                moderationStatus = "visible";
                adminAction = isPost ? "APPROVE_POST" : "APPROVE_COMMENT";
                auditResult = AuditLog.RESULT_MANUAL_PASS;
                isViolation = 0;
                break;
            case "reject":
                moderationStatus = "hidden";
                adminAction = isPost ? "REJECT_POST" : "REJECT_COMMENT";
                auditResult = AuditLog.RESULT_MANUAL_REJECT;
                isViolation = 1;
                break;
            default:
                throw new IllegalArgumentException("Unsupported moderation action");
        }

        int affected;
        if (isPost) {
            if ("delete".equals(action)) {
                affected = postService.deletePost((int) targetId) ? 1 : 0;
                postMapper.updateModerationStatus((int) targetId, "deleted");
            } else {
                affected = postMapper.updateModerationStatus((int) targetId, moderationStatus);
            }
        } else {
            if ("delete".equals(action)) {
                affected = commentMapper.deleteById((int) targetId);
                commentMapper.updateModerationStatus((int) targetId, "deleted");
            } else {
                affected = commentMapper.updateModerationStatus((int) targetId, moderationStatus);
            }
        }

        if (affected <= 0 && !"delete".equals(action)) {
            throw new IllegalArgumentException("Moderation failed, content may not exist");
        }

        AuditLog audit = new AuditLog();
        audit.setUserId(authorId.longValue());
        audit.setContentType(isPost ? "post" : "comment");
        audit.setContentId(targetId);
        audit.setContent(truncateAuditContent(content));
        audit.setIsViolation(isViolation);
        audit.setAuditResult(auditResult);
        audit.setAuditorId(adminId.longValue());
        audit.setAuditRemark(reason);
        audit.setCreateTime(LocalDateTime.now());
        audit.setAuditTime(LocalDateTime.now());
        auditLogMapper.insert(audit);

        safeLogAdminOp(adminId, authorId, targetId, targetType, adminAction, reason, null);
        return "Operation succeeded";
    }

    private String truncateAuditContent(String content) {
        if (content == null) {
            return null;
        }
        return content.length() > 500 ? content.substring(0, 500) : content;
    }

    private LocalDateTime resolveBanUntil(String action, Integer daysInput, int defaultDays) {
        if ("perm_banned".equals(action)) {
            return null;
        }
        int days = daysInput == null || daysInput < 1 ? defaultDays : daysInput;
        return LocalDateTime.now().plusDays(days);
    }

    private void sendPunishmentNotification(Integer adminId, Integer targetUserId, String accountStatus,
                                            String reason, LocalDateTime banUntil) {
        try {
            String actionText = switch (accountStatus) {
                case "warned" -> "\u8b66\u544a";
                case "restricted_post" -> "\u9650\u5236\u53d1\u5e16";
                case "restricted_comment" -> "\u9650\u5236\u8bc4\u8bba";
                case "temp_banned" -> "\u4e34\u65f6\u5c01\u7981";
                case "perm_banned" -> "\u6c38\u4e45\u5c01\u7981";
                default -> "\u8d26\u53f7\u5904\u7406";
            };
            StringBuilder content = new StringBuilder("\u4f60\u7684\u8d26\u53f7\u5df2\u88ab\u7ba1\u7406\u5458\u6267\u884c\u201c")
                    .append(actionText)
                    .append("\u201d\u5904\u7406\u3002");
            if (reason != null && !reason.isBlank()) {
                content.append(" \u539f\u56e0\uff1a").append(reason).append("\u3002");
            }
            if (banUntil != null) {
                content.append(" \u622a\u6b62\u65f6\u95f4\uff1a").append(banUntil).append("\u3002");
            }
            content.append(" \u5982\u6709\u5f02\u8bae\uff0c\u8bf7\u8054\u7cfb\u7ba1\u7406\u5458\u7533\u8bc9\u3002");
            notificationService.createNotification(
                    targetUserId,
                    "system",
                    adminId,
                    targetUserId,
                    "account_status",
                    "\u8d26\u53f7\u5904\u7f5a\u901a\u77e5",
                    content.toString()
            );
        } catch (Exception e) {
            System.err.println("\u53d1\u9001\u5904\u7f5a\u901a\u77e5\u5931\u8d25: " + e.getMessage());
        }
    }
    private void sendUnbanNotification(Integer adminId, Integer targetUserId, String reason) {
        try {
            StringBuilder content = new StringBuilder("\u4f60\u7684\u8d26\u53f7\u9650\u5236\u5df2\u89e3\u9664\uff0c\u5f53\u524d\u5df2\u6062\u590d\u6b63\u5e38\u4f7f\u7528\u3002");
            if (reason != null && !reason.isBlank()) {
                content.append(" \u8bf4\u660e\uff1a").append(reason).append("\u3002");
            }
            notificationService.createNotification(
                    targetUserId,
                    "system",
                    adminId,
                    targetUserId,
                    "account_status",
                    "\u8d26\u53f7\u89e3\u5c01\u901a\u77e5",
                    content.toString()
            );
        } catch (Exception e) {
            System.err.println("\u53d1\u9001\u89e3\u5c01\u901a\u77e5\u5931\u8d25: " + e.getMessage());
        }
    }
    private void safeLogAdminOp(Integer adminId, Integer targetUserId, Long targetId, Integer targetType,
                                String action, String reason, String detail) {
        try {
            AdminOperationLog logEntry = new AdminOperationLog();
            logEntry.setAdminId(adminId);
            logEntry.setTargetUserId(targetUserId);
            logEntry.setTargetId(targetId);
            logEntry.setTargetType(targetType);
            logEntry.setAction(action);
            logEntry.setReason(reason);
            logEntry.setDetail(detail);
            adminOperationLogMapper.insert(logEntry);
        } catch (Exception e) {
            System.err.println("Failed to write admin operation log: " + e.getMessage());
        }
    }

    private User sanitize(User user) {
        if (user == null) {
            return null;
        }
        User safe = new User();
        safe.setUserId(user.getUserId());
        safe.setUsername(user.getUsername());
        safe.setEmail(user.getEmail());
        safe.setPhone(user.getPhone());
        safe.setNickname(user.getNickname());
        safe.setGender(user.getGender());
        safe.setBirthday(user.getBirthday());
        safe.setRegion(user.getRegion());
        safe.setAvatarUrl(user.getAvatarUrl());
        safe.setBio(user.getBio());
        safe.setRole(user.getRole());
        safe.setAccountStatus(AccountStatusUtil.getEffectiveStatus(user));
        safe.setBanUntil(AccountStatusUtil.getEffectiveBanUntil(user));
        safe.setStatusReason(user.getStatusReason());
        safe.setCreatedAt(user.getCreatedAt());
        return safe;
    }
}
