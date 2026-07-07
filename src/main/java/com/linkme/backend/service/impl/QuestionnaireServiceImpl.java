package com.linkme.backend.service.impl;

import com.linkme.backend.controller.dto.PostCreateRequest;
import com.linkme.backend.entity.UserMatchingPreference;
import com.linkme.backend.entity.UserPersonalitySelection;
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
 * 问卷服务实现类（精简版）
 *
 * 职责：
 * - 保存/更新精简问卷数据：兴趣、自身性格 3 项、年龄/距离偏好、额外要求
 * - 仅在 finalSubmission=true 时标记 matching_questionnaire_completed=TRUE
 * - 不再写入：ideal 性格、关系品质、关系模式、沟通期待、必须/优先维度
 *
 * @author riki
 * @version 2.0
 */
@Service
public class QuestionnaireServiceImpl implements QuestionnaireService {

    @Autowired
    private UserMatchingPreferenceMapper userMatchingPreferenceMapper;

    @Autowired
    private UserPersonalityMapper userPersonalityMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserQuestionnaireCompletionMapper userQuestionnaireCompletionMapper;

    @Autowired
    private UserHobbyMapper userHobbyMapper;

    @Autowired
    private PersonalityTraitOptionMapper personalityTraitOptionMapper;

    // 兴趣代码 → 中文名映射（与 test_base.sql 中的 hobby 表一致）
    private static final Map<String, String> HOBBY_CODE_TO_NAME = Map.ofEntries(
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
        Map.entry("petting", "撸猫撸狗"),
        Map.entry("city_walk", "city walk")
    );

    // 中文名 → 兴趣代码映射（反向）
    private static final Map<String, String> HOBBY_NAME_TO_CODE;
    static {
        Map<String, String> m = new java.util.HashMap<>();
        for (Map.Entry<String, String> e : HOBBY_CODE_TO_NAME.entrySet()) {
            m.put(e.getValue(), e.getKey());
        }
        HOBBY_NAME_TO_CODE = m;
    }

    // 自身性格代码 → 中文名映射（仅保留 3 个维度）
    private static final Map<String, String> PERSONALITY_CODE_TO_NAME = Map.ofEntries(
        Map.entry("extroverted", "外向型（社交充电）"),
        Map.entry("introverted", "内向型（独处充电）"),
        Map.entry("ambivert", "中间型（看情况）"),
        Map.entry("rational", "理性型（逻辑优先）"),
        Map.entry("emotional", "感性型（感受优先）"),
        Map.entry("balanced", "平衡型"),
        Map.entry("planned", "计划型（凡事按规划）"),
        Map.entry("casual", "随性型（走一步看一步）"),
        Map.entry("flexible", "弹性型")
    );

    // 自身性格中文名 → 代码映射（反向）
    private static final Map<String, String> PERSONALITY_NAME_TO_CODE;
    static {
        Map<String, String> m = new java.util.HashMap<>();
        for (Map.Entry<String, String> e : PERSONALITY_CODE_TO_NAME.entrySet()) {
            m.put(e.getValue(), e.getKey());
        }
        PERSONALITY_NAME_TO_CODE = m;
    }

