package com.linkme.backend.service;

import com.linkme.backend.entity.Post;

import java.util.List;

public interface PostRecommendService {
    List<Post> getRecommendedPosts(Integer userId, Integer limit);
}
