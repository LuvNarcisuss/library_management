package com.example.library.service;

import com.example.library.entity.User;
import com.example.library.repository.UserRepository;
import com.example.library.service.impl.UserServiceImpl;
import com.example.library.utils.PasswordUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Date;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * 用户服务测试类
 * 测试UserService的各种功能方法
 */
public class UserServiceTest {
    
    /**
     * 模拟的用户数据访问接口
     */
    @Mock
    private UserRepository userRepository;
    
    /**
     * 被测试的用户服务实现类
     */
    @InjectMocks
    private UserServiceImpl userService;
    
    /**
     * 测试用的用户对象
     */
    private User user;
    
    /**
     * 测试前的准备工作
     * 初始化模拟对象和测试数据
     */
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        user = new User();
        user.setId(1L);
        user.setUsername("admin");
        user.setPassword(PasswordUtils.encode("admin123")); // 使用自定义PasswordUtils加密密码
        user.setEmail("admin@example.com");
        user.setPhone("13800138000");
        user.setRole("ADMIN");
        user.setStatus("ACTIVE");
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
    }
    
    /**
     * 测试根据用户名查询用户功能
     * 验证能够根据用户名成功查询到用户
     */
    @Test
    public void testFindByUsername() {
        when(userRepository.findByUsername("admin")).thenReturn(user);
        Optional<User> foundUser = userService.findByUsername("admin");
        assertTrue(foundUser.isPresent());
        assertEquals("admin", foundUser.get().getUsername());
        verify(userRepository, times(1)).findByUsername("admin");
    }
    
    /**
     * 测试登录成功的情况
     * 验证使用正确的用户名和密码能够成功登录
     */
    @Test
    public void testLoginSuccess() {
        when(userRepository.findByUsername("admin")).thenReturn(user);
        
        User loginUser = userService.login("admin", "admin123");
        assertNotNull(loginUser);
        assertEquals("admin", loginUser.getUsername());
        
        verify(userRepository, times(1)).findByUsername("admin");
    }
    
    /**
     * 测试使用错误用户名登录的情况
     * 验证使用不存在的用户名登录会抛出异常
     */
    @Test(expected = RuntimeException.class)
    public void testLoginWithWrongUsername() {
        when(userRepository.findByUsername("wronguser")).thenReturn(null);
        userService.login("wronguser", "admin123");
    }
    
    /**
     * 测试使用错误密码登录的情况
     * 验证使用错误的密码登录会抛出异常
     */
    @Test(expected = RuntimeException.class)
    public void testLoginWithWrongPassword() {
        when(userRepository.findByUsername("admin")).thenReturn(user);
        userService.login("admin", "wrongpassword");
    }
    
    /**
     * 测试使用禁用用户登录的情况
     * 验证使用禁用状态的用户登录会抛出异常
     */
    @Test(expected = RuntimeException.class)
    public void testLoginWithDisabledUser() {
        user.setStatus("INACTIVE");
        when(userRepository.findByUsername("admin")).thenReturn(user);
        userService.login("admin", "admin123");
    }
    
    /**
     * 测试保存用户功能
     * 验证用户信息能够成功保存到数据库
     */
    @Test
    public void testSaveUser() {
        User newUser = new User();
        newUser.setUsername("testuser");
        newUser.setPassword("testpassword");
        newUser.setEmail("test@example.com");
        newUser.setPhone("13800138001");
        newUser.setRole("USER");
        
        String encodedPassword = PasswordUtils.encode("testpassword");
        newUser.setPassword(encodedPassword);
        when(userRepository.save(newUser)).thenReturn(newUser);
        
        User savedUser = userService.saveUser(newUser);
        assertNotNull(savedUser);
        assertEquals("testuser", savedUser.getUsername());
        assertEquals("ACTIVE", savedUser.getStatus());
        
        verify(userRepository, times(1)).save(newUser);
    }
    
    /**
     * 测试根据ID获取用户功能
     * 验证能够根据ID成功查询到用户
     */
    @Test
    public void testGetUserById() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Optional<User> foundUser = userService.getUserById(1L);
        assertTrue(foundUser.isPresent());
        assertEquals("admin", foundUser.get().getUsername());
        
        verify(userRepository, times(1)).findById(1L);
    }
}