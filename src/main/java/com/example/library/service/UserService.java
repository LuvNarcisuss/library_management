package com.example.library.service;

import com.example.library.entity.User;
import java.util.Optional;

public interface UserService {
    // 根据用户名查询用户
    Optional<User> findByUsername(String username);
    
    // 用户登录验证
    User login(String username, String password);
    
    // 保存用户
    User saveUser(User user);
    
    // 根据ID查询用户
    Optional<User> getUserById(Long id);
    
    // 查询所有用户
    Iterable<User> getAllUsers();
    
    // 更新用户信息
    User updateUser(User user);
    
    // 删除用户
    void deleteUser(Long id);
    
    // 初始化管理员用户
    void initAdminUser();
}