package com.linkme.backend.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 验证码实体类
 * 
 * 功能描述：
 * - 存储注册/登录/重置密码验证码信息
 * - 支持邮箱和手机号验证码
 * 
 * 输入输出示例：
 * - 输入：用户ID、验证码、类型、用途、过期时间
 * - 输出：验证码信息
 * 
 * @author Ahz
 * @version 1.0
 */
@Data
public class VerificationCode {
    /**
     * 验证码ID - 主键，自增
     */
    private Integer codeId;
    
    /**
     * 用户ID - 外键，可为空（注册前可为空）
     */
    private Integer userId;
    
    /**
     * 验证码 - 验证码
     */
    private String code;
    
    /**
     * 类型 - 'email' or 'phone'，不为空
     */
    private String type;
    
    /**
     * 用途 - 'register', 'login', 'reset_password'，默认 'register'
     */
    private String purpose;
    
    /**
     * 过期时间 - 验证码过期时间
     */
    private LocalDateTime expireAt;
    
    /**
     * 是否已使用 - 默认 false
     */
    private Boolean isUsed;
    
    /**
     * 创建时间 - 发送时间
     */
    private LocalDateTime createdAt;
}
