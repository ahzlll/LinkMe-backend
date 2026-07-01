package com.linkme.backend.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AuditLog {
    private Long id;
    private Long userId;
    private String contentType;
    private Long contentId;
    private String content;
    private Integer isViolation;
    private String matchedWords;
    private String categories;
    private Integer auditResult;
    private Long auditorId;
    private String auditRemark;
    private LocalDateTime createTime;
    private LocalDateTime auditTime;

    /** 查询关联：发布者昵称 */
    private String userNickname;
    /** 查询关联：审核员昵称 */
    private String auditorNickname;
    
    private Long targetId;
    private Integer targetType;
    private String action;
    private String reason;

    public static final int RESULT_AUTO_PASS = 0;
    public static final int RESULT_NEED_MANUAL = 1;
    public static final int RESULT_MANUAL_PASS = 2;
    public static final int RESULT_MANUAL_REJECT = 3;
    public static final int RESULT_OFFLINE = 4;
}