package com.linkme.backend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 帖子tag访问层接口
 * 
 * 功能描述：
 * - 允许发贴带tag
 * 
 * @author riki
 * @version 1.0
 */
@Mapper
public interface PostTagMapper {
    List<Integer> selectTagIdsByPostId(@Param("postId") Integer postId);
    int insertBatch(@Param("postId") Integer postId, @Param("tagIds") List<Integer> tagIds);
    int deleteByPostId(@Param("postId") Integer postId);
}


