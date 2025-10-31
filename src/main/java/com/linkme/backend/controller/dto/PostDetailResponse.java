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
    private LocalDateTime createdAt;
    private List<String> images;
    private List<Integer> tags;
    private Integer likes;
}


