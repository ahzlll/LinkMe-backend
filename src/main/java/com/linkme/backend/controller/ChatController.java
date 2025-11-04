package com.linkme.backend.controller;

import com.linkme.backend.common.JwtUtil;
import com.linkme.backend.common.R;
import com.linkme.backend.controller.dto.ConversationCreateRequest;
import com.linkme.backend.controller.dto.ConversationResponse;
import com.linkme.backend.controller.dto.MessageRequest;
import com.linkme.backend.controller.dto.MessageResponse;
import com.linkme.backend.entity.Conversation;
import com.linkme.backend.service.ChatService;
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
 * 聊天控制器
 * 
 * 功能描述：
 * - 处理聊天相关的HTTP请求
 * - 包括会话管理、消息发送等功能
 * 
 * @author Ahz
 * @version 1.2.1
 */
@RestController
@RequestMapping("/conversations")
@Tag(name = "聊天管理", description = "聊天会话和消息相关的API接口")
public class ChatController {
    
    @Autowired
    private ChatService chatService;
    
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
     * 获取会话列表
     * 
     * @param page 页码
     * @param size 每页数量
     * @param request HTTP请求
     * @return 会话列表
     */
    @GetMapping
    @Operation(summary = "获取会话列表", description = "获取当前用户的会话列表", 
               security = @SecurityRequirement(name = "bearerAuth"))
    public R<List<ConversationResponse>> getConversations(
            @RequestParam(defaultValue = "1") @Parameter(description = "页码") Integer page,
            @RequestParam(defaultValue = "20") @Parameter(description = "每页数量") Integer size,
            HttpServletRequest request) {
        Integer userId = getCurrentUserId(request);
        if (userId == null) {
            return R.fail(401, "未授权，请先登录");
        }
        
        List<ConversationResponse> conversations = chatService.getConversationsByUserId(userId, page, size);
        return R.ok(conversations);
    }
    
