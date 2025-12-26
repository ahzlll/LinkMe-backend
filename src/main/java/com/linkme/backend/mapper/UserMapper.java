package com.linkme.backend.mapper;

import com.linkme.backend.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户数据访问层接口
 * 
 * 功能描述：
 * - 提供用户数据的增删改查操作
 * - 支持用户注册、登录、信息更新等功能
 * 
 * @author Ahz，riki
 * @version 1.2
 */
@Mapper
public interface UserMapper {
    
    /**
     * 根据用户ID查询用户信息
     * 
     * @param userId 用户ID
     * @return 用户信息
     */
    User selectById(@Param("userId") Integer userId);
    
    /**
     * 根据邮箱查询用户信息
     * 
     * @param email 邮箱
     * @return 用户信息
     */
    User selectByEmail(@Param("email") String email);
    
    /**
     * 根据手机号查询用户信息
     * 
     * @param phone 手机号
     * @return 用户信息
     */
    User selectByPhone(@Param("phone") String phone);
    
    /**
     * 根据用户名查询用户信息
     * 
     * @param username 用户名
     * @return 用户信息
     */
    User selectByUsername(@Param("username") String username);
    
    /**
     * 插入新用户
     * 
     * @param user 用户信息
     * @return 影响行数
     */
    int insert(User user);
    
    /**
     * 更新用户信息
     * 
     * @param user 用户信息
     * @return 影响行数
     */
    int update(User user);
    
    /**
     * 更新用户密码
     * 
     * @param userId 用户ID
     * @param passwordHash 加密后的密码
     * @return 影响行数
     */
    int updatePassword(@Param("userId") Integer userId, @Param("passwordHash") String passwordHash);
    
    /**
     * 根据用户ID删除用户
     * 
     * @param userId 用户ID
     * @return 影响行数
     */
    int deleteById(@Param("userId") Integer userId);
    
    /**
     * 查询所有用户（分页）
     * 
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 用户列表
     */
    List<User> selectAll(@Param("offset") Integer offset, @Param("limit") Integer limit);
    
    /**
     * 统计用户总数
     * 
     * @return 用户总数
     */
    int countAll();
    
    /**
     * 根据用户ID列表批量查询用户信息
     * 
     * @param userIds 用户ID列表
     * @return 用户信息列表
     */
    List<User> selectBatchIds(@Param("userIds") List<Integer> userIds);
    
    /**
     * 匹配推荐的候选用户查询
     * 
     * @param currentUserId 当前用户ID
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 用户信息列表
     * 
     */
    List<User> selectMatchCandidates(@Param("currentUserId") Integer currentUserId,
                                     @Param("offset") Integer offset,
                                     @Param("limit") Integer limit);
}
