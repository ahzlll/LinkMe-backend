package com.linkme.backend.mapper;

import com.linkme.backend.entity.Post;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 帖子数据访问层接口
 * 
 * 功能描述：
 * - 提供帖子数据的增删改查操作
 * - 支持帖子发布、编辑、删除等功能
 * 
 * @author Ahz, riki
 * @version 1.0
 */
@Mapper
public interface PostMapper {
    
    /**
     * 根据帖子ID查询帖子信息
     * 
     * @param postId 帖子ID
     * @return 帖子信息
     */
    Post selectById(@Param("postId") Integer postId);
    
    /**
     * 根据用户ID查询帖子列表
     * 
     * @param userId 用户ID
     * @param offset 偏移量
     * @param limit 限制数量
     * @param currentUserId 当前用户ID
     * @return 帖子列表
     */
    List<Post> selectByUserId(@Param("userId") Integer userId, @Param("offset") Integer offset, @Param("limit") Integer limit, @Param("currentUserId") Integer currentUserId);
    
    /**
     * 查询所有帖子（分页）
     * 
     * @param offset 偏移量
     * @param limit 限制数量
     * @param currentUserId 当前用户ID
     * @return 帖子列表
     */
    List<Post> selectAll(@Param("offset") Integer offset, @Param("limit") Integer limit, @Param("currentUserId") Integer currentUserId);
    
    /**
     * 根据标签查询帖子列表
     * 
     * @param tagId 标签ID
     * @param offset 偏移量
     * @param limit 限制数量
     * @param currentUserId 当前用户ID
     * @return 帖子列表
     */
    List<Post> selectByTag(@Param("tagId") Integer tagId, @Param("offset") Integer offset, @Param("limit") Integer limit, @Param("currentUserId") Integer currentUserId);
    
    /**
     * 插入新帖子
     * 
     * @param post 帖子信息
     * @return 影响行数
     */
    int insert(Post post);
    
    /**
     * 更新帖子信息
     * 
     * @param post 帖子信息
     * @return 影响行数
     */
    int update(Post post);
    
    /**
     * 根据帖子ID删除帖子
     * 
     * @param postId 帖子ID
     * @return 影响行数
     */
    int deleteById(@Param("postId") Integer postId);
    
    /**
     * 统计帖子总数
     * 
     * @return 帖子总数
     */
    int countAll();
    
    /**
     * 根据用户ID统计帖子数量
     * 
     * @param userId 用户ID
     * @return 帖子数量
     */
    int countByUserId(@Param("userId") Integer userId);
}
