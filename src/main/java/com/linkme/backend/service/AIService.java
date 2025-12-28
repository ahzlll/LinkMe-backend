package com.linkme.backend.service;

import com.linkme.backend.controller.dto.AIAnalysisRequest;
import com.linkme.backend.controller.dto.AIAnalysisResponse;

public interface AIService {
    AIAnalysisResponse analyzeChat(AIAnalysisRequest request);
}
