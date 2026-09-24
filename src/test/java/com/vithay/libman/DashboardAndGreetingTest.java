package com.vithay.libman;

import com.vithay.libman.controller.HomeController;
import com.vithay.libman.dao.DatabaseConfig;
import com.vithay.libman.model.User;
import com.vithay.libman.service.AuthService;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class DashboardAndGreetingTest {

    @BeforeAll
    public static void setUp() {
        DatabaseConfig.initializeDatabase();
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException ignored) {
            // Already started
        }
    }

    @Test
    public void testGreetingDynamicSyncAndFeaturedBooksLimit() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        final Throwable[] error = new Throwable[1];

        Platform.runLater(() -> {
            try {
                // Ensure logged out initially
                AuthService.getInstance().logout();

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/vithay/libman/view/HomeView.fxml"));
                Parent root = loader.load();
                HomeController controller = loader.getController();

                Label lblGreeting = controller.getLblGreeting();
                assertNotNull(lblGreeting, "lblGreeting should exist in HomeView");
                assertEquals("Xin chào!", lblGreeting.getText(), "When logged out, greeting must be 'Xin chào!'");

                // Featured books should be limited to 15 to eliminate dashboard lag
                HBox featuredBox = controller.getFeaturedBooksContainer();
                assertNotNull(featuredBox, "featuredBooksContainer must exist");
                assertTrue(featuredBox.getChildren().size() <= 15, "Featured books must be <= 15, found: " + featuredBox.getChildren().size());

                // Now test logging in with librarian
                boolean loggedIn = AuthService.getInstance().login("thuthu", "123456");
                assertTrue(loggedIn, "Login with thuthu should succeed");

                controller.updateGreeting();
                User currentUser = AuthService.getInstance().getCurrentUser();
                assertNotNull(currentUser, "Current user should not be null");
                assertEquals("Xin chào, " + currentUser.getFullName() + "!", lblGreeting.getText(), "Greeting must match logged in user full name");

                // Test logout
                AuthService.getInstance().logout();
                controller.updateGreeting();
                assertEquals("Xin chào!", lblGreeting.getText(), "Greeting must revert to 'Xin chào!' after logout");

            } catch (Throwable t) {
                error[0] = t;
            } finally {
                latch.countDown();
            }
        });

        assertTrue(latch.await(10, TimeUnit.SECONDS), "JavaFX loading timed out");
        if (error[0] != null) {
            fail("Dashboard test failed: " + error[0].getMessage());
        }
    }
}
