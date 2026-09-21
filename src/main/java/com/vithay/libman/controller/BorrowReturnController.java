package com.vithay.libman.controller;

import com.vithay.libman.model.Book;
import com.vithay.libman.model.BorrowTransaction;
import com.vithay.libman.model.Reader;
import com.vithay.libman.service.BookService;
import com.vithay.libman.service.BorrowService;
import com.vithay.libman.service.ExportService;
import com.vithay.libman.service.ReaderService;
import com.vithay.libman.service.SettingService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class BorrowReturnController implements Initializable {
    private static final Logger logger = LoggerFactory.getLogger(BorrowReturnController.class);

    @FXML private TabPane borrowTabPane;

    // TAB 1: Mượn Sách
    @FXML private ComboBox<Reader> comboBorrowReader;
    @FXML private ComboBox<Book> comboBorrowBook;
    @FXML private ComboBox<String> comboBorrowType;
    @FXML private Spinner<Integer> spnBorrowDays;
    @FXML private Label lblBorrowDaysHint;
    @FXML private TextField txtBorrowNotes;
    @FXML private Label lblReaderStatusNote;
    @FXML private Label lblBookAvailableNote;
    @FXML private Label lblBorrowMessage;

    // TAB 2: Trả Sách
    @FXML private ComboBox<BorrowTransaction> comboReturnTransaction;
    @FXML private Label lblReturnDetailReader;
    @FXML private Label lblReturnDetailBook;
    @FXML private Label lblReturnDetailDates;
    @FXML private Label lblReturnDetailType;
    @FXML private Label lblReturnDetailFine;
    @FXML private CheckBox chkDamagedOrLost;
    @FXML private Label lblCompensationNote;
    @FXML private Label lblReturnMessage;

    // TAB 3: Lịch Sử
    @FXML private TextField txtSearchTx;
    @FXML private TableView<BorrowTransaction> allTransactionsTable;
    @FXML private TableColumn<BorrowTransaction, String> colAllTxId;
    @FXML private TableColumn<BorrowTransaction, String> colAllReader;
    @FXML private TableColumn<BorrowTransaction, String> colAllBook;
    @FXML private TableColumn<BorrowTransaction, String> colAllBorrowType;
    @FXML private TableColumn<BorrowTransaction, String> colAllBorrowDate;
    @FXML private TableColumn<BorrowTransaction, String> colAllDueDate;
    @FXML private TableColumn<BorrowTransaction, String> colAllReturnDate;
    @FXML private TableColumn<BorrowTransaction, Double> colAllFine;
    @FXML private TableColumn<BorrowTransaction, String> colAllStatus;

    private final BorrowService borrowService = new BorrowService();
    private final BookService bookService = new BookService();
    private final ReaderService readerService = new ReaderService();
    private final SettingService settingService = SettingService.getInstance();
    private final ExportService exportService = new ExportService();

    private final ObservableList<BorrowTransaction> allTxList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupBorrowTab();
        setupReturnTab();
        setupHistoryTab();
        loadAllData();
    }

    public void selectTab(int index) {
        if (borrowTabPane != null && index >= 0 && index < borrowTabPane.getTabs().size()) {
            borrowTabPane.getSelectionModel().select(index);
        }
        loadAllData();
    }

    public void loadAllData() {
        loadBorrowFormData();
        loadActiveTransactionsForReturn();
        loadAllTransactions();
    }

    private void setupBorrowTab() {
        comboBorrowType.setItems(FXCollections.observableArrayList("Mang về nhà", "Mượn đọc tại chỗ"));
        comboBorrowType.setValue("Mang về nhà");

        int maxHomeDays = settingService.getMaxBorrowDaysHome();
        spnBorrowDays.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 90, maxHomeDays));

        comboBorrowType.setOnAction(e -> {
            if ("Mượn đọc tại chỗ".equalsIgnoreCase(comboBorrowType.getValue())) {
                spnBorrowDays.getValueFactory().setValue(settingService.getMaxBorrowDaysOnsite());
                lblBorrowDaysHint.setText("(Mượn đọc trong ngày tại phòng chuyên khảo)");
            } else {
                spnBorrowDays.getValueFactory().setValue(settingService.getMaxBorrowDaysHome());
                lblBorrowDaysHint.setText("(Mượn mang về tối đa " + settingService.getMaxBorrowDaysHome() + " ngày)");
            }
        });

        comboBorrowReader.setOnAction(e -> {
            Reader r = comboBorrowReader.getValue();
            if (r != null) {
                if ("Blocked".equalsIgnoreCase(r.getStatus())) {
                    lblReaderStatusNote.setText("❌ Thẻ bị khóa!");
                    lblReaderStatusNote.setStyle("-fx-text-fill: #EF4444;");
                } else if ("Expired".equalsIgnoreCase(r.getStatus())) {
                    lblReaderStatusNote.setText("❌ Thẻ hết hạn (" + r.getCardExpiryDate() + ")");
                    lblReaderStatusNote.setStyle("-fx-text-fill: #EF4444;");
                } else {
                    lblReaderStatusNote.setText("✓ Thẻ hợp lệ (Hạn: " + r.getCardExpiryDate() + ")");
                    lblReaderStatusNote.setStyle("-fx-text-fill: #1DB954;");
                }
            }
        });

        comboBorrowBook.setOnAction(e -> {
            Book b = comboBorrowBook.getValue();
            if (b != null) {
                lblBookAvailableNote.setText("Khả dụng: " + b.getAvailableCopies() + "/" + b.getTotalCopies() + " (" + b.getShelfLocation() + ")");
                lblBookAvailableNote.setStyle("-fx-text-fill: #1DB954;");
            }
        });
    }

    private void loadBorrowFormData() {
        List<Reader> readers = readerService.getAllReaders();
        comboBorrowReader.setItems(FXCollections.observableArrayList(readers));

        List<Book> books = bookService.getAllBooks();
        ObservableList<Book> availableBooks = FXCollections.observableArrayList();
        for (Book b : books) {
            if (b.getAvailableCopies() > 0) {
                availableBooks.add(b);
            }
        }
        comboBorrowBook.setItems(availableBooks);
    }

    @FXML
    public void handleConfirmBorrow() {
        Reader reader = comboBorrowReader.getValue();
        Book book = comboBorrowBook.getValue();

        if (reader == null) {
            lblBorrowMessage.setText("Vui lòng chọn độc giả!");
            lblBorrowMessage.setStyle("-fx-text-fill: #EF4444;");
            return;
        }
        if (book == null) {
            lblBorrowMessage.setText("Vui lòng chọn sách cần mượn!");
            lblBorrowMessage.setStyle("-fx-text-fill: #EF4444;");
            return;
        }

        String type = comboBorrowType.getValue();
        int days = spnBorrowDays.getValue();
        String notes = txtBorrowNotes.getText();

        String err = borrowService.borrowBook(reader.getId(), book.getId(), type, days, notes);
        if (err == null) {
            lblBorrowMessage.setText("✓ Lập phiếu mượn (" + type + ") thành công cho độc giả " + reader.getFullName() + "!");
            lblBorrowMessage.setStyle("-fx-text-fill: #1DB954;");
            handleResetBorrowForm();
            loadAllData();
        } else {
            lblBorrowMessage.setText(err);
            lblBorrowMessage.setStyle("-fx-text-fill: #EF4444;");
        }
    }

    @FXML
    public void handleResetBorrowForm() {
        comboBorrowReader.setValue(null);
        comboBorrowBook.setValue(null);
        txtBorrowNotes.clear();
        lblReaderStatusNote.setText("");
        lblBookAvailableNote.setText("");
    }

    private void setupReturnTab() {
        comboReturnTransaction.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(BorrowTransaction item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getId() + " - " + item.getReaderName() + " (" + item.getBookTitle() + ") [" + item.getBorrowType() + "]");
                }
            }
        });
        comboReturnTransaction.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(BorrowTransaction item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getId() + " - " + item.getReaderName() + " (" + item.getBookTitle() + ") [" + item.getBorrowType() + "]");
                }
            }
        });
    }

    public void loadActiveTransactionsForReturn() {
        List<BorrowTransaction> list = borrowService.getAllTransactions();
        ObservableList<BorrowTransaction> activeList = FXCollections.observableArrayList();
        for (BorrowTransaction tx : list) {
            if ("Đang Mượn".equalsIgnoreCase(tx.getStatus())) {
                activeList.add(tx);
            }
        }
        comboReturnTransaction.setItems(activeList);
        if (!activeList.isEmpty()) {
            comboReturnTransaction.getSelectionModel().selectFirst();
            handleSelectReturnTransaction();
        } else {
            clearReturnDetails();
        }
    }

    private void clearReturnDetails() {
        lblReturnDetailReader.setText("Độc giả: --");
        lblReturnDetailBook.setText("Sách: --");
        lblReturnDetailDates.setText("Ngày mượn: -- | Hạn trả: --");
        lblReturnDetailType.setText("Hình thức: --");
        lblReturnDetailFine.setText("Phạt quá hạn dự kiến: 0 VNĐ");
        lblCompensationNote.setText("");
        chkDamagedOrLost.setSelected(false);
    }

    @FXML
    public void handleSelectReturnTransaction() {
        BorrowTransaction tx = comboReturnTransaction.getValue();
        if (tx == null) {
            clearReturnDetails();
            return;
        }

        lblReturnDetailReader.setText("Độc giả: " + tx.getReaderName() + " (Mã: " + tx.getReaderId() + ")");
        lblReturnDetailBook.setText("Sách: " + tx.getBookTitle() + " (Mã: " + tx.getBookId() + ")");
        lblReturnDetailDates.setText("Ngày mượn: " + tx.getBorrowDate() + " | Hạn trả: " + tx.getDueDate());
        lblReturnDetailType.setText("Hình thức mượn: " + tx.getBorrowType());

        updateCalculatedFine(tx);
    }

    @FXML
    public void handleDamagedToggle() {
        BorrowTransaction tx = comboReturnTransaction.getValue();
        if (tx != null) {
            updateCalculatedFine(tx);
        }
    }

    private void updateCalculatedFine(BorrowTransaction tx) {
        LocalDate now = LocalDate.now();
        double overdueFine = borrowService.calculateOverdueFine(tx.getDueDate(), now);
        boolean isDamaged = chkDamagedOrLost.isSelected();

        Book book = bookService.getBookById(tx.getBookId());
        double lostFine = 0.0;
        if (isDamaged && book != null) {
            double multiplier = settingService.getLostBookMultiplier();
            double procFee = settingService.getProcessingFee();
            lostFine = (book.getPrice() * multiplier) + procFee;
            lblCompensationNote.setText(String.format("Phí bồi hoàn sách mất/hỏng: %,.0f đ (%.0f%% giá gốc + %,.0fđ lệ phí)", lostFine, multiplier * 100, procFee));
        } else {
            lblCompensationNote.setText("");
        }

        double totalFine = overdueFine + lostFine;
        if (totalFine > 0) {
            lblReturnDetailFine.setText(String.format("Tổng tiền phạt & bồi hoàn: %,.0f VNĐ", totalFine));
            lblReturnDetailFine.setStyle("-fx-text-fill: #EF4444; -fx-font-weight: bold;");
        } else {
            lblReturnDetailFine.setText("Không có tiền phạt (Trả đúng hạn & nguyên vẹn)");
            lblReturnDetailFine.setStyle("-fx-text-fill: #1DB954; -fx-font-weight: bold;");
        }
    }

    @FXML
    public void handleConfirmReturn() {
        BorrowTransaction tx = comboReturnTransaction.getValue();
        if (tx == null) {
            lblReturnMessage.setText("Vui lòng chọn phiếu mượn cần trả!");
            lblReturnMessage.setStyle("-fx-text-fill: #EF4444;");
            return;
        }

        boolean isDamaged = chkDamagedOrLost.isSelected();
        String err = borrowService.returnBook(tx.getId(), isDamaged);
        if (err == null) {
            lblReturnMessage.setText("✓ Đã nhận trả sách thành công cho phiếu " + tx.getId() + "!");
            lblReturnMessage.setStyle("-fx-text-fill: #1DB954;");
            loadAllData();
        } else {
            lblReturnMessage.setText(err);
            lblReturnMessage.setStyle("-fx-text-fill: #EF4444;");
        }
    }

    private void setupHistoryTab() {
        colAllTxId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colAllReader.setCellValueFactory(new PropertyValueFactory<>("readerName"));
        colAllBook.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
        colAllBorrowType.setCellValueFactory(new PropertyValueFactory<>("borrowType"));
        colAllBorrowDate.setCellValueFactory(new PropertyValueFactory<>("borrowDate"));
        colAllDueDate.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        colAllReturnDate.setCellValueFactory(new PropertyValueFactory<>("returnDate"));
        colAllFine.setCellValueFactory(new PropertyValueFactory<>("fineAmount"));

        colAllFine.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Double fine, boolean empty) {
                super.updateItem(fine, empty);
                if (empty || fine == null) {
                    setText(null);
                } else if (fine > 0) {
                    setText(String.format("%,.0f đ", fine));
                    setStyle("-fx-text-fill: #EF4444; -fx-font-weight: bold;");
                } else {
                    setText("0 đ");
                    setStyle("-fx-text-fill: #B3B3B3;");
                }
            }
        });

        colAllStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colAllStatus.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    Label badge = new Label(status);
                    if ("Đang Mượn".equalsIgnoreCase(status)) {
                        badge.getStyleClass().add("badge-borrowed");
                    } else if ("Đã Trả".equalsIgnoreCase(status)) {
                        badge.getStyleClass().add("badge-returned");
                    } else {
                        badge.getStyleClass().add("badge-overdue");
                    }
                    HBox box = new HBox(badge);
                    box.setAlignment(Pos.CENTER);
                    setGraphic(box);
                    setText(null);
                }
            }
        });
    }

    public void loadAllTransactions() {
        List<BorrowTransaction> list = borrowService.getAllTransactions();
        allTxList.setAll(list);
        filterTransactions(txtSearchTx != null ? txtSearchTx.getText() : "");
    }

    @FXML
    public void handleSearchTx() {
        filterTransactions(txtSearchTx.getText());
    }

    public void filterTransactions(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            allTransactionsTable.setItems(allTxList);
        } else {
            String lower = keyword.toLowerCase().trim();
            ObservableList<BorrowTransaction> filtered = FXCollections.observableArrayList();
            for (BorrowTransaction tx : allTxList) {
                if (tx.getId().toLowerCase().contains(lower) ||
                    tx.getReaderName().toLowerCase().contains(lower) ||
                    tx.getBookTitle().toLowerCase().contains(lower) ||
                    tx.getBorrowType().toLowerCase().contains(lower) ||
                    tx.getStatus().toLowerCase().contains(lower)) {
                    filtered.add(tx);
                }
            }
            allTransactionsTable.setItems(filtered);
        }
    }

    @FXML
    public void handlePrintSelectedSlip() {
        BorrowTransaction selected = allTransactionsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Vui lòng chọn một phiếu mượn từ danh sách để in!");
            alert.showAndWait();
            return;
        }

        String slipText = exportService.generateBorrowSlip(selected);

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("In Phiếu Mượn Sách - " + selected.getId());
        dialog.setHeaderText("Xem trước mẫu phiếu mượn in ấn (SRS Mục 6.1):");

        DialogPane pane = dialog.getDialogPane();
        pane.getStylesheets().add(getClass().getResource("/com/vithay/libman/css/style.css").toExternalForm());
        pane.getStyleClass().add("bg-surface");
        pane.getButtonTypes().add(ButtonType.CLOSE);

        TextArea txt = new TextArea(slipText);
        txt.setEditable(false);
        txt.setPrefSize(520, 380);
        txt.setStyle("-fx-font-family: monospace; -fx-font-size: 12px; -fx-text-fill: #FFFFFF;");

        VBox box = new VBox(10, txt);
        box.setPadding(new Insets(12));
        pane.setContent(box);

        dialog.showAndWait();
    }
}
