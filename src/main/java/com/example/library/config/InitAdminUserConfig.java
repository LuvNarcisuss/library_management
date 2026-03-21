package com.example.library.config;

import com.example.library.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InitAdminUserConfig {

    @Autowired
    private UserService userService;

    @Bean
    public ApplicationRunner initAdminUser() {
        return args -> {
            try {
                userService.initAdminUser();
                System.out.println("管理员用户初始化完成");
            } catch (Exception e) {
                System.out.println("管理员用户初始化失败：" + e.getMessage());
            }
        };
    }
}
