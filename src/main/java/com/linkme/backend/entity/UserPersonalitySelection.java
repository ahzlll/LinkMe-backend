package com.linkme.backend.entity;

import lombok.Data;

/**
 * 用户性格特质选择（查询视图）
 *
 * @author riki
 * @version 1.0
 */
@Data
public class UserPersonalitySelection {
    private Integer userId;
    private Integer optionId;
    private Integer categoryId;
    private String traitType;

    private String categoryName;
    private String optionName;
}
