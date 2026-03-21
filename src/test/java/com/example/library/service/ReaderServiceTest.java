package com.example.library.service;

import com.example.library.entity.Reader;
import com.example.library.repository.ReaderRepository;
import com.example.library.service.impl.ReaderServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * 读者服务测试类
 * 测试ReaderService的各种功能方法
 */
public class ReaderServiceTest {
    /**
     * 模拟的读者数据访问接口
     */
    @Mock
    private ReaderRepository readerRepository;

    /**
     * 被测试的读者服务实现类
     */
    @InjectMocks
    private ReaderServiceImpl readerService;

    /**
     * 测试用的读者对象
     */
    private Reader reader;

    /**
     * 测试前的准备工作
     * 初始化模拟对象和测试数据
     */
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        reader = new Reader();
        reader.setId(1L);
        reader.setName("测试读者");
        reader.setIdCard("110101199001011234");
        reader.setPhone("13800138000");
        reader.setEmail("test@example.com");
        reader.setAddress("测试地址");
        reader.setReaderType("STUDENT");
        reader.setStatus("ACTIVE");
        reader.setCreateTime(new Date());
        reader.setUpdateTime(new Date());
    }

    /**
     * 测试保存读者功能
     * 验证读者信息能够成功保存到数据库
     */
    @Test
    public void testSaveReader() {
        when(readerRepository.save(reader)).thenReturn(reader);
        Reader savedReader = readerService.saveReader(reader);
        assertNotNull(savedReader);
        assertEquals("测试读者", savedReader.getName());
        verify(readerRepository, times(1)).save(reader);
    }

    /**
     * 测试根据ID获取读者功能
     * 验证能够根据ID成功查询到读者
     */
    @Test
    public void testGetReaderById() {
        when(readerRepository.findById(1L)).thenReturn(Optional.of(reader));
        Optional<Reader> foundReader = readerService.getReaderById(1L);
        assertTrue(foundReader.isPresent());
        assertEquals("测试读者", foundReader.get().getName());
        verify(readerRepository, times(1)).findById(1L);
    }

    /**
     * 测试获取所有读者功能
     * 验证能够成功查询到所有读者
     */
    @Test
    public void testGetAllReaders() {
        List<Reader> readers = new ArrayList<>();
        readers.add(reader);
        when(readerRepository.findAll()).thenReturn(readers);
        List<Reader> foundReaders = readerService.getAllReaders();
        assertNotNull(foundReaders);
        assertEquals(1, foundReaders.size());
        verify(readerRepository, times(1)).findAll();
    }

    /**
     * 测试更新读者功能
     * 验证读者信息能够成功更新
     */
    @Test
    public void testUpdateReader() {
        when(readerRepository.findById(1L)).thenReturn(Optional.of(reader));
        when(readerRepository.save(reader)).thenReturn(reader);
        reader.setName("更新后的读者名");
        Reader updatedReader = readerService.updateReader(reader);
        assertNotNull(updatedReader);
        assertEquals("更新后的读者名", updatedReader.getName());
        verify(readerRepository, times(1)).findById(1L);
        verify(readerRepository, times(1)).save(reader);
    }

    /**
     * 测试删除读者功能
     * 验证能够成功删除读者
     */
    @Test
    public void testDeleteReader() {
        doNothing().when(readerRepository).deleteById(1L);
        readerService.deleteReader(1L);
        verify(readerRepository, times(1)).deleteById(1L);
    }

    /**
     * 测试根据姓名查询读者功能
     * 验证能够根据姓名成功查询到读者
     */
    @Test
    public void testFindReadersByName() {
        List<Reader> readers = new ArrayList<>();
        readers.add(reader);
        when(readerRepository.findByNameContaining("测试")).thenReturn(readers);
        List<Reader> foundReaders = readerService.findReadersByName("测试");
        assertNotNull(foundReaders);
        assertEquals(1, foundReaders.size());
        verify(readerRepository, times(1)).findByNameContaining("测试");
    }

    /**
     * 测试根据身份证号查询读者功能
     * 验证能够根据身份证号成功查询到读者
     */
    @Test
    public void testFindReaderByIdCard() {
        when(readerRepository.findByIdCard("110101199001011234")).thenReturn(reader);
        Optional<Reader> foundReader = readerService.findReaderByIdCard("110101199001011234");
        assertTrue(foundReader.isPresent());
        assertEquals("测试读者", foundReader.get().getName());
        verify(readerRepository, times(1)).findByIdCard("110101199001011234");
    }

    /**
     * 测试根据手机号查询读者功能
     * 验证能够根据手机号成功查询到读者
     */
    @Test
    public void testFindReaderByPhone() {
        when(readerRepository.findByPhone("13800138000")).thenReturn(reader);
        Optional<Reader> foundReader = readerService.findReaderByPhone("13800138000");
        assertTrue(foundReader.isPresent());
        assertEquals("测试读者", foundReader.get().getName());
        verify(readerRepository, times(1)).findByPhone("13800138000");
    }

    /**
     * 测试根据读者类型查询读者功能
     * 验证能够根据读者类型成功查询到读者列表
     */
    @Test
    public void testFindReadersByType() {
        List<Reader> readers = new ArrayList<>();
        readers.add(reader);
        when(readerRepository.findByReaderType("STUDENT")).thenReturn(readers);
        List<Reader> foundReaders = readerService.findReadersByType("STUDENT");
        assertNotNull(foundReaders);
        assertEquals(1, foundReaders.size());
        verify(readerRepository, times(1)).findByReaderType("STUDENT");
    }
}