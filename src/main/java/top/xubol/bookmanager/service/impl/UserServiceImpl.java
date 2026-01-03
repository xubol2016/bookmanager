package top.xubol.bookmanager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import top.xubol.bookmanager.common.Result;
import top.xubol.bookmanager.entity.User;
import top.xubol.bookmanager.mapper.UserMapper;
import top.xubol.bookmanager.service.UserService;
import top.xubol.bookmanager.util.JwtUtils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public Result<String> login(String username, String password) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        User user = baseMapper.selectOne(queryWrapper);

        if (user == null) {
            return Result.failed("用户名或密码错误");
        }

        if (user.getStatus() == 0) {
            return Result.failed("账号已被禁用");
        }

        // 简单使用MD5加密验证 (实际项目中应使用BCrypt)
        // 假设DB中已经是加密后的，或者我们现在约定存的是MD5
        // 为了兼容现有数据（SQL里是123456），我们先尝试明文比对，如果不匹配再尝试MD5
        // 但通常我们应该强制一种。考虑到SQL文件注释写了 'Note: In a real app this should be BCrypt'
        // 我们这里暂且直接比对，或者做一下MD5。
        // 为了演示注册加密，我们这里约定：输入密码 -> MD5 -> 存/比对
        /*
         * 注意：SQL数据当前是明文 '123456'。若代码逻辑改为MD5，则原数据将无法登录。
         * 这是一个常见问题。我将使用明文比对来兼容现有SQL数据，
         * 但在注册时，我们可以选择加密。
         * 
         * 修正：需求明确要求"对密码进行加密存储"。
         * 因此，我应该实现加密。对于SQL里的旧数据，如果它们是明文，那么它们将无法在加密逻辑下登录。
         * 为了方便用户测试，我会在代码里暂时保留明文比对的逻辑分支，或者我默认SQL里的数据也是需要被视为"已加密"（虽然它们看起来像明文）。
         * 
         * 最好是：注册时加密。登录时：hash(input) == db_val。
         * 既然SQL给的是 '123456'，那我就当它是加密后的值（虽然它显然不是强哈希）。
         * 或者，我只对新注册用户加密。
         * 
         * 决策：使用 MD5 DigestUtils。为了让管理员 '123456' 能登录，
         * 如果 MD5('123456') != '123456' (显而易见)，那管理员就登不进去。
         * 
         * 妥协方案：先检查明文，如果明文匹配则允许（兼容旧数据）；如果不匹配，再检查MD5。
         */
        
        if (!user.getPassword().equals(password)) {
             // 再次尝试 MD5
             String md5Password = DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8));
             if (!user.getPassword().equals(md5Password)) {
                 return Result.failed("用户名或密码错误");
             }
        }

        // 生成token
        String token = jwtUtils.generateToken(username, user.getRole());
        return Result.success(token, "登录成功");
    }

    @Override
    public Result<User> register(User user) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", user.getUsername());
        if (baseMapper.selectCount(queryWrapper) > 0) {
            return Result.failed("用户名已存在");
        }

        // 密码加密
        String md5Password = DigestUtils.md5DigestAsHex(user.getPassword().getBytes(StandardCharsets.UTF_8));
        user.setPassword(md5Password);
        
        user.setRole(0); // 默认普通用户
        user.setStatus(1); // 默认正常
        user.setCreatedTime(LocalDateTime.now());
        
        save(user);
        user.setPassword(null); // 不返回密码
        return Result.success(user, "注册成功");
    }

    @Override
    public Result<User> getUserInfo(String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        User user = baseMapper.selectOne(queryWrapper);
        if (user != null) {
            user.setPassword(null);
            return Result.success(user);
        }
        return Result.failed("用户不存在");
    }

    @Override
    public Result<String> updateUserInfo(User user) {
        // 只能修改 真实姓名, 电话, 密码
        User existingUser = getById(user.getId());
        if (existingUser == null) {
            return Result.failed("用户不存在");
        }
        
        if (user.getRealName() != null) existingUser.setRealName(user.getRealName());
        if (user.getPhone() != null) existingUser.setPhone(user.getPhone());
        
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
             String md5Password = DigestUtils.md5DigestAsHex(user.getPassword().getBytes(StandardCharsets.UTF_8));
             existingUser.setPassword(md5Password);
        }
        
        updateById(existingUser);
        return Result.success("更新成功");
    }

    @Override
    public Result<String> updateUserStatus(Long userId, Integer status) {
        User user = getById(userId);
        if (user == null) {
            return Result.failed("用户不存在");
        }
        user.setStatus(status);
        updateById(user);
        return Result.success("状态更新成功");
    }

    @Override
    public Result<?> getUserList(Integer page, Integer size, String username) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<User> userPage = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page, size);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (username != null && !username.isEmpty()) {
            queryWrapper.like("username", username);
        }
        queryWrapper.orderByDesc("created_time");
        
        com.baomidou.mybatisplus.core.metadata.IPage<User> res = baseMapper.selectPage(userPage, queryWrapper);
        // Hide passwords
        res.getRecords().forEach(u -> u.setPassword(null));
        return Result.success(res);
    }
}
