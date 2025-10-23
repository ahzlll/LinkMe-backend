package com.linkme.backend.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 关注实体类
 * 
 * 功能描述：
 * - 存储用户之间的关注关系
 * - 支持关注和取消关注功能
 * 
 * 输入输出示例：
 * - 输入：关注者ID、被关注者ID
 * - 输出：关注关系信息
 * 
 * @author Ahz
 * @version 1.0
 */
@Data
public class Follow {
    /**
     * 关注者ID - 外键，关注者用户ID
     */
    private Integer followerId;
    
    /**
     * 被关注者ID - 外键，被关注的用户ID
     */
    private Integer followeeId;
    
    /**
     * 关注时间 - 关注时间
     */
    private LocalDateTime createdAt;
}
