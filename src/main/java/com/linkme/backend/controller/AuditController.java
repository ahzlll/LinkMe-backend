package com.linkme.backend.controller;

import com.linkme.backend.common.JwtUtil;
import com.linkme.backend.common.R;
import com.linkme.backend.controller.dto.ContentModerateRequest;
import com.linkme.backend.controller.dto.UserPunishRequest;
import com.linkme.backend.entity.ManualReviewQueue;
import com.linkme.backend.service.AdminService;
import com.linkme.backend.service.AuditService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/audit")
@Tag(name = "内容审核管理", description = "人工复审队列管理、举报处理、敏感词库热重载")
public class AuditController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuditService auditService;

    @Autowired
    private AdminService adminService;

    private Long currentUserId(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            try {
                Integer userId = jwtUtil.getUserIdFromToken(token);
                return userId != null ? userId.longValue() : null;
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    private R<?> forbid(HttpServletRequest request) {
        Long uid = currentUserId(request);
        if (uid == null) {
            return R.fail(401, "未登录");
        }
        if (!adminService.isAdmin(uid.intValue())) {
            return R.fail(403, "没有管理端访问权限");
        }
        return null;
    }

    @GetMapping("/pending")
    @Operation(summary = "获取待复审列表", security = @SecurityRequirement(name = "bearerAuth"))
    public R<List<ManualReviewQueue>> getPendingList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            HttpServletRequest request) {
        R<?> check = forbid(request);
        if (check != null) return (R<List<ManualReviewQueue>>) check;
        return R.ok(auditService.getPendingReviewList(page, Math.min(size, 100)));
    }

    @GetMapping("/pending/count")
    @Operation(summary = "获取待复审数量", security = @SecurityRequirement(name = "bearerAuth"))
    public R<Integer> getPendingCount(HttpServletRequest request) {
        R<?> check = forbid(request);
        if (check != null) return (R<Integer>) check;
        return R.ok(auditService.getPendingCount());
    }

    @PostMapping("/{queueId}/approve")
    @Operation(summary = "通过举报", security = @SecurityRequirement(name = "bearerAuth"))
    public R<String> approve(@PathVariable Long queueId,
                             @RequestParam(required = false) String remark,
                             HttpServletRequest request) {
        R<?> check = forbid(request);
        if (check != null) return (R<String>) check;
        Long reviewerId = currentUserId(request);
        if (reviewerId == null) {
            return R.fail(401, "未登录");
        }
        boolean success = auditService.approveContent(reviewerId, queueId, remark);
        return success ? R.ok("举报已通过，已通知举报人") : R.fail(400, "处理失败");
    }

    @PostMapping("/{queueId}/reject")
    @Operation(summary = "驳回举报", security = @SecurityRequirement(name = "bearerAuth"))
    public R<String> reject(@PathVariable Long queueId,
                            @RequestParam(required = false) String remark,
                            HttpServletRequest request) {
        R<?> check = forbid(request);
        if (check != null) return (R<String>) check;
        Long reviewerId = currentUserId(request);
        if (reviewerId == null) {
            return R.fail(401, "未登录");
        }
        boolean success = auditService.rejectContent(reviewerId, queueId, remark);
        return success ? R.ok("举报已驳回，已通知举报人") : R.fail(400, "处理失败");
    }

    @PostMapping("/{queueId}/delete-content")
    @Operation(summary = "直接删除被举报内容", security = @SecurityRequirement(name = "bearerAuth"))
    public R<String> deleteReportedContent(@PathVariable Long queueId,
                                           @RequestParam(required = false) String remark,
                                           HttpServletRequest request) {
        R<?> check = forbid(request);
        if (check != null) return (R<String>) check;
        Long reviewerId = currentUserId(request);
        if (reviewerId == null) {
            return R.fail(401, "未登录");
        }
        ManualReviewQueue queue = auditService.getReviewQueue(queueId);
        if (queue == null) {
            return R.fail(404, "举报记录不存在");
        }
        try {
            switch (queue.getContentType()) {
                case "post" -> {
                    ContentModerateRequest body = new ContentModerateRequest();
                    body.setAction("delete");
                    body.setReason(remark);
                    adminService.moderatePost(reviewerId.intValue(), queue.getContentId().intValue(), body);
                }
                case "comment" -> {
                    ContentModerateRequest body = new ContentModerateRequest();
                    body.setAction("delete");
                    body.setReason(remark);
                    adminService.moderateComment(reviewerId.intValue(), queue.getContentId().intValue(), body);
                }
                case "message" -> adminService.deleteMessage(reviewerId.intValue(), queue.getContentId().intValue());
                default -> {
                    return R.fail(400, "该举报类型不支持直接删除内容");
                }
            }
            boolean success = auditService.completeReportAction(reviewerId, queueId, "delete_content", remark);
            return success ? R.ok("内容已删除，已通知举报人") : R.fail(400, "删除后更新举报状态失败");
        } catch (IllegalArgumentException e) {
            return R.fail(400, e.getMessage());
        }
    }

    @PostMapping("/{queueId}/punish")
    @Operation(summary = "处罚被举报用户", security = @SecurityRequirement(name = "bearerAuth"))
    public R<String> punishReportedUser(@PathVariable Long queueId,
                                        @RequestBody UserPunishRequest body,
                                        HttpServletRequest request) {
        R<?> check = forbid(request);
        if (check != null) return (R<String>) check;
        Long reviewerId = currentUserId(request);
        if (reviewerId == null) {
            return R.fail(401, "未登录");
        }
        ManualReviewQueue queue = auditService.getReviewQueue(queueId);
        if (queue == null) {
            return R.fail(404, "举报记录不存在");
        }
        if (queue.getTargetUserId() == null) {
            return R.fail(400, "该举报记录缺少被举报用户");
        }
        try {
            String msg = adminService.punishUser(reviewerId.intValue(), queue.getTargetUserId().intValue(), body);
            String processAction = "punish_user_" + (body.getAction() == null ? "warn" : body.getAction().trim().toLowerCase());
            boolean success = auditService.completeReportAction(reviewerId, queueId, processAction, body.getReason());
            return success ? R.ok(msg) : R.fail(400, "处罚成功，但更新举报状态失败");
        } catch (IllegalArgumentException e) {
            return R.fail(400, e.getMessage());
        }
    }

    @PostMapping("/offline")
    @Operation(summary = "事后下架已发布内容", security = @SecurityRequirement(name = "bearerAuth"))
    public R<String> offline(@RequestParam String contentType,
                             @RequestParam Long contentId,
                             @RequestParam(required = false) String remark,
                             HttpServletRequest request) {
        R<?> check = forbid(request);
        if (check != null) return (R<String>) check;
        Long auditorId = currentUserId(request);
        if (auditorId == null) {
            return R.fail(401, "未登录");
        }
        boolean success = auditService.offlineContent(auditorId, contentType, contentId, remark);
        return success ? R.ok("已下架") : R.fail(400, "下架失败");
    }

    @PostMapping("/reload")
    @Operation(summary = "热重载敏感词库", security = @SecurityRequirement(name = "bearerAuth"))
    public R<String> reloadWordLibrary(HttpServletRequest request) {
        R<?> check = forbid(request);
        if (check != null) return (R<String>) check;
        try {
            auditService.reloadWordLibrary();
            return R.ok("敏感词库已热重载");
        } catch (Exception e) {
            return R.fail(500, "热重载失败: " + e.getMessage());
        }
    }

    @GetMapping("/stats")
    @Operation(summary = "审核统计信息", security = @SecurityRequirement(name = "bearerAuth"))
    public R<Map<String, Object>> getStats(HttpServletRequest request) {
        R<?> check = forbid(request);
        if (check != null) return (R<Map<String, Object>>) check;
        return R.ok(auditService.getAuditStats());
    }
}
