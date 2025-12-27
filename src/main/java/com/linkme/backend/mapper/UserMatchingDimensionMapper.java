package com.linkme.backend.mapper;

import com.linkme.backend.entity.DimensionPrioritySelection;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 用户匹配维度（must/priority）数据访问层
 *
 * @author riki
 * @version 1.1
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

    /**
     * 查询用户必须满足的维度ID列表
     */
    List<Integer> selectMustDimensionIdsByUserId(@Param("userId") Integer userId);

    /**
     * 查询用户优先考虑的维度ID列表（含优先级顺序）
     * 返回Map，包含dimensionId和priorityOrder
     */
    List<Map<String, Object>> selectPriorityDimensionIdsByUserId(@Param("userId") Integer userId);

    /**
     * 插入用户必须匹配维度
     *
     * @param userId 用户ID
     * @param dimensionId 维度ID
     * @return 影响行数
     */
    int insertMustDimension(@Param("userId") Integer userId, @Param("dimensionId") Integer dimensionId);

    /**
     * 删除用户的所有必须匹配维度
     *
     * @param userId 用户ID
     * @return 影响行数
     */
    int deleteMustDimensionsByUserId(@Param("userId") Integer userId);

    /**
     * 插入用户优先匹配维度
     *
     * @param userId 用户ID
     * @param dimensionId 维度ID
     * @param priorityOrder 优先级顺序
     * @return 影响行数
     */
    int insertPriorityDimension(@Param("userId") Integer userId, @Param("dimensionId") Integer dimensionId, @Param("priorityOrder") Integer priorityOrder);

    /**
     * 删除用户的所有优先匹配维度
     *
     * @param userId 用户ID
     * @return 影响行数
     */
    int deletePriorityDimensionsByUserId(@Param("userId") Integer userId);
}
