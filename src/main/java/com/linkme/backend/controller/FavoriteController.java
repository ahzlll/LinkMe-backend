package com.linkme.backend.controller;

import com.linkme.backend.common.JwtUtil;
import com.linkme.backend.common.R;
import com.linkme.backend.entity.Favorite;
import com.linkme.backend.entity.FavoriteFolder;
import com.linkme.backend.service.FavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 收藏控制器
 * 
 * 功能描述：
 * - 处理收藏相关的HTTP请求
 * - 包括收藏夹管理、收藏帖子等功能
 * 
 * @author Ahz
 * @version 1.0
 */
@RestController
@RequestMapping("/favorite_folders")
@Tag(name = "收藏管理", description = "收藏夹和收藏相关的API接口")
public class FavoriteController {
    
    @Autowired
    private FavoriteService favoriteService;
    
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
     * 获取收藏夹列表
     * 
     * @param request HTTP请求
     * @return 收藏夹列表
     */
    @GetMapping
    @Operation(summary = "获取收藏夹列表", description = "获取当前用户的所有收藏夹", 
               security = @SecurityRequirement(name = "bearerAuth"))
    public R<List<FavoriteFolder>> getFavoriteFolders(HttpServletRequest request) {
        Integer userId = getCurrentUserId(request);
        if (userId == null) {
            return R.fail(401, "未授权，请先登录");
        }
        
        List<FavoriteFolder> folders = favoriteService.getFavoriteFolders(userId);
        return R.ok(folders);
    }
    
    /**
     * 创建收藏夹
     * 
     * @param requestBody 请求体（包含 user_id 和 folder_name）
     * @return 创建结果
     */
    @PostMapping
    @Operation(summary = "创建收藏夹", description = "创建新的收藏夹", 
               security = @SecurityRequirement(name = "bearerAuth"))
    public R<FavoriteFolder> createFavoriteFolder(@RequestBody Map<String, Object> requestBody) {
        Integer userId = null;
        String folderName = null;
        
        // 处理 user_id（可能是 Integer 或 String）
        Object userIdObj = requestBody.get("user_id");
        if (userIdObj instanceof Integer) {
            userId = (Integer) userIdObj;
        } else if (userIdObj instanceof String) {
            try {
                userId = Integer.parseInt((String) userIdObj);
            } catch (NumberFormatException e) {
                return R.fail(400, "user_id 格式错误");
            }
        } else if (userIdObj instanceof Number) {
            userId = ((Number) userIdObj).intValue();
        }
        
        // 处理 folder_name
        Object folderNameObj = requestBody.get("folder_name");
        if (folderNameObj instanceof String) {
            folderName = (String) folderNameObj;
        }
        
        if (userId == null) {
            return R.fail(400, "user_id 不能为空");
        }
        if (folderName == null || folderName.trim().isEmpty()) {
            return R.fail(400, "folder_name 不能为空");
        }
        
        FavoriteFolder folder = favoriteService.createFavoriteFolder(userId, folderName.trim());
        if (folder != null) {
            return R.ok(folder);
        } else {
            return R.fail(400, "创建收藏夹失败，可能名称已存在");
        }
    }
    
    /**
     * 更新收藏夹信息
     * 
     * @param id 收藏夹ID
     * @param requestBody 请求体（包含 name）
     * @param httpRequest HTTP请求
     * @return 更新结果
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新收藏夹信息", description = "更新指定收藏夹的名称", 
               security = @SecurityRequirement(name = "bearerAuth"))
    public R<String> updateFavoriteFolder(@PathVariable @Parameter(description = "收藏夹ID") Integer id,
                                          @RequestBody Map<String, String> requestBody,
                                          HttpServletRequest httpRequest) {
        Integer userId = getCurrentUserId(httpRequest);
        if (userId == null) {
            return R.fail(401, "未授权，请先登录");
        }
        
        String name = requestBody.get("name");
        if (name == null || name.trim().isEmpty()) {
            return R.fail(400, "收藏夹名称不能为空");
        }
        
        boolean success = favoriteService.updateFavoriteFolder(userId, id, name.trim());
        if (success) {
            return R.ok("收藏夹更新成功");
        } else {
            return R.fail(400, "收藏夹更新失败，可能名称已存在或无权限");
        }
    }
    
    /**
     * 删除收藏夹
     * 
     * @param id 收藏夹ID
     * @param httpRequest HTTP请求
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除收藏夹", description = "删除指定的收藏夹", 
               security = @SecurityRequirement(name = "bearerAuth"))
    public R<String> deleteFavoriteFolder(@PathVariable @Parameter(description = "收藏夹ID") Integer id,
                                         HttpServletRequest httpRequest) {
        Integer userId = getCurrentUserId(httpRequest);
        if (userId == null) {
            return R.fail(401, "未授权，请先登录");
        }
        
        boolean success = favoriteService.deleteFavoriteFolder(userId, id);
        if (success) {
            return R.ok("收藏夹删除成功");
        } else {
            return R.fail(400, "收藏夹删除失败，可能不存在或无权限");
        }
    }
}

/**
 * 收藏帖子控制器
 * 
 * 功能描述：
 * - 处理收藏帖子相关的HTTP请求
 * 
 * @author Ahz
 * @version 1.0
 */
@RestController
@RequestMapping("/favorites")
@Tag(name = "收藏帖子", description = "收藏帖子相关的API接口")
class FavoritePostController {
    
