package com.linkme.backend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC配置类
 * 
 * 功能描述：
 * - 配置Web MVC相关设置
 * - 注册拦截器和跨域配置
 * 
 * @author Ahz, riki
 * @version 1.1
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    
    @Autowired
    private JwtInterceptor jwtInterceptor;
    
    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/",  // 根路径
                        "/user/register",
                        "/user/login",
                        "/user/forgot-password/send-code",
                        "/user/forgot-password/reset",
                        // Swagger/OpenAPI 相关路径
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/swagger-ui/index.html",
                        "/v3/api-docs",
                        "/v3/api-docs/**",
                        "/swagger-resources/**",
                        "/webjars/**",
                        "/swagger-ui/index.html/**",
                        "/favicon.ico",
                        "/error",
                        // 静态资源
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/**.html",
                        "/**.css",
                        "/**.js"
                );
    }
}