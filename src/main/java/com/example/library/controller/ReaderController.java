package com.example.library.controller;

import com.example.library.entity.Reader;
import com.example.library.service.ReaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/readers")
public class ReaderController {
    @Autowired
    private ReaderService readerService;

    // 获取所有读者
    @GetMapping
    public ResponseEntity<List<Reader>> getAllReaders() {
        List<Reader> readers = readerService.getAllReaders();
        return new ResponseEntity<>(readers, HttpStatus.OK);
    }

    // 根据ID获取读者
    @GetMapping("/{id}")
    public ResponseEntity<Reader> getReaderById(@PathVariable Long id) {
        Optional<Reader> reader = readerService.getReaderById(id);
        return reader.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // 创建读者
    @PostMapping
    public ResponseEntity<Reader> createReader(@RequestBody Reader reader) {
        Reader savedReader = readerService.saveReader(reader);
        return new ResponseEntity<>(savedReader, HttpStatus.CREATED);
    }

    // 更新读者
    @PutMapping("/{id}")
    public ResponseEntity<Reader> updateReader(@PathVariable Long id, @RequestBody Reader reader) {
        reader.setId(id);
        Reader updatedReader = readerService.updateReader(reader);
        if (updatedReader != null) {
            return new ResponseEntity<>(updatedReader, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // 删除读者
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReader(@PathVariable Long id) {
        readerService.deleteReader(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // 根据姓名搜索
    @GetMapping("/search/name")
    public ResponseEntity<List<Reader>> searchReadersByName(@RequestParam String name) {
        List<Reader> readers = readerService.findReadersByName(name);
        return new ResponseEntity<>(readers, HttpStatus.OK);
    }

    // 根据身份证号搜索
    @GetMapping("/search/id-card")
    public ResponseEntity<Reader> searchReaderByIdCard(@RequestParam String idCard) {
        Optional<Reader> reader = readerService.findReaderByIdCard(idCard);
        return reader.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // 根据手机号搜索
    @GetMapping("/search/phone")
    public ResponseEntity<Reader> searchReaderByPhone(@RequestParam String phone) {
        Optional<Reader> reader = readerService.findReaderByPhone(phone);
        return reader.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // 综合搜索
    @GetMapping("/search")
    public ResponseEntity<List<Reader>> searchReaders(@RequestParam String keyword) {
        List<Reader> readers = readerService.searchReaders(keyword);
        return new ResponseEntity<>(readers, HttpStatus.OK);
    }
}