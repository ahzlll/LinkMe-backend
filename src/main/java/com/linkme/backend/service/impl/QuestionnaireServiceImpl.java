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
    
    @Autowired
    private RelationshipQualityDefMapper relationshipQualityDefMapper;
    
    @Autowired
    private UserQuestionnaireCompletionMapper userQuestionnaireCompletionMapper;
    
    @Autowired
    private UserHobbyMapper userHobbyMapper;
    
    @Autowired
    private PersonalityTraitOptionMapper personalityTraitOptionMapper;

    /**
     * 保存或更新问卷
     *
     * @param userId 用户ID
     * @param request 问卷请求数据
     */
    @Override
    @Transactional
    public void saveOrUpdateQuestionnaire(Integer userId, PostCreateRequest.QuestionnaireRequest request, boolean finalSubmission) {
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

        // 1.1 保存用户爱好（先删除旧的，再插入新的；前端传的是代码，需映射到中文名再查ID）
        userHobbyMapper.deleteByUserId(userId);
        if (request.getInterests() != null && !request.getInterests().isEmpty()) {
            Map<String, String> hobbyCodeToName = Map.ofEntries(
                Map.entry("art", "绘画"),
                Map.entry("photography", "摄影"),
                Map.entry("calligraphy", "书法"),
                Map.entry("writing", "写作"),
                Map.entry("singing", "歌唱"),
                Map.entry("dance", "舞蹈"),
                Map.entry("theater", "戏剧"),
                Map.entry("instrument", "乐器演奏"),
                Map.entry("graphic_design", "平面设计"),
                Map.entry("video_editing", "视频剪辑"),
                Map.entry("reading", "阅读"),
                Map.entry("programming", "编程"),
                Map.entry("teaching", "教学"),
                Map.entry("psychology", "心理学"),
                Map.entry("language_learning", "语言学习"),
                Map.entry("philosophy", "哲学思考"),
                Map.entry("history_research", "历史研究"),
                Map.entry("investment", "投资理财"),
                Map.entry("public_speaking", "公开演讲"),
                Map.entry("entrepreneurship", "创业项目"),
                Map.entry("running", "跑步"),
                Map.entry("fitness", "健身"),
                Map.entry("swimming", "游泳"),
                Map.entry("cycling", "骑行"),
                Map.entry("fishing", "钓鱼"),
                Map.entry("yoga", "瑜伽"),
                Map.entry("camping", "露营"),
                Map.entry("martial_arts", "武术"),
                Map.entry("mountaineering", "登山"),
                Map.entry("climbing", "攀岩"),
                Map.entry("frisbee", "飞盘"),
                Map.entry("team_sports", "球类运动"),
                Map.entry("board_games", "桌游"),
                Map.entry("card_games", "棋牌"),
                Map.entry("magic", "魔术"),
                Map.entry("collecting", "收藏"),
                Map.entry("tv_shows", "追剧"),
                Map.entry("movies", "看电影"),
                Map.entry("music", "听音乐"),
                Map.entry("script_killing", "剧本杀"),
                Map.entry("escape_room", "密室逃脱"),
                Map.entry("gaming", "电子游戏"),
                Map.entry("cooking_baking", "烹饪/烘焙"),
                Map.entry("coffee_tea_mixology", "咖啡/茶艺/调酒"),
                Map.entry("handicraft_diy", "手工 DIY"),
                Map.entry("sewing", "缝纫"),
                Map.entry("home_decoration", "家居装饰"),
                Map.entry("organizing", "收纳整理"),
                Map.entry("floristry_gardening", "花艺绿植"),
                Map.entry("travel", "旅行"),
                Map.entry("bird_watching", "观鸟"),
                Map.entry("music_festival", "音乐节"),
                Map.entry("concert", "演唱会"),
                Map.entry("restaurant_hopping", "探店打卡"),
                Map.entry("exhibition", "展览打卡"),
                Map.entry("astronomy", "天文观测"),
                Map.entry("volunteering", "公益志愿"),
                Map.entry("petting", "撸宠"),
                Map.entry("city_walk", "城市漫步")
            );
            for (String code : request.getInterests()) {
                String name = hobbyCodeToName.get(code);
                if (name == null) {
                    continue;
                }
                Integer hobbyId = userHobbyMapper.selectHobbyIdByName(name);
                if (hobbyId != null) {
                    userHobbyMapper.insert(userId, hobbyId);
                }
            }
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
        // 2.1 当 personalities 未提供时，根据代码字段进行映射插入
        else {
            Map<String, String> personalityCodeToName = Map.ofEntries(
                Map.entry("extroverted", "外向型（社交充电）"),
                Map.entry("introverted", "内向型（独处充电）"),
                Map.entry("ambivert", "中间型（看情况）"),
                Map.entry("rational", "理性型（逻辑优先）"),
                Map.entry("emotional", "感性型（感受优先）"),
                Map.entry("balanced", "平衡型"),
                Map.entry("planned", "计划型（凡事按规划）"),
                Map.entry("casual", "随性型（走一步看一步）"),
                Map.entry("flexible", "弹性型"),
                Map.entry("direct", "直接坦率型"),
                Map.entry("tactful", "委婉体贴型"),
                Map.entry("humorous", "幽默风趣型"),
                Map.entry("listening", "倾听为主型"),
                Map.entry("silent", "偶尔沉默型"),
                Map.entry("warm_talkative", "热情健谈"),
                Map.entry("calm_reserved", "沉稳内敛"),
                Map.entry("same_frequency", "同频即可"),
                Map.entry("meticulous", "严谨细致"),
                Map.entry("efficient", "高效行动"),
                Map.entry("steady", "踏实靠谱"),
                Map.entry("optimistic_positive", "乐观积极"),
                Map.entry("calm_rational", "冷静理智"),
                Map.entry("empathic_sensitive", "敏感共情"),
                Map.entry("stable", "情绪稳定")
            );
            List<String> codes = new ArrayList<>();
            if (request.getSocialEnergy() != null) codes.add(request.getSocialEnergy());
            if (request.getDecisionMaking() != null) codes.add(request.getDecisionMaking());
            if (request.getLifeRhythm() != null) codes.add(request.getLifeRhythm());
            if (request.getCommunicationStyle() != null) codes.add(request.getCommunicationStyle());
            if (request.getPreferredSocialStyle() != null) codes.add(request.getPreferredSocialStyle());
            if (request.getPreferredLifestyle() != null) codes.add(request.getPreferredLifestyle());
            if (request.getPreferredInterests() != null) codes.add(request.getPreferredInterests());
            for (String code : codes) {
                String name = personalityCodeToName.get(code);
                if (name == null) {
                    continue;
                }
                Integer optionId = personalityTraitOptionMapper.selectOptionIdByName(name);
                if (optionId != null) {
                    userPersonalityMapper.insert(userId, optionId);
                }
            }
        }

        // 3. 保存用户关系品质（先删除旧的，再插入新的）
        userRelationshipQualityMapper.deleteByUserId(userId);
        if (request.getRelationshipQualities() != null && !request.getRelationshipQualities().isEmpty()) {
            for (PostCreateRequest.QuestionnaireRequest.RelationshipQualityRequest quality : request.getRelationshipQualities()) {
                Integer qid = quality.getQualityId();
                if (qid == null && quality.getQualityName() != null) {
                    qid = relationshipQualityDefMapper.selectIdByName(quality.getQualityName());
                }
                if (qid != null) {
                    userRelationshipQualityMapper.insert(userId, qid);
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
        
        // 7. 仅在最终提交时写入问卷完成记录表（累计提交次数）
        if (finalSubmission) {
            userQuestionnaireCompletionMapper.upsertOnSubmit(userId);
        }
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
                String name = relationshipQualityDefMapper.selectNameById(selection.getQualityId());
                quality.setQualityName(name);
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

    @Override
    public List<Map<String, Object>> listCompletedUsers(Integer page, Integer size) {
        int p = (page == null || page < 1) ? 1 : page;
        int s = (size == null || size < 1) ? 10 : size;
        int offset = (p - 1) * s;
        return userQuestionnaireCompletionMapper.selectCompletedUsers(offset, s);
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

