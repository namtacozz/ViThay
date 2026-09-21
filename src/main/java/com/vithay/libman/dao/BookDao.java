package com.vithay.libman.dao;

import com.vithay.libman.model.Book;
import com.vithay.libman.util.VietnameseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BookDao {
    private static final Logger logger = LoggerFactory.getLogger(BookDao.class);

    public List<Book> getAllBooks() {
        List<Book> list = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE is_deleted = 0 ORDER BY id ASC";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            logger.error("Error retrieving active books", e);
        }
        return list;
    }

    public List<Book> getDeletedBooks() {
        List<Book> list = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE is_deleted = 1 ORDER BY id ASC";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            logger.error("Error retrieving deleted books", e);
        }
        return list;
    }

    public Book getBookById(String id) {
        String sql = "SELECT * FROM books WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSet(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Error retrieving book by id: {}", id, e);
        }
        return null;
    }

    public List<Book> searchBooks(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllBooks();
        }
        String kw = keyword.trim();
        List<Book> all = getAllBooks();
        List<Book> matched = new ArrayList<>();
        for (Book b : all) {
            if (VietnameseUtils.matches(b.getTitle(), kw) ||
                VietnameseUtils.matches(b.getAuthor(), kw) ||
                VietnameseUtils.matches(b.getCategory(), kw) ||
                VietnameseUtils.matches(b.getShelfLocation(), kw) ||
                VietnameseUtils.matches(b.getIsbn(), kw) ||
                VietnameseUtils.matches(b.getId(), kw)) {
                matched.add(b);
            }
        }
        return matched;
    }

    public boolean addBook(Book book) {
        String sql = "INSERT INTO books (id, title, author, category, category_id, shelf_location, isbn, price, publish_year, publisher, status, cover_image, total_copies, available_copies, is_deleted) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, book.getId());
            ps.setString(2, book.getTitle());
            ps.setString(3, book.getAuthor());
            ps.setString(4, book.getCategory());
            ps.setInt(5, book.getCategoryId());
            ps.setString(6, book.getShelfLocation());
            ps.setString(7, book.getIsbn());
            ps.setDouble(8, book.getPrice());
            ps.setInt(9, book.getPublishYear());
            ps.setString(10, book.getPublisher());
            ps.setString(11, book.getStatus());
            ps.setString(12, book.getCoverImage());
            ps.setInt(13, book.getTotalCopies());
            ps.setInt(14, book.getAvailableCopies());
            ps.setInt(15, book.isDeleted() ? 1 : 0);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error adding book: {}", book.getId(), e);
            return false;
        }
    }

    public boolean updateBook(Book book) {
        String sql = "UPDATE books SET title = ?, author = ?, category = ?, category_id = ?, shelf_location = ?, isbn = ?, price = ?, publish_year = ?, " +
                     "publisher = ?, status = ?, cover_image = ?, total_copies = ?, available_copies = ?, is_deleted = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, book.getTitle());
            ps.setString(2, book.getAuthor());
            ps.setString(3, book.getCategory());
            ps.setInt(4, book.getCategoryId());
            ps.setString(5, book.getShelfLocation());
            ps.setString(6, book.getIsbn());
            ps.setDouble(7, book.getPrice());
            ps.setInt(8, book.getPublishYear());
            ps.setString(9, book.getPublisher());
            ps.setString(10, book.getStatus());
            ps.setString(11, book.getCoverImage());
            ps.setInt(12, book.getTotalCopies());
            ps.setInt(13, book.getAvailableCopies());
            ps.setInt(14, book.isDeleted() ? 1 : 0);
            ps.setString(15, book.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error updating book: {}", book.getId(), e);
            return false;
        }
    }

    public boolean updateBookStatus(String id, String status, int availableCopies) {
        String sql = "UPDATE books SET status = ?, available_copies = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, availableCopies);
            ps.setString(3, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error updating status for book: {}", id, e);
            return false;
        }
    }

    public boolean softDeleteBook(String id) {
        String sql = "UPDATE books SET is_deleted = 1 WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error soft-deleting book: {}", id, e);
            return false;
        }
    }

    public boolean restoreBook(String id) {
        String sql = "UPDATE books SET is_deleted = 0 WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error restoring book: {}", id, e);
            return false;
        }
    }

    public boolean permanentDeleteBook(String id) {
        String sql = "DELETE FROM books WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error permanently deleting book: {}", id, e);
            return false;
        }
    }

    private Book mapResultSet(ResultSet rs) throws SQLException {
        return new Book(
                rs.getString("id"),
                rs.getString("title"),
                rs.getString("author"),
                rs.getString("category"),
                rs.getInt("category_id"),
                rs.getString("shelf_location"),
                rs.getString("isbn"),
                rs.getDouble("price"),
                rs.getInt("publish_year"),
                rs.getString("publisher"),
                rs.getString("status"),
                rs.getString("cover_image"),
                rs.getInt("total_copies"),
                rs.getInt("available_copies"),
                rs.getInt("is_deleted") == 1
        );
    }
}
