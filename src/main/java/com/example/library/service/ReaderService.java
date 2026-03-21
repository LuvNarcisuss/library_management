package com.example.library.service;

import com.example.library.entity.Reader;
import java.util.List;
import java.util.Optional;

public interface ReaderService {
    // 保存读者
    Reader saveReader(Reader reader);

    // 根据ID查询读者
    Optional<Reader> getReaderById(Long id);

    // 查询所有读者
    List<Reader> getAllReaders();

    // 更新读者信息
    Reader updateReader(Reader reader);

    // 删除读者
    void deleteReader(Long id);

    // 根据姓名模糊查询
    List<Reader> findReadersByName(String name);

    // 根据身份证号查询
    Optional<Reader> findReaderByIdCard(String idCard);

    // 根据手机号查询
    Optional<Reader> findReaderByPhone(String phone);

    // 根据读者类型查询
    List<Reader> findReadersByType(String readerType);

    // 根据状态查询
    List<Reader> findReadersByStatus(String status);

    // 搜索读者
    List<Reader> searchReaders(String keyword);
}