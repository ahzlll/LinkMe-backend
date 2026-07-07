package com.linkme.backend.service.impl;

import com.linkme.backend.mapper.UserLikeMapper;
import com.linkme.backend.mapper.UserMapper;
import com.linkme.backend.service.LikeService;
import com.linkme.backend.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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
        if (userMapper.selectById(toUserId) == null) {
            throw new RuntimeException("\u76ee\u6807\u7528\u6237\u4e0d\u5b58\u5728");
        }

        if (userLikeMapper.checkLikeStatus(fromUserId, toUserId)) {
            throw new RuntimeException("\u5df2\u7ecf\u53d1\u9001\u8fc7\u559c\u6b22\u901a\u77e5");
        }

        userLikeMapper.insertLike(fromUserId, toUserId, LocalDateTime.now());

        Map<String, Object> fromUser = userMapper.selectUserInfoById(fromUserId);
        String fromUserName = "\u67d0\u4f4d\u7528\u6237";
        if (fromUser != null && fromUser.get("nickname") != null) {
            fromUserName = (String) fromUser.get("nickname");
        }

        String title = "新的喜欢";
        String content = fromUserName + " 对你表达了喜欢！";
        notificationService.createNotification(toUserId, "LIKE", fromUserId,
                fromUserId, "USER", title, content);
    }

    @Override
    @Transactional
    public boolean cancelLikeNotification(Integer fromUserId, Integer toUserId) {
        int deleted = userLikeMapper.deleteLike(fromUserId, toUserId);
        return deleted > 0;
    }

    @Override
    public List<Map<String, Object>> getSentLikes(Integer userId, Integer page, Integer size) {
        int offset = (page - 1) * size;
        List<Map<String, Object>> likes = userLikeMapper.selectSentLikes(userId, offset, size);

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
