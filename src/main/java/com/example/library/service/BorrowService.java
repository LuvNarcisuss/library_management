package com.example.library.service;

import com.example.library.entity.BorrowRecord;
import com.example.library.entity.Book;
import com.example.library.entity.Reader;

import java.util.List;
import java.util.Optional;
import java.util.Date;

public interface BorrowService {
    // 创建借阅记录
    BorrowRecord borrowBook(Long bookId, Long readerId, Integer days);

    // 归还图书
    BorrowRecord returnBook(Long recordId);

    // 根据ID查询借阅记录
    Optional<BorrowRecord> getBorrowRecordById(Long id);

    // 查询所有借阅记录
    List<BorrowRecord> getAllBorrowRecords();

    // 根据图书查询借阅记录
    List<BorrowRecord> getBorrowRecordsByBook(Book book);

    // 根据读者查询借阅记录
    List<BorrowRecord> getBorrowRecordsByReader(Reader reader);

    // 根据状态查询借阅记录
    List<BorrowRecord> getBorrowRecordsByStatus(String status);

    // 查询过期未归还的记录
    List<BorrowRecord> getOverdueRecords();

    // 查询读者当前借阅的图书
    List<BorrowRecord> getCurrentBorrowedBooksByReader(Reader reader);

    // 查询图书当前的借阅状态
    Optional<BorrowRecord> getCurrentBorrowedRecordByBook(Book book);
}