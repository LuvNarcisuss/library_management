package com.example.library.utils;

public class PasswordUtils {
    
    /**
     * 直接返回明文密码
     * @param password 原始密码
     * @return 明文密码
     */
    public static String encode(String password) {
        return password;
    }
    
    /**
     * 直接比较明文密码
     * @param rawPassword 原始密码
     * @param encodedPassword 数据库中的密码
     * @return 是否匹配
     */
    public static boolean matches(String rawPassword, String encodedPassword) {
        return rawPassword.equals(encodedPassword);
    }
}