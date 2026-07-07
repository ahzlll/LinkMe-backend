package com.linkme.backend.controller;

import com.linkme.backend.common.JwtUtil;
import com.linkme.backend.common.R;
import com.linkme.backend.entity.User;
import com.linkme.backend.mapper.UserMapper;
import com.linkme.backend.service.LikeService;
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
 * 喜欢通知控制器
 * 
 * 功能描述：
 * - 处理用户之间的喜欢通知
 * - 包括发送喜欢、取消喜欢、查询喜欢状态等功能
 * 
 * @author riki
 * @version 1.0
 */
@RestController
@RequestMapping("/likes")
@Tag(name = "喜欢管理", description = "用户喜欢通知相关的API接口")
public class LikeController {
    
    @Autowired
    private LikeService likeService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserMapper userMapper;
    
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
     * 发送喜欢通知
     * 
     * @param requestDto 包含目标用户ID的请求对象
     * @param httpRequest HTTP请求
     * @return 操作结果
     */
    @PostMapping("/send")
    @Operation(summary = "发送喜欢通知", description = "向指定用户发送喜欢通知", 
               security = @SecurityRequirement(name = "bearerAuth"))
    public R<String> sendLike(
            @RequestBody @Parameter(description = "喜欢请求") Map<String, Integer> requestDto,
            HttpServletRequest httpRequest) {
        Integer currentUserId = getCurrentUserId(httpRequest);
        if (currentUserId == null) {
            return R.fail(401, "未授权，请先登录");
        }
        
        Integer targetUserId = requestDto.get("targetUserId");
        if (targetUserId == null) {
            return R.fail(400, "目标用户ID不能为空");
        }
        
        if (currentUserId.equals(targetUserId)) {
            return R.fail(400, "不能给自己发送喜欢通知");
        }

        // 校验当前用户是否已完成问卷
        User currentUser = userMapper.selectById(currentUserId);
        if (currentUser == null || !Boolean.TRUE.equals(currentUser.getMatchingQuestionnaireCompleted())) {
            return R.fail(403, "请先完成匹配问卷后再进行此操作");
        }

        try {
            likeService.sendLikeNotification(currentUserId, targetUserId);
            return R.ok("喜欢通知发送成功");
        } catch (Exception e) {
            return R.fail(500, "发送喜欢通知失败: " + e.getMessage());
        }
    }
    
    /**
     * 取消喜欢通知
     * 
     * @param requestDto 包含目标用户ID的请求对象
     * @param httpRequest HTTP请求
     * @return 操作结果
     */
    @DeleteMapping("/cancel")
    @Operation(summary = "取消喜欢通知", description = "取消向指定用户发送的喜欢通知", 
               security = @SecurityRequirement(name = "bearerAuth"))
    public R<String> cancelLike(
            @RequestBody @Parameter(description = "取消喜欢请求") Map<String, Integer> requestDto,
            HttpServletRequest httpRequest) {
        Integer currentUserId = getCurrentUserId(httpRequest);
        if (currentUserId == null) {
            return R.fail(401, "未授权，请先登录");
        }
        
        Integer targetUserId = requestDto.get("targetUserId");
        if (targetUserId == null) {
            return R.fail(400, "目标用户ID不能为空");
        }
        
        try {
            boolean success = likeService.cancelLikeNotification(currentUserId, targetUserId);
            if (success) {
                return R.ok("取消喜欢通知成功");
            } else {
                return R.fail(400, "取消失败，可能没有发送过喜欢通知");
            }
        } catch (Exception e) {
            return R.fail(500, "取消喜欢通知失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取我发送的喜欢列表
     * 
     * @param page 页码
     * @param size 每页数量
     * @param httpRequest HTTP请求
     * @return 我发送的喜欢列表
     */
    @GetMapping("/sent")
    @Operation(summary = "获取我发送的喜欢列表", description = "获取当前用户发送的喜欢通知列表", 
               security = @SecurityRequirement(name = "bearerAuth"))
    public R<List<Map<String, Object>>> getSentLikes(
            @RequestParam(defaultValue = "1") @Parameter(description = "页码") Integer page,
            @RequestParam(defaultValue = "20") @Parameter(description = "每页数量") Integer size,
            HttpServletRequest httpRequest) {
        Integer currentUserId = getCurrentUserId(httpRequest);
        if (currentUserId == null) {
            return R.fail(401, "未授权，请先登录");
        }
        
        List<Map<String, Object>> sentLikes = likeService.getSentLikes(currentUserId, page, size);
        return R.ok(sentLikes);
    }
    
    /**
     * 获取我收到的喜欢列表
     * 
     * @param page 页码
     * @param size 每页数量
     * @param httpRequest HTTP请求
     * @return 我收到的喜欢列表
     */
    @GetMapping("/received")
    @Operation(summary = "获取我收到的喜欢列表", description = "获取当前用户收到的喜欢通知列表", 
               security = @SecurityRequirement(name = "bearerAuth"))
    public R<List<Map<String, Object>>> getReceivedLikes(
            @RequestParam(defaultValue = "1") @Parameter(description = "页码") Integer page,
            @RequestParam(defaultValue = "20") @Parameter(description = "每页数量") Integer size,
            HttpServletRequest httpRequest) {
        Integer currentUserId = getCurrentUserId(httpRequest);
        if (currentUserId == null) {
            return R.fail(401, "未授权，请先登录");
        }
        
        List<Map<String, Object>> receivedLikes = likeService.getReceivedLikes(currentUserId, page, size);
        return R.ok(receivedLikes);
    }
    
    /**
     * 检查是否已喜欢指定用户
     * 
     * @param targetUserId 目标用户ID
     * @param httpRequest HTTP请求
     * @return 喜欢状态
     */
    @GetMapping("/status")
    @Operation(summary = "检查喜欢状态", description = "检查当前用户是否已喜欢指定用户", 
               security = @SecurityRequirement(name = "bearerAuth"))
    public R<Map<String, Object>> checkLikeStatus(
            @RequestParam @Parameter(description = "目标用户ID") Integer targetUserId,
            HttpServletRequest httpRequest) {
        Integer currentUserId = getCurrentUserId(httpRequest);
        if (currentUserId == null) {
            return R.fail(401, "未授权，请先登录");
        }
        
        boolean isLiked = likeService.checkLikeStatus(currentUserId, targetUserId);
        return R.ok(Map.of("isLiked", isLiked, "targetUserId", targetUserId));
    }
}
