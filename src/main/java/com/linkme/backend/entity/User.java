package com.linkme.backend.entity;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户实体类
 * 
 * 功能描述：
 * - 存储用户基本信息，包括个人资料、联系方式等
 * - 支持用户注册、登录、个人信息管理
 * 
 * 输入输出示例：
 * - 输入：用户注册信息（邮箱、密码、昵称等）
 * - 输出：用户详细信息（包含头像、简介、标签等）
 * 
 * @author Ahz
 * @version 1.0
 */
@Data
public class User {
    /**
     * 用户ID - 主键，自增
     */
    private Integer userId;
    
    /**
     * 用户名 - 唯一用户名
     */
    private String username;
    
    /**
     * 邮箱 - 唯一，可用于登录
     */
    private String email;
    
    /**
     * 手机号 - 唯一，可用于登录
     */
    private String phone;
    
    /**
     * 密码哈希 - 存储加密后的密码
     */
    @JsonAlias({"password_hash", "passwordHash"})
    private String passwordHash;
    
    /**
     * 昵称 - 用户显示名称
     */
    private String nickname;
    
    /**
     * 性别 - 男、女、其他
     */
    private String gender;
    
    /**
     * 生日 - 出生日期
     */
    private LocalDate birthday;
    
    /**
     * 地区 - 所在城市或地区
     */
    private String region;
    
    /**
     * 头像URL - 用户头像图片链接
     */
    private String avatarUrl;
    
    /**
     * 简介 - 个人简介
     */
    private String bio;
    
    /**
     * 创建时间 - 注册时间
     */
    private LocalDateTime createdAt;
}
