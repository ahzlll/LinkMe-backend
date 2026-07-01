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
    /** 查询关联：目标用户昵称 */
    private String targetUserNickname;
}
