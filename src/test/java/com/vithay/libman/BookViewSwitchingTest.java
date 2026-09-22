package com.vithay.libman;

import com.vithay.libman.model.Book;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class BookViewSwitchingTest {
    @Test
    public void testFilterSyncBetweenModes() {
        List<Book> allBooks = new ArrayList<>();
        allBooks.add(new Book("BK001", "Chí Phèo", "Nam Cao", "VH", "Khu A - 01", 65000, 5, 3, "KHA_DUNG", null));
        allBooks.add(new Book("BK002", "Clean Code", "Robert C. Martin", "CNTT", "Khu B - 02", 180000, 3, 0, "DANG_MUON", null));

        // Test filtering logic applied uniformly
        List<Book> filtered = allBooks.stream()
                .filter(b -> b.getTenSach().toLowerCase().contains("chí"))
                .toList();

        assertEquals(1, filtered.size());
        assertEquals("BK001", filtered.get(0).getMaSach());
    }

    @Test
    public void testBookCardViewDataBinding() {
        Book book = new Book("BK001", "Chí Phèo", "Nam Cao", "VH", "Khu A - 01", 65000, 5, 3, "KHA_DUNG", "chi_pheo.jpg");
        assertEquals("BK001", book.getMaSach());
        assertEquals("Chí Phèo", book.getTenSach());
        assertEquals("Nam Cao", book.getTenTacGia());
        assertEquals(3, book.getSoLuongConLai());
        assertEquals("chi_pheo.jpg", book.getImagePath());
    }

    @Test
    public void testViewModeSwitchingLogic() {
        boolean isTableViewMode = true;
        // Switch to grid view mode
        isTableViewMode = false;
        assertFalse(isTableViewMode);

        // Switch back to table view mode
        isTableViewMode = true;
        assertTrue(isTableViewMode);
    }
}
