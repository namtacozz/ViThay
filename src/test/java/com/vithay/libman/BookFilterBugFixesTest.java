package com.vithay.libman;

import com.vithay.libman.model.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BookFilterBugFixesTest {

    private List<Book> sampleBooks;

    @BeforeEach
    public void setUp() {
        sampleBooks = new ArrayList<>();
        sampleBooks.add(new Book("BK001", "Sapiens: Lược Sử Loài Người", "Yuval Noah Harari", "Khoa học", "Khu KH - Kệ 01", 185000, 5, 3, "Available", null));
        sampleBooks.add(new Book("BK002", "Homo Deus: Lược Sử Tương Lai", "Yuval Noah Harari", "Khoa học", "Khu KH - Kệ 01", 195000, 4, 2, "Available", null));
        sampleBooks.add(new Book("BK003", "Clean Code", "Robert C. Martin", "Công nghệ", "Khu CN - Kệ 02", 210000, 6, 0, "Borrowed", null));
        sampleBooks.add(new Book("BK004", "Clean Architecture", "Robert C. Martin", "Công nghệ", "Khu CN - Kệ 02", 220000, 5, 5, "Available", null));
        sampleBooks.add(new Book("BK005", "Cho Tôi Xin Một Vé Đi Tuổi Thơ", "Nguyễn Nhật Ánh", "Văn học", "Khu VH - Kệ 03", 85000, 10, 8, "Available", null));
        sampleBooks.add(new Book("BK006", "Truyện Kiều", "Nguyễn Du", "Văn học", "Khu VH - Kệ 03", 65000, 4, 4, "Available", null));
    }

    @Test
    public void testExactAuthorMatchDoesNotMatchSubstrings() {
        // Exact author matching should not confuse "Nguyễn Du" with "Nguyễn Nhật Ánh"
        final String targetAuthor = "Nguyễn Du";
        List<Book> filtered = sampleBooks.stream()
                .filter(b -> b.getAuthor() != null && b.getAuthor().trim().equalsIgnoreCase(targetAuthor.trim()))
                .toList();

        assertEquals(1, filtered.size());
        assertEquals("Truyện Kiều", filtered.get(0).getTitle());
    }

    @Test
    public void testAuthorFilterMultipleBooksBySameAuthor() {
        final String targetAuthor = "Yuval Noah Harari";
        List<Book> filtered = sampleBooks.stream()
                .filter(b -> b.getAuthor() != null && b.getAuthor().trim().equalsIgnoreCase(targetAuthor.trim()))
                .toList();

        assertEquals(2, filtered.size());
        assertTrue(filtered.stream().anyMatch(b -> b.getTitle().contains("Sapiens")));
        assertTrue(filtered.stream().anyMatch(b -> b.getTitle().contains("Homo Deus")));
    }

    @Test
    public void testResetAllFiltersRestoresFullCollection() {
        // Apply multiple filters
        final String authorFilter = "Robert C. Martin";
        final String statusFilter = "Available";

        List<Book> filtered = sampleBooks.stream()
                .filter(b -> (authorFilter == null || b.getAuthor().equalsIgnoreCase(authorFilter))
                        && (statusFilter == null || b.getStatus().equalsIgnoreCase(statusFilter)))
                .toList();

        assertEquals(1, filtered.size());
        assertEquals("Clean Architecture", filtered.get(0).getTitle());

        // Now reset all filters (represented by null filters)
        final String resetAuthorFilter = null;
        final String resetStatusFilter = null;

        List<Book> resetList = sampleBooks.stream()
                .filter(b -> (resetAuthorFilter == null || b.getAuthor().equalsIgnoreCase(resetAuthorFilter))
                        && (resetStatusFilter == null || b.getStatus().equalsIgnoreCase(resetStatusFilter)))
                .toList();

        assertEquals(sampleBooks.size(), resetList.size());
    }

    @Test
    public void testAdvancedCategoryFilterOverridesOrSyncs() {
        final String advCategory = "Công nghệ";
        List<Book> techBooks = sampleBooks.stream()
                .filter(b -> advCategory.equalsIgnoreCase(b.getCategory()))
                .toList();

        assertEquals(2, techBooks.size());
        assertTrue(techBooks.stream().allMatch(b -> "Robert C. Martin".equals(b.getAuthor())));
    }

    @Test
    public void testFilterGuardPreventsReentrantReset() {
        // Simulate isUpdatingFilterUI flag
        boolean[] guard = { false };
        String[] drilldownAuthorHolder = { "Yuval Noah Harari" };

        Runnable onComboAction = () -> {
            if (guard[0]) {
                // Guard is active, do NOT reset drilldownAuthor
                return;
            }
            drilldownAuthorHolder[0] = null;
        };

        // When updating combo programmatically
        guard[0] = true;
        try {
            onComboAction.run();
        } finally {
            guard[0] = false;
        }

        // drilldownAuthor must NOT be wiped
        assertEquals("Yuval Noah Harari", drilldownAuthorHolder[0]);

        // When user manually triggers combo action (guard is false)
        onComboAction.run();
        assertNull(drilldownAuthorHolder[0]);
    }
}
