package com.linkme.backend.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 收藏实体类
 * 
 * 功能描述：
 * - 存储用户对帖子的收藏信息
 * - 支持收藏到指定收藏夹
 * 
 * 输入输出示例：
 * - 输入：用户ID、帖子ID、收藏夹ID
 * - 输出：收藏信息
 * 
 * @author Ahz, riki
 * @version 1.0
 */
@Data
public class Favorite {
    /**
     * 收藏ID - 主键，自增
     */
    private Integer favoriteId;
    
    /**
     * 用户ID - 外键，收藏用户
     */
    private Integer userId;
    
    /**
     * 帖子ID - 外键，被收藏的帖子
     */
    private Integer postId;
    
    /**
     * 收藏夹ID - 外键，所属收藏夹
     */
    private Integer folderId;
    
    /**
     * 创建时间 - 收藏时间
     */
    private LocalDateTime createdAt;
}
