package com.linkme.backend.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 红心实体类
 * 
 * 功能描述：
 * - 存储用户对其他用户的红心信息
 * - 双方互相红心则自动匹配
 * 
 * 输入输出示例：
 * - 输入：发送者ID、接收者ID
 * - 输出：红心信息
 * 
 * @author Ahz
 * @version 1.0
 */
@Data
public class Heart {
    /**
     * 红心ID - 主键，自增
     */
    private Integer heartId;
    
    /**
     * 发送者ID - 外键，发出红心的用户
     */
    private Integer fromUserId;
    
    /**
     * 接收者ID - 外键，被心动的用户
     */
    private Integer toUserId;
    
    /**
     * 创建时间 - 红心时间
     */
    private LocalDateTime createdAt;
}
