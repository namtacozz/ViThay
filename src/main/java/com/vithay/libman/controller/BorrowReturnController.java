package com.vithay.libman.controller;

import com.vithay.libman.model.Book;
import com.vithay.libman.model.BorrowTransaction;
import com.vithay.libman.model.CirculationBasketItem;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class BorrowReturnController implements Initializable {
    private static final Logger logger = LoggerFactory.getLogger(BorrowReturnController.class);

    @FXML private TabPane borrowTabPane;

    // TAB 1: Bàn Lưu Hành (Canvas Plus Palette)
    @FXML private SplitPane deskSplitPane;
    @FXML private TextField txtDeskBookSearch;
    @FXML private ListView<Book> listDeskAvailableBooks;
    @FXML private Label lblDeskAvailableCount;
    @FXML private Button btnAddSelectedToBasket;
    @FXML private ComboBox<Reader> comboDeskReader;
    @FXML private HBox deskReaderChip;
    @FXML private Label lblDeskReaderStatus;
    @FXML private Label lblDeskReaderBorrowing;
    @FXML private Label lblDeskReaderQuota;
    @FXML private Label lblDeskReaderNote;
    @FXML private Label lblBasketCount;
    @FXML private VBox basketCardsContainer;
    @FXML private Label lblSummaryTotalBooks;
    @FXML private Label lblSummaryQuotaStatus;
    @FXML private TextField txtDeskNotes;
    @FXML private Button btnCompleteCheckout;
    @FXML private Button btnSwitchToWizard;
    @FXML private Button btnSwitchBackToDesk;

    // Intriguing Branches & Extras on Demand (Task 7)
    @FXML private HBox deskDueAlertBanner;
    @FXML private Label lblDeskDueAlertText;
    @FXML private Button btnDeskGoToReturn;
    @FXML private Button btnToggleRegulations;
    @FXML private VBox paneDeskRegulations;

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

    // TAB: TRỢ LÝ WIZARD (BORROWING WIZARD 4 BƯỚC)
    @FXML private Label stepNode1;
    @FXML private Label stepNode2;
    @FXML private Label stepNode3;
    @FXML private Label stepNode4;

    @FXML private VBox paneWizardStep1;
    @FXML private VBox paneWizardStep2;
    @FXML private VBox paneWizardStep3;
    @FXML private VBox paneWizardStep4;

    // Step 1: Reader
    @FXML private ComboBox<Reader> comboWizardReader;
    @FXML private Label lblWizardReaderName;
    @FXML private Label lblWizardReaderId;
    @FXML private Label lblWizardCardStatus;
    @FXML private Label lblWizardCardExpiry;
    @FXML private Label lblWizardActiveLoans;
    @FXML private Label lblWizardQuota;
    @FXML private Label lblWizardStep1Feedback;

    // Step 2: Book Selection
    @FXML private Label lblWizardQuotaRemainingHeader;
    @FXML private TextField txtWizardBookSearch;
    @FXML private ListView<Book> listWizardAvailableBooks;
    @FXML private Button btnWizardAddBook;
    @FXML private Label lblWizardSelectedCount;
    @FXML private ListView<Book> listWizardSelectedBooks;
    @FXML private Label lblWizardStep2Feedback;

    // Step 3: Terms & Dates
    @FXML private RadioButton radioWizardHome;
    @FXML private RadioButton radioWizardOnsite;
    @FXML private Spinner<Integer> spnWizardLoanDays;
    @FXML private Label lblWizardMaxDaysNotice;
    @FXML private Label lblWizardBorrowDate;
    @FXML private Label lblWizardCalculatedDueDate;
    @FXML private TextField txtWizardNotes;

    // Step 4: Preview Slip
    @FXML private TextArea txtWizardSlipPreview;

    // Stepper Navigation
    @FXML private Button btnWizardCancel;
    @FXML private Label lblWizardStepIndicator;
    @FXML private Button btnWizardPrev;
    @FXML private Button btnWizardNext;
    @FXML private Button btnWizardFinish;

    private int currentWizardStep = 1;
    private Reader wizardReader;
    private final ObservableList<Book> wizardSelectedBooks = FXCollections.observableArrayList();
    private final ObservableList<Book> wizardAvailableBooks = FXCollections.observableArrayList();
    private final ObservableList<Book> filteredWizardAvailableBooks = FXCollections.observableArrayList();
    private ToggleGroup wizardLoanTypeGroup;
    private String wizardLoanType = "Mang về nhà";
    private int wizardLoanDays = 14;
    private String wizardNotesText = "";

    private final BorrowService borrowService = new BorrowService();
    private final BookService bookService = new BookService();
    private final ReaderService readerService = new ReaderService();
    private final SettingService settingService = SettingService.getInstance();
    private final ExportService exportService = new ExportService();

    private final ObservableList<CirculationBasketItem> basketItems = FXCollections.observableArrayList();
    private final ObservableList<Book> availableBooksList = FXCollections.observableArrayList();
    private final ObservableList<Book> filteredAvailableBooks = FXCollections.observableArrayList();
    private final ObservableList<BorrowTransaction> allTxList = FXCollections.observableArrayList();

    private Reader selectedReader;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupDeskTab();
        setupWizardTab();
        setupReturnTab();
        setupHistoryTab();
        loadAllData();

        if (borrowTabPane != null) {
            borrowTabPane.getSelectionModel().selectedIndexProperty().addListener((obs, oldVal, newVal) -> {
                updateSwitchButtons(newVal != null ? newVal.intValue() : 0);
            });
            updateSwitchButtons(borrowTabPane.getSelectionModel().getSelectedIndex());
        }
    }

    private void updateSwitchButtons(int tabIndex) {
        if (btnSwitchToWizard != null) {
            boolean isDesk = (tabIndex == 0);
            btnSwitchToWizard.setVisible(isDesk);
            btnSwitchToWizard.setManaged(isDesk);
        }
        if (btnSwitchBackToDesk != null) {
            boolean isWizard = (tabIndex == 3);
            btnSwitchBackToDesk.setVisible(isWizard);
            btnSwitchBackToDesk.setManaged(isWizard);
        }
    }

    @FXML
    public void handleSwitchToWizard() {
        Reader reader = getSelectedReader();
        if (reader != null) {
            setWizardReader(reader);
        }
        selectTab(3);
    }

    @FXML
    public void handleSwitchBackToDesk() {
        Reader reader = getWizardReader();
        if (reader != null) {
            setSelectedReader(reader);
        }
        selectTab(0);
    }

    public void selectTab(int index) {
        if (index == 3) {
            Reader r = getSelectedReader();
            if (r != null) {
                this.wizardReader = r;
            }
        } else if (index == 0) {
            Reader r = getWizardReader();
            if (r != null) {
                this.selectedReader = r;
            }
        }
        if (borrowTabPane != null && index >= 0 && index < borrowTabPane.getTabs().size()) {
            borrowTabPane.getSelectionModel().select(index);
            updateSwitchButtons(index);
        }
        loadAllData();
        if (index == 3 && wizardReader != null) {
            setWizardReader(wizardReader);
        } else if (index == 0 && selectedReader != null) {
            setSelectedReader(selectedReader);
        }
    }

    public void selectTabByName(String name) {
        if (borrowTabPane != null && name != null) {
            for (Tab tab : borrowTabPane.getTabs()) {
                if (tab.getText().toLowerCase().contains(name.toLowerCase())) {
                    borrowTabPane.getSelectionModel().select(tab);
                    break;
                }
            }
        }
        loadAllData();
    }

    public void loadAllData() {
        loadDeskData();
        loadWizardData();
        loadActiveTransactionsForReturn();
        loadAllTransactions();
    }

    // =========================================================================
    // 1. CIRCULATION DESK (CANVAS PLUS PALETTE)
    // =========================================================================

    private void setupDeskTab() {
        if (listDeskAvailableBooks != null) {
            listDeskAvailableBooks.setItems(filteredAvailableBooks);
            listDeskAvailableBooks.setCellFactory(lv -> new ListCell<>() {
                private final HBox cellBox = new HBox(8);
                private final VBox textContainer = new VBox(2);
                private final Label lblTitle = new Label();
                private final Label lblSub = new Label();
                private final Button btnAdd = new Button("+ Thêm");
                private final Region spacer = new Region();

                {
                    cellBox.setAlignment(Pos.CENTER_LEFT);
                    HBox.setHgrow(spacer, Priority.ALWAYS);
                    cellBox.setPadding(new Insets(6, 8, 6, 8));
                    cellBox.setStyle("-fx-background-radius: 6px;");

                    lblTitle.getStyleClass().add("desk-palette-item-title");
                    lblSub.getStyleClass().add("desk-palette-item-sub");
                    textContainer.getChildren().addAll(lblTitle, lblSub);
                    HBox.setHgrow(textContainer, Priority.ALWAYS);

                    btnAdd.getStyleClass().add("btn-secondary");
                    btnAdd.setStyle("-fx-font-size: 11px; -fx-padding: 3 8; -fx-font-weight: bold;");

                    cellBox.getChildren().addAll(textContainer, spacer, btnAdd);
                }

                @Override
                protected void updateItem(Book book, boolean empty) {
                    super.updateItem(book, empty);
                    if (empty || book == null) {
                        setGraphic(null);
                        setText(null);
                    } else {
                        lblTitle.setText(book.getTitle());
                        lblSub.setText(book.getAuthor() + " • Kệ: " + book.getShelfLocation() + " (" + book.getAvailableCopies() + " cuốn)");
                        btnAdd.setOnAction(e -> addToBasket(book));
                        setGraphic(cellBox);
                        setText(null);
                    }
                }
            });

            listDeskAvailableBooks.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                    Book selected = listDeskAvailableBooks.getSelectionModel().getSelectedItem();
                    if (selected != null) {
                        addToBasket(selected);
                    }
                }
            });
        }

        if (comboDeskReader != null) {
            comboDeskReader.setCellFactory(lv -> new ListCell<>() {
                @Override
                protected void updateItem(Reader r, boolean empty) {
                    super.updateItem(r, empty);
                    if (empty || r == null) {
                        setText(null);
                    } else {
                        setText(r.getFullName() + " (" + r.getId() + ") - " + r.getStatus());
                    }
                }
            });
            comboDeskReader.setButtonCell(new ListCell<>() {
                @Override
                protected void updateItem(Reader r, boolean empty) {
                    super.updateItem(r, empty);
                    if (empty || r == null) {
                        setText(null);
                    } else {
                        setText(r.getFullName() + " (" + r.getId() + ")");
                    }
                }
            });
        }
    }

    private void loadDeskData() {
        if (comboDeskReader != null) {
            List<Reader> readers = readerService.getAllReaders();
            Reader previous = selectedReader != null ? selectedReader : comboDeskReader.getValue();
            comboDeskReader.setItems(FXCollections.observableArrayList(readers));
            if (previous != null) {
                for (Reader r : readers) {
                    if (r.getId().equalsIgnoreCase(previous.getId())) {
                        selectedReader = r;
                        comboDeskReader.setValue(r);
                        comboDeskReader.getSelectionModel().select(r);
                        break;
                    }
                }
            }
        }
        loadDeskPaletteBooks();
        updateDeskReaderInfo();
        renderBasketCards();
        updateBasketSummary();
    }

    private void loadDeskPaletteBooks() {
        List<Book> books = bookService.getAllBooks();
        availableBooksList.clear();
        for (Book b : books) {
            if (b.getAvailableCopies() > 0) {
                availableBooksList.add(b);
            }
        }
        filterDeskAvailableBooks(txtDeskBookSearch != null ? txtDeskBookSearch.getText() : "");
    }

    @FXML
    public void handleDeskBookSearch() {
        filterDeskAvailableBooks(txtDeskBookSearch != null ? txtDeskBookSearch.getText() : "");
    }

    private void filterDeskAvailableBooks(String query) {
        if (query == null || query.trim().isEmpty()) {
            filteredAvailableBooks.setAll(availableBooksList);
        } else {
            String lower = query.toLowerCase().trim();
            filteredAvailableBooks.setAll(availableBooksList.stream().filter(b ->
                    b.getTitle().toLowerCase().contains(lower) ||
                    b.getAuthor().toLowerCase().contains(lower) ||
                    b.getId().toLowerCase().contains(lower) ||
                    (b.getShelfLocation() != null && b.getShelfLocation().toLowerCase().contains(lower)) ||
                    (b.getCategory() != null && b.getCategory().toLowerCase().contains(lower))
            ).toList());
        }
        if (lblDeskAvailableCount != null) {
            lblDeskAvailableCount.setText("Khả dụng: " + filteredAvailableBooks.size() + " cuốn");
        }
    }

    @FXML
    public void handleSelectDeskReader() {
        if (comboDeskReader != null) {
            selectedReader = comboDeskReader.getValue();
        }
        updateDeskReaderInfo();
    }

    public void updateDeskReaderInfo() {
        Reader reader = getSelectedReader();
        if (reader == null) {
            if (lblDeskReaderStatus != null) {
                lblDeskReaderStatus.setText("Trạng thái: --");
                lblDeskReaderStatus.setStyle("-fx-text-fill: #9CA3AF; -fx-font-size: 12px;");
            }
            if (lblDeskReaderBorrowing != null) {
                lblDeskReaderBorrowing.setText("Đang mượn: --");
            }
            if (lblDeskReaderQuota != null) {
                lblDeskReaderQuota.setText("Còn được mượn: 5");
                lblDeskReaderQuota.setStyle("-fx-text-fill: #1DB954; -fx-font-size: 12px; -fx-font-weight: bold;");
            }
            if (lblDeskReaderNote != null) {
                lblDeskReaderNote.setText("Vui lòng chọn độc giả để bắt đầu phục vụ lưu hành.");
                lblDeskReaderNote.setStyle("-fx-text-fill: #9CA3AF; -fx-font-size: 12px;");
            }
            updateBasketSummary();
            checkReaderUpcomingDueAlerts(null);
            return;
        }

        int maxAllowed = settingService.getMaxBooksPerReader();
        int currentBorrowing = borrowService.getActiveBorrowCountForReader(reader.getId());
        int remainingQuota = Math.max(0, maxAllowed - currentBorrowing);

        if (lblDeskReaderStatus != null) {
            if ("Blocked".equalsIgnoreCase(reader.getStatus())) {
                lblDeskReaderStatus.setText("❌ Bị khóa");
                lblDeskReaderStatus.setStyle("-fx-text-fill: #EF4444; -fx-font-size: 12px; -fx-font-weight: bold;");
            } else if ("Expired".equalsIgnoreCase(reader.getStatus())) {
                lblDeskReaderStatus.setText("❌ Hết hạn");
                lblDeskReaderStatus.setStyle("-fx-text-fill: #EF4444; -fx-font-size: 12px; -fx-font-weight: bold;");
            } else if ("Chờ Cấp Thẻ".equalsIgnoreCase(reader.getStatus())) {
                lblDeskReaderStatus.setText("⚠️ Chờ cấp thẻ");
                lblDeskReaderStatus.setStyle("-fx-text-fill: #F59E0B; -fx-font-size: 12px; -fx-font-weight: bold;");
            } else {
                lblDeskReaderStatus.setText("✓ " + reader.getStatus());
                lblDeskReaderStatus.setStyle("-fx-text-fill: #1DB954; -fx-font-size: 12px; -fx-font-weight: bold;");
            }
        }

        if (lblDeskReaderBorrowing != null) {
            lblDeskReaderBorrowing.setText("Đang mượn: " + currentBorrowing + "/" + maxAllowed);
        }

        if (lblDeskReaderQuota != null) {
            lblDeskReaderQuota.setText("Còn được mượn: " + remainingQuota);
            if (remainingQuota == 0) {
                lblDeskReaderQuota.setStyle("-fx-text-fill: #EF4444; -fx-font-size: 12px; -fx-font-weight: bold;");
            } else {
                lblDeskReaderQuota.setStyle("-fx-text-fill: #1DB954; -fx-font-size: 12px; -fx-font-weight: bold;");
            }
        }

        if (lblDeskReaderNote != null) {
            if ("Blocked".equalsIgnoreCase(reader.getStatus())) {
                lblDeskReaderNote.setText("❌ Thẻ độc giả đang bị khóa tài khoản! Không thể thực hiện mượn sách.");
                lblDeskReaderNote.setStyle("-fx-text-fill: #EF4444; -fx-font-size: 12px;");
            } else if ("Expired".equalsIgnoreCase(reader.getStatus())) {
                lblDeskReaderNote.setText("❌ Thẻ độc giả đã hết hạn (" + reader.getCardExpiryDate() + ")! Vui lòng gia hạn trước.");
                lblDeskReaderNote.setStyle("-fx-text-fill: #EF4444; -fx-font-size: 12px;");
            } else if ("Chờ Cấp Thẻ".equalsIgnoreCase(reader.getStatus())) {
                lblDeskReaderNote.setText("⚠️ Độc giả chưa hoàn tất cấp thẻ tại quầy.");
                lblDeskReaderNote.setStyle("-fx-text-fill: #F59E0B; -fx-font-size: 12px;");
            } else {
                lblDeskReaderNote.setText("Thẻ hợp lệ (Hạn dùng: " + reader.getCardExpiryDate() + ") • Hạn ngạch còn lại: " + remainingQuota + " cuốn");
                lblDeskReaderNote.setStyle("-fx-text-fill: #1DB954; -fx-font-size: 12px;");
            }
        }

        updateBasketSummary();
        checkReaderUpcomingDueAlerts(reader.getId());
    }

    public List<BorrowTransaction> checkReaderUpcomingDueAlerts(String readerId) {
        if (readerId == null || readerId.trim().isEmpty()) {
            if (deskDueAlertBanner != null) {
                deskDueAlertBanner.setVisible(false);
                deskDueAlertBanner.setManaged(false);
            }
            return new ArrayList<>();
        }

        LocalDate today = LocalDate.now();
        List<BorrowTransaction> readerTx = borrowService.getTransactionsByReader(readerId);
        List<BorrowTransaction> dueOrOverdue = readerTx.stream()
                .filter(tx -> {
                    String st = tx.getStatus();
                    boolean isActive = !"Đã Trả".equalsIgnoreCase(st) && !"DA_TRA".equalsIgnoreCase(st);
                    if (!isActive) return false;
                    LocalDate due = tx.getHanTra();
                    if (due == null) return false;
                    // Quá hạn hoặc sắp hết hạn trong vòng 2 ngày (hôm nay, ngày mai, ngày kia)
                    return due.isBefore(today.plusDays(3));
                })
                .toList();

        if (!dueOrOverdue.isEmpty()) {
            String msg = "⚠️ Độc giả có " + dueOrOverdue.size() + " cuốn sách sắp đến hạn trả (hoặc quá hạn): Vui lòng nhắc nhở độc giả gia hạn hoặc mang trả!";
            if (lblDeskDueAlertText != null) {
                lblDeskDueAlertText.setText(msg);
            }
            if (deskDueAlertBanner != null) {
                deskDueAlertBanner.setVisible(true);
                deskDueAlertBanner.setManaged(true);
            }
        } else {
            if (deskDueAlertBanner != null) {
                deskDueAlertBanner.setVisible(false);
                deskDueAlertBanner.setManaged(false);
            }
        }
        return dueOrOverdue;
    }

    @FXML
    public void handleDeskGoToReturnTab() {
        if (borrowTabPane != null) {
            borrowTabPane.getSelectionModel().select(1);
        }
        Reader reader = getSelectedReader();
        if (reader != null && comboReturnTransaction != null) {
            for (BorrowTransaction tx : comboReturnTransaction.getItems()) {
                if (reader.getId().equals(tx.getReaderId())) {
                    comboReturnTransaction.setValue(tx);
                    handleSelectReturnTransaction();
                    break;
                }
            }
        }
    }

    @FXML
    public void handleDismissDueAlert() {
        if (deskDueAlertBanner != null) {
            deskDueAlertBanner.setVisible(false);
            deskDueAlertBanner.setManaged(false);
        }
    }

    @FXML
    public void handleToggleDeskRegulations() {
        if (paneDeskRegulations != null) {
            boolean show = !paneDeskRegulations.isVisible();
            paneDeskRegulations.setVisible(show);
            paneDeskRegulations.setManaged(show);
            if (btnToggleRegulations != null) {
                btnToggleRegulations.setText(show ? "ⓘ Thu gọn quy chế ▴" : "ⓘ Quy chế & chế tài áp dụng ▾");
            }
        }
    }

    public void setPaneDeskRegulationsVisible(boolean visible) {
        if (paneDeskRegulations != null) {
            paneDeskRegulations.setVisible(visible);
            paneDeskRegulations.setManaged(visible);
        }
        if (btnToggleRegulations != null) {
            btnToggleRegulations.setText(visible ? "ⓘ Thu gọn quy chế ▴" : "ⓘ Quy chế & chế tài áp dụng ▾");
        }
    }

    public boolean isDeskRegulationsVisible() {
        return paneDeskRegulations != null && paneDeskRegulations.isVisible();
    }

    @FXML
    public void handleDeskReaderSelect() {
        handleSelectDeskReader();
    }

    public HBox getDeskDueAlertBanner() { return deskDueAlertBanner; }
    public Label getLblDeskDueAlertText() { return lblDeskDueAlertText; }
    public VBox getPaneDeskRegulations() { return paneDeskRegulations; }
    public Button getBtnToggleRegulations() { return btnToggleRegulations; }

    @FXML
    public void handleDeskAddSelectedBook() {
        if (listDeskAvailableBooks != null) {
            Book selected = listDeskAvailableBooks.getSelectionModel().getSelectedItem();
            if (selected != null) {
                addToBasket(selected);
            } else {
                showDeskAlert(Alert.AlertType.INFORMATION, "Chưa Chọn Sách", "Vui lòng chọn một cuốn sách từ bảng bên trái!");
            }
        }
    }

    public void addToBasket(Book book) {
        if (book == null) return;

        Reader reader = getSelectedReader();
        if (reader == null) {
            showDeskAlert(Alert.AlertType.WARNING, "Chưa Chọn Độc Giả", "Vui lòng chọn độc giả trước khi xếp sách lên bàn lưu hành!");
            return;
        }

        if ("Blocked".equalsIgnoreCase(reader.getStatus())) {
            showDeskAlert(Alert.AlertType.ERROR, "Thẻ Bị Khóa", "Thẻ độc giả đang bị khóa, không thể mượn sách!");
            return;
        }
        if ("Expired".equalsIgnoreCase(reader.getStatus())) {
            showDeskAlert(Alert.AlertType.ERROR, "Thẻ Hết Hạn", "Thẻ độc giả đã hết hạn (" + reader.getCardExpiryDate() + ")!");
            return;
        }
        if ("Chờ Cấp Thẻ".equalsIgnoreCase(reader.getStatus())) {
            showDeskAlert(Alert.AlertType.ERROR, "Chưa Cấp Thẻ", "Độc giả chưa được cấp thẻ thư viện tại quầy!");
            return;
        }

        // Check if book already in basket
        for (CirculationBasketItem item : basketItems) {
            if (item.getBook().getId().equalsIgnoreCase(book.getId())) {
                showDeskAlert(Alert.AlertType.WARNING, "Trùng Lặp Sách", "Cuốn sách \"" + book.getTitle() + "\" đã có trên bàn lưu hành!");
                return;
            }
        }

        // Check availability
        if (book.getAvailableCopies() <= 0) {
            showDeskAlert(Alert.AlertType.WARNING, "Hết Sách", "Sách này hiện không còn bản nào khả dụng trong kho!");
            return;
        }

        // Enforce quota (SRS 6.1: max 5 books total per reader)
        int maxAllowed = settingService.getMaxBooksPerReader();
        int currentBorrowing = borrowService.getActiveBorrowCountForReader(reader.getId());
        int remainingQuota = Math.max(0, maxAllowed - currentBorrowing);

        if (basketItems.size() >= remainingQuota) {
            showDeskAlert(Alert.AlertType.ERROR, "Vượt Hạn Ngạch Mượn",
                    "Độc giả đang mượn " + currentBorrowing + "/" + maxAllowed + " cuốn.\n" +
                    "Chỉ còn được mượn thêm tối đa " + remainingQuota + " cuốn nữa theo quy chế thư viện!");
            return;
        }

        int defaultDays = settingService.getMaxBorrowDaysHome();
        CirculationBasketItem item = new CirculationBasketItem(book, "Mang về nhà", defaultDays);
        basketItems.add(item);
        renderBasketCards();
        updateBasketSummary();
    }

    public void removeFromBasket(Book book) {
        if (book == null) return;
        basketItems.removeIf(item -> item.getBook().getId().equalsIgnoreCase(book.getId()));
        renderBasketCards();
        updateBasketSummary();
    }

    public void removeFromBasket(CirculationBasketItem item) {
        if (item == null) return;
        basketItems.remove(item);
        renderBasketCards();
        updateBasketSummary();
    }

    @FXML
    public void handleClearBasket() {
        basketItems.clear();
        renderBasketCards();
        updateBasketSummary();
    }

    public ObservableList<CirculationBasketItem> getBasketItems() {
        return basketItems;
    }

    public Reader getSelectedReader() {
        if (selectedReader != null) {
            return selectedReader;
        }
        if (comboDeskReader != null) {
            if (comboDeskReader.getValue() != null) {
                return comboDeskReader.getValue();
            }
            if (comboDeskReader.getSelectionModel() != null && comboDeskReader.getSelectionModel().getSelectedItem() != null) {
                return comboDeskReader.getSelectionModel().getSelectedItem();
            }
        }
        return null;
    }

    public void setSelectedReader(Reader reader) {
        this.selectedReader = reader;
        if (comboDeskReader != null && reader != null) {
            boolean found = false;
            for (Reader r : comboDeskReader.getItems()) {
                if (r.getId().equalsIgnoreCase(reader.getId())) {
                    comboDeskReader.setValue(r);
                    comboDeskReader.getSelectionModel().select(r);
                    found = true;
                    break;
                }
            }
            if (!found) {
                comboDeskReader.setValue(reader);
            }
        } else if (comboDeskReader != null) {
            comboDeskReader.setValue(null);
            if (comboDeskReader.getSelectionModel() != null) {
                comboDeskReader.getSelectionModel().clearSelection();
            }
        }
        updateDeskReaderInfo();
    }

    private void renderBasketCards() {
        if (basketCardsContainer == null) return;

        basketCardsContainer.getChildren().clear();
        if (basketItems.isEmpty()) {
            VBox emptyBox = new VBox(10);
            emptyBox.setAlignment(Pos.CENTER);
            emptyBox.setPadding(new Insets(40, 20, 40, 20));

            javafx.scene.shape.SVGPath emptyIcon = new javafx.scene.shape.SVGPath();
            emptyIcon.setContent("M 18 2 H 6 C 4.9 2 4 2.9 4 4 V 20 C 4 21.1 4.9 22 6 22 H 18 C 19.1 22 20 21.1 20 20 V 4 C 20 2.9 19.1 2 18 2 Z M 18 20 H 6 V 4 H 18 V 20 Z");
            emptyIcon.setScaleX(1.4);
            emptyIcon.setScaleY(1.4);
            emptyIcon.setFill(javafx.scene.paint.Color.web("#555555"));

            Label emptyTitle = new Label("Bàn lưu hành hiện đang trống");
            emptyTitle.getStyleClass().add("desk-empty-title");
            emptyTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

            Label emptySub = new Label("Chọn sách từ bảng bên trái (Palette) và bấm [+ Thêm] để xếp sách lên bàn.");
            emptySub.setStyle("-fx-text-fill: #737373; -fx-font-size: 12px;");

            emptyBox.getChildren().addAll(emptyIcon, emptyTitle, emptySub);
            basketCardsContainer.getChildren().add(emptyBox);
        } else {
            for (CirculationBasketItem item : basketItems) {
                basketCardsContainer.getChildren().add(createBasketCard(item));
            }
        }
    }

    private HBox createBasketCard(CirculationBasketItem item) {
        HBox card = new HBox(12);
        card.getStyleClass().add("desk-basket-card");
        card.setAlignment(Pos.CENTER_LEFT);

        Book book = item.getBook();

        // 1. Cover image / placeholder thumbnail
        StackPane thumbBox = new StackPane();
        thumbBox.setPrefSize(42, 60);
        thumbBox.setMinSize(42, 60);
        thumbBox.setMaxSize(42, 60);
        thumbBox.getStyleClass().add("desk-basket-thumb");

        boolean imageLoaded = false;
        if (book.getImagePath() != null && !book.getImagePath().trim().isEmpty()) {
            try {
                String path = book.getImagePath();
                URL res = getClass().getResource("/com/vithay/libman/images/" + path);
                if (res == null && !path.startsWith("/")) {
                    res = getClass().getResource("/" + path);
                }
                if (res != null) {
                    ImageView iv = new ImageView(new Image(res.toExternalForm(), 42, 60, true, true));
                    iv.setFitWidth(42);
                    iv.setFitHeight(60);
                    thumbBox.getChildren().add(iv);
                    imageLoaded = true;
                }
            } catch (Exception ignored) {
            }
        }
        if (!imageLoaded) {
            Label iconLbl = new Label("•");
            iconLbl.setStyle("-fx-font-size: 18px;");
            thumbBox.getChildren().add(iconLbl);
        }

        // 2. Book Info (Title, Author, Shelf location)
        VBox infoBox = new VBox(3);
        infoBox.setPrefWidth(200);
        HBox.setHgrow(infoBox, Priority.ALWAYS);

        Label lblTitle = new Label(book.getTitle());
        lblTitle.getStyleClass().add("desk-card-title");
        lblTitle.setWrapText(true);

        Label lblMeta = new Label(book.getAuthor() + " • Kệ: " + book.getShelfLocation());
        lblMeta.getStyleClass().add("desk-card-meta");

        Label lblId = new Label("Mã: " + book.getId() + " | Còn: " + book.getAvailableCopies() + " cuốn");
        lblId.getStyleClass().add("desk-card-sub");

        infoBox.getChildren().addAll(lblTitle, lblMeta, lblId);

        // 3. Loan Type Selector
        VBox typeBox = new VBox(2);
        typeBox.setAlignment(Pos.CENTER_LEFT);
        Label lblTypeHeader = new Label("Hình thức:");
        lblTypeHeader.getStyleClass().add("desk-card-header");
        ComboBox<String> comboType = new ComboBox<>(FXCollections.observableArrayList("Mang về nhà", "Mượn đọc tại chỗ"));
        comboType.setValue(item.getLoanType());
        comboType.setPrefWidth(135);
        comboType.getStyleClass().add("form-control");
        comboType.setStyle("-fx-font-size: 11px;");
        typeBox.getChildren().addAll(lblTypeHeader, comboType);

        // 4. Loan Days Spinner & Due Date
        VBox daysBox = new VBox(2);
        daysBox.setAlignment(Pos.CENTER_LEFT);
        Label lblDaysHeader = new Label("Thời hạn:");
        lblDaysHeader.getStyleClass().add("desk-card-header");

        int maxHome = settingService.getMaxBorrowDaysHome();
        int maxOnsite = settingService.getMaxBorrowDaysOnsite();
        Spinner<Integer> spnDays = new Spinner<>(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 90, item.getLoanDays()));
        spnDays.setPrefWidth(80);
        spnDays.getStyleClass().add("form-control");
        spnDays.setStyle("-fx-font-size: 11px;");
        daysBox.getChildren().addAll(lblDaysHeader, spnDays);

        // 5. Due Date Badge
        VBox dueBox = new VBox(2);
        dueBox.setAlignment(Pos.CENTER_LEFT);
        dueBox.setPrefWidth(110);
        Label lblDueHeader = new Label("Hạn trả dự kiến:");
        lblDueHeader.getStyleClass().add("desk-card-header");
        Label lblDueDate = new Label(item.getDueDate().toString());
        lblDueDate.getStyleClass().add("desk-card-due");
        dueBox.getChildren().addAll(lblDueHeader, lblDueDate);

        // Listeners for type & days changes
        comboType.setOnAction(e -> {
            String val = comboType.getValue();
            item.setLoanType(val);
            if ("Mượn đọc tại chỗ".equalsIgnoreCase(val)) {
                spnDays.getValueFactory().setValue(maxOnsite);
                item.setLoanDays(maxOnsite);
            } else {
                spnDays.getValueFactory().setValue(maxHome);
                item.setLoanDays(maxHome);
            }
            lblDueDate.setText(item.getDueDate().toString());
        });

        spnDays.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                item.setLoanDays(newVal);
                lblDueDate.setText(item.getDueDate().toString());
            }
        });

        // 6. Remove Button [✕]
        Button btnRemove = new Button("✕");
        btnRemove.getStyleClass().add("desk-card-remove-btn");
        btnRemove.setOnAction(e -> removeFromBasket(item));

        card.getChildren().addAll(thumbBox, infoBox, typeBox, daysBox, dueBox, btnRemove);
        return card;
    }

    private void updateBasketSummary() {
        int count = basketItems.size();
        if (lblBasketCount != null) {
            lblBasketCount.setText("(" + count + "/5 cuốn)");
        }
        if (lblSummaryTotalBooks != null) {
            lblSummaryTotalBooks.setText("Tổng trong giỏ: " + count + " cuốn");
        }

        Reader reader = getSelectedReader();
        int maxAllowed = settingService != null ? settingService.getMaxBooksPerReader() : 5;
        int currentBorrowing = (reader != null && borrowService != null) ? borrowService.getActiveBorrowCountForReader(reader.getId()) : 0;
        int remainingQuota = Math.max(0, maxAllowed - currentBorrowing);
        boolean isCardValid = reader != null && (readerService == null || readerService.isCardValid(reader));

        if (lblSummaryQuotaStatus != null) {
            if (reader == null) {
                lblSummaryQuotaStatus.setText("⚠️ Chưa chọn độc giả");
                lblSummaryQuotaStatus.setStyle("-fx-text-fill: #F59E0B; -fx-font-size: 11px;");
            } else if (!isCardValid) {
                lblSummaryQuotaStatus.setText("❌ Thẻ độc giả không hợp lệ hoặc đã hết hạn!");
                lblSummaryQuotaStatus.setStyle("-fx-text-fill: #EF4444; -fx-font-weight: bold; -fx-font-size: 11px;");
            } else if (count == 0) {
                lblSummaryQuotaStatus.setText("Chưa chọn sách vào giỏ (Hạn ngạch còn: " + remainingQuota + " cuốn)");
                lblSummaryQuotaStatus.setStyle("-fx-text-fill: #9CA3AF; -fx-font-size: 11px;");
            } else if (count <= remainingQuota) {
                lblSummaryQuotaStatus.setText("✓ Hạn ngạch hợp lệ (Còn lại " + (remainingQuota - count) + " cuốn sau khi mượn)");
                lblSummaryQuotaStatus.setStyle("-fx-text-fill: #1DB954; -fx-font-weight: bold; -fx-font-size: 11px;");
            } else {
                lblSummaryQuotaStatus.setText("❌ Vượt hạn ngạch cho phép! (Vượt " + (count - remainingQuota) + " cuốn)");
                lblSummaryQuotaStatus.setStyle("-fx-text-fill: #EF4444; -fx-font-weight: bold; -fx-font-size: 11px;");
            }
        }

        if (btnCompleteCheckout != null) {
            boolean disabled = (reader == null || count == 0 || count > remainingQuota || !isCardValid);
            btnCompleteCheckout.setDisable(disabled);
        }
    }

    @FXML
    public void handleCompleteDeskCheckout() {
        Reader reader = getSelectedReader();
        if (reader == null) {
            showDeskAlert(Alert.AlertType.WARNING, "Chưa Chọn Độc Giả", "Vui lòng chọn độc giả trước khi hoàn tất mượn sách!");
            return;
        }
        if (basketItems.isEmpty()) {
            showDeskAlert(Alert.AlertType.WARNING, "Giỏ Trống", "Chưa có sách nào trên bàn lưu hành để lập phiếu!");
            return;
        }

        int maxAllowed = settingService.getMaxBooksPerReader();
        int currentBorrowing = borrowService.getActiveBorrowCountForReader(reader.getId());
        int remainingQuota = maxAllowed - currentBorrowing;
        if (basketItems.size() > remainingQuota) {
            showDeskAlert(Alert.AlertType.ERROR, "Vượt Quá Hạn Ngạch",
                    "Số sách trong giỏ (" + basketItems.size() + ") vượt quá hạn ngạch còn lại (" + remainingQuota + ")!");
            return;
        }

        String notes = (txtDeskNotes != null && !txtDeskNotes.getText().trim().isEmpty())
                ? txtDeskNotes.getText().trim()
                : "Mượn tại bàn lưu hành (Canvas Desk)";

        List<BorrowTransaction> createdTransactions = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        for (CirculationBasketItem item : new ArrayList<>(basketItems)) {
            String err = borrowService.createBorrowTransaction(
                    reader.getId(),
                    item.getBook().getId(),
                    item.getLoanType(),
                    item.getLoanDays(),
                    notes
            );
            if (err != null) {
                errors.add(item.getBook().getTitle() + ": " + err);
            } else {
                // Find newly created transaction
                List<BorrowTransaction> readerTxs = borrowService.getTransactionsByReader(reader.getId());
                for (BorrowTransaction tx : readerTxs) {
                    if (tx.getBookId().equals(item.getBook().getId()) && "Đang Mượn".equalsIgnoreCase(tx.getStatus())) {
                        if (createdTransactions.stream().noneMatch(t -> t.getId().equals(tx.getId()))) {
                            createdTransactions.add(tx);
                            break;
                        }
                    }
                }
            }
        }

        if (!errors.isEmpty()) {
            showDeskAlert(Alert.AlertType.ERROR, "Lỗi Khi Lập Phiếu", String.join("\n", errors));
        }

        if (!createdTransactions.isEmpty()) {
            showReceiptDialog(reader, createdTransactions);
            for (BorrowTransaction tx : createdTransactions) {
                basketItems.removeIf(it -> it.getBook().getId().equals(tx.getBookId()));
            }
            if (txtDeskNotes != null) txtDeskNotes.clear();
            loadAllData();
            updateDeskReaderInfo();
        }
    }

    private void showReceiptDialog(Reader reader, List<BorrowTransaction> transactions) {
        try {
            String slipText = exportService.generateBasketBorrowSlip(reader, transactions);
            String title = "In Phiếu Mượn Sách - " + (reader != null ? reader.getFullName() : "");
            String heading = "Lập phiếu mượn thành công! Xem trước mẫu phiếu in (SRS Mục 6.1):";
            MainLayoutController.showReceiptPopup(title, heading, slipText);
        } catch (Throwable t) {
            logger.warn("Could not display receipt dialog: {}", t.getMessage());
        }
    }

    private void showDeskAlert(Alert.AlertType type, String title, String message) {
        MainLayoutController.showAppNotice(type, title, message);
    }

    // =========================================================================
    // 2. TAB 2: XỬ LÝ TRẢ SÁCH & BỒI HOÀN
    // =========================================================================

    private void setupReturnTab() {
        if (comboReturnTransaction == null) return;
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
        if (comboReturnTransaction == null) return;
        List<BorrowTransaction> activeList = borrowService.getActiveTransactions();
        comboReturnTransaction.setItems(FXCollections.observableArrayList(activeList));
        if (!activeList.isEmpty()) {
            comboReturnTransaction.getSelectionModel().selectFirst();
            handleSelectReturnTransaction();
        } else {
            clearReturnDetails();
        }
    }

    private void clearReturnDetails() {
        if (lblReturnDetailReader != null) lblReturnDetailReader.setText("Độc giả: --");
        if (lblReturnDetailBook != null) lblReturnDetailBook.setText("Sách: --");
        if (lblReturnDetailDates != null) lblReturnDetailDates.setText("Ngày mượn: -- | Hạn trả: --");
        if (lblReturnDetailType != null) lblReturnDetailType.setText("Hình thức: --");
        if (lblReturnDetailFine != null) lblReturnDetailFine.setText("Phạt quá hạn dự kiến: 0 VNĐ");
        if (lblCompensationNote != null) lblCompensationNote.setText("");
        if (chkDamagedOrLost != null) chkDamagedOrLost.setSelected(false);
    }

    @FXML
    public void handleSelectReturnTransaction() {
        if (comboReturnTransaction == null) return;
        BorrowTransaction tx = comboReturnTransaction.getValue();
        if (tx == null) {
            clearReturnDetails();
            return;
        }

        if (lblReturnDetailReader != null) lblReturnDetailReader.setText("Độc giả: " + tx.getReaderName() + " (Mã: " + tx.getReaderId() + ")");
        if (lblReturnDetailBook != null) lblReturnDetailBook.setText("Sách: " + tx.getBookTitle() + " (Mã: " + tx.getBookId() + ")");
        if (lblReturnDetailDates != null) lblReturnDetailDates.setText("Ngày mượn: " + tx.getBorrowDate() + " | Hạn trả: " + tx.getDueDate());
        if (lblReturnDetailType != null) lblReturnDetailType.setText("Hình thức mượn: " + tx.getBorrowType());

        updateCalculatedFine(tx);
    }

    @FXML
    public void handleDamagedToggle() {
        if (comboReturnTransaction == null) return;
        BorrowTransaction tx = comboReturnTransaction.getValue();
        if (tx != null) {
            updateCalculatedFine(tx);
        }
    }

    private void updateCalculatedFine(BorrowTransaction tx) {
        LocalDate now = LocalDate.now();
        double overdueFine = borrowService.calculateOverdueFine(tx.getDueDate(), now);
        boolean isDamaged = chkDamagedOrLost != null && chkDamagedOrLost.isSelected();

        Book book = bookService.getBookById(tx.getBookId());
        double lostFine = 0.0;
        if (isDamaged && book != null) {
            double multiplier = settingService.getLostBookMultiplier();
            double procFee = settingService.getProcessingFee();
            lostFine = (book.getPrice() * multiplier) + procFee;
            if (lblCompensationNote != null) {
                lblCompensationNote.setText(String.format("Phí bồi hoàn sách mất/hỏng: %,.0f đ (%.0f%% giá gốc + %,.0fđ lệ phí)", lostFine, multiplier * 100, procFee));
            }
        } else {
            if (lblCompensationNote != null) lblCompensationNote.setText("");
        }

        double totalFine = overdueFine + lostFine;
        if (lblReturnDetailFine != null) {
            if (totalFine > 0) {
                lblReturnDetailFine.setText(String.format("Tổng tiền phạt & bồi hoàn: %,.0f VNĐ", totalFine));
                lblReturnDetailFine.setStyle("-fx-text-fill: #EF4444; -fx-font-weight: bold;");
            } else {
                lblReturnDetailFine.setText("Không có tiền phạt (Trả đúng hạn & nguyên vẹn)");
                lblReturnDetailFine.setStyle("-fx-text-fill: #1DB954; -fx-font-weight: bold;");
            }
        }
    }

    @FXML
    public void handleConfirmReturn() {
        if (comboReturnTransaction == null) return;
        BorrowTransaction tx = comboReturnTransaction.getValue();
        if (tx == null) {
            if (lblReturnMessage != null) {
                lblReturnMessage.setText("Vui lòng chọn phiếu mượn cần trả!");
                lblReturnMessage.setStyle("-fx-text-fill: #EF4444;");
            }
            return;
        }

        boolean isDamaged = chkDamagedOrLost != null && chkDamagedOrLost.isSelected();
        String err = borrowService.returnBook(tx.getId(), isDamaged);
        if (err == null) {
            if (lblReturnMessage != null) {
                lblReturnMessage.setText("✓ Đã nhận trả sách thành công cho phiếu " + tx.getId() + "!");
                lblReturnMessage.setStyle("-fx-text-fill: #1DB954;");
            }
            loadAllData();
        } else {
            if (lblReturnMessage != null) {
                lblReturnMessage.setText(err);
                lblReturnMessage.setStyle("-fx-text-fill: #EF4444;");
            }
        }
    }

    // =========================================================================
    // 3. TAB 3: LỊCH SỬ TẤT CẢ PHIẾU MƯỢN
    // =========================================================================

    private void setupHistoryTab() {
        if (allTransactionsTable == null) return;

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
        com.vithay.libman.service.AuthService auth = com.vithay.libman.service.AuthService.getInstance();
        com.vithay.libman.model.User currentUser = auth.getCurrentUser();
        List<BorrowTransaction> list;
        if (auth.isReader() && currentUser != null) {
            Reader r = readerService.getReaderForUser(currentUser);
            if (r != null) {
                list = borrowService.getTransactionsByReader(r.getId());
            } else {
                list = new ArrayList<>();
                for (BorrowTransaction tx : borrowService.getAllTransactions()) {
                    if (currentUser.getFullName() != null && currentUser.getFullName().equalsIgnoreCase(tx.getReaderName())) {
                        list.add(tx);
                    }
                }
            }
        } else {
            list = borrowService.getAllTransactions();
        }
        allTxList.setAll(list);
        filterTransactions(txtSearchTx != null ? txtSearchTx.getText() : "");
    }

    @FXML
    public void handleSearchTx() {
        filterTransactions(txtSearchTx != null ? txtSearchTx.getText() : "");
    }

    public void filterTransactions(String keyword) {
        if (allTransactionsTable == null) return;
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
        if (allTransactionsTable == null) return;
        BorrowTransaction selected = allTransactionsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            MainLayoutController.showAppNotice(Alert.AlertType.WARNING, "Chưa Chọn Phiếu", "Vui lòng chọn một phiếu mượn từ danh sách để in!");
            return;
        }

        String slipText = exportService.generateBorrowSlip(selected);
        String title = "In Phiếu Mượn Sách - " + selected.getId();
        String heading = "Xem trước mẫu phiếu mượn in ấn (SRS Mục 6.1):";
        MainLayoutController.showReceiptPopup(title, heading, slipText);
    }

    // =========================================================================
    // 4. TRỢ LÝ WIZARD (4-STEP BORROWING WIZARD)
    // =========================================================================

    private void setupWizardTab() {
        if (comboWizardReader != null) {
            comboWizardReader.setCellFactory(lv -> new ListCell<>() {
                @Override
                protected void updateItem(Reader r, boolean empty) {
                    super.updateItem(r, empty);
                    if (empty || r == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        boolean valid = readerService != null && readerService.isCardValid(r);
                        String badge = valid ? "[✓ Hợp lệ]" : "[" + (r.getStatus() != null ? r.getStatus() : "Không hợp lệ") + "]";
                        setText(r.getFullName() + " (" + r.getId() + ") - " + badge);
                        setGraphic(null);
                    }
                }
            });
            comboWizardReader.setButtonCell(new ListCell<>() {
                @Override
                protected void updateItem(Reader r, boolean empty) {
                    super.updateItem(r, empty);
                    if (empty || r == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        boolean valid = readerService != null && readerService.isCardValid(r);
                        String badge = valid ? "[✓ Hợp lệ]" : "[" + (r.getStatus() != null ? r.getStatus() : "Không hợp lệ") + "]";
                        setText(r.getFullName() + " (" + r.getId() + ") - " + badge);
                        setGraphic(null);
                    }
                }
            });

            comboWizardReader.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    wizardReader = newVal;
                    updateWizardReaderInfo();
                }
            });
            comboWizardReader.valueProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    wizardReader = newVal;
                    updateWizardReaderInfo();
                }
            });
        }

        if (listWizardAvailableBooks != null) {
            listWizardAvailableBooks.setItems(filteredWizardAvailableBooks);
            listWizardAvailableBooks.setCellFactory(lv -> new ListCell<>() {
                @Override
                protected void updateItem(Book b, boolean empty) {
                    super.updateItem(b, empty);
                    if (empty || b == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        HBox box = new HBox(8);
                        box.setAlignment(Pos.CENTER_LEFT);
                        VBox v = new VBox(2);
                        Label title = new Label(b.getTitle());
                        title.getStyleClass().add("wizard-item-title");
                        title.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");
                        Label sub = new Label(b.getAuthor() + " • Kệ: " + b.getShelfLocation() + " • Còn: " + b.getAvailableCopies() + " cuốn");
                        sub.setStyle("-fx-text-fill: #9CA3AF; -fx-font-size: 11px;");
                        v.getChildren().addAll(title, sub);
                        HBox.setHgrow(v, Priority.ALWAYS);

                        Button add = new Button("+ Chọn");
                        add.getStyleClass().add("btn-secondary");
                        add.setStyle("-fx-font-size: 11px; -fx-padding: 3 8;");
                        add.setOnAction(e -> addWizardBook(b));

                        box.getChildren().addAll(v, add);
                        setGraphic(box);
                    }
                }
            });

            listWizardAvailableBooks.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2 && e.getButton() == MouseButton.PRIMARY) {
                    Book selected = listWizardAvailableBooks.getSelectionModel().getSelectedItem();
                    if (selected != null) {
                        addWizardBook(selected);
                    }
                }
            });
        }

        if (listWizardSelectedBooks != null) {
            listWizardSelectedBooks.setItems(wizardSelectedBooks);
            listWizardSelectedBooks.setCellFactory(lv -> new ListCell<>() {
                @Override
                protected void updateItem(Book b, boolean empty) {
                    super.updateItem(b, empty);
                    if (empty || b == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        HBox box = new HBox(8);
                        box.setAlignment(Pos.CENTER_LEFT);
                        VBox v = new VBox(2);
                        Label title = new Label(b.getTitle());
                        title.getStyleClass().add("wizard-item-title");
                        title.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");
                        Label sub = new Label("Mã: " + b.getId() + " • Tác giả: " + b.getAuthor());
                        sub.setStyle("-fx-text-fill: #9CA3AF; -fx-font-size: 11px;");
                        v.getChildren().addAll(title, sub);
                        HBox.setHgrow(v, Priority.ALWAYS);

                        Button remove = new Button("✕");
                        remove.getStyleClass().add("btn-secondary");
                        remove.setStyle("-fx-font-size: 11px; -fx-text-fill: #EF4444; -fx-padding: 2 6;");
                        remove.setOnAction(e -> removeWizardBook(b));

                        box.getChildren().addAll(v, remove);
                        setGraphic(box);
                    }
                }
            });
        }

        wizardLoanTypeGroup = new ToggleGroup();
        if (radioWizardHome != null && radioWizardOnsite != null) {
            radioWizardHome.setToggleGroup(wizardLoanTypeGroup);
            radioWizardOnsite.setToggleGroup(wizardLoanTypeGroup);
            radioWizardHome.setSelected(true);

            wizardLoanTypeGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
                if (radioWizardHome.isSelected()) {
                    wizardLoanType = "Mang về nhà";
                    int maxHome = settingService.getMaxBorrowDaysHome();
                    setWizardLoanDays(maxHome > 0 ? maxHome : 14);
                    if (lblWizardMaxDaysNotice != null) {
                        lblWizardMaxDaysNotice.setText("(Tối đa " + wizardLoanDays + " ngày đối với mang về)");
                    }
                } else {
                    wizardLoanType = "Mượn đọc tại chỗ";
                    int maxOnsite = settingService.getMaxBorrowDaysOnsite();
                    setWizardLoanDays(maxOnsite > 0 ? maxOnsite : 1);
                    if (lblWizardMaxDaysNotice != null) {
                        lblWizardMaxDaysNotice.setText("(Phải trả trong ngày)");
                    }
                }
                updateWizardDueDate();
            });
        }

        int defaultDays = settingService.getMaxBorrowDaysHome();
        if (defaultDays <= 0) defaultDays = 14;
        wizardLoanDays = defaultDays;

        if (spnWizardLoanDays != null) {
            spnWizardLoanDays.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 90, wizardLoanDays));
            spnWizardLoanDays.valueProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    wizardLoanDays = newVal;
                    updateWizardDueDate();
                }
            });
        }

        if (lblWizardBorrowDate != null) {
            lblWizardBorrowDate.setText(LocalDate.now().toString());
        }
        updateWizardDueDate();

        updateWizardStepUi();
    }

    public void loadWizardData() {
        List<Reader> all = readerService.getAllReaders();
        if (comboWizardReader != null) {
            Reader prev = getWizardReader();
            comboWizardReader.setItems(FXCollections.observableArrayList(all));
            if (prev != null) {
                for (Reader r : all) {
                    if (r.getId().equalsIgnoreCase(prev.getId())) {
                        wizardReader = r;
                        comboWizardReader.setValue(r);
                        comboWizardReader.getSelectionModel().select(r);
                        break;
                    }
                }
            }
        }

        List<Book> available = bookService.getAllBooks().stream()
                .filter(b -> b.getAvailableCopies() > 0 && !"Ngưng phục vụ".equalsIgnoreCase(b.getStatus()))
                .toList();
        wizardAvailableBooks.setAll(available);
        applyWizardBookFilter();
        updateWizardReaderInfo();
    }

    public void updateWizardStepUi() {
        Label[] nodes = new Label[]{stepNode1, stepNode2, stepNode3, stepNode4};
        for (int i = 0; i < nodes.length; i++) {
            Label node = nodes[i];
            if (node == null) continue;
            node.getStyleClass().removeAll("wizard-step-active", "wizard-step-done");
            if (!node.getStyleClass().contains("wizard-step-node")) {
                node.getStyleClass().add("wizard-step-node");
            }
            int stepNum = i + 1;
            if (stepNum == currentWizardStep) {
                node.getStyleClass().add("wizard-step-active");
            } else if (stepNum < currentWizardStep) {
                node.getStyleClass().add("wizard-step-done");
            }
        }

        VBox[] panes = new VBox[]{paneWizardStep1, paneWizardStep2, paneWizardStep3, paneWizardStep4};
        for (int i = 0; i < panes.length; i++) {
            VBox pane = panes[i];
            if (pane == null) continue;
            boolean active = (i + 1 == currentWizardStep);
            pane.setVisible(active);
            pane.setManaged(active);
        }

        if (btnWizardPrev != null) {
            btnWizardPrev.setDisable(currentWizardStep <= 1);
        }

        if (lblWizardStepIndicator != null) {
            switch (currentWizardStep) {
                case 1 -> lblWizardStepIndicator.setText("Bước 1 / 4: Chọn Độc Giả");
                case 2 -> lblWizardStepIndicator.setText("Bước 2 / 4: Chọn Sách Khả Dụng (" + wizardSelectedBooks.size() + " cuốn)");
                case 3 -> lblWizardStepIndicator.setText("Bước 3 / 4: Thiết Lập Hạn & Quy Định");
                case 4 -> lblWizardStepIndicator.setText("Bước 4 / 4: Xác Nhận & In Phiếu");
            }
        }

        if (btnWizardNext != null && btnWizardFinish != null) {
            if (currentWizardStep < 4) {
                btnWizardNext.setVisible(true);
                btnWizardNext.setManaged(true);
                btnWizardFinish.setVisible(false);
                btnWizardFinish.setManaged(false);
            } else {
                btnWizardNext.setVisible(false);
                btnWizardNext.setManaged(false);
                btnWizardFinish.setVisible(true);
                btnWizardFinish.setManaged(true);
            }
        }

        if (currentWizardStep == 1) {
            updateWizardReaderInfo();
        } else if (currentWizardStep == 2) {
            updateWizardStep2Ui();
        } else if (currentWizardStep == 3) {
            updateWizardDueDate();
        } else if (currentWizardStep == 4) {
            updateWizardStep4Preview();
        }
    }

    private void updateWizardDueDate() {
        LocalDate due = LocalDate.now().plusDays(getWizardLoanDays());
        if (lblWizardCalculatedDueDate != null) {
            lblWizardCalculatedDueDate.setText(due.toString() + " (" + getWizardLoanDays() + " ngày)");
        }
    }

    private void updateWizardReaderInfo() {
        Reader reader = getWizardReader();
        if (reader == null) {
            if (lblWizardReaderName != null) lblWizardReaderName.setText("-- Chưa chọn --");
            if (lblWizardReaderId != null) lblWizardReaderId.setText("--");
            if (lblWizardCardStatus != null) {
                lblWizardCardStatus.setText("--");
                lblWizardCardStatus.setStyle("-fx-text-fill: #CCCCCC;");
            }
            if (lblWizardCardExpiry != null) lblWizardCardExpiry.setText("--");
            if (lblWizardActiveLoans != null) lblWizardActiveLoans.setText("0 / 5 cuốn");
            if (lblWizardQuota != null) {
                lblWizardQuota.setText("5 cuốn");
                lblWizardQuota.setStyle("-fx-font-weight: bold; -fx-text-fill: #1DB954;");
            }
            if (lblWizardStep1Feedback != null) {
                lblWizardStep1Feedback.setText("Vui lòng chọn độc giả để tiến hành kiểm tra điều kiện mượn.");
                lblWizardStep1Feedback.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #F59E0B;");
            }
            return;
        }

        int maxAllowed = settingService.getMaxBooksPerReader();
        int activeLoans = borrowService.getActiveBorrowCountForReader(reader.getId());
        int remainingQuota = Math.max(0, maxAllowed - activeLoans);

        if (lblWizardReaderName != null) lblWizardReaderName.setText(reader.getFullName());
        if (lblWizardReaderId != null) lblWizardReaderId.setText(reader.getId() + " • " + (reader.getEmail() != null ? reader.getEmail() : ""));
        if (lblWizardCardExpiry != null) lblWizardCardExpiry.setText(reader.getCardExpiryDate() != null ? reader.getCardExpiryDate() : "--");
        if (lblWizardActiveLoans != null) lblWizardActiveLoans.setText(activeLoans + " / " + maxAllowed + " cuốn");
        if (lblWizardQuota != null) {
            lblWizardQuota.setText(remainingQuota + " cuốn");
            lblWizardQuota.setStyle(remainingQuota > 0 ? "-fx-font-weight: bold; -fx-text-fill: #1DB954;" : "-fx-font-weight: bold; -fx-text-fill: #EF4444;");
        }

        if (lblWizardQuotaRemainingHeader != null) {
            lblWizardQuotaRemainingHeader.setText("Hạn ngạch còn: " + remainingQuota + " cuốn");
            lblWizardQuotaRemainingHeader.setStyle(remainingQuota > 0 ? "-fx-font-weight: bold; -fx-text-fill: #1DB954; -fx-font-size: 12px;" : "-fx-font-weight: bold; -fx-text-fill: #EF4444; -fx-font-size: 12px;");
        }

        boolean isValid = readerService.isCardValid(reader);
        if (lblWizardCardStatus != null) {
            String status = reader.getStatus() != null ? reader.getStatus() : "Không xác định";
            lblWizardCardStatus.setText(status);
            if (isValid) {
                lblWizardCardStatus.setStyle("-fx-text-fill: #1DB954; -fx-font-weight: bold;");
            } else {
                lblWizardCardStatus.setStyle("-fx-text-fill: #EF4444; -fx-font-weight: bold;");
            }
        }

        if (lblWizardStep1Feedback != null) {
            String st = reader.getStatus() != null ? reader.getStatus().trim() : "";
            if ("Blocked".equalsIgnoreCase(st) || "KHOA".equalsIgnoreCase(st) || "Bị Khóa".equalsIgnoreCase(st) || "Khóa".equalsIgnoreCase(st)) {
                lblWizardStep1Feedback.setText("❌ THẺ BỊ KHÓA: Độc giả đang bị khóa tài khoản! Không thể mượn sách.");
                lblWizardStep1Feedback.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #EF4444;");
            } else if ("Expired".equalsIgnoreCase(st) || "Hết Hạn".equalsIgnoreCase(st) || (reader.getNgayHetHan() != null && reader.getNgayHetHan().isBefore(LocalDate.now()))) {
                lblWizardStep1Feedback.setText("❌ THẺ HẾT HẠN: Thẻ độc giả đã hết hạn sử dụng (" + (reader.getCardExpiryDate() != null ? reader.getCardExpiryDate() : "") + ")! Cần gia hạn thẻ trước.");
                lblWizardStep1Feedback.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #EF4444;");
            } else if ("Chờ Cấp Thẻ".equalsIgnoreCase(st) || "Cho Cap The".equalsIgnoreCase(st)) {
                lblWizardStep1Feedback.setText("⚠️ CHƯA CẤP THẺ: Độc giả chưa hoàn tất cấp thẻ tại quầy.");
                lblWizardStep1Feedback.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #F59E0B;");
            } else if (remainingQuota <= 0) {
                lblWizardStep1Feedback.setText("❌ HẾT HẠN NGẠCH: Độc giả đang mượn đủ " + activeLoans + "/" + maxAllowed + " cuốn! Cần trả sách trước khi mượn tiếp.");
                lblWizardStep1Feedback.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #EF4444;");
            } else {
                lblWizardStep1Feedback.setText("✓ THẺ HỢP LỆ: Đủ điều kiện lập phiếu. Được mượn thêm tối đa " + remainingQuota + " cuốn sách.");
                lblWizardStep1Feedback.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #1DB954;");
            }
        }
    }

    public int getWizardRemainingQuota() {
        Reader reader = getWizardReader();
        if (reader == null) return 0;
        int maxAllowed = settingService.getMaxBooksPerReader();
        int activeLoans = borrowService.getActiveBorrowCountForReader(reader.getId());
        return Math.max(0, maxAllowed - activeLoans);
    }

    public boolean addWizardBook(Book book) {
        if (book == null) return false;
        Reader reader = getWizardReader();
        if (reader == null) {
            showDeskAlert(Alert.AlertType.WARNING, "Chưa Chọn Độc Giả", "Vui lòng hoàn thành Bước 1 (chọn độc giả) trước!");
            return false;
        }

        for (Book b : wizardSelectedBooks) {
            if (b.getId().equalsIgnoreCase(book.getId())) {
                showDeskAlert(Alert.AlertType.WARNING, "Đã Chọn", "Cuốn sách này đã có trong danh sách chọn!");
                return false;
            }
        }

        if (book.getAvailableCopies() <= 0) {
            showDeskAlert(Alert.AlertType.WARNING, "Hết Sách", "Sách này hiện không còn bản nào khả dụng trong kho!");
            return false;
        }

        int remainingQuota = getWizardRemainingQuota();
        if (wizardSelectedBooks.size() >= remainingQuota) {
            showDeskAlert(Alert.AlertType.ERROR, "Vượt Hạn Ngạch",
                    "Hạn ngạch còn lại của độc giả chỉ được mượn thêm " + remainingQuota + " cuốn!");
            return false;
        }

        wizardSelectedBooks.add(book);
        updateWizardStep2Ui();
        return true;
    }

    public void removeWizardBook(Book book) {
        if (book == null) return;
        wizardSelectedBooks.removeIf(b -> b.getId().equalsIgnoreCase(book.getId()));
        updateWizardStep2Ui();
    }

    public void clearWizardSelectedBooks() {
        wizardSelectedBooks.clear();
        updateWizardStep2Ui();
    }

    private void updateWizardStep2Ui() {
        int count = wizardSelectedBooks.size();
        int remainingQuota = getWizardRemainingQuota();
        if (lblWizardSelectedCount != null) {
            lblWizardSelectedCount.setText("Đã chọn: " + count + " cuốn");
        }
        if (lblWizardStep2Feedback != null) {
            if (count == 0) {
                lblWizardStep2Feedback.setText("Chưa chọn sách nào. Vui lòng chọn ít nhất 1 cuốn.");
                lblWizardStep2Feedback.setStyle("-fx-text-fill: #9CA3AF;");
            } else if (count <= remainingQuota) {
                lblWizardStep2Feedback.setText("✓ Đã chọn " + count + " cuốn. Hợp lệ để tiếp tục bước tiếp theo.");
                lblWizardStep2Feedback.setStyle("-fx-text-fill: #1DB954; -fx-font-weight: bold;");
            } else {
                lblWizardStep2Feedback.setText("❌ Đã chọn vượt quá hạn ngạch cho phép (" + count + "/" + remainingQuota + ")!");
                lblWizardStep2Feedback.setStyle("-fx-text-fill: #EF4444; -fx-font-weight: bold;");
            }
        }
    }

    private void updateWizardStep4Preview() {
        if (txtWizardSlipPreview != null) {
            String notes = getWizardNotes();
            String previewText = exportService.generateWizardBorrowSlipPreview(
                    getWizardReader(),
                    new ArrayList<>(wizardSelectedBooks),
                    getWizardLoanType(),
                    getWizardLoanDays(),
                    notes
            );
            txtWizardSlipPreview.setText(previewText);
        }
    }

    public boolean validateStep1() {
        Reader reader = getWizardReader();
        if (reader == null) {
            showDeskAlert(Alert.AlertType.WARNING, "Chưa Chọn Độc Giả", "Vui lòng chọn độc giả trước khi tiếp tục!");
            if (lblWizardStep1Feedback != null) {
                lblWizardStep1Feedback.setText("❌ Chưa chọn độc giả!");
                lblWizardStep1Feedback.setStyle("-fx-text-fill: #EF4444; -fx-font-weight: bold;");
            }
            return false;
        }

        if (!readerService.isCardValid(reader)) {
            String reason = "Thẻ độc giả không hợp lệ hoặc đã hết hạn!";
            String st = reader.getStatus() != null ? reader.getStatus().trim() : "";
            if ("Blocked".equalsIgnoreCase(st) || "KHOA".equalsIgnoreCase(st) || "Bị Khóa".equalsIgnoreCase(st) || "Khóa".equalsIgnoreCase(st)) {
                reason = "Thẻ độc giả đang bị khóa tài khoản!";
            } else if ("Expired".equalsIgnoreCase(st) || "Hết Hạn".equalsIgnoreCase(st) || (reader.getNgayHetHan() != null && reader.getNgayHetHan().isBefore(LocalDate.now()))) {
                reason = "Thẻ độc giả đã hết hạn sử dụng (" + (reader.getCardExpiryDate() != null ? reader.getCardExpiryDate() : "") + ")!";
            } else if ("Chờ Cấp Thẻ".equalsIgnoreCase(st) || "Cho Cap The".equalsIgnoreCase(st)) {
                reason = "Độc giả chưa được cấp thẻ thư viện tại quầy!";
            }
            showDeskAlert(Alert.AlertType.ERROR, "Thẻ Không Hợp Lệ", reason);
            return false;
        }

        int remainingQuota = getWizardRemainingQuota();
        if (remainingQuota <= 0) {
            showDeskAlert(Alert.AlertType.ERROR, "Hết Hạn Ngạch", "Độc giả đã mượn tối đa 5 cuốn sách theo quy định!");
            return false;
        }

        return true;
    }

    public boolean validateStep2() {
        if (wizardSelectedBooks.isEmpty()) {
            showDeskAlert(Alert.AlertType.WARNING, "Chưa Chọn Sách", "Vui lòng chọn ít nhất 1 cuốn sách từ kho!");
            if (lblWizardStep2Feedback != null) {
                lblWizardStep2Feedback.setText("❌ Vui lòng chọn ít nhất 1 cuốn sách!");
                lblWizardStep2Feedback.setStyle("-fx-text-fill: #EF4444; -fx-font-weight: bold;");
            }
            return false;
        }

        int remainingQuota = getWizardRemainingQuota();
        if (wizardSelectedBooks.size() > remainingQuota) {
            showDeskAlert(Alert.AlertType.ERROR, "Vượt Hạn Ngạch",
                    "Số sách chọn (" + wizardSelectedBooks.size() + ") vượt quá hạn ngạch còn lại (" + remainingQuota + ")!");
            return false;
        }

        for (Book b : wizardSelectedBooks) {
            if (b.getAvailableCopies() <= 0) {
                showDeskAlert(Alert.AlertType.ERROR, "Sách Hết Bản Khả Dụng",
                        "Sách \"" + b.getTitle() + "\" đã hết bản khả dụng trong kho!");
                return false;
            }
        }

        return true;
    }

    public boolean validateStep3() {
        if (getWizardLoanDays() <= 0) {
            showDeskAlert(Alert.AlertType.WARNING, "Thời Hạn Không Hợp Lệ", "Thời hạn mượn sách phải lớn hơn 0 ngày!");
            return false;
        }
        return true;
    }

    public void goToWizardStep(int step) {
        if (step < 1) step = 1;
        if (step > 4) step = 4;
        this.currentWizardStep = step;
        updateWizardStepUi();
    }

    public boolean wizardNextStep() {
        if (currentWizardStep == 1) {
            if (!validateStep1()) return false;
            goToWizardStep(2);
            return true;
        } else if (currentWizardStep == 2) {
            if (!validateStep2()) return false;
            goToWizardStep(3);
            return true;
        } else if (currentWizardStep == 3) {
            if (!validateStep3()) return false;
            goToWizardStep(4);
            return true;
        }
        return false;
    }

    public boolean wizardPrevStep() {
        if (currentWizardStep > 1) {
            goToWizardStep(currentWizardStep - 1);
            return true;
        }
        return false;
    }

    public boolean wizardFinish() {
        if (!validateStep1() || !validateStep2() || !validateStep3()) {
            return false;
        }

        Reader reader = getWizardReader();
        List<BorrowTransaction> createdTransactions = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        String notes = getWizardNotes();
        if (notes == null || notes.trim().isEmpty()) {
            notes = "Mượn qua Trợ Lý Wizard (SRS Mục 6.1)";
        }
        String loanType = getWizardLoanType();
        int loanDays = getWizardLoanDays();

        for (Book book : new ArrayList<>(wizardSelectedBooks)) {
            String err = borrowService.createBorrowTransaction(
                    reader.getId(),
                    book.getId(),
                    loanType,
                    loanDays,
                    notes
            );
            if (err != null) {
                errors.add(book.getTitle() + ": " + err);
            } else {
                List<BorrowTransaction> txs = borrowService.getTransactionsByReader(reader.getId());
                for (BorrowTransaction tx : txs) {
                    if (tx.getBookId().equals(book.getId()) && "Đang Mượn".equalsIgnoreCase(tx.getStatus())) {
                        if (createdTransactions.stream().noneMatch(t -> t.getId().equals(tx.getId()))) {
                            createdTransactions.add(tx);
                            break;
                        }
                    }
                }
            }
        }

        if (!errors.isEmpty()) {
            showDeskAlert(Alert.AlertType.ERROR, "Lỗi Khi Lập Phiếu", String.join("\n", errors));
        }

        if (!createdTransactions.isEmpty()) {
            try {
                showReceiptDialog(reader, createdTransactions);
            } catch (Exception e) {
                logger.warn("Could not show receipt dialog: {}", e.getMessage());
            }

            // Remove only successfully checked-out books from wizard selection
            for (BorrowTransaction tx : createdTransactions) {
                wizardSelectedBooks.removeIf(b -> b.getId().equals(tx.getBookId()));
            }

            loadAllData();

            // If all selected books were checked out successfully, reset wizard to step 1
            if (wizardSelectedBooks.isEmpty()) {
                resetWizard();
            } else {
                // Some books failed; return to step 2 so user can review/adjust remaining items
                updateWizardStep2Ui();
                goToWizardStep(2);
            }
            return true;
        }
        return false;
    }

    public void resetWizard() {
        currentWizardStep = 1;
        wizardSelectedBooks.clear();
        setWizardReader(null);
        if (comboWizardReader != null) comboWizardReader.setValue(null);
        if (txtWizardBookSearch != null) txtWizardBookSearch.clear();
        if (txtWizardNotes != null) txtWizardNotes.clear();
        if (radioWizardHome != null) radioWizardHome.setSelected(true);
        wizardLoanType = "Mang về nhà";
        wizardLoanDays = settingService.getMaxBorrowDaysHome();
        if (wizardLoanDays <= 0) wizardLoanDays = 14;
        if (spnWizardLoanDays != null && spnWizardLoanDays.getValueFactory() != null) {
            spnWizardLoanDays.getValueFactory().setValue(wizardLoanDays);
        }
        updateWizardStepUi();
    }

    // Getters and Setters for Wizard State
    public int getCurrentWizardStep() {
        return currentWizardStep;
    }

    public void setCurrentWizardStep(int step) {
        this.currentWizardStep = step;
        updateWizardStepUi();
    }

    public Reader getWizardReader() {
        if (wizardReader != null) {
            return wizardReader;
        }
        if (comboWizardReader != null) {
            if (comboWizardReader.getValue() != null) {
                return comboWizardReader.getValue();
            }
            if (comboWizardReader.getSelectionModel() != null && comboWizardReader.getSelectionModel().getSelectedItem() != null) {
                return comboWizardReader.getSelectionModel().getSelectedItem();
            }
        }
        if (selectedReader != null) {
            return selectedReader;
        }
        return null;
    }

    public void setWizardReader(Reader reader) {
        this.wizardReader = reader;
        if (comboWizardReader != null && reader != null) {
            boolean found = false;
            for (Reader r : comboWizardReader.getItems()) {
                if (r.getId().equalsIgnoreCase(reader.getId())) {
                    comboWizardReader.setValue(r);
                    comboWizardReader.getSelectionModel().select(r);
                    found = true;
                    break;
                }
            }
            if (!found) {
                comboWizardReader.setValue(reader);
            }
        } else if (comboWizardReader != null) {
            comboWizardReader.setValue(null);
            if (comboWizardReader.getSelectionModel() != null) {
                comboWizardReader.getSelectionModel().clearSelection();
            }
        }
        updateWizardReaderInfo();
    }

    public ObservableList<Book> getWizardSelectedBooks() {
        return wizardSelectedBooks;
    }

    public String getWizardLoanType() {
        if (radioWizardOnsite != null && radioWizardOnsite.isSelected()) {
            return "Mượn đọc tại chỗ";
        }
        return wizardLoanType != null ? wizardLoanType : "Mang về nhà";
    }

    public void setWizardLoanType(String loanType) {
        this.wizardLoanType = loanType;
        if ("Mượn đọc tại chỗ".equalsIgnoreCase(loanType)) {
            if (radioWizardOnsite != null) radioWizardOnsite.setSelected(true);
        } else {
            if (radioWizardHome != null) radioWizardHome.setSelected(true);
        }
        updateWizardDueDate();
    }

    public int getWizardLoanDays() {
        if (spnWizardLoanDays != null && spnWizardLoanDays.getValue() != null) {
            return spnWizardLoanDays.getValue();
        }
        return wizardLoanDays;
    }

    public void setWizardLoanDays(int days) {
        this.wizardLoanDays = days;
        if (spnWizardLoanDays != null && spnWizardLoanDays.getValueFactory() != null) {
            spnWizardLoanDays.getValueFactory().setValue(days);
        }
        updateWizardDueDate();
    }

    public String getWizardNotes() {
        if (txtWizardNotes != null && !txtWizardNotes.getText().trim().isEmpty()) {
            return txtWizardNotes.getText().trim();
        }
        return wizardNotesText;
    }

    public void setWizardNotes(String notes) {
        this.wizardNotesText = notes;
        if (txtWizardNotes != null) {
            txtWizardNotes.setText(notes);
        }
    }

    // Wizard Event Handlers
    @FXML public void handleWizardNext() { wizardNextStep(); }
    @FXML public void handleWizardPrev() { wizardPrevStep(); }
    @FXML public void handleWizardFinish() { wizardFinish(); }
    @FXML public void handleWizardCancel() { resetWizard(); }

    @FXML
    public void handleSelectWizardReader() {
        if (comboWizardReader != null) {
            Reader r = comboWizardReader.getValue();
            if (r == null && comboWizardReader.getSelectionModel() != null) {
                r = comboWizardReader.getSelectionModel().getSelectedItem();
            }
            if (r != null) {
                this.wizardReader = r;
            }
            updateWizardReaderInfo();
        }
    }

    @FXML
    public void handleRefreshWizardReader() {
        loadWizardData();
    }

    @FXML
    public void handleWizardBookSearch() {
        applyWizardBookFilter();
    }

    private void applyWizardBookFilter() {
        String query = (txtWizardBookSearch != null && txtWizardBookSearch.getText() != null)
                ? txtWizardBookSearch.getText().trim().toLowerCase()
                : "";
        if (query.isEmpty()) {
            filteredWizardAvailableBooks.setAll(wizardAvailableBooks);
        } else {
            List<Book> filtered = wizardAvailableBooks.stream()
                    .filter(b -> (b.getTitle() != null && b.getTitle().toLowerCase().contains(query)) ||
                            (b.getAuthor() != null && b.getAuthor().toLowerCase().contains(query)) ||
                            (b.getId() != null && b.getId().toLowerCase().contains(query)) ||
                            (b.getCategory() != null && b.getCategory().toLowerCase().contains(query)))
                    .toList();
            filteredWizardAvailableBooks.setAll(filtered);
        }
    }

    @FXML
    public void handleWizardAddSelectedBook() {
        if (listWizardAvailableBooks != null) {
            Book selected = listWizardAvailableBooks.getSelectionModel().getSelectedItem();
            if (selected != null) {
                addWizardBook(selected);
            } else {
                showDeskAlert(Alert.AlertType.INFORMATION, "Chưa Chọn Sách", "Vui lòng chọn một cuốn sách từ danh sách khả dụng!");
            }
        }
    }

    @FXML
    public void handleWizardRemoveSelectedBook() {
        if (listWizardSelectedBooks != null) {
            Book selected = listWizardSelectedBooks.getSelectionModel().getSelectedItem();
            if (selected != null) {
                removeWizardBook(selected);
            }
        }
    }

    @FXML
    public void handleWizardClearSelectedBooks() {
        clearWizardSelectedBooks();
    }

    public ObservableList<BorrowTransaction> getAllTxList() {
        return allTxList;
    }
}
