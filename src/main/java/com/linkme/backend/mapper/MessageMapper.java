package com.linkme.backend.mapper;

import com.linkme.backend.entity.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 消息数据访问层接口
 * 
 * 功能描述：
 * - 提供消息数据的增删改查操作
 * - 支持消息发送、查询、删除等功能
 * 
 * @author Ahz
 * @version 1.0
 */
@Mapper
public interface MessageMapper {
    
    /**
     * 根据消息ID查询消息信息
     * 
     * @param messageId 消息ID
     * @return 消息信息
     */
    Message selectById(@Param("messageId") Integer messageId);
    
    /**
     * 根据会话ID查询消息列表
     * 
     * @param conversationId 会话ID
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 消息列表
     */
    List<Message> selectByConversationId(@Param("conversationId") Integer conversationId, @Param("offset") Integer offset, @Param("limit") Integer limit);
    
    /**
     * 根据发送者ID查询消息列表
     * 
     * @param senderId 发送者ID
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 消息列表
     */
    List<Message> selectBySenderId(@Param("senderId") Integer senderId, @Param("offset") Integer offset, @Param("limit") Integer limit);
    
    /**
     * 插入新消息
     * 
     * @param message 消息信息
     * @return 影响行数
     */
    int insert(Message message);
    
    /**
     * 根据消息ID删除消息
     * 
     * @param messageId 消息ID
     * @return 影响行数
     */
    int deleteById(@Param("messageId") Integer messageId);
    
    /**
     * 根据会话ID统计消息数量
     * 
     * @param conversationId 会话ID
     * @return 消息数量
     */
    int countByConversationId(@Param("conversationId") Integer conversationId);
}