    @Autowired
    private FavoriteService favoriteService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 收藏帖子到指定收藏夹
     * 
     * @param requestBody 请求体（包含 user_id, post_id, folder_id）
     * @return 收藏结果
     */
    @PostMapping
    @Operation(summary = "收藏帖子", description = "将帖子收藏到指定收藏夹", 
               security = @SecurityRequirement(name = "bearerAuth"))
    public R<String> favoritePost(@RequestBody Map<String, Object> requestBody) {
        Integer userId = null;
        Integer postId = null;
        Integer folderId = null;
        
        // 处理 user_id
        Object userIdObj = requestBody.get("user_id");
        if (userIdObj instanceof Integer) {
            userId = (Integer) userIdObj;
        } else if (userIdObj instanceof String) {
            try {
                userId = Integer.parseInt((String) userIdObj);
            } catch (NumberFormatException e) {
                return R.fail(400, "user_id 格式错误");
            }
        } else if (userIdObj instanceof Number) {
            userId = ((Number) userIdObj).intValue();
        }
        
        // 处理 post_id
        Object postIdObj = requestBody.get("post_id");
        if (postIdObj instanceof Integer) {
            postId = (Integer) postIdObj;
        } else if (postIdObj instanceof String) {
            try {
                postId = Integer.parseInt((String) postIdObj);
            } catch (NumberFormatException e) {
                return R.fail(400, "post_id 格式错误");
            }
        } else if (postIdObj instanceof Number) {
            postId = ((Number) postIdObj).intValue();
        }
        
        // 处理 folder_id（可选）
        Object folderIdObj = requestBody.get("folder_id");
        if (folderIdObj != null) {
            if (folderIdObj instanceof Integer) {
                folderId = (Integer) folderIdObj;
            } else if (folderIdObj instanceof String) {
                try {
                    folderId = Integer.parseInt((String) folderIdObj);
                } catch (NumberFormatException e) {
                    return R.fail(400, "folder_id 格式错误");
                }
            } else if (folderIdObj instanceof Number) {
                folderId = ((Number) folderIdObj).intValue();
            }
        }
        
        if (userId == null) {
            return R.fail(400, "user_id 不能为空");
        }
        if (postId == null) {
            return R.fail(400, "post_id 不能为空");
        }
        if (folderId == null) {
            return R.fail(400, "folder_id 不能为空");
        }
        
        boolean success = favoriteService.favoritePost(userId, postId, folderId);
        if (success) {
            return R.ok("收藏成功");
        } else {
            return R.fail(400, "收藏失败");
        }
    }
    
    /**
     * 取消收藏帖子
     * 
     * @param id 收藏ID
     * @param request HTTP请求
     * @return 取消收藏结果
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "取消收藏", description = "根据收藏ID取消收藏", 
               security = @SecurityRequirement(name = "bearerAuth"))
    public R<String> unfavoritePost(@PathVariable @Parameter(description = "收藏ID") Integer id,
                                    HttpServletRequest request) {
        // 从请求中获取用户ID（用于验证权限）
        Integer userId = null;
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            try {
                userId = jwtUtil.getUserIdFromToken(token);
            } catch (Exception e) {
                // 忽略
            }
        }
        
        if (userId == null) {
            return R.fail(401, "未授权，请先登录");
        }
        
        boolean success = favoriteService.unfavoriteById(userId, id);
        if (success) {
            return R.ok("取消收藏成功");
        } else {
            return R.fail(400, "取消收藏失败，可能不存在或无权限");
        }
    }
}

