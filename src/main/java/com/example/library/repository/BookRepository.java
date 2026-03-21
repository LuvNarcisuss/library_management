package com.example.library.repository;

import com.example.library.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    // 根据书名模糊查询
    List<Book> findByTitleContaining(String title);

    // 根据作者查询
    List<Book> findByAuthor(String author);

    // 根据ISBN查询
    Book findByIsbn(String isbn);

    // 根据分类查询
    List<Book> findByCategory(String category);

    // 根据状态查询
    List<Book> findByStatus(String status);

    // 搜索功能：根据书名、作者、ISBN模糊查询
    @Query("SELECT b FROM Book b WHERE b.title LIKE %:keyword% OR b.author LIKE %:keyword% OR b.isbn LIKE %:keyword%")
    List<Book> searchBooks(@Param("keyword") String keyword);
}