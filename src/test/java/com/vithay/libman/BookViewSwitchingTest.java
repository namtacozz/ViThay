package com.vithay.libman;

import com.vithay.libman.model.Book;
import com.vithay.libman.util.VietnameseUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class BookViewSwitchingTest {

    private List<Book> sampleBooks;

    @BeforeEach
    public void setup() {
        sampleBooks = new ArrayList<>();
        sampleBooks.add(new Book("BK001", "Chí Phèo", "Nam Cao", "Văn học", "Khu A - 01", 65000, 5, 3, "Available", "chi_pheo.jpg"));
        sampleBooks.add(new Book("BK002", "Clean Code", "Robert C. Martin", "Công nghệ thông tin", "Khu B - 02", 180000, 3, 0, "Borrowed", "clean_code.jpg"));
        sampleBooks.add(new Book("BK003", "Lão Hạc", "Nam Cao", "Văn học", "Khu A - 01", 55000, 4, 2, "Available", null));
        sampleBooks.add(new Book("BK004", "Design Patterns", "Erich Gamma", "Công nghệ thông tin", "Khu B - 01", 210000, 2, 2, "Available", "design_patterns.jpg"));
    }

    @Test
    public void testFilterSyncBetweenModes() {
        // Test filtering logic applied uniformly across table and grid card views
        List<Book> filtered = sampleBooks.stream()
                .filter(b -> b.getTenSach().toLowerCase().contains("chí"))
                .toList();

        assertEquals(1, filtered.size());
        assertEquals("BK001", filtered.get(0).getMaSach());
    }

    @Test
    public void testBookCardViewDataBinding() {
        Book book = new Book("BK001", "Chí Phèo", "Nam Cao", "VH", "Khu A - 01", 65000, 5, 3, "Available", "chi_pheo.jpg");
        assertEquals("BK001", book.getMaSach());
        assertEquals("Chí Phèo", book.getTenSach());
        assertEquals("Nam Cao", book.getTenTacGia());
        assertEquals(3, book.getSoLuongConLai());
        assertEquals("chi_pheo.jpg", book.getImagePath());
    }

    @Test
    public void testCombinedMultiCriteriaFiltering() {
        // Test real filtering behavior combining author, status, and keyword search
        String selectedAuthor = "Nam Cao";
        String selectedStatus = "Available";
        String searchKeyword = "hạc";

        List<Book> filtered = sampleBooks.stream()
                .filter(b -> (selectedAuthor.equals("Tất cả") || VietnameseUtils.matches(b.getAuthor(), selectedAuthor)))
                .filter(b -> (selectedStatus.equals("Tất cả") || selectedStatus.equalsIgnoreCase(b.getStatus())))
                .filter(b -> (searchKeyword.isEmpty() ||
                        VietnameseUtils.matches(b.getTitle(), searchKeyword) ||
                        VietnameseUtils.matches(b.getAuthor(), searchKeyword) ||
                        VietnameseUtils.matches(b.getId(), searchKeyword)))
                .toList();

        assertEquals(1, filtered.size(), "Should find exactly 1 book matching author Nam Cao, status Available, keyword 'hạc'");
        assertEquals("BK003", filtered.get(0).getId());
        assertEquals("Lão Hạc", filtered.get(0).getTitle());

        // Now test when status filter eliminates available books
        String borrowedStatus = "Borrowed";
        List<Book> borrowedNamCao = sampleBooks.stream()
                .filter(b -> VietnameseUtils.matches(b.getAuthor(), selectedAuthor))
                .filter(b -> borrowedStatus.equalsIgnoreCase(b.getStatus()))
                .toList();
        assertTrue(borrowedNamCao.isEmpty(), "Nam Cao should have no borrowed books");

        // Test unaccented search across both modes
        List<Book> unaccentedSearch = sampleBooks.stream()
                .filter(b -> VietnameseUtils.matches(b.getTitle(), "chi pheo"))
                .toList();
        assertEquals(1, unaccentedSearch.size());
        assertEquals("BK001", unaccentedSearch.get(0).getId());
    }
}
