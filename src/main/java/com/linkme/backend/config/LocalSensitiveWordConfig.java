package com.linkme.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashSet;
import java.util.Set;

@Configuration
public class LocalSensitiveWordConfig {

    @Bean
    public Set<String> localSensitiveWords() {
        Set<String> words = new HashSet<>();

        words.add("台独");
        words.add("分裂国家");
        words.add("颠覆政权");
        words.add("暴力恐怖");
        words.add("血腥暴力");
        words.add("色情低俗");
        words.add("赌博诈骗");
        words.add("毒品");
        words.add("武器");
        words.add("假币");
        words.add("反动");
        words.add("邪教");
        words.add("敏感");
        words.add("裸");
        words.add("色情");
        words.add("赌场");
        words.add("博彩");
        words.add("毒品");
        words.add("制毒");
        words.add("贩卖毒品");
        words.add("腐败");
        words.add("贪污");
        words.add("反动言论");
        words.add("分裂");
        words.add("独立");
        words.add("抵制");
        words.add("游行");
        words.add("示威");
        words.add("罢工");
        words.add("军火");
        words.add("枪支");
        words.add("爆炸物");
        words.add("核弹");
        words.add("原子弹");
        words.add("氢弹");
        words.add("造假");
        words.add("假货");
        words.add("诈骗");
        words.add("电信诈骗");
        words.add("网络诈骗");
        words.add("钓鱼网站");
        words.add("黑客");
        words.add("入侵");
        words.add("木马");
        words.add("病毒");
        words.add("色情网站");
        words.add("成人网站");
        words.add("一夜情");
        words.add("约炮");
        words.add("援交");
        words.add("出售");
        words.add("买入");
        words.add("联系方式");
        words.add("微信号");
        words.add("QQ号");
        words.add("手机号");
        words.add("私人联系方式");

        return words;
    }
}
