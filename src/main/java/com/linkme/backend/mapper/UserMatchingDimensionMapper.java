package com.linkme.backend.mapper;

import com.linkme.backend.entity.DimensionPrioritySelection;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户匹配维度（must/priority）数据访问层
 *
 * @author riki
 * @version 1.0
 */
@Mapper
public interface UserMatchingDimensionMapper {

    /**
     * 查询用户必须满足的维度 code 列表
     */
    List<String> selectMustDimensionCodesByUserId(@Param("userId") Integer userId);

    /**
     * 查询用户优先考虑的维度 code 列表（含优先级顺序）
     */
    List<DimensionPrioritySelection> selectPriorityDimensionsByUserId(@Param("userId") Integer userId);
}
