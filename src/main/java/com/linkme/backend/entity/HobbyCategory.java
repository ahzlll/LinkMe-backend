package com.linkme.backend.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 爱好分类实体类，对应表 hobby_category。
 */
@Data
public class HobbyCategory {
    private Integer categoryId;
    private String name;
    private String icon;
    private Integer displayOrder;
    private LocalDateTime createdAt;
}
