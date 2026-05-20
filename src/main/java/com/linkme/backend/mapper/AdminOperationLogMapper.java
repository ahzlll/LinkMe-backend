package com.linkme.backend.mapper;

import com.linkme.backend.entity.AdminOperationLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AdminOperationLogMapper {
    int insert(AdminOperationLog log);

    List<AdminOperationLog> selectRecent(@Param("offset") int offset, @Param("limit") int limit);
}
