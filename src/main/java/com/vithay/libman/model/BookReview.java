package com.vithay.libman.model;

/**
 * Model đại diện cho một đánh giá / bình luận thảo luận của độc giả về một cuốn sách.
 */
public class BookReview {
    private String id;
    private String bookId;
    private String readerName;
    private String avatar;
    private int rating; // 1 - 5 sao
    private String date;
    private String content;

    public BookReview() {}

    public BookReview(String id, String bookId, String readerName, String avatar, int rating, String date, String content) {
        this.id = id;
        this.bookId = bookId;
        this.readerName = readerName;
        this.avatar = avatar != null && !avatar.isBlank() ? avatar : "/com/vithay/libman/images/avatar.png";
        this.rating = Math.max(1, Math.min(5, rating));
        this.date = date;
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getReaderName() {
        return readerName;
    }

    public void setReaderName(String readerName) {
        this.readerName = readerName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = Math.max(1, Math.min(5, rating));
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getRatingStars() {
        return "★".repeat(rating) + "☆".repeat(5 - rating);
    }
}
