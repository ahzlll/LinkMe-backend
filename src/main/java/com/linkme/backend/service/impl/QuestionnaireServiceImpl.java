package com.linkme.backend.service.impl;

import com.linkme.backend.controller.dto.PostCreateRequest;
import com.linkme.backend.entity.UserMatchingPreference;
import com.linkme.backend.entity.UserPersonalitySelection;
import com.linkme.backend.entity.UserRelationshipQualitySelection;
import com.linkme.backend.mapper.*;
import com.linkme.backend.service.QuestionnaireService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 问卷服务实现类
 *
 * 职责：
 * - 实现问卷相关的业务逻辑
 * - 处理问卷的保存和更新
 * - 处理问卷数据的查询和组装
 *
 * @author riki
 * @version 1.1
 */
@Service
public class QuestionnaireServiceImpl implements QuestionnaireService {

    @Autowired
    private UserMatchingPreferenceMapper userMatchingPreferenceMapper;

    @Autowired
    private UserPersonalityMapper userPersonalityMapper;

    @Autowired
    private UserRelationshipQualityMapper userRelationshipQualityMapper;

    @Autowired
    private UserMatchingDimensionMapper userMatchingDimensionMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 保存或更新问卷
     *
     * @param userId 用户ID
     * @param request 问卷请求数据
     */
    @Override
    @Transactional
    public void saveOrUpdateQuestionnaire(Integer userId, PostCreateRequest.QuestionnaireRequest request) {
        if (userId == null || request == null) {
            throw new IllegalArgumentException("用户ID和问卷数据不能为空");
        }

        // 1. 保存或更新用户匹配偏好
        UserMatchingPreference preference = userMatchingPreferenceMapper.selectByUserId(userId);
        if (preference == null) {
            // 创建新的偏好记录
            preference = new UserMatchingPreference();
            preference.setUserId(userId);
            preference.setAgeMin(request.getAgeMin());
            preference.setAgeMax(request.getAgeMax());
            preference.setAgeUnlimited(request.getAgeUnlimited());
            preference.setDistancePreference(request.getDistancePreference());
            preference.setRelationshipModeId(request.getRelationshipModeId());
            preference.setCommunicationExpectationId(request.getCommunicationExpectationId());
            preference.setAdditionalRequirements(request.getAdditionalRequirements());
            preference.setCreatedAt(LocalDateTime.now());
            preference.setUpdatedAt(LocalDateTime.now());
            userMatchingPreferenceMapper.insert(preference);
        } else {
            // 更新现有偏好记录
            preference.setAgeMin(request.getAgeMin());
            preference.setAgeMax(request.getAgeMax());
            preference.setAgeUnlimited(request.getAgeUnlimited());
            preference.setDistancePreference(request.getDistancePreference());
            preference.setRelationshipModeId(request.getRelationshipModeId());
            preference.setCommunicationExpectationId(request.getCommunicationExpectationId());
            preference.setAdditionalRequirements(request.getAdditionalRequirements());
            preference.setUpdatedAt(LocalDateTime.now());
            userMatchingPreferenceMapper.update(preference);
        }

        // 2. 保存用户性格特质（先删除旧的，再插入新的）
        userPersonalityMapper.deleteByUserId(userId);
        if (request.getPersonalities() != null && !request.getPersonalities().isEmpty()) {
            for (PostCreateRequest.QuestionnaireRequest.PersonalitySelectionRequest personality : request.getPersonalities()) {
                if (personality.getTraitOptionId() != null) {
                    userPersonalityMapper.insert(userId, personality.getTraitOptionId());
                }
            }
        }

        // 3. 保存用户关系品质（先删除旧的，再插入新的）
        userRelationshipQualityMapper.deleteByUserId(userId);
        if (request.getRelationshipQualities() != null && !request.getRelationshipQualities().isEmpty()) {
            for (PostCreateRequest.QuestionnaireRequest.RelationshipQualityRequest quality : request.getRelationshipQualities()) {
                if (quality.getQualityId() != null) {
                    userRelationshipQualityMapper.insert(userId, quality.getQualityId());
                }
            }
        }

        // 4. 保存必须匹配维度（先删除旧的，再插入新的）
        userMatchingDimensionMapper.deleteMustDimensionsByUserId(userId);
        if (request.getMustDimensions() != null && !request.getMustDimensions().isEmpty()) {
            for (Integer dimensionId : request.getMustDimensions()) {
                if (dimensionId != null) {
                    userMatchingDimensionMapper.insertMustDimension(userId, dimensionId);
                }
            }
        }

        // 5. 保存优先匹配维度（先删除旧的，再插入新的）
        userMatchingDimensionMapper.deletePriorityDimensionsByUserId(userId);
        if (request.getPriorityDimensions() != null && !request.getPriorityDimensions().isEmpty()) {
            for (PostCreateRequest.QuestionnaireRequest.PriorityDimensionRequest priorityDim : request.getPriorityDimensions()) {
                if (priorityDim.getDimensionId() != null) {
                    // 将priority字符串转换为数字：high=1, medium=2, low=3
                    int order = convertPriorityToOrder(priorityDim.getPriority());
                    userMatchingDimensionMapper.insertPriorityDimension(userId, priorityDim.getDimensionId(), order);
                }
            }
        }

        // 6. 更新用户表的问卷完成状态
        userMapper.updateQuestionnaireCompleted(userId, true);
    }

