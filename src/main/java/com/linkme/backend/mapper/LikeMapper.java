package com.linkme.backend.mapper;

import com.linkme.backend.entity.Like;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 点赞数据访问层接口
 * 
 * 功能描述：
 * - 提供点赞数据的增删改查操作
 * - 支持点赞和取消点赞功能
 * 
 * @author Ahz, riki
 * @version 1.0
 */
@Mapper
public interface LikeMapper {
    
    /**
     * 根据用户ID和帖子ID查询点赞信息
     * 
     * @param userId 用户ID
     * @param postId 帖子ID
     * @return 点赞信息
     */
    Like selectByUserAndPost(@Param("userId") Integer userId, @Param("postId") Integer postId);
    
    /**
     * 根据帖子ID查询点赞列表
     * 
     * @param postId 帖子ID
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 点赞列表
     */
    List<Like> selectByPostId(@Param("postId") Integer postId, @Param("offset") Integer offset, @Param("limit") Integer limit);
    
    /**
     * 根据用户ID查询点赞列表
     * 
     * @param userId 用户ID
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 点赞列表
     */
    List<Like> selectByUserId(@Param("userId") Integer userId, @Param("offset") Integer offset, @Param("limit") Integer limit);
    
    /**
     * 插入新点赞
     * 
     * @param like 点赞信息
     * @return 影响行数
     */
    int insert(Like like);
    
    /**
     * 根据用户ID和帖子ID删除点赞
     * 
     * @param userId 用户ID
     * @param postId 帖子ID
     * @return 影响行数
     */
    int deleteByUserAndPost(@Param("userId") Integer userId, @Param("postId") Integer postId);
    
    /**
     * 根据帖子ID统计点赞数量
     * 
     * @param postId 帖子ID
     * @return 点赞数量
     */
    int countByPostId(@Param("postId") Integer postId);
    
    /**
     * 根据用户ID统计点赞数量
     * 
     * @param userId 用户ID
     * @return 点赞数量
     */
    int countByUserId(@Param("userId") Integer userId);
}
