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
 * @author Ahz, riki
 * @version 1.0
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
}
