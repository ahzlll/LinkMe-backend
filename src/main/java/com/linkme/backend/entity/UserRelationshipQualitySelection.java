package com.linkme.backend.entity;

import lombok.Data;

/**
 * 用户关系品质选择（查询视图）
 *
 * @author riki
 * @version 1.0
 */
@Data
public class UserRelationshipQualitySelection {
    private Integer userId;
    private Integer qualityId;
}
