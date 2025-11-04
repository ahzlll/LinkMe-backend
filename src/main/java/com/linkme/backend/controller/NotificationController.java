package com.linkme.backend.controller;

import com.linkme.backend.common.JwtUtil;
import com.linkme.backend.common.R;
import com.linkme.backend.controller.dto.NotificationResponse;
import com.linkme.backend.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 通知控制器
 * 
 * 功能描述：
 * - 处理通知相关的HTTP请求
 * - 包括通知查询、标记已读、删除等功能
 * 
 * @author Ahz
 * @version 1.2
 */
@RestController
@RequestMapping("/notifications")
@Tag(name = "通知管理", description = "通知相关的API接口")
public class NotificationController {
    
    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 从请求头中获取当前用户ID
     */
    private Integer getCurrentUserId(HttpServletRequest request) {
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
    
    /**
     * 获取通知列表
     * 
     * @param isRead 是否已读（可选，不传表示全部）
     * @param page 页码
     * @param size 每页数量
     * @param request HTTP请求
     * @return 通知列表
     */
    @GetMapping
    @Operation(summary = "获取通知列表", description = "获取当前用户的通知列表", 
               security = @SecurityRequirement(name = "bearerAuth"))
    public R<List<NotificationResponse>> getNotifications(
            @RequestParam(required = false) @Parameter(description = "是否已读") Boolean isRead,
            @RequestParam(defaultValue = "1") @Parameter(description = "页码") Integer page,
            @RequestParam(defaultValue = "20") @Parameter(description = "每页数量") Integer size,
            HttpServletRequest request) {
        Integer userId = getCurrentUserId(request);
        if (userId == null) {
            return R.fail(401, "未授权，请先登录");
        }
        
        List<NotificationResponse> notifications = notificationService.getNotificationsByUserId(userId, isRead, page, size);
        return R.ok(notifications);
    }
    
    /**
     * 获取未读通知数量
     * 
     * @param request HTTP请求
     * @return 未读通知数量
     */
    @GetMapping("/unread-count")
    @Operation(summary = "获取未读通知数量", description = "获取当前用户的未读通知数量", 
               security = @SecurityRequirement(name = "bearerAuth"))
    public R<Map<String, Integer>> getUnreadCount(HttpServletRequest request) {
        Integer userId = getCurrentUserId(request);
        if (userId == null) {
            return R.fail(401, "未授权，请先登录");
        }
        
        int count = notificationService.getUnreadCount(userId);
        return R.ok(Map.of("unreadCount", count));
    }
    
    /**
     * 标记通知为已读
     * 
     * @param notificationId 通知ID
     * @param request HTTP请求
     * @return 操作结果
     */
    @PutMapping("/{notificationId}/read")
    @Operation(summary = "标记通知为已读", description = "标记指定通知为已读", 
               security = @SecurityRequirement(name = "bearerAuth"))
    public R<String> markAsRead(
            @PathVariable @Parameter(description = "通知ID") Integer notificationId,
            HttpServletRequest request) {
        Integer userId = getCurrentUserId(request);
        if (userId == null) {
            return R.fail(401, "未授权，请先登录");
        }
        
        boolean success = notificationService.markAsRead(notificationId, userId);
        if (success) {
            return R.ok("标记已读成功");
        } else {
            return R.fail(400, "标记已读失败，通知可能不存在或无权限访问");
        }
    }
    
    /**
     * 标记所有通知为已读
     * 
     * @param request HTTP请求
     * @return 操作结果
     */
    @PutMapping("/read-all")
    @Operation(summary = "标记所有通知为已读", description = "标记当前用户的所有通知为已读", 
               security = @SecurityRequirement(name = "bearerAuth"))
    public R<Map<String, Object>> markAllAsRead(HttpServletRequest request) {
        Integer userId = getCurrentUserId(request);
        if (userId == null) {
            return R.fail(401, "未授权，请先登录");
        }
        
        int count = notificationService.markAllAsRead(userId);
        return R.ok(Map.of("message", "标记成功", "count", count));
    }
    
    /**
     * 删除通知
     * 
     * @param notificationId 通知ID
     * @param request HTTP请求
     * @return 操作结果
     */
    @DeleteMapping("/{notificationId}")
    @Operation(summary = "删除通知", description = "删除指定通知", 
               security = @SecurityRequirement(name = "bearerAuth"))
    public R<String> deleteNotification(
            @PathVariable @Parameter(description = "通知ID") Integer notificationId,
            HttpServletRequest request) {
        Integer userId = getCurrentUserId(request);
        if (userId == null) {
            return R.fail(401, "未授权，请先登录");
        }
        
        boolean success = notificationService.deleteNotification(notificationId, userId);
        if (success) {
            return R.ok("删除成功");
        } else {
            return R.fail(400, "删除失败，通知可能不存在或无权限访问");
        }
    }
}

