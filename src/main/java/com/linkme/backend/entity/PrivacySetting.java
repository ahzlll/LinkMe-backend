package com.linkme.backend.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 隐私设置实体类
 * 
 * 功能描述：
 * - 存储用户的隐私设置信息
 * - 控制用户资料可见性和匹配权限
 * 
 * 输入输出示例：
 * - 输入：用户ID、允许匹配、允许私聊、允许查看资料
 * - 输出：隐私设置信息
 * 
 * @author Ahz
 * @version 1.0
 */
@Data
public class PrivacySetting {
    /**
     * 隐私设置ID - 主键，自增
     */
    private Integer privacyId;
    
    /**
     * 用户ID - 外键，关联User表
     */
    private Integer userId;
    
    /**
     * 允许匹配 - 是否允许匹配
     */
    private Boolean allowMatch;
    
    /**
     * 允许私聊 - 是否允许陌生人私聊
     */
    private Boolean allowPrivateMessages;
    
    /**
     * 允许查看个人资料 - 是否允许陌生人查看个人资料
     */
    private Boolean allowProfileView;
    
    /**
     * 更新时间 - 设置更新时间
     */
    private LocalDateTime updatedAt;
}
