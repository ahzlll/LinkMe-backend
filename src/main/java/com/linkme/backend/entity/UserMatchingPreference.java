package com.linkme.backend.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户匹配偏好实体类
 *
 * 对应表：user_matching_preference
 * - 存储用户的年龄范围、距离偏好、关系模式/沟通期待等匹配偏好
 *
 * @author riki
 * @version 1.0
 */
@Data
public class UserMatchingPreference {
    /** 偏好ID */
    private Integer preferenceId;

    /** 用户ID */
    private Integer userId;

    /** 最小年龄要求 */
    private Integer ageMin;

    /** 最大年龄要求 */
    private Integer ageMax;

    /** 是否无年龄限制 */
    private Boolean ageUnlimited;

    /** 距离偏好：same_city / same_city_or_remote / unlimited */
    private String distancePreference;

    /** 理想关系模式ID（可为空） */
    private Integer relationshipModeId;

    /** 沟通期待ID（可为空） */
    private Integer communicationExpectationId;

    /** 其他未覆盖的交友要求（文本） */
    private String additionalRequirements;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 更新时间 */
    private LocalDateTime updatedAt;
}
