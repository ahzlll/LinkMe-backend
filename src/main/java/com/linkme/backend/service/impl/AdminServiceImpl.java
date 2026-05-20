package com.linkme.backend.service.impl;

import com.linkme.backend.common.AccountStatusUtil;
import com.linkme.backend.controller.dto.ContentModerateRequest;
import com.linkme.backend.controller.dto.UserPunishRequest;
import com.linkme.backend.entity.AdminOperationLog;
import com.linkme.backend.entity.AuditLog;
import com.linkme.backend.entity.Comment;
import com.linkme.backend.entity.Post;
import com.linkme.backend.entity.User;
import com.linkme.backend.mapper.AdminOperationLogMapper;
import com.linkme.backend.mapper.AuditLogMapper;
import com.linkme.backend.mapper.CommentMapper;
import com.linkme.backend.mapper.PostMapper;
import com.linkme.backend.mapper.UserMapper;
import com.linkme.backend.service.AdminService;
import com.linkme.backend.service.PostService;
import com.linkme.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private AuditLogMapper auditLogMapper;
    @Autowired
    private AdminOperationLogMapper adminOperationLogMapper;

    @Override
    public boolean isAdmin(Integer userId) {
        User user = userService.getUserById(userId);
        return AccountStatusUtil.isAdminRole(user);
    }

    @Override
    public List<User> listUsers(int page, int size, String keyword, String role, String accountStatus) {
        int offset = (Math.max(page, 1) - 1) * size;
        return userMapper.selectForAdmin(offset, size, keyword, role, accountStatus);
    }

    @Override
    public User getUserDetail(Integer userId) {
        return sanitize(userService.getUserById(userId));
    }

    @Override
    public String punishUser(Integer adminId, Integer targetUserId, UserPunishRequest request) {
        if (adminId.equals(targetUserId)) {
            throw new IllegalArgumentException("不能处罚当前管理员账号");
        }
        String action = request.getAction() == null ? "" : request.getAction().trim().toLowerCase();
        String reason = request.getReason();
        LocalDateTime banUntil = null;
        String accountStatus;

        switch (action) {
            case "warn":
                accountStatus = "warned";
                break;
            case "restricted_post":
                accountStatus = "restricted_post";
                break;
            case "restricted_comment":
                accountStatus = "restricted_comment";
                break;
            case "temp_banned":
                accountStatus = "temp_banned";
                int days = request.getBanDays() == null || request.getBanDays() < 1 ? 7 : request.getBanDays();
                banUntil = LocalDateTime.now().plusDays(days);
                break;
            case "perm_banned":
                accountStatus = "perm_banned";
                break;
            default:
                throw new IllegalArgumentException("不支持的处罚类型");
        }

        int affected = userMapper.updateAccountStatus(targetUserId, accountStatus, banUntil, reason);
        if (affected <= 0) {
            throw new IllegalArgumentException("处罚失败，用户可能不存在");
        }
        safeLogAdminOp(adminId, targetUserId, null, 2, action.toUpperCase(), reason, null);
        return "处罚已生效";
    }

    @Override
    public String unbanUser(Integer adminId, Integer targetUserId, String reason) {
        int affected = userMapper.updateAccountStatus(targetUserId, "normal", null, reason);
        if (affected <= 0) {
            throw new IllegalArgumentException("解封失败，用户可能不存在");
        }
        safeLogAdminOp(adminId, targetUserId, null, 2, "UNBAN", reason, null);
        return "用户已解封";
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
            throw new IllegalArgumentException("帖子不存在");
        }
        return applyContentAction(adminId, postId, 0, post.getUserId(), request, true);
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
            throw new IllegalArgumentException("评论不存在");
        }
        return applyContentAction(adminId, commentId.longValue(), 1, comment.getUserId(), request, false);
    }

    @Override
    public boolean deleteUser(Integer adminId, Integer userId) {
        if (adminId.equals(userId)) {
            throw new IllegalArgumentException("不能删除当前管理员账号");
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
    public List<AuditLog> listAuditLogs(int page, int size) {
        int offset = (Math.max(page, 1) - 1) * size;
        return auditLogMapper.selectRecent(offset, size);
    }

    @Override
    public List<AdminOperationLog> listOperationLogs(int page, int size) {
        int offset = (Math.max(page, 1) - 1) * size;
        return adminOperationLogMapper.selectRecent(offset, size);
    }

    private String applyContentAction(Integer adminId, long targetId, int targetType, Integer authorId,
                                      ContentModerateRequest request, boolean isPost) {
        String action = request.getAction() == null ? "" : request.getAction().trim().toLowerCase();
        String reason = request.getReason();
        String auditAction;
        String moderationStatus;
        String adminAction;

        switch (action) {
            case "hide":
                moderationStatus = "hidden";
                auditAction = "BLOCK";
                adminAction = isPost ? "HIDE_POST" : "HIDE_COMMENT";
                break;
            case "delete":
                moderationStatus = "deleted";
                auditAction = "DELETE";
                adminAction = isPost ? "DELETE_POST" : "DELETE_COMMENT";
                break;
            case "approve":
                moderationStatus = "visible";
                auditAction = "PASS";
                adminAction = isPost ? "APPROVE_POST" : "APPROVE_COMMENT";
                break;
            case "reject":
                moderationStatus = "hidden";
                auditAction = "BLOCK";
                adminAction = isPost ? "REJECT_POST" : "REJECT_COMMENT";
                break;
            default:
                throw new IllegalArgumentException("不支持的内容操作");
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
            throw new IllegalArgumentException("操作失败，内容可能不存在");
        }

        AuditLog audit = new AuditLog();
        audit.setTargetId(targetId);
        audit.setTargetType(targetType);
        audit.setAuditorId(adminId.longValue());
        audit.setAction(auditAction);
        audit.setReason(reason);
        auditLogMapper.insert(audit);

        safeLogAdminOp(adminId, authorId, targetId, targetType, adminAction, reason, null);
        return "操作成功";
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
            System.err.println("管理员操作日志写入失败（处罚/审核已落库）: " + e.getMessage());
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
        safe.setAccountStatus(user.getAccountStatus());
        safe.setBanUntil(user.getBanUntil());
        safe.setStatusReason(user.getStatusReason());
        safe.setCreatedAt(user.getCreatedAt());
        return safe;
    }
}
