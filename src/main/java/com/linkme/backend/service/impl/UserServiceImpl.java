package com.linkme.backend.service.impl;

import com.linkme.backend.entity.User;
import com.linkme.backend.entity.Like;
import com.linkme.backend.entity.Favorite;
import com.linkme.backend.entity.FavoriteFolder;
import com.linkme.backend.entity.Follow;
import com.linkme.backend.entity.Block;
import com.linkme.backend.mapper.UserMapper;
import com.linkme.backend.mapper.LikeMapper;
import com.linkme.backend.mapper.FavoriteMapper;
import com.linkme.backend.mapper.FavoriteFolderMapper;
import com.linkme.backend.mapper.FollowMapper;
import com.linkme.backend.mapper.BlockMapper;
import com.linkme.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户服务实现类
 * 
 * 功能描述：
 * - 实现用户相关的业务逻辑处理
 * - 包括用户注册、登录、信息管理等功能
 * 
 * @author Ahz
 * @version 1.2.2
 */
@Service
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private LikeMapper likeMapper;
    
    @Autowired
    private FavoriteMapper favoriteMapper;
    
    @Autowired
    private FavoriteFolderMapper favoriteFolderMapper;
    
    @Autowired
    private FollowMapper followMapper;
    
    @Autowired
    private BlockMapper blockMapper;
    
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    @Override
    public User getUserById(Integer userId) {
        return userMapper.selectById(userId);
    }
    
    @Override
    public User getUserByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return null;
        }
        return userMapper.selectByEmail(email.trim());
    }
    
    @Override
    public User getUserByPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return null;
        }
        return userMapper.selectByPhone(phone.trim());
    }
    
    @Override
    public User getUserByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return null;
        }
        return userMapper.selectByUsername(username.trim());
    }
    
    @Override
    public boolean register(User user) {
        try {
            // 检查邮箱是否已存在
            if (user.getEmail() != null && getUserByEmail(user.getEmail()) != null) {
                return false;
            }
            
            // 检查手机号是否已存在
            if (user.getPhone() != null && getUserByPhone(user.getPhone()) != null) {
                return false;
            }
            
            // 检查用户名是否已存在
            if (user.getUsername() != null && getUserByUsername(user.getUsername()) != null) {
                return false;
            }
            
            // 加密密码
            if (user.getPasswordHash() != null) {
                user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
            }
            
            // 设置创建时间
            user.setCreatedAt(LocalDateTime.now());
            
            // 插入用户
            int result = userMapper.insert(user);
            return result > 0;
        } catch (Exception e) {
            // 记录异常信息以便调试
            System.err.println("注册失败，异常信息: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public User login(String loginName, String password) {
        User user = null;
        
        // 尝试通过邮箱登录
        if (loginName.contains("@")) {
            user = getUserByEmail(loginName);
        } else if (loginName.matches("^1[3-9]\\d{9}$")) {
            // 尝试通过手机号登录
            user = getUserByPhone(loginName);
        } else {
            // 尝试通过用户名登录
            user = getUserByUsername(loginName);
        }
        
        if (user != null && passwordEncoder.matches(password, user.getPasswordHash())) {
            return user;
        }
        
        return null;
    }
    
    @Override
    public boolean updateUser(User user) {
        try {
            return userMapper.update(user) > 0;
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public boolean changePassword(Integer userId, String oldPassword, String newPassword) {
        try {
            // 获取用户信息
            User user = userMapper.selectById(userId);
            if (user == null) {
                return false;
            }
            
            // 验证旧密码
            if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
                return false; // 旧密码错误
            }
            
            // 加密新密码
            String encodedPassword = passwordEncoder.encode(newPassword);
            
            // 更新密码
            return userMapper.updatePassword(userId, encodedPassword) > 0;
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public boolean resetPassword(Integer userId, String newPassword) {
        try {
            // 加密新密码
            String encodedPassword = passwordEncoder.encode(newPassword);
            
            // 更新密码
            return userMapper.updatePassword(userId, encodedPassword) > 0;
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public boolean deleteUser(Integer userId) {
        try {
            return userMapper.deleteById(userId) > 0;
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public List<User> getUserList(Integer page, Integer size) {
        int offset = (page - 1) * size;
        return userMapper.selectAll(offset, size);
    }
    
    @Override
    public int getUserCount() {
        return userMapper.countAll();
    }
    
    @Override
    public List<Like> getUserLikes(Integer userId, Integer offset, Integer limit) {
        try {
            return likeMapper.selectByUserId(userId, offset, limit);
        } catch (Exception e) {
            return null;
        }
    }
    
    @Override
    public List<Favorite> getUserFavorites(Integer userId, Integer folderId, Integer offset, Integer limit) {
        try {
            return favoriteMapper.selectByUserId(userId, folderId, offset, limit);
        } catch (Exception e) {
            return null;
        }
    }
    
    @Override
    public List<FavoriteFolder> getFavoriteFolders(Integer userId) {
        try {
            return favoriteFolderMapper.selectByUserId(userId);
        } catch (Exception e) {
            return null;
        }
    }
    
    @Override
    public FavoriteFolder createFavoriteFolder(Integer userId, String name) {
        try {
            // 检查是否已存在同名收藏夹
            FavoriteFolder existing = favoriteFolderMapper.selectByUserIdAndName(userId, name);
            if (existing != null) {
                return null; // 名称已存在
            }
            
            // 创建新收藏夹
            FavoriteFolder folder = new FavoriteFolder();
            folder.setUserId(userId);
            folder.setName(name);
            folder.setCreatedAt(LocalDateTime.now());
            
            int result = favoriteFolderMapper.insert(folder);
            if (result > 0) {
                return folder;
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
    
    @Override
    public boolean deleteFavoriteFolder(Integer userId, Integer folderId) {
        try {
            // 检查收藏夹是否存在且属于该用户
            FavoriteFolder folder = favoriteFolderMapper.selectById(folderId);
            if (folder == null || !folder.getUserId().equals(userId)) {
                return false; // 收藏夹不存在或无权限
            }
            
            // 删除收藏夹
            int result = favoriteFolderMapper.deleteById(folderId);
            return result > 0;
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public boolean followUser(Integer followerId, Integer followeeId) {
        try {
            // 不能关注自己
            if (followerId.equals(followeeId)) {
                return false;
            }
            
            // 检查是否已经关注
            Follow existing = followMapper.selectByFollowerAndFollowee(followerId, followeeId);
            if (existing != null) {
                return false; // 已经关注
            }
            
            // 检查被关注者是否存在
            User followee = userMapper.selectById(followeeId);
            if (followee == null) {
                return false; // 被关注者不存在
            }
            
            // 创建关注关系
            Follow follow = new Follow();
            follow.setFollowerId(followerId);
            follow.setFolloweeId(followeeId);
            follow.setCreatedAt(LocalDateTime.now());
            
            int result = followMapper.insert(follow);
            return result > 0;
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public boolean unfollowUser(Integer followerId, Integer followeeId) {
        try {
            int result = followMapper.deleteByFollowerAndFollowee(followerId, followeeId);
            return result > 0;
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public boolean isFollowing(Integer followerId, Integer followeeId) {
        try {
            Follow follow = followMapper.selectByFollowerAndFollowee(followerId, followeeId);
            return follow != null;
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public boolean blockUser(Integer blockerId, Integer blockedId) {
        try {
            // 不能屏蔽自己
            if (blockerId.equals(blockedId)) {
                return false;
            }
            
            // 检查是否已经屏蔽
            Block existing = blockMapper.selectByBlockerAndBlocked(blockerId, blockedId);
            if (existing != null) {
                return false; // 已经屏蔽
            }
            
            // 检查被屏蔽者是否存在
            User blocked = userMapper.selectById(blockedId);
            if (blocked == null) {
                return false; // 被屏蔽者不存在
            }
            
            // 创建屏蔽关系
            Block block = new Block();
            block.setBlockerId(blockerId);
            block.setBlockedId(blockedId);
            block.setCreatedAt(LocalDateTime.now());
            
            int result = blockMapper.insert(block);
            return result > 0;
        } catch (Exception e) {
            return false;
        }
    }
}
