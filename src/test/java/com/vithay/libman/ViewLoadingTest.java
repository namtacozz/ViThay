
package com.vithay.libman;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import org.junit.jupiter.api.Test;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class ViewLoadingTest {
    @Test
    public void testLoadAllViews() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<Throwable> err = new AtomicReference<>();
        
        try {
            Platform.startup(() -> {
                String[] views = {
                    "/com/vithay/libman/view/HomeView.fxml",
                    "/com/vithay/libman/view/BookManagementView.fxml",
                    "/com/vithay/libman/view/ReaderManagementView.fxml",
                    "/com/vithay/libman/view/BorrowReturnView.fxml",
                    "/com/vithay/libman/view/StatisticsView.fxml",
                    "/com/vithay/libman/view/CategoryManagementView.fxml",
                    "/com/vithay/libman/view/SettingsView.fxml",
                    "/com/vithay/libman/view/RecycleBinView.fxml"
                };
                for (String v : views) {
                    try {
                        System.out.println("Testing loading: " + v);
                        FXMLLoader loader = new FXMLLoader(getClass().getResource(v));
                        loader.load();
                        System.out.println("SUCCESS: " + v);
                    } catch (Throwable t) {
                        System.err.println("FAILED LOADING: " + v);
                        t.printStackTrace();
                        err.set(t);
                    }
                }
                latch.countDown();
            });
        } catch (IllegalStateException e) {
            // Platform already started
            latch.countDown();
        }
        
        latch.await(10, TimeUnit.SECONDS);
        if (err.get() != null) {
            throw new RuntimeException("FXML load failed", err.get());
        }
    }
}
