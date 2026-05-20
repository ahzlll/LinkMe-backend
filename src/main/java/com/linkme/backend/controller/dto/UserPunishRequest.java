package com.linkme.backend.controller.dto;

import lombok.Data;

@Data
public class UserPunishRequest {
    /** warn | restricted_post | restricted_comment | temp_banned | perm_banned */
    private String action;
    private String reason;
    /** 临时封禁天数，仅 temp_banned 时有效 */
    private Integer banDays;
}
