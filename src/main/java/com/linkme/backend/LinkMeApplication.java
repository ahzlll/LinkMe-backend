package com.linkme.backend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * LinkMe交友聊天社交软件后端应用启动类
 * 
 * 功能描述：
 * - 交友聊天社交软件后端服务
 * - 支持用户管理、帖子发布、匹配交友、聊天通信等功能
 * 
 * @author Ahz, riki
 * @version 1.0
 */
@SpringBootApplication
@MapperScan("com.linkme.backend.mapper")
public class LinkMeApplication {
    public static void main(String[] args) {
        SpringApplication.run(LinkMeApplication.class, args);
    }
}