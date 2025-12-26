package com.linkme.backend.controller;

import com.linkme.backend.common.JwtUtil;
import com.linkme.backend.common.R;
import com.linkme.backend.controller.dto.MatchRecommendationResponse;
import com.linkme.backend.service.MatchRecommendService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 匹配推荐控制器
 * - 提供推荐列表接口，供前端展示并发起聊天
 *
 * @author riki
 * @version 1.0
 */
@RestController
@RequestMapping("/match")
@Tag(name = "匹配推荐", description = "匹配推荐相关API")
public class MatchController {

    @Autowired
    private MatchRecommendService matchRecommendService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 从请求头 Authorization 中解析当前用户ID
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
     * 获取匹配推荐列表
     */
    @GetMapping("/recommendations")
    @Operation(summary = "获取匹配推荐列表", description = "从数据库筛选候选用户并计算匹配度，按分数降序返回",
            security = @SecurityRequirement(name = "bearerAuth"))
    public R<List<MatchRecommendationResponse>> getRecommendations(
            @RequestParam(defaultValue = "1") @Parameter(description = "页码") Integer page,
            @RequestParam(defaultValue = "20") @Parameter(description = "每页数量") Integer size,
            HttpServletRequest request) {
        Integer currentUserId = getCurrentUserId(request);
        if (currentUserId == null) {
            return R.fail(401, "未授权，请先登录");
        }

        List<MatchRecommendationResponse> list = matchRecommendService.getRecommendations(currentUserId, page, size);
        return R.ok(list);
    }
}
