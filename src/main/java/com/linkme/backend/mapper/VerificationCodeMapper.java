package com.linkme.backend.mapper;

import com.linkme.backend.entity.VerificationCode;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 验证码数据访问层接口
 * 
 * 功能描述：
 * - 提供验证码数据的增删改查操作
 * - 支持验证码的生成、验证和管理
 * 
 * @author Ahz
 * @version 1.1
 */
@Mapper
public interface VerificationCodeMapper {
    
    /**
     * 插入验证码
     * 
     * @param verificationCode 验证码信息
     * @return 影响行数
     */
    int insert(VerificationCode verificationCode);
    
    /**
     * 根据验证码、类型和用途查询有效的验证码
     * 
     * @param code 验证码
     * @param type 类型（email/phone）
     * @param purpose 用途（register/login/reset_password）
     * @param userId 用户ID（可选，用于reset_password场景）
     * @return 验证码信息
     */
    VerificationCode selectValidCode(@Param("code") String code,
                                     @Param("type") String type,
                                     @Param("purpose") String purpose,
                                     @Param("userId") Integer userId);
    
    /**
     * 根据邮箱或手机号和用途查询最新的有效验证码
     * 
     * @param email 邮箱（可为空）
     * @param phone 手机号（可为空）
     * @param type 类型（email/phone）
     * @param purpose 用途
     * @return 验证码信息
     */
    VerificationCode selectLatestByContact(@Param("email") String email,
                                          @Param("phone") String phone,
                                          @Param("type") String type,
                                          @Param("purpose") String purpose);
    
    /**
     * 标记验证码为已使用
     * 
     * @param codeId 验证码ID
     * @return 影响行数
     */
    int markAsUsed(@Param("codeId") Integer codeId);
    
    /**
     * 删除过期的验证码（可选，用于清理）
     * 
     * @return 影响行数
     */
    int deleteExpiredCodes();
}

