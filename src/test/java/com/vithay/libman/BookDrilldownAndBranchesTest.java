package com.vithay.libman;

import com.vithay.libman.dao.BorrowTransactionDao;
import com.vithay.libman.model.Book;
import com.vithay.libman.model.BorrowTransaction;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BookDrilldownAndBranchesTest {

    @Test
    public void testFindBooksOnSameShelf() {
        List<Book> books = new ArrayList<>();
        books.add(new Book("BK001", "Sách 1", "TG A", "VH", "Khu A - Kệ 01", 50000, 5, 2, "KHA_DUNG", null));
        books.add(new Book("BK002", "Sách 2", "TG B", "VH", "Khu A - Kệ 01", 60000, 3, 1, "KHA_DUNG", null));
        books.add(new Book("BK003", "Sách 3", "TG C", "TN", "Khu B - Kệ 02", 70000, 4, 4, "KHA_DUNG", null));

        Book current = books.get(0);
        List<Book> sameShelf = books.stream()
                .filter(b -> !b.getMaSach().equals(current.getMaSach()) && b.getShelfLocation().equalsIgnoreCase(current.getShelfLocation()))
                .toList();

        assertEquals(1, sameShelf.size());
        assertEquals("BK002", sameShelf.get(0).getMaSach());
    }

    @Test
    public void testFindBooksBySameAuthor() {
        List<Book> books = new ArrayList<>();
        books.add(new Book("BK001", "Tôi Thấy Hoa Vàng Trên Cỏ Xanh", "Nguyễn Nhật Ánh", "VH", "Khu A - Kệ 01", 85000, 5, 3, "Available", null));
        books.add(new Book("BK002", "Cho Tôi Xin Một Vé Đi Tuổi Thơ", "Nguyễn Nhật Ánh", "VH", "Khu A - Kệ 02", 90000, 4, 1, "Available", null));
        books.add(new Book("BK003", "Dế Mèn Phiêu Lưu Ký", "Tô Hoài", "VH", "Khu B - Kệ 01", 55000, 6, 4, "Available", null));

        Book current = books.get(0);
        List<Book> sameAuthor = books.stream()
                .filter(b -> !b.getMaSach().equals(current.getMaSach()) && b.getTenTacGia().equalsIgnoreCase(current.getTenTacGia()))
                .toList();

        assertEquals(1, sameAuthor.size());
        assertEquals("BK002", sameAuthor.get(0).getMaSach());
        assertEquals("Cho Tôi Xin Một Vé Đi Tuổi Thơ", sameAuthor.get(0).getTenSach());
    }

    @Test
    public void testAdvancedFilterMatchingLogic() {
        List<Book> books = new ArrayList<>();
        books.add(new Book("BK001", "Sách Toán 1", "TG 1", "Toán học", "Khu A - Kệ 01", 50000, 5, 3, "Available", null));
        books.add(new Book("BK002", "Sách Toán 2", "TG 2", "Toán học", "Khu A - Kệ 01", 120000, 4, 0, "Borrowed", null));
        books.add(new Book("BK003", "Sách Văn 1", "TG 3", "Văn học", "Khu B - Kệ 02", 75000, 6, 2, "Available", null));

        // Filter: Category "Toán học", Shelf "Khu A - Kệ 01", MinPrice 40000, MaxPrice 100000, StockStatus "Còn sách"
        Double minPrice = 40000.0;
        Double maxPrice = 100000.0;
        String stockFilter = "Còn sách";
        String shelfFilter = "Khu A - Kệ 01";
        String catFilter = "Toán học";

        List<Book> filtered = books.stream().filter(b -> {
            boolean matchCat = catFilter.equalsIgnoreCase(b.getCategory());
            boolean matchShelf = shelfFilter.equalsIgnoreCase(b.getShelfLocation());
            boolean matchMin = minPrice == null || b.getPrice() >= minPrice;
            boolean matchMax = maxPrice == null || b.getPrice() <= maxPrice;
            boolean matchStock = true;
            if ("Còn sách".equalsIgnoreCase(stockFilter)) {
                matchStock = b.getAvailableCopies() > 0;
            } else if ("Hết sách".equalsIgnoreCase(stockFilter)) {
                matchStock = b.getAvailableCopies() <= 0;
            }
            return matchCat && matchShelf && matchMin && matchMax && matchStock;
        }).toList();

        assertEquals(1, filtered.size());
        assertEquals("BK001", filtered.get(0).getId());
    }

    @Test
    public void testBorrowTransactionDaoGetByBook() {
        BorrowTransactionDao dao = new BorrowTransactionDao();
        List<BorrowTransaction> list = dao.getTransactionsByBook("BK001");
        assertNotNull(list);
    }

    @Test
    public void testBreadcrumbPathFormation() {
        String root = "Kho Sách";
        String category = "Văn học";
        String shelf = "Khu A - Kệ 01";
        String author = "Nam Cao";
        String bookTitle = "Chí Phèo";

        String path1 = root + " > " + "Thể loại: " + category + " > " + "Chi tiết: " + bookTitle;
        assertEquals("Kho Sách > Thể loại: Văn học > Chi tiết: Chí Phèo", path1);

        String path2 = root + " > " + "Kệ: " + shelf + " > " + "Chi tiết: " + bookTitle + " > Lịch sử lưu hành";
        assertEquals("Kho Sách > Kệ: Khu A - Kệ 01 > Chi tiết: Chí Phèo > Lịch sử lưu hành", path2);

        String path3 = root + " > " + "Tác giả: " + author + " > " + "Chi tiết: " + bookTitle;
        assertEquals("Kho Sách > Tác giả: Nam Cao > Chi tiết: Chí Phèo", path3);
    }

    @Test
    public void testStockStatusFilterConditions() {
        Book inStock = new Book("BK001", "Book A", "Author A", "VH", "Khu A", 50000, 5, 2, "Available", null);
        Book outOfStock = new Book("BK002", "Book B", "Author B", "VH", "Khu A", 60000, 3, 0, "Borrowed", null);

        assertTrue(inStock.getAvailableCopies() > 0);
        assertFalse(outOfStock.getAvailableCopies() > 0);
        assertEquals(0, outOfStock.getAvailableCopies());
    }
}
