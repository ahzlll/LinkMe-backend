package com.linkme.backend.controller.dto;

import lombok.Data;

import java.time.LocalDate;

/**
 * 匹配推荐返回 DTO
 * - 返回给前端用于展示推荐用户与匹配度
 *
 * @author riki
 * @version 1.0
 */
@Data
public class MatchRecommendationResponse {
    /** 用户ID */
    private Integer userId;

    /** 昵称 */
    private String nickname;

    /** 性别 */
    private String gender;

    /** 生日 */
    private LocalDate birthday;

    /** 地区 */
    private String region;

    /** 头像 */
    private String avatarUrl;

    /** 简介 */
    private String bio;

    /** 匹配度（0~100） */
    private Integer matchScore;
}
