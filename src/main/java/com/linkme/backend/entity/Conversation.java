package com.linkme.backend.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 会话实体类
 * 
 * 功能描述：
 * - 存储用户之间的私聊会话信息
 * - 支持一对一聊天会话管理
 * 
 * 输入输出示例：
 * - 输入：用户1ID、用户2ID
 * - 输出：会话信息
 * 
 * @author Ahz, riki
 * @version 1.0
 */
@Data
public class Conversation {
    /**
     * 会话ID - 主键，自增
     */
    private Integer conversationId;
    
    /**
     * 用户1ID - 外键，参与会话的用户（ID小的一方）
     */
    private Integer user1Id;
    
    /**
     * 用户2ID - 外键，参与会话的另一个用户
     */
    private Integer user2Id;
    
    /**
     * 创建时间 - 会话创建时间
     */
    private LocalDateTime createdAt;
}
