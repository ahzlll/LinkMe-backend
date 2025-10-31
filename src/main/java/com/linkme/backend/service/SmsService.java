package com.linkme.backend.service;

/**
 * 短信服务接口
 * 
 * 功能描述：
 * - 提供短信发送功能
 * - 支持发送验证码短信
 * 
 * @author Ahz
 * @version 1.1
 */
public interface SmsService {
    
    /**
     * 发送验证码短信
     * 
     * @param phone 手机号
     * @param code 验证码
     * @param purpose 用途（register/login/reset_password）
     * @return 是否发送成功
     */
    boolean sendVerificationCodeSms(String phone, String code, String purpose);
    
    /**
     * 发送短信
     * 
     * @param phone 手机号
     * @param templateCode 短信模板代码
     * @param templateParam 短信模板参数（JSON格式）
     * @return 是否发送成功
     */
    boolean sendSms(String phone, String templateCode, String templateParam);
}

