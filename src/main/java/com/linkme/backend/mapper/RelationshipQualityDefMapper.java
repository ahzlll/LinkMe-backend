package com.linkme.backend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RelationshipQualityDefMapper {
    Integer selectIdByName(@Param("name") String name);
    String selectNameById(@Param("qualityId") Integer qualityId);
}
