package com.linkme.backend.service;

import com.linkme.backend.entity.AuditLog;
import com.linkme.backend.entity.ManualReviewQueue;
import java.util.List;
import java.util.Map;

public interface AuditService {

    AuditResult checkContent(Long userId, String contentType, Long contentId, String content);

    List<ManualReviewQueue> getPendingReviewList(int page, int size);

    ManualReviewQueue getReviewQueue(Long queueId);

    int getPendingCount();

    boolean approveContent(Long reviewerId, Long queueId, String remark);

    boolean rejectContent(Long reviewerId, Long queueId, String remark);

    boolean completeReportAction(Long reviewerId, Long queueId, String processAction, String remark);

    boolean offlineContent(Long auditorId, String contentType, Long contentId, String remark);

    void reloadWordLibrary();

    Map<String, Object> getAuditStats();

    boolean reportPost(Long reporterId, Long postId, String postContent, Integer postUserId, String reason);

    boolean reportComment(Long reporterId, Long commentId, String commentContent, Integer postId, Integer commentUserId);

    boolean reportComment(Long reporterId, Long commentId, String commentContent, Integer postId, Integer commentUserId, String reason);

    boolean reportUser(Long reporterId, Long targetUserId, String targetProfileContent, String reason);

    boolean reportMessage(Long reporterId, Long messageId, String messageContent, Integer messageSenderId, String reason);

    static class AuditResult {
        private boolean passed;
        private boolean needManualReview;
        private List<String> matchedWords;
        private List<String> categories;

        public AuditResult(boolean passed, boolean needManualReview, List<String> matchedWords, List<String> categories) {
            this.passed = passed;
            this.needManualReview = needManualReview;
            this.matchedWords = matchedWords;
            this.categories = categories;
        }

        public boolean isPassed() { return passed; }
        public boolean isNeedManualReview() { return needManualReview; }
        public List<String> getMatchedWords() { return matchedWords; }
        public List<String> getCategories() { return categories; }
    }
}
