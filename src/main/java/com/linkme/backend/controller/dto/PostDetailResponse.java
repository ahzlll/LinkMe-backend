package com.linkme.backend.controller.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 帖子详情响应
 *
 * 字段含义：
 * - 基本信息：postId、userId、content、createdAt
 * - images：图片 URL 列表
 * - tags：标签 ID 列表
 * - likes：点赞数
 *
 * author: riki
 * version: 1.1
 */
@Data
public class PostDetailResponse {
    private Integer postId;
    private Integer userId;
    private String content;
    private String topic;
    private LocalDateTime createdAt;
    private List<String> images;
    private List<Integer> tags;
    private Integer likeCount;
    private Integer commentCount;
    private Integer favoriteCount;
    private Boolean isLiked; // 当前用户是否已点赞
    private Boolean isFavorited; // 当前用户是否已收藏
    private Integer favoriteId; // 收藏ID（如果已收藏）
    // 用户信息
    private String nickname;
    private String username;
    private String avatarUrl;
}


