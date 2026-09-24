package com.vithay.libman;

import com.vithay.libman.dao.BookDao;
import com.vithay.libman.dao.DatabaseConfig;
import com.vithay.libman.model.Book;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.security.MessageDigest;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class BookCoversDistinctTest {

    private static BookDao bookDao;

    @BeforeAll
    public static void setUp() {
        DatabaseConfig.initializeDatabase();
        bookDao = new BookDao();
    }

    @Test
    public void testAllBooksHaveDistinctCoverImages() throws Exception {
        List<Book> books = bookDao.getAllBooks();
        assertNotNull(books, "Books list should not be null");
        assertTrue(books.size() >= 200, "Should have at least 200 books, found: " + books.size());

        Map<String, String> hashToBook = new HashMap<>();
        List<String> collisions = new ArrayList<>();

        for (Book b : books) {
            String coverPath = b.getCoverImage();
            assertNotNull(coverPath, "Book coverImage should not be null for: " + b.getId() + " - " + b.getTitle());
            assertFalse(coverPath.isBlank(), "Book coverImage should not be blank for: " + b.getId());

            try (InputStream is = getClass().getResourceAsStream(coverPath)) {
                assertNotNull(is, "Cover resource must exist on classpath: " + coverPath + " for " + b.getTitle());
                byte[] bytes = is.readAllBytes();
                assertTrue(bytes.length >= 15000, "Cover file must be an authentic high-resolution web image (>= 15KB): " + coverPath);

                MessageDigest md = MessageDigest.getInstance("MD5");
                byte[] digest = md.digest(bytes);
                StringBuilder sb = new StringBuilder();
                for (byte bt : digest) {
                    sb.append(String.format("%02x", bt));
                }
                String hash = sb.toString();

                if (hashToBook.containsKey(hash)) {
                    collisions.add("Collision between [" + b.getId() + " - " + b.getTitle() + "] and [" + hashToBook.get(hash) + "] on cover " + coverPath);
                } else {
                    hashToBook.put(hash, b.getId() + " - " + b.getTitle());
                }
            }
        }

        assertTrue(collisions.isEmpty(), "Found duplicate book cover images:\n" + String.join("\n", collisions));
    }
}
