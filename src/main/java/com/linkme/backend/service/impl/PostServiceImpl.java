package com.linkme.backend.service.impl;

import com.linkme.backend.entity.Post;
import com.linkme.backend.entity.PostImage;
import com.linkme.backend.mapper.PostMapper;
import com.linkme.backend.mapper.PostImageMapper;
import com.linkme.backend.mapper.PostTagMapper;
import com.linkme.backend.mapper.LikeMapper;
import com.linkme.backend.mapper.CommentMapper;
import com.linkme.backend.mapper.FavoriteMapper;
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
 * - 图片以Base64编码字符串形式存储到数据库
 * - 查询帖子列表与详情聚合（图片、标签、点赞数）
 * - 编辑与删除帖子
 *
 * author: riki
 * version: 1.2
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
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private FavoriteMapper favoriteMapper;
    
    @Override
    public Post getPostById(Integer postId) {
        return postMapper.selectById(postId);
    }
    
    @Override
    public List<Post> getPostsByUserId(Integer userId, Integer page, Integer size, Integer currentUserId) {
        int offset = (page - 1) * size;
        return postMapper.selectByUserId(userId, offset, size, currentUserId);
    }
    
    @Override
    public List<Post> getAllPosts(Integer page, Integer size, Integer currentUserId) {
        int offset = (page - 1) * size;
        return postMapper.selectAll(offset, size, currentUserId);
    }
    
    @Override
    public List<Post> getPostsByTag(Integer tagId, Integer page, Integer size, Integer currentUserId) {
        int offset = (page - 1) * size;
        return postMapper.selectByTag(tagId, offset, size, currentUserId);
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
        
        // 安全地获取图片列表
        List<String> imageUrls = new ArrayList<>();
        try {
            List<PostImage> images = postImageMapper.selectByPostId(postId);
            if (images != null) {
                for (PostImage img : images) {
                    if (img != null && img.getImageUrl() != null) {
                        imageUrls.add(img.getImageUrl());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("获取帖子图片失败, postId=" + postId + ", error=" + e.getMessage());
            e.printStackTrace();
        }
        
        // 安全地获取标签列表
        List<Integer> tagIds = new ArrayList<>();
        try {
            List<Integer> tags = postTagMapper.selectTagIdsByPostId(postId);
            if (tags != null) {
                tagIds = tags;
            }
        } catch (Exception e) {
            System.err.println("获取帖子标签失败, postId=" + postId + ", error=" + e.getMessage());
            e.printStackTrace();
        }
        
        // 安全地获取点赞数
        int likeCount = 0;
        try {
            likeCount = likeMapper.countByPostId(postId);
        } catch (Exception e) {
            System.err.println("获取帖子点赞数失败, postId=" + postId + ", error=" + e.getMessage());
            e.printStackTrace();
        }
        
        // 安全地获取评论数
        int commentCount = 0;
        try {
            commentCount = commentMapper.countByPostId(postId);
        } catch (Exception e) {
            System.err.println("获取帖子评论数失败, postId=" + postId + ", error=" + e.getMessage());
            e.printStackTrace();
        }
        
        // 安全地获取收藏数
        int favoriteCount = 0;
        try {
            favoriteCount = favoriteMapper.countByPostId(postId);
        } catch (Exception e) {
            System.err.println("获取帖子收藏数失败, postId=" + postId + ", error=" + e.getMessage());
            e.printStackTrace();
        }
        
        map.put("images", imageUrls);
        map.put("tags", tagIds);
        map.put("likeCount", likeCount);
        map.put("commentCount", commentCount);
        map.put("favoriteCount", favoriteCount);
        return map;
    }

    /**
     * 创建帖子（带图片和标签）
     * 图片以Base64编码字符串形式存储到数据库
     * 
     * @param userId 用户ID
     * @param content 帖子内容
     * @param topic 帖子主题
     * @param images Base64编码的图片字符串列表
     * @param tagIds 标签ID列表
     * @return 创建结果
     */
    @Override
    public boolean createPostWithMediaAndTags(Integer userId, String content, String topic, List<String> images, List<Integer> tagIds) {
        Post post = new Post();
        post.setUserId(userId);
        post.setContent(content);
        post.setTopic(topic);
        post.setCreatedAt(LocalDateTime.now());
        if (postMapper.insert(post) <= 0) {
            return false;
        }
        Integer postId = post.getPostId();
        // 存储Base64编码的图片字符串到数据库
        if (images != null && !images.isEmpty()) {
            postImageMapper.insertBatch(postId, images);
        }
        if (tagIds != null && !tagIds.isEmpty()) {
            postTagMapper.insertBatch(postId, tagIds);
        }
        return true;
    }
}
