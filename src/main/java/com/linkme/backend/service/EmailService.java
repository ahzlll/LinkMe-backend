package com.linkme.backend.service;

/**
 * 邮件服务接口
 * 
 * 功能描述：
 * - 提供邮件发送功能
 * - 支持发送验证码邮件
 * 
 * @author Ahz
 * @version 1.1
 */
public interface EmailService {
    
    /**
     * 发送验证码邮件
     * 
     * @param to 收件人邮箱
     * @param code 验证码
     * @param purpose 用途（register/login/reset_password）
     * @return 是否发送成功
     */
    boolean sendVerificationCodeEmail(String to, String code, String purpose);
    
    /**
     * 发送普通邮件
     * 
     * @param to 收件人邮箱
     * @param subject 邮件主题
     * @param content 邮件内容
     * @return 是否发送成功
     */
    boolean sendEmail(String to, String subject, String content);
}

