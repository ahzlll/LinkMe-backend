package com.linkme.backend.service.impl;

import com.linkme.backend.controller.dto.MatchRecommendationResponse;
import com.linkme.backend.entity.DimensionPrioritySelection;
import com.linkme.backend.entity.Hobby;
import com.linkme.backend.entity.User;
import com.linkme.backend.entity.UserMatchingPreference;
import com.linkme.backend.entity.UserPersonalitySelection;
import com.linkme.backend.entity.UserRelationshipQualitySelection;
import com.linkme.backend.mapper.UserHobbyMapper;
import com.linkme.backend.mapper.UserMatchingDimensionMapper;
import com.linkme.backend.mapper.UserMapper;
import com.linkme.backend.mapper.UserMatchingPreferenceMapper;
import com.linkme.backend.mapper.UserPersonalityMapper;
import com.linkme.backend.mapper.UserRelationshipQualityMapper;
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
 * 计算逻辑（当前版本）：
 * - 基础分 80
 * - 年龄不在偏好范围内：-10
 * - 同城优先且不同地区：-5
 * - 爱好重合：运动户外类 +2，其余 +1
 *
 * @author riki
 * @version 1.0
 */
@Service
public class MatchRecommendServiceImpl implements MatchRecommendService {

    private static final int BASE_SCORE = 80;
    private static final int AGE_OUT_OF_RANGE_PENALTY = 10;
    private static final int SAME_CITY_PENALTY = 5;
    private static final int MUST_NOT_SATISFIED_PENALTY = 10;
    private static final int RELATIONSHIP_MODE_MATCH_BONUS = 5;
    private static final int COMMUNICATION_EXPECTATION_MATCH_BONUS = 3;
    private static final int RELATIONSHIP_QUALITY_MATCH_BONUS_PER_ITEM = 2;
    private static final int RELATIONSHIP_QUALITY_MATCH_BONUS_MAX = 6;
    private static final int PERSONALITY_SELF_MATCH_BONUS_PER_ITEM = 1;
    private static final int PERSONALITY_IDEAL_MATCH_BONUS_PER_ITEM = 2;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserHobbyMapper userHobbyMapper;

    @Autowired
    private UserMatchingPreferenceMapper userMatchingPreferenceMapper;

    @Autowired
    private UserPersonalityMapper userPersonalityMapper;

    @Autowired
    private UserRelationshipQualityMapper userRelationshipQualityMapper;

    @Autowired
    private UserMatchingDimensionMapper userMatchingDimensionMapper;

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
        UserMatchingPreference preference = userMatchingPreferenceMapper.selectByUserId(currentUserId);

        List<String> mustDimensionCodes = userMatchingDimensionMapper.selectMustDimensionCodesByUserId(currentUserId);
        if (mustDimensionCodes == null) {
            mustDimensionCodes = Collections.emptyList();
        }
        List<DimensionPrioritySelection> priorityDimensions = userMatchingDimensionMapper.selectPriorityDimensionsByUserId(currentUserId);
        if (priorityDimensions == null) {
            priorityDimensions = Collections.emptyList();
        }

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

        Map<Integer, UserMatchingPreference> candidatePreferenceMap = new HashMap<>();
        if (!candidateUserIds.isEmpty()) {
            List<UserMatchingPreference> candidatePreferences = userMatchingPreferenceMapper.selectByUserIds(candidateUserIds);
            if (candidatePreferences != null) {
                for (UserMatchingPreference p : candidatePreferences) {
                    if (p != null && p.getUserId() != null) {
                        candidatePreferenceMap.put(p.getUserId(), p);
                    }
                }
            }
        }

        List<Integer> allUserIds = new ArrayList<>(candidateUserIds.size() + 1);
        allUserIds.add(currentUserId);
        allUserIds.addAll(candidateUserIds);

