package com.linkme.backend.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 消息实体类
 * 
 * 功能描述：
 * - 存储会话中的消息记录
 * - 支持文字、图片、视频、语音通话等多种消息类型
 * 
 * 输入输出示例：
 * - 输入：会话ID、发送者ID、内容类型、消息内容
 * - 输出：消息详细信息
 * 
 * @author Ahz
 * @version 1.0
 */
@Data
public class Message {
    /**
     * 消息ID - 主键，自增
     */
    private Integer messageId;
    
    /**
     * 会话ID - 外键，所属会话
     */
    private Integer conversationId;
    
    /**
     * 发送者ID - 外键，消息发送者
     */
    private Integer senderId;
    
    /**
     * 内容类型 - 消息类型（text/image/video/call）
     */
    private String contentType;
    
    /**
     * 内容 - 文本内容或媒体链接
     */
    private String content;
    
    /**
     * 是否已读 - 消息是否已被接收者阅读
     */
    private Boolean isRead;
    
    /**
     * 创建时间 - 发送时间
     */
    private LocalDateTime createdAt;
}
