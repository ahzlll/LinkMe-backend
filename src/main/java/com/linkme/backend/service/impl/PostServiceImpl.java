package com.linkme.backend.service.impl;

import com.linkme.backend.entity.Post;
import com.linkme.backend.mapper.PostMapper;
import com.linkme.backend.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 帖子服务实现类
 * 
 * 功能描述：
 * - 实现帖子相关的业务逻辑处理
 * - 包括帖子发布、编辑、删除、查询等功能
 * 
 * @author Ahz, riki
 * @version 1.0
 */
@Service
public class PostServiceImpl implements PostService {
    
    @Autowired
    private PostMapper postMapper;
    
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
}
