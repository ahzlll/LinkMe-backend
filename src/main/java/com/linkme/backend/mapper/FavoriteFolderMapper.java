package com.linkme.backend.mapper;

import com.linkme.backend.entity.FavoriteFolder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 收藏夹数据访问层接口
 * 
 * 功能描述：
 * - 提供收藏夹数据的增删改查操作
 * - 支持收藏夹的创建和管理
 * 
 * @author Ahz
 * @version 1.0
 */
@Mapper
public interface FavoriteFolderMapper {
    
    /**
     * 根据收藏夹ID查询收藏夹信息
     * 
     * @param folderId 收藏夹ID
     * @return 收藏夹信息
     */
    FavoriteFolder selectById(@Param("folderId") Integer folderId);
    
    /**
     * 根据用户ID查询收藏夹列表
     * 
     * @param userId 用户ID
     * @return 收藏夹列表
     */
    List<FavoriteFolder> selectByUserId(@Param("userId") Integer userId);
    
    /**
     * 根据用户ID和收藏夹名称查询收藏夹
     * 
     * @param userId 用户ID
     * @param name 收藏夹名称
     * @return 收藏夹信息
     */
    FavoriteFolder selectByUserIdAndName(@Param("userId") Integer userId, @Param("name") String name);
    
    /**
     * 插入新收藏夹
     * 
     * @param folder 收藏夹信息
     * @return 影响行数
     */
    int insert(FavoriteFolder folder);
    
    /**
     * 更新收藏夹信息
     * 
     * @param folder 收藏夹信息
     * @return 影响行数
     */
    int update(FavoriteFolder folder);
    
    /**
     * 根据收藏夹ID删除收藏夹
     * 
     * @param folderId 收藏夹ID
     * @return 影响行数
     */
    int deleteById(@Param("folderId") Integer folderId);
}

