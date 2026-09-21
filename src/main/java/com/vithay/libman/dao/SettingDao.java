package com.vithay.libman.dao;

import com.vithay.libman.model.SystemSetting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SettingDao {
    private static final Logger logger = LoggerFactory.getLogger(SettingDao.class);

    public List<SystemSetting> getAllSettings() {
        List<SystemSetting> list = new ArrayList<>();
        String sql = "SELECT * FROM system_settings ORDER BY setting_key ASC";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new SystemSetting(
                        rs.getString("setting_key"),
                        rs.getString("setting_value"),
                        rs.getString("description")
                ));
            }
        } catch (SQLException e) {
            logger.error("Error fetching system settings", e);
        }
        return list;
    }

    public String getSetting(String key, String defaultValue) {
        String sql = "SELECT setting_value FROM system_settings WHERE setting_key = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, key);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("setting_value");
                }
            }
        } catch (SQLException e) {
            logger.error("Error getting setting for key: {}", key, e);
        }
        return defaultValue;
    }

    public int getIntSetting(String key, int defaultValue) {
        String val = getSetting(key, null);
        if (val != null) {
            try {
                return Integer.parseInt(val.trim());
            } catch (NumberFormatException ignored) {}
        }
        return defaultValue;
    }

    public double getDoubleSetting(String key, double defaultValue) {
        String val = getSetting(key, null);
        if (val != null) {
            try {
                return Double.parseDouble(val.trim());
            } catch (NumberFormatException ignored) {}
        }
        return defaultValue;
    }

    public boolean saveSetting(String key, String value) {
        String sql = "INSERT INTO system_settings (setting_key, setting_value) VALUES (?, ?) " +
                     "ON CONFLICT(setting_key) DO UPDATE SET setting_value = excluded.setting_value";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, key);
            ps.setString(2, value);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error saving setting: {}={}", key, value, e);
            return false;
        }
    }
}
