package com.linkme.backend.controller.dto;

import lombok.Data;

@Data
public class ContentModerateRequest {
    /** hide | delete | approve | reject */
    private String action;
    private String reason;
}