    /**
     * 根据用户ID获取问卷数据
     *
     * @param userId 用户ID
     * @return 问卷响应数据，如果不存在则返回null
     */
    @Override
    public PostCreateRequest.QuestionnaireResponse getQuestionnaireByUserId(Integer userId) {
        if (userId == null) {
            return null;
        }

        // 1. 查询用户匹配偏好
        UserMatchingPreference preference = userMatchingPreferenceMapper.selectByUserId(userId);
        if (preference == null) {
            return null;
        }

        PostCreateRequest.QuestionnaireResponse response = new PostCreateRequest.QuestionnaireResponse();
        response.setUserId(userId);
        response.setAgeMin(preference.getAgeMin());
        response.setAgeMax(preference.getAgeMax());
        response.setAgeUnlimited(preference.getAgeUnlimited());
        response.setDistancePreference(preference.getDistancePreference());
        response.setRelationshipModeId(preference.getRelationshipModeId());
        response.setCommunicationExpectationId(preference.getCommunicationExpectationId());
        response.setAdditionalRequirements(preference.getAdditionalRequirements());

        // 2. 查询用户性格特质
        List<UserPersonalitySelection> personalitySelections = userPersonalityMapper.selectSelectionsByUserId(userId);
        if (personalitySelections != null && !personalitySelections.isEmpty()) {
            List<PostCreateRequest.QuestionnaireResponse.PersonalitySelectionResponse> personalities = new ArrayList<>();
            for (UserPersonalitySelection selection : personalitySelections) {
                PostCreateRequest.QuestionnaireResponse.PersonalitySelectionResponse personality = new PostCreateRequest.QuestionnaireResponse.PersonalitySelectionResponse();
                personality.setTraitOptionId(selection.getOptionId());
                personality.setSelectionType(selection.getTraitType()); // self 或 ideal
                personality.setCategoryName(selection.getCategoryName());
                personality.setOptionName(selection.getOptionName());
                personalities.add(personality);
            }
            response.setPersonalities(personalities);
        }

        // 3. 查询用户关系品质（需要查询品质名称）
        List<UserRelationshipQualitySelection> qualitySelections = userRelationshipQualityMapper.selectByUserId(userId);
        if (qualitySelections != null && !qualitySelections.isEmpty()) {
            List<PostCreateRequest.QuestionnaireResponse.RelationshipQualityResponse> qualities = new ArrayList<>();
            // 注意：这里需要查询品质名称，暂时只设置ID，名称可以通过JOIN查询获取
            // 为了简化，这里先只设置ID，实际使用时可以通过额外的查询获取名称
            for (UserRelationshipQualitySelection selection : qualitySelections) {
                PostCreateRequest.QuestionnaireResponse.RelationshipQualityResponse quality = new PostCreateRequest.QuestionnaireResponse.RelationshipQualityResponse();
                quality.setQualityId(selection.getQualityId());
                // qualityName需要通过额外的查询获取，这里暂时留空
                qualities.add(quality);
            }
            response.setRelationshipQualities(qualities);
        }

        // 4. 查询必须匹配维度（需要查询维度名称）
        List<Integer> mustDimensionIds = userMatchingDimensionMapper.selectMustDimensionIdsByUserId(userId);
        if (mustDimensionIds != null && !mustDimensionIds.isEmpty()) {
            List<PostCreateRequest.QuestionnaireResponse.DimensionResponse> mustDimensions = new ArrayList<>();
            for (Integer dimensionId : mustDimensionIds) {
                PostCreateRequest.QuestionnaireResponse.DimensionResponse dimension = new PostCreateRequest.QuestionnaireResponse.DimensionResponse();
                dimension.setDimensionId(dimensionId);
                // dimensionName需要通过额外的查询获取，这里暂时留空
                mustDimensions.add(dimension);
            }
            response.setMustDimensions(mustDimensions);
        }

        // 5. 查询优先匹配维度（需要查询维度名称和优先级）
        List<Map<String, Object>> prioritySelections = userMatchingDimensionMapper.selectPriorityDimensionIdsByUserId(userId);
        if (prioritySelections != null && !prioritySelections.isEmpty()) {
            List<PostCreateRequest.QuestionnaireResponse.PriorityDimensionResponse> priorityDimensions = new ArrayList<>();
            for (Map<String, Object> selection : prioritySelections) {
                PostCreateRequest.QuestionnaireResponse.PriorityDimensionResponse priorityDim = new PostCreateRequest.QuestionnaireResponse.PriorityDimensionResponse();
                Object dimensionIdObj = selection.get("dimensionId");
                Object priorityOrderObj = selection.get("priorityOrder");
                if (dimensionIdObj != null) {
                    if (dimensionIdObj instanceof Integer) {
                        priorityDim.setDimensionId((Integer) dimensionIdObj);
                    } else if (dimensionIdObj instanceof Number) {
                        priorityDim.setDimensionId(((Number) dimensionIdObj).intValue());
                    }
                }
                if (priorityOrderObj != null) {
                    Integer order = priorityOrderObj instanceof Integer ? (Integer) priorityOrderObj : 
                                   ((Number) priorityOrderObj).intValue();
                    priorityDim.setPriority(convertOrderToPriority(order));
                }
                // dimensionName需要通过额外的查询获取，这里暂时留空
                priorityDimensions.add(priorityDim);
            }
            response.setPriorityDimensions(priorityDimensions);
        }

        return response;
    }

    /**
     * 将优先级字符串转换为数字顺序
     * high -> 1, medium -> 2, low -> 3
     */
    private int convertPriorityToOrder(String priority) {
        if (priority == null) {
            return 1;
        }
        switch (priority.toLowerCase()) {
            case "high":
                return 1;
            case "medium":
                return 2;
            case "low":
                return 3;
            default:
                return 1;
        }
    }

    /**
     * 将数字顺序转换为优先级字符串
     * 1 -> high, 2 -> medium, 3 -> low
     */
    private String convertOrderToPriority(Integer order) {
        if (order == null) {
            return "medium";
        }
        switch (order) {
            case 1:
                return "high";
            case 2:
                return "medium";
            case 3:
                return "low";
            default:
                return "medium";
        }
    }
}

