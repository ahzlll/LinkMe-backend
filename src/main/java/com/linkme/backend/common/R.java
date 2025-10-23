package com.linkme.backend.common;

/**
 * 统一响应结果类
 * 
 * 功能描述：
 * - 统一API响应格式
 * - 提供成功和失败的响应方法
 * 
 * @author Ahz, riki
 * @version 1.0
 */
public class R<T> {
    /**
     * 响应码
     */
    private int code;
    
    /**
     * 响应消息
     */
    private String message;
    
    /**
     * 响应数据
     */
    private T data;
    
    public R() {}
    
    public R(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
    
    /**
     * 成功响应（带数据）
     * 
     * @param data 响应数据
     * @return 响应结果
     */
    public static <T> R<T> ok(T data) {
        return new R<>(200, "success", data);
    }
    
    /**
     * 成功响应（无数据）
     * 
     * @return 响应结果
     */
    public static <T> R<T> ok() {
        return new R<>(200, "success", null);
    }
    
    /**
     * 成功响应（自定义消息）
     * 
     * @param message 响应消息
     * @return 响应结果
     */
    public static <T> R<T> ok(String message) {
        return new R<>(200, message, null);
    }
    
    /**
     * 失败响应
     * 
     * @param message 错误消息
     * @return 响应结果
     */
    public static <T> R<T> fail(String message) {
        return new R<>(500, message, null);
    }
    
    /**
     * 失败响应（自定义状态码）
     * 
     * @param code 状态码
     * @param message 错误消息
     * @return 响应结果
     */
    public static <T> R<T> fail(int code, String message) {
        return new R<>(code, message, null);
    }
    
    // Getter和Setter方法
    public int getCode() {
        return code;
    }
    
    public void setCode(int code) {
        this.code = code;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public T getData() {
        return data;
    }
    
    public void setData(T data) {
        this.data = data;
    }
}