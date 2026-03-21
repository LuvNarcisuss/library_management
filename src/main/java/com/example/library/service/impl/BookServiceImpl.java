package com.example.library.service.impl;

import com.example.library.entity.Book;
import com.example.library.repository.BookRepository;
import com.example.library.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BookServiceImpl implements BookService {
    @Autowired
    private BookRepository bookRepository;

    @Override
    public Book saveBook(Book book) {
        // 默认状态为可借阅
        if (book.getStatus() == null) {
            book.setStatus("AVAILABLE");
        }
        return bookRepository.save(book);
    }

    @Override
    public Optional<Book> getBookById(Long id) {
        return bookRepository.findById(id);
    }

    @Override
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @Override
    public Book updateBook(Book book) {
        Optional<Book> existingBook = bookRepository.findById(book.getId());
        if (existingBook.isPresent()) {
            return bookRepository.save(book);
        }
        return null;
    }

    @Override
    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }

    @Override
    public List<Book> findBooksByTitle(String title) {
        return bookRepository.findByTitleContaining(title);
    }

    @Override
    public List<Book> findBooksByAuthor(String author) {
        return bookRepository.findByAuthor(author);
    }

    @Override
    public Optional<Book> findBookByIsbn(String isbn) {
        return Optional.ofNullable(bookRepository.findByIsbn(isbn));
    }

    @Override
    public List<Book> findBooksByCategory(String category) {
        return bookRepository.findByCategory(category);
    }

    @Override
    public List<Book> findBooksByStatus(String status) {
        return bookRepository.findByStatus(status);
    }

    @Override
    public List<Book> searchBooks(String keyword) {
        return bookRepository.searchBooks(keyword);
    }

    @Override
    public Book addStock(Long id, int quantity) {
        Optional<Book> bookOptional = bookRepository.findById(id);
        if (bookOptional.isPresent()) {
            Book book = bookOptional.get();
            book.setStock(book.getStock() + quantity);
            // 如果库存大于0且状态不是可借阅，则更新状态为可借阅
            if (book.getStock() > 0 && !"AVAILABLE".equals(book.getStatus())) {
                book.setStatus("AVAILABLE");
            }
            return bookRepository.save(book);
        }
        return null;
    }

    @Override
    public Book reduceStock(Long id, int quantity) {
        Optional<Book> bookOptional = bookRepository.findById(id);
        if (bookOptional.isPresent()) {
            Book book = bookOptional.get();
            if (book.getStock() >= quantity) {
                book.setStock(book.getStock() - quantity);
                // 如果库存为0，则更新状态为已借出
                if (book.getStock() == 0) {
                    book.setStatus("BORROWED");
                }
                return bookRepository.save(book);
            }
            throw new RuntimeException("库存不足");
        }
        return null;
    }
}