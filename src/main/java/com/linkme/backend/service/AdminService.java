package com.linkme.backend.service;

import com.linkme.backend.controller.dto.AdminOperationLogResponse;
import com.linkme.backend.controller.dto.AuditLogResponse;
import com.linkme.backend.controller.dto.ContentModerateRequest;
import com.linkme.backend.controller.dto.UserPunishRequest;
import com.linkme.backend.entity.Comment;
import com.linkme.backend.entity.Message;
import com.linkme.backend.entity.Post;
import com.linkme.backend.entity.User;

import java.util.List;
import java.util.Map;

public interface AdminService {
    boolean isAdmin(Integer userId);

    List<User> listUsers(int page, int size, String keyword, String role, String accountStatus);

    User getUserDetail(Integer userId);

    String punishUser(Integer adminId, Integer targetUserId, UserPunishRequest request);

    String unbanUser(Integer adminId, Integer targetUserId, String reason);

    List<Post> listPosts(int page, int size);

    String moderatePost(Integer adminId, Integer postId, ContentModerateRequest request);

    List<Comment> listComments(int page, int size);

    String moderateComment(Integer adminId, Integer commentId, ContentModerateRequest request);

    boolean deleteMessage(Integer adminId, Integer messageId);

    boolean deleteUser(Integer adminId, Integer userId);

    Map<String, Integer> stats();

    List<AuditLogResponse> listAuditLogs(int page, int size);

    List<AdminOperationLogResponse> listOperationLogs(int page, int size);
}
