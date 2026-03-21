package com.example.library.service.impl;

import com.example.library.entity.User;
import com.example.library.repository.UserRepository;
import com.example.library.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserRepository userRepository;

    @Override
    public Optional<User> findByUsername(String username) {
        return Optional.ofNullable(userRepository.findByUsername(username));
    }

    @Override
    public User login(String username, String password) {
        Optional<User> userOptional = findByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // 验证密码（使用明文）
            if (password.equals(user.getPassword())) {
                // 验证用户状态
                if ("ACTIVE".equals(user.getStatus())) {
                    return user;
                } else {
                    throw new RuntimeException("用户账号已被禁用");
                }
            } else {
                throw new RuntimeException("用户名或密码错误");
            }
        } else {
            throw new RuntimeException("用户名或密码错误");
        }
    }

    @Override
    public User saveUser(User user) {
        // 默认状态为ACTIVE
        if (user.getStatus() == null) {
            user.setStatus("ACTIVE");
        }
        // 使用明文密码
        // 不需要加密处理
        return userRepository.save(user);
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User updateUser(User user) {
        Optional<User> existingUser = userRepository.findById(user.getId());
        if (existingUser.isPresent()) {
            // 如果密码不为空，则更新密码（使用明文）
            if (user.getPassword() != null && !user.getPassword().equals(existingUser.get().getPassword())) {
                // 使用明文密码，不需要加密
            } else {
                // 如果密码为空，则使用原密码
                user.setPassword(existingUser.get().getPassword());
            }
            return userRepository.save(user);
        }
        return null;
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public void initAdminUser() {
        Optional<User> adminUser = findByUsername("admin");
        if (!adminUser.isPresent()) {
            User user = new User();
            user.setUsername("admin");
            user.setPassword("admin123");
            user.setEmail("admin@example.com");
            user.setPhone("13800138000");
            user.setRole("ADMIN");
            user.setStatus("ACTIVE");
            userRepository.save(user);
        }
    }
}