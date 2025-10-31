package com.linkme.backend.service;

/**
 * 验证码服务接口
 * 
 * 功能描述：
 * - 提供验证码的生成、发送、验证等功能
 * - 支持邮箱和手机号验证码
 * 
 * @author Ahz
 * @version 1.1
 */
public interface VerificationCodeService {
    
    /**
     * 生成并发送验证码
     * 
     * @param email 邮箱（可为空）
     * @param phone 手机号（可为空）
     * @param purpose 用途（register/login/reset_password）
     * @return 是否发送成功
     */
    boolean sendVerificationCode(String email, String phone, String purpose);
    
    /**
     * 验证验证码
     * 
     * @param code 验证码
     * @param email 邮箱（可为空）
     * @param phone 手机号（可为空）
     * @param type 类型（email/phone）
     * @param purpose 用途（register/login/reset_password）
     * @return 是否验证通过
     */
    boolean verifyCode(String code, String email, String phone, String type, String purpose);
    
    /**
     * 生成随机验证码
     * 
     * @param length 验证码长度（默认6位）
     * @return 验证码
     */
    String generateCode(int length);
}

