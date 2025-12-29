package com.linkme.backend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PersonalityTraitOptionMapper {
    Integer selectOptionIdByName(@Param("name") String name);
}

