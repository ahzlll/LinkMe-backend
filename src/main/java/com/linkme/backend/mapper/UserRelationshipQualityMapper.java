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
}
