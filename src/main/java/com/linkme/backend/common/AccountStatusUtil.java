package com.linkme.backend.common;

import com.linkme.backend.entity.User;

import java.time.LocalDateTime;

public final class AccountStatusUtil {

    private static final String STATUS_NORMAL = "normal";
    private static final String STATUS_WARNED = "warned";
    private static final String STATUS_RESTRICTED_POST = "restricted_post";
    private static final String STATUS_RESTRICTED_COMMENT = "restricted_comment";
    private static final String STATUS_TEMP_BANNED = "temp_banned";
    private static final String STATUS_PERM_BANNED = "perm_banned";

    private AccountStatusUtil() {
    }

    public static boolean isAdminRole(User user) {
        if (user == null || user.getRole() == null) {
            return false;
        }
        String role = user.getRole().toLowerCase();
        return "admin".equals(role) || "moderator".equals(role);
    }

    public static boolean isBanned(User user) {
        if (user == null) {
            return true;
        }
        String status = getEffectiveStatus(user);
        if (STATUS_PERM_BANNED.equals(status)) {
            return true;
        }
        if (STATUS_TEMP_BANNED.equals(status)) {
            LocalDateTime until = getEffectiveBanUntil(user);
            return until == null || until.isAfter(LocalDateTime.now());
        }
        return false;
    }

    public static boolean canPost(User user) {
        if (isBanned(user)) {
            return false;
        }
        return !STATUS_RESTRICTED_POST.equals(getEffectiveStatus(user));
    }

    public static boolean canComment(User user) {
        if (isBanned(user)) {
            return false;
        }
        return !STATUS_RESTRICTED_COMMENT.equals(getEffectiveStatus(user));
    }

    public static String getEffectiveStatus(User user) {
        if (user == null) {
            return STATUS_NORMAL;
        }
        String status = normalize(user.getAccountStatus());
        if (STATUS_PERM_BANNED.equals(status)) {
            return status;
        }
        if (!isTimedStatus(status)) {
            return status;
        }
        LocalDateTime until = user.getBanUntil();
        if (until != null && !until.isAfter(LocalDateTime.now())) {
            return STATUS_NORMAL;
        }
        return status;
    }

    public static LocalDateTime getEffectiveBanUntil(User user) {
        if (user == null) {
            return null;
        }
        String status = normalize(user.getAccountStatus());
        if (!isTimedStatus(status)) {
            return null;
        }
        LocalDateTime until = user.getBanUntil();
        if (until != null && !until.isAfter(LocalDateTime.now())) {
            return null;
        }
        return until;
    }

    public static String normalize(String status) {
        return status == null ? STATUS_NORMAL : status.trim().toLowerCase();
    }

    private static boolean isTimedStatus(String status) {
        return STATUS_WARNED.equals(status)
                || STATUS_RESTRICTED_POST.equals(status)
                || STATUS_RESTRICTED_COMMENT.equals(status)
                || STATUS_TEMP_BANNED.equals(status);
    }
}
