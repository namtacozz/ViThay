package com.vithay.libman.model;

public class Book {
    private String id;
    private String title;
    private String author;
    private String category;
    private int categoryId;
    private String shelfLocation;
    private String isbn;
    private double price;
    private int publishYear;
    private String publisher;
    private String status; // Available, Borrowed, On Hold
    private String coverImage;
    private int totalCopies;
    private int availableCopies;
    private boolean isDeleted;

    public Book() {
    }

    public Book(String id, String title, String author, String category, int categoryId,
                String shelfLocation, String isbn, double price, int publishYear,
                String publisher, String status, String coverImage,
                int totalCopies, int availableCopies, boolean isDeleted) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.category = category;
        this.categoryId = categoryId;
        this.shelfLocation = shelfLocation != null ? shelfLocation : "Khu A - Kệ 01";
        this.isbn = isbn;
        this.price = price;
        this.publishYear = publishYear;
        this.publisher = publisher;
        this.status = status;
        this.coverImage = coverImage;
        this.totalCopies = totalCopies;
        this.availableCopies = availableCopies;
        this.isDeleted = isDeleted;
    }

    public Book(String id, String title, String author, String category, String isbn,
                double price, int publishYear, String publisher, String status,
                String coverImage, int totalCopies, int availableCopies) {
        this(id, title, author, category, 0, "Khu A - Kệ 01", isbn, price, publishYear, publisher, status, coverImage, totalCopies, availableCopies, false);
    }

    public Book(String id, String title, String author, String category,
                String shelfLocation, double price, int totalCopies, int availableCopies,
                String status, String coverImage) {
        this(id, title, author, category, 0, shelfLocation, "", price, 2024, "NXB", status, coverImage, totalCopies, availableCopies, false);
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public String getShelfLocation() { return shelfLocation; }
    public void setShelfLocation(String shelfLocation) { this.shelfLocation = shelfLocation; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getPublishYear() { return publishYear; }
    public void setPublishYear(int publishYear) { this.publishYear = publishYear; }

    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCoverImage() { return coverImage; }
    public void setCoverImage(String coverImage) { this.coverImage = coverImage; }

    public int getTotalCopies() { return totalCopies; }
    public void setTotalCopies(int totalCopies) { this.totalCopies = totalCopies; }

    public int getAvailableCopies() { return availableCopies; }
    public void setAvailableCopies(int availableCopies) { this.availableCopies = availableCopies; }

    public boolean isDeleted() { return isDeleted; }
    public void setDeleted(boolean deleted) { isDeleted = deleted; }

    public String getMaSach() { return id; }
    public String getTenSach() { return title; }
    public String getTenTacGia() { return author; }
    public int getSoLuongConLai() { return availableCopies; }
    public String getImagePath() {
        if (coverImage != null && coverImage.startsWith("/com/vithay/libman/images/")) {
            return coverImage.substring("/com/vithay/libman/images/".length());
        }
        return coverImage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return id != null && id.equalsIgnoreCase(book.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.toLowerCase().hashCode() : 0;
    }

    @Override
    public String toString() {
        return title + " - " + author + " (" + shelfLocation + ")";
    }
}
