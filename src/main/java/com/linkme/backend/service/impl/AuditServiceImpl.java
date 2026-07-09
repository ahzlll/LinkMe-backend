package com.linkme.backend.service.impl;

import com.github.houbb.sensitive.word.bs.SensitiveWordBs;
import com.linkme.backend.entity.AuditLog;
import com.linkme.backend.entity.ManualReviewQueue;
import com.linkme.backend.mapper.AuditLogMapper;
import com.linkme.backend.mapper.ManualReviewQueueMapper;
import com.linkme.backend.service.AuditService;
import com.linkme.backend.service.LocalSensitiveWordService;
import com.linkme.backend.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AuditServiceImpl implements AuditService {

    @Autowired
    private AuditLogMapper auditLogMapper;

    @Autowired
    private ManualReviewQueueMapper manualReviewQueueMapper;

    @Autowired(required = false)
    private LocalSensitiveWordService localSensitiveWordService;

    @Autowired
    private NotificationService notificationService;

    private SensitiveWordBs sensitiveWordBs;

    @PostConstruct
    public void init() {
        try {
            this.sensitiveWordBs = SensitiveWordBs.newInstance()
                    .ignoreCase(true)
                    .ignoreWidth(true)
                    .ignoreNumStyle(true)
                    .ignoreChineseStyle(true)
                    .ignoreRepeat(true)
                    .enableNumCheck(true)
                    .enableEmailCheck(true)
                    .enableUrlCheck(true)
                    .init();
            System.out.println("敏感词库(sensitive-word)初始化成功");
        } catch (Exception e) {
            this.sensitiveWordBs = null;
            System.err.println("敏感词库(sensitive-word)初始化失败: " + e.getMessage());
            if (localSensitiveWordService != null) {
                System.out.println("已启用本地敏感词库作为备用，包含 " + localSensitiveWordService.getWordCount() + " 个词条");
            }
        }
    }

    @Override
    public AuditResult checkContent(Long userId, String contentType, Long contentId, String content) {
        if (content == null || content.trim().isEmpty()) {
            return new AuditResult(true, false, new ArrayList<>(), new ArrayList<>());
        }

        List<String> allMatchedWords = new ArrayList<>();

        if (sensitiveWordBs != null) {
            try {
                List<String> matchedWords = sensitiveWordBs.findAll(content);
                if (matchedWords != null && !matchedWords.isEmpty()) {
                    allMatchedWords.addAll(matchedWords);
                }
            } catch (Exception e) {
                System.err.println("外部敏感词库检测异常，将尝试使用本地词库: " + e.getMessage());
            }
        }

        if (localSensitiveWordService != null) {
            try {
                List<String> localMatchedWords = localSensitiveWordService.findAll(content);
                if (localMatchedWords != null && !localMatchedWords.isEmpty()) {
                    for (String word : localMatchedWords) {
                        if (!allMatchedWords.contains(word)) {
                            allMatchedWords.add(word);
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("本地敏感词库检测异常: " + e.getMessage());
            }
        }

        if (allMatchedWords.isEmpty()) {
            logAudit(userId, contentType, contentId, content, 0, null, null, AuditLog.RESULT_AUTO_PASS);
            return new AuditResult(true, false, new ArrayList<>(), new ArrayList<>());
        }

        List<String> categories = allMatchedWords.stream()
                .map(this::getCategoryForWord)
                .distinct()
                .collect(Collectors.toList());

        logAudit(userId, contentType, contentId, content, 1,
                String.join(",", allMatchedWords),
                String.join(",", categories),
                AuditLog.RESULT_MANUAL_REJECT);

        return new AuditResult(false, false, allMatchedWords, categories);
    }

    private String getCategoryForWord(String word) {
        return "敏感词";
    }

    private void logAudit(Long userId, String contentType, Long contentId, String content,
                          int isViolation, String matchedWords, String categories, int auditResult) {
        try {
            AuditLog log = new AuditLog();
            log.setUserId(userId);
            log.setContentType(contentType);
            log.setContentId(contentId);
            log.setContent(content != null && content.length() > 500 ? content.substring(0, 500) : content);
            log.setIsViolation(isViolation);
            log.setMatchedWords(matchedWords);
            log.setCategories(categories);
            log.setAuditResult(auditResult);
            log.setCreateTime(LocalDateTime.now());
            if (auditResult != AuditLog.RESULT_NEED_MANUAL) {
                log.setAuditTime(LocalDateTime.now());
            }
            auditLogMapper.insert(log);
        } catch (Exception e) {
            System.err.println("记录审核日志失败: " + e.getMessage());
        }
    }

    private void addToReviewQueue(Long userId, String contentType, Long contentId,
                                  String content, List<String> matchedWords, List<String> categories) {
        addToReviewQueue(userId, contentType, contentId, content, matchedWords, categories,
                "system", null, null, userId);
    }

    private void addToReviewQueue(Long userId, String contentType, Long contentId,
                                  String content, List<String> matchedWords, List<String> categories,
                                  String sourceType, Long reporterId, String reportReason, Long targetUserId) {
        try {
            ManualReviewQueue queue = new ManualReviewQueue();
            queue.setUserId(userId);
            queue.setContentType(contentType);
            queue.setContentId(contentId);
            queue.setContent(content);
            queue.setMatchedWords(String.join(",", matchedWords));
            queue.setCategories(String.join(",", categories));
            queue.setSourceType(sourceType);
            queue.setReporterId(reporterId);
            queue.setReportReason(reportReason);
            queue.setTargetUserId(targetUserId);
            queue.setStatus(ManualReviewQueue.STATUS_PENDING);
            queue.setCreateTime(LocalDateTime.now());
            manualReviewQueueMapper.insert(queue);
        } catch (Exception e) {
            System.err.println("添加到人工复审队列失败: " + e.getMessage());
        }
    }

    private boolean createUserReport(Long reporterId, Long ownerUserId, String contentType, Long contentId,
                                     String content, String reason) {
        try {
            String safeReason = reason == null || reason.isBlank() ? "其他" : reason.trim();
            logAudit(ownerUserId != null ? ownerUserId : reporterId,
                    contentType, contentId, content, 1,
                    null, "用户举报", AuditLog.RESULT_NEED_MANUAL);

            addToReviewQueue(ownerUserId != null ? ownerUserId : reporterId,
                    contentType,
                    contentId,
                    content,
                    new ArrayList<>(),
                    List.of("用户举报"),
                    "user_report",
                    reporterId,
                    safeReason,
                    ownerUserId);
            return true;
        } catch (Exception e) {
            System.err.println("创建举报审核记录失败: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<ManualReviewQueue> getPendingReviewList(int page, int size) {
        try {
            int offset = (page - 1) * size;
            return manualReviewQueueMapper.selectPending(offset, size);
        } catch (Exception e) {
            System.err.println("获取待复审列表失败: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public ManualReviewQueue getReviewQueue(Long queueId) {
        try {
            return manualReviewQueueMapper.selectById(queueId);
        } catch (Exception e) {
            System.err.println("获取待审核详情失败: " + e.getMessage());
            return null;
        }
    }

    @Override
    public int getPendingCount() {
        try {
            return manualReviewQueueMapper.countPending();
        } catch (Exception e) {
            System.err.println("获取待复审数量失败: " + e.getMessage());
            return 0;
        }
    }

    @Override
    public boolean approveContent(Long reviewerId, Long queueId, String remark) {
        return completeReportAction(reviewerId, queueId, "approve_report", remark);
    }

    @Override
    public boolean rejectContent(Long reviewerId, Long queueId, String remark) {
        return completeReportAction(reviewerId, queueId, "reject_report", remark);
    }

    @Override
    public boolean completeReportAction(Long reviewerId, Long queueId, String processAction, String remark) {
        try {
            ManualReviewQueue queue = manualReviewQueueMapper.selectById(queueId);
            if (queue == null) {
                return false;
            }

            boolean approved = !"reject_report".equals(processAction);
            manualReviewQueueMapper.updateStatus(
                    queueId,
                    approved ? ManualReviewQueue.STATUS_APPROVED : ManualReviewQueue.STATUS_REJECTED,
                    reviewerId,
                    remark,
                    processAction
            );

            if ("reject_report".equals(processAction)) {
                auditLogMapper.updateResult(queue.getContentId(), queue.getContentType(),
                        AuditLog.RESULT_MANUAL_REJECT, reviewerId, remark);
            } else if (!"delete_content".equals(processAction)) {
                auditLogMapper.updateResult(queue.getContentId(), queue.getContentType(),
                        AuditLog.RESULT_MANUAL_PASS, reviewerId, remark);
            }

            notifyReporter(queue, reviewerId, processAction, remark);
            return true;
        } catch (Exception e) {
            System.err.println("处理举报队列失败: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean offlineContent(Long auditorId, String contentType, Long contentId, String remark) {
        try {
            auditLogMapper.updateResult(contentId, contentType,
                    AuditLog.RESULT_OFFLINE, auditorId, remark);
            return true;
        } catch (Exception e) {
            System.err.println("事后下架失败: " + e.getMessage());
            return false;
        }
    }

    @Override
    public void reloadWordLibrary() {
        try {
            this.sensitiveWordBs = SensitiveWordBs.newInstance()
                    .ignoreCase(true)
                    .ignoreWidth(true)
                    .ignoreNumStyle(true)
                    .ignoreChineseStyle(true)
                    .ignoreRepeat(true)
                    .enableNumCheck(true)
                    .enableEmailCheck(true)
                    .enableUrlCheck(true)
                    .init();
            System.out.println("敏感词库热重载成功");
        } catch (Exception e) {
            System.err.println("敏感词库热重载失败: " + e.getMessage());
        }
    }

    @Override
    public Map<String, Object> getAuditStats() {
        Map<String, Object> stats = new HashMap<>();
        try {
            stats.put("total", auditLogMapper.countTotal());
            stats.put("autoPass", auditLogMapper.countByResult(AuditLog.RESULT_AUTO_PASS));
            stats.put("needManual", auditLogMapper.countByResult(AuditLog.RESULT_NEED_MANUAL));
            stats.put("manualPass", auditLogMapper.countByResult(AuditLog.RESULT_MANUAL_PASS));
            stats.put("manualReject", auditLogMapper.countByResult(AuditLog.RESULT_MANUAL_REJECT));
            stats.put("offline", auditLogMapper.countByResult(AuditLog.RESULT_OFFLINE));
            stats.put("pendingReview", getPendingCount());
        } catch (Exception e) {
            System.err.println("获取审核统计失败: " + e.getMessage());
        }
        return stats;
    }

    @Override
    public boolean reportPost(Long reporterId, Long postId, String postContent, Integer postUserId, String reason) {
        return createUserReport(reporterId,
                postUserId != null ? postUserId.longValue() : reporterId,
                "post",
                postId,
                postContent,
                reason);
    }

    @Override
    public boolean reportComment(Long reporterId, Long commentId, String commentContent, Integer postId, Integer commentUserId) {
        return reportComment(reporterId, commentId, commentContent, postId, commentUserId, "其他");
    }

    @Override
    public boolean reportComment(Long reporterId, Long commentId, String commentContent, Integer postId, Integer commentUserId, String reason) {
        return createUserReport(reporterId,
                commentUserId != null ? commentUserId.longValue() : reporterId,
                "comment",
                commentId,
                commentContent,
                reason);
    }

    @Override
    public boolean reportUser(Long reporterId, Long targetUserId, String targetProfileContent, String reason) {
        return createUserReport(reporterId,
                targetUserId,
                "user",
                targetUserId,
                targetProfileContent,
                reason);
    }

    @Override
    public boolean reportMessage(Long reporterId, Long messageId, String messageContent, Integer messageSenderId, String reason) {
        return createUserReport(reporterId,
                messageSenderId != null ? messageSenderId.longValue() : reporterId,
                "message",
                messageId,
                messageContent,
                reason);
    }

    private void notifyReporter(ManualReviewQueue queue, Long reviewerId, String processAction, String remark) {
        if (queue.getReporterId() == null) {
            return;
        }
        try {
            String title = "\u4e3e\u62a5\u5904\u7406\u7ed3\u679c";
            String suffix = (remark == null || remark.isBlank()) ? "" : " \u8bf4\u660e\uff1a" + remark;
            String content = switch (processAction) {
                case "reject_report" -> "\u7ecf\u5ba1\u6838\uff0c\u4e3e\u62a5\u5185\u5bb9\u6682\u65e0\u660e\u663e\u95ee\u9898\uff0c\u672c\u6b21\u4e3e\u62a5\u5df2\u9a73\u56de\u3002" + suffix;
                case "delete_content" -> "\u7ecf\u5ba1\u6838\uff0c\u76f8\u5173\u5185\u5bb9\u5df2\u88ab\u5220\u9664\u3002" + suffix;
                case "punish_user_warn" -> "\u7ecf\u5ba1\u6838\uff0c\u76f8\u5173\u7528\u6237\u5df2\u88ab\u8b66\u544a\u3002" + suffix;
                case "punish_user_restricted_post" -> "\u7ecf\u5ba1\u6838\uff0c\u76f8\u5173\u7528\u6237\u5df2\u88ab\u9650\u5236\u53d1\u5e16\u3002" + suffix;
                case "punish_user_restricted_comment" -> "\u7ecf\u5ba1\u6838\uff0c\u76f8\u5173\u7528\u6237\u5df2\u88ab\u9650\u5236\u8bc4\u8bba\u3002" + suffix;
                case "punish_user_temp_banned" -> "\u7ecf\u5ba1\u6838\uff0c\u76f8\u5173\u7528\u6237\u5df2\u88ab\u4e34\u65f6\u5c01\u7981\u3002" + suffix;
                case "punish_user_perm_banned" -> "\u7ecf\u5ba1\u6838\uff0c\u76f8\u5173\u7528\u6237\u5df2\u88ab\u6c38\u4e45\u5c01\u7981\u3002" + suffix;
                default -> "\u4f60\u63d0\u4ea4\u7684\u4e3e\u62a5\u5df2\u5904\u7406\u3002" + suffix;
            };

            notificationService.createNotification(
                    queue.getReporterId().intValue(),
                    "system",
                    reviewerId != null ? reviewerId.intValue() : null,
                    queue.getContentId() != null ? queue.getContentId().intValue() : null,
                    queue.getContentType(),
                    title,
                    content
            );
        } catch (Exception e) {
            System.err.println("通知举报人失败: " + e.getMessage());
        }
    }
}

