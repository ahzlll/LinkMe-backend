package com.linkme.backend.service;

import com.linkme.backend.controller.dto.MatchRecommendationResponse;

import java.util.List;

/**
 * 匹配推荐服务
 * - 从数据库筛选候选用户并计算匹配度，返回推荐列表
 *
 * @author riki
 * @version 1.0
 */
public interface MatchRecommendService {
    /**
     * 获取匹配推荐列表
     *
     * @param currentUserId 当前登录用户ID
     * @param page 页码
     * @param size 每页数量
     * @return 推荐结果列表
     */
    List<MatchRecommendationResponse> getRecommendations(Integer currentUserId, Integer page, Integer size);
}
