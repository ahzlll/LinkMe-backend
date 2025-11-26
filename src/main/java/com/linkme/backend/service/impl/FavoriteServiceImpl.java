package com.linkme.backend.service.impl;

import com.linkme.backend.entity.Favorite;
import com.linkme.backend.entity.FavoriteFolder;
import com.linkme.backend.mapper.FavoriteFolderMapper;
import com.linkme.backend.mapper.FavoriteMapper;
import com.linkme.backend.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 收藏服务实现类
 * 
 * 功能描述：
 * - 实现收藏相关的业务逻辑
 * 
 * @author Ahz
 * @version 1.0
 */
@Service
public class FavoriteServiceImpl implements FavoriteService {
    
    @Autowired
    private FavoriteFolderMapper favoriteFolderMapper;
    
    @Autowired
    private FavoriteMapper favoriteMapper;
    
    @Override
    public List<FavoriteFolder> getFavoriteFolders(Integer userId) {
        return favoriteFolderMapper.selectByUserId(userId);
    }
    
    @Override
    public FavoriteFolder createFavoriteFolder(Integer userId, String name) {
        // 检查是否已存在同名收藏夹
        FavoriteFolder existing = favoriteFolderMapper.selectByUserIdAndName(userId, name);
        if (existing != null) {
            return null; // 名称已存在
        }
        
        FavoriteFolder folder = new FavoriteFolder();
        folder.setUserId(userId);
        folder.setName(name);
        folder.setCreatedAt(LocalDateTime.now());
        
        int result = favoriteFolderMapper.insert(folder);
        if (result > 0) {
            return favoriteFolderMapper.selectById(folder.getFolderId());
        }
        return null;
    }
    
    @Override
    public boolean updateFavoriteFolder(Integer userId, Integer folderId, String name) {
        FavoriteFolder folder = favoriteFolderMapper.selectById(folderId);
        if (folder == null || !folder.getUserId().equals(userId)) {
            return false; // 收藏夹不存在或无权限
        }
        
        // 检查新名称是否与其他收藏夹冲突
        FavoriteFolder existing = favoriteFolderMapper.selectByUserIdAndName(userId, name);
        if (existing != null && !existing.getFolderId().equals(folderId)) {
            return false; // 名称已存在
        }
        
        folder.setName(name);
        return favoriteFolderMapper.update(folder) > 0;
    }
    
    @Override
    public boolean deleteFavoriteFolder(Integer userId, Integer folderId) {
        FavoriteFolder folder = favoriteFolderMapper.selectById(folderId);
        if (folder == null || !folder.getUserId().equals(userId)) {
            return false; // 收藏夹不存在或无权限
        }
        
        return favoriteFolderMapper.deleteById(folderId) > 0;
    }
    
    @Override
    public List<Favorite> getUserFavorites(Integer userId, Integer folderId, Integer offset, Integer limit) {
        return favoriteMapper.selectByUserId(userId, folderId, offset, limit);
    }
    
    @Override
    public boolean favoritePost(Integer userId, Integer postId, Integer folderId) {
        // 检查是否已收藏
        Favorite existing = favoriteMapper.selectByUserAndPost(userId, postId, folderId);
        if (existing != null) {
            return true; // 已收藏
        }
        
        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setPostId(postId);
        favorite.setFolderId(folderId);
        favorite.setCreatedAt(LocalDateTime.now());
        
        return favoriteMapper.insert(favorite) > 0;
    }
    
    @Override
    public boolean unfavoritePost(Integer userId, Integer postId, Integer folderId) {
        return favoriteMapper.deleteByUserAndPost(userId, postId, folderId) > 0;
    }
    
    @Override
    public boolean unfavoriteById(Integer userId, Integer favoriteId) {
        Favorite favorite = favoriteMapper.selectById(favoriteId);
        if (favorite == null || !favorite.getUserId().equals(userId)) {
            return false; // 收藏不存在或无权限
        }
        
        return favoriteMapper.deleteById(favoriteId) > 0;
    }
}

