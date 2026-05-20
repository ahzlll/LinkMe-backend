package com.linkme.backend.common;

import com.linkme.backend.entity.User;

import java.time.LocalDateTime;

public final class AccountStatusUtil {

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
        String status = normalize(user.getAccountStatus());
        if ("perm_banned".equals(status)) {
            return true;
        }
        if ("temp_banned".equals(status)) {
            LocalDateTime until = user.getBanUntil();
            return until == null || until.isAfter(LocalDateTime.now());
        }
        return false;
    }

    public static boolean canPost(User user) {
        if (isBanned(user)) {
            return false;
        }
        String status = normalize(user.getAccountStatus());
        return !"restricted_post".equals(status);
    }

    public static boolean canComment(User user) {
        if (isBanned(user)) {
            return false;
        }
        String status = normalize(user.getAccountStatus());
        return !"restricted_comment".equals(status);
    }

    public static String normalize(String status) {
        return status == null ? "normal" : status.trim().toLowerCase();
    }
}
