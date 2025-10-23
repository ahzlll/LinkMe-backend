package com.linkme.backend.mapper;

import com.linkme.backend.entity.Match;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 匹配数据访问层接口
 * 
 * 功能描述：
 * - 提供匹配数据的增删改查操作
 * - 支持匹配关系管理和状态更新
 * 
 * @author Ahz, riki
 * @version 1.0
 */
@Mapper
public interface MatchMapper {
    
    /**
     * 根据匹配ID查询匹配信息
     * 
     * @param matchId 匹配ID
     * @return 匹配信息
     */
    Match selectById(@Param("matchId") Integer matchId);
    
    /**
     * 根据用户ID查询匹配列表
     * 
     * @param userId 用户ID
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 匹配列表
     */
    List<Match> selectByUserId(@Param("userId") Integer userId, @Param("offset") Integer offset, @Param("limit") Integer limit);
    
    /**
     * 根据两个用户ID查询匹配关系
     * 
     * @param user1Id 用户1ID
     * @param user2Id 用户2ID
     * @return 匹配关系
     */
    Match selectByUserPair(@Param("user1Id") Integer user1Id, @Param("user2Id") Integer user2Id);
    
    /**
     * 插入新匹配关系
     * 
     * @param match 匹配关系
     * @return 影响行数
     */
    int insert(Match match);
    
    /**
     * 更新匹配状态
     * 
     * @param match 匹配关系
     * @return 影响行数
     */
    int update(Match match);
    
    /**
     * 根据匹配ID删除匹配关系
     * 
     * @param matchId 匹配ID
     * @return 影响行数
     */
    int deleteById(@Param("matchId") Integer matchId);
    
    /**
     * 根据用户ID统计匹配数量
     * 
     * @param userId 用户ID
     * @return 匹配数量
     */
    int countByUserId(@Param("userId") Integer userId);
}
