package com.vithay.libman;

import com.vithay.libman.controller.MainLayoutController;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class HelpModalAndProfileTest {

    @BeforeAll
    public static void initJavaFX() throws InterruptedException {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException ignored) {
            // Already started
        }
    }

    @Test
    public void testMainLayoutContainsHelpModalAndPasswordToggle() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        final Throwable[] error = new Throwable[1];

        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/vithay/libman/view/MainLayout.fxml"));
                Parent root = loader.load();
                assertNotNull(root, "MainLayout root should not be null");
                MainLayoutController controller = loader.getController();
                assertNotNull(controller, "MainLayoutController should not be null");

                // Check helpModalBox exists
                VBox helpModalBox = controller.getHelpModalBox();
                assertNotNull(helpModalBox, "helpModalBox must exist in MainLayout");
                assertFalse(helpModalBox.isVisible(), "helpModalBox should be hidden by default");

                // Check passwordChangeBox exists in profile modal
                VBox passwordChangeBox = controller.getPasswordChangeBox();
                assertNotNull(passwordChangeBox, "passwordChangeBox must exist in MainLayout");
                assertFalse(passwordChangeBox.isVisible(), "passwordChangeBox should be hidden by default");

                // Check btnToggleChangePassword exists
                Button btnToggle = controller.getBtnToggleChangePassword();
                assertNotNull(btnToggle, "btnToggleChangePassword must exist in MainLayout");
                assertTrue(btnToggle.getText().contains("Đổi mật khẩu"), "Button text should contain 'Đổi mật khẩu'");

                // Check readerBorrowModalBox exists
                VBox readerBorrowModalBox = controller.getReaderBorrowModalBox();
                assertNotNull(readerBorrowModalBox, "readerBorrowModalBox must exist in MainLayout");
                assertFalse(readerBorrowModalBox.isVisible(), "readerBorrowModalBox should be hidden by default");

                // Check appNoticeModalBox exists for in-app popups
                VBox appNoticeModalBox = controller.getAppNoticeModalBox();
                assertNotNull(appNoticeModalBox, "appNoticeModalBox must exist for in-app popup notices");
                assertFalse(appNoticeModalBox.isVisible(), "appNoticeModalBox should be hidden by default");

                // Check separated circulation buttons (btnNavBorrowWizard merged into Desk tab)
                assertNotNull(controller.getBtnNavReturnBook(), "btnNavReturnBook must exist");
                assertNull(controller.getBtnNavBorrowWizard(), "btnNavBorrowWizard was removed from sidebar");
                assertNotNull(controller.getBtnNavReaderBorrow(), "btnNavReaderBorrow must exist");

            } catch (Throwable t) {
                error[0] = t;
            } finally {
                latch.countDown();
            }
        });

        assertTrue(latch.await(10, TimeUnit.SECONDS), "JavaFX loading timed out");
        if (error[0] != null) {
            fail("Failed loading MainLayout.fxml: " + error[0].getMessage());
        }
    }
}
