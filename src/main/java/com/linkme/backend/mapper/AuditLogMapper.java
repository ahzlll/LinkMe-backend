package com.linkme.backend.mapper;

import com.linkme.backend.entity.AuditLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AuditLogMapper {
    int insert(AuditLog log);

    List<AuditLog> selectRecent(@Param("offset") int offset, @Param("limit") int limit);
}
