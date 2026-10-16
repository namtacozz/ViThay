package com.vithay.libman.view.component;

import com.vithay.libman.model.Book;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import java.net.URL;
import java.util.function.Consumer;

public class BookCardView {

    public static VBox createCard(Book book, boolean isSelected, Consumer<Book> onSelect) {
        VBox card = new VBox(8);
        card.setPrefWidth(180);
        card.setMaxWidth(180);
        card.setPrefHeight(315);
        card.setMinHeight(315);
        card.setAlignment(Pos.TOP_LEFT);
        card.getStyleClass().add("book-grid-card");
        if (isSelected) {
            card.getStyleClass().add("book-grid-card-selected");
        }

        // Image cover in fixed container to prevent shifting
        ImageView coverView = new ImageView();
        coverView.setFitWidth(156);
        coverView.setFitHeight(200);
        coverView.setPreserveRatio(false);

        StackPane coverPane = new StackPane(coverView);
        coverPane.setPrefSize(156, 200);
        coverPane.setMinSize(156, 200);
        coverPane.setMaxSize(156, 200);
        coverPane.setAlignment(Pos.CENTER);
        coverPane.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 6, 0, 0, 2);");

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

        // Title & Author with fixed dimensions to guarantee constant baseline
        String title = book.getTenSach() != null ? book.getTenSach() : book.getTitle();
        Label lblTitle = new Label(title);
        lblTitle.getStyleClass().add("book-grid-title");
        lblTitle.setWrapText(true);
        lblTitle.setPrefWidth(156);
        lblTitle.setMaxWidth(156);
        lblTitle.setPrefHeight(38);
        lblTitle.setMinHeight(38);
        lblTitle.setMaxHeight(38);
        lblTitle.setAlignment(Pos.TOP_LEFT);

        String author = book.getTenTacGia() != null ? book.getTenTacGia() : book.getAuthor();
        Label lblAuthor = new Label(author);
        lblAuthor.getStyleClass().add("book-grid-author");
        lblAuthor.setPrefWidth(156);
        lblAuthor.setMaxWidth(156);
        lblAuthor.setPrefHeight(18);
        lblAuthor.setMinHeight(18);
        lblAuthor.setMaxHeight(18);
        lblAuthor.setAlignment(Pos.TOP_LEFT);

        // Spacer to push badge to bottom
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // Status Badge pinned at bottom-right
        int available = book.getSoLuongConLai();
        Label lblBadge = new Label(available > 0 ? "Khả dụng (" + available + ")" : "Hết sách");
        lblBadge.getStyleClass().add(available > 0 ? "badge-available" : "badge-borrowed");
        lblBadge.setStyle("-fx-padding: 3 8;");

        HBox badgeBox = new HBox(lblBadge);
        badgeBox.setAlignment(Pos.BOTTOM_RIGHT);
        badgeBox.setMaxWidth(Double.MAX_VALUE);

        card.getChildren().addAll(coverView, lblTitle, lblAuthor, spacer, badgeBox);
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
