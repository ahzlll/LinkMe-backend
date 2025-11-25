package com.linkme.backend.service;

import com.linkme.backend.entity.TagDef;

import java.util.List;

/**
 * 标签服务接口
 *
 * 职责：
 * - 提供标签查询与创建能力
 *
 * author: riki
 * version: 1.0
 */
public interface TagService {

    /**
     * 获取指定类型的标签列表
     *
     * @param tagType 标签类型："post" 或 "user"
     * @return 标签列表
     */
    List<TagDef> getTagsByType(String tagType);

    /**
     * 创建新标签（如果已存在则直接返回已存在的标签）
     *
     * @param name 标签名称
     * @param tagType 标签类型："post" 或 "user"
     * @param createdBy 创建者用户ID（可为 null，表示系统）
     * @return 创建或已存在的标签
     */
    TagDef createTag(String name, String tagType, Integer createdBy);
}


