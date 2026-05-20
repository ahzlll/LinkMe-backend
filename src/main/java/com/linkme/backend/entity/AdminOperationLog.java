package com.linkme.backend.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AdminOperationLog {
    private Long id;
    private Integer adminId;
    private Integer targetUserId;
    private Long targetId;
    private Integer targetType;
    private String action;
    private String reason;
    private String detail;
    private LocalDateTime createTime;
    private String adminNickname;
}
