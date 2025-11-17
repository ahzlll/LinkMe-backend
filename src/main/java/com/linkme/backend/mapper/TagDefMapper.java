package com.linkme.backend.mapper;

import com.linkme.backend.entity.TagDef;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 标签定义 Mapper
 *
 * 功能：
 * - 查询帖子标签 / 用户标签
 * - 创建新标签
 *
 * author: riki
 * version: 1.0
 */
@Mapper
public interface TagDefMapper {

    /**
     * 按类型查询标签列表
     *
     * @param tagType 标签类型："post" 或 "user"
     * @return 标签列表
     */
    List<TagDef> selectByType(@Param("tagType") String tagType);

    /**
     * 根据名称和类型查询单个标签
     *
     * @param name 标签名称
     * @param tagType 标签类型
     * @return 标签定义
     */
    TagDef selectByNameAndType(@Param("name") String name, @Param("tagType") String tagType);

    /**
     * 新增标签
     *
     * @param tag 标签定义
     * @return 影响行数
     */
    int insert(TagDef tag);
}


