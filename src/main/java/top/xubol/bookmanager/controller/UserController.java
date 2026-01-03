package top.xubol.bookmanager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.xubol.bookmanager.common.Result;
import top.xubol.bookmanager.dto.LoginDto;
import top.xubol.bookmanager.entity.User;
import top.xubol.bookmanager.service.UserService;
import top.xubol.bookmanager.util.JwtUtils;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/login")
    public Result<String> login(@RequestBody LoginDto loginDto) {
        try {
            if (loginDto.getUsername() == null || loginDto.getPassword() == null) {
                return Result.validateFailed("用户名或密码不能为空");
            }
            return userService.login(loginDto.getUsername(), loginDto.getPassword());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.failed("登录失败: " + e.getMessage()); // Expose error for debugging
        }
    }

    @PostMapping("/register")
    public Result<User> register(@RequestBody User user) {
        if (user.getUsername() == null || user.getPassword() == null) {
            return Result.validateFailed("用户名或密码不能为空");
        }
        return userService.register(user);
    }

    @GetMapping("/profile")
    public Result<User> getProfile(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            try {
                token = token.substring(7);
                String username = jwtUtils.extractUsername(token);
                return userService.getUserInfo(username);
            } catch (Exception e) {
                return Result.unauthorized(null);
            }
        }
        return Result.unauthorized(null);
    }

    @PutMapping("/profile/update")
    public Result<String> updateProfile(@RequestBody User user, HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            try {
                token = token.substring(7);
                String username = jwtUtils.extractUsername(token);
                // 确保只能修改自己的信息，或者通过Service层根据username查ID
                Result<User> currentUserResult = userService.getUserInfo(username);
                if (currentUserResult.getCode() == 200) {
                    user.setId(currentUserResult.getData().getId());
                    return userService.updateUserInfo(user);
                }
            } catch (Exception e) {
                 return Result.unauthorized(null);
            }
        }
        return Result.unauthorized(null);
    }
    
    // 管理员接口
    @GetMapping("/check-admin")
    @top.xubol.bookmanager.common.RequireRole("admin")
    public Result<String> checkAdmin() {
        return Result.success("Admin access confirmed");
    }

    @PutMapping("/admin/status")
    @top.xubol.bookmanager.common.RequireRole("admin")
    public Result<String> updateUserStatus(@RequestParam Long userId, @RequestParam Integer status) {
        return userService.updateUserStatus(userId, status);
    }

    @GetMapping("/admin/list")
    @top.xubol.bookmanager.common.RequireRole("admin")
    public Result<?> getUserList(@RequestParam(defaultValue = "1") Integer page, 
                                 @RequestParam(defaultValue = "10") Integer size,
                                 @RequestParam(required = false) String username) {
        return userService.getUserList(page, size, username);
    }
}
