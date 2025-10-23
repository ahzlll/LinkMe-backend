package com.linkme.backend.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 标签定义实体类
 * 
 * 功能描述：
 * - 存储系统预定义标签和用户自定义标签
 * - 支持帖子标签和用户个性标签分类
 * 
 * 输入输出示例：
 * - 输入：标签名称、类型、创建者
 * - 输出：标签详细信息
 * 
 * @author Ahz
 * @version 1.0
 */
@Data
public class TagDef {
    /**
     * 标签ID - 主键，自增
     */
    private Integer tagId;
    
    /**
     * 标签名称 - 唯一的标签名称
     */
    private String name;
    
    /**
     * 创建者ID - NULL为系统设定，非NULL为用户自定义标签
     */
    private Integer createdBy;
    
    /**
     * 创建时间 - 标签创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 标签类型 - 帖子标签/用户标签（"post"，"user"）
     */
    private String tagType;
}
