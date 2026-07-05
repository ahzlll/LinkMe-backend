package com.linkme.backend.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "用户登录请求")
public class LoginRequest {

    @Schema(
            description = "登录名，支持邮箱、手机号或用户名",
            example = "user@example.com",
            required = true
    )
    private String loginName;

    @Schema(
            description = "用户密码",
            example = "password123",
            required = true
    )
    private String password;
}
