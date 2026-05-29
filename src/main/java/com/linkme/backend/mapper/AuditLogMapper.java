package com.linkme.backend.mapper;

import com.linkme.backend.entity.AuditLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface AuditLogMapper {
    int insert(AuditLog log);

    List<AuditLog> selectRecent(@Param("offset") int offset, @Param("limit") int limit);

    AuditLog selectById(@Param("id") Long id);

    List<AuditLog> selectByContent(@Param("contentType") String contentType, @Param("contentId") Long contentId);

    int countByResult(@Param("auditResult") Integer auditResult);

    int countTotal();

    int updateResult(@Param("contentId") Long contentId, @Param("contentType") String contentType,
                     @Param("auditResult") Integer auditResult, @Param("auditorId") Long auditorId,
                     @Param("remark") String remark);
}