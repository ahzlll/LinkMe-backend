package com.linkme.backend.service.impl;

import com.linkme.backend.service.AISettingsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * AI助手开关服务实现
 *
 * 职责：
 * - 从配置读取初始开关状态
 * - 在内存中维护可变的开关状态（线程安全）
 *
 * @author riki
 * @version 1.0
 */
@Service
public class AISettingsServiceImpl implements AISettingsService {
    private final AtomicBoolean enabled;
    /**
     * 构造函数
     *
     * @param initEnabled 初始开关状态，来自配置项 ai.enabled，默认 true
     */
    public AISettingsServiceImpl(@Value("${ai.enabled:true}") boolean initEnabled) {
        this.enabled = new AtomicBoolean(initEnabled);
    }
    @Override
    /**
     * 获取AI助手是否启用
     */
    public boolean isEnabled() {
        return enabled.get();
    }
    @Override
    /**
     * 设置AI助手启用/禁用
     *
     * @param enabled true启用，false禁用
     */
    public void setEnabled(boolean enabled) {
        this.enabled.set(enabled);
    }
}