    /**
     * 保存或更新问卷
     */
    @Override
    @Transactional
    public void saveOrUpdateQuestionnaire(Integer userId, PostCreateRequest.QuestionnaireRequest request, boolean finalSubmission) {
        if (userId == null || request == null) {
            throw new IllegalArgumentException("用户ID和问卷数据不能为空");
        }

        // 1. 保存或更新用户匹配偏好（仅年龄/距离/额外要求）
        UserMatchingPreference preference = userMatchingPreferenceMapper.selectByUserId(userId);
        if (preference == null) {
            preference = new UserMatchingPreference();
            preference.setUserId(userId);
            preference.setAgeMin(request.getAgeMin());
            preference.setAgeMax(request.getAgeMax());
            preference.setAgeUnlimited(request.getAgeUnlimited());
            preference.setDistancePreference(request.getDistancePreference());
            preference.setAdditionalRequirements(request.getAdditionalRequirements());
            preference.setCreatedAt(LocalDateTime.now());
            preference.setUpdatedAt(LocalDateTime.now());
            userMatchingPreferenceMapper.insert(preference);
        } else {
            preference.setAgeMin(request.getAgeMin());
            preference.setAgeMax(request.getAgeMax());
            preference.setAgeUnlimited(request.getAgeUnlimited());
            preference.setDistancePreference(request.getDistancePreference());
            preference.setAdditionalRequirements(request.getAdditionalRequirements());
            preference.setUpdatedAt(LocalDateTime.now());
            userMatchingPreferenceMapper.update(preference);
        }

        // 2. 保存用户爱好（先删除旧的，再插入新的）
        userHobbyMapper.deleteByUserId(userId);
        if (request.getInterests() != null && !request.getInterests().isEmpty()) {
            for (String code : request.getInterests()) {
                String name = HOBBY_CODE_TO_NAME.get(code);
                if (name == null) {
                    continue;
                }
                Integer hobbyId = userHobbyMapper.selectHobbyIdByName(name);
                if (hobbyId != null) {
                    userHobbyMapper.insert(userId, hobbyId);
                }
            }
        }

        // 3. 保存用户自身性格特质（先删除旧的，再插入新的）
        userPersonalityMapper.deleteByUserId(userId);
        List<String> personalityCodes = new ArrayList<>();
        if (request.getSocialEnergy() != null) personalityCodes.add(request.getSocialEnergy());
        if (request.getDecisionMaking() != null) personalityCodes.add(request.getDecisionMaking());
        if (request.getLifeRhythm() != null) personalityCodes.add(request.getLifeRhythm());
        for (String code : personalityCodes) {
            String name = PERSONALITY_CODE_TO_NAME.get(code);
            if (name == null) {
                continue;
            }
            Integer optionId = personalityTraitOptionMapper.selectOptionIdByName(name);
            if (optionId != null) {
                userPersonalityMapper.insert(userId, optionId);
            }
        }

        // 4. 仅在最终提交时标记问卷完成
        if (finalSubmission) {
            userMapper.updateQuestionnaireCompleted(userId, true);
            userQuestionnaireCompletionMapper.upsertOnSubmit(userId);
        }
    }

    /**
     * 根据用户ID获取问卷数据
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
        response.setAdditionalRequirements(preference.getAdditionalRequirements());

        // 2. 查询用户性格特质（仅 self 类型）
        List<UserPersonalitySelection> personalitySelections = userPersonalityMapper.selectSelectionsByUserId(userId);
        if (personalitySelections != null && !personalitySelections.isEmpty()) {
            for (UserPersonalitySelection sel : personalitySelections) {
                String name = sel.getOptionName();
                if (name == null) continue;
                String code = PERSONALITY_NAME_TO_CODE.get(name);
                if (code == null) continue;

                // 根据 categoryName 分配到对应字段
                String categoryName = sel.getCategoryName();
                if ("社交能量来源".equals(categoryName)) {
                    response.setSocialEnergy(code);
                } else if ("决策方式".equals(categoryName)) {
                    response.setDecisionMaking(code);
                } else if ("生活节奏".equals(categoryName)) {
                    response.setLifeRhythm(code);
                }
            }
        }

        // 3. 查询用户爱好
        List<com.linkme.backend.entity.Hobby> hobbies = userHobbyMapper.selectHobbiesByUserId(userId);
        if (hobbies != null && !hobbies.isEmpty()) {
            List<String> codes = new ArrayList<>();
            for (com.linkme.backend.entity.Hobby h : hobbies) {
                if (h != null && h.getName() != null) {
                    String code = HOBBY_NAME_TO_CODE.get(h.getName());
                    if (code != null) {
                        codes.add(code);
                    }
                }
            }
            response.setInterests(codes);
        }

        return response;
    }

    @Override
    public PostCreateRequest.QuestionnaireResponse getPublicQuestionnaireByUserId(Integer userId) {
        if (userId == null) {
            return null;
        }

        // 检查用户是否已完成问卷
        UserMatchingPreference preference = userMatchingPreferenceMapper.selectByUserId(userId);
        if (preference == null) {
            return null;
        }

        PostCreateRequest.QuestionnaireResponse response = new PostCreateRequest.QuestionnaireResponse();
        response.setUserId(userId);

        // 只返回公开信息：兴趣爱好
        List<com.linkme.backend.entity.Hobby> hobbies = userHobbyMapper.selectHobbiesByUserId(userId);
        if (hobbies != null && !hobbies.isEmpty()) {
            List<String> codes = new ArrayList<>();
            for (com.linkme.backend.entity.Hobby h : hobbies) {
                if (h != null && h.getName() != null) {
                    String code = HOBBY_NAME_TO_CODE.get(h.getName());
                    if (code != null) {
                        codes.add(code);
                    }
                }
            }
            response.setInterests(codes);
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
}
