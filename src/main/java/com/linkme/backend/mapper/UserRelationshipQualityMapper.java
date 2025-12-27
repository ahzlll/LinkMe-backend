package com.linkme.backend.mapper;

import com.linkme.backend.entity.UserRelationshipQualitySelection;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户关系品质数据访问层
 *
 * @author riki
 * @version 1.0
 */
@Mapper
public interface UserRelationshipQualityMapper {

    /**
     * 批量查询用户选择的关系品质
     *
     * @param userIds 用户ID列表
     * @return 选择列表
     */
    List<UserRelationshipQualitySelection> selectByUserIds(@Param("userIds") List<Integer> userIds);

    /**
     * 查询单个用户选择的关系品质
     *
     * @param userId 用户ID
     * @return 选择列表
     */
    List<UserRelationshipQualitySelection> selectByUserId(@Param("userId") Integer userId);

    /**
     * 插入用户关系品质
     *
     * @param userId 用户ID
     * @param qualityId 品质ID
     * @return 影响行数
     */
    int insert(@Param("userId") Integer userId, @Param("qualityId") Integer qualityId);

    /**
     * 删除用户的所有关系品质
     *
     * @param userId 用户ID
     * @return 影响行数
     */
    int deleteByUserId(@Param("userId") Integer userId);
}
