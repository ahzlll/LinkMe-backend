package com.linkme.backend.mapper;

import com.linkme.backend.entity.Heart;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 红心数据访问层接口
 * 
 * 功能描述：
 * - 提供红心数据的增删改查操作
 * - 支持红心和取消红心功能
 * 
 * @author Ahz, riki
 * @version 1.0
 */
@Mapper
public interface HeartMapper {
    
    /**
     * 根据发送者ID和接收者ID查询红心关系
     * 
     * @param fromUserId 发送者ID
     * @param toUserId 接收者ID
     * @return 红心关系
     */
    Heart selectByFromAndTo(@Param("fromUserId") Integer fromUserId, @Param("toUserId") Integer toUserId);
    
    /**
     * 根据发送者ID查询红心列表
     * 
     * @param fromUserId 发送者ID
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 红心列表
     */
    List<Heart> selectByFromUserId(@Param("fromUserId") Integer fromUserId, @Param("offset") Integer offset, @Param("limit") Integer limit);
    
    /**
     * 根据接收者ID查询被红心列表
     * 
     * @param toUserId 接收者ID
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 被红心列表
     */
    List<Heart> selectByToUserId(@Param("toUserId") Integer toUserId, @Param("offset") Integer offset, @Param("limit") Integer limit);
    
    /**
     * 插入新红心关系
     * 
     * @param heart 红心关系
     * @return 影响行数
     */
    int insert(Heart heart);
    
    /**
     * 根据发送者ID和接收者ID删除红心关系
     * 
     * @param fromUserId 发送者ID
     * @param toUserId 接收者ID
     * @return 影响行数
     */
    int deleteByFromAndTo(@Param("fromUserId") Integer fromUserId, @Param("toUserId") Integer toUserId);
    
    /**
     * 根据发送者ID统计红心数量
     * 
     * @param fromUserId 发送者ID
     * @return 红心数量
     */
    int countByFromUserId(@Param("fromUserId") Integer fromUserId);
    
    /**
     * 根据接收者ID统计被红心数量
     * 
     * @param toUserId 接收者ID
     * @return 被红心数量
     */
    int countByToUserId(@Param("toUserId") Integer toUserId);
}
