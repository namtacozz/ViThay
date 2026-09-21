package com.vithay.libman.service;

import com.vithay.libman.dao.SettingDao;
import com.vithay.libman.model.SystemSetting;

import java.util.List;

public class SettingService {
    private static SettingService instance;
    private final SettingDao settingDao = new SettingDao();

    public static synchronized SettingService getInstance() {
        if (instance == null) {
            instance = new SettingService();
        }
        return instance;
    }

    public List<SystemSetting> getAllSettings() {
        return settingDao.getAllSettings();
    }

    public int getMaxBooksPerReader() {
        return settingDao.getIntSetting("max_books_per_reader", 5);
    }

    public int getMaxBorrowDaysHome() {
        return settingDao.getIntSetting("max_borrow_days_home", 14);
    }

    public int getMaxBorrowDaysOnsite() {
        return settingDao.getIntSetting("max_borrow_days_onsite", 1);
    }

    public double getFinePerDay() {
        return settingDao.getDoubleSetting("fine_per_day", 2000.0);
    }

    public double getLostBookMultiplier() {
        return settingDao.getDoubleSetting("lost_book_multiplier", 2.0);
    }

    public double getProcessingFee() {
        return settingDao.getDoubleSetting("processing_fee", 20000.0);
    }

    public int getCardValidityMonths() {
        return settingDao.getIntSetting("card_validity_months", 12);
    }

    public String getAppTheme() {
        return settingDao.getSetting("app_theme", "DARK");
    }

    public void setAppTheme(String themeCode) {
        settingDao.saveSetting("app_theme", themeCode);
    }

    public boolean updateSetting(String key, String value) {
        return settingDao.saveSetting(key, value);
    }
}
