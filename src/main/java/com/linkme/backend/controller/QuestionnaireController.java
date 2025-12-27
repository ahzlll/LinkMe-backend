package com.linkme.backend.controller;

import com.linkme.backend.controller.dto.PostCreateRequest;
import com.linkme.backend.service.QuestionnaireService;
import com.linkme.backend.common.JwtUtil;
import com.linkme.backend.common.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 问卷控制器
 *
 * 职责：
 * - 处理问卷相关的 HTTP 请求
 * - 提供问卷创建/提交功能
 * - 提供问卷查询功能（当前用户或指定用户）
 *
 * 接口说明：
 * - POST /api/questionnaire - 创建/提交问卷（需要Bearer Token认证）
 * - GET /api/questionnaire - 获取当前登录用户的问卷数据（需要Bearer Token认证）
 * - GET /api/questionnaire/{userId} - 获取指定用户的问卷数据（需要Bearer Token认证，仅自己可见）
 *
 * 认证方式：
 * - 所有接口都需要在请求头中携带 Bearer Token
 * - Token格式：Authorization: Bearer {token}
 *
 * @author riki
 * @version 1.0
 */
@RestController
@RequestMapping("/api/questionnaire")
@Tag(name = "问卷管理", description = "问卷提交与查询相关API")
public class QuestionnaireController {

    @Autowired
    private QuestionnaireService questionnaireService;
    
    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 从请求头中获取当前用户ID
     * 
     * 功能说明：
     * - 从HTTP请求头中提取Authorization字段
     * - 解析Bearer Token并获取用户ID
     * - 如果Token无效或不存在，返回null
     * 
     * @param request HTTP请求对象
     * @return 当前登录用户的ID，如果未认证则返回null
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
     * 创建/提交问卷
     * 
     * 功能说明：
     * - 提交或更新当前登录用户的问卷数据
     * - 如果用户已有问卷数据，则更新；否则创建新记录
     * - 需要Bearer Token认证
     * 
     * 请求路径：POST /api/questionnaire
     * 
     * 请求头：
     * - Authorization: Bearer {token} (必需)
     * 
     * 请求体：
     * - QuestionnaireRequest对象，包含问卷的所有数据
     *   - ageMin: 最小年龄要求
     *   - ageMax: 最大年龄要求
     *   - ageUnlimited: 是否无年龄限制
     *   - distancePreference: 距离偏好
     *   - relationshipModeId: 理想关系模式ID
     *   - communicationExpectationId: 沟通期待ID
     *   - additionalRequirements: 其他交友要求
     *   - personalities: 性格特质选择列表
     *   - relationshipQualities: 关系品质选择列表
     *   - mustDimensions: 必须匹配维度ID列表
     *   - priorityDimensions: 优先匹配维度列表
     * 
     * 响应：
     * - 成功：返回200状态码和成功消息
     * - 失败：返回401状态码（未授权）或其他错误信息
     * 
     * @param request 问卷请求数据对象
     * @param httpRequest HTTP请求对象，用于获取认证信息
     * @return 统一响应对象，成功时返回R.ok()，失败时返回R.fail()
     */
    @PostMapping
    @Operation(summary = "创建/提交问卷", description = "提交或更新当前登录用户的问卷数据，需要Bearer Token认证",
            security = @SecurityRequirement(name = "bearerAuth"))
    public R<Void> submitQuestionnaire(@RequestBody PostCreateRequest.QuestionnaireRequest request, HttpServletRequest httpRequest) {
        Integer currentUserId = getCurrentUserId(httpRequest);
        if (currentUserId == null) {
            return R.fail(401, "未授权，请先登录");
        }
        questionnaireService.saveOrUpdateQuestionnaire(currentUserId, request);
        return R.ok();
    }

    /**
     * 获取当前用户问卷
     * 
     * 功能说明：
     * - 获取当前登录用户的问卷数据
     * - 需要Bearer Token认证
     * - 如果用户没有提交过问卷，返回404
     * 
     * 请求路径：GET /api/questionnaire
     * 
     * 请求头：
     * - Authorization: Bearer {token} (必需)
     * 
     * 响应：
     * - 成功：返回200状态码和QuestionnaireResponse对象
     * - 失败：返回401状态码（未授权）或404状态码（问卷数据不存在）
     * 
     * @param request HTTP请求对象，用于获取认证信息
     * @return 统一响应对象，包含问卷数据或错误信息
     */
    @GetMapping
    @Operation(summary = "获取当前用户问卷", description = "获取当前登录用户的问卷数据，需要Bearer Token认证",
            security = @SecurityRequirement(name = "bearerAuth"))
    public R<PostCreateRequest.QuestionnaireResponse> getCurrentUserQuestionnaire(HttpServletRequest request) {
        Integer currentUserId = getCurrentUserId(request);
        if (currentUserId == null) {
            return R.fail(401, "未授权，请先登录");
        }
        PostCreateRequest.QuestionnaireResponse response = questionnaireService.getQuestionnaireByUserId(currentUserId);
        if (response == null) {
            return R.fail(404, "问卷数据不存在");
        }
        return R.ok(response);
    }

    /**
     * 获取指定用户问卷
     * 
     * 功能说明：
     * - 获取指定用户的问卷数据
     * - 需要Bearer Token认证
     * - 权限控制：只能查看自己的问卷，不能查看他人的问卷
     * - 如果用户没有提交过问卷，返回404
     * 
     * 请求路径：GET /api/questionnaire/{userId}
     * 
     * 请求头：
     * - Authorization: Bearer {token} (必需)
     * 
     * 路径参数：
     * - userId: 用户ID（必需）
     * 
     * 响应：
     * - 成功：返回200状态码和QuestionnaireResponse对象
     * - 失败：
     *   - 401：未授权，请先登录
     *   - 403：无权访问他人问卷（只能查看自己的问卷）
     *   - 404：问卷数据不存在
     * 
     * @param userId 要查询的用户ID
     * @param request HTTP请求对象，用于获取认证信息
     * @return 统一响应对象，包含问卷数据或错误信息
     */
    @GetMapping("/{userId}")
    @Operation(summary = "获取指定用户问卷", description = "获取指定用户的问卷数据（仅自己可见），需要Bearer Token认证",
            security = @SecurityRequirement(name = "bearerAuth"))
    public R<PostCreateRequest.QuestionnaireResponse> getQuestionnaireByUserId(
            @Parameter(description = "用户ID") @PathVariable Integer userId,
            HttpServletRequest request) {
        Integer currentUserId = getCurrentUserId(request);
        if (currentUserId == null) {
            return R.fail(401, "未授权，请先登录");
        }
        
        // 仅自己可见：只能查看自己的问卷
        if (!currentUserId.equals(userId)) {
            return R.fail(403, "无权访问他人问卷");
        }
        
        PostCreateRequest.QuestionnaireResponse response = questionnaireService.getQuestionnaireByUserId(userId);
        if (response == null) {
            return R.fail(404, "问卷数据不存在");
        }
        return R.ok(response);
    }
}
