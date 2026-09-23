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

            Scene scene = new Scene(root, 1280, 800);

            // Set Application Window Icon
            try (InputStream iconStream = getClass().getResourceAsStream("/com/vithay/libman/images/logo.png")) {
                if (iconStream != null) {
                    primaryStage.getIcons().add(new Image(iconStream));
                }
            } catch (Exception e) {
                logger.warn("Could not load window icon", e);
            }

            primaryStage.setTitle("LibMan - Hệ Thống Quản Lý Thư Viện Hiện Đại");
            primaryStage.setMinWidth(1100);
            primaryStage.setMinHeight(700);
            primaryStage.setScene(scene);
            primaryStage.show();
            logger.info("Application started successfully.");
        } catch (Exception e) {
            logger.error("Failed to start application", e);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
