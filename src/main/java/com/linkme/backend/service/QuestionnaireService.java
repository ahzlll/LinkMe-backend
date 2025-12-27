package com.linkme.backend.service;

import com.linkme.backend.controller.dto.PostCreateRequest;

/**
 * 问卷服务接口
 *
 * 职责：
 * - 提供问卷相关的业务逻辑处理
 * - 支持问卷的创建/提交和查询功能
 * - 处理用户匹配偏好的保存和更新
 * - 包括年龄范围、距离偏好、关系模式、沟通期待等设置
 * - 管理用户性格特质、关系品质、匹配维度等数据
 *
 * author: riki
 * version: 1.0
 */
public interface QuestionnaireService {
    
    /**
     * 保存或更新问卷
     *
     * 功能说明：
     * - 如果用户已有问卷数据，则更新；否则创建新记录
     * - 保存用户匹配偏好（年龄范围、距离偏好、关系模式、沟通期待等）
     * - 保存用户性格特质选择（自身特质和理想对象特质）
     * - 保存用户关系品质选择
     * - 保存必须匹配维度
     * - 保存优先匹配维度（支持优先级设置）
     * - 更新用户表的问卷完成状态
     *
     * @param userId 用户ID
     * @param request 问卷请求数据对象，包含所有问卷相关信息
     *                 - ageMin/ageMax/ageUnlimited: 年龄要求
     *                 - distancePreference: 距离偏好
     *                 - relationshipModeId: 理想关系模式ID
     *                 - communicationExpectationId: 沟通期待ID
     *                 - additionalRequirements: 其他交友要求
     *                 - personalities: 性格特质选择列表
     *                 - relationshipQualities: 关系品质选择列表
     *                 - mustDimensions: 必须匹配维度ID列表
     *                 - priorityDimensions: 优先匹配维度列表
     */
    void saveOrUpdateQuestionnaire(Integer userId, PostCreateRequest.QuestionnaireRequest request);
    
    /**
     * 根据用户ID获取问卷数据
     *
     * 功能说明：
     * - 查询用户的完整问卷数据
     * - 包括匹配偏好、性格特质、关系品质、匹配维度等信息
     * - 如果用户没有提交过问卷，返回null
     *
     * @param userId 用户ID
     * @return 问卷响应数据对象，包含所有问卷相关信息
     *         - 如果用户没有提交过问卷，返回null
     *         - 包含userId、年龄要求、距离偏好、关系模式、沟通期待等基本信息
     *         - 包含性格特质选择列表（带分类名称和选项名称）
     *         - 包含关系品质选择列表
     *         - 包含必须匹配维度列表
     *         - 包含优先匹配维度列表（带优先级）
     */
    PostCreateRequest.QuestionnaireResponse getQuestionnaireByUserId(Integer userId);
}
