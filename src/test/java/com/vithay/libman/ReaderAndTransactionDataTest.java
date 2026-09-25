package com.vithay.libman;

import com.vithay.libman.dao.DatabaseConfig;
import com.vithay.libman.dao.ReaderDao;
import com.vithay.libman.dao.BorrowTransactionDao;
import com.vithay.libman.model.Reader;
import com.vithay.libman.model.BorrowTransaction;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class ReaderAndTransactionDataTest {

    private static ReaderDao readerDao;
    private static BorrowTransactionDao transactionDao;

    @BeforeAll
    public static void setUp() {
        DatabaseConfig.initializeDatabase();
        readerDao = new ReaderDao();
        transactionDao = new BorrowTransactionDao();
    }

    @Test
    public void testAtLeast36ReadersExist() {
        List<Reader> readers = readerDao.getAllReaders();
        assertNotNull(readers, "Reader list should not be null");
        assertTrue(readers.size() >= 36, "Must have at least 36 readers, found: " + readers.size());
    }

    @Test
    public void testDiverseReaderStatuses() {
        List<Reader> readers = readerDao.getAllReaders();
        Set<String> statuses = readers.stream()
                .map(Reader::getStatus)
                .collect(Collectors.toSet());

        // Must include Active, Chờ Cấp Thẻ, Bị Khóa, Expired
        assertTrue(statuses.contains("Active") || statuses.contains("Hoạt Động"), "Must contain Active status");
        assertTrue(statuses.contains("Chờ Cấp Thẻ"), "Must contain Chờ Cấp Thẻ status");
        assertTrue(statuses.contains("Bị Khóa"), "Must contain Bị Khóa status");
        assertTrue(statuses.contains("Expired") || statuses.contains("Hết Hạn"), "Must contain Expired status");
    }

    @Test
    public void testExpandedRecentBorrowTransactions() {
        List<BorrowTransaction> transactions = transactionDao.getAllTransactions();
        assertNotNull(transactions, "Transactions list should not be null");
        assertTrue(transactions.size() >= 25, "Must have at least 25 borrow transactions, found: " + transactions.size());

        Set<String> txStatuses = transactions.stream()
                .map(BorrowTransaction::getStatus)
                .collect(Collectors.toSet());

        assertTrue(txStatuses.contains("Đang Mượn"), "Must contain Đang Mượn status");
        assertTrue(txStatuses.contains("Đã Trả"), "Must contain Đã Trả status");
    }

    @Test
    public void testAllTransactionsHaveUniformCodeFormat() {
        List<BorrowTransaction> transactions = transactionDao.getAllTransactions();
        for (BorrowTransaction tx : transactions) {
            assertNotNull(tx.getId(), "Transaction ID must not be null");
            assertTrue(tx.getId().startsWith("2112"),
                    "Transaction ID must start with standard prefix 2112, found: " + tx.getId());
            assertFalse(tx.getId().startsWith("TX"),
                    "Transaction ID must not have messy random TX prefix, found: " + tx.getId());
        }
    }

    @Test
    public void testSequentialTransactionIdGeneration() {
        String nextId = transactionDao.getNextTransactionId();
        assertNotNull(nextId, "Next transaction ID must not be null");
        assertTrue(nextId.startsWith("2112"), "Next transaction ID must start with 2112, found: " + nextId);
        assertEquals(9, nextId.length(), "Next transaction ID must have 9 digits, found: " + nextId);
        assertTrue(Long.parseLong(nextId) > 211200028, "Next transaction ID should increment past seed data: " + nextId);
    }

    @Test
    public void testDocGiaAccountNameIsTranVanAn() {
        com.vithay.libman.service.AuthService auth = com.vithay.libman.service.AuthService.getInstance();
        boolean loggedIn = auth.login("docgia", "123456");
        assertTrue(loggedIn, "Should login docgia successfully");
        com.vithay.libman.model.User user = auth.getCurrentUser();
        assertNotNull(user, "User should not be null");
        assertEquals("Trần Văn An", user.getFullName(), "User full name must be 'Trần Văn An' (not 'Trần Văn An Mới')");
        auth.logout();
    }
}
