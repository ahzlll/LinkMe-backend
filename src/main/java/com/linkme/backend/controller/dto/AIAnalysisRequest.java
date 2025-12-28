package com.linkme.backend.controller.dto;

import lombok.Data;
import java.util.List;

@Data
public class AIAnalysisRequest {
    private List<String> messages;
    private String otherUserName;
    private String myName;
}
