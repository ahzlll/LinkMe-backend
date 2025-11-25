package com.linkme.backend.entity;

import lombok.Data;

/**
 * 帖子图片实体类
 * 
 * 功能描述：
 * - 存储帖子关联的图片信息
 * - 支持一个帖子多张图片
 * - 图片以Base64编码字符串形式存储
 * 
 * 输入输出示例：
 * - 输入：帖子ID、图片Base64编码字符串
 * - 输出：图片详细信息
 * 
 * @author Ahz, riki
 * @version 1.1
 */
@Data
public class PostImage {
    /**
     * 图片ID - 主键，自增
     */
    private Integer imageId;
    
    /**
     * 帖子ID - 外键，所属帖子
     */
    private Integer postId;
    
    /**
     * 图片Base64编码字符串 - 存储图片的Base64编码数据
     */
    private String imageUrl;
}
