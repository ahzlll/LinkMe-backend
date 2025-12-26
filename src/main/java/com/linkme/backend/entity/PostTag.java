package com.linkme.backend.entity;

import lombok.Data;

/**
 * 帖子标签关联实体类
 * 
 * 功能描述：
 * - 存储帖子与标签的多对多关系
 * - 支持帖子分类和标签筛选
 * 
 * 输入输出示例：
 * - 输入：帖子ID、标签ID
 * - 输出：帖子标签关联信息
 * 
 * @author Ahz，riki
 * @version 1.1
 */
@Data
public class PostTag {
    /**
     * 帖子ID - 外键，关联Post表
     */
    private Integer postId;
    
    /**
     * 标签ID - 外键，关联TagDef表
     */
    private Integer tagId;
}
