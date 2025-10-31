package com.linkme.backend.service.impl;

import com.linkme.backend.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * 邮件服务实现类
 * 
 * 功能描述：
 * - 实现邮件发送功能
 * - 支持发送验证码邮件和普通邮件
 * 
 * @author Ahz
 * @version 1.1
 */
@Service
public class EmailServiceImpl implements EmailService {
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    @Override
    public boolean sendVerificationCodeEmail(String to, String code, String purpose) {
        try {
            String subject = getSubjectByPurpose(purpose);
            String content = getVerificationCodeContent(code, purpose);
            return sendEmail(to, subject, content);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean sendEmail(String to, String subject, String content) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content);
            
            mailSender.send(message);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 根据用途获取邮件主题
     */
    private String getSubjectByPurpose(String purpose) {
        switch (purpose) {
            case "register":
                return "LinkMe - 注册验证码";
            case "login":
                return "LinkMe - 登录验证码";
            case "reset_password":
                return "LinkMe - 重置密码验证码";
            default:
                return "LinkMe - 验证码";
        }
    }
    
    /**
     * 生成验证码邮件内容
     */
    private String getVerificationCodeContent(String code, String purpose) {
        String action = "";
        switch (purpose) {
            case "register":
                action = "注册";
                break;
            case "login":
                action = "登录";
                break;
            case "reset_password":
                action = "重置密码";
                break;
            default:
                action = "操作";
        }
        
        return String.format(
            "尊敬的用户，\n\n" +
            "您正在进行 %s 操作，验证码为：\n\n" +
            "%s\n\n" +
            "验证码有效期为 10 分钟，请勿泄露给他人。\n\n" +
            "如果这不是您的操作，请忽略此邮件。\n\n" +
            "LinkMe 团队",
            action, code
        );
    }
}

