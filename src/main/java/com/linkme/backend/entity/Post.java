package com.linkme.backend.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 帖子实体类
 * 
 * 功能描述：
 * - 存储用户发布的帖子信息
 * - 支持帖子内容、标签、图片等多媒体内容
 * 
 * 输入输出示例：
 * - 输入：帖子内容、标签、图片链接
 * - 输出：帖子详细信息（包含评论、点赞数等）
 * 
 * @author Ahz
 * @version 1.0
 */
@Data
public class Post {
    /**
     * 帖子ID - 主键，自增
     */
    private Integer postId;
    
    /**
     * 用户ID - 外键，发帖用户
     */
    private Integer userId;
    
    /**
     * 内容 - 帖子文本内容
     */
    private String content;
    
    /**
     * 创建时间 - 帖子创建时间
     */
    private LocalDateTime createdAt;
}
