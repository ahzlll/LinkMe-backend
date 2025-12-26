package com.linkme.backend.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 通知实体类
 * 
 * 功能描述：
 * - 存储用户的提醒消息
 * - 支持多种通知类型（关注、红心、点赞、评论等）
 * 
 * 输入输出示例：
 * - 输入：用户ID、通知类型、操作者ID、关联ID
 * - 输出：通知详细信息
 * 
 * @author Ahz, riki
 * @version 1.0
 */
@Data
public class Notification {
    /**
     * 通知ID - 主键，自增
     */
    private Integer notificationId;
    
    /**
     * 用户ID - 外键，接收通知的用户
     */
    private Integer userId;
    
    /**
     * 类型 - 通知类型（message, follow, heart, like等）
     */
    private String type;
    
    /**
     * 操作者ID - 外键，触发该通知的用户
     */
    private Integer actorId;
    
    /**
     * 关联ID - 关联实体ID（如消息ID、帖子ID等）
     */
    private Integer relatedId;
    
    /**
     * 关联类型 - 关联实体类型（如message、post等）
     */
    private String relatedType;
    
    /**
     * 通知标题
     */
    private String title;
    
    /**
     * 通知内容
     */
    private String content;
    
    /**
     * 创建时间 - 通知时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 是否已读 - 是否已读，默认false
     */
    private Boolean isRead;
}
