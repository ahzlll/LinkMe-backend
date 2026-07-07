package com.linkme.backend.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.List;

/**
 * 创建帖子请求（JSON格式）
 *
 * 对齐 API.md：
 * - userId: 发帖用户
 * - content: 文本内容
 * - images: 图片 Base64 编码字符串列表
 * - tags: 标签 ID 列表
 *
 * author: riki
 * version: 2.0
 */
@Data
public class PostCreateRequest {
    private Integer userId;
    private String content;
    private String topic;
    private List<String> images; // Base64编码的图片字符串列表
    private List<Integer> tags;

    @Data
    @Schema(description = "问卷提交请求")
    public static class QuestionnaireRequest {

        @Schema(description = "最小年龄要求")
        private Integer ageMin;

        @Schema(description = "最大年龄要求")
        private Integer ageMax;

        @Schema(description = "是否无年龄限制")
        private Boolean ageUnlimited;

        @Schema(description = "距离偏好(same_city/same_city_or_remote/unlimited)")
        private String distancePreference;

        @Schema(description = "其他交友要求")
        private String additionalRequirements;

        @Schema(description = "兴趣代码列表（如 photography, reading 等）")
        private List<String> interests;

        @Schema(description = "社交能量代码（extroverted/introverted/ambivert）")
        private String socialEnergy;

        @Schema(description = "决策方式代码（rational/emotional/balanced）")
        private String decisionMaking;

        @Schema(description = "生活节奏代码（planned/casual/flexible）")
        private String lifeRhythm;

        @Schema(description = "用户头像(Base64编码，选填)")
        private String avatarUrl;
    }

    @Data
    @Schema(description = "问卷数据响应")
    public static class QuestionnaireResponse {

        @Schema(description = "用户ID")
        private Integer userId;

        @Schema(description = "最小年龄要求")
        private Integer ageMin;

        @Schema(description = "最大年龄要求")
        private Integer ageMax;

        @Schema(description = "是否无年龄限制")
        private Boolean ageUnlimited;

        @Schema(description = "距离偏好")
        private String distancePreference;

        @Schema(description = "其他交友要求")
        private String additionalRequirements;

        @Schema(description = "兴趣代码列表")
        private List<String> interests;

        @Schema(description = "社交能量代码")
        private String socialEnergy;

        @Schema(description = "决策方式代码")
        private String decisionMaking;

        @Schema(description = "生活节奏代码")
        private String lifeRhythm;

        @Schema(description = "用户头像URL")
        private String avatarUrl;
    }
}
