package com.linkme.backend.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户爱好关联实体类
 *
 * 对应表：user_hobby
 * - 记录用户选择的爱好（多选）
 *
 * @author riki
 * @version 1.0
 */
@Data
public class UserHobby {
    /** 用户ID */
    private Integer userId;

    /** 爱好ID */
    private Integer hobbyId;

    /** 创建时间 */
    private LocalDateTime createdAt;
}
