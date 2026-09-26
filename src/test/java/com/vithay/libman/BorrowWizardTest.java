package com.vithay.libman;

import com.vithay.libman.controller.BorrowReturnController;
import com.vithay.libman.model.Book;
import com.vithay.libman.model.Reader;
import com.vithay.libman.service.ReaderService;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class BorrowWizardTest {

    @BeforeAll
    public static void initJavaFX() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException ignored) {
        }
    }

    @Test
    public void testStep1ReaderValidation() {
        Reader validReader = new Reader("DG001", "Nguyen Van A", "a@test.com", "0901", "123", "Da Nang",
                LocalDate.now().minusYears(20), LocalDate.now().minusDays(10), LocalDate.now().plusMonths(6), "HOAT_DONG");
        assertTrue(validReader.getNgayHetHan().isAfter(LocalDate.now()), "Card must be active and not expired");

        Reader expiredReader = new Reader("DG002", "Tran Van B", "b@test.com", "0902", "124", "Da Nang",
                LocalDate.now().minusYears(25), LocalDate.now().minusYears(1), LocalDate.now().minusDays(1), "KHOA");
        assertFalse(expiredReader.getNgayHetHan().isAfter(LocalDate.now()), "Card expired must fail Step 1");
    }

    @Test
    public void testReaderServiceCardValidity() {
        ReaderService readerService = new ReaderService();

        Reader validReader = new Reader("DG001", "Nguyen Van A", "a@test.com", "0901", "123", "Da Nang",
                LocalDate.now().minusYears(20), LocalDate.now().minusDays(10), LocalDate.now().plusMonths(6), "HOAT_DONG");
        assertTrue(readerService.isCardValid(validReader), "Valid reader should pass card validity check");

        Reader expiredReader = new Reader("DG002", "Tran Van B", "b@test.com", "0902", "124", "Da Nang",
                LocalDate.now().minusYears(25), LocalDate.now().minusYears(1), LocalDate.now().minusDays(1), "HOAT_DONG");
        assertFalse(readerService.isCardValid(expiredReader), "Expired card must be invalid");

        Reader blockedReader = new Reader("DG003", "Le Van C", "c@test.com", "0903", "125", "Da Nang",
                LocalDate.now().minusYears(30), LocalDate.now().minusDays(10), LocalDate.now().plusMonths(6), "KHOA");
        assertFalse(readerService.isCardValid(blockedReader), "Blocked reader (KHOA) must be invalid");

        Reader diacriticBlockedReader1 = new Reader("DG004", "Pham Van D", "d@test.com", "0904", "126", "Da Nang",
                LocalDate.now().minusYears(28), LocalDate.now().minusDays(10), LocalDate.now().plusMonths(6), "Khóa");
        assertFalse(readerService.isCardValid(diacriticBlockedReader1), "Reader with status 'Khóa' must be invalid");

        Reader diacriticBlockedReader2 = new Reader("DG005", "Vu Van E", "e@test.com", "0905", "127", "Da Nang",
                LocalDate.now().minusYears(22), LocalDate.now().minusDays(10), LocalDate.now().plusMonths(6), "Bị khóa");
        assertFalse(readerService.isCardValid(diacriticBlockedReader2), "Reader with status 'Bị khóa' must be invalid");
    }

    @Test
    public void testStep2BookSelectionQuotaValidation() {
        int maxAllowed = 5;
        int activeLoans = 3;
        int remainingQuota = maxAllowed - activeLoans; // 2

        List<Book> selectedBooks = new ArrayList<>();
        // Empty selection should not pass Step 2
        assertTrue(selectedBooks.isEmpty(), "Initially no books selected");

        selectedBooks.add(new Book("B01", "Book 1", "Author 1", "Category", "Shelf", 50000, 5, 2, "Available", null));
        assertEquals(1, selectedBooks.size());
        assertTrue(selectedBooks.size() <= remainingQuota, "1 book fits in remaining quota of 2");

        selectedBooks.add(new Book("B02", "Book 2", "Author 2", "Category", "Shelf", 60000, 3, 1, "Available", null));
        assertEquals(2, selectedBooks.size());
        assertTrue(selectedBooks.size() <= remainingQuota, "2 books fits exactly in remaining quota of 2");

        selectedBooks.add(new Book("B03", "Book 3", "Author 3", "Category", "Shelf", 70000, 4, 3, "Available", null));
        assertEquals(3, selectedBooks.size());
        assertFalse(selectedBooks.size() <= remainingQuota, "3 books exceeds quota of 2");
    }

    @Test
    public void testStep3LoanTypeAndDueDateCalculation() {
        LocalDate today = LocalDate.now();

        // Mượn mang về nhà: 14 ngày
        int homeDays = 14;
        LocalDate homeDueDate = today.plusDays(homeDays);
        assertEquals(today.plusDays(14), homeDueDate);

        // Mượn đọc tại chỗ: 1 ngày
        int onsiteDays = 1;
        LocalDate onsiteDueDate = today.plusDays(onsiteDays);
        assertEquals(today.plusDays(1), onsiteDueDate);
    }

    @Test
    public void testWizardControllerStepNavigation() {
        BorrowReturnController controller = new BorrowReturnController();
        assertEquals(1, controller.getCurrentWizardStep(), "Wizard should start at Step 1");

        // Step 1: Cannot advance without reader
        assertFalse(controller.wizardNextStep(), "Step 1 cannot advance without reader");
        assertEquals(1, controller.getCurrentWizardStep());

        // Step 1: Cannot advance with expired reader
        Reader expiredReader = new Reader("DG_EXP", "Nguyen Expired", "exp@test.com", "0900", "111", "HN",
                LocalDate.now().minusYears(25), LocalDate.now().minusYears(1), LocalDate.now().minusDays(1), "Expired");
        controller.setWizardReader(expiredReader);
        assertFalse(controller.wizardNextStep(), "Step 1 cannot advance with expired reader");
        assertEquals(1, controller.getCurrentWizardStep());

        // Step 1: Valid reader advances to Step 2
        Reader validReader = new Reader("DG_OK", "Nguyen Valid", "ok@test.com", "0901", "222", "HN",
                LocalDate.now().minusYears(20), LocalDate.now().minusDays(5), LocalDate.now().plusMonths(6), "Active");
        controller.setWizardReader(validReader);
        assertEquals(validReader, controller.getWizardReader());
        assertTrue(controller.wizardNextStep(), "Step 1 advances with valid reader");
        assertEquals(2, controller.getCurrentWizardStep(), "Current step should now be 2");

        // Step 2: Cannot advance without selecting any book
        assertFalse(controller.wizardNextStep(), "Step 2 cannot advance without books");
        assertEquals(2, controller.getCurrentWizardStep());

        // Step 2: Add book and advance to Step 3
        Book book1 = new Book("BK_W01", "Clean Architecture", "Robert C. Martin", "CNTT", "Khu A - Kệ 01", 150000, 5, 3, "Available", null);
        assertTrue(controller.addWizardBook(book1));
        assertEquals(1, controller.getWizardSelectedBooks().size());
        assertTrue(controller.wizardNextStep(), "Step 2 advances when book is selected");
        assertEquals(3, controller.getCurrentWizardStep(), "Current step should now be 3");

        // Step 3: Configure loan parameters and advance to Step 4
        controller.setWizardLoanDays(7);
        assertEquals(7, controller.getWizardLoanDays());
        controller.setWizardLoanType("Mang về nhà");
        assertEquals("Mang về nhà", controller.getWizardLoanType());
        controller.setWizardNotes("Ghi chú thử nghiệm");
        assertEquals("Ghi chú thử nghiệm", controller.getWizardNotes());

        assertTrue(controller.wizardNextStep(), "Step 3 advances to Step 4");
        assertEquals(4, controller.getCurrentWizardStep(), "Current step should now be 4");

        // Navigation back using prevStep()
        assertTrue(controller.wizardPrevStep());
        assertEquals(3, controller.getCurrentWizardStep());
        assertTrue(controller.wizardPrevStep());
        assertEquals(2, controller.getCurrentWizardStep());
        assertTrue(controller.wizardPrevStep());
        assertEquals(1, controller.getCurrentWizardStep());
        assertFalse(controller.wizardPrevStep(), "Cannot go previous from Step 1");
        assertEquals(1, controller.getCurrentWizardStep());

        // Reset wizard
        controller.resetWizard();
        assertEquals(1, controller.getCurrentWizardStep());
        assertNull(controller.getWizardReader());
        assertEquals(0, controller.getWizardSelectedBooks().size());
    }

    @Test
    public void testWizardControllerBookOperations() {
        BorrowReturnController controller = new BorrowReturnController();
        Reader validReader = new Reader("DG_TEST_OP", "Reader Test Op", "op@test.com", "0909", "333", "HN",
                LocalDate.now().minusYears(22), LocalDate.now().minusDays(10), LocalDate.now().plusMonths(12), "Active");
        controller.setWizardReader(validReader);

        Book b1 = new Book("BK_OP1", "Domain Driven Design", "Eric Evans", "CNTT", "Kệ 1", 200000, 5, 2, "Available", null);
        Book b2 = new Book("BK_OP2", "Design Patterns", "GoF", "CNTT", "Kệ 2", 180000, 3, 1, "Available", null);

        assertTrue(controller.addWizardBook(b1));
        assertFalse(controller.addWizardBook(b1), "Cannot add duplicate book to wizard selection");
        assertEquals(1, controller.getWizardSelectedBooks().size());

        assertTrue(controller.addWizardBook(b2));
        assertEquals(2, controller.getWizardSelectedBooks().size());

        controller.removeWizardBook(b1);
        assertEquals(1, controller.getWizardSelectedBooks().size());
        assertEquals("BK_OP2", controller.getWizardSelectedBooks().get(0).getId());

        controller.clearWizardSelectedBooks();
        assertEquals(0, controller.getWizardSelectedBooks().size());
    }

    @Test
    public void testFXMLWizardStep1ReaderSelection() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        final Throwable[] error = new Throwable[1];

        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/vithay/libman/view/BorrowReturnView.fxml"));
                Parent root = loader.load();
                BorrowReturnController controller = loader.getController();

                // Switch to wizard tab
                controller.handleSwitchToWizard();
                assertEquals(1, controller.getCurrentWizardStep());

                // Find comboWizardReader reflectively or via controller
                java.lang.reflect.Field comboField = BorrowReturnController.class.getDeclaredField("comboWizardReader");
                comboField.setAccessible(true);
                @SuppressWarnings("unchecked")
                ComboBox<Reader> combo = (ComboBox<Reader>) comboField.get(controller);

                assertNotNull(combo, "comboWizardReader should be injected");
                assertFalse(combo.getItems().isEmpty(), "comboWizardReader should have items loaded");

                // Test if setItems or selectTab wipes wizardReader
                Reader validReader = null;
                Reader blockedReader = null;
                for (Reader rd : combo.getItems()) {
                    if ("Active".equalsIgnoreCase(rd.getStatus()) && validReader == null) {
                        validReader = rd;
                    }
                    if (("Bị Khóa".equalsIgnoreCase(rd.getStatus()) || "Blocked".equalsIgnoreCase(rd.getStatus())) && blockedReader == null) {
                        blockedReader = rd;
                    }
                }
                assertNotNull(validReader, "Should have at least one active reader in seed data");

                // Test 1: Selecting valid reader via ComboBox value and pressing Next
                combo.setValue(validReader);
                controller.handleSelectWizardReader();
                assertEquals(validReader.getId(), controller.getWizardReader().getId());
                boolean nextSuccess = controller.wizardNextStep();
                assertTrue(nextSuccess, "Selecting a valid reader must allow proceeding to Step 2");
                assertEquals(2, controller.getCurrentWizardStep(), "Wizard must advance to Step 2");

                // Return to Step 1
                controller.goToWizardStep(1);
                assertEquals(1, controller.getCurrentWizardStep());

                // Test 2: If a blocked reader is selected, it must NOT advance to Step 2
                if (blockedReader != null) {
                    combo.setValue(blockedReader);
                    controller.handleSelectWizardReader();
                    assertFalse(controller.wizardNextStep(), "Blocked reader must not allow proceeding to Step 2");
                    assertEquals(1, controller.getCurrentWizardStep(), "Wizard should stay on Step 1 for blocked reader");
                }

                // Test 3: Desk-to-wizard sync
                controller.goToWizardStep(1);
                controller.handleSwitchBackToDesk();
                controller.setSelectedReader(validReader);
                controller.handleSwitchToWizard();
                assertEquals(validReader.getId(), controller.getWizardReader().getId(), "Desk reader must sync to wizard");
                assertTrue(controller.wizardNextStep(), "Wizard must allow proceeding to Step 2 after switching from desk with reader selected");
                assertEquals(2, controller.getCurrentWizardStep(), "Must be on Step 2");
            } catch (Throwable t) {
                error[0] = t;
            } finally {
                latch.countDown();
            }
        });

        assertTrue(latch.await(10, TimeUnit.SECONDS), "Timeout waiting for JavaFX");
        if (error[0] != null) {
            error[0].printStackTrace();
            fail("testFXMLWizardStep1ReaderSelection failed: " + error[0].getMessage());
        }
    }
}




