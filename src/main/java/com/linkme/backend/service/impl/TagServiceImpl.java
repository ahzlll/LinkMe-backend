package com.linkme.backend.service.impl;

import com.linkme.backend.entity.TagDef;
import com.linkme.backend.mapper.TagDefMapper;
import com.linkme.backend.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 标签服务实现类
 *
 * author: riki
 * version: 1.0
 */
@Service
public class TagServiceImpl implements TagService {

    @Autowired
    private TagDefMapper tagDefMapper;

    @Override
    public List<TagDef> getTagsByType(String tagType) {
        return tagDefMapper.selectByType(tagType);
    }

    @Override
    public TagDef createTag(String name, String tagType, Integer createdBy) {
        // 先判断是否已存在相同名称 + 类型的标签
        TagDef exists = tagDefMapper.selectByNameAndType(name, tagType);
        if (exists != null) {
            return exists;
        }
        TagDef tag = new TagDef();
        tag.setName(name);
        tag.setTagType(tagType);
        tag.setCreatedBy(createdBy);
        tagDefMapper.insert(tag);
        return tag;
    }
}


