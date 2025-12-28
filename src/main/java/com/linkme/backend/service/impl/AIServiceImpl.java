package com.linkme.backend.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.linkme.backend.controller.dto.AIAnalysisRequest;
import com.linkme.backend.controller.dto.AIAnalysisResponse;
import com.linkme.backend.service.AIService;
import com.linkme.backend.service.AISettingsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
/**
 * AI情感聊天助手服务实现
 *
 * 职责：
 * - 根据对话历史构造提示词，调用外部AI接口生成情感提示与回复建议
 * - 支持失败重试与降级方案
 * - 受全局开关控制（关闭时直接返回降级提示，不调用外部服务）
 *
 * @author riki
 * @version 1.0
 */
public class AIServiceImpl implements AIService {

    @Value("${ai.api-url}")
    private String apiUrl;

    @Value("${ai.api-key}")
    private String apiKey;

    @Value("${ai.model}")
    private String model;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final AISettingsService settingsService;

    public AIServiceImpl(AISettingsService settingsService) {
        this.settingsService = settingsService;
    }

    @Override
    public AIAnalysisResponse analyzeChat(AIAnalysisRequest request) {
        /**
         * 开关判断：关闭时直接返回降级提示
         */
        if (!settingsService.isEnabled()) {
            AIAnalysisResponse r = new AIAnalysisResponse();
            r.setTip("AI助手已关闭。");
            r.setSuggestion("AI已关闭。");
            return r;
        }
        int maxRetries = 3;
        int retryCount = 0;
        Exception lastException = null;

        while (retryCount < maxRetries) {
            try {
                // 构造提示词
                StringBuilder prompt = new StringBuilder();
                prompt.append("你是一个情感助手。请分析以下对话历史，并为当前用户（在对话中标识为'我'）给出建议。\n");
                prompt.append("对话参与者：\n");
                prompt.append("- 当前用户（我）：").append(request.getMyName() != null ? request.getMyName() : "用户")
                        .append("\n");
                prompt.append("- 对方用户：").append(request.getOtherUserName() != null ? request.getOtherUserName() : "对方")
                        .append("\n");

                prompt.append("\n请基于对话历史，给出两个输出：\n");
                prompt.append("1. 'tip': 分析当前对话氛围，给当前用户（我）的情感建议（简短，50字以内）。\n");
                prompt.append("2. 'suggestion': 站在当前用户（我）的角度，拟写一条回复给对方的消息（简短，30字以内）。\n");
                prompt.append("请严格以JSON格式返回，格式为：{\"tip\": \"...\", \"suggestion\": \"...\"}\n\n");
                prompt.append("对话历史：\n");

                List<String> messages = request.getMessages();
                // 取最近的10条消息
                int start = Math.max(0, messages.size() - 10);
                for (int i = start; i < messages.size(); i++) {
                    prompt.append(messages.get(i)).append("\n");
                }

                // 构造请求体
                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("model", model);

                Map<String, String> message = new HashMap<>();
                message.put("role", "user");
                message.put("content", prompt.toString());

                requestBody.put("messages", new Object[] { message });
                requestBody.put("temperature", 0.7);

                // 设置请求头
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.setBearerAuth(apiKey);

                HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

                // 发送请求
                ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, entity, String.class);

                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                    JsonNode root = objectMapper.readTree(response.getBody());
                    String content = root.path("choices").get(0).path("message").path("content").asText();

                    // 清理可能的Markdown代码块标记
                    content = content.replace("```json", "").replace("```", "").trim();

                    // 解析返回的JSON
                    JsonNode result = objectMapper.readTree(content);
                    AIAnalysisResponse analysisResponse = new AIAnalysisResponse();
                    analysisResponse.setTip(result.path("tip").asText("保持自然交流。"));
                    analysisResponse.setSuggestion(result.path("suggestion").asText("你好！"));
                    return analysisResponse;
                }
            } catch (Exception e) {
                lastException = e;
                retryCount++;
                System.err.println("AI服务调用失败，正在重试 (" + retryCount + "/" + maxRetries + "): " + e.getMessage());
                if (retryCount < maxRetries) {
                    try {
                        Thread.sleep(1000); // 等待1秒后重试
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }

        if (lastException != null) {
            lastException.printStackTrace();
        }

        // 默认降级方案
        AIAnalysisResponse fallback = new AIAnalysisResponse();
        fallback.setTip("AI服务暂时不可用，请保持真诚的交流。");
        fallback.setSuggestion("听起来不错！");
        return fallback;
    }
}
