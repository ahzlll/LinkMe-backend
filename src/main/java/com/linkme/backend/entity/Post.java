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
     * 主题 - 帖子主题
     */
    private String topic;
    
    /**
     * 创建时间 - 帖子创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 用户昵称 - 从user表关联查询
     */
    private String nickname;
    
    /**
     * 用户名 - 从user表关联查询
     */
    private String username;
    
    /**
     * 用户头像URL - 从user表关联查询
     */
    private String avatarUrl;
    
    /**
     * 点赞数 - 统计该帖子的点赞数量
     */
    private Integer likeCount;
    
    /**
     * 收藏数 - 统计该帖子的收藏数量
     */
    private Integer favoriteCount;
    
    /**
     * 评论数 - 统计该帖子的评论数量
     */
    private Integer commentCount;
}
