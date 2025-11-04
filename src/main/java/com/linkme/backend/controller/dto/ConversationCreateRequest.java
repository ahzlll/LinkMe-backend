package com.linkme.backend.controller.dto;

import lombok.Data;

/**
 * 会话创建请求DTO
 * 
 * 功能描述：
 * - 用于创建新的聊天会话
 * - 包含参与会话的用户ID
 * 
 * @author Ahz
 * @version 1.2
 */
@Data
public class ConversationCreateRequest {
    /**
     * 另一个用户的ID（会话的另一方）
     */
    private Integer userId;
}

