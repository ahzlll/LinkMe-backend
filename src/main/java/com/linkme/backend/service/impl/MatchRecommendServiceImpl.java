package com.linkme.backend.service.impl;

import com.linkme.backend.controller.dto.MatchRecommendationResponse;
import com.linkme.backend.entity.Hobby;
import com.linkme.backend.entity.User;
import com.linkme.backend.entity.UserMatchingPreference;
import com.linkme.backend.entity.UserPersonalitySelection;
import com.linkme.backend.mapper.UserHobbyMapper;
import com.linkme.backend.mapper.UserMapper;
import com.linkme.backend.mapper.UserMatchingPreferenceMapper;
import com.linkme.backend.mapper.UserPersonalityMapper;
import com.linkme.backend.service.MatchRecommendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 匹配推荐服务实现
 *
 * 计算逻辑（精简版）：
 * - 冷启动基础分 50（总是计算）
 *   - 同城: region 相同 +25
 *   - 年龄差: 0岁 +25; 1-3岁 +20; 4-7岁 +12; 8-12岁 +5; 13+ +0
 * - 问卷加分（仅当前用户已完成问卷时叠加）
 *   - 兴趣重合: 每项 +1，运动户外类 +2
 *   - 自身性格: self-self 同维度同选项，每维 +2（最多 3 维 = +6）
 *   - 年龄偏好: 超出问卷 min/max 扣 10
 *   - 距离偏好: 选 same_city 且不同 region 扣 8
 *
 * @author riki
 * @version 2.0
 */
@Service
public class MatchRecommendServiceImpl implements MatchRecommendService {

    // 冷启动基础分
    private static final int BASE_SCORE = 50;
    private static final int SAME_CITY_BONUS = 25;
    private static final int AGE_DIFF_0_BONUS = 25;
    private static final int AGE_DIFF_1_3_BONUS = 20;
    private static final int AGE_DIFF_4_7_BONUS = 12;
    private static final int AGE_DIFF_8_12_BONUS = 5;

    // 问卷加分/扣分
    private static final int AGE_OUT_OF_RANGE_PENALTY = 10;
    private static final int SAME_CITY_PENALTY = 8;
    private static final int PERSONALITY_SELF_MATCH_BONUS = 2;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserHobbyMapper userHobbyMapper;

    @Autowired
    private UserMatchingPreferenceMapper userMatchingPreferenceMapper;

    @Autowired
    private UserPersonalityMapper userPersonalityMapper;

