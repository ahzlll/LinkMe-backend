package com.linkme.backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * OpenAPI配置类
 * 
 * 功能描述：
 * - 配置Swagger API文档
 * - 提供API接口文档和测试功能
 * - 配置JWT认证方案（但不强制所有接口使用，具体接口可选择性使用）
 * 
 * @author Ahz, riki
 * @version 1.1
 */
@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        final String bearerKey = "bearerAuth";
        
        // 配置服务器列表
        List<Server> servers = new ArrayList<>();
        servers.add(new Server().url("http://localhost:8080").description("本地开发环境"));
        servers.add(new Server().url("https://api.linkme.com").description("生产环境"));
        
        // 创建SecurityScheme
        SecurityScheme bearerAuth = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("JWT Bearer Token认证\n\n" +
                        "使用说明：\n" +
                        "1. 首先调用注册或登录接口获取token\n" +
                        "2. 点击右上角的🔒图标（Authorize按钮）\n" +
                        "3. 在弹出框中输入token（不需要包含'Bearer'前缀）\n" +
                        "4. 点击'Authorize'按钮保存\n" +
                        "5. 现在可以测试需要认证的接口了");
        
        return new OpenAPI()
                // 配置JWT认证方案
                .components(new Components()
                        .addSecuritySchemes(bearerKey, bearerAuth))
                // 配置服务器列表
                .servers(servers)
                // 配置API信息
                .info(new Info()
                        .title("LinkMe交友聊天社交软件API")
                        .description("LinkMe交友聊天社交软件后端API接口文档\n\n" +
                                "**认证说明：**\n" +
                                "- 注册和登录接口无需Token\n" +
                                "- 其他接口需要在请求头中携带Token: `Authorization: Bearer {token}`\n" +
                                "- 登录成功后返回的token可用于后续请求认证\n" +
                                "- 在Swagger UI中点击右上角的🔒图标（Authorize按钮）可以输入token")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("LinkMe Team")
                                .email("contact@linkme.com")
                                .url("https://www.linkme.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")));
    }
}