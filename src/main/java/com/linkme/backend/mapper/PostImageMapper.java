package com.linkme.backend.mapper;

import com.linkme.backend.entity.PostImage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * 帖子照片访问层接口
 * 
 * 功能描述：
 * - 允许发帖时也发照片
 * @author riki
 * @version 1.0
 */
@Mapper
public interface PostImageMapper {
    List<PostImage> selectByPostId(@Param("postId") Integer postId);
    int insertBatch(@Param("postId") Integer postId, @Param("images") List<String> images);
    int deleteByPostId(@Param("postId") Integer postId);
}


