package com.linkme.backend.util;

/**
 * 密码校验工具类
 * 规则：
 * - 密码不能为空
 * - 长度不少于 8 位
 */
public class PasswordValidator {

    public static boolean isValid(String password) {
        return password != null && !password.isEmpty() && password.length() >= 8;
    }

    public static String getErrorMessage(String password) {
        if (password == null || password.isEmpty()) {
            return "密码不能为空";
        }

        if (password.length() < 8) {
            return "密码长度不能少于 8 位";
        }

        return null;
    }
}
