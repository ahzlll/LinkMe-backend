package com.linkme.backend.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "问卷爱好选项（来自数据库 hobby 表）")
public class HobbyOptionResponse {

    @Schema(description = "分类ID")
    private Integer categoryId;

    @Schema(description = "分类名称")
    private String name;

    @Schema(description = "该分类下的爱好列表")
    private List<HobbyItem> hobbies;

    @Data
    @Schema(description = "爱好选项")
    public static class HobbyItem {
        @Schema(description = "爱好ID")
        private Integer hobbyId;

        @Schema(description = "爱好名称（与数据库 hobby.name 一致）")
        private String name;

        @Schema(description = "历史兼容用的英文代码，可能为空")
        private String code;
    }
}
