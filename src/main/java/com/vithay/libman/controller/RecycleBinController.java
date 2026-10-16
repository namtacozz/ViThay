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
import javafx.scene.shape.SVGPath;

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
            private final Button btnRestore = new Button();
            private final Button btnPermanent = new Button();
            private final HBox pane = new HBox(8, btnRestore, btnPermanent);

            {
                pane.setAlignment(Pos.CENTER);

                // Restore Button (SVG Undo / Restore)
                btnRestore.getStyleClass().addAll("btn-primary", "btn-icon-action");
                btnRestore.setTooltip(new Tooltip("Khôi phục sách về kho lưu trữ"));
                btnRestore.setMinWidth(28);
                btnRestore.setPrefWidth(28);
                btnRestore.setMinHeight(28);
                btnRestore.setPrefHeight(28);
                SVGPath restoreSvg = new SVGPath();
                restoreSvg.setContent("M 12 5 V 1 L 7 6 l 5 5 V 7 c 3.31 0 6 2.69 6 6 s -2.69 6 -6 6 -6 -2.69 -6 -6 H 4 c 0 4.42 3.58 8 8 8 s 8 -3.58 8 -8 -3.58 -8 -8 -8 Z");
                restoreSvg.setStyle("-fx-fill: #000000;");
                restoreSvg.setScaleX(0.7);
                restoreSvg.setScaleY(0.7);
                btnRestore.setGraphic(restoreSvg);
                btnRestore.setOnAction(e -> {
                    Book b = getTableView().getItems().get(getIndex());
                    bookService.restoreBook(b.getId());
                    loadRecycleBinData();
                });

                // Permanent Delete Button (SVG Trash)
                btnPermanent.getStyleClass().addAll("btn-danger", "btn-icon-action");
                btnPermanent.setTooltip(new Tooltip("Hủy vĩnh viễn khỏi hệ thống"));
                btnPermanent.setMinWidth(28);
                btnPermanent.setPrefWidth(28);
                btnPermanent.setMinHeight(28);
                btnPermanent.setPrefHeight(28);
                SVGPath deleteSvg = new SVGPath();
                deleteSvg.setContent("M 6 19 c 0 1.1 0.9 2 2 2 h 8 c 1.1 0 2 -0.9 2 -2 V 7 H 6 v 12 Z M 19 4 h -3.5 l -1 -1 h -5 l -1 1 H 5 v 2 h 14 V 4 Z");
                deleteSvg.setStyle("-fx-fill: #EF4444;");
                deleteSvg.setScaleX(0.7);
                deleteSvg.setScaleY(0.7);
                btnPermanent.setGraphic(deleteSvg);
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
            private final Button btnRestore = new Button();
            private final Button btnPermanent = new Button();
            private final HBox pane = new HBox(8, btnRestore, btnPermanent);

            {
                pane.setAlignment(Pos.CENTER);

                // Restore Button (SVG Undo / Restore)
                btnRestore.getStyleClass().addAll("btn-primary", "btn-icon-action");
                btnRestore.setTooltip(new Tooltip("Khôi phục hồ sơ độc giả"));
                btnRestore.setMinWidth(28);
                btnRestore.setPrefWidth(28);
                btnRestore.setMinHeight(28);
                btnRestore.setPrefHeight(28);
                SVGPath restoreSvg = new SVGPath();
                restoreSvg.setContent("M 12 5 V 1 L 7 6 l 5 5 V 7 c 3.31 0 6 2.69 6 6 s -2.69 6 -6 6 -6 -2.69 -6 -6 H 4 c 0 4.42 3.58 8 8 8 s 8 -3.58 8 -8 -3.58 -8 -8 -8 Z");
                restoreSvg.setStyle("-fx-fill: #000000;");
                restoreSvg.setScaleX(0.7);
                restoreSvg.setScaleY(0.7);
                btnRestore.setGraphic(restoreSvg);
                btnRestore.setOnAction(e -> {
                    Reader r = getTableView().getItems().get(getIndex());
                    readerService.restoreReader(r.getId());
                    loadRecycleBinData();
                });

                // Permanent Delete Button (SVG Trash)
                btnPermanent.getStyleClass().addAll("btn-danger", "btn-icon-action");
                btnPermanent.setTooltip(new Tooltip("Hủy vĩnh viễn khỏi hệ thống"));
                btnPermanent.setMinWidth(28);
                btnPermanent.setPrefWidth(28);
                btnPermanent.setMinHeight(28);
                btnPermanent.setPrefHeight(28);
                SVGPath deleteSvg = new SVGPath();
                deleteSvg.setContent("M 6 19 c 0 1.1 0.9 2 2 2 h 8 c 1.1 0 2 -0.9 2 -2 V 7 H 6 v 12 Z M 19 4 h -3.5 l -1 -1 h -5 l -1 1 H 5 v 2 h 14 V 4 Z");
                deleteSvg.setStyle("-fx-fill: #EF4444;");
                deleteSvg.setScaleX(0.7);
                deleteSvg.setScaleY(0.7);
                btnPermanent.setGraphic(deleteSvg);
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
