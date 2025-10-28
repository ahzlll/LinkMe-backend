package com.linkme.backend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PostTagMapper {
    List<Integer> selectTagIdsByPostId(@Param("postId") Integer postId);
    int insertBatch(@Param("postId") Integer postId, @Param("tagIds") List<Integer> tagIds);
    int deleteByPostId(@Param("postId") Integer postId);
}


