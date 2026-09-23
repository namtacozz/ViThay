package com.vithay.libman;

import com.vithay.libman.controller.BorrowReturnController;
import com.vithay.libman.model.BorrowTransaction;
import com.vithay.libman.model.Reader;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CirculationContextualBranchTest {

    @Test
    public void testDetectUpcomingDueDateAlert() {
        LocalDate today = LocalDate.now();
        List<BorrowTransaction> activeTx = new ArrayList<>();
        // Sách sắp hết hạn sau 2 ngày
        activeTx.add(new BorrowTransaction("TX001", "DG001", "BK001", "VANG_VE", today.minusDays(12), today.plusDays(2), null, 0, "DANG_MUON"));
        // Sách còn hạn 10 ngày
        activeTx.add(new BorrowTransaction("TX002", "DG001", "BK002", "VANG_VE", today.minusDays(4), today.plusDays(10), null, 0, "DANG_MUON"));

        List<BorrowTransaction> upcomingAlerts = activeTx.stream()
                .filter(tx -> tx.getHanTra().isAfter(today) && tx.getHanTra().isBefore(today.plusDays(3)))
                .toList();

        assertEquals(1, upcomingAlerts.size());
        assertEquals("TX001", upcomingAlerts.get(0).getMaPhieu());
    }

    @Test
    public void testDetectOverdueAndUpcomingDueMultipleCases() {
        LocalDate today = LocalDate.now();
        List<BorrowTransaction> activeTx = new ArrayList<>();
        // 1. Quá hạn 2 ngày
        activeTx.add(new BorrowTransaction("TX_OVERDUE", "DG001", "BK001", "VANG_VE", today.minusDays(16), today.minusDays(2), null, 4000, "Quá Hạn"));
        // 2. Đúng hạn hôm nay
        activeTx.add(new BorrowTransaction("TX_TODAY", "DG001", "BK002", "VANG_VE", today.minusDays(14), today, null, 0, "Đang Mượn"));
        // 3. Sắp hết hạn ngày mai (+1 ngày)
        activeTx.add(new BorrowTransaction("TX_TOMORROW", "DG001", "BK003", "VANG_VE", today.minusDays(13), today.plusDays(1), null, 0, "Đang Mượn"));
        // 4. Còn hạn 7 ngày (+7 ngày)
        activeTx.add(new BorrowTransaction("TX_SAFE", "DG001", "BK004", "VANG_VE", today.minusDays(7), today.plusDays(7), null, 0, "Đang Mượn"));
        // 5. Đã trả (không tính)
        activeTx.add(new BorrowTransaction("TX_RETURNED", "DG001", "BK005", "VANG_VE", today.minusDays(10), today.plusDays(1), today.minusDays(1), 0, "Đã Trả"));

        LocalDate threshold = today.plusDays(3);
        List<BorrowTransaction> alertList = activeTx.stream()
                .filter(tx -> {
                    String st = tx.getStatus();
                    boolean isActive = !"Đã Trả".equalsIgnoreCase(st) && !"DA_TRA".equalsIgnoreCase(st);
                    if (!isActive) return false;
                    LocalDate due = tx.getHanTra();
                    return due != null && due.isBefore(threshold);
                })
                .toList();

        assertEquals(3, alertList.size(), "Overdue, due today, and due tomorrow must trigger alert");
        assertTrue(alertList.stream().anyMatch(tx -> "TX_OVERDUE".equals(tx.getId())));
        assertTrue(alertList.stream().anyMatch(tx -> "TX_TODAY".equals(tx.getId())));
        assertTrue(alertList.stream().anyMatch(tx -> "TX_TOMORROW".equals(tx.getId())));
    }

    @Test
    public void testReaderMaDocGiaAlias() {
        Reader reader = new Reader("DG007", "Tran Thi Mai", "mai@example.com", "0912345678", "Da Nang",
                "201234567", LocalDate.now().minusYears(20), LocalDate.now().minusMonths(1), LocalDate.now().plusMonths(11), "Active");
        assertEquals("DG007", reader.getMaDocGia());
        assertEquals(reader.getId(), reader.getMaDocGia());
    }

    @Test
    public void testControllerDueAlertsNullReader() {
        BorrowReturnController controller = new BorrowReturnController();
        List<BorrowTransaction> alerts = controller.checkReaderUpcomingDueAlerts(null);
        assertNotNull(alerts);
        assertTrue(alerts.isEmpty());

        List<BorrowTransaction> alertsEmptyStr = controller.checkReaderUpcomingDueAlerts("   ");
        assertNotNull(alertsEmptyStr);
        assertTrue(alertsEmptyStr.isEmpty());
    }

    @Test
    public void testExtrasOnDemandRegulationsToggle() {
        BorrowReturnController controller = new BorrowReturnController();
        // Initially null in headless mode, but safe to toggle
        assertDoesNotThrow(controller::handleToggleDeskRegulations);
        assertFalse(controller.isDeskRegulationsVisible());
    }
}
