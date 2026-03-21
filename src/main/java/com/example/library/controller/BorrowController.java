package com.example.library.controller;

import com.example.library.entity.BorrowRecord;
import com.example.library.entity.Book;
import com.example.library.entity.Reader;
import com.example.library.service.BorrowService;
import com.example.library.service.BookService;
import com.example.library.service.ReaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/borrow")
public class BorrowController {
    @Autowired
    private BorrowService borrowService;
    @Autowired
    private BookService bookService;
    @Autowired
    private ReaderService readerService;

    // 获取所有借阅记录
    @GetMapping
    public ResponseEntity<List<BorrowRecord>> getAllBorrowRecords() {
        List<BorrowRecord> records = borrowService.getAllBorrowRecords();
        return new ResponseEntity<>(records, HttpStatus.OK);
    }

    // 根据ID获取借阅记录
    @GetMapping("/{id}")
    public ResponseEntity<BorrowRecord> getBorrowRecordById(@PathVariable Long id) {
        Optional<BorrowRecord> record = borrowService.getBorrowRecordById(id);
        return record.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // 借书
    @PostMapping("/borrow")
    public ResponseEntity<BorrowRecord> borrowBook(@RequestParam Long bookId, @RequestParam Long readerId, @RequestParam(defaultValue = "30") Integer days) {
        try {
            BorrowRecord borrowRecord = borrowService.borrowBook(bookId, readerId, days);
            return new ResponseEntity<>(borrowRecord, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // 还书
    @PutMapping("/return/{id}")
    public ResponseEntity<BorrowRecord> returnBook(@PathVariable Long id) {
        try {
            BorrowRecord borrowRecord = borrowService.returnBook(id);
            return new ResponseEntity<>(borrowRecord, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // 根据图书ID获取借阅记录
    @GetMapping("/book/{bookId}")
    public ResponseEntity<List<BorrowRecord>> getBorrowRecordsByBookId(@PathVariable Long bookId) {
        Optional<Book> book = bookService.getBookById(bookId);
        if (book.isPresent()) {
            List<BorrowRecord> records = borrowService.getBorrowRecordsByBook(book.get());
            return new ResponseEntity<>(records, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // 根据读者ID获取借阅记录
    @GetMapping("/reader/{readerId}")
    public ResponseEntity<List<BorrowRecord>> getBorrowRecordsByReaderId(@PathVariable Long readerId) {
        Optional<Reader> reader = readerService.getReaderById(readerId);
        if (reader.isPresent()) {
            List<BorrowRecord> records = borrowService.getBorrowRecordsByReader(reader.get());
            return new ResponseEntity<>(records, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // 获取当前借阅中的记录
    @GetMapping("/current")
    public ResponseEntity<List<BorrowRecord>> getCurrentBorrowedRecords() {
        List<BorrowRecord> records = borrowService.getBorrowRecordsByStatus("BORROWED");
        return new ResponseEntity<>(records, HttpStatus.OK);
    }

    // 获取过期未归还的记录
    @GetMapping("/overdue")
    public ResponseEntity<List<BorrowRecord>> getOverdueRecords() {
        List<BorrowRecord> records = borrowService.getOverdueRecords();
        return new ResponseEntity<>(records, HttpStatus.OK);
    }

    // 根据读者ID获取当前借阅的图书
    @GetMapping("/reader/{readerId}/current")
    public ResponseEntity<List<BorrowRecord>> getCurrentBorrowedBooksByReaderId(@PathVariable Long readerId) {
        Optional<Reader> reader = readerService.getReaderById(readerId);
        if (reader.isPresent()) {
            List<BorrowRecord> records = borrowService.getCurrentBorrowedBooksByReader(reader.get());
            return new ResponseEntity<>(records, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}