package com.linkme.backend.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 收藏夹实体类
 * 
 * 功能描述：
 * - 存储用户创建的收藏夹信息
 * - 支持用户自定义收藏夹分组管理
 * 
 * 输入输出示例：
 * - 输入：用户ID、收藏夹名称
 * - 输出：收藏夹详细信息
 * 
 * @author Ahz
 * @version 1.0
 */
@Data
public class FavoriteFolder {
    /**
     * 收藏夹ID - 主键，自增
     */
    private Integer folderId;
    
    /**
     * 用户ID - 外键，所属用户
     */
    private Integer userId;
    
    /**
     * 收藏夹名称 - 收藏夹名称
     */
    private String name;
    
    /**
     * 创建时间 - 创建时间
     */
    private LocalDateTime createdAt;
}
