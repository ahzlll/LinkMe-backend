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
 * version: 1.2
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

        @Schema(description = "理想关系模式ID")
        private Integer relationshipModeId;

        @Schema(description = "沟通期待ID")
        private Integer communicationExpectationId;

        @Schema(description = "其他交友要求")
        private String additionalRequirements;

        @Schema(description = "性格特质选择列表")
        private List<PersonalitySelectionRequest> personalities;

        @Schema(description = "关系品质选择列表")
        private List<RelationshipQualityRequest> relationshipQualities;

        @Schema(description = "必须匹配维度ID列表")
        private List<Integer> mustDimensions;

        @Schema(description = "优先匹配维度列表")
        private List<PriorityDimensionRequest> priorityDimensions;

        @Data
        @Schema(description = "性格特质选择")
        public static class PersonalitySelectionRequest {
            @Schema(description = "性格特质选项ID")
            private Integer traitOptionId;
            @Schema(description = "选择类型(self/ideal)")
            private String selectionType;
        }

        @Data
        @Schema(description = "关系品质选择")
        public static class RelationshipQualityRequest {
            @Schema(description = "关系品质ID")
            private Integer qualityId;
        }

        @Data
        @Schema(description = "优先匹配维度")
        public static class PriorityDimensionRequest {
            @Schema(description = "维度ID")
            private Integer dimensionId;
            @Schema(description = "优先级(high/medium/low)")
            private String priority;
        }
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

        @Schema(description = "理想关系模式ID")
        private Integer relationshipModeId;

        @Schema(description = "沟通期待ID")
        private Integer communicationExpectationId;

        @Schema(description = "其他交友要求")
        private String additionalRequirements;

        @Schema(description = "性格特质选择列表")
        private List<PersonalitySelectionResponse> personalities;

        @Schema(description = "关系品质选择列表")
        private List<RelationshipQualityResponse> relationshipQualities;

        @Schema(description = "必须匹配维度列表")
        private List<DimensionResponse> mustDimensions;

        @Schema(description = "优先匹配维度列表")
        private List<PriorityDimensionResponse> priorityDimensions;

        @Data
        @Schema(description = "性格特质选择响应")
        public static class PersonalitySelectionResponse {
            @Schema(description = "性格特质选项ID")
            private Integer traitOptionId;
            @Schema(description = "选择类型")
            private String selectionType;
            @Schema(description = "特质分类名称")
            private String categoryName;
            @Schema(description = "特质选项名称")
            private String optionName;
        }

        @Data
        @Schema(description = "关系品质响应")
        public static class RelationshipQualityResponse {
            @Schema(description = "关系品质ID")
            private Integer qualityId;
            @Schema(description = "品质名称")
            private String qualityName;
        }

        @Data
        @Schema(description = "维度响应")
        public static class DimensionResponse {
            @Schema(description = "维度ID")
            private Integer dimensionId;
            @Schema(description = "维度名称")
            private String dimensionName;
        }

        @Data
        @Schema(description = "优先维度响应")
        public static class PriorityDimensionResponse {
            @Schema(description = "维度ID")
            private Integer dimensionId;
            @Schema(description = "维度名称")
            private String dimensionName;
            @Schema(description = "优先级")
            private String priority;
        }
    }
}


