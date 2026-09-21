package com.vithay.libman;

import com.vithay.libman.dao.*;
import com.vithay.libman.model.*;
import com.vithay.libman.service.*;
import com.vithay.libman.util.VietnameseUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class LibManTest {

    @BeforeAll
    public static void setup() {
        DatabaseConfig.initializeDatabase();
    }

    @Test
    public void testSeedBooksLoaded() {
        BookDao bookDao = new BookDao();
        List<Book> books = bookDao.getAllBooks();
        assertNotNull(books);
        assertTrue(books.size() >= 8, "Database must contain both mockup books and SRS sample books");

        // Verify SRS specific book
        Book srsBook = bookDao.getBookById("BK001");
        assertNotNull(srsBook);
        assertEquals("Tư Tưởng Hồ Chí Minh", srsBook.getTitle());
        assertEquals("Khu A - Kệ 01", srsBook.getShelfLocation());
        assertFalse(srsBook.isDeleted());
    }

    @Test
    public void testSeedCategoriesLoaded() {
        CategoryDao catDao = new CategoryDao();
        List<Category> categories = catDao.getAllCategories();
        assertNotNull(categories);
        assertTrue(categories.size() >= 3, "Database must contain categories from SRS");

        Category c1 = catDao.getCategoryById(1);
        assertNotNull(c1);
        assertTrue(c1.getName().startsWith("Lý luận chính trị"), "Category 1 should match political philosophy");
    }

    @Test
    public void testSeedReadersLoaded() {
        ReaderDao readerDao = new ReaderDao();
        List<Reader> readers = readerDao.getAllReaders();
        assertNotNull(readers);
        assertTrue(readers.size() >= 4);

        Reader r1 = readerDao.getReaderById("DG001");
        assertNotNull(r1);
        assertEquals("Trần Văn An", r1.getFullName());
        assertEquals("Active", r1.getStatus());
        assertNotNull(r1.getCardExpiryDate());
    }

    @Test
    public void testAuthAndRoles() {
        AuthService auth = AuthService.getInstance();
        assertTrue(auth.login("quantri", "admin123"));
        assertTrue(auth.isAdmin());
        assertTrue(auth.canConfigureSettings());

        assertTrue(auth.login("thuthu", "123456"));
        assertTrue(auth.isLibrarian());
        assertFalse(auth.canConfigureSettings());

        assertTrue(auth.login("docgia", "123456"));
        assertTrue(auth.isReader());
        assertFalse(auth.canManageBooks());
        assertFalse(auth.canBorrowReturn());
    }

    @Test
    public void testCardIssuanceWorkflow() {
        ReaderDao readerDao = new ReaderDao();
        ReaderService readerService = new ReaderService();
        BorrowService borrowService = new BorrowService();

        // DG005 is registered online with status "Chờ Cấp Thẻ"
        Reader pendingReader = readerDao.getReaderById("DG005");
        assertNotNull(pendingReader);
        assertEquals("Chờ Cấp Thẻ", pendingReader.getStatus());

        // Attempting to borrow before card issuance must fail
        String borrowError = borrowService.borrowBook("DG005", "BK001", "Mang về nhà", 14, "Mượn thử");
        assertNotNull(borrowError);
        assertTrue(borrowError.contains("chưa được cấp thẻ thư viện tại quầy"));

        // Librarian verifies reader ID and issues physical card at counter
        String today = LocalDate.now().toString();
        String expiry = LocalDate.now().plusYears(1).toString();
        boolean issued = readerService.issueCard("DG005", today, expiry);
        assertTrue(issued, "Card issuance should succeed");

        // Verify status transitioned to Active
        Reader activeReader = readerDao.getReaderById("DG005");
        assertEquals("Active", activeReader.getStatus());
        assertEquals(today, activeReader.getCardIssueDate());
        assertEquals(expiry, activeReader.getCardExpiryDate());
    }

    @Test
    public void testSystemSettings() {
        SettingService settings = SettingService.getInstance();
        assertEquals(5, settings.getMaxBooksPerReader());
        assertEquals(14, settings.getMaxBorrowDaysHome());
        assertEquals(1, settings.getMaxBorrowDaysOnsite());
        assertEquals(2000.0, settings.getFinePerDay());

        settings.updateSetting("fine_per_day", "3000");
        assertEquals(3000.0, settings.getFinePerDay());
        // Revert back
        settings.updateSetting("fine_per_day", "2000");
    }

    @Test
    public void testSoftDeleteAndRestore() {
        BookService bookService = new BookService();
        Book book = new Book("TEST01", "Sách Thử Nghiệm", "Tác Giả A", "Công nghệ thông tin", 9, "Khu T - Kệ 01",
                "123-456", 100000, 2024, "NXB Trẻ", "Available", null, 3, 3, false);
        bookService.saveBook(book);

        // Soft delete
        assertTrue(bookService.softDeleteBook("TEST01"));
        assertNull(new BookDao().getBookById("TEST01") != null && !new BookDao().getBookById("TEST01").isDeleted() ? new BookDao().getBookById("TEST01") : null);

        // Check in deleted list
        List<Book> deleted = bookService.getDeletedBooks();
        assertTrue(deleted.stream().anyMatch(b -> "TEST01".equals(b.getId())));

        // Restore
        assertTrue(bookService.restoreBook("TEST01"));
        assertNotNull(bookService.getBookById("TEST01"));

        // Clean up
        assertTrue(bookService.permanentDeleteBook("TEST01"));
    }

    @Test
    public void testBorrowTypesAndFines() {
        BorrowService service = new BorrowService();
        LocalDate returnDate = LocalDate.parse("2024-03-20");
        String dueDate = "2024-03-15"; // 5 days overdue

        double fine = service.calculateOverdueFine(dueDate, returnDate);
        assertEquals(5 * 2000.0, fine, "Fine should use 2,000 VND per day");
    }

    @Test
    public void testVietnameseSearch() {
        BookService bookService = new BookService();

        // 1. Accented search
        List<Book> res1 = bookService.searchBooks("tư tưởng");
        assertFalse(res1.isEmpty(), "Search 'tư tưởng' must find 'Tư Tưởng Hồ Chí Minh'");
        assertTrue(res1.stream().anyMatch(b -> b.getTitle().contains("Tư Tưởng")));

        // 2. Unaccented search
        List<Book> res2 = bookService.searchBooks("tu tuong");
        assertFalse(res2.isEmpty(), "Unaccented search 'tu tuong' must find 'Tư Tưởng Hồ Chí Minh'");
        assertTrue(res2.stream().anyMatch(b -> b.getTitle().contains("Tư Tưởng")));

        // 3. Special Vietnamese 'đ' / 'd'
        List<Book> res3 = bookService.searchBooks("dac nhan tam");
        assertFalse(res3.isEmpty(), "Unaccented search 'dac nhan tam' must find 'Đắc Nhân Tâm'");
        assertTrue(res3.stream().anyMatch(b -> b.getTitle().contains("Đắc Nhân Tâm")));

        List<Book> res4 = bookService.searchBooks("so do");
        assertFalse(res4.isEmpty(), "Unaccented search 'so do' must find 'Số Đỏ'");
        assertTrue(res4.stream().anyMatch(b -> b.getTitle().contains("Số Đỏ")));

        // 4. Category matching with accents
        List<Book> res5 = bookService.searchBooks("ngoại ngữ");
        assertFalse(res5.isEmpty(), "Search 'ngoại ngữ' must find books in 'Ngoại ngữ & Từ điển'");
    }

    @Test
    public void testProfileUpdateAndChangePassword() {
        AuthService auth = AuthService.getInstance();
        UserDao userDao = new UserDao();

        // Login as docgia
        assertTrue(auth.login("docgia", "123456"));
        User user = auth.getCurrentUser();
        assertNotNull(user);

        // Update profile
        boolean updated = auth.updateProfile("Trần Văn An Mới", "an.new@gmail.com", "0999888777");
        assertTrue(updated, "Profile update should succeed");
        assertEquals("Trần Văn An Mới", auth.getCurrentUser().getFullName());
        assertEquals("an.new@gmail.com", auth.getCurrentUser().getEmail());

        // Change password
        boolean wrongPass = auth.changePassword("wrongpass", "654321");
        assertFalse(wrongPass, "Changing with wrong password should fail");

        boolean passChanged = auth.changePassword("123456", "654321");
        assertTrue(passChanged, "Password change should succeed");

        // Verify login with new password
        auth.logout();
        assertNull(auth.getCurrentUser());
        assertTrue(auth.login("docgia", "654321"));

        // Revert password back for test repeatability
        assertTrue(auth.changePassword("654321", "123456"));
    }
}
