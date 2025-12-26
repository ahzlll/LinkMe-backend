package com.linkme.backend.mapper;

import com.linkme.backend.entity.Hobby;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户爱好数据访问层
 *
 * @author riki
 * @version 1.0
 */
@Mapper
public interface UserHobbyMapper {
    /**
     * 查询用户已选择的爱好列表
     *
     * @param userId 用户ID
     * @return 爱好列表
     */
    List<Hobby> selectHobbiesByUserId(@Param("userId") Integer userId);
}
