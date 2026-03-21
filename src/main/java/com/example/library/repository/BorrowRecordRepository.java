package com.example.library.repository;

import com.example.library.entity.BorrowRecord;
import com.example.library.entity.Book;
import com.example.library.entity.Reader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Date;

@Repository
public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {
    // 根据图书查询借阅记录
    List<BorrowRecord> findByBook(Book book);

    // 根据读者查询借阅记录
    List<BorrowRecord> findByReader(Reader reader);

    // 根据状态查询借阅记录
    List<BorrowRecord> findByStatus(String status);

    // 根据到期日期查询过期记录
    List<BorrowRecord> findByDueDateBeforeAndStatus(Date dueDate, String status);

    // 查询读者当前借阅的图书记录
    List<BorrowRecord> findByReaderAndStatus(Reader reader, String status);

    // 查询图书当前的借阅记录
    BorrowRecord findByBookAndStatus(Book book, String status);
}