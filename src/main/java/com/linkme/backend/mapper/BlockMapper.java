package com.linkme.backend.mapper;

import com.linkme.backend.entity.Block;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 屏蔽数据访问层接口
 * 
 * 功能描述：
 * - 提供屏蔽数据的增删改查操作
 * - 支持屏蔽和取消屏蔽功能
 * 
 * @author Ahz
 * @version 1.2.2
 */
@Mapper
public interface BlockMapper {
    
    /**
     * 根据屏蔽者ID和被屏蔽者ID查询屏蔽关系
     * 
     * @param blockerId 屏蔽者ID
     * @param blockedId 被屏蔽者ID
     * @return 屏蔽关系
     */
    Block selectByBlockerAndBlocked(@Param("blockerId") Integer blockerId, @Param("blockedId") Integer blockedId);
    
    /**
     * 根据屏蔽者ID查询屏蔽列表
     * 
     * @param blockerId 屏蔽者ID
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 屏蔽列表
     */
    List<Block> selectByBlockerId(@Param("blockerId") Integer blockerId, @Param("offset") Integer offset, @Param("limit") Integer limit);
    
    /**
     * 插入新屏蔽关系
     * 
     * @param block 屏蔽关系
     * @return 影响行数
     */
    int insert(Block block);
    
    /**
     * 根据屏蔽者ID和被屏蔽者ID删除屏蔽关系
     * 
     * @param blockerId 屏蔽者ID
     * @param blockedId 被屏蔽者ID
     * @return 影响行数
     */
    int deleteByBlockerAndBlocked(@Param("blockerId") Integer blockerId, @Param("blockedId") Integer blockedId);
    
    /**
     * 根据屏蔽者ID统计屏蔽数量
     * 
     * @param blockerId 屏蔽者ID
     * @return 屏蔽数量
     */
    int countByBlockerId(@Param("blockerId") Integer blockerId);
}