        Map<Integer, Map<String, String>> selfTraitMapByUserId = new HashMap<>();
        Map<Integer, Map<String, String>> idealTraitMapByUserId = new HashMap<>();
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
                } else if ("ideal".equalsIgnoreCase(s.getTraitType())) {
                    idealTraitMapByUserId
                            .computeIfAbsent(s.getUserId(), k -> new HashMap<>())
                            .put(s.getCategoryName(), s.getOptionName());
                }
            }
        }

        Map<Integer, Set<Integer>> relationshipQualityIdSetByUserId = new HashMap<>();
        List<UserRelationshipQualitySelection> qualitySelections = userRelationshipQualityMapper.selectByUserIds(allUserIds);
        if (qualitySelections != null) {
            for (UserRelationshipQualitySelection q : qualitySelections) {
                if (q == null || q.getUserId() == null || q.getQualityId() == null) {
                    continue;
                }
                relationshipQualityIdSetByUserId
                        .computeIfAbsent(q.getUserId(), k -> new HashSet<>())
                        .add(q.getQualityId());
            }
        }

        List<Hobby> currentHobbies = userHobbyMapper.selectHobbiesByUserId(currentUserId);
        Map<Integer, Hobby> currentHobbyMap = currentHobbies == null ? new HashMap<>() : currentHobbies.stream()
                .filter(h -> h.getHobbyId() != null)
                .collect(Collectors.toMap(Hobby::getHobbyId, h -> h, (a, b) -> a));

        List<ScoredUser> scored = new ArrayList<>(candidates.size());
        for (User candidate : candidates) {
            UserMatchingPreference candidatePreference = candidate == null ? null : candidatePreferenceMap.get(candidate.getUserId());
            int score = calculateScore(currentUser, preference, currentHobbyMap, candidate);
            score = applyPersonalityAndPreferenceScore(score,
                    currentUser,
                    preference,
                    currentHobbyMap,
                    candidate,
                    candidatePreference,
                    selfTraitMapByUserId,
                    idealTraitMapByUserId,
                    relationshipQualityIdSetByUserId,
                    mustDimensionCodes,
                    priorityDimensions);
            scored.add(new ScoredUser(candidate, score));
        }

        scored.sort(Comparator.comparingInt(ScoredUser::score).reversed().thenComparingInt(su -> su.user().getUserId()));

        int fromIndex = Math.min(offset, scored.size());
        int toIndex = Math.min(offset + safeSize, scored.size());
        List<ScoredUser> pageItems = scored.subList(fromIndex, toIndex);

        List<MatchRecommendationResponse> result = new ArrayList<>(pageItems.size());
        for (ScoredUser su : pageItems) {
            result.add(toResponse(su.user(), su.score()));
        }
        return result;
    }

    private int applyPersonalityAndPreferenceScore(int baseScore,
                                                   User currentUser,
                                                   UserMatchingPreference preference,
                                                   Map<Integer, Hobby> currentHobbyMap,
                                                   User candidate,
                                                   UserMatchingPreference candidatePreference,
                                                   Map<Integer, Map<String, String>> selfTraitMapByUserId,
                                                   Map<Integer, Map<String, String>> idealTraitMapByUserId,
                                                   Map<Integer, Set<Integer>> relationshipQualityIdSetByUserId,
                                                   List<String> mustDimensionCodes,
                                                   List<DimensionPrioritySelection> priorityDimensions) {
        int score = baseScore;

        Integer candidateUserId = candidate == null ? null : candidate.getUserId();
        if (candidateUserId == null) {
            return clamp(score, 0, 100);
        }

        // 计算兴趣重合数（用于 must/priority）
        int overlapCount = 0;
        List<Hobby> candidateHobbies = userHobbyMapper.selectHobbiesByUserId(candidateUserId);
        if (candidateHobbies != null && currentHobbyMap != null && !currentHobbyMap.isEmpty()) {
            for (Hobby h : candidateHobbies) {
                if (h != null && h.getHobbyId() != null && currentHobbyMap.containsKey(h.getHobbyId())) {
                    overlapCount++;
                }
            }
        }

        // 关系品质（多选）交集
        Set<Integer> currentQualities = relationshipQualityIdSetByUserId.getOrDefault(currentUser.getUserId(), Collections.emptySet());
        Set<Integer> candidateQualities = relationshipQualityIdSetByUserId.getOrDefault(candidateUserId, Collections.emptySet());
        if (!currentQualities.isEmpty() && !candidateQualities.isEmpty()) {
            int qualityOverlap = 0;
            for (Integer q : currentQualities) {
                if (candidateQualities.contains(q)) {
                    qualityOverlap++;
                }
            }
            int qualityBonus = Math.min(qualityOverlap * RELATIONSHIP_QUALITY_MATCH_BONUS_PER_ITEM, RELATIONSHIP_QUALITY_MATCH_BONUS_MAX);
            score += qualityBonus;
        }

        // 关系模式一致
        boolean relationshipModeMatched = preference != null && candidatePreference != null
                && preference.getRelationshipModeId() != null
                && preference.getRelationshipModeId().equals(candidatePreference.getRelationshipModeId());
        if (relationshipModeMatched) {
            score += RELATIONSHIP_MODE_MATCH_BONUS;
        }

        // 沟通期待一致
        boolean communicationExpectationMatched = preference != null && candidatePreference != null
                && preference.getCommunicationExpectationId() != null
                && preference.getCommunicationExpectationId().equals(candidatePreference.getCommunicationExpectationId());
        if (communicationExpectationMatched) {
            score += COMMUNICATION_EXPECTATION_MATCH_BONUS;
        }

        // 性格特质匹配（self-self + ideal-self）
        int personalityScore = 0;
        Map<String, String> currentSelf = selfTraitMapByUserId.getOrDefault(currentUser.getUserId(), Collections.emptyMap());
        Map<String, String> candidateSelf = selfTraitMapByUserId.getOrDefault(candidateUserId, Collections.emptyMap());
        if (!currentSelf.isEmpty() && !candidateSelf.isEmpty()) {
            for (Map.Entry<String, String> e : currentSelf.entrySet()) {
                String cat = e.getKey();
                String val = e.getValue();
                if (cat != null && val != null && val.equals(candidateSelf.get(cat))) {
                    personalityScore += PERSONALITY_SELF_MATCH_BONUS_PER_ITEM;
                }
            }
        }

        Map<String, String> currentIdeal = idealTraitMapByUserId.getOrDefault(currentUser.getUserId(), Collections.emptyMap());
        Map<String, String> candidateIdeal = idealTraitMapByUserId.getOrDefault(candidateUserId, Collections.emptyMap());
        personalityScore += calculateIdealToSelfScore(currentIdeal, candidateSelf);
        personalityScore += calculateIdealToSelfScore(candidateIdeal, currentSelf);

        score += personalityScore;

        // must 扣分（策略A：不满足扣分）
        Set<String> mustCodes = mustDimensionCodes == null ? Collections.emptySet() : new HashSet<>(mustDimensionCodes);

        // age_range（你原规则已经扣过一次，这里只补充“候选人的年龄偏好”方向）
        if (mustCodes.contains("age_range")) {
            boolean ageSatisfied = isAgeSatisfiedMutual(currentUser, candidate, preference, candidatePreference);
            if (!ageSatisfied) {
                score -= MUST_NOT_SATISFIED_PENALTY;
            }
        }

        // distance
        if (mustCodes.contains("distance")) {
            boolean distanceSatisfied = isDistanceSatisfiedMutual(currentUser, candidate, preference, candidatePreference);
            if (!distanceSatisfied) {
                score -= MUST_NOT_SATISFIED_PENALTY;
            }
        }

        // interest_overlap
        if (mustCodes.contains("interest_overlap")) {
            if (overlapCount <= 0) {
                score -= MUST_NOT_SATISFIED_PENALTY;
            }
        }

        // personality_match
        if (mustCodes.contains("personality_match")) {
            if (personalityScore <= 0) {
                score -= MUST_NOT_SATISFIED_PENALTY;
            }
        }

        // relationship_mode
        if (mustCodes.contains("relationship_mode")) {
            if (!relationshipModeMatched) {
                score -= MUST_NOT_SATISFIED_PENALTY;
            }
        }

        // communication_style（用 self 里的“沟通风格”做匹配）
        if (mustCodes.contains("communication_style")) {
            boolean communicationStyleMatched = isCommunicationStyleMatched(currentSelf, candidateSelf);
            if (!communicationStyleMatched) {
                score -= MUST_NOT_SATISFIED_PENALTY;
            }
        }

        // priority 小加权（最多3项，权重 3/2/1）
        if (priorityDimensions != null && !priorityDimensions.isEmpty()) {
            for (DimensionPrioritySelection p : priorityDimensions) {
                if (p == null || p.getCode() == null) {
                    continue;
                }
                int weight = priorityWeight(p.getPriorityOrder());
                switch (p.getCode()) {
                    case "age_range" -> {
                        if (isAgeSatisfiedMutual(currentUser, candidate, preference, candidatePreference)) {
                            score += weight;
                        }
                    }
                    case "distance" -> {
                        if (isDistanceSatisfiedMutual(currentUser, candidate, preference, candidatePreference)) {
                            score += weight;
                        }
                    }
                    case "interest_overlap" -> {
                        if (overlapCount > 0) {
                            score += weight;
                        }
                    }
                    case "personality_match" -> {
                        if (personalityScore > 0) {
                            score += weight;
                        }
                    }
                    case "relationship_mode" -> {
                        if (relationshipModeMatched) {
                            score += weight;
                        }
                    }
                    case "communication_style" -> {
                        if (isCommunicationStyleMatched(currentSelf, candidateSelf)) {
                            score += weight;
                        }
                    }
                    default -> {
                    }
                }
            }
        }

        return clamp(score, 0, 100);
    }

    /**
     * 计算当前用户对候选用户的匹配度
     */
    private int calculateScore(User currentUser,
                               UserMatchingPreference preference,
                               Map<Integer, Hobby> currentHobbyMap,
                               User candidate) {
        int score = BASE_SCORE;

        // 年龄范围（按当前用户的交友年龄要求）
        Integer candidateAge = calculateAge(candidate == null ? null : candidate.getBirthday());
        if (candidateAge != null && preference != null && Boolean.FALSE.equals(preference.getAgeUnlimited())) {
            Integer min = preference.getAgeMin();
            Integer max = preference.getAgeMax();
            if (min != null && max != null) {
                if (candidateAge < min || candidateAge > max) {
                    score -= AGE_OUT_OF_RANGE_PENALTY;
                }
            }
        }

        // 距离偏好（同城优先：不同地区扣分）
        if (preference != null && "same_city".equals(preference.getDistancePreference())) {
            String a = currentUser == null ? null : normalizeRegion(currentUser.getRegion());
            String b = normalizeRegion(candidate == null ? null : candidate.getRegion());
            if (a != null && b != null && !a.equalsIgnoreCase(b)) {
                score -= SAME_CITY_PENALTY;
            }
        }

        // 爱好重合
        List<Hobby> candidateHobbies = candidate == null ? Collections.emptyList() : userHobbyMapper.selectHobbiesByUserId(candidate.getUserId());
        if (candidateHobbies != null && !candidateHobbies.isEmpty() && currentHobbyMap != null && !currentHobbyMap.isEmpty()) {
            for (Hobby h : candidateHobbies) {
                if (h == null || h.getHobbyId() == null) {
                    continue;
                }
                if (currentHobbyMap.containsKey(h.getHobbyId())) {
                    // 运动户外类 category_id = 3 -> +2，其他 +1
                    if (h.getCategoryId() != null && h.getCategoryId() == 3) {
                        score += 2;
                    } else {
                        score += 1;
                    }
                }
            }
        }

        return clamp(score, 0, 100);
    }

    private int calculateIdealToSelfScore(Map<String, String> idealTraitMap, Map<String, String> selfTraitMap) {
        if (idealTraitMap == null || idealTraitMap.isEmpty() || selfTraitMap == null || selfTraitMap.isEmpty()) {
            return 0;
        }

        int score = 0;

        // 希望对方的社交风格 -> 社交能量来源
        String idealSocial = idealTraitMap.get("希望对方的社交风格");
        String selfEnergy = selfTraitMap.get("社交能量来源");
        if (idealSocial != null && selfEnergy != null && matchesIdealSocialStyle(idealSocial, selfEnergy)) {
            score += PERSONALITY_IDEAL_MATCH_BONUS_PER_ITEM;
        }

        // 希望对方的处事风格 -> 决策方式/生活节奏（近似映射）
        String idealAction = idealTraitMap.get("希望对方的处事风格");
        String selfDecision = selfTraitMap.get("决策方式");
        String selfPace = selfTraitMap.get("生活节奏");
        if (idealAction != null && matchesIdealActionStyle(idealAction, selfDecision, selfPace)) {
            score += PERSONALITY_IDEAL_MATCH_BONUS_PER_ITEM;
        }

        // 希望对方的情绪特质：当前问卷未提供对应 self 维度，暂不计分
        return score;
    }

    private boolean matchesIdealSocialStyle(String idealSocial, String selfEnergy) {
        if (idealSocial.contains("同频即可")) {
            return true;
        }
        if (idealSocial.contains("热情健谈")) {
            return selfEnergy.contains("外向型");
        }
        if (idealSocial.contains("沉稳内敛")) {
            return selfEnergy.contains("内向型");
        }
        return false;
    }

    private boolean matchesIdealActionStyle(String idealAction, String selfDecision, String selfPace) {
        String d = selfDecision == null ? "" : selfDecision;
        String p = selfPace == null ? "" : selfPace;

        if (idealAction.contains("严谨细致")) {
            return p.contains("计划型");
        }
        if (idealAction.contains("高效行动")) {
            return p.contains("计划型") || d.contains("理性型");
        }
        if (idealAction.contains("灵活变通")) {
            return p.contains("弹性型") || d.contains("平衡型");
        }
        if (idealAction.contains("踏实靠谱")) {
            return !p.isEmpty() || !d.isEmpty();
        }
        return false;
    }

    private boolean isCommunicationStyleMatched(Map<String, String> aSelf, Map<String, String> bSelf) {
        if (aSelf == null || bSelf == null) {
            return false;
        }
        String a = aSelf.get("沟通风格");
        String b = bSelf.get("沟通风格");
        return a != null && a.equals(b);
    }

    private boolean isAgeSatisfiedMutual(User currentUser,
                                         User candidate,
                                         UserMatchingPreference currentPreference,
                                         UserMatchingPreference candidatePreference) {
        Integer currentAge = calculateAge(currentUser == null ? null : currentUser.getBirthday());
        Integer candidateAge = calculateAge(candidate == null ? null : candidate.getBirthday());

        boolean aOk = isAgeSatisfiedOneWay(candidateAge, currentPreference);
        boolean bOk = isAgeSatisfiedOneWay(currentAge, candidatePreference);
        return aOk && bOk;
    }

    private boolean isAgeSatisfiedOneWay(Integer targetAge, UserMatchingPreference preference) {
        if (preference == null) {
            return true;
        }
        if (Boolean.TRUE.equals(preference.getAgeUnlimited())) {
            return true;
        }
        if (targetAge == null) {
            return false;
        }
        Integer min = preference.getAgeMin();
        Integer max = preference.getAgeMax();
        if (min == null || max == null) {
            return true;
        }
        return targetAge >= min && targetAge <= max;
    }

    private boolean isDistanceSatisfiedMutual(User currentUser,
                                              User candidate,
                                              UserMatchingPreference currentPreference,
                                              UserMatchingPreference candidatePreference) {
        String a = currentUser == null ? null : normalizeRegion(currentUser.getRegion());
        String b = candidate == null ? null : normalizeRegion(candidate.getRegion());

        boolean aOk = isDistanceSatisfiedOneWay(a, b, currentPreference);
        boolean bOk = isDistanceSatisfiedOneWay(b, a, candidatePreference);
        return aOk && bOk;
    }

    private boolean isDistanceSatisfiedOneWay(String selfRegion, String targetRegion, UserMatchingPreference preference) {
        if (preference == null || preference.getDistancePreference() == null) {
            return true;
        }
        String dp = preference.getDistancePreference();
        if ("unlimited".equals(dp) || "same_city_or_remote".equals(dp)) {
            return true;
        }
        if ("same_city".equals(dp)) {
            if (selfRegion == null || targetRegion == null) {
                return false;
            }
            return selfRegion.equalsIgnoreCase(targetRegion);
        }
        return true;
    }

    private int priorityWeight(Integer priorityOrder) {
        int o = priorityOrder == null ? 1 : Math.max(priorityOrder, 1);
        if (o == 1) {
            return 3;
        }
        if (o == 2) {
            return 2;
        }
        return 1;
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
