package com.linkme.backend.service;

import com.linkme.backend.entity.Favorite;
import com.linkme.backend.entity.FavoriteFolder;

import java.util.List;

/**
 * 收藏服务接口
 * 
 * 功能描述：
 * - 提供收藏相关的业务逻辑
 * - 包括收藏、取消收藏、查询收藏等功能
 * 
 * @author Ahz
 * @version 1.0
 */
public interface FavoriteService {
    
    /**
     * 获取用户的收藏夹列表
     * 
     * @param userId 用户ID
     * @return 收藏夹列表
     */
    List<FavoriteFolder> getFavoriteFolders(Integer userId);
    
    /**
     * 创建收藏夹
     * 
     * @param userId 用户ID
     * @param name 收藏夹名称
     * @return 收藏夹信息
     */
    FavoriteFolder createFavoriteFolder(Integer userId, String name);
    
    /**
     * 更新收藏夹信息
     * 
     * @param userId 用户ID
     * @param folderId 收藏夹ID
     * @param name 新名称
     * @return 是否成功
     */
    boolean updateFavoriteFolder(Integer userId, Integer folderId, String name);
    
    /**
     * 删除收藏夹
     * 
     * @param userId 用户ID
     * @param folderId 收藏夹ID
     * @return 是否成功
     */
    boolean deleteFavoriteFolder(Integer userId, Integer folderId);
    
    /**
     * 获取用户的收藏列表
     * 
     * @param userId 用户ID
     * @param folderId 收藏夹ID（可选）
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 收藏列表
     */
    List<Favorite> getUserFavorites(Integer userId, Integer folderId, Integer offset, Integer limit);
    
    /**
     * 收藏帖子
     * 
     * @param userId 用户ID
     * @param postId 帖子ID
     * @param folderId 收藏夹ID
     * @return 是否成功
     */
    boolean favoritePost(Integer userId, Integer postId, Integer folderId);
    
    /**
     * 取消收藏帖子
     * 
     * @param userId 用户ID
     * @param postId 帖子ID
     * @param folderId 收藏夹ID（可选，如果为null则删除所有收藏）
     * @return 是否成功
     */
    boolean unfavoritePost(Integer userId, Integer postId, Integer folderId);
    
    /**
     * 根据收藏ID取消收藏
     * 
     * @param userId 用户ID
     * @param favoriteId 收藏ID
     * @return 是否成功
     */
    boolean unfavoriteById(Integer userId, Integer favoriteId);
}

