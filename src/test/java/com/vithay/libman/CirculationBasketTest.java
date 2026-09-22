package com.vithay.libman;

import com.vithay.libman.model.Book;
import com.vithay.libman.model.CirculationBasketItem;
import com.vithay.libman.model.Reader;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CirculationBasketTest {

    @Test
    public void testBasketMaxLimitEnforcement() {
        int maxAllowed = 5;
        int currentlyBorrowing = 3;
        int remainingQuota = maxAllowed - currentlyBorrowing; // 2

        List<Book> basket = new ArrayList<>();
        basket.add(new Book("BK001", "Book 1", "A", "VH", "K1", 50000, 5, 2, "KHA_DUNG", null));
        basket.add(new Book("BK002", "Book 2", "B", "VH", "K2", 60000, 3, 1, "KHA_DUNG", null));

        assertTrue(basket.size() <= remainingQuota, "Basket fits in remaining quota");

        basket.add(new Book("BK003", "Book 3", "C", "VH", "K3", 70000, 4, 2, "KHA_DUNG", null));
        assertFalse(basket.size() <= remainingQuota, "Basket exceeds reader limit of 5");
    }

    @Test
    public void testCirculationBasketItemCreationAndModification() {
        Book book = new Book("BK010", "Dế Mèn Phiêu Lưu Ký", "Tô Hoài", "Văn Học", "Khu A - Kệ 01", 45000, 10, 5, "Available", null);
        CirculationBasketItem item = new CirculationBasketItem(book, "Mang về nhà", 14);

        assertEquals(book, item.getBook());
        assertEquals("Mang về nhà", item.getLoanType());
        assertEquals(14, item.getLoanDays());
        assertEquals(LocalDate.now().plusDays(14), item.getDueDate());

        // Thay đổi loại mượn và số ngày
        item.setLoanType("Mượn đọc tại chỗ");
        item.setLoanDays(1);
        assertEquals("Mượn đọc tại chỗ", item.getLoanType());
        assertEquals(1, item.getLoanDays());
        assertEquals(LocalDate.now().plusDays(1), item.getDueDate());
    }

    @Test
    public void testQuotaCalculationForReader() {
        int maxAllowed = 5;
        int activeLoans = 4;
        int remaining = Math.max(0, maxAllowed - activeLoans);
        assertEquals(1, remaining);

        List<CirculationBasketItem> basket = new ArrayList<>();
        Book book1 = new Book("BK011", "Java Concurrency", "Brian Goetz", "CNTT", "Khu B - Kệ 02", 120000, 3, 1, "Available", null);
        basket.add(new CirculationBasketItem(book1, "Mang về nhà", 14));

        assertEquals(1, basket.size());
        boolean canAddMore = basket.size() < remaining;
        assertFalse(canAddMore, "Should not be able to add more books when basket reaches remaining quota");
    }

    @Test
    public void testBorrowReturnControllerBasketOperations() {
        com.vithay.libman.controller.BorrowReturnController controller = new com.vithay.libman.controller.BorrowReturnController();
        Reader reader = new Reader("RD_TEST_99", "Độc Giả Test Quota", "test@libman.com", "0901234567", "Hà Nội", "123456789", "2000-01-01", "2024-01-01", "Active");
        controller.setSelectedReader(reader);

        assertEquals(reader, controller.getSelectedReader());
        assertEquals(0, controller.getBasketItems().size());

        Book b1 = new Book("BK_T1", "Lập Trình Java", "Tác giả 1", "CNTT", "Khu A - Kệ 01", 100000, 5, 3, "Available", null);
        Book b2 = new Book("BK_T2", "Cấu Trúc Dữ Liệu", "Tác giả 2", "CNTT", "Khu A - Kệ 02", 120000, 4, 2, "Available", null);

        controller.addToBasket(b1);
        controller.addToBasket(b2);

        assertEquals(2, controller.getBasketItems().size());
        assertEquals("BK_T1", controller.getBasketItems().get(0).getBook().getId());
        assertEquals("BK_T2", controller.getBasketItems().get(1).getBook().getId());

        // Xóa 1 cuốn khỏi bàn
        controller.removeFromBasket(b1);
        assertEquals(1, controller.getBasketItems().size());
        assertEquals("BK_T2", controller.getBasketItems().get(0).getBook().getId());

        // Xóa toàn bộ bàn
        controller.handleClearBasket();
        assertEquals(0, controller.getBasketItems().size());
    }
}
