package com.linkme.backend.mapper;

import com.linkme.backend.entity.Conversation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 会话数据访问层接口
 * 
 * 功能描述：
 * - 提供会话数据的增删改查操作
 * - 支持私聊会话管理
 * 
 * @author Ahz, riki
 * @version 1.2.2
 */
@Mapper
public interface ConversationMapper {
    
    /**
     * 根据会话ID查询会话信息
     * 
     * @param conversationId 会话ID
     * @return 会话信息
     */
    Conversation selectById(@Param("conversationId") Integer conversationId);
    
    /**
     * 根据用户ID查询会话列表
     * 
     * @param userId 用户ID
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 会话列表
     */
    List<Conversation> selectByUserId(@Param("userId") Integer userId, @Param("offset") Integer offset, @Param("limit") Integer limit);
    
    /**
     * 根据两个用户ID查询会话
     * 
     * @param user1Id 用户1ID
     * @param user2Id 用户2ID
     * @return 会话信息
     */
    Conversation selectByUserPair(@Param("user1Id") Integer user1Id, @Param("user2Id") Integer user2Id);
    
    /**
     * 插入新会话
     * 
     * @param conversation 会话信息
     * @return 影响行数
     */
    int insert(Conversation conversation);
    
    /**
     * 根据会话ID删除会话
     * 
     * @param conversationId 会话ID
     * @return 影响行数
     */
    int deleteById(@Param("conversationId") Integer conversationId);
    
    /**
     * 根据用户ID统计会话数量
     * 
     * @param userId 用户ID
     * @return 会话数量
     */
    int countByUserId(@Param("userId") Integer userId);
    
    /**
     * 更新会话信息
     * 
     * @param conversation 会话信息
     * @return 影响行数
     */
    int update(Conversation conversation);
}
