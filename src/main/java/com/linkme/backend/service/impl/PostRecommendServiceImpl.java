package com.linkme.backend.service.impl;

import com.linkme.backend.entity.Like;
import com.linkme.backend.entity.Post;
import com.linkme.backend.mapper.LikeMapper;
import com.linkme.backend.mapper.PostMapper;
import com.linkme.backend.service.PostRecommendService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 帖子推荐服务实现类
 * 
 * 采用基于用户的协同过滤算法 (User-Based Collaborative Filtering)
 * 1. 计算用户之间的相似度（基于共同点赞的帖子）
 * 2. 找到与目标用户最相似的 Top N 个用户
 * 3. 从这些相似用户点赞过但目标用户未点赞的帖子中进行推荐
 * 4. 兜底策略：如果推荐不足，补充最新的热门帖子
 * 
 * author: riki
 * version: 1.0
 */
@Service
public class PostRecommendServiceImpl implements PostRecommendService {

    private static final Logger logger = LoggerFactory.getLogger(PostRecommendServiceImpl.class);

    @Autowired
    private LikeMapper likeMapper;

    @Autowired
    private PostMapper postMapper;

    @Override
    public List<Post> getRecommendedPosts(Integer userId, Integer limit) {
        if (userId == null) {
            // 未登录用户，直接返回热门帖子
            return getPopularPosts(null, limit);
        }

        // 1. 获取所有点赞数据
        List<Like> allLikes = likeMapper.selectAll();
        if (allLikes == null || allLikes.isEmpty()) {
            return getPopularPosts(userId, limit);
        }

        // 2. 构建 用户-帖子 映射表
        Map<Integer, Set<Integer>> userPostMap = new HashMap<>();
        for (Like like : allLikes) {
            userPostMap.computeIfAbsent(like.getUserId(), k -> new HashSet<>()).add(like.getPostId());
        }

        Set<Integer> currentUserLikes = userPostMap.getOrDefault(userId, Collections.emptySet());
        if (currentUserLikes.isEmpty()) {
            // 当前用户没有点赞记录，无法计算相似度，返回热门
            logger.info("PostRecommend: userId={} has no likes, fallback to popular posts", userId);
            return getPopularPosts(userId, limit);
        }

        // 3. 计算与其他用户的相似度 (Jaccard Similarity)
        Map<Integer, Double> userSimilarityMap = new HashMap<>();
        for (Map.Entry<Integer, Set<Integer>> entry : userPostMap.entrySet()) {
            Integer otherUserId = entry.getKey();
            if (otherUserId.equals(userId))
                continue;

            Set<Integer> otherUserLikes = entry.getValue();
            double similarity = calculateJaccardSimilarity(currentUserLikes, otherUserLikes);
            if (similarity > 0) {
                userSimilarityMap.put(otherUserId, similarity);
            }
        }

        if (userSimilarityMap.isEmpty()) {
            logger.info("PostRecommend: userId={} no similar users found, fallback to popular posts", userId);
            return getPopularPosts(userId, limit);
        }

        // 4. 推荐候选帖子并计分
        Map<Integer, Double> postScoreMap = new HashMap<>();
        for (Map.Entry<Integer, Double> entry : userSimilarityMap.entrySet()) {
            Integer similarUserId = entry.getKey();
            Double similarity = entry.getValue();
            Set<Integer> similarUserPosts = userPostMap.get(similarUserId);

            for (Integer postId : similarUserPosts) {
                if (!currentUserLikes.contains(postId)) {
                    postScoreMap.put(postId, postScoreMap.getOrDefault(postId, 0.0) + similarity);
                }
            }
        }

        // 5. 排序并取 Top N
        List<Integer> recommendedPostIds = postScoreMap.entrySet().stream()
                .sorted(Map.Entry.<Integer, Double>comparingByValue().reversed())
                .limit(limit)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        if (recommendedPostIds.isEmpty()) {
            return getPopularPosts(userId, limit);
        }

        // 6. 查询帖子详情（按推荐分数顺序返回）
        List<Post> recommendedPosts = orderPostsByIds(
                postMapper.selectByIds(recommendedPostIds, userId), recommendedPostIds);
        boolean fallbackUsed = false;
        int initialRecommendedCount = recommendedPosts.size();

        // 7. 如果推荐数量不足，补充热门帖子
        if (recommendedPosts.size() < limit) {
            fallbackUsed = true;
            List<Post> popularPosts = getPopularPosts(userId, limit);
            Set<Integer> existingIds = recommendedPosts.stream().map(Post::getPostId).collect(Collectors.toSet());
            for (Post p : popularPosts) {
                if (recommendedPosts.size() >= limit)
                    break;
                if (!existingIds.contains(p.getPostId())) {
                    recommendedPosts.add(p);
                }
            }
        }

        logger.info("PostRecommend: userId={} similarUserCount={} candidatePostCount={} initialRecommended={} fallbackUsed={} finalRecommended={}",
                userId,
                userSimilarityMap.size(),
                postScoreMap.size(),
                initialRecommendedCount,
                fallbackUsed,
                recommendedPosts.size());

        return recommendedPosts;
    }

    /**
     * 按推荐 ID 顺序重排帖子列表（SQL IN 查询不保֤˳序）
     */
    private List<Post> orderPostsByIds(List<Post> posts, List<Integer> orderedIds) {
        if (posts == null || posts.isEmpty() || orderedIds == null || orderedIds.isEmpty()) {
            return posts == null ? Collections.emptyList() : posts;
        }
        Map<Integer, Post> postMap = posts.stream()
                .filter(p -> p != null && p.getPostId() != null)
                .collect(Collectors.toMap(Post::getPostId, p -> p, (a, b) -> a));
        List<Post> ordered = new ArrayList<>();
        for (Integer id : orderedIds) {
            Post post = postMap.get(id);
            if (post != null) {
                ordered.add(post);
            }
        }
        return ordered;
    }

    /**
     * 计算 Jaccard 相似度
     */
    private double calculateJaccardSimilarity(Set<Integer> set1, Set<Integer> set2) {
        if (set1.isEmpty() || set2.isEmpty())
            return 0.0;

        long intersectionSize = set1.stream().filter(set2::contains).count();
        if (intersectionSize == 0)
            return 0.0;

        long unionSize = set1.size() + set2.size() - intersectionSize;
        return (double) intersectionSize / unionSize;
    }

    /**
     * 兜底策略：获取热门帖子（按点赞数排序）
     */
    private List<Post> getPopularPosts(Integer userId, Integer limit) {
        // 这里简单使用 selectAll 并按点赞数排序，实际项目中应该有专门的热门表或缓存
        // PostMapper.selectAll 默认是按时间排序的，我们取最新的作为简单替代
        return postMapper.selectAll(0, limit, userId);
    }
}
