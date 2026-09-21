package com.vithay.libman.service;

import com.vithay.libman.dao.SettingDao;
import javafx.scene.Scene;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ThemeManager {
    private static final Logger logger = LoggerFactory.getLogger(ThemeManager.class);
    private static ThemeManager instance;

    public enum Theme {
        DARK("DARK", "Spotify Dark Theme"),
        PINK_LIGHT("PINK_LIGHT", "Modern Bright Pink Theme (Light Mode)");

        private final String code;
        private final String displayName;

        Theme(String code, String displayName) {
            this.code = code;
            this.displayName = displayName;
        }

        public String getCode() { return code; }
        public String getDisplayName() { return displayName; }

        public static Theme fromCode(String code) {
            for (Theme t : values()) {
                if (t.code.equalsIgnoreCase(code)) return t;
            }
            return DARK;
        }
    }

    @FunctionalInterface
    public interface ThemeChangeListener {
        void onThemeChanged(Theme newTheme);
    }

    private final SettingDao settingDao = new SettingDao();
    private Theme currentTheme = Theme.DARK;
    private Scene activeScene;
    private final List<ThemeChangeListener> listeners = new ArrayList<>();

    private ThemeManager() {
        String saved = settingDao.getSetting("app_theme", "DARK");
        this.currentTheme = Theme.fromCode(saved);
        logger.info("Initialized ThemeManager with theme: {}", currentTheme);
    }

    public static synchronized ThemeManager getInstance() {
        if (instance == null) instance = new ThemeManager();
        return instance;
    }

    public Theme getCurrentTheme() { return currentTheme; }

    public void addThemeChangeListener(ThemeChangeListener listener) {
        listeners.add(listener);
    }

    public void registerScene(Scene scene) {
        this.activeScene = scene;
        applyThemeToScene(scene);
        notifyListeners();
    }

    public void setTheme(Theme theme) {
        this.currentTheme = theme;
        settingDao.saveSetting("app_theme", theme.getCode());
        logger.info("Theme changed to: {}", theme);
        if (activeScene != null) applyThemeToScene(activeScene);
        notifyListeners();
    }

    public void toggleTheme() {
        setTheme(currentTheme == Theme.DARK ? Theme.PINK_LIGHT : Theme.DARK);
    }

    private void applyThemeToScene(Scene scene) {
        if (scene == null || scene.getRoot() == null) return;
        if (currentTheme == Theme.PINK_LIGHT) {
            if (!scene.getRoot().getStyleClass().contains("theme-pink-light"))
                scene.getRoot().getStyleClass().add("theme-pink-light");
        } else {
            scene.getRoot().getStyleClass().remove("theme-pink-light");
        }
    }

    private void notifyListeners() {
        for (ThemeChangeListener l : listeners) l.onThemeChanged(currentTheme);
    }
}
