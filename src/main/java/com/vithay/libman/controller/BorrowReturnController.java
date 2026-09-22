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

    private final ObservableList<CirculationBasketItem> basketItems = FXCollections.observableArrayList();
    private final ObservableList<Book> availableBooksList = FXCollections.observableArrayList();
    private final ObservableList<Book> filteredAvailableBooks = FXCollections.observableArrayList();
    private final ObservableList<BorrowTransaction> allTxList = FXCollections.observableArrayList();

    private Reader selectedReader;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupDeskTab();
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
        loadDeskData();
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

                    lblTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #FFFFFF; -fx-font-size: 12px;");
                    lblSub.setStyle("-fx-text-fill: #9CA3AF; -fx-font-size: 11px;");
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
            Reader previous = comboDeskReader.getValue();
            comboDeskReader.setItems(FXCollections.observableArrayList(readers));
            if (previous != null) {
                for (Reader r : readers) {
                    if (r.getId().equals(previous.getId())) {
                        comboDeskReader.setValue(r);
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
    }

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
        if (comboDeskReader != null && comboDeskReader.getValue() != null) {
            return comboDeskReader.getValue();
        }
        return selectedReader;
    }

    public void setSelectedReader(Reader reader) {
        this.selectedReader = reader;
        if (comboDeskReader != null) {
            comboDeskReader.setValue(reader);
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

            Label emptyIcon = new Label("📚");
            emptyIcon.setStyle("-fx-font-size: 32px;");

            Label emptyTitle = new Label("Bàn lưu hành hiện đang trống");
            emptyTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #FFFFFF; -fx-font-size: 14px;");

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
        thumbBox.setStyle("-fx-background-color: #262626; -fx-background-radius: 4px; -fx-border-color: #383838; -fx-border-radius: 4px;");

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
            Label iconLbl = new Label("📖");
            iconLbl.setStyle("-fx-font-size: 18px;");
            thumbBox.getChildren().add(iconLbl);
        }

        // 2. Book Info (Title, Author, Shelf location)
        VBox infoBox = new VBox(3);
        infoBox.setPrefWidth(200);
        HBox.setHgrow(infoBox, Priority.ALWAYS);

        Label lblTitle = new Label(book.getTitle());
        lblTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #FFFFFF; -fx-font-size: 13px;");
        lblTitle.setWrapText(true);

        Label lblMeta = new Label(book.getAuthor() + " • Kệ: " + book.getShelfLocation());
        lblMeta.setStyle("-fx-text-fill: #9CA3AF; -fx-font-size: 11px;");

        Label lblId = new Label("Mã: " + book.getId() + " | Còn: " + book.getAvailableCopies() + " cuốn");
        lblId.setStyle("-fx-text-fill: #6B7280; -fx-font-size: 10px;");

        infoBox.getChildren().addAll(lblTitle, lblMeta, lblId);

        // 3. Loan Type Selector
        VBox typeBox = new VBox(2);
        typeBox.setAlignment(Pos.CENTER_LEFT);
        Label lblTypeHeader = new Label("Hình thức:");
        lblTypeHeader.setStyle("-fx-text-fill: #9CA3AF; -fx-font-size: 10px;");
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
        lblDaysHeader.setStyle("-fx-text-fill: #9CA3AF; -fx-font-size: 10px;");

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
        lblDueHeader.setStyle("-fx-text-fill: #9CA3AF; -fx-font-size: 10px;");
        Label lblDueDate = new Label(item.getDueDate().toString());
        lblDueDate.setStyle("-fx-text-fill: #1DB954; -fx-font-weight: bold; -fx-font-size: 12px;");
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
        btnRemove.setStyle("-fx-background-color: transparent; -fx-text-fill: #EF4444; -fx-font-weight: bold; -fx-font-size: 14px; -fx-cursor: hand; -fx-padding: 4 8;");
        btnRemove.setOnMouseEntered(e -> btnRemove.setStyle("-fx-background-color: #3F1D1D; -fx-text-fill: #F87171; -fx-font-weight: bold; -fx-font-size: 14px; -fx-background-radius: 4px; -fx-cursor: hand; -fx-padding: 4 8;"));
        btnRemove.setOnMouseExited(e -> btnRemove.setStyle("-fx-background-color: transparent; -fx-text-fill: #EF4444; -fx-font-weight: bold; -fx-font-size: 14px; -fx-cursor: hand; -fx-padding: 4 8;"));
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
        if (lblSummaryQuotaStatus != null) {
            if (reader == null) {
                lblSummaryQuotaStatus.setText("⚠️ Chưa chọn độc giả");
                lblSummaryQuotaStatus.setStyle("-fx-text-fill: #F59E0B; -fx-font-size: 11px;");
                return;
            }

            int maxAllowed = settingService.getMaxBooksPerReader();
            int currentBorrowing = borrowService.getActiveBorrowCountForReader(reader.getId());
            int remainingQuota = Math.max(0, maxAllowed - currentBorrowing);

            if (count == 0) {
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
            String err = borrowService.borrowBook(
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
                List<BorrowTransaction> readerTxs = borrowService.searchTransactions(reader.getId());
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
            basketItems.clear();
            if (txtDeskNotes != null) txtDeskNotes.clear();
            loadAllData();
            updateDeskReaderInfo();
        }
    }

    private void showReceiptDialog(Reader reader, List<BorrowTransaction> transactions) {
        String slipText = exportService.generateBasketBorrowSlip(reader, transactions);

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("In Phiếu Mượn Sách - " + reader.getFullName());
        dialog.setHeaderText("Lập phiếu mượn thành công! Xem trước mẫu phiếu in (SRS Mục 6.1):");

        DialogPane pane = dialog.getDialogPane();
        try {
            pane.getStylesheets().add(getClass().getResource("/com/vithay/libman/css/style.css").toExternalForm());
        } catch (Exception ignored) {
        }
        pane.getStyleClass().add("bg-surface");
        pane.getButtonTypes().add(ButtonType.CLOSE);

        TextArea txt = new TextArea(slipText);
        txt.setEditable(false);
        txt.setPrefSize(580, 420);
        txt.setStyle("-fx-font-family: monospace; -fx-font-size: 12px; -fx-text-fill: #FFFFFF;");

        VBox box = new VBox(10, txt);
        box.setPadding(new Insets(12));
        pane.setContent(box);

        dialog.showAndWait();
    }

    private void showDeskAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type, message, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null);
        try {
            DialogPane pane = alert.getDialogPane();
            pane.getStylesheets().add(getClass().getResource("/com/vithay/libman/css/style.css").toExternalForm());
            pane.getStyleClass().add("bg-surface");
        } catch (Exception ignored) {
        }
        alert.showAndWait();
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
        List<BorrowTransaction> list = borrowService.getAllTransactions();
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
            Alert alert = new Alert(Alert.AlertType.WARNING, "Vui lòng chọn một phiếu mượn từ danh sách để in!");
            alert.showAndWait();
            return;
        }

        String slipText = exportService.generateBorrowSlip(selected);

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("In Phiếu Mượn Sách - " + selected.getId());
        dialog.setHeaderText("Xem trước mẫu phiếu mượn in ấn (SRS Mục 6.1):");

        DialogPane pane = dialog.getDialogPane();
        try {
            pane.getStylesheets().add(getClass().getResource("/com/vithay/libman/css/style.css").toExternalForm());
        } catch (Exception ignored) {
        }
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
