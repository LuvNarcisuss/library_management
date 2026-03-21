package com.example.library.service;

import com.example.library.entity.BorrowRecord;
import com.example.library.entity.Book;
import com.example.library.entity.Reader;
import com.example.library.repository.BorrowRecordRepository;
import com.example.library.service.impl.BorrowServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * 借阅服务测试类
 * 测试BorrowService的各种功能方法
 */
public class BorrowServiceTest {
    /**
     * 模拟的借阅记录数据访问接口
     */
    @Mock
    private BorrowRecordRepository borrowRecordRepository;
    
    /**
     * 模拟的图书服务接口
     */
    @Mock
    private BookService bookService;
    
    /**
     * 模拟的读者服务接口
     */
    @Mock
    private ReaderService readerService;
    
    /**
     * 被测试的借阅服务实现类
     */
    @InjectMocks
    private BorrowServiceImpl borrowService;
    
    /**
     * 测试用的图书对象
     */
    private Book book;
    
    /**
     * 测试用的读者对象
     */
    private Reader reader;
    
    /**
     * 测试用的借阅记录对象
     */
    private BorrowRecord borrowRecord;
    
    /**
     * 测试前的准备工作
     * 初始化模拟对象和测试数据
     */
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        
        // 初始化图书对象
        book = new Book();
        book.setId(1L);
        book.setTitle("测试图书");
        book.setAuthor("测试作者");
        book.setIsbn("9787123456789");
        book.setCategory("测试分类");
        book.setPublisher("测试出版社");
        book.setPublishDate(new Date());
        book.setPrice(29.99);
        book.setStock(10);
        book.setStatus("AVAILABLE");
        book.setCreateTime(new Date());
        book.setUpdateTime(new Date());
        
        // 初始化读者对象
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
        
