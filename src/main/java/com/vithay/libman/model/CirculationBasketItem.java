package com.vithay.libman.model;

import java.time.LocalDate;

public class CirculationBasketItem {
    private final Book book;
    private String loanType; // "TAI_CHO" / "VE_NHA" or "Mượn đọc tại chỗ" / "Mang về nhà"
    private int loanDays;
    private LocalDate dueDate;

    public CirculationBasketItem(Book book, String loanType, int loanDays) {
        this.book = book;
        this.loanType = loanType;
        this.loanDays = loanDays;
        this.dueDate = LocalDate.now().plusDays(loanDays);
    }

    public Book getBook() { return book; }
    public String getLoanType() { return loanType; }
    public void setLoanType(String loanType) { this.loanType = loanType; }
    public int getLoanDays() { return loanDays; }
    public void setLoanDays(int loanDays) { 
        this.loanDays = loanDays; 
        this.dueDate = LocalDate.now().plusDays(loanDays);
    }
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
}
