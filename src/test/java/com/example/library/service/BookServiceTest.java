package com.example.library.service;

import com.example.library.entity.Book;
import com.example.library.repository.BookRepository;
import com.example.library.service.impl.BookServiceImpl;
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
 * 图书服务测试类
 * 测试BookService的各种功能方法
 */
public class BookServiceTest {
    /**
     * 模拟的图书数据访问接口
     */
    @Mock
    private BookRepository bookRepository;

    /**
     * 被测试的图书服务实现类
     */
    @InjectMocks
    private BookServiceImpl bookService;

    /**
     * 测试用的图书对象
     */
    private Book book;

    /**
     * 测试前的准备工作
     * 初始化模拟对象和测试数据
     */
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
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
    }

    /**
     * 测试保存图书功能
     * 验证图书能够成功保存到数据库
     */
    @Test
    public void testSaveBook() {
        when(bookRepository.save(book)).thenReturn(book);
        Book savedBook = bookService.saveBook(book);
        assertNotNull(savedBook);
        assertEquals("测试图书", savedBook.getTitle());
        verify(bookRepository, times(1)).save(book);
    }

    /**
     * 测试根据ID获取图书功能
     * 验证能够根据ID成功查询到图书
     */
    @Test
    public void testGetBookById() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        Optional<Book> foundBook = bookService.getBookById(1L);
        assertTrue(foundBook.isPresent());
        assertEquals("测试图书", foundBook.get().getTitle());
        verify(bookRepository, times(1)).findById(1L);
    }

    /**
     * 测试获取所有图书功能
     * 验证能够成功查询到所有图书
     */
    @Test
    public void testGetAllBooks() {
        List<Book> books = new ArrayList<>();
        books.add(book);
        when(bookRepository.findAll()).thenReturn(books);
        List<Book> foundBooks = bookService.getAllBooks();
        assertNotNull(foundBooks);
        assertEquals(1, foundBooks.size());
        verify(bookRepository, times(1)).findAll();
    }

    /**
     * 测试更新图书功能
     * 验证图书信息能够成功更新
     */
    @Test
    public void testUpdateBook() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRepository.save(book)).thenReturn(book);
        book.setTitle("更新后的书名");
        Book updatedBook = bookService.updateBook(book);
        assertNotNull(updatedBook);
        assertEquals("更新后的书名", updatedBook.getTitle());
        verify(bookRepository, times(1)).findById(1L);
        verify(bookRepository, times(1)).save(book);
    }

    /**
     * 测试删除图书功能
     * 验证能够成功删除图书
     */
    @Test
    public void testDeleteBook() {
        doNothing().when(bookRepository).deleteById(1L);
        bookService.deleteBook(1L);
        verify(bookRepository, times(1)).deleteById(1L);
    }

    /**
     * 测试根据书名查询图书功能
     * 验证能够根据书名成功查询到图书
     */
    @Test
    public void testFindBooksByTitle() {
        List<Book> books = new ArrayList<>();
        books.add(book);
        when(bookRepository.findByTitleContaining("测试")).thenReturn(books);
        List<Book> foundBooks = bookService.findBooksByTitle("测试");
        assertNotNull(foundBooks);
        assertEquals(1, foundBooks.size());
        verify(bookRepository, times(1)).findByTitleContaining("测试");
    }

    /**
     * 测试根据作者查询图书功能
     * 验证能够根据作者成功查询到图书
     */
    @Test
    public void testFindBooksByAuthor() {
        List<Book> books = new ArrayList<>();
        books.add(book);
        when(bookRepository.findByAuthor("测试作者")).thenReturn(books);
        List<Book> foundBooks = bookService.findBooksByAuthor("测试作者");
        assertNotNull(foundBooks);
        assertEquals(1, foundBooks.size());
        verify(bookRepository, times(1)).findByAuthor("测试作者");
    }

    /**
     * 测试根据ISBN查询图书功能
     * 验证能够根据ISBN成功查询到图书
     */
    @Test
    public void testFindBookByIsbn() {
        when(bookRepository.findByIsbn("9787123456789")).thenReturn(book);
        Optional<Book> foundBook = bookService.findBookByIsbn("9787123456789");
        assertTrue(foundBook.isPresent());
        assertEquals("测试图书", foundBook.get().getTitle());
        verify(bookRepository, times(1)).findByIsbn("9787123456789");
    }

    /**
     * 测试增加图书库存功能
     * 验证能够成功增加图书库存
     */
    @Test
    public void testAddStock() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRepository.save(book)).thenReturn(book);
        Book updatedBook = bookService.addStock(1L, 5);
        assertNotNull(updatedBook);
        assertEquals(15, updatedBook.getStock().intValue());
        verify(bookRepository, times(1)).findById(1L);
        verify(bookRepository, times(1)).save(book);
    }

    /**
     * 测试减少图书库存功能
     * 验证能够成功减少图书库存
     */
    @Test
    public void testReduceStock() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRepository.save(book)).thenReturn(book);
        Book updatedBook = bookService.reduceStock(1L, 3);
        assertNotNull(updatedBook);
        assertEquals(7, updatedBook.getStock().intValue());
        verify(bookRepository, times(1)).findById(1L);
        verify(bookRepository, times(1)).save(book);
    }

    /**
     * 测试库存不足时减少库存功能
     * 验证库存不足时会抛出异常
     */
    @Test(expected = RuntimeException.class)
    public void testReduceStockWithInsufficientStock() {
        book.setStock(2);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        bookService.reduceStock(1L, 3);
    }
}