        // 初始化借阅记录对象
        borrowRecord = new BorrowRecord();
        borrowRecord.setId(1L);
        borrowRecord.setBook(book);
        borrowRecord.setReader(reader);
        borrowRecord.setBorrowDate(new Date());
        
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH, 30);
        borrowRecord.setDueDate(calendar.getTime());
        
        borrowRecord.setStatus("BORROWED");
        borrowRecord.setCreateTime(new Date());
        borrowRecord.setUpdateTime(new Date());
    }
    
    /**
     * 测试借阅图书功能
     * 验证图书能够成功被借阅
     */
    @Test
    public void testBorrowBookSuccess() {
        // 模拟依赖方法的返回值
        when(bookService.getBookById(1L)).thenReturn(Optional.of(book));
        when(readerService.getReaderById(1L)).thenReturn(Optional.of(reader));
        when(bookService.reduceStock(1L, 1)).thenReturn(book);
        when(borrowRecordRepository.save(any(BorrowRecord.class))).thenReturn(borrowRecord);
        
        // 执行被测试方法
        BorrowRecord result = borrowService.borrowBook(1L, 1L, 30);
        
        // 验证结果
        assertNotNull(result);
        assertEquals("BORROWED", result.getStatus());
        assertEquals(book, result.getBook());
        assertEquals(reader, result.getReader());
        
        // 验证依赖方法的调用
        verify(bookService, times(1)).getBookById(1L);
        verify(readerService, times(1)).getReaderById(1L);
        verify(bookService, times(1)).reduceStock(1L, 1);
        verify(borrowRecordRepository, times(1)).save(any(BorrowRecord.class));
    }
    
    /**
     * 测试借阅不存在的图书
     * 验证借阅不存在的图书会抛出异常
     */
    @Test(expected = RuntimeException.class)
    public void testBorrowBookWithNonExistingBook() {
        // 模拟图书不存在的情况
        when(bookService.getBookById(1L)).thenReturn(Optional.empty());
        
        // 执行被测试方法，应该抛出异常
        borrowService.borrowBook(1L, 1L, 30);
        
        // 验证依赖方法的调用
        verify(bookService, times(1)).getBookById(1L);
        verify(readerService, never()).getReaderById(anyLong());
        verify(bookService, never()).reduceStock(anyLong(), anyInt());
        verify(borrowRecordRepository, never()).save(any(BorrowRecord.class));
    }
    
    /**
     * 测试借阅不存在的读者
     * 验证借阅给不存在的读者会抛出异常
     */
    @Test(expected = RuntimeException.class)
    public void testBorrowBookWithNonExistingReader() {
        // 模拟图书存在但读者不存在的情况
        when(bookService.getBookById(1L)).thenReturn(Optional.of(book));
        when(readerService.getReaderById(1L)).thenReturn(Optional.empty());
        
        // 执行被测试方法，应该抛出异常
        borrowService.borrowBook(1L, 1L, 30);
        
        // 验证依赖方法的调用
        verify(bookService, times(1)).getBookById(1L);
        verify(readerService, times(1)).getReaderById(1L);
        verify(bookService, never()).reduceStock(anyLong(), anyInt());
        verify(borrowRecordRepository, never()).save(any(BorrowRecord.class));
    }
    
    /**
     * 测试归还图书功能
     * 验证图书能够成功归还
     */
    @Test
    public void testReturnBookSuccess() {
        // 模拟依赖方法的返回值
        when(borrowRecordRepository.findById(1L)).thenReturn(Optional.of(borrowRecord));
        when(bookService.getBookById(1L)).thenReturn(Optional.of(book));
        when(bookService.addStock(1L, 1)).thenReturn(book);
        when(borrowRecordRepository.save(any(BorrowRecord.class))).thenReturn(borrowRecord);
        
        // 执行被测试方法
        BorrowRecord result = borrowService.returnBook(1L);
        
        // 验证结果
        assertNotNull(result);
        assertNotNull(result.getReturnDate());
        
        // 验证依赖方法的调用
        verify(borrowRecordRepository, times(1)).findById(1L);
        verify(bookService, times(1)).addStock(1L, 1);
        verify(borrowRecordRepository, times(1)).save(any(BorrowRecord.class));
    }
    
    /**
     * 测试归还不存在的借阅记录
     * 验证归还不存在的借阅记录会抛出异常
     */
    @Test(expected = RuntimeException.class)
    public void testReturnBookWithNonExistingRecord() {
        // 模拟借阅记录不存在的情况
        when(borrowRecordRepository.findById(1L)).thenReturn(Optional.empty());
        
        // 执行被测试方法，应该抛出异常
        borrowService.returnBook(1L);
        
        // 验证依赖方法的调用
        verify(borrowRecordRepository, times(1)).findById(1L);
        verify(bookService, never()).addStock(anyLong(), anyInt());
        verify(borrowRecordRepository, never()).save(any(BorrowRecord.class));
    }
    
    /**
     * 测试根据ID获取借阅记录功能
     * 验证能够根据ID成功查询到借阅记录
     */
    @Test
    public void testGetBorrowRecordById() {
        // 模拟依赖方法的返回值
        when(borrowRecordRepository.findById(1L)).thenReturn(Optional.of(borrowRecord));
        
        // 执行被测试方法
        Optional<BorrowRecord> result = borrowService.getBorrowRecordById(1L);
        
        // 验证结果
        assertTrue(result.isPresent());
        assertEquals(borrowRecord, result.get());
        
        // 验证依赖方法的调用
        verify(borrowRecordRepository, times(1)).findById(1L);
    }
    
    /**
     * 测试获取所有借阅记录功能
     * 验证能够成功查询到所有借阅记录
     */
    @Test
    public void testGetAllBorrowRecords() {
        // 创建借阅记录列表
        List<BorrowRecord> records = new ArrayList<>();
        records.add(borrowRecord);
        
        // 模拟依赖方法的返回值
        when(borrowRecordRepository.findAll()).thenReturn(records);
        
        // 执行被测试方法
        List<BorrowRecord> result = borrowService.getAllBorrowRecords();
        
        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(borrowRecord, result.get(0));
        
        // 验证依赖方法的调用
        verify(borrowRecordRepository, times(1)).findAll();
    }
    
    /**
     * 测试根据图书查询借阅记录功能
     * 验证能够根据图书成功查询到借阅记录
     */
    @Test
    public void testGetBorrowRecordsByBook() {
        // 创建借阅记录列表
        List<BorrowRecord> records = new ArrayList<>();
        records.add(borrowRecord);
        
        // 模拟依赖方法的返回值
        when(borrowRecordRepository.findByBook(book)).thenReturn(records);
        
        // 执行被测试方法
        List<BorrowRecord> result = borrowService.getBorrowRecordsByBook(book);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(borrowRecord, result.get(0));
        
        // 验证依赖方法的调用
        verify(borrowRecordRepository, times(1)).findByBook(book);
    }
    
    /**
     * 测试根据读者查询借阅记录功能
     * 验证能够根据读者成功查询到借阅记录
     */
    @Test
    public void testGetBorrowRecordsByReader() {
        // 创建借阅记录列表
        List<BorrowRecord> records = new ArrayList<>();
        records.add(borrowRecord);
        
        // 模拟依赖方法的返回值
        when(borrowRecordRepository.findByReader(reader)).thenReturn(records);
        
        // 执行被测试方法
        List<BorrowRecord> result = borrowService.getBorrowRecordsByReader(reader);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(borrowRecord, result.get(0));
        
        // 验证依赖方法的调用
        verify(borrowRecordRepository, times(1)).findByReader(reader);
    }
    
    /**
     * 测试根据状态查询借阅记录功能
     * 验证能够根据状态成功查询到借阅记录
     */
    @Test
    public void testGetBorrowRecordsByStatus() {
        // 创建借阅记录列表
        List<BorrowRecord> records = new ArrayList<>();
        records.add(borrowRecord);
        
        // 模拟依赖方法的返回值
        when(borrowRecordRepository.findByStatus("BORROWED")).thenReturn(records);
        
        // 执行被测试方法
        List<BorrowRecord> result = borrowService.getBorrowRecordsByStatus("BORROWED");
        
        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(borrowRecord, result.get(0));
        
        // 验证依赖方法的调用
        verify(borrowRecordRepository, times(1)).findByStatus("BORROWED");
    }
    
    /**
     * 测试查询过期未归还的记录功能
     * 验证能够成功查询到过期未归还的记录
     */
    @Test
    public void testGetOverdueRecords() {
        // 创建借阅记录列表
        List<BorrowRecord> records = new ArrayList<>();
        records.add(borrowRecord);
        
        // 模拟依赖方法的返回值
        when(borrowRecordRepository.findByDueDateBeforeAndStatus(any(Date.class), eq("BORROWED"))).thenReturn(records);
        
        // 执行被测试方法
        List<BorrowRecord> result = borrowService.getOverdueRecords();
        
        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(borrowRecord, result.get(0));
        
        // 验证依赖方法的调用
        verify(borrowRecordRepository, times(1)).findByDueDateBeforeAndStatus(any(Date.class), eq("BORROWED"));
    }
    
    /**
     * 测试查询读者当前借阅的图书功能
     * 验证能够成功查询到读者当前借阅的图书
     */
    @Test
    public void testGetCurrentBorrowedBooksByReader() {
        // 创建借阅记录列表
        List<BorrowRecord> records = new ArrayList<>();
        records.add(borrowRecord);
        
        // 模拟依赖方法的返回值
        when(borrowRecordRepository.findByReaderAndStatus(reader, "BORROWED")).thenReturn(records);
        
        // 执行被测试方法
        List<BorrowRecord> result = borrowService.getCurrentBorrowedBooksByReader(reader);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(borrowRecord, result.get(0));
        
        // 验证依赖方法的调用
        verify(borrowRecordRepository, times(1)).findByReaderAndStatus(reader, "BORROWED");
    }
    
    /**
     * 测试查询图书当前的借阅状态功能
     * 验证能够成功查询到图书当前的借阅状态
     */
    @Test
    public void testGetCurrentBorrowedRecordByBook() {
        // 模拟依赖方法的返回值
        when(borrowRecordRepository.findByBookAndStatus(book, "BORROWED")).thenReturn(borrowRecord);
        
        // 执行被测试方法
        Optional<BorrowRecord> result = borrowService.getCurrentBorrowedRecordByBook(book);
        
        // 验证结果
        assertTrue(result.isPresent());
        assertEquals(borrowRecord, result.get());
        
        // 验证依赖方法的调用
        verify(borrowRecordRepository, times(1)).findByBookAndStatus(book, "BORROWED");
    }
}