package com.linkme.backend.service;

import com.linkme.backend.entity.User;
import java.util.List;

/**
 * 用户服务接口
 * 
 * 功能描述：
 * - 提供用户相关的业务逻辑处理
 * - 包括用户注册、登录、信息管理等功能
 * 
 * @author Ahz
 * @version 1.2.2
 */
public interface UserService {
    
    /**
     * 根据用户ID获取用户信息
     * 
     * @param userId 用户ID
     * @return 用户信息
     */
    User getUserById(Integer userId);
    
    /**
     * 根据邮箱获取用户信息
     * 
     * @param email 邮箱
     * @return 用户信息
     */
    User getUserByEmail(String email);
    
    /**
     * 根据手机号获取用户信息
     * 
     * @param phone 手机号
     * @return 用户信息
     */
    User getUserByPhone(String phone);
    
    /**
     * 根据用户名获取用户信息
     * 
     * @param username 用户名
     * @return 用户信息
     */
    User getUserByUsername(String username);
    
    /**
     * 用户注册
     * 
     * @param user 用户信息
     * @return 注册结果
     */
    boolean register(User user);
    
    /**
     * 用户登录
     * 
     * @param loginName 登录名（邮箱/手机号/用户名）
     * @param password 密码
     * @return 登录结果
     */
    User login(String loginName, String password);
    
    /**
     * 更新用户信息
     * 
     * @param user 用户信息
     * @return 更新结果
     */
    boolean updateUser(User user);
    
    /**
     * 修改密码
     * 
     * @param userId 用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 更新结果
     */
    boolean changePassword(Integer userId, String oldPassword, String newPassword);
    
    /**
     * 重置密码（忘记密码时使用，不需要旧密码）
     * 
     * @param userId 用户ID
     * @param newPassword 新密码
     * @return 更新结果
     */
    boolean resetPassword(Integer userId, String newPassword);
    
    /**
     * 删除用户
     * 
     * @param userId 用户ID
     * @return 删除结果
     */
    boolean deleteUser(Integer userId);
    
    /**
     * 获取用户列表（分页）
     * 
     * @param page 页码
     * @param size 每页数量
     * @return 用户列表
     */
    List<User> getUserList(Integer page, Integer size);
    
    /**
     * 获取用户总数
     * 
     * @return 用户总数
     */
    int getUserCount();
    
    /**
     * 获取用户点赞列表
     * 
     * @param userId 用户ID
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 点赞列表
     */
    List<com.linkme.backend.entity.Like> getUserLikes(Integer userId, Integer offset, Integer limit);
    
    /**
     * 获取用户收藏列表
     * 
     * @param userId 用户ID
     * @param folderId 收藏夹ID（可选）
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 收藏列表
     */
    List<com.linkme.backend.entity.Favorite> getUserFavorites(Integer userId, Integer folderId, Integer offset, Integer limit);
    
    /**
     * 获取用户的收藏夹列表
     * 
     * @param userId 用户ID
     * @return 收藏夹列表
     */
    List<com.linkme.backend.entity.FavoriteFolder> getFavoriteFolders(Integer userId);
    
    /**
     * 创建收藏夹
     * 
     * @param userId 用户ID
     * @param name 收藏夹名称
     * @return 创建的收藏夹信息
     */
    com.linkme.backend.entity.FavoriteFolder createFavoriteFolder(Integer userId, String name);
    
    /**
     * 删除收藏夹
     * 
     * @param userId 用户ID
     * @param folderId 收藏夹ID
     * @return 删除结果
     */
    boolean deleteFavoriteFolder(Integer userId, Integer folderId);
    
    /**
     * 关注用户
     * 
     * @param followerId 关注者ID
     * @param followeeId 被关注者ID
     * @return 关注结果
     */
    boolean followUser(Integer followerId, Integer followeeId);
    
    /**
     * 取消关注用户
     * 
     * @param followerId 关注者ID
     * @param followeeId 被关注者ID
     * @return 取消关注结果
     */
    boolean unfollowUser(Integer followerId, Integer followeeId);
    
    /**
     * 检查是否关注某用户
     * 
     * @param followerId 关注者ID
     * @param followeeId 被关注者ID
     * @return 是否关注
     */
    boolean isFollowing(Integer followerId, Integer followeeId);
    
    /**
     * 屏蔽用户
     * 
     * @param blockerId 屏蔽者ID
     * @param blockedId 被屏蔽者ID
     * @return 屏蔽结果
     */
    boolean blockUser(Integer blockerId, Integer blockedId);
}
