package com.linkme.backend.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 爱好实体类
 *
 * 对应表：hobby
 * - 爱好属于某个分类（category_id）
 *
 * @author riki
 * @version 1.0
 */
@Data
public class Hobby {
    /** 爱好ID */
    private Integer hobbyId;

    /** 爱好分类ID */
    private Integer categoryId;

    /** 爱好名称 */
    private String name;

    /** 显示顺序 */
    private Integer displayOrder;

    /** 创建时间 */
    private LocalDateTime createdAt;
}
