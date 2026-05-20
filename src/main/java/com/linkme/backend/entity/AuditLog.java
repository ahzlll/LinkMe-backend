package com.linkme.backend.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AuditLog {
    private Long id;
    private Long targetId;
    private Integer targetType;
    private Long auditorId;
    private String action;
    private String reason;
    private LocalDateTime createTime;
    private String auditorNickname;
}
