package com.linkme.backend.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 登录请求DTO
 * 
 * 功能描述：
 * - 用于接收用户登录请求
 * - 支持邮箱、手机号或用户名登录
 * 
 * @author Ahz
 * @version 1.2.2
 */
@Data
@Schema(description = "用户登录请求")
public class LoginRequest {
    
    /**
     * 登录名（支持邮箱、手机号或用户名）
     * 示例：
     * - 邮箱: user@example.com
     * - 手机号: 13800138000
     * - 用户名: testuser
     */
    @Schema(
        description = "登录名，支持邮箱、手机号或用户名",
        example = "user@example.com",
        required = true
    )
    private String loginName;
    
    /**
     * 密码
     */
    @Schema(
        description = "用户密码",
        example = "password123",
        required = true
    )
    private String password;
}


