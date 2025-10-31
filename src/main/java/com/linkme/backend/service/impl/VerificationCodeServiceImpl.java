package com.linkme.backend.service.impl;

import com.linkme.backend.entity.VerificationCode;
import com.linkme.backend.mapper.VerificationCodeMapper;
import com.linkme.backend.mapper.UserMapper;
import com.linkme.backend.entity.User;
import com.linkme.backend.service.VerificationCodeService;
import com.linkme.backend.service.EmailService;
import com.linkme.backend.service.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

/**
 * 验证码服务实现类
 * 
 * 功能描述：
 * - 实现验证码的生成、发送、验证等功能
 * - 支持邮箱和手机号验证码
 * 
 * @author Ahz
 * @version 1.1
 */
@Service
public class VerificationCodeServiceImpl implements VerificationCodeService {
    
    @Autowired
    private VerificationCodeMapper verificationCodeMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private SmsService smsService;
    
    private static final int CODE_LENGTH = 6;
    private static final int CODE_EXPIRE_MINUTES = 10; // 验证码10分钟过期
    
    @Override
    public boolean sendVerificationCode(String email, String phone, String purpose) {
        try {
            // 确定类型
            String type = email != null && !email.isEmpty() ? "email" : "phone";
            
            // 如果是重置密码，需要验证用户是否存在
            if ("reset_password".equals(purpose)) {
                User user = null;
                if (type.equals("email")) {
                    user = userMapper.selectByEmail(email);
                } else {
                    user = userMapper.selectByPhone(phone);
                }
                if (user == null) {
                    return false; // 用户不存在
                }
            }
            
            // 生成验证码
            String code = generateCode(CODE_LENGTH);
            
            // 创建验证码对象
            VerificationCode verificationCode = new VerificationCode();
            if ("reset_password".equals(purpose)) {
                // 重置密码时需要关联用户ID
                User user = type.equals("email") ? userMapper.selectByEmail(email) : userMapper.selectByPhone(phone);
                if (user != null) {
                    verificationCode.setUserId(user.getUserId());
                }
            }
            verificationCode.setCode(code);
            verificationCode.setType(type);
            verificationCode.setPurpose(purpose);
            verificationCode.setExpireAt(LocalDateTime.now().plusMinutes(CODE_EXPIRE_MINUTES));
            verificationCode.setIsUsed(false);
            verificationCode.setCreatedAt(LocalDateTime.now());
            
            // 保存验证码
            int result = verificationCodeMapper.insert(verificationCode);
            if (result > 0) {
                // 发送验证码到邮箱或手机
                if (type.equals("email")) {
                    // 发送邮件
                    boolean emailSent = emailService.sendVerificationCodeEmail(email, code, purpose);
                    if (emailSent) {
                        System.out.println("验证码已发送到邮箱：" + email);
                    } else {
                        System.err.println("验证码邮件发送失败，邮箱：" + email);
                        // 即使邮件发送失败，也返回true，因为验证码已保存到数据库
                        // 用户可以通过其他方式（如查看日志或联系客服）获取验证码
                    }
                } else {
                    // 使用阿里云短信服务发送验证码
                    boolean smsSent = smsService.sendVerificationCodeSms(phone, code, purpose);
                    if (smsSent) {
                        System.out.println("验证码已发送到手机：" + phone);
                    } else {
                        System.err.println("验证码短信发送失败，手机号：" + phone);
                        // 即使短信发送失败，也返回true，因为验证码已保存到数据库
                        // 用户可以通过其他方式（如查看日志或联系客服）获取验证码
                    }
                }
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean verifyCode(String code, String email, String phone, String type, String purpose) {
        try {
            // 如果是重置密码，需要先查找用户
            Integer userId = null;
            if ("reset_password".equals(purpose)) {
                User user = null;
                if (type.equals("email")) {
                    user = userMapper.selectByEmail(email);
                } else {
                    user = userMapper.selectByPhone(phone);
                }
                
                if (user == null) {
                    return false; // 用户不存在
                }
                userId = user.getUserId();
            }
            
            // 查询有效的验证码
            VerificationCode verificationCode = verificationCodeMapper.selectValidCode(code, type, purpose, userId);
            
            if (verificationCode == null) {
                return false; // 验证码不存在或已过期或已使用
            }
            
            // 如果是重置密码，再次验证用户ID是否匹配（双重保险）
            if ("reset_password".equals(purpose) && userId != null) {
                if (verificationCode.getUserId() == null 
                    || !verificationCode.getUserId().equals(userId)) {
                    return false; // 验证码与用户不匹配
                }
            }
            
            // 标记验证码为已使用
            verificationCodeMapper.markAsUsed(verificationCode.getCodeId());
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public String generateCode(int length) {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < length; i++) {
            code.append(random.nextInt(10)); // 生成0-9的随机数字
        }
        return code.toString();
    }
}

