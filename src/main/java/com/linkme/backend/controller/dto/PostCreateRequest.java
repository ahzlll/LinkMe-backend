package com.linkme.backend.controller.dto;

import lombok.Data;
import java.util.List;

/**
 * 创建帖子请求（JSON格式）
 *
 * 对齐 API.md：
 * - userId: 发帖用户
 * - content: 文本内容
 * - images: 图片 Base64 编码字符串列表
 * - tags: 标签 ID 列表
 *
 * author: riki
 * version: 1.2
 */
@Data
public class PostCreateRequest {
    private Integer userId;
    private String content;
    private String topic;
    private List<String> images; // Base64编码的图片字符串列表
    private List<Integer> tags;
}


