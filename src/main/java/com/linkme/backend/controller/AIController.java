package com.linkme.backend.controller;

import com.linkme.backend.common.R;
import com.linkme.backend.controller.dto.AIAnalysisRequest;
import com.linkme.backend.controller.dto.AIAnalysisResponse;
import com.linkme.backend.service.AIService;
import com.linkme.backend.service.AISettingsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * AI助手控制器
 *
 * 职责：
 * - 提供AI情感聊天助手的分析接口
 * - 提供AI助手开关的查询与切换接口
 *
 * 接口说明：
 * - POST /ai/analyze：分析对话历史，给出情感提示与回复建议
 * - GET  /ai/status：查询AI助手开关状态
 * - POST /ai/enable：开启AI助手
 * - POST /ai/disable：关闭AI助手
 *
 * @author riki
 * @version 1.0
 */
@RestController
@RequestMapping("/ai")
@Tag(name = "AI助手", description = "AI情感分析和回复建议")
public class AIController {

    @Autowired
    private AIService aiService;
    @Autowired
    private AISettingsService settingsService;

    @PostMapping("/analyze")
    @Operation(summary = "分析对话", description = "根据对话历史生成情感提示和回复建议")
    public R<AIAnalysisResponse> analyzeChat(@RequestBody AIAnalysisRequest request) {
        return R.ok(aiService.analyzeChat(request));
    }

    @GetMapping("/status")
    @Operation(summary = "获取AI助手开关状态", description = "返回AI助手是否启用")
    public R<java.util.Map<String, Object>> status() {
        java.util.Map<String, Object> m = new java.util.HashMap<>();
        m.put("enabled", settingsService.isEnabled());
        return R.ok(m);
    }

    @PostMapping("/enable")
    @Operation(summary = "开启AI助手", description = "开启AI情感聊天助手")
    public R<Void> enable() {
        settingsService.setEnabled(true);
        return R.ok();
    }

    @PostMapping("/disable")
    @Operation(summary = "关闭AI助手", description = "关闭AI情感聊天助手")
    public R<Void> disable() {
        settingsService.setEnabled(false);
        return R.ok();
    }
}
