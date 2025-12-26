package com.linkme.backend.entity;

import lombok.Data;

/**
 * 用户优先匹配维度（查询视图）
 *
 * @author riki
 * @version 1.0
 */
@Data
public class DimensionPrioritySelection {
    private String code;
    private Integer priorityOrder;
}
