package com.vithay.libman;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

public class LibManApp extends Application {
    private static final Logger logger = LoggerFactory.getLogger(LibManApp.class);

    @Override
    public void start(Stage primaryStage) {
        try {
            logger.info("Starting LibMan Desktop Application...");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/vithay/libman/view/MainLayout.fxml"));
            Parent root = loader.load();

            // Rescale window dynamically based on device display resolution (accounting for OS panels / dock)
            javafx.geometry.Rectangle2D visualBounds = javafx.stage.Screen.getPrimary().getVisualBounds();
            double screenW = visualBounds.getWidth();
            double screenH = visualBounds.getHeight();

            double initialW = Math.min(1366, Math.max(960, screenW * 0.95));
            double initialH = Math.min(840, Math.max(560, screenH * 0.94));

            Scene scene = new Scene(root, initialW, initialH);

            // Set Application Window Icon
            try (InputStream iconStream = getClass().getResourceAsStream("/com/vithay/libman/images/logo.png")) {
                if (iconStream != null) {
                    primaryStage.getIcons().add(new Image(iconStream));
                }
            } catch (Exception e) {
                logger.warn("Could not load window icon", e);
            }

            primaryStage.setTitle("LibMan - Hệ Thống Quản Lý Thư Viện Hiện Đại");
            primaryStage.setMinWidth(960);
            primaryStage.setMinHeight(520);
            primaryStage.setScene(scene);

            // Automatically maximize if running on compact laptop screens (e.g. <= 1366x800)
            if (screenW <= 1366 || screenH <= 800) {
                primaryStage.setMaximized(true);
            }

            primaryStage.show();
            logger.info("Application started successfully (Screen: {}x{}, Window: {}x{}).",
                    (int) screenW, (int) screenH, (int) initialW, (int) initialH);
        } catch (Exception e) {
            logger.error("Failed to start application", e);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
