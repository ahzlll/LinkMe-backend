package com.linkme.backend.util;

/**
 * 密码验证工具类
 * 
 * 功能描述：
 * - 验证密码是否符合安全要求
 * - 密码规则：不少于8个字符，必须同时包含大写字母、小写字母、数字、下划线中的至少三种
 * 
 * @author Ahz
 * @version 1.1
 */
public class PasswordValidator {
    
    /**
     * 验证密码是否符合要求
     * 
     * 密码规则：
     * 1. 最小长度：不少于8个字符
     * 2. 复杂度：必须同时包含 大写字母、小写字母、数字、下划线 中的至少三种
     * 
     * @param password 待验证的密码
     * @return 验证结果，如果符合要求返回 true，否则返回 false
     */
    public static boolean isValid(String password) {
        if (password == null || password.isEmpty()) {
            return false;
        }
        
        // 检查长度：不少于8个字符
        if (password.length() < 8) {
            return false;
        }
        
        // 检查复杂度：必须包含至少三种字符类型
        boolean hasUpperCase = false;  // 大写字母
        boolean hasLowerCase = false;  // 小写字母
        boolean hasDigit = false;      // 数字
        boolean hasUnderscore = false; // 下划线
        
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                hasUpperCase = true;
            } else if (Character.isLowerCase(c)) {
                hasLowerCase = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            } else if (c == '_') {
                hasUnderscore = true;
            }
        }
        
        // 统计包含的字符类型数量
        int typeCount = 0;
        if (hasUpperCase) typeCount++;
        if (hasLowerCase) typeCount++;
        if (hasDigit) typeCount++;
        if (hasUnderscore) typeCount++;
        
        // 必须至少包含三种类型
        return typeCount >= 3;
    }
    
    /**
     * 获取密码验证的错误信息
     * 
     * @param password 待验证的密码
     * @return 错误信息，如果密码符合要求返回 null
     */
    public static String getErrorMessage(String password) {
        if (password == null || password.isEmpty()) {
            return "密码不能为空";
        }
        
        if (password.length() < 8) {
            return "密码长度不能少于8个字符";
        }
        
        // 检查复杂度
        boolean hasUpperCase = false;
        boolean hasLowerCase = false;
        boolean hasDigit = false;
        boolean hasUnderscore = false;
        
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                hasUpperCase = true;
            } else if (Character.isLowerCase(c)) {
                hasLowerCase = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            } else if (c == '_') {
                hasUnderscore = true;
            }
        }
        
        int typeCount = 0;
        if (hasUpperCase) typeCount++;
        if (hasLowerCase) typeCount++;
        if (hasDigit) typeCount++;
        if (hasUnderscore) typeCount++;
        
        if (typeCount < 3) {
            // 无论密码包含哪些类型，统一提示需要包含的四种类型
            return "密码必须同时包含大写字母、小写字母、数字、下划线中的至少三种";
        }
        
        return null; // 密码符合要求
    }
}

