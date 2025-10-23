package com.linkme.backend.controller;

import com.linkme.backend.common.R;
import com.linkme.backend.entity.User;
import com.linkme.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户控制器
 * 
 * 功能描述：
 * - 处理用户相关的HTTP请求
 * - 包括用户注册、登录、信息管理等功能
 * 
 * @author Ahz, riki
 * @version 1.0
 */
@RestController
@RequestMapping("/user")
@Tag(name = "用户管理", description = "用户相关的API接口")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    /**
     * 获取用户信息
     * 
     * @param userId 用户ID
     * @return 用户信息
     */
    @GetMapping("/{userId}/info")
    @Operation(summary = "获取用户信息", description = "根据用户ID获取用户详细信息")
    public R<User> getUserInfo(@PathVariable @Parameter(description = "用户ID") Integer userId) {
        User user = userService.getUserById(userId);
        if (user != null) {
            return R.ok(user);
        } else {
            return R.fail(404, "用户不存在");
        }
    }
    
    /**
     * 更新用户信息
     * 
     * @param userId 用户ID
     * @param user 用户信息
     * @return 更新结果
     */
    @PutMapping("/{userId}/info")
    @Operation(summary = "更新用户信息", description = "更新用户的基本信息")
    public R<String> updateUserInfo(@PathVariable @Parameter(description = "用户ID") Integer userId,
                                   @RequestBody User user) {
        user.setUserId(userId);
        boolean success = userService.updateUser(user);
        if (success) {
            return R.ok("用户信息更新成功");
        } else {
            return R.fail("用户信息更新失败");
        }
    }
    
    /**
     * 用户注册
     * 
     * @param user 用户信息
     * @return 注册结果
     */
    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "新用户注册")
    public R<String> register(@RequestBody User user) {
        boolean success = userService.register(user);
        if (success) {
            return R.ok("注册成功");
        } else {
            return R.fail("注册失败，邮箱、手机号或用户名已存在");
        }
    }
    
    /**
     * 用户登录
     * 
     * @param loginRequest 登录请求
     * @return 登录结果
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户登录验证")
    public R<Map<String, Object>> login(@RequestBody Map<String, String> loginRequest) {
        String loginName = loginRequest.get("loginName");
        String password = loginRequest.get("password");
        
        User user = userService.login(loginName, password);
        if (user != null) {
            Map<String, Object> result = new HashMap<>();
            result.put("user", user);
            result.put("token", "jwt_token_here"); // 这里应该生成JWT token
            return R.ok(result);
        } else {
            return R.fail(401, "用户名或密码错误");
        }
    }
    
    /**
     * 获取用户列表
     * 
     * @param page 页码
     * @param size 每页数量
     * @return 用户列表
     */
    @GetMapping("/list")
    @Operation(summary = "获取用户列表", description = "分页获取用户列表")
    public R<List<User>> getUserList(@RequestParam(defaultValue = "1") Integer page,
                                    @RequestParam(defaultValue = "10") Integer size) {
        List<User> users = userService.getUserList(page, size);
        return R.ok(users);
    }
    
    /**
     * 删除用户
     * 
     * @param userId 用户ID
     * @return 删除结果
     */
    @DeleteMapping("/{userId}")
    @Operation(summary = "删除用户", description = "根据用户ID删除用户")
    public R<String> deleteUser(@PathVariable @Parameter(description = "用户ID") Integer userId) {
        boolean success = userService.deleteUser(userId);
        if (success) {
            return R.ok("用户删除成功");
        } else {
            return R.fail("用户删除失败");
        }
    }
}
