package com.linkme.backend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 用户喜欢记录数据访问接口
 * 
 * 功能描述：
 * - 处理用户之间喜欢记录的数据库操作
 * - 包括插入、删除、查询等功能
 * 
 * @author riki
 * @version 1.0
 */
@Mapper
public interface UserLikeMapper {
    
    /**
     * 插入喜欢记录
     * 
     * @param fromUserId 发送喜欢的用户ID
     * @param toUserId 接收喜欢的用户ID
     * @param createdAt 创建时间
     * @return 影响的行数
     */
    int insertLike(@Param("fromUserId") Integer fromUserId, 
                   @Param("toUserId") Integer toUserId, 
                   @Param("createdAt") LocalDateTime createdAt);
    
    /**
     * 删除喜欢记录
     * 
     * @param fromUserId 发送喜欢的用户ID
     * @param toUserId 接收喜欢的用户ID
     * @return 影响的行数
     */
    int deleteLike(@Param("fromUserId") Integer fromUserId, 
                   @Param("toUserId") Integer toUserId);
    
    /**
     * 检查喜欢状态
     * 
     * @param fromUserId 发送喜欢的用户ID
     * @param toUserId 接收喜欢的用户ID
     * @return 是否存在喜欢记录
     */
    boolean checkLikeStatus(@Param("fromUserId") Integer fromUserId, 
                            @Param("toUserId") Integer toUserId);
    
    /**
     * 查询我发送的喜欢列表
     * 
     * @param userId 用户ID
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 我发送的喜欢列表
     */
    List<Map<String, Object>> selectSentLikes(@Param("userId") Integer userId, 
                                            @Param("offset") Integer offset, 
                                            @Param("limit") Integer limit);
    
    /**
     * 查询我收到的喜欢列表
     * 
     * @param userId 用户ID
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 我收到的喜欢列表
     */
    List<Map<String, Object>> selectReceivedLikes(@Param("userId") Integer userId, 
                                                  @Param("offset") Integer offset, 
                                                  @Param("limit") Integer limit);
    
    /**
     * 查询用户之间的喜欢记录
     * 
     * @param fromUserId 发送喜欢的用户ID
     * @param toUserId 接收喜欢的用户ID
     * @return 喜欢记录
     */
    Map<String, Object> selectLikeRecord(@Param("fromUserId") Integer fromUserId, 
                                         @Param("toUserId") Integer toUserId);
}
