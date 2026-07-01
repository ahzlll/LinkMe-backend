package com.linkme.backend.controller;

import com.linkme.backend.common.JwtUtil;
import com.linkme.backend.common.R;
import com.linkme.backend.controller.dto.AdminOperationLogResponse;
import com.linkme.backend.controller.dto.AuditLogResponse;
import com.linkme.backend.controller.dto.ContentModerateRequest;
import com.linkme.backend.controller.dto.UserPunishRequest;
import com.linkme.backend.entity.Comment;
import com.linkme.backend.entity.Post;
import com.linkme.backend.entity.User;
import com.linkme.backend.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@Tag(name = "管理端", description = "用户、内容审核、处罚与日志")
public class AdminController {

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private AdminService adminService;

    private Integer currentUserId(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            try {
                return jwtUtil.getUserIdFromToken(token);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    private R<?> forbid(HttpServletRequest request) {
        Integer uid = currentUserId(request);
        if (uid == null) {
            return R.fail(401, "未登录");
        }
        if (!adminService.isAdmin(uid)) {
            return R.fail(403, "没有管理端访问权限");
        }
        return null;
    }

    @GetMapping("/users")
    @Operation(summary = "用户列表", security = @SecurityRequirement(name = "bearerAuth"))
    public R<List<User>> listUsers(@RequestParam(defaultValue = "1") Integer page,
                                   @RequestParam(defaultValue = "20") Integer size,
                                   @RequestParam(required = false) String keyword,
                                   @RequestParam(required = false) String role,
                                   @RequestParam(required = false) String accountStatus,
                                   HttpServletRequest request) {
        R<?> check = forbid(request);
        if (check != null) return (R<List<User>>) check;
        return R.ok(adminService.listUsers(page, Math.min(size, 100), keyword, role, accountStatus));
    }

    @GetMapping("/users/{userId}")
    @Operation(summary = "用户详情", security = @SecurityRequirement(name = "bearerAuth"))
    public R<User> userDetail(@PathVariable Integer userId, HttpServletRequest request) {
        R<?> check = forbid(request);
        if (check != null) return (R<User>) check;
        User user = adminService.getUserDetail(userId);
        return user == null ? R.fail(404, "用户不存在") : R.ok(user);
    }

    @PostMapping("/users/{userId}/punish")
    @Operation(summary = "用户处罚", security = @SecurityRequirement(name = "bearerAuth"))
    public R<String> punish(@PathVariable Integer userId,
                            @RequestBody UserPunishRequest body,
                            HttpServletRequest request) {
        R<?> check = forbid(request);
        if (check != null) return (R<String>) check;
        try {
            return R.ok(adminService.punishUser(currentUserId(request), userId, body));
        } catch (IllegalArgumentException e) {
            return R.fail(400, e.getMessage());
        }
    }

    @PostMapping("/users/{userId}/unban")
    @Operation(summary = "解除封禁/恢复正常", security = @SecurityRequirement(name = "bearerAuth"))
    public R<String> unban(@PathVariable Integer userId,
                           @RequestParam(required = false) String reason,
                           HttpServletRequest request) {
        R<?> check = forbid(request);
        if (check != null) return (R<String>) check;
        try {
            return R.ok(adminService.unbanUser(currentUserId(request), userId, reason));
        } catch (IllegalArgumentException e) {
            return R.fail(400, e.getMessage());
        }
    }

    @DeleteMapping("/users/{userId}")
    @Operation(summary = "删除用户", security = @SecurityRequirement(name = "bearerAuth"))
    public R<String> deleteUser(@PathVariable Integer userId, HttpServletRequest request) {
        R<?> check = forbid(request);
        if (check != null) return (R<String>) check;
        try {
            return adminService.deleteUser(currentUserId(request), userId)
                    ? R.ok("用户已删除") : R.fail(400, "删除失败");
        } catch (IllegalArgumentException e) {
            return R.fail(400, e.getMessage());
        }
    }

    @GetMapping("/posts")
    @Operation(summary = "帖子列表（含隐藏）", security = @SecurityRequirement(name = "bearerAuth"))
    public R<List<Post>> listPosts(@RequestParam(defaultValue = "1") Integer page,
                                   @RequestParam(defaultValue = "20") Integer size,
                                   HttpServletRequest request) {
        R<?> check = forbid(request);
        if (check != null) return (R<List<Post>>) check;
        return R.ok(adminService.listPosts(page, Math.min(size, 100)));
    }

    @PostMapping("/posts/{postId}/moderate")
    @Operation(summary = "帖子审核/隐藏/删除", security = @SecurityRequirement(name = "bearerAuth"))
    public R<String> moderatePost(@PathVariable Integer postId,
                                  @RequestBody ContentModerateRequest body,
                                  HttpServletRequest request) {
        R<?> check = forbid(request);
        if (check != null) return (R<String>) check;
        try {
            return R.ok(adminService.moderatePost(currentUserId(request), postId, body));
        } catch (IllegalArgumentException e) {
            return R.fail(400, e.getMessage());
        }
    }

    @DeleteMapping("/posts/{postId}")
    @Operation(summary = "删除帖子", security = @SecurityRequirement(name = "bearerAuth"))
    public R<String> deletePost(@PathVariable Integer postId, HttpServletRequest request) {
        ContentModerateRequest body = new ContentModerateRequest();
        body.setAction("delete");
        return moderatePost(postId, body, request);
    }

    @GetMapping("/comments")
    @Operation(summary = "评论列表", security = @SecurityRequirement(name = "bearerAuth"))
    public R<List<Comment>> listComments(@RequestParam(defaultValue = "1") Integer page,
                                         @RequestParam(defaultValue = "20") Integer size,
                                         HttpServletRequest request) {
        R<?> check = forbid(request);
        if (check != null) return (R<List<Comment>>) check;
        return R.ok(adminService.listComments(page, Math.min(size, 100)));
    }

    @PostMapping("/comments/{commentId}/moderate")
    @Operation(summary = "评论审核/隐藏/删除", security = @SecurityRequirement(name = "bearerAuth"))
    public R<String> moderateComment(@PathVariable Integer commentId,
                                     @RequestBody ContentModerateRequest body,
                                     HttpServletRequest request) {
        R<?> check = forbid(request);
        if (check != null) return (R<String>) check;
        try {
            return R.ok(adminService.moderateComment(currentUserId(request), commentId, body));
        } catch (IllegalArgumentException e) {
            return R.fail(400, e.getMessage());
        }
    }

    @DeleteMapping("/comments/{commentId}")
    @Operation(summary = "删除评论", security = @SecurityRequirement(name = "bearerAuth"))
    public R<String> deleteComment(@PathVariable Integer commentId, HttpServletRequest request) {
        ContentModerateRequest body = new ContentModerateRequest();
        body.setAction("delete");
        return moderateComment(commentId, body, request);
    }

    @GetMapping("/stats")
    @Operation(summary = "统计", security = @SecurityRequirement(name = "bearerAuth"))
    public R<Map<String, Integer>> stats(HttpServletRequest request) {
        R<?> check = forbid(request);
        if (check != null) return (R<Map<String, Integer>>) check;
        return R.ok(adminService.stats());
    }

    @GetMapping("/audit-logs")
    @Operation(summary = "审核日志", security = @SecurityRequirement(name = "bearerAuth"))
    public R<List<AuditLogResponse>> auditLogs(@RequestParam(defaultValue = "1") Integer page,
                                       @RequestParam(defaultValue = "20") Integer size,
                                       HttpServletRequest request) {
        R<?> check = forbid(request);
        if (check != null) return (R<List<AuditLogResponse>>) check;
        return R.ok(adminService.listAuditLogs(page, Math.min(size, 100)));
    }

    @GetMapping("/operation-logs")
    @Operation(summary = "管理员操作日志", security = @SecurityRequirement(name = "bearerAuth"))
    public R<List<AdminOperationLogResponse>> operationLogs(@RequestParam(defaultValue = "1") Integer page,
                                                    @RequestParam(defaultValue = "20") Integer size,
                                                    HttpServletRequest request) {
        R<?> check = forbid(request);
        if (check != null) return (R<List<AdminOperationLogResponse>>) check;
        return R.ok(adminService.listOperationLogs(page, Math.min(size, 100)));
    }

    /** 兼容旧接口：永久封禁 */
    @PostMapping("/users/{userId}/ban")
    public R<String> banLegacy(@PathVariable Integer userId, HttpServletRequest request) {
        UserPunishRequest body = new UserPunishRequest();
        body.setAction("perm_banned");
        body.setReason("管理员封禁");
        return punish(userId, body, request);
    }
}
