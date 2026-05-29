package com.linkme.backend.service.impl;

import com.github.houbb.sensitive.word.bs.SensitiveWordBs;
import com.linkme.backend.entity.AuditLog;
import com.linkme.backend.entity.ManualReviewQueue;
import com.linkme.backend.mapper.AuditLogMapper;
import com.linkme.backend.mapper.ManualReviewQueueMapper;
import com.linkme.backend.service.AuditService;
import com.linkme.backend.service.LocalSensitiveWordService;
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

        if (sensitiveWordBs == null) {
            if (localSensitiveWordService != null) {
                return checkWithLocalService(userId, contentType, contentId, content);
            }
            logAudit(userId, contentType, contentId, content, 1, "SYSTEM_ERROR",
                    "敏感词库未初始化", AuditLog.RESULT_NEED_MANUAL);
            addToReviewQueue(userId, contentType, contentId, content,
                    java.util.Arrays.asList("SYSTEM_ERROR"),
                    java.util.Arrays.asList("敏感词库未初始化"));
            return new AuditResult(false, true,
                    java.util.Arrays.asList("SYSTEM_ERROR"),
                    java.util.Arrays.asList("敏感词库未初始化"));
        }

        try {
            List<String> matchedWords = sensitiveWordBs.findAll(content);

            if (matchedWords == null || matchedWords.isEmpty()) {
                logAudit(userId, contentType, contentId, content, 0, null, null, AuditLog.RESULT_AUTO_PASS);
                return new AuditResult(true, false, new ArrayList<>(), new ArrayList<>());
            }

            List<String> categories = matchedWords.stream()
                    .map(w -> getCategoryForWord(w))
                    .distinct()
                    .collect(Collectors.toList());

            logAudit(userId, contentType, contentId, content, 1,
                    String.join(",", matchedWords),
                    String.join(",", categories),
                    AuditLog.RESULT_NEED_MANUAL);

            addToReviewQueue(userId, contentType, contentId, content, matchedWords, categories);

            return new AuditResult(false, true, matchedWords, categories);

        } catch (Exception e) {
            if (localSensitiveWordService != null) {
                return checkWithLocalService(userId, contentType, contentId, content);
            }
            logAudit(userId, contentType, contentId, content, 1, "SYSTEM_ERROR",
                    "审核服务异常", AuditLog.RESULT_NEED_MANUAL);
            addToReviewQueue(userId, contentType, contentId, content,
                    java.util.Arrays.asList("SYSTEM_ERROR"),
                    java.util.Arrays.asList("审核服务异常"));
            return new AuditResult(false, true,
                    java.util.Arrays.asList("SYSTEM_ERROR"),
                    java.util.Arrays.asList("审核服务异常"));
        }
    }

    private String getCategoryForWord(String word) {
        return "敏感词";
    }

    private AuditResult checkWithLocalService(Long userId, String contentType, Long contentId, String content) {
        List<String> matchedWords = localSensitiveWordService.findAll(content);

        if (matchedWords == null || matchedWords.isEmpty()) {
            logAudit(userId, contentType, contentId, content, 0, null, null, AuditLog.RESULT_AUTO_PASS);
            return new AuditResult(true, false, new ArrayList<>(), new ArrayList<>());
        }

        List<String> categories = matchedWords.stream()
                .map(w -> getCategoryForWord(w))
                .distinct()
                .collect(Collectors.toList());

        logAudit(userId, contentType, contentId, content, 1,
                String.join(",", matchedWords),
                String.join(",", categories),
                AuditLog.RESULT_NEED_MANUAL);

        addToReviewQueue(userId, contentType, contentId, content, matchedWords, categories);

        return new AuditResult(false, true, matchedWords, categories);
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
        try {
            ManualReviewQueue queue = new ManualReviewQueue();
            queue.setUserId(userId);
            queue.setContentType(contentType);
            queue.setContentId(contentId);
            queue.setContent(content);
            queue.setMatchedWords(String.join(",", matchedWords));
            queue.setCategories(String.join(",", categories));
            queue.setStatus(ManualReviewQueue.STATUS_PENDING);
            queue.setCreateTime(LocalDateTime.now());
            manualReviewQueueMapper.insert(queue);
        } catch (Exception e) {
            System.err.println("添加到人工复审队列失败: " + e.getMessage());
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
        try {
            ManualReviewQueue queue = manualReviewQueueMapper.selectById(queueId);
            if (queue == null) {
                return false;
            }

            manualReviewQueueMapper.updateStatus(queueId, ManualReviewQueue.STATUS_APPROVED, reviewerId, remark);

            auditLogMapper.updateResult(queue.getContentId(), queue.getContentType(),
                    AuditLog.RESULT_MANUAL_PASS, reviewerId, remark);

            return true;
        } catch (Exception e) {
            System.err.println("审核通过失败: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean rejectContent(Long reviewerId, Long queueId, String remark) {
        try {
            ManualReviewQueue queue = manualReviewQueueMapper.selectById(queueId);
            if (queue == null) {
                return false;
            }

            manualReviewQueueMapper.updateStatus(queueId, ManualReviewQueue.STATUS_REJECTED, reviewerId, remark);

            auditLogMapper.updateResult(queue.getContentId(), queue.getContentType(),
                    AuditLog.RESULT_MANUAL_REJECT, reviewerId, remark);

            return true;
        } catch (Exception e) {
            System.err.println("审核拒绝失败: " + e.getMessage());
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
}
