package com.vithay.libman;

import com.vithay.libman.model.Book;
import com.vithay.libman.model.BookReview;
import com.vithay.libman.service.BookDiscussionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BookDiscussionServiceTest {

    private BookDiscussionService discussionService;

    @BeforeEach
    public void setUp() {
        discussionService = BookDiscussionService.getInstance();
    }

    @Test
    public void testGetBookDescriptionKnownBook() {
        Book book = new Book("B001", "Sapiens: Lược Sử Loài Người", "Yuval Noah Harari", "Lịch sử & Địa lý", "Khu L - Kệ 01", 189000, 6, 5, "Available", null);
        String desc = discussionService.getBookDescription(book);
        assertNotNull(desc);
        assertTrue(desc.contains("Sapiens") && desc.contains("Yuval Noah Harari"));
    }

    @Test
    public void testGetBookDescriptionFallback() {
        Book book = new Book("CUSTOM_999", "Cuốn Sách Mới", "Tác Giả Mới", "Khoa học", "Khu X - Kệ 05", 99000, 3, 3, "Available", null);
        String desc = discussionService.getBookDescription(book);
        assertNotNull(desc);
        assertTrue(desc.contains("Cuốn Sách Mới"));
        assertTrue(desc.contains("Tác Giả Mới"));
    }

    @Test
    public void testGetReviewsForBook() {
        List<BookReview> reviews = discussionService.getReviewsForBook("B001");
        assertNotNull(reviews);
        assertFalse(reviews.isEmpty());
        assertTrue(reviews.stream().anyMatch(r -> r.getReaderName().contains("Trần Văn An")));
    }

    @Test
    public void testAddNewReview() {
        String bookId = "BK010";
        int initialCount = discussionService.getReviewsForBook(bookId).size();

        BookReview newReview = new BookReview(
                "TEST_REV_1",
                bookId,
                "Độc Giả Thử Nghiệm",
                "/avatar.png",
                5,
                "2026-09-23",
                "Đánh giá thử nghiệm chất lượng mã nguồn rất tốt!"
        );
        discussionService.addReview(newReview);

        List<BookReview> updatedReviews = discussionService.getReviewsForBook(bookId);
        assertEquals(initialCount + 1, updatedReviews.size());
        assertEquals("TEST_REV_1", updatedReviews.get(0).getId());
        assertEquals("★★★★★", updatedReviews.get(0).getRatingStars());
    }
}
