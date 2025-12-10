package com.linkme.backend.controller;

import com.linkme.backend.common.R;
import com.linkme.backend.common.JwtUtil;
import com.linkme.backend.entity.Post;
import com.linkme.backend.entity.Comment;
import com.linkme.backend.service.PostService;
import com.linkme.backend.mapper.CommentMapper;
import com.linkme.backend.mapper.LikeMapper;
import com.linkme.backend.controller.dto.PostCreateRequest;
import com.linkme.backend.controller.dto.PostDetailResponse;
import jakarta.servlet.http.HttpServletRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.ArrayList;
import java.util.Base64;
import java.io.IOException;

/**
 * 帖子控制器
 *
 * 职责：
 * - 处理帖子相关的 HTTP 请求
 * - 提供帖子列表、创建、详情、编辑、删除
 * - 提供评论创建/列表、点赞/取消点赞能力
 *
 * 说明：严格遵循 API.md/README.md 约定，不修改接口路径与入参结构。
 *
 * author: riki
 * version: 1.2
 */
@RestController
@RequestMapping("/posts")
@Tag(name = "帖子管理", description = "帖子相关的API接口")
public class PostController {
    
    @Autowired
    private PostService postService;
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private LikeMapper likeMapper;
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 从请求头中获取当前用户ID
     */
    private Integer getCurrentUserId(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            try {
                return jwtUtil.getUserIdFromToken(token);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }
    
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
    @Operation(summary = "获取帖子列表", description = "支持分页和过滤的帖子列表",
               security = @SecurityRequirement(name = "bearerAuth"))
    public R<List<Post>> getPosts(@RequestParam(defaultValue = "1") Integer page,
                                 @RequestParam(defaultValue = "10") Integer limit,
                                 @RequestParam(required = false) Integer tag,
                                 @RequestParam(required = false) Integer userId,
                                 HttpServletRequest request) {
        List<Post> posts;
        Integer currentUserId = getCurrentUserId(request);
        
        if (userId != null) {
            posts = postService.getPostsByUserId(userId, page, limit, currentUserId);
        } else if (tag != null) {
            posts = postService.getPostsByTag(tag, page, limit, currentUserId);
        } else {
            posts = postService.getAllPosts(page, limit, currentUserId);
        }
        
        return R.ok(posts);
    }
    
    /**
     * 创建帖子（JSON格式）
     * 注意：images字段应为Base64编码的字符串列表
     * 
     * @param req 帖子请求信息
     * @return 创建结果
     */
    @PostMapping(consumes = {"application/json"})
    @Operation(summary = "创建帖子（JSON格式）", description = "发布新帖子，images字段应为Base64编码字符串列表",
               security = @SecurityRequirement(name = "bearerAuth"))
    public R<String> createPost(@RequestBody PostCreateRequest req) {
        boolean success = postService.createPostWithMediaAndTags(req.getUserId(), req.getContent(), req.getTopic(), req.getImages(), req.getTags());
        if (success) {
            return R.ok("帖子发布成功");
        } else {
            return R.fail("帖子发布失败");
        }
    }
    
    /**
     * 创建帖子（文件上传方式）
     * 支持直接上传图片文件，系统会自动将文件转换为Base64编码存储
     * 
     * @param userId 用户ID
     * @param content 帖子内容
     * @param topic 帖子主题
     * @param images 图片文件列表（可选）
     * @param tags 标签ID列表（可选，JSON格式字符串，如 "[1,2,3]"）
     * @return 创建结果
     */
    @PostMapping(value = "/upload", consumes = {"multipart/form-data"})
    @Operation(summary = "创建帖子（文件上传）", description = "发布新帖子，支持直接上传图片文件，系统会自动转换为Base64编码",
               security = @SecurityRequirement(name = "bearerAuth"))
    public R<String> createPostWithFiles(
            @RequestParam("userId") Integer userId,
            @RequestParam("content") String content,
            @RequestParam(required = false) String topic,
            @RequestParam(required = false) List<MultipartFile> images,
            @RequestParam(required = false) String tags) {
        try {
            // 将图片文件转换为Base64字符串列表
            List<String> base64Images = null;
            if (images != null && !images.isEmpty()) {
                base64Images = new ArrayList<>();
                for (MultipartFile file : images) {
                    if (!file.isEmpty()) {
                        byte[] imageBytes = file.getBytes();
                        String base64String = Base64.getEncoder().encodeToString(imageBytes);
                        base64Images.add(base64String);
                    }
                }
            }
            
            // 解析标签ID列表
            List<Integer> tagIds = null;
            if (tags != null && !tags.trim().isEmpty()) {
                // 简单解析JSON数组格式的字符串，如 "[1,2,3]"
                tagIds = new ArrayList<>();
                String cleanTags = tags.trim().replaceAll("[\\[\\]\\s]", "");
                if (!cleanTags.isEmpty()) {
                    String[] tagArray = cleanTags.split(",");
                    for (String tag : tagArray) {
                        try {
                            tagIds.add(Integer.parseInt(tag.trim()));
                        } catch (NumberFormatException e) {
                            // 忽略无效的标签ID
                        }
                    }
                }
            }
            
            boolean success = postService.createPostWithMediaAndTags(userId, content, topic, base64Images, tagIds);
            if (success) {
                return R.ok("帖子发布成功");
            } else {
                return R.fail("帖子发布失败");
            }
        } catch (IOException e) {
            return R.fail("图片处理失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取指定帖子详情
     * 
     * @param postId 帖子ID
     * @return 帖子详情
     */
    @GetMapping("/{postId}")
    @Operation(summary = "获取帖子详情", description = "根据帖子ID获取帖子详细信息",
               security = @SecurityRequirement(name = "bearerAuth"))
    public R<PostDetailResponse> getPostById(@PathVariable @Parameter(description = "帖子ID") Integer postId) {
        Post post = postService.getPostById(postId);
        if (post == null) {
            return R.fail(404, "帖子不存在");
        }
        PostDetailResponse resp = new PostDetailResponse();
        resp.setPostId(post.getPostId());
        resp.setUserId(post.getUserId());
        resp.setContent(post.getContent());
        resp.setTopic(post.getTopic());
        resp.setCreatedAt(post.getCreatedAt());
        // 设置用户信息
        resp.setNickname(post.getNickname());
        resp.setUsername(post.getUsername());
        resp.setAvatarUrl(post.getAvatarUrl());
        var agg = postService.getPostAggregates(postId);
        @SuppressWarnings("unchecked")
        java.util.List<String> images = (java.util.List<String>) agg.get("images");
        resp.setImages(images);
        @SuppressWarnings("unchecked")
        java.util.List<Integer> tags = (java.util.List<Integer>) agg.get("tags");
        resp.setTags(tags);
        resp.setLikeCount((Integer) agg.get("likeCount"));
        return R.ok(resp);
    }
    
    /**
     * 编辑帖子
     * 
     * @param postId 帖子ID
     * @param post 帖子信息
     * @return 更新结果
     */
    @PutMapping("/{postId}")
    @Operation(summary = "编辑帖子", description = "更新帖子内容",
               security = @SecurityRequirement(name = "bearerAuth"))
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
    @Operation(summary = "删除帖子", description = "根据帖子ID删除帖子",
               security = @SecurityRequirement(name = "bearerAuth"))
    public R<String> deletePost(@PathVariable @Parameter(description = "帖子ID") Integer postId) {
        boolean success = postService.deletePost(postId);
        if (success) {
            return R.ok("帖子删除成功");
        } else {
            return R.fail("帖子删除失败");
        }
    }

    // 发表评论
    @PostMapping("/{postId}/comments")
    @Operation(summary = "发表评论", security = @SecurityRequirement(name = "bearerAuth"))
    public R<String> addComment(@PathVariable Integer postId, @RequestBody Comment comment) {
        comment.setPostId(postId);
        comment.setCreatedAt(java.time.LocalDateTime.now());
        int rows = commentMapper.insert(comment);
        return rows > 0 ? R.ok("评论成功") : R.fail("评论失败");
    }

    // 获取评论列表
    @GetMapping("/{postId}/comments")
    @Operation(summary = "获取评论列表", security = @SecurityRequirement(name = "bearerAuth"))
    public R<java.util.List<Comment>> listComments(@PathVariable Integer postId,
                                                   @RequestParam(defaultValue = "1") Integer page,
                                                   @RequestParam(defaultValue = "10") Integer limit) {
        int offset = (page - 1) * limit;
        var list = commentMapper.selectByPostId(postId, offset, limit);
        return R.ok(list);
    }

    // 点赞帖子
    @PostMapping("/{postId}/like")
    @Operation(summary = "点赞帖子", security = @SecurityRequirement(name = "bearerAuth"))
    public R<String> likePost(@PathVariable Integer postId, @RequestBody com.linkme.backend.entity.Like body) {
        if (likeMapper.selectByUserAndPost(body.getUserId(), postId) != null) {
            return R.ok("已点赞");
        }
        body.setPostId(postId);
        body.setCreatedAt(java.time.LocalDateTime.now());
        int rows = likeMapper.insert(body);
        return rows > 0 ? R.ok("点赞成功") : R.fail("点赞失败");
    }

    // 取消点赞
    @DeleteMapping("/{postId}/like")
    @Operation(summary = "取消点赞", security = @SecurityRequirement(name = "bearerAuth"))
    public R<String> unlikePost(@PathVariable Integer postId, @RequestBody com.linkme.backend.entity.Like body) {
        int rows = likeMapper.deleteByUserAndPost(body.getUserId(), postId);
        return rows > 0 ? R.ok("已取消点赞") : R.fail("取消失败");
    }
}
