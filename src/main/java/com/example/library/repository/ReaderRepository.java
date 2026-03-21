package com.example.library.repository;

import com.example.library.entity.Reader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReaderRepository extends JpaRepository<Reader, Long> {
    // 根据姓名模糊查询
    List<Reader> findByNameContaining(String name);

    // 根据身份证号查询
    Reader findByIdCard(String idCard);

    // 根据手机号查询
    Reader findByPhone(String phone);

    // 根据读者类型查询
    List<Reader> findByReaderType(String readerType);

    // 根据状态查询
    List<Reader> findByStatus(String status);

    // 搜索功能：根据姓名、身份证号、手机号模糊查询
    @Query("SELECT r FROM Reader r WHERE r.name LIKE %:keyword% OR r.idCard LIKE %:keyword% OR r.phone LIKE %:keyword%")
    List<Reader> searchReaders(@Param("keyword") String keyword);
}