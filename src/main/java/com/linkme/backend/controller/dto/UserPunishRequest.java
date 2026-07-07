package com.linkme.backend.controller.dto;

import lombok.Data;

@Data
public class UserPunishRequest {
    /** warn | restricted_post | restricted_comment | temp_banned | perm_banned */
    private String action;
    private String reason;
    /** Optional duration in days for warn/restrict/temp-ban actions */
    private Integer banDays;
}
