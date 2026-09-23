package com.vithay.libman.model;

import java.time.LocalDate;

public class Reader {
    private String id;
    private String fullName;
    private String email;
    private String phone;
    private String address;
    private String idCard;
    private String birthDate;
    private String joinDate;
    private String cardIssueDate;
    private String cardExpiryDate;
    private String status; // Active, Blocked, Expired
    private boolean isDeleted;

    public Reader() {
    }

    public Reader(String id, String fullName, String email, String phone, String address,
                  String idCard, String birthDate, String joinDate,
                  String cardIssueDate, String cardExpiryDate, String status, boolean isDeleted) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.idCard = idCard;
        this.birthDate = birthDate;
        this.joinDate = joinDate;
        this.cardIssueDate = cardIssueDate != null ? cardIssueDate : joinDate;
        this.cardExpiryDate = cardExpiryDate != null ? cardExpiryDate : "2026-12-31";
        this.status = status;
        this.isDeleted = isDeleted;
    }

    public Reader(String id, String fullName, String email, String phone, String address,
                  String idCard, String birthDate, String joinDate, String status) {
        this(id, fullName, email, phone, address, idCard, birthDate, joinDate, joinDate, "2026-12-31", status, false);
    }

    public Reader(String id, String fullName, String email, String phone, String idCard, String address,
                  LocalDate birthDate, LocalDate cardIssueDate, LocalDate cardExpiryDate, String status) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.idCard = idCard;
        this.address = address;
        this.birthDate = birthDate != null ? birthDate.toString() : null;
        this.joinDate = cardIssueDate != null ? cardIssueDate.toString() : LocalDate.now().toString();
        this.cardIssueDate = cardIssueDate != null ? cardIssueDate.toString() : LocalDate.now().toString();
        this.cardExpiryDate = cardExpiryDate != null ? cardExpiryDate.toString() : null;
        this.status = status;
        this.isDeleted = false;
    }

    public String getId() { return id; }
    public String getMaDocGia() { return id; }
    public void setId(String id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getIdCard() { return idCard; }
    public void setIdCard(String idCard) { this.idCard = idCard; }

    public String getBirthDate() { return birthDate; }
    public void setBirthDate(String birthDate) { this.birthDate = birthDate; }

    public String getJoinDate() { return joinDate; }
    public void setJoinDate(String joinDate) { this.joinDate = joinDate; }

    public String getCardIssueDate() { return cardIssueDate; }
    public void setCardIssueDate(String cardIssueDate) { this.cardIssueDate = cardIssueDate; }

    public String getCardExpiryDate() { return cardExpiryDate; }
    public void setCardExpiryDate(String cardExpiryDate) { this.cardExpiryDate = cardExpiryDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public boolean isDeleted() { return isDeleted; }
    public void setDeleted(boolean deleted) { isDeleted = deleted; }

    public LocalDate getNgayHetHan() {
        if (cardExpiryDate == null || cardExpiryDate.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(cardExpiryDate.trim());
        } catch (Exception e) {
            return null;
        }
    }

    public void setNgayHetHan(LocalDate date) {
        this.cardExpiryDate = date != null ? date.toString() : null;
    }

    public LocalDate getNgayCapThe() {
        if (cardIssueDate == null || cardIssueDate.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(cardIssueDate.trim());
        } catch (Exception e) {
            return null;
        }
    }

    public void setNgayCapThe(LocalDate date) {
        this.cardIssueDate = date != null ? date.toString() : null;
    }

    public LocalDate getNgaySinh() {
        if (birthDate == null || birthDate.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(birthDate.trim());
        } catch (Exception e) {
            return null;
        }
    }

    public void setNgaySinh(LocalDate date) {
        this.birthDate = date != null ? date.toString() : null;
    }

    @Override
    public String toString() {
        return fullName + " (" + id + ")";
    }
}
