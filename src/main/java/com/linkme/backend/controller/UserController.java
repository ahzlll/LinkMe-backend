package com.linkme.backend.controller;

import com.linkme.backend.common.JwtUtil;
import com.linkme.backend.common.R;
import com.linkme.backend.controller.dto.LoginRequest;
import com.linkme.backend.entity.User;
import com.linkme.backend.service.UserService;
import com.linkme.backend.service.VerificationCodeService;
import com.linkme.backend.util.PasswordValidator;
import com.linkme.backend.util.EmailValidator;
import com.linkme.backend.util.PhoneValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
 * @author Ahz
 * @version 1.1
 */
@RestController
@RequestMapping("/user")
@Tag(name = "用户管理", description = "用户相关的API接口")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private VerificationCodeService verificationCodeService;
    
    /**
     * 移除用户信息中的敏感数据（密码哈希）
     * 
     * @param user 原始用户对象
     * @return 清理后的用户对象
     */
    private User sanitizeUser(User user) {
        if (user == null) {
            return null;
        }
        User userInfo = new User();
        userInfo.setUserId(user.getUserId());
        userInfo.setUsername(user.getUsername());
        userInfo.setEmail(user.getEmail());
        userInfo.setPhone(user.getPhone());
        userInfo.setNickname(user.getNickname());
        userInfo.setGender(user.getGender());
        userInfo.setBirthday(user.getBirthday());
        userInfo.setRegion(user.getRegion());
        userInfo.setAvatarUrl(user.getAvatarUrl());
        userInfo.setBio(user.getBio());
        userInfo.setCreatedAt(user.getCreatedAt());
        // 不包含passwordHash
        return userInfo;
    }
    
    /**
     * 获取用户信息
     * 
     * @param userId 用户ID
     * @return 用户信息（不包含敏感数据）
     */
    @GetMapping("/{userId}/info")
    @Operation(summary = "获取用户信息", description = "根据用户ID获取用户详细信息", 
               security = @SecurityRequirement(name = "bearerAuth"))
    public R<User> getUserInfo(@PathVariable @Parameter(description = "用户ID") Integer userId) {
        User user = userService.getUserById(userId);
        if (user != null) {
            return R.ok(sanitizeUser(user));
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
    @Operation(summary = "更新用户信息", description = "更新用户的基本信息", 
               security = @SecurityRequirement(name = "bearerAuth"))
    public R<String> updateUserInfo(@PathVariable @Parameter(description = "用户ID") Integer userId,
                                   @RequestBody User user) {
        user.setUserId(userId);
        boolean success = userService.updateUser(user);
        if (success) {
            return R.ok("用户信息更新成功");
        } else {
            return R.fail(400, "用户信息更新失败");
        }
    }
    
    /**
     * 用户注册
     * 
     * @param user 用户信息
     * @return 注册结果（注册成功后返回用户信息和token）
     */
    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "新用户注册，注册成功后自动登录返回token")
    public R<Map<String, Object>> register(@RequestBody User user) {
        // 参数验证
        if (user.getPasswordHash() == null || user.getPasswordHash().trim().isEmpty()) {
            return R.fail(400, "密码不能为空");
        }
        if (user.getEmail() == null && user.getPhone() == null) {
            return R.fail(400, "邮箱或手机号至少填写一个");
        }
        
        // 邮箱格式验证
        if (user.getEmail() != null && !user.getEmail().trim().isEmpty()) {
            String emailError = EmailValidator.getErrorMessage(user.getEmail());
            if (emailError != null) {
                return R.fail(400, emailError);
            }
        }
        
        // 手机号格式验证
        if (user.getPhone() != null && !user.getPhone().trim().isEmpty()) {
            String phoneError = PhoneValidator.getErrorMessage(user.getPhone());
            if (phoneError != null) {
                return R.fail(400, phoneError);
            }
        }
        
        // 密码验证
        String passwordError = PasswordValidator.getErrorMessage(user.getPasswordHash());
        if (passwordError != null) {
            return R.fail(400, passwordError);
        }
        
        // 检查用户名是否已存在
        if (user.getUsername() != null && !user.getUsername().trim().isEmpty()) {
            User existingUser = userService.getUserByUsername(user.getUsername());
            if (existingUser != null) {
                return R.fail(400, "注册失败，用户名已存在");
            }
        }
        
        // 检查邮箱是否已存在
        if (user.getEmail() != null && !user.getEmail().trim().isEmpty()) {
            User existingUser = userService.getUserByEmail(user.getEmail());
            if (existingUser != null) {
                return R.fail(400, "注册失败，邮箱已存在");
            }
        }
        
        // 检查手机号是否已存在
        if (user.getPhone() != null && !user.getPhone().trim().isEmpty()) {
            User existingUser = userService.getUserByPhone(user.getPhone());
            if (existingUser != null) {
                return R.fail(400, "注册失败，手机号已存在");
            }
        }
        
        // 验证 username 是否为空（因为数据库要求 NOT NULL）
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            return R.fail(400, "注册失败，用户名不能为空");
        }
        
        boolean success = userService.register(user);
        if (success) {
            // 注册成功后，重新查询用户信息（获取生成的userId）
            User registeredUser = null;
            if (user.getEmail() != null) {
                registeredUser = userService.getUserByEmail(user.getEmail());
            } else if (user.getPhone() != null) {
                registeredUser = userService.getUserByPhone(user.getPhone());
            }
            
            if (registeredUser != null) {
                // 生成JWT token（如果username为空，使用email或phone作为替代）
                String usernameForToken = registeredUser.getUsername();
                if (usernameForToken == null || usernameForToken.trim().isEmpty()) {
                    usernameForToken = registeredUser.getEmail() != null ? registeredUser.getEmail() : registeredUser.getPhone();
                }
                String token = jwtUtil.generateToken(registeredUser.getUserId(), usernameForToken);
                
                Map<String, Object> result = new HashMap<>();
                result.put("user", sanitizeUser(registeredUser));
                result.put("token", token);
                return R.ok(result);
            } else {
                return R.ok("注册成功");
            }
        } else {
            // 如果到这里，说明插入数据库时失败了
            // 可能是数据库约束错误或其他异常
            // 检查控制台日志以获取详细错误信息
            return R.fail(400, "注册失败，可能是数据库插入错误。请检查控制台日志获取详细信息。如果问题持续，请联系管理员。");
        }
    }
    
    /**
     * 用户登录
     * 
     * @param loginRequest 登录请求
     * @return 登录结果（包含用户信息和token）
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户登录验证，支持邮箱、手机号或用户名登录。\n" +
               "登录名格式：\n" +
               "- 邮箱：user@example.com\n" +
               "- 手机号：13800138000\n" +
               "- 用户名：testuser")
    public R<Map<String, Object>> login(@RequestBody LoginRequest loginRequest) {
        String loginName = loginRequest.getLoginName();
        String password = loginRequest.getPassword();
        
        // 参数验证
        if (loginName == null || loginName.trim().isEmpty()) {
            return R.fail(400, "登录名不能为空");
        }
        if (password == null || password.trim().isEmpty()) {
            return R.fail(400, "密码不能为空");
        }
        
        User user = userService.login(loginName, password);
        if (user != null) {
            // 生成JWT token（如果username为空，使用email或phone作为替代）
            String usernameForToken = user.getUsername();
            if (usernameForToken == null || usernameForToken.trim().isEmpty()) {
                usernameForToken = user.getEmail() != null ? user.getEmail() : user.getPhone();
            }
            String token = jwtUtil.generateToken(user.getUserId(), usernameForToken);
            
            Map<String, Object> result = new HashMap<>();
            result.put("user", sanitizeUser(user));
            result.put("token", token);
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
    @Operation(summary = "获取用户列表", description = "分页获取用户列表", 
               security = @SecurityRequirement(name = "bearerAuth"))
    public R<List<User>> getUserList(@RequestParam(defaultValue = "1") Integer page,
                                    @RequestParam(defaultValue = "10") Integer size) {
        List<User> users = userService.getUserList(page, size);
        return R.ok(users);
    }
    
    /**
     * 修改密码
     * 
     * @param userId 用户ID
     * @param request 密码修改请求（包含 oldPassword 和 newPassword）
     * @return 修改结果
     */
    @PutMapping("/{userId}/password")
    @Operation(summary = "修改密码", description = "用户修改密码，需要提供旧密码", 
               security = @SecurityRequirement(name = "bearerAuth"))
    public R<String> changePassword(@PathVariable @Parameter(description = "用户ID") Integer userId,
                                   @RequestBody Map<String, String> request) {
        String oldPassword = request.get("oldPassword");
        String newPassword = request.get("newPassword");
        
        // 参数验证
        if (oldPassword == null || oldPassword.trim().isEmpty()) {
            return R.fail(400, "旧密码不能为空");
        }
        if (newPassword == null || newPassword.trim().isEmpty()) {
            return R.fail(400, "新密码不能为空");
        }
        
        // 密码验证
        String passwordError = PasswordValidator.getErrorMessage(newPassword);
        if (passwordError != null) {
            return R.fail(400, passwordError);
        }
        
        boolean success = userService.changePassword(userId, oldPassword, newPassword);
        if (success) {
            return R.ok("密码修改成功");
        } else {
            return R.fail(400, "密码修改失败，请检查旧密码是否正确");
        }
    }
    
    /**
     * 发送重置密码验证码
     * 
     * @param request 请求（包含 email 或 phone）
     * @return 发送结果
     */
    @PostMapping("/forgot-password/send-code")
    @Operation(summary = "发送重置密码验证码", description = "向邮箱或手机号发送重置密码验证码")
    public R<String> sendResetPasswordCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String phone = request.get("phone");
        
        // 参数验证
        if ((email == null || email.trim().isEmpty()) && (phone == null || phone.trim().isEmpty())) {
            return R.fail(400, "邮箱或手机号至少填写一个");
        }
        
        // 邮箱格式验证
        if (email != null && !email.trim().isEmpty()) {
            String emailError = EmailValidator.getErrorMessage(email);
            if (emailError != null) {
                return R.fail(400, emailError);
            }
        }
        
        // 手机号格式验证
        if (phone != null && !phone.trim().isEmpty()) {
            String phoneError = PhoneValidator.getErrorMessage(phone);
            if (phoneError != null) {
                return R.fail(400, phoneError);
            }
        }
        
        boolean success = verificationCodeService.sendVerificationCode(email, phone, "reset_password");
        if (success) {
            return R.ok("验证码已发送");
        } else {
            return R.fail(400, "验证码发送失败，请检查邮箱或手机号是否正确");
        }
    }
    
    /**
     * 重置密码（忘记密码时使用）
     * 
     * @param request 重置密码请求（包含 email/phone、code、newPassword）
     * @return 重置结果
     */
    @PostMapping("/forgot-password/reset")
    @Operation(summary = "重置密码", description = "通过验证码重置密码，用于忘记密码场景")
    public R<String> resetPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String phone = request.get("phone");
        String code = request.get("code");
        String newPassword = request.get("newPassword");
        
        // 参数验证
        if ((email == null || email.trim().isEmpty()) && (phone == null || phone.trim().isEmpty())) {
            return R.fail(400, "邮箱或手机号至少填写一个");
        }
        if (code == null || code.trim().isEmpty()) {
            return R.fail(400, "验证码不能为空");
        }
        if (newPassword == null || newPassword.trim().isEmpty()) {
            return R.fail(400, "新密码不能为空");
        }
        
        // 邮箱格式验证
        if (email != null && !email.trim().isEmpty()) {
            String emailError = EmailValidator.getErrorMessage(email);
            if (emailError != null) {
                return R.fail(400, emailError);
            }
        }
        
        // 手机号格式验证
        if (phone != null && !phone.trim().isEmpty()) {
            String phoneError = PhoneValidator.getErrorMessage(phone);
            if (phoneError != null) {
                return R.fail(400, phoneError);
            }
        }
        
        // 密码验证
        String passwordError = PasswordValidator.getErrorMessage(newPassword);
        if (passwordError != null) {
            return R.fail(400, passwordError);
        }
        
        // 确定类型
        String type = email != null && !email.trim().isEmpty() ? "email" : "phone";
        
        // 验证验证码
        boolean codeValid = verificationCodeService.verifyCode(code, email, phone, type, "reset_password");
        if (!codeValid) {
            return R.fail(400, "验证码错误或已过期");
        }
        
        // 查找用户
        User user = null;
        if (type.equals("email")) {
            user = userService.getUserByEmail(email);
        } else {
            user = userService.getUserByPhone(phone);
        }
        
        if (user == null) {
            return R.fail(404, "用户不存在");
        }
        
        // 重置密码
        boolean success = userService.resetPassword(user.getUserId(), newPassword);
        if (success) {
            return R.ok("密码重置成功");
        } else {
            return R.fail(400, "密码重置失败");
        }
    }
    
    /**
     * 删除用户
     * 
     * @param userId 用户ID
     * @return 删除结果
     */
    @DeleteMapping("/{userId}")
    @Operation(summary = "删除用户", description = "根据用户ID删除用户", 
               security = @SecurityRequirement(name = "bearerAuth"))
    public R<String> deleteUser(@PathVariable @Parameter(description = "用户ID") Integer userId) {
        boolean success = userService.deleteUser(userId);
        if (success) {
            return R.ok("用户删除成功");
        } else {
            return R.fail(400, "用户删除失败，用户可能不存在");
        }
    }
}
