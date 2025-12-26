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
}
