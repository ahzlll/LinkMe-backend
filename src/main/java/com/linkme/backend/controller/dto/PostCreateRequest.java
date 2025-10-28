package com.linkme.backend.controller.dto;

import lombok.Data;
import java.util.List;

/**
 * 创建帖子请求
 *
 * 对齐 API.md：
 * - userId: 发帖用户
 * - content: 文本内容
 * - images: 图片 URL 列表
 * - tags: 标签 ID 列表
 *
 * author: riki
 * version: 1.0
 */
@Data
public class PostCreateRequest {
    private Integer userId;
    private String content;
    private List<String> images;
    private List<Integer> tags;
}


