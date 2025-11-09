package com.linkme.backend.service;

import com.linkme.backend.entity.Post;
import java.util.List;
import java.util.Map;

/**
 * 帖子服务接口
 *
 * 职责：
 * - 提供帖子相关的业务逻辑
 * - 支持帖子创建（可带图片/标签）、编辑、删除、查询
 * - 提供帖子详情聚合（图片、标签、点赞数）
 *
 * author: riki
 * version: 1.1
 */
public interface PostService {
    
    /**
     * 根据帖子ID获取帖子信息
     * 
     * @param postId 帖子ID
     * @return 帖子信息
     */
    Post getPostById(Integer postId);
    
    /**
     * 根据用户ID获取帖子列表
     * 
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页数量
     * @return 帖子列表
     */
    List<Post> getPostsByUserId(Integer userId, Integer page, Integer size);
    
    /**
     * 获取所有帖子列表（分页）
     * 
     * @param page 页码
     * @param size 每页数量
     * @return 帖子列表
     */
    List<Post> getAllPosts(Integer page, Integer size);
    
    /**
     * 根据标签获取帖子列表
     * 
     * @param tagId 标签ID
     * @param page 页码
     * @param size 每页数量
     * @return 帖子列表
     */
    List<Post> getPostsByTag(Integer tagId, Integer page, Integer size);
    
    /**
     * 创建新帖子
     * 
     * @param post 帖子信息
     * @return 创建结果
     */
    boolean createPost(Post post);
    
    /**
     * 更新帖子信息
     * 
     * @param post 帖子信息
     * @return 更新结果
     */
    boolean updatePost(Post post);
    
    /**
     * 删除帖子
     * 
     * @param postId 帖子ID
     * @return 删除结果
     */
    boolean deletePost(Integer postId);
    
    /**
     * 获取帖子总数
     * 
     * @return 帖子总数
     */
    int getPostCount();
    
    /**
     * 根据用户ID获取帖子数量
     * 
     * @param userId 用户ID
     * @return 帖子数量
     */
    int getPostCountByUserId(Integer userId);

    // 聚合：返回帖子详情的图片、标签、点赞数
    Map<String, Object> getPostAggregates(Integer postId);

    // 扩展创建：带图片与标签
    boolean createPostWithMediaAndTags(Integer userId, String content, String topic, java.util.List<String> images, java.util.List<Integer> tagIds);
}
