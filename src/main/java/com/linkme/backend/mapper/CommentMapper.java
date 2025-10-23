package com.linkme.backend.mapper;

import com.linkme.backend.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 评论数据访问层接口
 * 
 * 功能描述：
 * - 提供评论数据的增删改查操作
 * - 支持评论发布、回复、删除等功能
 * 
 * @author Ahz, riki
 * @version 1.0
 */
@Mapper
public interface CommentMapper {
    
    /**
     * 根据评论ID查询评论信息
     * 
     * @param commentId 评论ID
     * @return 评论信息
     */
    Comment selectById(@Param("commentId") Integer commentId);
    
    /**
     * 根据帖子ID查询评论列表
     * 
     * @param postId 帖子ID
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 评论列表
     */
    List<Comment> selectByPostId(@Param("postId") Integer postId, @Param("offset") Integer offset, @Param("limit") Integer limit);
    
    /**
     * 根据用户ID查询评论列表
     * 
     * @param userId 用户ID
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 评论列表
     */
    List<Comment> selectByUserId(@Param("userId") Integer userId, @Param("offset") Integer offset, @Param("limit") Integer limit);
    
    /**
     * 根据父评论ID查询回复列表
     * 
     * @param parentId 父评论ID
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 回复列表
     */
    List<Comment> selectByParentId(@Param("parentId") Integer parentId, @Param("offset") Integer offset, @Param("limit") Integer limit);
    
    /**
     * 插入新评论
     * 
     * @param comment 评论信息
     * @return 影响行数
     */
    int insert(Comment comment);
    
    /**
     * 更新评论信息
     * 
     * @param comment 评论信息
     * @return 影响行数
     */
    int update(Comment comment);
    
    /**
     * 根据评论ID删除评论
     * 
     * @param commentId 评论ID
     * @return 影响行数
     */
    int deleteById(@Param("commentId") Integer commentId);
    
    /**
     * 根据帖子ID统计评论数量
     * 
     * @param postId 帖子ID
     * @return 评论数量
     */
    int countByPostId(@Param("postId") Integer postId);
}
