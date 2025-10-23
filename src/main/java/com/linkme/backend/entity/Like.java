package com.linkme.backend.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 点赞实体类
 * 
 * 功能描述：
 * - 存储用户对帖子的点赞信息
 * - 支持点赞和取消点赞功能
 * 
 * 输入输出示例：
 * - 输入：用户ID、帖子ID
 * - 输出：点赞信息
 * 
 * @author Ahz
 * @version 1.0
 */
@Data
public class Like {
    /**
     * 点赞ID - 主键，自增
     */
    private Integer likeId;
    
    /**
     * 用户ID - 外键，点赞用户
     */
    private Integer userId;
    
    /**
     * 帖子ID - 外键，被点赞的帖子
     */
    private Integer postId;
    
    /**
     * 创建时间 - 点赞时间
     */
    private LocalDateTime createdAt;
}
