package com.vithay.libman.controller;

import com.vithay.libman.model.Book;
import com.vithay.libman.model.Reader;
import com.vithay.libman.service.BookService;
import com.vithay.libman.service.ReaderService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class RecycleBinController implements Initializable {

    // Books Table
    @FXML private TableView<Book> deletedBooksTable;
    @FXML private TableColumn<Book, String> colDelBookId;
    @FXML private TableColumn<Book, String> colDelBookTitle;
    @FXML private TableColumn<Book, String> colDelBookAuthor;
    @FXML private TableColumn<Book, String> colDelBookCategory;
    @FXML private TableColumn<Book, Double> colDelBookPrice;
    @FXML private TableColumn<Book, Void> colDelBookActions;

    // Readers Table
    @FXML private TableView<Reader> deletedReadersTable;
    @FXML private TableColumn<Reader, String> colDelReaderId;
    @FXML private TableColumn<Reader, String> colDelReaderName;
    @FXML private TableColumn<Reader, String> colDelReaderEmail;
    @FXML private TableColumn<Reader, String> colDelReaderPhone;
    @FXML private TableColumn<Reader, String> colDelReaderIdCard;
    @FXML private TableColumn<Reader, Void> colDelReaderActions;

    private final BookService bookService = new BookService();
    private final ReaderService readerService = new ReaderService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupBookColumns();
        setupReaderColumns();
        loadRecycleBinData();
    }

    private void setupBookColumns() {
        colDelBookId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colDelBookTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colDelBookAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));
        colDelBookCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colDelBookPrice.setCellValueFactory(new PropertyValueFactory<>("price"));

        colDelBookActions.setCellFactory(param -> new TableCell<>() {
            private final Button btnRestore = new Button("Phục Hồi");
            private final Button btnPermanent = new Button("Hủy Vĩnh Viễn");
            private final HBox pane = new HBox(6, btnRestore, btnPermanent);

            {
                pane.setAlignment(Pos.CENTER);
                btnRestore.getStyleClass().add("btn-primary");
                btnRestore.setStyle("-fx-font-size: 11px; -fx-padding: 3 8;");
                btnRestore.setOnAction(e -> {
                    Book b = getTableView().getItems().get(getIndex());
                    bookService.restoreBook(b.getId());
                    loadRecycleBinData();
                });

                btnPermanent.getStyleClass().add("btn-danger");
                btnPermanent.setStyle("-fx-font-size: 11px; -fx-padding: 3 8;");
                btnPermanent.setOnAction(e -> {
                    Book b = getTableView().getItems().get(getIndex());
                    handlePermanentDeleteBook(b);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });
    }

    private void setupReaderColumns() {
        colDelReaderId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colDelReaderName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colDelReaderEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colDelReaderPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colDelReaderIdCard.setCellValueFactory(new PropertyValueFactory<>("idCard"));

        colDelReaderActions.setCellFactory(param -> new TableCell<>() {
            private final Button btnRestore = new Button("Phục Hồi");
            private final Button btnPermanent = new Button("Hủy Vĩnh Viễn");
            private final HBox pane = new HBox(6, btnRestore, btnPermanent);

            {
                pane.setAlignment(Pos.CENTER);
                btnRestore.getStyleClass().add("btn-primary");
                btnRestore.setStyle("-fx-font-size: 11px; -fx-padding: 3 8;");
                btnRestore.setOnAction(e -> {
                    Reader r = getTableView().getItems().get(getIndex());
                    readerService.restoreReader(r.getId());
                    loadRecycleBinData();
                });

                btnPermanent.getStyleClass().add("btn-danger");
                btnPermanent.setStyle("-fx-font-size: 11px; -fx-padding: 3 8;");
                btnPermanent.setOnAction(e -> {
                    Reader r = getTableView().getItems().get(getIndex());
                    handlePermanentDeleteReader(r);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });
    }

    public void loadRecycleBinData() {
        deletedBooksTable.setItems(FXCollections.observableArrayList(bookService.getDeletedBooks()));
        deletedReadersTable.setItems(FXCollections.observableArrayList(readerService.getDeletedReaders()));
    }

    private void handlePermanentDeleteBook(Book b) {
        MainLayoutController.showAppNotice(
                Alert.AlertType.CONFIRMATION,
                "Hủy Vĩnh Viễn Sách",
                "CẢNH BÁO: Bạn có muốn hủy vĩnh viễn cuốn sách '" + b.getTitle() + "'?\nDữ liệu sẽ bị xóa hoàn toàn khỏi cơ sở dữ liệu và không thể khôi phục.",
                () -> {
                    bookService.permanentDeleteBook(b.getId());
                    loadRecycleBinData();
                }
        );
    }

    private void handlePermanentDeleteReader(Reader r) {
        MainLayoutController.showAppNotice(
                Alert.AlertType.CONFIRMATION,
                "Hủy Vĩnh Viễn Độc Giả",
                "CẢNH BÁO: Bạn có muốn hủy vĩnh viễn hồ sơ độc giả '" + r.getFullName() + "'?\nDữ liệu sẽ bị xóa hoàn toàn khỏi cơ sở dữ liệu và không thể khôi phục.",
                () -> {
                    readerService.permanentDeleteReader(r.getId());
                    loadRecycleBinData();
                }
        );
    }
}
