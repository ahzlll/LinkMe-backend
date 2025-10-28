package com.linkme.backend.service.impl;

import com.linkme.backend.entity.Post;
import com.linkme.backend.entity.PostImage;
import com.linkme.backend.mapper.PostMapper;
import com.linkme.backend.mapper.PostImageMapper;
import com.linkme.backend.mapper.PostTagMapper;
import com.linkme.backend.mapper.LikeMapper;
import com.linkme.backend.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 帖子服务实现类
 *
 * 职责：
 * - 实现帖子服务接口定义的业务逻辑
 * - 创建帖子（含图片与标签批量写入）
 * - 查询帖子列表与详情聚合（图片、标签、点赞数）
 * - 编辑与删除帖子
 *
 * author: riki
 * version: 1.1
 */
@Service
public class PostServiceImpl implements PostService {
    
    @Autowired
    private PostMapper postMapper;
    @Autowired
    private PostImageMapper postImageMapper;
    @Autowired
    private PostTagMapper postTagMapper;
    @Autowired
    private LikeMapper likeMapper;
    
    @Override
    public Post getPostById(Integer postId) {
        return postMapper.selectById(postId);
    }
    
    @Override
    public List<Post> getPostsByUserId(Integer userId, Integer page, Integer size) {
        int offset = (page - 1) * size;
        return postMapper.selectByUserId(userId, offset, size);
    }
    
    @Override
    public List<Post> getAllPosts(Integer page, Integer size) {
        int offset = (page - 1) * size;
        return postMapper.selectAll(offset, size);
    }
    
    @Override
    public List<Post> getPostsByTag(Integer tagId, Integer page, Integer size) {
        int offset = (page - 1) * size;
        return postMapper.selectByTag(tagId, offset, size);
    }
    
    @Override
    public boolean createPost(Post post) {
        try {
            // 设置创建时间
            post.setCreatedAt(LocalDateTime.now());
            
            // 插入帖子
            return postMapper.insert(post) > 0;
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public boolean updatePost(Post post) {
        try {
            return postMapper.update(post) > 0;
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public boolean deletePost(Integer postId) {
        try {
            return postMapper.deleteById(postId) > 0;
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public int getPostCount() {
        return postMapper.countAll();
    }
    
    @Override
    public int getPostCountByUserId(Integer userId) {
        return postMapper.countByUserId(userId);
    }

    @Override
    public Map<String, Object> getPostAggregates(Integer postId) {
        Map<String, Object> map = new HashMap<>();
        List<PostImage> images = postImageMapper.selectByPostId(postId);
        List<Integer> tagIds = postTagMapper.selectTagIdsByPostId(postId);
        int likeCount = likeMapper.countByPostId(postId);
        List<String> imageUrls = new ArrayList<>();
        for (PostImage img : images) {
            imageUrls.add(img.getImageUrl());
        }
        map.put("images", imageUrls);
        map.put("tags", tagIds);
        map.put("likes", likeCount);
        return map;
    }

    @Override
    public boolean createPostWithMediaAndTags(Integer userId, String content, List<String> images, List<Integer> tagIds) {
        Post post = new Post();
        post.setUserId(userId);
        post.setContent(content);
        post.setCreatedAt(LocalDateTime.now());
        if (postMapper.insert(post) <= 0) {
            return false;
        }
        Integer postId = post.getPostId();
        if (images != null && !images.isEmpty()) {
            postImageMapper.insertBatch(postId, images);
        }
        if (tagIds != null && !tagIds.isEmpty()) {
            postTagMapper.insertBatch(postId, tagIds);
        }
        return true;
    }
}
