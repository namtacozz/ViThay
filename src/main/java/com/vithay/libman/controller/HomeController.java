package com.vithay.libman.controller;

import com.vithay.libman.model.Book;
import com.vithay.libman.model.BorrowTransaction;
import com.vithay.libman.service.BookService;
import com.vithay.libman.service.BorrowService;
import com.vithay.libman.service.DashboardService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class HomeController implements Initializable {
    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @FXML private Label lblGreeting;
    @FXML private Label lblTotalBooks;
    @FXML private Label lblAvailableBooks;
    @FXML private Label lblBorrowedBooks;
    @FXML private Label lblTotalReaders;

    @FXML private HBox featuredBooksContainer;

    @FXML private TableView<BorrowTransaction> transactionsTable;
    @FXML private TableColumn<BorrowTransaction, String> colTxId;
    @FXML private TableColumn<BorrowTransaction, String> colReaderName;
    @FXML private TableColumn<BorrowTransaction, String> colBookTitle;
    @FXML private TableColumn<BorrowTransaction, String> colBorrowDate;
    @FXML private TableColumn<BorrowTransaction, String> colDueDate;
    @FXML private TableColumn<BorrowTransaction, String> colStatus;
    @FXML private TableColumn<BorrowTransaction, Void> colAction;

    private final DashboardService dashboardService = new DashboardService();
    private final BookService bookService = new BookService();
    private final BorrowService borrowService = new BorrowService();

    private ObservableList<BorrowTransaction> transactionList = FXCollections.observableArrayList();
    private MainLayoutController mainController;

    public void setMainController(MainLayoutController mainController) {
        this.mainController = mainController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTable();
        refreshData();
    }

    public void refreshData() {
        loadStats();
        loadFeaturedBooks();
        loadTransactions();
    }

    private void loadStats() {
        Map<String, Object> stats = dashboardService.getDashboardStats();
        lblTotalBooks.setText(String.valueOf(stats.getOrDefault("totalBooks", 0)));
        lblAvailableBooks.setText(String.valueOf(stats.getOrDefault("availableBooks", 0)));
        lblBorrowedBooks.setText(String.valueOf(stats.getOrDefault("borrowedBooks", 0)));
        lblTotalReaders.setText(String.valueOf(stats.getOrDefault("totalReaders", 0)));
    }

    private void loadFeaturedBooks() {
        featuredBooksContainer.getChildren().clear();
        List<Book> books = bookService.getAllBooks();

        for (Book book : books) {
            VBox card = createBookCard(book);
            featuredBooksContainer.getChildren().add(card);
        }
    }

    private VBox createBookCard(Book book) {
        VBox card = new VBox(8);
        card.getStyleClass().add("bg-card");
        card.setPrefWidth(180);
        card.setMinWidth(180);
        card.setMaxWidth(180);
        card.setPadding(new Insets(12));
        card.setAlignment(Pos.TOP_LEFT);

        // Book Cover Image
        ImageView imageView = new ImageView();
        imageView.setFitWidth(156);
        imageView.setFitHeight(200);
        imageView.setPreserveRatio(false);
        imageView.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 6, 0, 0, 2);");

        try {
            String imgPath = book.getCoverImage();
            if (imgPath != null) {
                InputStream is = getClass().getResourceAsStream(imgPath);
                if (is != null) {
                    imageView.setImage(new Image(is));
                } else {
                    imageView.setImage(new Image(getClass().getResourceAsStream("/com/vithay/libman/images/logo.png")));
                }
            }
        } catch (Exception e) {
            logger.warn("Could not load image for book: {}", book.getTitle());
        }

        // Title
        Label lblTitle = new Label(book.getTitle());
        lblTitle.getStyleClass().add("title-card");
        lblTitle.setWrapText(true);
        lblTitle.setMaxWidth(156);
        lblTitle.setMaxHeight(38);

        // Author
        Label lblAuthor = new Label(book.getAuthor());
        lblAuthor.getStyleClass().add("subtitle-card");
        lblAuthor.setMaxWidth(156);

        // Category
        Label lblCategory = new Label(book.getCategory());
        lblCategory.getStyleClass().add("text-muted");

        // Status Badge
        Label lblBadge = new Label(book.getStatus());
        String status = book.getStatus();
        if ("Available".equalsIgnoreCase(status)) {
            lblBadge.getStyleClass().add("badge-available");
            lblBadge.setText("Available");
        } else if ("Borrowed".equalsIgnoreCase(status)) {
            lblBadge.getStyleClass().add("badge-borrowed");
            lblBadge.setText("Borrowed");
        } else if ("On Hold".equalsIgnoreCase(status)) {
            lblBadge.getStyleClass().add("badge-onhold");
            lblBadge.setText("On Hold");
        } else {
            lblBadge.getStyleClass().add("badge-returned");
        }

        HBox badgeBox = new HBox(lblBadge);
        badgeBox.setAlignment(Pos.CENTER_LEFT);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        card.getChildren().addAll(imageView, lblTitle, lblAuthor, lblCategory, spacer, badgeBox);

        card.setOnMouseClicked(event -> {
            if (mainController != null) {
                mainController.showBookView();
            }
        });

        return card;
    }

    private void setupTable() {
        colTxId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colReaderName.setCellValueFactory(new PropertyValueFactory<>("readerName"));
        colBookTitle.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
        colBorrowDate.setCellValueFactory(new PropertyValueFactory<>("borrowDate"));
        colDueDate.setCellValueFactory(new PropertyValueFactory<>("dueDate"));

        // Custom Badge for Status Column
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colStatus.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    Label badge = new Label(item);
                    if ("Đang Mượn".equalsIgnoreCase(item) || "Borrowed".equalsIgnoreCase(item)) {
                        badge.getStyleClass().add("badge-borrowed");
                    } else if ("Đã Trả".equalsIgnoreCase(item) || "Returned".equalsIgnoreCase(item)) {
                        badge.getStyleClass().add("badge-returned");
                    } else if ("Quá Hạn".equalsIgnoreCase(item) || "Overdue".equalsIgnoreCase(item)) {
                        badge.getStyleClass().add("badge-overdue");
                    } else {
                        badge.getStyleClass().add("badge-available");
                    }
                    HBox box = new HBox(badge);
                    box.setAlignment(Pos.CENTER);
                    setGraphic(box);
                    setText(null);
                }
            }
        });

        // Action Column: Trả Sách quick action
        colAction.setCellFactory(param -> new TableCell<>() {
            private final Button btnReturn = new Button("Trả Sách");

            {
                btnReturn.getStyleClass().add("btn-secondary");
                btnReturn.setStyle("-fx-font-size: 11px; -fx-padding: 4 10;");
                btnReturn.setOnAction(event -> {
                    BorrowTransaction tx = getTableView().getItems().get(getIndex());
                    if (mainController != null) {
                        mainController.showBorrowReturnView(1);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    BorrowTransaction tx = getTableView().getItems().get(getIndex());
                    if ("Đang Mượn".equalsIgnoreCase(tx.getStatus())) {
                        setGraphic(btnReturn);
                    } else {
                        Label lblDone = new Label("Hoàn tất");
                        lblDone.setStyle("-fx-text-fill: #727272; -fx-font-size: 11px;");
                        setGraphic(lblDone);
                    }
                    setAlignment(Pos.CENTER);
                }
            }
        });
    }

    private void loadTransactions() {
        List<BorrowTransaction> list = borrowService.getRecentTransactions(10);
        transactionList.setAll(list);
        transactionsTable.setItems(transactionList);
    }

    public void filterRecentTransactions(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            transactionsTable.setItems(transactionList);
        } else {
            ObservableList<BorrowTransaction> filtered = FXCollections.observableArrayList();
            String lower = keyword.toLowerCase().trim();
            for (BorrowTransaction tx : transactionList) {
                if (tx.getReaderName().toLowerCase().contains(lower) ||
                    tx.getBookTitle().toLowerCase().contains(lower) ||
                    tx.getId().toLowerCase().contains(lower)) {
                    filtered.add(tx);
                }
            }
            transactionsTable.setItems(filtered);
        }
    }

    @FXML
    public void handleViewAllBooks() {
        if (mainController != null) {
            mainController.showBookView();
        }
    }

    @FXML
    public void handleNewBorrow() {
        if (mainController != null) {
            mainController.showBorrowReturnView(0);
        }
    }
}
