package com.linkme.backend.util;

import java.util.regex.Pattern;

/**
 * 邮箱验证工具类
 * 
 * 功能描述：
 * - 验证邮箱格式是否符合标准
 * - 使用正则表达式验证邮箱格式
 * 
 * @author Ahz
 * @version 1.1
 */
public class EmailValidator {
    
    // 邮箱格式正则表达式
    // 基本格式：用户名@域名.顶级域名
    // 用户名可以包含字母、数字、点号、下划线、连字符
    // 域名可以包含字母、数字、点号、连字符
    private static final String EMAIL_PATTERN = 
        "^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    
    private static final Pattern pattern = Pattern.compile(EMAIL_PATTERN);
    
    /**
     * 验证邮箱格式是否符合要求
     * 
     * @param email 待验证的邮箱
     * @return 验证结果，如果符合格式要求返回 true，否则返回 false
     */
    public static boolean isValid(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        
        return pattern.matcher(email.trim()).matches();
    }
    
    /**
     * 获取邮箱验证的错误信息
     * 
     * @param email 待验证的邮箱
     * @return 错误信息，如果邮箱符合格式要求返回 null
     */
    public static String getErrorMessage(String email) {
        if (email == null || email.trim().isEmpty()) {
            return "邮箱不能为空";
        }
        
        if (!isValid(email)) {
            return "邮箱格式不正确，请使用正确的邮箱格式（例如：user@example.com）";
        }
        
        return null; // 邮箱符合格式要求
    }
}

