package com.vithay.libman.model;

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

    public BorrowTransaction(String id, String readerId, String readerName, String bookId,
                             String bookTitle, String borrowDate, String dueDate,
                             String returnDate, String status, double fineAmount, String notes) {
        this(id, readerId, readerName, bookId, bookTitle, borrowDate, dueDate, returnDate, "Mang về nhà", status, fineAmount, notes);
    }

    public String getId() { return id; }
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
