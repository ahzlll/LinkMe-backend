package com.linkme.backend.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 屏蔽实体类
 * 
 * 功能描述：
 * - 存储用户之间的屏蔽关系
 * - 支持屏蔽和取消屏蔽功能
 * 
 * 输入输出示例：
 * - 输入：屏蔽者ID、被屏蔽者ID
 * - 输出：屏蔽关系信息
 * 
 * @author Ahz
 * @version 1.2.2
 */
@Data
public class Block {
    /**
     * 屏蔽者ID - 外键，屏蔽者用户ID
     */
    private Integer blockerId;
    
    /**
     * 被屏蔽者ID - 外键，被屏蔽的用户ID
     */
    private Integer blockedId;
    
    /**
     * 屏蔽时间 - 屏蔽时间
     */
    private LocalDateTime createdAt;
}

