package com.linkme.backend.controller;

import com.linkme.backend.common.R;
import com.linkme.backend.controller.dto.AIAnalysisRequest;
import com.linkme.backend.controller.dto.AIAnalysisResponse;
import com.linkme.backend.service.AIService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai")
@Tag(name = "AI助手", description = "AI情感分析和回复建议")
public class AIController {

    @Autowired
    private AIService aiService;

    @PostMapping("/analyze")
    @Operation(summary = "分析对话", description = "根据对话历史生成情感提示和回复建议")
    public R<AIAnalysisResponse> analyzeChat(@RequestBody AIAnalysisRequest request) {
        return R.ok(aiService.analyzeChat(request));
    }
}
