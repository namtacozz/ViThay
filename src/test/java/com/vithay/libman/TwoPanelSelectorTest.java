package com.vithay.libman;

import com.vithay.libman.controller.BookManagementController;
import com.vithay.libman.model.Book;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TwoPanelSelectorTest {

    @Test
    public void testSelectedBookDetailsExtraction() {
        Book b = new Book("BK010", "Dế Mèn", "Tô Hoài", "TN", "Khu A - 03", 50000, 10, 8, "KHA_DUNG", "de_men.jpg");
        assertNotNull(b.getMaSach());
        assertEquals("Dế Mèn", b.getTenSach());
        assertEquals("Khu A - 03", b.getShelfLocation());
        assertEquals(8, b.getSoLuongConLai());
    }

    @Test
    public void testInspectorFormattingValues() {
        Book b = new Book("BK010", "Dế Mèn", "Tô Hoài", "TN", "Khu A - 03", 50000, 10, 8, "Available", "de_men.jpg");
        String copiesText = b.getAvailableCopies() + " / " + b.getTotalCopies() + " bản";
        assertEquals("8 / 10 bản", copiesText);

        String priceFormatted = String.format("%,.0f đ", b.getPrice());
        assertTrue(priceFormatted.endsWith("đ") && priceFormatted.contains("50") && priceFormatted.contains("000"));

        assertEquals("Available", b.getStatus());
        assertEquals("Khu A - 03", b.getShelfLocation());
        assertEquals("de_men.jpg", b.getImagePath());
    }

    @Test
    public void testDividerRatioConstant() {
        assertEquals(0.68, BookManagementController.DIVIDER_RATIO, 0.001);
    }

    @Test
    public void testInspectorBorrowAvailabilityLogic() {
        Book availableBook = new Book("BK001", "Book 1", "Author 1", "VH", "Khu A", 40000, 5, 2, "Available", null);
        assertTrue(availableBook.getAvailableCopies() > 0, "Book with available copies should enable quick borrow");

        Book outOfStockBook = new Book("BK002", "Book 2", "Author 2", "VH", "Khu A", 40000, 5, 0, "Borrowed", null);
        assertFalse(outOfStockBook.getAvailableCopies() > 0, "Out of stock book should disable quick borrow");
    }
}
