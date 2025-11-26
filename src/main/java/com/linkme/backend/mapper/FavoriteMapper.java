package com.linkme.backend.mapper;

import com.linkme.backend.entity.Favorite;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 收藏数据访问层接口
 * 
 * 功能描述：
 * - 提供收藏数据的增删改查操作
 * - 支持收藏和取消收藏功能
 * 
 * @author Ahz
 * @version 1.0
 */
@Mapper
public interface FavoriteMapper {
    
    /**
     * 根据用户ID和帖子ID查询收藏信息
     * 
     * @param userId 用户ID
     * @param postId 帖子ID
     * @param folderId 收藏夹ID（可选）
     * @return 收藏信息
     */
    Favorite selectByUserAndPost(@Param("userId") Integer userId, 
                                 @Param("postId") Integer postId,
                                 @Param("folderId") Integer folderId);
    
    /**
     * 根据用户ID查询收藏列表
     * 
     * @param userId 用户ID
     * @param folderId 收藏夹ID（可选）
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 收藏列表
     */
    List<Favorite> selectByUserId(@Param("userId") Integer userId,
                                  @Param("folderId") Integer folderId,
                                  @Param("offset") Integer offset,
                                  @Param("limit") Integer limit);
    
    /**
     * 根据收藏夹ID查询收藏列表
     * 
     * @param folderId 收藏夹ID
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 收藏列表
     */
    List<Favorite> selectByFolderId(@Param("folderId") Integer folderId,
                                     @Param("offset") Integer offset,
                                     @Param("limit") Integer limit);
    
    /**
     * 插入新收藏
     * 
     * @param favorite 收藏信息
     * @return 影响行数
     */
    int insert(Favorite favorite);
    
    /**
     * 根据用户ID和帖子ID删除收藏
     * 
     * @param userId 用户ID
     * @param postId 帖子ID
     * @param folderId 收藏夹ID（可选）
     * @return 影响行数
     */
    int deleteByUserAndPost(@Param("userId") Integer userId,
                            @Param("postId") Integer postId,
                            @Param("folderId") Integer folderId);
    
    /**
     * 根据收藏ID查询收藏信息
     * 
     * @param favoriteId 收藏ID
     * @return 收藏信息
     */
    Favorite selectById(@Param("favoriteId") Integer favoriteId);
    
    /**
     * 根据收藏ID删除收藏
     * 
     * @param favoriteId 收藏ID
     * @return 影响行数
     */
    int deleteById(@Param("favoriteId") Integer favoriteId);
    
    /**
     * 根据帖子ID统计收藏数量
     * 
     * @param postId 帖子ID
     * @return 收藏数量
     */
    int countByPostId(@Param("postId") Integer postId);
    
    /**
     * 根据用户ID统计收藏数量
     * 
     * @param userId 用户ID
     * @return 收藏数量
     */
    int countByUserId(@Param("userId") Integer userId);
}

