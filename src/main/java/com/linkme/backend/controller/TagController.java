package com.linkme.backend.controller;

import com.linkme.backend.common.R;
import com.linkme.backend.entity.TagDef;
import com.linkme.backend.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 标签管理控制器
 *
 * 职责：
 * - 提供标签查询与创建接口
 *
 * author: riki
 * version: 1.0
 */
@RestController
@RequestMapping("/tags")
@Tag(name = "标签管理", description = "标签相关的API接口")
public class TagController {

    @Autowired
    private TagService tagService;

    /**
     * 获取标签列表
     *
     * @param type 标签类型："post" 或 "user"，默认 "post"
     * @return 标签列表
     */
    @GetMapping
    @Operation(summary = "获取标签列表", description = "按类型获取标签列表",
            security = @SecurityRequirement(name = "bearerAuth"))
    public R<List<TagDef>> listTags(
            @RequestParam(name = "type", defaultValue = "post")
            @Parameter(description = "标签类型：post 或 user") String type) {
        List<TagDef> list = tagService.getTagsByType(type);
        return R.ok(list);
    }

    /**
     * 创建新标签
     *
     * 说明：目前简单实现为任何登录用户都可以创建标签，
     * 如果后续需要严格控制，可在此处增加角色校验。
     *
     * @param req 标签创建请求
     * @return 新建或已存在的标签
     */
    @PostMapping
    @Operation(summary = "创建标签", description = "创建新的标签定义",
            security = @SecurityRequirement(name = "bearerAuth"))
    public R<TagDef> createTag(@RequestBody TagCreateRequest req) {
        if (req == null || req.getName() == null || req.getName().trim().isEmpty()) {
            return R.fail("标签名称不能为空");
        }
        String type = (req.getTagType() == null || req.getTagType().isBlank())
                ? "post"
                : req.getTagType();
        TagDef tag = tagService.createTag(req.getName().trim(), type, req.getCreatedBy());
        return R.ok(tag);
    }

    /**
     * 标签创建请求 DTO
     *
     * author: riki
     * version: 1.0
     */
    public static class TagCreateRequest {
        /**
         * 标签名称
         */
        private String name;

        /**
         * 标签类型："post" 或 "user"
         */
        private String tagType;

        /**
         * 创建者用户ID（可选）
         */
        private Integer createdBy;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getTagType() {
            return tagType;
        }

        public void setTagType(String tagType) {
            this.tagType = tagType;
        }

        public Integer getCreatedBy() {
            return createdBy;
        }

        public void setCreatedBy(Integer createdBy) {
            this.createdBy = createdBy;
        }
    }
}


