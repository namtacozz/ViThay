package com.vithay.libman.dao;

import com.vithay.libman.model.BorrowTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BorrowTransactionDao {
    private static final Logger logger = LoggerFactory.getLogger(BorrowTransactionDao.class);

    public List<BorrowTransaction> getAllTransactions() {
        List<BorrowTransaction> list = new ArrayList<>();
        String sql = "SELECT * FROM borrow_transactions ORDER BY borrow_date DESC, id DESC";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            logger.error("Error retrieving all transactions", e);
        }
        return list;
    }

    public List<BorrowTransaction> getRecentTransactions(int limit) {
        List<BorrowTransaction> list = new ArrayList<>();
        String sql = "SELECT * FROM borrow_transactions ORDER BY borrow_date DESC, id DESC LIMIT ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSet(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error retrieving recent transactions", e);
        }
        return list;
    }

    public List<BorrowTransaction> getTransactionsByReader(String readerId) {
        List<BorrowTransaction> list = new ArrayList<>();
        String sql = "SELECT * FROM borrow_transactions WHERE reader_id = ? ORDER BY borrow_date DESC";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, readerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSet(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error retrieving transactions for reader: {}", readerId, e);
        }
        return list;
    }

    public int getActiveBorrowCountForReader(String readerId) {
        String sql = "SELECT COUNT(*) FROM borrow_transactions WHERE reader_id = ? AND status = 'Đang Mượn'";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, readerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            logger.error("Error counting active transactions for reader: {}", readerId, e);
        }
        return 0;
    }

    public BorrowTransaction getTransactionById(String id) {
        String sql = "SELECT * FROM borrow_transactions WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSet(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Error retrieving transaction by id: {}", id, e);
        }
        return null;
    }

    public boolean addTransaction(BorrowTransaction tx) {
        String sql = "INSERT INTO borrow_transactions (id, reader_id, reader_name, book_id, book_title, borrow_date, due_date, return_date, borrow_type, status, fine_amount, notes) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tx.getId());
            ps.setString(2, tx.getReaderId());
            ps.setString(3, tx.getReaderName());
            ps.setString(4, tx.getBookId());
            ps.setString(5, tx.getBookTitle());
            ps.setString(6, tx.getBorrowDate());
            ps.setString(7, tx.getDueDate());
            ps.setString(8, tx.getReturnDate());
            ps.setString(9, tx.getBorrowType());
            ps.setString(10, tx.getStatus());
            ps.setDouble(11, tx.getFineAmount());
            ps.setString(12, tx.getNotes());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error adding borrow transaction: {}", tx.getId(), e);
            return false;
        }
    }

    public boolean updateTransaction(BorrowTransaction tx) {
        String sql = "UPDATE borrow_transactions SET return_date = ?, borrow_type = ?, status = ?, fine_amount = ?, notes = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tx.getReturnDate());
            ps.setString(2, tx.getBorrowType());
            ps.setString(3, tx.getStatus());
            ps.setDouble(4, tx.getFineAmount());
            ps.setString(5, tx.getNotes());
            ps.setString(6, tx.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error updating transaction: {}", tx.getId(), e);
            return false;
        }
    }

    public boolean returnBook(String txId, String returnDate, double fineAmount) {
        String sql = "UPDATE borrow_transactions SET return_date = ?, status = 'Đã Trả', fine_amount = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, returnDate);
            ps.setDouble(2, fineAmount);
            ps.setString(3, txId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error marking transaction as returned: {}", txId, e);
            return false;
        }
    }

    public List<BorrowTransaction> searchTransactions(String keyword) {
        List<BorrowTransaction> list = new ArrayList<>();
        String sql = "SELECT * FROM borrow_transactions WHERE id LIKE ? OR reader_name LIKE ? OR book_title LIKE ? OR status LIKE ? OR borrow_type LIKE ? ORDER BY borrow_date DESC";
        String pattern = "%" + keyword + "%";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ps.setString(3, pattern);
            ps.setString(4, pattern);
            ps.setString(5, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSet(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error searching transactions with keyword: {}", keyword, e);
        }
        return list;
    }

    private BorrowTransaction mapResultSet(ResultSet rs) throws SQLException {
        return new BorrowTransaction(
                rs.getString("id"),
                rs.getString("reader_id"),
                rs.getString("reader_name"),
                rs.getString("book_id"),
                rs.getString("book_title"),
                rs.getString("borrow_date"),
                rs.getString("due_date"),
                rs.getString("return_date"),
                rs.getString("borrow_type"),
                rs.getString("status"),
                rs.getDouble("fine_amount"),
                rs.getString("notes")
        );
    }
}
