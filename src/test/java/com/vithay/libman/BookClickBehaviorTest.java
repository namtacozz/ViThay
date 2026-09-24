package com.vithay.libman;

import com.vithay.libman.model.Book;
import com.vithay.libman.view.component.BookCardView;
import javafx.application.Platform;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class BookClickBehaviorTest {

    @BeforeAll
    public static void initJavaFX() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException ignored) {
            // Already started
        }
    }

    @Test
    public void testCardSelectionCallbackOnSingleClick() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicInteger selectCount = new AtomicInteger(0);

        Platform.runLater(() -> {
            try {
                Book book = new Book("B001", "Sapiens", "Yuval Harari", "LS", "Khu L", 189000, 6, 5, "Available", "sapiens.jpg");
                VBox card = BookCardView.createCard(book, false, b -> {
                    selectCount.incrementAndGet();
                });

                assertNotNull(card);
                // Simulate single click
                MouseEvent clickEvent = new MouseEvent(
                        MouseEvent.MOUSE_CLICKED, 0, 0, 0, 0,
                        MouseButton.PRIMARY, 1, false, false, false, false,
                        true, false, false, false, false, false, null
                );
                card.getOnMouseClicked().handle(clickEvent);

                assertEquals(1, selectCount.get(), "Single click should trigger onSelect callback once");
            } finally {
                latch.countDown();
            }
        });

        assertTrue(latch.await(5, TimeUnit.SECONDS));
    }
}
