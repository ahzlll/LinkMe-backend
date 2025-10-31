package com.linkme.backend.util;

import java.util.regex.Pattern;

/**
 * 手机号验证工具类
 * 
 * 功能描述：
 * - 验证手机号格式是否符合标准
 * - 使用正则表达式验证中国手机号格式（11位，以1开头）
 * 
 * @author Ahz
 * @version 1.1
 */
public class PhoneValidator {
    
    // 中国手机号格式正则表达式：11位数字，以1开头，第二位为3-9
    private static final String PHONE_PATTERN = "^1[3-9]\\d{9}$";
    
    private static final Pattern pattern = Pattern.compile(PHONE_PATTERN);
    
    /**
     * 验证手机号格式是否符合要求
     * 
     * @param phone 待验证的手机号
     * @return 验证结果，如果符合格式要求返回 true，否则返回 false
     */
    public static boolean isValid(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        
        return pattern.matcher(phone.trim()).matches();
    }
    
    /**
     * 获取手机号验证的错误信息
     * 
     * @param phone 待验证的手机号
     * @return 错误信息，如果手机号符合格式要求返回 null
     */
    public static String getErrorMessage(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return "手机号不能为空";
        }
        
        if (!isValid(phone)) {
            return "手机号格式不正确，请输入11位中国手机号（例如：13800138000）";
        }
        
        return null; // 手机号符合格式要求
    }
}

