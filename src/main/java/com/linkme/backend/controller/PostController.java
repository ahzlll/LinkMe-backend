package com.linkme.backend.controller;

import com.linkme.backend.common.R;
import com.linkme.backend.entity.Post;
import com.linkme.backend.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 帖子控制器
 * 
 * 功能描述：
 * - 处理帖子相关的HTTP请求
 * - 包括帖子发布、编辑、删除、查询等功能
 * 
 * @author Ahz, riki
 * @version 1.0
 */
@RestController
@RequestMapping("/posts")
@Tag(name = "帖子管理", description = "帖子相关的API接口")
public class PostController {
    
    @Autowired
    private PostService postService;
    
    /**
     * 获取帖子列表
     * 
     * @param page 页码
     * @param limit 每页数量
     * @param tag 标签
     * @param userId 用户ID
     * @return 帖子列表
     */
    @GetMapping
    @Operation(summary = "获取帖子列表", description = "支持分页和过滤的帖子列表")
    public R<List<Post>> getPosts(@RequestParam(defaultValue = "1") Integer page,
                                 @RequestParam(defaultValue = "10") Integer limit,
                                 @RequestParam(required = false) Integer tag,
                                 @RequestParam(required = false) Integer userId) {
        List<Post> posts;
        
        if (userId != null) {
            posts = postService.getPostsByUserId(userId, page, limit);
        } else if (tag != null) {
            posts = postService.getPostsByTag(tag, page, limit);
        } else {
            posts = postService.getAllPosts(page, limit);
        }
        
        return R.ok(posts);
    }
    
    /**
     * 创建帖子
     * 
     * @param post 帖子信息
     * @return 创建结果
     */
    @PostMapping
    @Operation(summary = "创建帖子", description = "发布新帖子")
    public R<String> createPost(@RequestBody Post post) {
        boolean success = postService.createPost(post);
        if (success) {
            return R.ok("帖子发布成功");
        } else {
            return R.fail("帖子发布失败");
        }
    }
    
    /**
     * 获取指定帖子详情
     * 
     * @param postId 帖子ID
     * @return 帖子详情
     */
    @GetMapping("/{postId}")
    @Operation(summary = "获取帖子详情", description = "根据帖子ID获取帖子详细信息")
    public R<Post> getPostById(@PathVariable @Parameter(description = "帖子ID") Integer postId) {
        Post post = postService.getPostById(postId);
        if (post != null) {
            return R.ok(post);
        } else {
            return R.fail(404, "帖子不存在");
        }
    }
    
    /**
     * 编辑帖子
     * 
     * @param postId 帖子ID
     * @param post 帖子信息
     * @return 更新结果
     */
    @PutMapping("/{postId}")
    @Operation(summary = "编辑帖子", description = "更新帖子内容")
    public R<String> updatePost(@PathVariable @Parameter(description = "帖子ID") Integer postId,
                              @RequestBody Post post) {
        post.setPostId(postId);
        boolean success = postService.updatePost(post);
        if (success) {
            return R.ok("帖子更新成功");
        } else {
            return R.fail("帖子更新失败");
        }
    }
    
    /**
     * 删除帖子
     * 
     * @param postId 帖子ID
     * @return 删除结果
     */
    @DeleteMapping("/{postId}")
    @Operation(summary = "删除帖子", description = "根据帖子ID删除帖子")
    public R<String> deletePost(@PathVariable @Parameter(description = "帖子ID") Integer postId) {
        boolean success = postService.deletePost(postId);
        if (success) {
            return R.ok("帖子删除成功");
        } else {
            return R.fail("帖子删除失败");
        }
    }
}
