package com.vithay.libman;

import com.vithay.libman.controller.BorrowReturnController;
import com.vithay.libman.controller.HomeController;
import com.vithay.libman.controller.MainLayoutController;
import com.vithay.libman.dao.DatabaseConfig;
import com.vithay.libman.model.BorrowTransaction;
import com.vithay.libman.service.AuthService;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class ReaderCirculationAndRoleTest {

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
    public void testHomeViewRBACVisibility() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        final Throwable[] error = new Throwable[1];

        Platform.runLater(() -> {
            try {
                AuthService.getInstance().logout();

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/vithay/libman/view/HomeView.fxml"));
                Parent root = loader.load();
                HomeController controller = loader.getController();

                // Test Guest: admin/staff cards must be hidden
                controller.applyPermissions();
                assertFalse(controller.getCardBorrowedBooks().isVisible(), "cardBorrowedBooks must be hidden for guest");
                assertFalse(controller.getCardTotalReaders().isVisible(), "cardTotalReaders must be hidden for guest");
                assertFalse(controller.getRecentTransactionsSection().isVisible(), "recentTransactionsSection must be hidden for guest");

                // Test Reader: admin/staff cards must also be hidden
                AuthService.getInstance().login("docgia", "123456");
                controller.applyPermissions();
                assertFalse(controller.getCardBorrowedBooks().isVisible(), "cardBorrowedBooks must be hidden for reader");
                assertFalse(controller.getRecentTransactionsSection().isVisible(), "recentTransactionsSection must be hidden for reader");

                // Test Staff (Librarian): all stats and recent transactions must be visible
                AuthService.getInstance().login("thuthu", "123456");
                controller.applyPermissions();
                assertTrue(controller.getCardBorrowedBooks().isVisible(), "cardBorrowedBooks must be visible for librarian");
                assertTrue(controller.getCardTotalReaders().isVisible(), "cardTotalReaders must be visible for librarian");
                assertTrue(controller.getRecentTransactionsSection().isVisible(), "recentTransactionsSection must be visible for librarian");

            } catch (Throwable t) {
                error[0] = t;
            } finally {
                latch.countDown();
            }
        });

        assertTrue(latch.await(10, TimeUnit.SECONDS), "JavaFX thread timed out");
        if (error[0] != null) {
            fail("testHomeViewRBACVisibility failed: " + error[0].getMessage());
        }
    }

    @Test
    public void testReaderTransactionFilterIsolation() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        final Throwable[] error = new Throwable[1];

        Platform.runLater(() -> {
            try {
                // Login as reader
                AuthService.getInstance().login("docgia", "123456");

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/vithay/libman/view/BorrowReturnView.fxml"));
                Parent root = loader.load();
                BorrowReturnController controller = loader.getController();

                controller.loadAllTransactions();
                List<BorrowTransaction> readerTxs = controller.getAllTxList();

                // Reader should only see transactions belonging to their account (or empty if none)
                for (BorrowTransaction tx : readerTxs) {
                    assertEquals("DG001", tx.getReaderId(), "Reader should only see their own transactions");
                }

                // Now login as librarian
                AuthService.getInstance().login("thuthu", "123456");
                controller.loadAllTransactions();
                List<BorrowTransaction> staffTxs = controller.getAllTxList();

                // Staff should see all library transactions
                assertTrue(staffTxs.size() >= readerTxs.size(), "Staff should see all transactions across library");

            } catch (Throwable t) {
                error[0] = t;
            } finally {
                latch.countDown();
            }
        });

        assertTrue(latch.await(10, TimeUnit.SECONDS), "JavaFX thread timed out");
        if (error[0] != null) {
            fail("testReaderTransactionFilterIsolation failed: " + error[0].getMessage());
        }
    }

    @Test
    public void testCategoryToggleTextButton() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        final Throwable[] error = new Throwable[1];

        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/vithay/libman/view/MainLayout.fxml"));
                Parent root = loader.load();
                MainLayoutController controller = loader.getController();

                controller.handleToggleCategories();
                controller.handleToggleCategories();

            } catch (Throwable t) {
                error[0] = t;
            } finally {
                latch.countDown();
            }
        });

        assertTrue(latch.await(10, TimeUnit.SECONDS), "JavaFX thread timed out");
        if (error[0] != null) {
            fail("testCategoryToggleTextButton failed: " + error[0].getMessage());
        }
    }
}
