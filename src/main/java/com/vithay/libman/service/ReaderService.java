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

    public boolean isCardValid(Reader reader) {
        if (reader == null || reader.isDeleted()) {
            return false;
        }
        String status = reader.getStatus();
        if (status == null) {
            return false;
        }
        String s = status.trim().toUpperCase();
        if (s.contains("BLOCK") || s.contains("KHOA") || s.contains("KHÓA") || s.contains("BỊ KHÓA") ||
                s.contains("CHO") || s.contains("CHỜ") || s.contains("EXPIRE") || s.contains("HET") || s.contains("HẾT")) {
            return false;
        }
        LocalDate expiry = reader.getNgayHetHan();
        if (expiry == null || expiry.isBefore(LocalDate.now())) {
            return false;
        }
        return true;
    }

    public boolean isCardValid(String readerId) {
        if (readerId == null || readerId.trim().isEmpty()) {
            return false;
        }
        Reader reader = getReaderById(readerId.trim());
        return isCardValid(reader);
    }

    public Reader getReaderForUser(com.vithay.libman.model.User user) {
        if (user == null) return null;
        if ("docgia".equalsIgnoreCase(user.getUsername())) {
            Reader r = getReaderById("DG001");
            if (r != null) return r;
        }
        List<Reader> all = getAllReaders();
        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            for (Reader r : all) {
                if (user.getEmail().equalsIgnoreCase(r.getEmail())) {
                    return r;
                }
            }
        }
        if (user.getFullName() != null && !user.getFullName().isBlank()) {
            for (Reader r : all) {
                if (user.getFullName().equalsIgnoreCase(r.getFullName())) {
                    return r;
                }
            }
        }
        if ("Độc Giả".equalsIgnoreCase(user.getRole())) {
            String readerId = "DG_" + user.getUsername().toUpperCase();
            Reader newReader = new Reader(
                    readerId,
                    user.getFullName() != null ? user.getFullName() : user.getUsername(),
                    user.getEmail(),
                    user.getPhone(),
                    "Độc giả trực tuyến",
                    "048000" + (System.currentTimeMillis() % 1000000),
                    "2000-01-01",
                    LocalDate.now().toString(),
                    LocalDate.now().toString(),
                    LocalDate.now().plusYears(2).toString(),
                    "Active",
                    false
            );
            readerDao.addReader(newReader);
            return newReader;
        }
        return null;
    }
}
