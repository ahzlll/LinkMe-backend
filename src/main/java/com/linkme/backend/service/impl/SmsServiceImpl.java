package com.linkme.backend.service.impl;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.teaopenapi.models.Config;
import com.linkme.backend.service.SmsService;
import com.alibaba.fastjson2.JSON;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 阿里云短信服务实现类
 * 
 * 功能描述：
 * - 实现短信发送功能
 * - 支持发送验证码短信和自定义模板短信
 * 
 * @author Ahz
 * @version 1.1
 */
@Service
public class SmsServiceImpl implements SmsService {
    
    @Value("${aliyun.sms.access-key-id}")
    private String accessKeyId;
    
    @Value("${aliyun.sms.access-key-secret}")
    private String accessKeySecret;
    
    @Value("${aliyun.sms.sign-name}")
    private String signName;
    
    @Value("${aliyun.sms.template-code}")
    private String templateCode;
    
    @Value("${aliyun.sms.endpoint:dysmsapi.aliyuncs.com}")
    private String endpoint;
    
    /**
     * 创建阿里云短信客户端
     */
    private Client createClient() throws Exception {
        Config config = new Config()
            .setAccessKeyId(accessKeyId)
            .setAccessKeySecret(accessKeySecret)
            .setEndpoint(endpoint);
        return new Client(config);
    }
    
    @Override
    public boolean sendVerificationCodeSms(String phone, String code, String purpose) {
        try {
            // 阿里云短信模板参数通常只需要验证码
            // 模板示例：您的验证码是${code}，10分钟内有效
            Map<String, String> templateParams = new HashMap<>();
            templateParams.put("code", code);
            
            // 转换为JSON字符串
            String templateParam = JSON.toJSONString(templateParams);
            
            // 使用默认模板发送
            return sendSms(phone, templateCode, templateParam);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean sendSms(String phone, String templateCode, String templateParam) {
        try {
            Client client = createClient();
            SendSmsRequest request = new SendSmsRequest()
                .setPhoneNumbers(phone)
                .setSignName(signName)
                .setTemplateCode(templateCode)
                .setTemplateParam(templateParam);
            
            SendSmsResponse response = client.sendSms(request);
            
            // 检查响应
            if (response.getBody() != null && "OK".equals(response.getBody().getCode())) {
                return true;
            } else {
                String errorMsg = response.getBody() != null ? 
                    response.getBody().getMessage() : "未知错误";
                System.err.println("短信发送失败：" + errorMsg);
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("短信发送异常：" + e.getMessage());
            return false;
        }
    }
}

