package com.vithay.libman.service;

import com.vithay.libman.dao.BookDao;
import com.vithay.libman.dao.BorrowTransactionDao;
import com.vithay.libman.dao.ReaderDao;
import com.vithay.libman.model.Book;
import com.vithay.libman.model.BorrowTransaction;
import com.vithay.libman.model.Reader;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class BorrowService {
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final BorrowTransactionDao transactionDao = new BorrowTransactionDao();
    private final BookDao bookDao = new BookDao();
    private final ReaderDao readerDao = new ReaderDao();
    private final SettingService settingService = SettingService.getInstance();

    public List<BorrowTransaction> getAllTransactions() {
        return transactionDao.getAllTransactions();
    }

    public List<BorrowTransaction> getRecentTransactions(int limit) {
        return transactionDao.getRecentTransactions(limit);
    }

    public List<BorrowTransaction> searchTransactions(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllTransactions();
        }
        return transactionDao.searchTransactions(keyword.trim());
    }

    public int getActiveBorrowCountForReader(String readerId) {
        return transactionDao.getActiveBorrowCountForReader(readerId);
    }

    public List<BorrowTransaction> getActiveTransactions() {
        return transactionDao.getActiveTransactions();
    }

    public List<BorrowTransaction> getTransactionsByReader(String readerId) {
        if (readerId == null || readerId.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return transactionDao.getTransactionsByReader(readerId.trim());
    }

    public String createBorrowTransaction(String readerId, String bookId, String borrowType, int borrowDays, String notes) {
        return borrowBook(readerId, bookId, borrowType, borrowDays, notes);
    }

    public String borrowBook(String readerId, String bookId, String borrowType, int borrowDays, String notes) {
        Reader reader = readerDao.getReaderById(readerId);
        if (reader == null) {
            return "Lỗi: Không tìm thấy độc giả với mã " + readerId;
        }
        if ("Chờ Cấp Thẻ".equalsIgnoreCase(reader.getStatus())) {
            return "Lỗi: Độc giả chưa được cấp thẻ thư viện tại quầy. Vui lòng gặp Thủ Thư để xác nhận hồ sơ và nhận thẻ trước khi mượn sách!";
        }
        if ("Blocked".equalsIgnoreCase(reader.getStatus())) {
            return "Lỗi: Thẻ độc giả đang bị khóa tài khoản, không thể mượn sách!";
        }
        if ("Expired".equalsIgnoreCase(reader.getStatus())) {
            return "Lỗi: Thẻ độc giả đã hết hạn hiệu lực! Vui lòng gia hạn trước khi mượn.";
        }

        // Quy định kiểm tra số lượng sách mượn tối đa (SRS mục 6.1)
        int currentActiveBorrows = transactionDao.getActiveBorrowCountForReader(readerId);
        int maxAllowed = settingService.getMaxBooksPerReader();
        if (currentActiveBorrows >= maxAllowed) {
            return "Lỗi quy định: Độc giả đã mượn " + currentActiveBorrows + "/" + maxAllowed + " cuốn sách. Không thể mượn thêm!";
        }

        Book book = bookDao.getBookById(bookId);
        if (book == null) {
            return "Lỗi: Không tìm thấy sách với mã " + bookId;
        }
        if (book.getAvailableCopies() <= 0) {
            return "Lỗi: Sách hiện đã hết bản có sẵn để mượn!";
        }

        String type = (borrowType != null && !borrowType.trim().isEmpty()) ? borrowType : "Mang về nhà";
        int days = borrowDays;
        if ("Mượn đọc tại chỗ".equalsIgnoreCase(type)) {
            days = Math.min(days, settingService.getMaxBorrowDaysOnsite());
        } else {
            days = Math.min(days, settingService.getMaxBorrowDaysHome());
        }

        LocalDate now = LocalDate.now();
        LocalDate due = now.plusDays(days > 0 ? days : 14);

        String txId = transactionDao.getNextTransactionId();
        BorrowTransaction tx = new BorrowTransaction(
                txId,
                reader.getId(),
                reader.getFullName(),
                book.getId(),
                book.getTitle(),
                now.format(DATE_FMT),
                due.format(DATE_FMT),
                null,
                type,
                "Đang Mượn",
                0.0,
                notes != null ? notes : "Mượn tại quầy"
        );

        boolean added = transactionDao.addTransaction(tx);
        if (added) {
            int newAvailable = book.getAvailableCopies() - 1;
            String newStatus = newAvailable == 0 ? "Borrowed" : "Available";
            bookDao.updateBookStatus(book.getId(), newStatus, newAvailable);
            return null; // success
        } else {
            return "Lỗi hệ thống: Không thể lưu phiếu mượn vào cơ sở dữ liệu!";
        }
    }

    public double calculateOverdueFine(String dueDateStr, LocalDate returnDate) {
        try {
            LocalDate due = LocalDate.parse(dueDateStr, DATE_FMT);
            if (returnDate.isAfter(due)) {
                long daysOverdue = ChronoUnit.DAYS.between(due, returnDate);
                double finePerDay = settingService.getFinePerDay();
                return daysOverdue * finePerDay;
            }
        } catch (Exception ignored) {
        }
        return 0.0;
    }

    public String returnBook(String transactionId, boolean isLostOrDamaged) {
        BorrowTransaction tx = transactionDao.getTransactionById(transactionId);
        if (tx == null) {
            return "Lỗi: Không tìm thấy giao dịch với mã " + transactionId;
        }
        if ("Đã Trả".equalsIgnoreCase(tx.getStatus())) {
            return "Thông báo: Phiếu mượn này đã được trả trước đó.";
        }

        LocalDate now = LocalDate.now();
        double overdueFine = calculateOverdueFine(tx.getDueDate(), now);
        double totalFine = overdueFine;

        Book book = bookDao.getBookById(tx.getBookId());
        if (isLostOrDamaged && book != null) {
            double multiplier = settingService.getLostBookMultiplier();
            double procFee = settingService.getProcessingFee();
            totalFine += (book.getPrice() * multiplier) + procFee;
        }

        boolean updated = transactionDao.returnBook(transactionId, now.format(DATE_FMT), totalFine);
        if (updated) {
            if (book != null && !isLostOrDamaged) {
                int newAvailable = book.getAvailableCopies() + 1;
                bookDao.updateBookStatus(book.getId(), "Available", newAvailable);
            }
            return null; // success
        } else {
            return "Lỗi hệ thống: Không thể cập nhật trạng thái phiếu trả!";
        }
    }
}
