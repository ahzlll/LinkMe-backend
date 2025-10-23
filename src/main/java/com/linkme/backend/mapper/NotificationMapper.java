package com.linkme.backend.mapper;

import com.linkme.backend.entity.Notification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 通知数据访问层接口
 * 
 * 功能描述：
 * - 提供通知数据的增删改查操作
 * - 支持通知发送、查询、标记已读等功能
 * 
 * @author Ahz, riki
 * @version 1.0
 */
@Mapper
public interface NotificationMapper {
    
    /**
     * 根据通知ID查询通知信息
     * 
     * @param notificationId 通知ID
     * @return 通知信息
     */
    Notification selectById(@Param("notificationId") Integer notificationId);
    
    /**
     * 根据用户ID查询通知列表
     * 
     * @param userId 用户ID
     * @param isRead 是否已读（可选）
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 通知列表
     */
    List<Notification> selectByUserId(@Param("userId") Integer userId, @Param("isRead") Boolean isRead, @Param("offset") Integer offset, @Param("limit") Integer limit);
    
    /**
     * 根据操作者ID查询通知列表
     * 
     * @param actorId 操作者ID
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 通知列表
     */
    List<Notification> selectByActorId(@Param("actorId") Integer actorId, @Param("offset") Integer offset, @Param("limit") Integer limit);
    
    /**
     * 插入新通知
     * 
     * @param notification 通知信息
     * @return 影响行数
     */
    int insert(Notification notification);
    
    /**
     * 更新通知信息
     * 
     * @param notification 通知信息
     * @return 影响行数
     */
    int update(Notification notification);
    
    /**
     * 根据通知ID删除通知
     * 
     * @param notificationId 通知ID
     * @return 影响行数
     */
    int deleteById(@Param("notificationId") Integer notificationId);
    
    /**
     * 根据用户ID统计未读通知数量
     * 
     * @param userId 用户ID
     * @return 未读通知数量
     */
    int countUnreadByUserId(@Param("userId") Integer userId);
}
