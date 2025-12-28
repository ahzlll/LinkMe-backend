package com.linkme.backend.service;

/**
 * AI助手开关服务接口
 *
 * 职责：
 * - 提供AI情感聊天助手的启用/禁用状态管理
 * - 支持运行时切换开关状态
 *
 * @author riki
 * @version 1.0
 */
public interface AISettingsService {
    /**
     * 获取AI助手是否启用
     *
     * @return true表示启用；false表示禁用
     */
    boolean isEnabled();
    /**
     * 设置AI助手启用/禁用状态
     *
     * @param enabled true表示启用；false表示禁用
     */
    void setEnabled(boolean enabled);
}
