package com.linkme.backend.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 评论实体类
 * 
 * 功能描述：
 * - 存储用户对帖子的评论信息
 * - 支持评论回复功能
 * 
 * 输入输出示例：
 * - 输入：帖子ID、用户ID、评论内容、父评论ID（可选）
 * - 输出：评论详细信息（包含回复关系）
 * 
 * @author Ahz
 * @version 1.0
 */
@Data
public class Comment {
    /**
     * 评论ID - 主键，自增
     */
    private Integer commentId;
    
    /**
     * 帖子ID - 外键，所属帖子
     */
    private Integer postId;
    
    /**
     * 用户ID - 外键，评论用户
     */
    private Integer userId;
    
    /**
     * 内容 - 评论文本内容
     */
    private String content;
    
    /**
     * 父评论ID - 外键，可为空（回复其他评论）
     */
    private Integer parentId;
    
    /**
     * 创建时间 - 评论创建时间
     */
    private LocalDateTime createdAt;
}
