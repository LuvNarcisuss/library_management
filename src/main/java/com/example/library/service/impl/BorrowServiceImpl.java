package com.example.library.service.impl;

import com.example.library.entity.BorrowRecord;
import com.example.library.entity.Book;
import com.example.library.entity.Reader;
import com.example.library.repository.BorrowRecordRepository;
import com.example.library.service.BorrowService;
import com.example.library.service.BookService;
import com.example.library.service.ReaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BorrowServiceImpl implements BorrowService {
    @Autowired
    private BorrowRecordRepository borrowRecordRepository;
    @Autowired
    private BookService bookService;
    @Autowired
    private ReaderService readerService;

    @Override
    public BorrowRecord borrowBook(Long bookId, Long readerId, Integer days) {
        // 1. 检查图书是否存在且可借
        Optional<Book> bookOptional = bookService.getBookById(bookId);
        if (!bookOptional.isPresent()) {
            throw new RuntimeException("图书不存在");
        }
        Book book = bookOptional.get();
        if (!"AVAILABLE".equals(book.getStatus()) || book.getStock() <= 0) {
            throw new RuntimeException("图书不可借或库存不足");
        }

        // 2. 检查读者是否存在且状态正常
        Optional<Reader> readerOptional = readerService.getReaderById(readerId);
        if (!readerOptional.isPresent()) {
            throw new RuntimeException("读者不存在");
        }
        Reader reader = readerOptional.get();
        if (!"ACTIVE".equals(reader.getStatus())) {
            throw new RuntimeException("读者状态异常，无法借书");
        }

        // 3. 创建借阅记录
        BorrowRecord borrowRecord = new BorrowRecord();
        borrowRecord.setBook(book);
        borrowRecord.setReader(reader);
        borrowRecord.setBorrowDate(new Date());

        // 设置到期日期
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH, days);
        borrowRecord.setDueDate(calendar.getTime());

        borrowRecord.setStatus("BORROWED");

        // 4. 更新图书库存和状态
        bookService.reduceStock(bookId, 1);

        // 5. 保存借阅记录
        return borrowRecordRepository.save(borrowRecord);
    }

    @Override
    public BorrowRecord returnBook(Long recordId) {
        // 1. 检查借阅记录是否存在
        Optional<BorrowRecord> recordOptional = borrowRecordRepository.findById(recordId);
        if (!recordOptional.isPresent()) {
            throw new RuntimeException("借阅记录不存在");
        }
        BorrowRecord borrowRecord = recordOptional.get();

        // 2. 检查是否已经归还
        if (!"BORROWED".equals(borrowRecord.getStatus())) {
            throw new RuntimeException("图书已经归还");
        }

        // 3. 更新借阅记录
        borrowRecord.setReturnDate(new Date());
        
        // 检查是否过期
        Date currentDate = new Date();
        if (currentDate.after(borrowRecord.getDueDate())) {
            borrowRecord.setStatus("OVERDUE");
        } else {
            borrowRecord.setStatus("RETURNED");
        }

        // 4. 更新图书库存和状态
        Book book = borrowRecord.getBook();
        bookService.addStock(book.getId(), 1);

        // 5. 保存借阅记录
        return borrowRecordRepository.save(borrowRecord);
    }

    @Override
    public Optional<BorrowRecord> getBorrowRecordById(Long id) {
        return borrowRecordRepository.findById(id);
    }

    @Override
    public List<BorrowRecord> getAllBorrowRecords() {
        return borrowRecordRepository.findAll();
    }

    @Override
    public List<BorrowRecord> getBorrowRecordsByBook(Book book) {
        return borrowRecordRepository.findByBook(book);
    }

    @Override
    public List<BorrowRecord> getBorrowRecordsByReader(Reader reader) {
        return borrowRecordRepository.findByReader(reader);
    }

    @Override
    public List<BorrowRecord> getBorrowRecordsByStatus(String status) {
        return borrowRecordRepository.findByStatus(status);
    }

    @Override
    public List<BorrowRecord> getOverdueRecords() {
        return borrowRecordRepository.findByDueDateBeforeAndStatus(new Date(), "BORROWED");
    }

    @Override
    public List<BorrowRecord> getCurrentBorrowedBooksByReader(Reader reader) {
        return borrowRecordRepository.findByReaderAndStatus(reader, "BORROWED");
    }

    @Override
    public Optional<BorrowRecord> getCurrentBorrowedRecordByBook(Book book) {
        return Optional.ofNullable(borrowRecordRepository.findByBookAndStatus(book, "BORROWED"));
    }
}