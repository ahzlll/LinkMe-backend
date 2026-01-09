package com.linkme.backend.service.impl;

import com.linkme.backend.service.LikeService;
import com.linkme.backend.mapper.UserLikeMapper;
import com.linkme.backend.mapper.UserMapper;
import com.linkme.backend.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 喜欢服务实现类
 * 
 * 功能描述：
 * - 实现用户之间的喜欢通知业务逻辑
 * - 包括发送喜欢、取消喜欢、查询喜欢状态等功能
 * 
 * @author riki
 * @version 1.0
 */
@Service
public class LikeServiceImpl implements LikeService {
    
    @Autowired
    private UserLikeMapper userLikeMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private NotificationService notificationService;
    
    @Override
    @Transactional
    public void sendLikeNotification(Integer fromUserId, Integer toUserId) {
        // 检查目标用户是否存在
        if (userMapper.selectById(toUserId) == null) {
            throw new RuntimeException("目标用户不存在");
        }
        
        // 检查是否已经发送过喜欢通知
        if (userLikeMapper.checkLikeStatus(fromUserId, toUserId)) {
            throw new RuntimeException("已经发送过喜欢通知");
        }
        
        // 创建喜欢记录
        userLikeMapper.insertLike(fromUserId, toUserId, LocalDateTime.now());
        
        // 获取发送点赞的用户信息
        Map<String, Object> fromUser = userMapper.selectUserInfoById(fromUserId);
        String fromUserName = "某位用户";
        if (fromUser != null && fromUser.get("nickname") != null) {
            fromUserName = (String) fromUser.get("nickname");
        }
        
        // 发送通知给目标用户
        String title = "新的喜欢";
        String content = fromUserName + " 对你表示了喜欢！";
        notificationService.createNotification(toUserId, "LIKE", fromUserId, 
                                           fromUserId, "USER", title, content);
    }
    
    @Override
    @Transactional
    public boolean cancelLikeNotification(Integer fromUserId, Integer toUserId) {
        // 删除喜欢记录
        int deleted = userLikeMapper.deleteLike(fromUserId, toUserId);
        
        if (deleted > 0) {
            // 删除相关的通知（可选）
            // notificationService.deleteLikeNotification(fromUserId, toUserId);
            return true;
        }
        
        return false;
    }
    
    @Override
    public List<Map<String, Object>> getSentLikes(Integer userId, Integer page, Integer size) {
        int offset = (page - 1) * size;
        List<Map<String, Object>> likes = userLikeMapper.selectSentLikes(userId, offset, size);
        
        // 为每个喜欢记录添加目标用户信息
        for (Map<String, Object> like : likes) {
            Integer targetUserId = (Integer) like.get("target_user_id");
            Map<String, Object> targetUser = userMapper.selectUserInfoById(targetUserId);
            if (targetUser != null) {
                like.put("targetUser", targetUser);
            }
        }
        
        return likes;
    }
    
    @Override
    public List<Map<String, Object>> getReceivedLikes(Integer userId, Integer page, Integer size) {
        int offset = (page - 1) * size;
        List<Map<String, Object>> likes = userLikeMapper.selectReceivedLikes(userId, offset, size);
        
        // 为每个喜欢记录添加发送者信息
        for (Map<String, Object> like : likes) {
            Integer fromUserId = (Integer) like.get("from_user_id");
            Map<String, Object> fromUser = userMapper.selectUserInfoById(fromUserId);
            if (fromUser != null) {
                like.put("fromUser", fromUser);
            }
        }
        
        return likes;
    }
    
    @Override
    public boolean checkLikeStatus(Integer fromUserId, Integer toUserId) {
        return userLikeMapper.checkLikeStatus(fromUserId, toUserId);
    }
}
