package com.linkme.backend.mapper;

import com.linkme.backend.entity.Follow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 关注数据访问层接口
 * 
 * 功能描述：
 * - 提供关注数据的增删改查操作
 * - 支持关注和取消关注功能
 * 
 * @author Ahz, riki
 * @version 1.0
 */
@Mapper
public interface FollowMapper {
    
    /**
     * 根据关注者ID和被关注者ID查询关注关系
     * 
     * @param followerId 关注者ID
     * @param followeeId 被关注者ID
     * @return 关注关系
     */
    Follow selectByFollowerAndFollowee(@Param("followerId") Integer followerId, @Param("followeeId") Integer followeeId);
    
    /**
     * 根据关注者ID查询关注列表
     * 
     * @param followerId 关注者ID
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 关注列表
     */
    List<Follow> selectByFollowerId(@Param("followerId") Integer followerId, @Param("offset") Integer offset, @Param("limit") Integer limit);
    
    /**
     * 根据被关注者ID查询粉丝列表
     * 
     * @param followeeId 被关注者ID
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 粉丝列表
     */
    List<Follow> selectByFolloweeId(@Param("followeeId") Integer followeeId, @Param("offset") Integer offset, @Param("limit") Integer limit);
    
    /**
     * 插入新关注关系
     * 
     * @param follow 关注关系
     * @return 影响行数
     */
    int insert(Follow follow);
    
    /**
     * 根据关注者ID和被关注者ID删除关注关系
     * 
     * @param followerId 关注者ID
     * @param followeeId 被关注者ID
     * @return 影响行数
     */
    int deleteByFollowerAndFollowee(@Param("followerId") Integer followerId, @Param("followeeId") Integer followeeId);
    
    /**
     * 根据关注者ID统计关注数量
     * 
     * @param followerId 关注者ID
     * @return 关注数量
     */
    int countByFollowerId(@Param("followerId") Integer followerId);
    
    /**
     * 根据被关注者ID统计粉丝数量
     * 
     * @param followeeId 被关注者ID
     * @return 粉丝数量
     */
    int countByFolloweeId(@Param("followeeId") Integer followeeId);
}
