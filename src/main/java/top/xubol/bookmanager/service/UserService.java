package top.xubol.bookmanager.service;

import top.xubol.bookmanager.common.Result;
import top.xubol.bookmanager.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

public interface UserService extends IService<User> {
    
    Result<String> login(String username, String password);

    Result<User> register(User user);
    
    Result<User> getUserInfo(String username);
    
    Result<String> updateUserInfo(User user);

    Result<String> updateUserStatus(Long userId, Integer status);

    Result<?> getUserList(Integer page, Integer size, String username);
}
