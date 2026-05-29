package com.linkme.backend.mapper;

import com.linkme.backend.entity.ManualReviewQueue;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ManualReviewQueueMapper {
    int insert(ManualReviewQueue queue);

    ManualReviewQueue selectById(@Param("id") Long id);

    List<ManualReviewQueue> selectPending(@Param("offset") int offset, @Param("limit") int limit);

    int countPending();

    int updateStatus(@Param("id") Long id, @Param("status") Integer status,
                     @Param("reviewerId") Long reviewerId, @Param("reviewRemark") String reviewRemark);

    List<ManualReviewQueue> selectByUserId(@Param("userId") Long userId, @Param("offset") int offset, @Param("limit") int limit);
}