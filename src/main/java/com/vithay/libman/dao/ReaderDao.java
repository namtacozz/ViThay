package com.vithay.libman.dao;

import com.vithay.libman.model.Reader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReaderDao {
    private static final Logger logger = LoggerFactory.getLogger(ReaderDao.class);

    public List<Reader> getAllReaders() {
        List<Reader> list = new ArrayList<>();
        String sql = "SELECT * FROM readers WHERE is_deleted = 0 ORDER BY id ASC";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            logger.error("Error retrieving readers", e);
        }
        return list;
    }

    public List<Reader> getDeletedReaders() {
        List<Reader> list = new ArrayList<>();
        String sql = "SELECT * FROM readers WHERE is_deleted = 1 ORDER BY id ASC";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            logger.error("Error retrieving deleted readers", e);
        }
        return list;
    }

    public Reader getReaderById(String id) {
        String sql = "SELECT * FROM readers WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSet(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Error retrieving reader by id: {}", id, e);
        }
        return null;
    }

    public List<Reader> searchReaders(String keyword) {
        List<Reader> list = new ArrayList<>();
        String sql = "SELECT * FROM readers WHERE is_deleted = 0 AND (full_name LIKE ? OR email LIKE ? OR phone LIKE ? OR id LIKE ? OR id_card LIKE ? OR status LIKE ?) ORDER BY id ASC";
        String pattern = "%" + keyword + "%";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ps.setString(3, pattern);
            ps.setString(4, pattern);
            ps.setString(5, pattern);
            ps.setString(6, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSet(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error searching readers with keyword: {}", keyword, e);
        }
        return list;
    }

    public boolean addReader(Reader reader) {
        String sql = "INSERT INTO readers (id, full_name, email, phone, address, id_card, birth_date, join_date, card_issue_date, card_expiry_date, status, is_deleted) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, reader.getId());
            ps.setString(2, reader.getFullName());
            ps.setString(3, reader.getEmail());
            ps.setString(4, reader.getPhone());
            ps.setString(5, reader.getAddress());
            ps.setString(6, reader.getIdCard());
            ps.setString(7, reader.getBirthDate());
            ps.setString(8, reader.getJoinDate());
            ps.setString(9, reader.getCardIssueDate());
            ps.setString(10, reader.getCardExpiryDate());
            ps.setString(11, reader.getStatus());
            ps.setInt(12, reader.isDeleted() ? 1 : 0);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error adding reader: {}", reader.getId(), e);
            return false;
        }
    }

    public boolean updateReader(Reader reader) {
        String sql = "UPDATE readers SET full_name = ?, email = ?, phone = ?, address = ?, id_card = ?, " +
                     "birth_date = ?, join_date = ?, card_issue_date = ?, card_expiry_date = ?, status = ?, is_deleted = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, reader.getFullName());
            ps.setString(2, reader.getEmail());
            ps.setString(3, reader.getPhone());
            ps.setString(4, reader.getAddress());
            ps.setString(5, reader.getIdCard());
            ps.setString(6, reader.getBirthDate());
            ps.setString(7, reader.getJoinDate());
            ps.setString(8, reader.getCardIssueDate());
            ps.setString(9, reader.getCardExpiryDate());
            ps.setString(10, reader.getStatus());
            ps.setInt(11, reader.isDeleted() ? 1 : 0);
            ps.setString(12, reader.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error updating reader: {}", reader.getId(), e);
            return false;
        }
    }

    public boolean issueCard(String readerId, String issueDate, String expiryDate) {
        String sql = "UPDATE readers SET card_issue_date = ?, card_expiry_date = ?, status = 'Active' WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, issueDate);
            ps.setString(2, expiryDate);
            ps.setString(3, readerId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error issuing card to reader: {}", readerId, e);
            return false;
        }
    }

    public boolean softDeleteReader(String id) {
        String sql = "UPDATE readers SET is_deleted = 1 WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error soft deleting reader: {}", id, e);
            return false;
        }
    }

    public boolean restoreReader(String id) {
        String sql = "UPDATE readers SET is_deleted = 0 WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error restoring reader: {}", id, e);
            return false;
        }
    }

    public boolean permanentDeleteReader(String id) {
        String sql = "DELETE FROM readers WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error permanently deleting reader: {}", id, e);
            return false;
        }
    }

    public int deactivateExpiredReaders(String currentDate) {
        String sql = "UPDATE readers SET status = 'Expired' WHERE is_deleted = 0 AND status = 'Active' AND card_expiry_date IS NOT NULL AND card_expiry_date < ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, currentDate);
            return ps.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error deactivating expired readers", e);
            return 0;
        }
    }

    private Reader mapResultSet(ResultSet rs) throws SQLException {
        return new Reader(
                rs.getString("id"),
                rs.getString("full_name"),
                rs.getString("email"),
                rs.getString("phone"),
                rs.getString("address"),
                rs.getString("id_card"),
                rs.getString("birth_date"),
                rs.getString("join_date"),
                rs.getString("card_issue_date"),
                rs.getString("card_expiry_date"),
                rs.getString("status"),
                rs.getInt("is_deleted") == 1
        );
    }
}
