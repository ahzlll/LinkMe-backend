package com.linkme.backend.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ManualReviewQueue {
    private Long id;
    private Long userId;
    private String contentType;
    private Long contentId;
    private String content;
    private String matchedWords;
    private String categories;
    private Integer status;
    private Long reviewerId;
    private String reviewRemark;
    private LocalDateTime createTime;
    private LocalDateTime reviewTime;

    public static final int STATUS_PENDING = 0;
    public static final int STATUS_APPROVED = 1;
    public static final int STATUS_REJECTED = 2;
}