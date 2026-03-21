package com.example.library.service.impl;

import com.example.library.entity.Reader;
import com.example.library.repository.ReaderRepository;
import com.example.library.service.ReaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ReaderServiceImpl implements ReaderService {
    @Autowired
    private ReaderRepository readerRepository;

    @Override
    public Reader saveReader(Reader reader) {
        // 默认状态为激活
        if (reader.getStatus() == null) {
            reader.setStatus("ACTIVE");
        }
        return readerRepository.save(reader);
    }

    @Override
    public Optional<Reader> getReaderById(Long id) {
        return readerRepository.findById(id);
    }

    @Override
    public List<Reader> getAllReaders() {
        return readerRepository.findAll();
    }

    @Override
    public Reader updateReader(Reader reader) {
        Optional<Reader> existingReader = readerRepository.findById(reader.getId());
        if (existingReader.isPresent()) {
            return readerRepository.save(reader);
        }
        return null;
    }

    @Override
    public void deleteReader(Long id) {
        readerRepository.deleteById(id);
    }

    @Override
    public List<Reader> findReadersByName(String name) {
        return readerRepository.findByNameContaining(name);
    }

    @Override
    public Optional<Reader> findReaderByIdCard(String idCard) {
        return Optional.ofNullable(readerRepository.findByIdCard(idCard));
    }

    @Override
    public Optional<Reader> findReaderByPhone(String phone) {
        return Optional.ofNullable(readerRepository.findByPhone(phone));
    }

    @Override
    public List<Reader> findReadersByType(String readerType) {
        return readerRepository.findByReaderType(readerType);
    }

    @Override
    public List<Reader> findReadersByStatus(String status) {
        return readerRepository.findByStatus(status);
    }

    @Override
    public List<Reader> searchReaders(String keyword) {
        return readerRepository.searchReaders(keyword);
    }
}