    @Override
    public List<MatchRecommendationResponse> getRecommendations(Integer currentUserId, Integer page, Integer size) {
        if (currentUserId == null) {
            return Collections.emptyList();
        }

        int safePage = page == null || page < 1 ? 1 : page;
        int safeSize = size == null || size < 1 ? 20 : Math.min(size, 100);

        int candidateFetchSize = Math.max(safeSize * 5, 100);
        int offset = (safePage - 1) * safeSize;

        User currentUser = userMapper.selectById(currentUserId);
        if (currentUser == null) {
            return Collections.emptyList();
        }

        // 判断当前用户是否已完成问卷
        boolean questionnaireCompleted = Boolean.TRUE.equals(currentUser.getMatchingQuestionnaireCompleted());

        // 获取当前用户的匹配偏好（问卷加分时使用）
        UserMatchingPreference preference = questionnaireCompleted
                ? userMatchingPreferenceMapper.selectByUserId(currentUserId)
                : null;

        List<User> candidates = userMapper.selectMatchCandidates(currentUserId, 0, candidateFetchSize);
        if (candidates == null || candidates.isEmpty()) {
            return Collections.emptyList();
        }

        List<Integer> candidateUserIds = candidates.stream()
                .map(User::getUserId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        if (candidateUserIds.isEmpty()) {
            return Collections.emptyList();
        }

        // 当前用户的兴趣 Map（问卷加分时使用）
        List<Hobby> currentHobbies = questionnaireCompleted
                ? userHobbyMapper.selectHobbiesByUserId(currentUserId)
                : Collections.emptyList();
        Map<Integer, Hobby> currentHobbyMap = currentHobbies == null ? new HashMap<>() : currentHobbies.stream()
                .filter(h -> h.getHobbyId() != null)
                .collect(Collectors.toMap(Hobby::getHobbyId, h -> h, (a, b) -> a));

        // 批量获取候选人的匹配偏好（问卷加分中的年龄偏好需双向检查）
        Map<Integer, UserMatchingPreference> candidatePreferenceMap = new HashMap<>();
        if (questionnaireCompleted && !candidateUserIds.isEmpty()) {
            List<UserMatchingPreference> candidatePreferences = userMatchingPreferenceMapper.selectByUserIds(candidateUserIds);
            if (candidatePreferences != null) {
                for (UserMatchingPreference p : candidatePreferences) {
                    if (p != null && p.getUserId() != null) {
                        candidatePreferenceMap.put(p.getUserId(), p);
                    }
                }
            }
        }

        // 批量获取性格特质（self 类型，用于问卷加分）
        Map<Integer, Map<String, String>> selfTraitMapByUserId = new HashMap<>();
        if (questionnaireCompleted) {
            List<Integer> allUserIds = new ArrayList<>(candidateUserIds.size() + 1);
            allUserIds.add(currentUserId);
            allUserIds.addAll(candidateUserIds);

            List<UserPersonalitySelection> personalitySelections = userPersonalityMapper.selectSelectionsByUserIds(allUserIds);
            if (personalitySelections != null) {
                for (UserPersonalitySelection s : personalitySelections) {
                    if (s == null || s.getUserId() == null || s.getCategoryName() == null || s.getOptionName() == null) {
                        continue;
                    }
                    if ("self".equalsIgnoreCase(s.getTraitType())) {
                        selfTraitMapByUserId
                                .computeIfAbsent(s.getUserId(), k -> new HashMap<>())
                                .put(s.getCategoryName(), s.getOptionName());
                    }
                }
            }
        }

        Map<String, String> currentSelf = selfTraitMapByUserId.getOrDefault(currentUserId, Collections.emptyMap());

        // 计算每个候选人的分数
        List<ScoredUser> scored = new ArrayList<>(candidates.size());
        for (User candidate : candidates) {
            // 冷启动基础分（总是计算）
            int score = calculateColdStartScore(currentUser, candidate);

            // 问卷加分（仅当前用户已完成问卷时叠加）
            if (questionnaireCompleted) {
                UserMatchingPreference candidatePreference = candidatePreferenceMap.get(candidate.getUserId());
                Map<String, String> candidateSelf = selfTraitMapByUserId.getOrDefault(candidate.getUserId(), Collections.emptyMap());
                score = applyQuestionnaireBonus(score, currentUser, preference, currentHobbyMap,
                        candidate, candidatePreference, currentSelf, candidateSelf);
            }

            scored.add(new ScoredUser(candidate, score));
        }

        // 按分数降序排序，同分按 userId 升序
        scored.sort(Comparator.comparingInt(ScoredUser::score).reversed().thenComparingInt(su -> su.user().getUserId()));

        // 分页
        int fromIndex = Math.min(offset, scored.size());
        int toIndex = Math.min(offset + safeSize, scored.size());
        List<ScoredUser> pageItems = scored.subList(fromIndex, toIndex);

        List<MatchRecommendationResponse> result = new ArrayList<>(pageItems.size());
        for (ScoredUser su : pageItems) {
            result.add(toResponse(su.user(), su.score()));
        }
        return result;
    }

    /**
     * 冷启动基础分（总是计算，不依赖问卷）
     */
    private int calculateColdStartScore(User currentUser, User candidate) {
        int score = BASE_SCORE; // 50

        // 同城加分
        String a = normalizeRegion(currentUser.getRegion());
        String b = normalizeRegion(candidate == null ? null : candidate.getRegion());
        if (a != null && b != null && a.equalsIgnoreCase(b)) {
            score += SAME_CITY_BONUS; // +25
        }

        // 年龄差加分
        Integer currentAge = calculateAge(currentUser.getBirthday());
        Integer candidateAge = calculateAge(candidate == null ? null : candidate.getBirthday());
        if (currentAge != null && candidateAge != null) {
            int diff = Math.abs(currentAge - candidateAge);
            if (diff == 0) {
                score += AGE_DIFF_0_BONUS;       // +25
            } else if (diff <= 3) {
                score += AGE_DIFF_1_3_BONUS;    // +20
            } else if (diff <= 7) {
                score += AGE_DIFF_4_7_BONUS;    // +12
            } else if (diff <= 12) {
                score += AGE_DIFF_8_12_BONUS;   // +5
            }
            // 13+ 不加分
        }

        return score;
    }

    /**
     * 问卷加分（仅当前用户已完成问卷时叠加）
     */
    private int applyQuestionnaireBonus(int score,
                                        User currentUser,
                                        UserMatchingPreference preference,
                                        Map<Integer, Hobby> currentHobbyMap,
                                        User candidate,
                                        UserMatchingPreference candidatePreference,
                                        Map<String, String> currentSelf,
                                        Map<String, String> candidateSelf) {
        Integer candidateUserId = candidate == null ? null : candidate.getUserId();
        if (candidateUserId == null) {
            return clamp(score, 0, 100);
        }

        // 1. 兴趣重合：每项 +1，运动户外类(category_id=3) +2
        List<Hobby> candidateHobbies = userHobbyMapper.selectHobbiesByUserId(candidateUserId);
        if (candidateHobbies != null && !candidateHobbies.isEmpty() && currentHobbyMap != null && !currentHobbyMap.isEmpty()) {
            for (Hobby h : candidateHobbies) {
                if (h == null || h.getHobbyId() == null) {
                    continue;
                }
                if (currentHobbyMap.containsKey(h.getHobbyId())) {
                    if (h.getCategoryId() != null && h.getCategoryId() == 3) {
                        score += 2;
                    } else {
                        score += 1;
                    }
                }
            }
        }

        // 2. 自身性格匹配：self-self 同维度同选项，每维 +2（最多 3 维 = +6）
        if (!currentSelf.isEmpty() && !candidateSelf.isEmpty()) {
            for (Map.Entry<String, String> e : currentSelf.entrySet()) {
                String cat = e.getKey();
                String val = e.getValue();
                if (cat != null && val != null && val.equals(candidateSelf.get(cat))) {
                    score += PERSONALITY_SELF_MATCH_BONUS;
                }
            }
        }

        // 3. 年龄偏好：候选人年龄超出当前用户问卷 min/max → -10
        Integer candidateAge = calculateAge(candidate.getBirthday());
        if (candidateAge != null && preference != null && Boolean.FALSE.equals(preference.getAgeUnlimited())) {
            Integer min = preference.getAgeMin();
            Integer max = preference.getAgeMax();
            if (min != null && max != null) {
                if (candidateAge < min || candidateAge > max) {
                    score -= AGE_OUT_OF_RANGE_PENALTY;
                }
            }
        }

        // 4. 距离偏好：当前用户选 same_city 且不同 region → -8
        if (preference != null && "same_city".equals(preference.getDistancePreference())) {
            String a = normalizeRegion(currentUser.getRegion());
            String b = normalizeRegion(candidate.getRegion());
            if (a != null && b != null && !a.equalsIgnoreCase(b)) {
                score -= SAME_CITY_PENALTY;
            }
        }

        return clamp(score, 0, 100);
    }

    /**
     * 转为对外返回 DTO（避免返回敏感字段）
     */
    private MatchRecommendationResponse toResponse(User user, int score) {
        MatchRecommendationResponse r = new MatchRecommendationResponse();
        if (user != null) {
            r.setUserId(user.getUserId());
            r.setNickname(user.getNickname());
            r.setGender(user.getGender());
            r.setBirthday(user.getBirthday());
            r.setRegion(user.getRegion());
            r.setAvatarUrl(user.getAvatarUrl());
            r.setBio(user.getBio());
            // 聚合兴趣编码列表
            try {
                List<Hobby> hobbies = userHobbyMapper.selectHobbiesByUserId(user.getUserId());
                if (hobbies != null && !hobbies.isEmpty()) {
                    Map<String, String> nameToCode = Map.ofEntries(
                        Map.entry("绘画", "art"),
                        Map.entry("摄影", "photography"),
                        Map.entry("书法", "calligraphy"),
                        Map.entry("写作", "writing"),
                        Map.entry("歌唱", "singing"),
                        Map.entry("舞蹈", "dance"),
                        Map.entry("戏剧", "theater"),
                        Map.entry("乐器演奏", "instrument"),
                        Map.entry("平面设计", "graphic_design"),
                        Map.entry("视频剪辑", "video_editing"),
                        Map.entry("阅读", "reading"),
                        Map.entry("编程", "programming"),
                        Map.entry("教学", "teaching"),
                        Map.entry("心理学", "psychology"),
                        Map.entry("语言学习", "language_learning"),
                        Map.entry("哲学思考", "philosophy"),
                        Map.entry("历史研究", "history_research"),
                        Map.entry("投资理财", "investment"),
                        Map.entry("公开演讲", "public_speaking"),
                        Map.entry("创业项目", "entrepreneurship"),
                        Map.entry("跑步", "running"),
                        Map.entry("健身", "fitness"),
                        Map.entry("游泳", "swimming"),
                        Map.entry("骑行", "cycling"),
                        Map.entry("钓鱼", "fishing"),
                        Map.entry("瑜伽", "yoga"),
                        Map.entry("露营", "camping"),
                        Map.entry("武术", "martial_arts"),
                        Map.entry("登山", "mountaineering"),
                        Map.entry("攀岩", "climbing"),
                        Map.entry("飞盘", "frisbee"),
                        Map.entry("球类运动", "team_sports"),
                        Map.entry("桌游", "board_games"),
                        Map.entry("棋牌", "card_games"),
                        Map.entry("魔术", "magic"),
                        Map.entry("收藏", "collecting"),
                        Map.entry("追剧", "tv_shows"),
                        Map.entry("看电影", "movies"),
                        Map.entry("听音乐", "music"),
                        Map.entry("剧本杀", "script_killing"),
                        Map.entry("密室逃脱", "escape_room"),
                        Map.entry("电子游戏", "gaming"),
                        Map.entry("烹饪/烘焙", "cooking_baking"),
                        Map.entry("咖啡/茶艺/调酒", "coffee_tea_mixology"),
                        Map.entry("手工 DIY", "handicraft_diy"),
                        Map.entry("缝纫", "sewing"),
                        Map.entry("家居装饰", "home_decoration"),
                        Map.entry("收纳整理", "organizing"),
                        Map.entry("花艺绿植", "floristry_gardening"),
                        Map.entry("旅行", "travel"),
                        Map.entry("观鸟", "bird_watching"),
                        Map.entry("音乐节", "music_festival"),
                        Map.entry("演唱会", "concert"),
                        Map.entry("探店打卡", "restaurant_hopping"),
                        Map.entry("展览打卡", "exhibition"),
                        Map.entry("天文观测", "astronomy"),
                        Map.entry("公益志愿", "volunteering"),
                        Map.entry("撸猫撸狗", "petting"),
                        Map.entry("city walk", "city_walk")
                    );
                    List<String> codes = new ArrayList<>();
                    for (Hobby h : hobbies) {
                        if (h != null && h.getName() != null) {
                            String code = nameToCode.get(h.getName());
                            if (code != null) {
                                codes.add(code);
                            }
                        }
                    }
                    r.setInterests(codes);
                }
            } catch (Exception ignored) {
            }
        }
        r.setMatchScore(score);
        return r;
    }

    /**
     * 根据生日计算年龄（年）
     */
    private Integer calculateAge(LocalDate birthday) {
        if (birthday == null) {
            return null;
        }
        LocalDate now = LocalDate.now();
        if (birthday.isAfter(now)) {
            return null;
        }
        return Period.between(birthday, now).getYears();
    }

    private String normalizeRegion(String region) {
        if (region == null) {
            return null;
        }
        String r = region.trim();
        return r.isEmpty() ? null : r;
    }

    private int clamp(int v, int min, int max) {
        if (v < min) {
            return min;
        }
        if (v > max) {
            return max;
        }
        return v;
    }

    private record ScoredUser(User user, int score) {
    }
}
