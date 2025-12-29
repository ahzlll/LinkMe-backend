package com.linkme.backend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserQuestionnaireCompletionMapper {
    /**
     * 在用户提交问卷时写入或更新完成记录：
     * - 首次提交：插入记录，first_completed_at 和 last_submitted_at 为当前时间，submission_count=1
     * - 后续提交：更新 last_submitted_at 为当前时间，submission_count 自增
     *
     *   @author riki
     *  *@version 1.0
     */
    int upsertOnSubmit(@Param("userId") Integer userId);
}

