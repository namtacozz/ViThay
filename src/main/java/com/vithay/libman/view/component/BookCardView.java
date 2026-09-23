package com.vithay.libman.view.component;

import com.vithay.libman.model.Book;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import java.net.URL;
import java.util.function.Consumer;

public class BookCardView {

    public static VBox createCard(Book book, boolean isSelected, Consumer<Book> onSelect) {
        VBox card = new VBox(8);
        card.setPrefWidth(180);
        card.setMaxWidth(180);
        card.setAlignment(Pos.TOP_LEFT);
        card.getStyleClass().add("book-grid-card");
        if (isSelected) {
            card.getStyleClass().add("book-grid-card-selected");
        }

        // Image cover
        ImageView coverView = new ImageView();
        coverView.setFitWidth(156);
        coverView.setFitHeight(200);
        coverView.setPreserveRatio(false);

        String imageName = book.getImagePath();
        if (imageName == null || imageName.isBlank()) {
            imageName = "clean_code.jpg";
        }
        String resourcePath = imageName.startsWith("/") ? imageName : "/com/vithay/libman/images/" + imageName;
        URL imgUrl = BookCardView.class.getResource(resourcePath);
        if (imgUrl == null) {
            imgUrl = BookCardView.class.getResource("/com/vithay/libman/images/clean_code.jpg");
        }
        if (imgUrl != null) {
            coverView.setImage(new Image(imgUrl.toExternalForm(), true));
        }

        // Title & Author
        String title = book.getTenSach() != null ? book.getTenSach() : book.getTitle();
        Label lblTitle = new Label(title);
        lblTitle.getStyleClass().add("book-grid-title");
        lblTitle.setWrapText(true);
        lblTitle.setMaxHeight(40);

        String author = book.getTenTacGia() != null ? book.getTenTacGia() : book.getAuthor();
        Label lblAuthor = new Label(author);
        lblAuthor.getStyleClass().add("book-grid-author");

        // Status Badge
        int available = book.getSoLuongConLai();
        Label lblBadge = new Label(available > 0 ? "Khả dụng (" + available + ")" : "Hết sách");
        lblBadge.getStyleClass().add(available > 0 ? "badge-available" : "badge-borrowed");

        card.getChildren().addAll(coverView, lblTitle, lblAuthor, lblBadge);
        card.setOnMouseClicked(e -> {
            if (onSelect != null) onSelect.accept(book);
        });

        return card;
    }

    public static VBox createCard(Book book, Consumer<Book> onSelect) {
        return createCard(book, false, onSelect);
    }

    public static VBox createBookCard(Book book, Consumer<Book> onSelect) {
        return createCard(book, false, onSelect);
    }
}
