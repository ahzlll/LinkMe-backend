package com.linkme.backend.service;

import java.util.List;
import java.util.Map;

/**
 * 喜欢服务接口
 * 
 * 功能描述：
 * - 处理用户之间的喜欢通知业务逻辑
 * - 包括发送喜欢、取消喜欢、查询喜欢状态等功能
 * 
 * @author riki
 * @version 1.0
 */
public interface LikeService {
    
    /**
     * 发送喜欢通知
     * 
     * @param fromUserId 发送喜欢的用户ID
     * @param toUserId 接收喜欢的用户ID
     * @throws RuntimeException 如果发送失败
     */
    void sendLikeNotification(Integer fromUserId, Integer toUserId);
    
    /**
     * 取消喜欢通知
     * 
     * @param fromUserId 发送喜欢的用户ID
     * @param toUserId 接收喜欢的用户ID
     * @return 是否成功取消
     */
    boolean cancelLikeNotification(Integer fromUserId, Integer toUserId);
    
    /**
     * 获取我发送的喜欢列表
     * 
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页数量
     * @return 我发送的喜欢列表
     */
    List<Map<String, Object>> getSentLikes(Integer userId, Integer page, Integer size);
    
    /**
     * 获取我收到的喜欢列表
     * 
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页数量
     * @return 我收到的喜欢列表
     */
    List<Map<String, Object>> getReceivedLikes(Integer userId, Integer page, Integer size);
    
    /**
     * 检查是否已喜欢指定用户
     * 
     * @param fromUserId 发送喜欢的用户ID
     * @param toUserId 接收喜欢的用户ID
     * @return 是否已喜欢
     */
    boolean checkLikeStatus(Integer fromUserId, Integer toUserId);
}
