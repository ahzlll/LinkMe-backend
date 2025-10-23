package com.linkme.backend.entity;

import lombok.Data;

/**
 * 用户标签关联实体类
 * 
 * 功能描述：
 * - 存储用户与个性/兴趣标签的多对多关系
 * - 支持用户设置个人标签，用于匹配推荐
 * 
 * 输入输出示例：
 * - 输入：用户ID、标签ID
 * - 输出：用户标签关联信息
 * 
 * @author Ahz
 * @version 1.0
 */
@Data
public class UserTag {
    /**
     * 用户ID - 外键，关联User表
     */
    private Integer userId;
    
    /**
     * 标签ID - 外键，关联TagDef表
     */
    private Integer tagId;
}
