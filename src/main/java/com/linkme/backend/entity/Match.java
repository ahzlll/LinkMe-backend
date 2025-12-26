package com.linkme.backend.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 匹配实体类
 * 
 * 功能描述：
 * - 存储用户之间的匹配关系（亲密关系）
 * - 记录双方互相红心后的匹配状态
 * 
 * 输入输出示例：
 * - 输入：用户1ID、用户2ID
 * - 输出：匹配关系信息
 * 
 * @author Ahz，riki
 * @version 1.1
 */
@Data
public class Match {
    /**
     * 匹配ID - 主键，自增
     */
    private Integer matchId;
    
    /**
     * 用户1ID - 外键，匹配关系的用户A，一定比user2Id小
     */
    private Integer user1Id;
    
    /**
     * 用户2ID - 外键，匹配关系的用户B
     */
    private Integer user2Id;
    
    /**
     * 创建时间 - 匹配成功时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 状态 - 默认0-进行中，1-已结束
     */
    private Integer status;
}
