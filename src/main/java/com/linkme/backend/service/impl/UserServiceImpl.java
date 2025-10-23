package com.linkme.backend.service.impl;

import com.linkme.backend.entity.User;
import com.linkme.backend.mapper.UserMapper;
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
 * @author Ahz, riki
 * @version 1.0
 */
@Service
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserMapper userMapper;
    
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    @Override
    public User getUserById(Integer userId) {
        return userMapper.selectById(userId);
    }
    
    @Override
    public User getUserByEmail(String email) {
        return userMapper.selectByEmail(email);
    }
    
    @Override
    public User getUserByPhone(String phone) {
        return userMapper.selectByPhone(phone);
    }
    
    @Override
    public User getUserByUsername(String username) {
        return userMapper.selectByUsername(username);
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
            return userMapper.insert(user) > 0;
        } catch (Exception e) {
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
}
