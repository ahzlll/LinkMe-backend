package com.linkme.backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI配置类
 * 
 * 功能描述：
 * - 配置Swagger API文档
 * - 提供API接口文档和测试功能
 * 
 * @author Ahz, riki
 * @version 1.1
 */
@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        final String bearerKey = "bearerAuth";
        return new OpenAPI()
                .components(new Components().addSecuritySchemes(bearerKey,
                        new SecurityScheme()
                                .name(bearerKey)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList(bearerKey))
                .info(new Info()
                        .title("LinkMe交友聊天社交软件API")
                        .description("LinkMe交友聊天社交软件后端API接口文档")
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