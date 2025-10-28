package com.linkme.backend.mapper;

import com.linkme.backend.entity.PostImage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PostImageMapper {
    List<PostImage> selectByPostId(@Param("postId") Integer postId);
    int insertBatch(@Param("postId") Integer postId, @Param("images") List<String> images);
    int deleteByPostId(@Param("postId") Integer postId);
}