    /**
     * 创建会话
     * 
     * @param requestBody 创建请求
     * @param httpRequest HTTP请求
     * @return 会话信息
     */
    @PostMapping
    @Operation(summary = "创建会话", description = "与指定用户创建或获取会话", 
               security = @SecurityRequirement(name = "bearerAuth"))
    public R<ConversationResponse> createConversation(
            @RequestBody ConversationCreateRequest requestBody,
            HttpServletRequest httpRequest) {
        Integer currentUserId = getCurrentUserId(httpRequest);
        if (currentUserId == null) {
            return R.fail(401, "未授权，请先登录");
        }
        
        if (requestBody.getUserId() == null) {
            return R.fail(400, "用户ID不能为空");
        }
        
        if (requestBody.getUserId().equals(currentUserId)) {
            return R.fail(400, "不能与自己创建会话");
        }
        
        try {
            Conversation conversation = chatService.createOrGetConversation(currentUserId, requestBody.getUserId());
            ConversationResponse response = chatService.getConversationById(conversation.getConversationId(), currentUserId);
            return R.ok(response);
        } catch (Exception e) {
            return R.fail(500, "创建会话失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取会话详情
     * 
     * @param conversationId 会话ID
     * @param request HTTP请求
     * @return 会话信息
     */
    @GetMapping("/{conversationId}")
    @Operation(summary = "获取会话详情", description = "根据会话ID获取会话详细信息", 
               security = @SecurityRequirement(name = "bearerAuth"))
    public R<ConversationResponse> getConversation(
            @PathVariable @Parameter(description = "会话ID") Integer conversationId,
            HttpServletRequest request) {
        Integer userId = getCurrentUserId(request);
        if (userId == null) {
            return R.fail(401, "未授权，请先登录");
        }
        
        ConversationResponse conversation = chatService.getConversationById(conversationId, userId);
        if (conversation == null) {
            return R.fail(404, "会话不存在或无权限访问");
        }
        
        return R.ok(conversation);
    }
    
    /**
     * 获取消息列表
     * 
     * @param conversationId 会话ID
     * @param page 页码
     * @param size 每页数量
     * @param request HTTP请求
     * @return 消息列表
     */
    @GetMapping("/{conversationId}/messages")
    @Operation(summary = "获取消息列表", description = "获取指定会话的消息列表", 
               security = @SecurityRequirement(name = "bearerAuth"))
    public R<List<MessageResponse>> getMessages(
            @PathVariable @Parameter(description = "会话ID") Integer conversationId,
            @RequestParam(defaultValue = "1") @Parameter(description = "页码") Integer page,
            @RequestParam(defaultValue = "50") @Parameter(description = "每页数量") Integer size,
            HttpServletRequest request) {
        Integer userId = getCurrentUserId(request);
        if (userId == null) {
            return R.fail(401, "未授权，请先登录");
        }
        
        List<MessageResponse> messages = chatService.getMessagesByConversationId(conversationId, userId, page, size);
        return R.ok(messages);
    }
    
    /**
     * 发送消息
     * 
     * @param conversationId 会话ID
     * @param messageRequest 消息请求
     * @param request HTTP请求
     * @return 消息信息
     */
    @PostMapping("/{conversationId}/messages")
    @Operation(summary = "发送消息", description = "在指定会话中发送消息", 
               security = @SecurityRequirement(name = "bearerAuth"))
    public R<MessageResponse> sendMessage(
            @PathVariable @Parameter(description = "会话ID") Integer conversationId,
            @RequestBody MessageRequest messageRequest,
            HttpServletRequest request) {
        Integer userId = getCurrentUserId(request);
        if (userId == null) {
            return R.fail(401, "未授权，请先登录");
        }
        
        if (messageRequest.getContent() == null || messageRequest.getContent().trim().isEmpty()) {
            return R.fail(400, "消息内容不能为空");
        }
        
        try {
            MessageResponse message = chatService.sendMessage(
                    userId,
                    conversationId,
                    messageRequest.getReceiverId(),
                    messageRequest.getContentType(),
                    messageRequest.getContent());
            return R.ok(message);
        } catch (Exception e) {
            return R.fail(500, "发送消息失败: " + e.getMessage());
        }
    }
    
    /**
     * 直接发送消息（通过接收者ID，自动创建或获取会话）
     * 
     * @param messageRequest 消息请求
     * @param request HTTP请求
     * @return 消息信息
     */
    @PostMapping("/messages")
    @Operation(summary = "发送消息", description = "直接向指定用户发送消息（自动创建或获取会话）", 
               security = @SecurityRequirement(name = "bearerAuth"))
    public R<MessageResponse> sendMessageDirect(
            @RequestBody MessageRequest messageRequest,
            HttpServletRequest request) {
        Integer userId = getCurrentUserId(request);
        if (userId == null) {
            return R.fail(401, "未授权，请先登录");
        }
        
        if (messageRequest.getReceiverId() == null) {
            return R.fail(400, "接收者ID不能为空");
        }
        
        if (messageRequest.getContent() == null || messageRequest.getContent().trim().isEmpty()) {
            return R.fail(400, "消息内容不能为空");
        }
        
        try {
            MessageResponse message = chatService.sendMessage(
                    userId,
                    messageRequest.getConversationId(),
                    messageRequest.getReceiverId(),
                    messageRequest.getContentType(),
                    messageRequest.getContent());
            return R.ok(message);
        } catch (Exception e) {
            return R.fail(500, "发送消息失败: " + e.getMessage());
        }
    }
    
    /**
     * 标记消息为已读
     * 
     * @param conversationId 会话ID
     * @param request HTTP请求
     * @return 操作结果
     */
    @PutMapping("/{conversationId}/read")
    @Operation(summary = "标记消息为已读", description = "标记指定会话的所有消息为已读", 
               security = @SecurityRequirement(name = "bearerAuth"))
    public R<String> markAsRead(
            @PathVariable @Parameter(description = "会话ID") Integer conversationId,
            HttpServletRequest request) {
        Integer userId = getCurrentUserId(request);
        if (userId == null) {
            return R.fail(401, "未授权，请先登录");
        }
        
        boolean success = chatService.markMessagesAsRead(conversationId, userId);
        if (success) {
            return R.ok("标记已读成功");
        } else {
            return R.fail(400, "标记已读失败，会话可能不存在或无权限访问");
        }
    }
    
    /**
     * 获取未读消息数量
     * 
     * @param conversationId 会话ID
     * @param request HTTP请求
     * @return 未读消息数量
     */
    @GetMapping("/{conversationId}/unread-count")
    @Operation(summary = "获取未读消息数量", description = "获取指定会话的未读消息数量", 
               security = @SecurityRequirement(name = "bearerAuth"))
    public R<Map<String, Integer>> getUnreadCount(
            @PathVariable @Parameter(description = "会话ID") Integer conversationId,
            HttpServletRequest request) {
        Integer userId = getCurrentUserId(request);
        if (userId == null) {
            return R.fail(401, "未授权，请先登录");
        }
        
        int count = chatService.getUnreadCount(conversationId, userId);
        return R.ok(Map.of("unreadCount", count));
    }
    
    /**
     * 获取用户总未读消息数量
     * 
     * @param request HTTP请求
     * @return 总未读消息数量
     */
    @GetMapping("/unread-count/total")
    @Operation(summary = "获取总未读消息数量", description = "获取当前用户所有会话的总未读消息数量", 
               security = @SecurityRequirement(name = "bearerAuth"))
    public R<Map<String, Integer>> getTotalUnreadCount(HttpServletRequest request) {
        Integer userId = getCurrentUserId(request);
        if (userId == null) {
            return R.fail(401, "未授权，请先登录");
        }
        
        int count = chatService.getTotalUnreadCount(userId);
        return R.ok(Map.of("totalUnreadCount", count));
    }
}

