package com.example.library.service;

import com.example.library.entity.Book;
import java.util.List;
import java.util.Optional;

public interface BookService {
    // 保存图书
    Book saveBook(Book book);

    // 根据ID查询图书
    Optional<Book> getBookById(Long id);

    // 查询所有图书
    List<Book> getAllBooks();

    // 更新图书信息
    Book updateBook(Book book);

    // 删除图书
    void deleteBook(Long id);

    // 根据书名模糊查询
    List<Book> findBooksByTitle(String title);

    // 根据作者查询
    List<Book> findBooksByAuthor(String author);

    // 根据ISBN查询
    Optional<Book> findBookByIsbn(String isbn);

    // 根据分类查询
    List<Book> findBooksByCategory(String category);

    // 根据状态查询
    List<Book> findBooksByStatus(String status);

    // 搜索图书
    List<Book> searchBooks(String keyword);

    // 图书入库
    Book addStock(Long id, int quantity);

    // 图书出库
    Book reduceStock(Long id, int quantity);
}