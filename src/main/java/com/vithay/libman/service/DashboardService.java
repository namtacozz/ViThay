package com.vithay.libman.service;

import com.vithay.libman.dao.BookDao;
import com.vithay.libman.dao.BorrowTransactionDao;
import com.vithay.libman.dao.ReaderDao;
import com.vithay.libman.model.Book;
import com.vithay.libman.model.BorrowTransaction;
import com.vithay.libman.model.Reader;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashboardService {
    private final BookDao bookDao = new BookDao();
    private final ReaderDao readerDao = new ReaderDao();
    private final BorrowTransactionDao transactionDao = new BorrowTransactionDao();

    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        List<Book> books = bookDao.getAllBooks();
        List<Reader> readers = readerDao.getAllReaders();
        List<BorrowTransaction> transactions = transactionDao.getAllTransactions();

        int totalBooks = books.size();
        int availableBooks = 0;
        int borrowedBooks = 0;
        for (Book b : books) {
            if ("Available".equalsIgnoreCase(b.getStatus())) {
                availableBooks++;
            } else if ("Borrowed".equalsIgnoreCase(b.getStatus())) {
                borrowedBooks++;
            }
        }

        int activeTransactions = 0;
        int overdueCount = 0;
        LocalDate today = LocalDate.now();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (BorrowTransaction tx : transactions) {
            if ("Đang Mượn".equalsIgnoreCase(tx.getStatus())) {
                activeTransactions++;
                try {
                    LocalDate due = LocalDate.parse(tx.getDueDate(), fmt);
                    if (today.isAfter(due)) {
                        overdueCount++;
                    }
                } catch (Exception ignored) {
                }
            }
        }

        stats.put("totalBooks", totalBooks);
        stats.put("availableBooks", availableBooks);
        stats.put("borrowedBooks", borrowedBooks);
        stats.put("totalReaders", readers.size());
        stats.put("activeTransactions", activeTransactions);
        stats.put("overdueCount", overdueCount);

        return stats;
    }
}
