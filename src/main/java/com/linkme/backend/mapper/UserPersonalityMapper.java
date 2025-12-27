package com.linkme.backend.mapper;

import com.linkme.backend.entity.UserPersonalitySelection;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户性格特质数据访问层
 *
 * @author riki
 * @version 1.0
 */
@Mapper
public interface UserPersonalityMapper {

    /**
     * 批量查询用户的性格特质选择（包含 trait_type / category_id）
     *
     * @param userIds 用户ID列表
     * @return 选择列表
     */
    List<UserPersonalitySelection> selectSelectionsByUserIds(@Param("userIds") List<Integer> userIds);

    /**
     * 查询单个用户的性格特质选择
     *
     * @param userId 用户ID
     * @return 选择列表
     */
    List<UserPersonalitySelection> selectSelectionsByUserId(@Param("userId") Integer userId);

    /**
     * 插入用户性格特质
     *
     * @param userId 用户ID
     * @param optionId 选项ID
     * @return 影响行数
     */
    int insert(@Param("userId") Integer userId, @Param("optionId") Integer optionId);

    /**
     * 删除用户的所有性格特质
     *
     * @param userId 用户ID
     * @return 影响行数
     */
    int deleteByUserId(@Param("userId") Integer userId);
}
