package com.vithay.libman.service;

import com.vithay.libman.dao.ReaderDao;
import com.vithay.libman.model.Reader;

import java.time.LocalDate;
import java.util.List;

public class ReaderService {
    private final ReaderDao readerDao = new ReaderDao();
    private final SettingService settingService = SettingService.getInstance();

    public List<Reader> getAllReaders() {
        return readerDao.getAllReaders();
    }

    public List<Reader> getDeletedReaders() {
        return readerDao.getDeletedReaders();
    }

    public Reader getReaderById(String id) {
        return readerDao.getReaderById(id);
    }

    public List<Reader> searchReaders(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllReaders();
        }
        return readerDao.searchReaders(keyword.trim());
    }

    public boolean saveReader(Reader reader) {
        if (reader.getId() == null || reader.getId().trim().isEmpty()) {
            return false;
        }
        Reader existing = readerDao.getReaderById(reader.getId());
        if (existing == null) {
            return readerDao.addReader(reader);
        } else {
            return readerDao.updateReader(reader);
        }
    }

    public boolean issueCard(String readerId) {
        LocalDate today = LocalDate.now();
        int months = settingService.getCardValidityMonths();
        LocalDate expiry = today.plusMonths(months > 0 ? months : 12);
        return readerDao.issueCard(readerId, today.toString(), expiry.toString());
    }

    public boolean issueCard(String readerId, String issueDate, String expiryDate) {
        return readerDao.issueCard(readerId, issueDate, expiryDate);
    }

    public boolean softDeleteReader(String id) {
        return readerDao.softDeleteReader(id);
    }

    public boolean restoreReader(String id) {
        return readerDao.restoreReader(id);
    }

    public boolean permanentDeleteReader(String id) {
        return readerDao.permanentDeleteReader(id);
    }

    public int deactivateExpiredReaders() {
        String today = LocalDate.now().toString();
        return readerDao.deactivateExpiredReaders(today);
    }
}
