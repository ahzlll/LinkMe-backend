package com.linkme.backend.mapper;

import com.linkme.backend.entity.UserMatchingPreference;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户匹配偏好数据访问层
 *
 * @author riki
 * @version 1.0
 */
@Mapper
public interface UserMatchingPreferenceMapper {
    /**
     * 根据用户ID查询匹配偏好
     *
     * @param userId 用户ID
     * @return 匹配偏好
     */
    UserMatchingPreference selectByUserId(@Param("userId") Integer userId);

    /**
     * 批量查询用户匹配偏好
     *
     * @param userIds 用户ID列表
     * @return 匹配偏好列表
     */
    List<UserMatchingPreference> selectByUserIds(@Param("userIds") List<Integer> userIds);
}
