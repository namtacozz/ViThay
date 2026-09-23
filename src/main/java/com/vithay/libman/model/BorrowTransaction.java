package com.vithay.libman.model;

import java.time.LocalDate;

public class BorrowTransaction {
    private String id;
    private String readerId;
    private String readerName;
    private String bookId;
    private String bookTitle;
    private String borrowDate;
    private String dueDate;
    private String returnDate;
    private String borrowType; // 'Mượn đọc tại chỗ', 'Mang về nhà'
    private String status; // 'Đang Mượn', 'Đã Trả', 'Quá Hạn'
    private double fineAmount;
    private String notes;

    public BorrowTransaction() {
    }

    public BorrowTransaction(String id, String readerId, String readerName, String bookId,
                             String bookTitle, String borrowDate, String dueDate,
                             String returnDate, String borrowType, String status,
                             double fineAmount, String notes) {
        this.id = id;
        this.readerId = readerId;
        this.readerName = readerName;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.borrowType = borrowType != null ? borrowType : "Mang về nhà";
        this.status = status;
        this.fineAmount = fineAmount;
        this.notes = notes;
    }

    public BorrowTransaction(String id, String readerId, String bookId, String borrowType,
                             LocalDate borrowDate, LocalDate dueDate, LocalDate returnDate,
                             double fineAmount, String status) {
        this.id = id;
        this.readerId = readerId;
        this.bookId = bookId;
        this.borrowType = borrowType != null ? borrowType : "Mang về nhà";
        this.borrowDate = borrowDate != null ? borrowDate.toString() : null;
        this.dueDate = dueDate != null ? dueDate.toString() : null;
        this.returnDate = returnDate != null ? returnDate.toString() : null;
        this.fineAmount = fineAmount;
        this.status = status;
    }

    public BorrowTransaction(String id, String readerId, String readerName, String bookId,
                             String bookTitle, String borrowDate, String dueDate,
                             String returnDate, String status, double fineAmount, String notes) {
        this(id, readerId, readerName, bookId, bookTitle, borrowDate, dueDate, returnDate, "Mang về nhà", status, fineAmount, notes);
    }

    public String getId() { return id; }
    public String getMaPhieu() { return id; }
    public void setId(String id) { this.id = id; }

    public String getReaderId() { return readerId; }
    public void setReaderId(String readerId) { this.readerId = readerId; }

    public String getReaderName() { return readerName; }
    public void setReaderName(String readerName) { this.readerName = readerName; }

    public String getBookId() { return bookId; }
    public void setBookId(String bookId) { this.bookId = bookId; }

    public String getBookTitle() { return bookTitle; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }

    public String getBorrowDate() { return borrowDate; }
    public void setBorrowDate(String borrowDate) { this.borrowDate = borrowDate; }

    public String getDueDate() { return dueDate; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }

    public LocalDate getHanTra() {
        if (dueDate == null || dueDate.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(dueDate.trim());
        } catch (Exception e) {
            return null;
        }
    }

    public void setHanTra(LocalDate date) {
        this.dueDate = date != null ? date.toString() : null;
    }

    public LocalDate getNgayMuon() {
        if (borrowDate == null || borrowDate.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(borrowDate.trim());
        } catch (Exception e) {
            return null;
        }
    }

    public void setNgayMuon(LocalDate date) {
        this.borrowDate = date != null ? date.toString() : null;
    }

    public String getReturnDate() { return returnDate; }
    public void setReturnDate(String returnDate) { this.returnDate = returnDate; }

    public String getBorrowType() { return borrowType; }
    public void setBorrowType(String borrowType) { this.borrowType = borrowType; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getFineAmount() { return fineAmount; }
    public void setFineAmount(double fineAmount) { this.fineAmount = fineAmount; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
