package com.linkme.backend.controller;

import com.linkme.backend.common.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 首页控制器
 * 
 * 功能描述：
 * - 提供根路径的欢迎信息
 * - 返回API文档链接
 * 
 * @author Ahz
 * @version 1.1
 */
@RestController
public class HomeController {
    
    /**
     * 根路径欢迎信息
     * 
     * @return 欢迎信息和API文档链接
     */
    @GetMapping("/")
    public R<Map<String, Object>> home() {
        Map<String, Object> data = new HashMap<>();
        data.put("message", "欢迎使用 LinkMe 交友聊天社交软件后端服务");
        data.put("version", "1.1.0");
        data.put("api_docs", "/swagger-ui/index.html");
        data.put("openapi_json", "/v3/api-docs");
        return R.ok(data);
    }
}
