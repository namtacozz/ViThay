package com.vithay.libman.controller;

import com.vithay.libman.dao.BorrowTransactionDao;
import com.vithay.libman.model.Book;
import com.vithay.libman.model.BorrowTransaction;
import com.vithay.libman.model.Category;
import com.vithay.libman.service.AuthService;
import com.vithay.libman.service.BookService;
import com.vithay.libman.service.CategoryService;
import com.vithay.libman.service.ExportService;
import com.vithay.libman.util.VietnameseUtils;
import com.vithay.libman.view.component.BookCardView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class BookManagementController implements Initializable {
    private static final Logger logger = LoggerFactory.getLogger(BookManagementController.class);
    public static final double DIVIDER_RATIO = 0.68;

    @FXML private StackPane bookRootPane;
    @FXML private VBox bookMainContainer;
    @FXML private HBox breadcrumbContainer;
    @FXML private ComboBox<String> authorFilterCombo;
    @FXML private ComboBox<String> statusFilterCombo;
    private String currentCategoryFilter = "Tất cả";
    private String drilldownAuthor = null;
    private String drilldownShelf = null;

    @FXML private TextField txtSearchBook;
    @FXML private Button btnAddBook;
    @FXML private Button btnExportBooks;

    // Extras on Demand (Advanced Filters)
    @FXML private Button btnToggleAdvancedFilter;
    @FXML private VBox advancedFilterPane;
    @FXML private ComboBox<String> comboAdvCategory;
    @FXML private ComboBox<String> comboAdvShelf;
    @FXML private TextField txtAdvMinPrice;
    @FXML private TextField txtAdvMaxPrice;
    @FXML private ComboBox<String> comboAdvStockStatus;

    // In-Place Circulation History View (One-Window Drilldown)
    @FXML private VBox circulationHistoryPane;
    @FXML private Label lblCirculationBookTitle;
    @FXML private Label lblCirculationBookMeta;
    @FXML private TableView<BorrowTransaction> circulationTable;
    @FXML private TableColumn<BorrowTransaction, String> colTxId;
    @FXML private TableColumn<BorrowTransaction, String> colTxReaderName;
    @FXML private TableColumn<BorrowTransaction, String> colTxBorrowDate;
    @FXML private TableColumn<BorrowTransaction, String> colTxDueDate;
    @FXML private TableColumn<BorrowTransaction, String> colTxReturnDate;
    @FXML private TableColumn<BorrowTransaction, String> colTxType;
    @FXML private TableColumn<BorrowTransaction, String> colTxStatus;
    @FXML private TableColumn<BorrowTransaction, Double> colTxFine;
    private boolean isCirculationMode = false;
    private Book circulationBook = null;

    // Two-Panel Selector: SplitPane & Master/Inspector Containers
    @FXML private SplitPane bookSplitPane;
    @FXML private StackPane bookMasterPane;
    @FXML private VBox detailInspectorPane;

    // Detail Inspector UI Controls
    @FXML private ImageView imgInspectorCover;
    @FXML private Label lblInspectorTitle;
    @FXML private Label lblInspectorAuthor;
    @FXML private Label lblInspectorCategory;
    @FXML private Label lblInspectorStatus;
    @FXML private Label lblInspectorShelf;
    @FXML private Label lblInspectorIsbn;
    @FXML private Label lblInspectorPrice;
    @FXML private Label lblInspectorCopies;
    @FXML private Button btnInspectorBorrow;
    @FXML private Button btnInspectorEdit;
    @FXML private Button btnInspectorDelete;
    @FXML private VBox inspectorBranchesBox;

    private Book currentSelectedBook = null;
    private MainLayoutController mainController = null;

    @FXML private TableView<Book> booksTable;
    @FXML private TableColumn<Book, String> colBookId;
    @FXML private TableColumn<Book, String> colBookTitle;
    @FXML private TableColumn<Book, String> colBookAuthor;
    @FXML private TableColumn<Book, String> colBookCategory;
    @FXML private TableColumn<Book, String> colBookShelf;
    @FXML private TableColumn<Book, Double> colBookPrice;
    @FXML private TableColumn<Book, Integer> colBookAvailable;
    @FXML private TableColumn<Book, String> colBookStatus;
    @FXML private TableColumn<Book, Void> colBookActions;

    // Alternative Views (Table & Grid Card View)
    @FXML private Button btnTableView;
    @FXML private Button btnGridView;
    @FXML private ScrollPane booksGridScrollPane;
    @FXML private FlowPane booksGridPane;
    private boolean isTableViewMode = true;
    private ObservableList<Book> currentFilteredBooks = FXCollections.observableArrayList();

    // In-Window Modal Overlay Fields
    @FXML private StackPane bookModalOverlay;
    @FXML private VBox bookModalBox;
    @FXML private Label lblBookModalTitle;
    @FXML private Label lblBookModalError;
    @FXML private TextField txtModalBookId;
    @FXML private TextField txtModalBookTitle;
    @FXML private TextField txtModalBookAuthor;
    @FXML private ComboBox<Category> comboModalBookCategory;
    @FXML private TextField txtModalBookShelf;
    @FXML private TextField txtModalBookIsbn;
    @FXML private TextField txtModalBookPrice;
    @FXML private TextField txtModalBookCopies;
    @FXML private ComboBox<String> comboModalBookStatus;
    private Book currentEditingBook = null;

    private final BookService bookService = new BookService();
    private final CategoryService categoryService = new CategoryService();
    private final ExportService exportService = new ExportService();
    private final BorrowTransactionDao borrowTransactionDao = new BorrowTransactionDao();

    private final ObservableList<Book> bookMasterList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (bookSplitPane != null) {
            bookSplitPane.setDividerPositions(DIVIDER_RATIO);
        }
        setupFilters();
        setupAdvancedFilters();
        setupCirculationTable();
        setupTable();
        loadBooks();
        switchView(true);
        updateBreadcrumbs();
    }

    private void setupFilters() {
        statusFilterCombo.setItems(FXCollections.observableArrayList(
                "Tất cả", "Available", "Borrowed", "On Hold"
        ));
        statusFilterCombo.getSelectionModel().selectFirst();
        refreshAuthorFilterOptions();
    }

    private void setupAdvancedFilters() {
        if (comboAdvStockStatus != null) {
            comboAdvStockStatus.setItems(FXCollections.observableArrayList(
                    "Tất cả", "Còn sách (In Stock)", "Hết sách (Out of Stock)"
            ));
            comboAdvStockStatus.getSelectionModel().selectFirst();
        }
        refreshAdvancedFilterOptions();
    }

    private void refreshAdvancedFilterOptions() {
        if (comboAdvCategory != null) {
            ObservableList<String> categories = FXCollections.observableArrayList("Tất cả");
            for (Category c : categoryService.getAllCategories()) {
                categories.add(c.getName());
            }
            comboAdvCategory.setItems(categories);
            if (comboAdvCategory.getValue() == null) {
                comboAdvCategory.getSelectionModel().selectFirst();
            }
        }

        if (comboAdvShelf != null) {
            ObservableList<String> shelves = FXCollections.observableArrayList("Tất cả");
            java.util.Set<String> uniqueShelves = new java.util.TreeSet<>(String.CASE_INSENSITIVE_ORDER);
            for (Book b : bookMasterList) {
                if (b.getShelfLocation() != null && !b.getShelfLocation().trim().isEmpty()) {
                    uniqueShelves.add(b.getShelfLocation().trim());
                }
            }
            shelves.addAll(uniqueShelves);
            comboAdvShelf.setItems(shelves);
            if (comboAdvShelf.getValue() == null) {
                comboAdvShelf.getSelectionModel().selectFirst();
            }
        }
    }

    private void setupCirculationTable() {
        if (circulationTable == null) return;
        colTxId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTxReaderName.setCellValueFactory(new PropertyValueFactory<>("readerName"));
        colTxBorrowDate.setCellValueFactory(new PropertyValueFactory<>("borrowDate"));
        colTxDueDate.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        colTxReturnDate.setCellValueFactory(new PropertyValueFactory<>("returnDate"));
        colTxReturnDate.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(String date, boolean empty) {
                super.updateItem(date, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(date != null && !date.isBlank() ? date : "— (Chưa trả)");
                }
            }
        });
        colTxType.setCellValueFactory(new PropertyValueFactory<>("borrowType"));
        colTxStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colTxStatus.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    Label badge = new Label(item);
                    if ("Đã Trả".equalsIgnoreCase(item) || "Available".equalsIgnoreCase(item)) {
                        badge.getStyleClass().add("badge-available");
                    } else if ("Đang Mượn".equalsIgnoreCase(item) || "Borrowed".equalsIgnoreCase(item)) {
                        badge.getStyleClass().add("badge-borrowed");
                    } else if ("Quá Hạn".equalsIgnoreCase(item) || "Overdue".equalsIgnoreCase(item)) {
                        badge.getStyleClass().add("badge-overdue");
                    } else {
                        badge.getStyleClass().add("badge-onhold");
                    }
                    HBox box = new HBox(badge);
                    box.setAlignment(Pos.CENTER);
                    setGraphic(box);
                    setText(null);
                }
            }
        });
        colTxFine.setCellValueFactory(new PropertyValueFactory<>("fineAmount"));
        colTxFine.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Double fine, boolean empty) {
                super.updateItem(fine, empty);
                if (empty || fine == null) {
                    setText(null);
                } else {
                    setText(fine > 0 ? String.format("%,.0f đ", fine) : "0 đ");
                }
            }
        });
    }

    private void refreshAuthorFilterOptions() {
        if (authorFilterCombo == null) return;
        ObservableList<String> items = FXCollections.observableArrayList("Tất cả");
        java.util.Set<String> uniqueAuthors = new java.util.TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        for (Book b : bookMasterList) {
            if (b.getAuthor() != null && !b.getAuthor().trim().isEmpty()) {
                uniqueAuthors.add(b.getAuthor().trim());
            }
        }
        items.addAll(uniqueAuthors);
        authorFilterCombo.setItems(items);
        if (authorFilterCombo.getValue() == null || !items.contains(authorFilterCombo.getValue())) {
            authorFilterCombo.getSelectionModel().selectFirst();
        }
    }

    private void setupTable() {
        colBookId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colBookTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colBookAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));
        colBookCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colBookShelf.setCellValueFactory(new PropertyValueFactory<>("shelfLocation"));
        colBookPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colBookAvailable.setCellValueFactory(new PropertyValueFactory<>("availableCopies"));

        // Format Price Column
        colBookPrice.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText(String.format("%,.0f đ", price));
                }
            }
        });

        // Status Badge Cell
        colBookStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colBookStatus.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    Label badge = new Label(item);
                    if ("Available".equalsIgnoreCase(item)) {
                        badge.getStyleClass().add("badge-available");
                    } else if ("Borrowed".equalsIgnoreCase(item)) {
                        badge.getStyleClass().add("badge-borrowed");
                    } else if ("On Hold".equalsIgnoreCase(item)) {
                        badge.getStyleClass().add("badge-onhold");
                    } else {
                        badge.getStyleClass().add("badge-returned");
                    }
                    HBox box = new HBox(badge);
                    box.setAlignment(Pos.CENTER);
                    setGraphic(box);
                    setText(null);
                }
            }
        });

        // Action Buttons: Edit, Move to Recycle Bin (Xóa tạm)
        colBookActions.setCellFactory(param -> new TableCell<>() {
            private final Button btnEdit = new Button("Sửa");
            private final Button btnDelete = new Button("Xóa");
            private final HBox pane = new HBox(6, btnEdit, btnDelete);

            {
                pane.setAlignment(Pos.CENTER);
                btnEdit.getStyleClass().add("btn-secondary");
                btnEdit.setStyle("-fx-font-size: 11px; -fx-padding: 3 8;");
                btnEdit.setOnAction(e -> {
                    Book book = getTableView().getItems().get(getIndex());
                    showBookFormModal(book);
                });

                btnDelete.getStyleClass().add("btn-danger");
                btnDelete.setStyle("-fx-font-size: 11px; -fx-padding: 3 8;");
                btnDelete.setOnAction(e -> {
                    Book book = getTableView().getItems().get(getIndex());
                    handleSoftDeleteBook(book);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });

        booksTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            showBookDetail(newVal);
            if (!isTableViewMode && booksGridPane != null) {
                renderGridView(currentFilteredBooks);
            }
        });

        booksTable.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.UP || event.getCode() == KeyCode.DOWN) {
                Book selected = booksTable.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    showBookDetail(selected);
                }
            }
        });
    }

    public void loadBooks() {
        applySecurityPermissions();
        List<Book> list = bookService.getAllBooks();
        bookMasterList.setAll(list);
        refreshAuthorFilterOptions();
        refreshAdvancedFilterOptions();
        applyFilters();
    }

    public void applySecurityPermissions() {
        boolean canManage = AuthService.getInstance().isLibrarian();
        if (btnAddBook != null) {
            btnAddBook.setVisible(canManage);
            btnAddBook.setManaged(canManage);
        }
        if (btnExportBooks != null) {
            btnExportBooks.setVisible(canManage);
            btnExportBooks.setManaged(canManage);
        }
        if (colBookActions != null) {
            colBookActions.setVisible(canManage);
        }
        if (btnInspectorEdit != null) {
            btnInspectorEdit.setVisible(canManage);
            btnInspectorEdit.setManaged(canManage);
        }
        if (btnInspectorDelete != null) {
            btnInspectorDelete.setVisible(canManage);
            btnInspectorDelete.setManaged(canManage);
        }
    }

    @FXML
    public void handleAuthorFilter() {
        this.drilldownAuthor = null;
        applyFilters();
    }

    @FXML
    public void handleStatusFilter() {
        applyFilters();
    }

    @FXML
    public void handleSearchBook() {
        if (isCirculationMode) {
            exitCirculationMode();
        }
        applyFilters();
    }

    @FXML
    public void handleToggleAdvancedFilter() {
        if (advancedFilterPane == null) return;
        boolean isVisible = !advancedFilterPane.isVisible();
        advancedFilterPane.setVisible(isVisible);
        advancedFilterPane.setManaged(isVisible);
        if (btnToggleAdvancedFilter != null) {
            btnToggleAdvancedFilter.setText(isVisible ? "Lọc nâng cao ▴" : "Lọc nâng cao ▾");
        }
    }

    @FXML
    public void handleAdvancedFilterChanged() {
        applyFilters();
    }

    @FXML
    public void handleResetAdvancedFilter() {
        if (comboAdvCategory != null) comboAdvCategory.getSelectionModel().selectFirst();
        if (comboAdvShelf != null) comboAdvShelf.setValue("Tất cả");
        if (txtAdvMinPrice != null) txtAdvMinPrice.clear();
        if (txtAdvMaxPrice != null) txtAdvMaxPrice.clear();
        if (comboAdvStockStatus != null) comboAdvStockStatus.getSelectionModel().selectFirst();
        this.drilldownAuthor = null;
        this.drilldownShelf = null;
        applyFilters();
    }

    public void filterBooks(String keyword) {
        if (txtSearchBook != null) {
            txtSearchBook.setText(keyword != null ? keyword : "");
            applyFilters();
        }
    }

    public void filterByCategory(String categoryName) {
        if (categoryName == null || "Tất cả".equalsIgnoreCase(categoryName) || categoryName.trim().isEmpty()) {
            this.currentCategoryFilter = "Tất cả";
        } else {
            this.currentCategoryFilter = categoryName.trim();
        }
        if (comboAdvCategory != null && comboAdvCategory.getItems().contains(this.currentCategoryFilter)) {
            comboAdvCategory.setValue(this.currentCategoryFilter);
        }
        if (isCirculationMode) {
            exitCirculationMode();
        }
        applyFilters();
    }

    private void applyFilters() {
        String selectedAuthor = authorFilterCombo != null ? authorFilterCombo.getValue() : "Tất cả";
        String selectedStatus = statusFilterCombo != null ? statusFilterCombo.getValue() : "Tất cả";
        String search = txtSearchBook != null && txtSearchBook.getText() != null ? txtSearchBook.getText().trim() : "";

        String advCategory = comboAdvCategory != null ? comboAdvCategory.getValue() : "Tất cả";
        String advShelf = comboAdvShelf != null && comboAdvShelf.getValue() != null ? comboAdvShelf.getValue().trim() : "Tất cả";
        String advStock = comboAdvStockStatus != null ? comboAdvStockStatus.getValue() : "Tất cả";

        Double minPrice = null;
        if (txtAdvMinPrice != null && !txtAdvMinPrice.getText().trim().isEmpty()) {
            try {
                minPrice = Double.parseDouble(txtAdvMinPrice.getText().trim());
            } catch (NumberFormatException ignored) {}
        }

        Double maxPrice = null;
        if (txtAdvMaxPrice != null && !txtAdvMaxPrice.getText().trim().isEmpty()) {
            try {
                maxPrice = Double.parseDouble(txtAdvMaxPrice.getText().trim());
            } catch (NumberFormatException ignored) {}
        }

        ObservableList<Book> filtered = FXCollections.observableArrayList();
        for (Book b : bookMasterList) {
            // 1. Category Filter
            boolean matchesCategory = true;
            String activeCategory = (currentCategoryFilter != null && !"Tất cả".equalsIgnoreCase(currentCategoryFilter))
                    ? currentCategoryFilter
                    : (advCategory != null && !"Tất cả".equalsIgnoreCase(advCategory) ? advCategory : null);

            if (activeCategory != null) {
                String cat = b.getCategory() != null ? b.getCategory() : "";
                matchesCategory = VietnameseUtils.matches(cat, activeCategory) ||
                        VietnameseUtils.matches(activeCategory, cat) ||
                        (activeCategory.toLowerCase().contains("toán") && cat.toLowerCase().contains("toán"));
            }

            // 2. Author Filter (from combo or drilldown)
            boolean matchesAuthor = true;
            if (drilldownAuthor != null && !drilldownAuthor.isBlank()) {
                matchesAuthor = b.getAuthor() != null && VietnameseUtils.matches(b.getAuthor(), drilldownAuthor);
            } else if (selectedAuthor != null && !"Tất cả".equalsIgnoreCase(selectedAuthor)) {
                matchesAuthor = b.getAuthor() != null && VietnameseUtils.matches(b.getAuthor(), selectedAuthor);
            }

            // 3. Shelf Filter (from combo or drilldown)
            boolean matchesShelf = true;
            if (drilldownShelf != null && !drilldownShelf.isBlank()) {
                matchesShelf = b.getShelfLocation() != null && VietnameseUtils.matches(b.getShelfLocation(), drilldownShelf);
            } else if (advShelf != null && !"Tất cả".equalsIgnoreCase(advShelf) && !advShelf.isBlank()) {
                matchesShelf = b.getShelfLocation() != null && VietnameseUtils.matches(b.getShelfLocation(), advShelf);
            }

            // 4. Status Filter
            boolean matchesStatus = selectedStatus == null || "Tất cả".equalsIgnoreCase(selectedStatus) ||
                    (b.getStatus() != null && selectedStatus.equalsIgnoreCase(b.getStatus()));

            // 5. Price Range Filter
            boolean matchesMinPrice = (minPrice == null) || (b.getPrice() >= minPrice);
            boolean matchesMaxPrice = (maxPrice == null) || (b.getPrice() <= maxPrice);

            // 6. Stock Status Filter
            boolean matchesStock = true;
            if (advStock != null) {
                if (advStock.toLowerCase().contains("còn") || advStock.toLowerCase().contains("stock")) {
                    matchesStock = b.getAvailableCopies() > 0;
                } else if (advStock.toLowerCase().contains("hết") || advStock.toLowerCase().contains("out")) {
                    matchesStock = b.getAvailableCopies() <= 0;
                }
            }

            // 7. Search Keyword Filter
            boolean matchesSearch = search.isEmpty() ||
                    VietnameseUtils.matches(b.getId(), search) ||
                    VietnameseUtils.matches(b.getTitle(), search) ||
                    VietnameseUtils.matches(b.getAuthor(), search) ||
                    VietnameseUtils.matches(b.getShelfLocation(), search) ||
                    VietnameseUtils.matches(b.getIsbn(), search);

            if (matchesCategory && matchesAuthor && matchesShelf && matchesStatus &&
                    matchesMinPrice && matchesMaxPrice && matchesStock && matchesSearch) {
                filtered.add(b);
            }
        }
        this.currentFilteredBooks = filtered;
        if (booksTable != null) {
            booksTable.setItems(filtered);
            Book toSelect = null;
            if (currentSelectedBook != null) {
                for (Book b : filtered) {
                    if (java.util.Objects.equals(b.getId(), currentSelectedBook.getId())) {
                        toSelect = b;
                        break;
                    }
                }
            }
            if (toSelect == null && !filtered.isEmpty()) {
                toSelect = filtered.get(0);
            }
            if (toSelect != null) {
                booksTable.getSelectionModel().select(toSelect);
                showBookDetail(toSelect);
            } else {
                booksTable.getSelectionModel().clearSelection();
                showBookDetail(null);
            }
        }
        if (!isTableViewMode && booksGridPane != null) {
            renderGridView(filtered);
        }
        updateBreadcrumbs();
    }

    public boolean isTableViewMode() {
        return isTableViewMode;
    }

    public void setTableViewMode(boolean tableViewMode) {
        switchView(tableViewMode);
    }

    @FXML
    public void handleSwitchToTableView() {
        switchView(true);
    }

    @FXML
    public void handleSwitchToGridView() {
        switchView(false);
    }

    public void switchView(boolean isTable) {
        this.isTableViewMode = isTable;
        if (isCirculationMode) {
            exitCirculationMode();
        }
        if (btnTableView != null && btnGridView != null) {
            if (isTable) {
                if (!btnTableView.getStyleClass().contains("segmented-view-active")) {
                    btnTableView.getStyleClass().add("segmented-view-active");
                }
                btnGridView.getStyleClass().remove("segmented-view-active");
            } else {
                if (!btnGridView.getStyleClass().contains("segmented-view-active")) {
                    btnGridView.getStyleClass().add("segmented-view-active");
                }
                btnTableView.getStyleClass().remove("segmented-view-active");
            }
        }
        if (booksTable != null) {
            booksTable.setVisible(isTable);
            booksTable.setManaged(isTable);
        }
        if (booksGridScrollPane != null) {
            booksGridScrollPane.setVisible(!isTable);
            booksGridScrollPane.setManaged(!isTable);
        }
        if (!isTable) {
            renderGridView(currentFilteredBooks);
        }
    }

    private void renderGridView(List<Book> books) {
        if (booksGridPane == null) return;
        booksGridPane.getChildren().clear();
        Book selected = booksTable != null ? booksTable.getSelectionModel().getSelectedItem() : null;
        for (Book b : books) {
            boolean isSelected = selected != null && java.util.Objects.equals(selected.getId(), b.getId());
            VBox card = BookCardView.createCard(b, isSelected, clickedBook -> {
                if (booksTable != null) {
                    booksTable.getSelectionModel().select(clickedBook);
                }
            });
            booksGridPane.getChildren().add(card);
        }
    }

    @FXML
    public void handleExportCsv() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Lưu Danh Sách Sách (CSV)");
        chooser.setInitialFileName("danh_sach_sach.csv");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files (*.csv)", "*.csv"));
        File file = chooser.showSaveDialog(booksTable.getScene().getWindow());
        if (file != null) {
            boolean ok = exportService.exportBooksToCsv(file);
            if (ok) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Xuất danh sách sách thành công vào tệp: " + file.getName());
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Lỗi khi xuất danh sách sách ra CSV!");
                alert.showAndWait();
            }
        }
    }

    @FXML
    public void handleOpenAddDialog() {
        showBookFormModal(null);
    }

    public void showBookFormModal(Book bookToEdit) {
        this.currentEditingBook = bookToEdit;
        lblBookModalError.setText("");

        // Populate Category items
        comboModalBookCategory.setItems(FXCollections.observableArrayList(categoryService.getAllCategories()));
        comboModalBookStatus.setItems(FXCollections.observableArrayList("Available", "Borrowed", "On Hold"));

        if (bookToEdit != null) {
            lblBookModalTitle.setText("Chỉnh Sửa Thông Tin Sách");
            txtModalBookId.setText(bookToEdit.getId());
            txtModalBookId.setDisable(true);
            txtModalBookTitle.setText(bookToEdit.getTitle());
            txtModalBookAuthor.setText(bookToEdit.getAuthor());

            for (Category c : comboModalBookCategory.getItems()) {
                if (c.getName().equalsIgnoreCase(bookToEdit.getCategory())) {
                    comboModalBookCategory.setValue(c);
                    break;
                }
            }
            txtModalBookShelf.setText(bookToEdit.getShelfLocation());
            txtModalBookIsbn.setText(bookToEdit.getIsbn());
            txtModalBookPrice.setText(String.valueOf((long) bookToEdit.getPrice()));
            txtModalBookCopies.setText(String.valueOf(bookToEdit.getTotalCopies()));
            comboModalBookStatus.setValue(bookToEdit.getStatus());
        } else {
            lblBookModalTitle.setText("Thêm Sách Mới");
            txtModalBookId.setText("BK0" + (bookMasterList.size() + 1));
            txtModalBookId.setDisable(false);
            txtModalBookTitle.setText("");
            txtModalBookAuthor.setText("");
            if (!comboModalBookCategory.getItems().isEmpty()) {
                comboModalBookCategory.getSelectionModel().selectFirst();
            }
            txtModalBookShelf.setText("Khu TN - Kệ 01");
            txtModalBookIsbn.setText("978-604-");
            txtModalBookPrice.setText("95000");
            txtModalBookCopies.setText("5");
            comboModalBookStatus.setValue("Available");
        }

        bookMainContainer.setEffect(new GaussianBlur(14));
        bookModalOverlay.setVisible(true);
        bookModalOverlay.setManaged(true);
    }

    @FXML
    public void closeBookModal() {
        bookMainContainer.setEffect(null);
        bookModalOverlay.setVisible(false);
        bookModalOverlay.setManaged(false);
    }

    @FXML
    public void handleSaveBookModal() {
        String id = txtModalBookId.getText() != null ? txtModalBookId.getText().trim() : "";
        String title = txtModalBookTitle.getText() != null ? txtModalBookTitle.getText().trim() : "";
        String author = txtModalBookAuthor.getText() != null ? txtModalBookAuthor.getText().trim() : "";
        Category cat = comboModalBookCategory.getValue();

        if (id.isEmpty() || title.isEmpty() || author.isEmpty()) {
            lblBookModalError.setText("Vui lòng điền đầy đủ: Mã sách, Tên sách và Tác giả!");
            lblBookModalError.setStyle("-fx-text-fill: #EF4444;");
            return;
        }

        try {
            double price = Double.parseDouble(txtModalBookPrice.getText().trim());
            int copies = Integer.parseInt(txtModalBookCopies.getText().trim());
            int available = currentEditingBook != null ? Math.min(currentEditingBook.getAvailableCopies(), copies) : copies;
            String cover = currentEditingBook != null ? currentEditingBook.getCoverImage() : "/com/vithay/libman/images/doraemon.jpg";

            Book book = new Book(
                    id,
                    title,
                    author,
                    cat != null ? cat.getName() : "Khác",
                    cat != null ? cat.getId() : 0,
                    txtModalBookShelf.getText().trim(),
                    txtModalBookIsbn.getText().trim(),
                    price,
                    2024,
                    "NXB Kim Đồng",
                    comboModalBookStatus.getValue() != null ? comboModalBookStatus.getValue() : "Available",
                    cover,
                    copies,
                    available,
                    false
            );

            boolean ok = bookService.saveBook(book);
            if (ok) {
                closeBookModal();
                loadBooks();
            } else {
                lblBookModalError.setText("Lỗi: Không thể lưu sách (Mã sách có thể đã trùng)!");
                lblBookModalError.setStyle("-fx-text-fill: #EF4444;");
            }
        } catch (NumberFormatException e) {
            lblBookModalError.setText("Lỗi: Đơn giá và số lượng phải là số hợp lệ!");
            lblBookModalError.setStyle("-fx-text-fill: #EF4444;");
        }
    }

    private void handleSoftDeleteBook(Book book) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác Nhận Xóa Sách");
        alert.setHeaderText("Chuyển sách '" + book.getTitle() + "' vào Thùng Rác?");
        alert.setContentText("Sách sẽ được ẩn khỏi kho mượn nhưng có thể phục hồi lại từ Thùng Rác bất cứ lúc nào.");

        Optional<ButtonType> res = alert.showAndWait();
        if (res.isPresent() && res.get() == ButtonType.OK) {
            boolean ok = bookService.softDeleteBook(book.getId());
            if (ok) {
                loadBooks();
            }
        }
    }

    public Book getCurrentSelectedBook() {
        return currentSelectedBook;
    }

    public void setMainController(MainLayoutController mainController) {
        this.mainController = mainController;
    }

    public void selectAndInspectBook(Book book) {
        if (book == null) return;
        Book target = null;
        for (Book b : bookMasterList) {
            if (b.getId() != null && b.getId().equals(book.getId())) {
                target = b;
                break;
            }
        }
        if (target == null) target = book;
        this.currentSelectedBook = target;
        if (booksTable != null) {
            booksTable.getSelectionModel().select(target);
            booksTable.scrollTo(target);
        }
        showBookDetail(target);
    }

    public void showBookDetail(Book book) {
        if (book != null && isCirculationMode && circulationBook != null && !java.util.Objects.equals(circulationBook.getId(), book.getId())) {
            exitCirculationMode();
        }
        this.currentSelectedBook = book;
        if (detailInspectorPane == null) return;

        if (book == null) {
            detailInspectorPane.setVisible(false);
            detailInspectorPane.setManaged(false);
            if (inspectorBranchesBox != null) {
                inspectorBranchesBox.getChildren().clear();
            }
            updateBreadcrumbs();
            return;
        }

        detailInspectorPane.setVisible(true);
        detailInspectorPane.setManaged(true);

        if (lblInspectorTitle != null) {
            lblInspectorTitle.setText(book.getTitle() != null ? book.getTitle() : "Không có tiêu đề");
        }
        if (lblInspectorAuthor != null) {
            lblInspectorAuthor.setText(book.getAuthor() != null ? book.getAuthor() : "Không rõ tác giả");
            lblInspectorAuthor.setStyle("-fx-cursor: hand;");
            lblInspectorAuthor.setOnMouseClicked(e -> drilldownByAuthor(book.getAuthor()));
        }
        if (lblInspectorCategory != null) {
            lblInspectorCategory.setText(book.getCategory() != null ? book.getCategory() : "Khác");
        }
        if (lblInspectorShelf != null) {
            lblInspectorShelf.setText(book.getShelfLocation() != null ? book.getShelfLocation() : "Chưa xếp kệ");
            lblInspectorShelf.setStyle("-fx-cursor: hand;");
            lblInspectorShelf.setOnMouseClicked(e -> drilldownByShelf(book.getShelfLocation()));
        }
        if (lblInspectorIsbn != null) {
            lblInspectorIsbn.setText(book.getIsbn() != null && !book.getIsbn().isBlank() ? book.getIsbn() : "Chưa có ISBN");
        }
        if (lblInspectorPrice != null) {
            lblInspectorPrice.setText(String.format("%,.0f đ", book.getPrice()));
        }
        if (lblInspectorCopies != null) {
            lblInspectorCopies.setText(book.getAvailableCopies() + " / " + book.getTotalCopies() + " bản");
        }

        // Status badge
        if (lblInspectorStatus != null) {
            String status = book.getStatus() != null ? book.getStatus() : "Available";
            lblInspectorStatus.setText(status);
            lblInspectorStatus.getStyleClass().removeAll("badge-available", "badge-borrowed", "badge-onhold", "badge-returned", "badge-overdue");
            if ("Available".equalsIgnoreCase(status) || "KHA_DUNG".equalsIgnoreCase(status)) {
                lblInspectorStatus.getStyleClass().add("badge-available");
            } else if ("Borrowed".equalsIgnoreCase(status) || "DANG_MUON".equalsIgnoreCase(status)) {
                lblInspectorStatus.getStyleClass().add("badge-borrowed");
            } else if ("On Hold".equalsIgnoreCase(status) || "CHO_DUYET".equalsIgnoreCase(status)) {
                lblInspectorStatus.getStyleClass().add("badge-onhold");
            } else {
                lblInspectorStatus.getStyleClass().add("badge-returned");
            }
        }

        // Cover image loading with fallback
        if (imgInspectorCover != null) {
            String imageName = book.getImagePath();
            if (imageName == null || imageName.isBlank()) {
                imageName = "clean_code.jpg";
            }
            URL imgUrl = getClass().getResource("/com/vithay/libman/images/" + imageName);
            if (imgUrl == null) {
                imgUrl = getClass().getResource("/com/vithay/libman/images/clean_code.jpg");
            }
            if (imgUrl != null) {
                imgInspectorCover.setImage(new Image(imgUrl.toExternalForm(), true));
            }
        }

        // Quick borrow state
        if (btnInspectorBorrow != null) {
            btnInspectorBorrow.setDisable(book.getAvailableCopies() <= 0);
        }
        applySecurityPermissions();

        // Intriguing branches & Breadcrumb sync
        renderIntriguingBranches(book);
        updateBreadcrumbs();
    }

    public void renderIntriguingBranches(Book book) {
        if (inspectorBranchesBox == null) return;
        inspectorBranchesBox.getChildren().clear();
        if (book == null) return;

        Label lblSection = new Label("NHÁNH LIÊN QUAN & GỢI Ý");
        lblSection.getStyleClass().add("branch-section-title");
        inspectorBranchesBox.getChildren().add(lblSection);

        // 1. Same Shelf Branch
        VBox shelfBox = new VBox(4);
        HBox shelfHeader = new HBox(8);
        shelfHeader.setAlignment(Pos.CENTER_LEFT);
        Label lblShelf = new Label("Cùng vị trí kệ: " + (book.getShelfLocation() != null ? book.getShelfLocation() : "Chưa xếp"));
        lblShelf.getStyleClass().add("branch-header-label");
        Region spacer1 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);
        Label linkAllShelf = new Label("Xem tất cả ➔");
        linkAllShelf.getStyleClass().add("branch-action-link");
        linkAllShelf.setOnMouseClicked(e -> drilldownByShelf(book.getShelfLocation()));
        shelfHeader.getChildren().addAll(lblShelf, spacer1, linkAllShelf);
        shelfBox.getChildren().add(shelfHeader);

        List<Book> sameShelfBooks = bookMasterList.stream()
                .filter(b -> !b.getId().equals(book.getId()) &&
                        book.getShelfLocation() != null &&
                        book.getShelfLocation().equalsIgnoreCase(b.getShelfLocation()))
                .limit(3)
                .toList();

        if (sameShelfBooks.isEmpty()) {
            Label emptyLbl = new Label("(Không có sách khác trên kệ này)");
            emptyLbl.getStyleClass().add("branch-subtitle");
            emptyLbl.setStyle("-fx-font-style: italic;");
            shelfBox.getChildren().add(emptyLbl);
        } else {
            for (Book b : sameShelfBooks) {
                shelfBox.getChildren().add(createMiniBranchCard(b));
            }
        }
        inspectorBranchesBox.getChildren().add(shelfBox);

        // 2. Same Author Branch
        VBox authorBox = new VBox(4);
        HBox authorHeader = new HBox(8);
        authorHeader.setAlignment(Pos.CENTER_LEFT);
        Label lblAuthor = new Label("Cùng tác giả: " + (book.getAuthor() != null ? book.getAuthor() : "Không rõ"));
        lblAuthor.getStyleClass().add("branch-header-label");
        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, Priority.ALWAYS);
        Label linkAllAuthor = new Label("Xem tất cả ➔");
        linkAllAuthor.getStyleClass().add("branch-action-link");
        linkAllAuthor.setOnMouseClicked(e -> drilldownByAuthor(book.getAuthor()));
        authorHeader.getChildren().addAll(lblAuthor, spacer2, linkAllAuthor);
        authorBox.getChildren().add(authorHeader);

        List<Book> sameAuthorBooks = bookMasterList.stream()
                .filter(b -> !b.getId().equals(book.getId()) &&
                        book.getAuthor() != null &&
                        book.getAuthor().equalsIgnoreCase(b.getAuthor()))
                .limit(3)
                .toList();

        if (sameAuthorBooks.isEmpty()) {
            Label emptyLbl = new Label("(Chưa có sách khác cùng tác giả)");
            emptyLbl.getStyleClass().add("branch-subtitle");
            emptyLbl.setStyle("-fx-font-style: italic;");
            authorBox.getChildren().add(emptyLbl);
        } else {
            for (Book b : sameAuthorBooks) {
                authorBox.getChildren().add(createMiniBranchCard(b));
            }
        }
        inspectorBranchesBox.getChildren().add(authorBox);

        // 3. Circulation History Action
        Button btnViewCirculation = new Button("Xem Lịch Sử Lưu Hành Cuốn Này");
        btnViewCirculation.setMaxWidth(Double.MAX_VALUE);
        btnViewCirculation.getStyleClass().add("btn-secondary");
        btnViewCirculation.setStyle("-fx-font-size: 11px; -fx-padding: 6 12;");
        btnViewCirculation.setOnAction(e -> showCirculationHistory(book));
        inspectorBranchesBox.getChildren().add(btnViewCirculation);
    }

    private HBox createMiniBranchCard(Book b) {
        HBox card = new HBox(8);
        card.setAlignment(Pos.CENTER_LEFT);
        card.getStyleClass().add("branch-card");

        Label iconLbl = new Label("•");
        iconLbl.setStyle("-fx-font-size: 13px;");

        VBox info = new VBox(2);
        Label titleLbl = new Label(b.getTitle());
        titleLbl.getStyleClass().add("branch-title");
        titleLbl.setMaxWidth(180);

        Label subLbl = new Label(String.format("%,.0f đ | Còn %d/%d", b.getPrice(), b.getAvailableCopies(), b.getTotalCopies()));
        subLbl.getStyleClass().add("branch-subtitle");

        info.getChildren().addAll(titleLbl, subLbl);
        HBox.setHgrow(info, Priority.ALWAYS);

        card.getChildren().addAll(iconLbl, info);

        card.setOnMouseClicked(e -> {
            if (isCirculationMode) {
                exitCirculationMode();
            }
            if (booksTable != null) {
                booksTable.getSelectionModel().select(b);
                booksTable.scrollTo(b);
            }
            showBookDetail(b);
            if (!isTableViewMode && booksGridPane != null) {
                renderGridView(currentFilteredBooks);
            }
        });

        return card;
    }

    public void drilldownByShelf(String shelf) {
        if (shelf == null || shelf.trim().isEmpty()) return;
        this.drilldownShelf = shelf.trim();
        this.drilldownAuthor = null;
        if (comboAdvShelf != null) {
            comboAdvShelf.setValue(this.drilldownShelf);
        }
        if (isCirculationMode) {
            exitCirculationMode();
        }
        applyFilters();
    }

    public void drilldownByAuthor(String author) {
        if (author == null || author.trim().isEmpty()) return;
        this.drilldownAuthor = author.trim();
        this.drilldownShelf = null;
        if (authorFilterCombo != null && authorFilterCombo.getItems().contains(this.drilldownAuthor)) {
            authorFilterCombo.setValue(this.drilldownAuthor);
        }
        if (isCirculationMode) {
            exitCirculationMode();
        }
        applyFilters();
    }

    public void showCirculationHistory(Book book) {
        if (book == null) return;
        this.isCirculationMode = true;
        this.circulationBook = book;

        if (booksTable != null) {
            booksTable.setVisible(false);
            booksTable.setManaged(false);
        }
        if (booksGridScrollPane != null) {
            booksGridScrollPane.setVisible(false);
            booksGridScrollPane.setManaged(false);
        }
        if (circulationHistoryPane != null) {
            circulationHistoryPane.setVisible(true);
            circulationHistoryPane.setManaged(true);
        }

        if (lblCirculationBookTitle != null) {
            lblCirculationBookTitle.setText("Lịch Sử Lưu Hành: " + book.getTitle());
        }
        if (lblCirculationBookMeta != null) {
            lblCirculationBookMeta.setText("Mã sách: " + book.getId() + " | Tác giả: " + book.getAuthor() + " | Vị trí: " + book.getShelfLocation());
        }

        if (circulationTable != null) {
            List<BorrowTransaction> txList = borrowTransactionDao.getTransactionsByBook(book.getId());
            circulationTable.setItems(FXCollections.observableArrayList(txList));
        }

        updateBreadcrumbs();
    }

    public void exitCirculationMode() {
        this.isCirculationMode = false;
        if (circulationHistoryPane != null) {
            circulationHistoryPane.setVisible(false);
            circulationHistoryPane.setManaged(false);
        }
        if (isTableViewMode) {
            if (booksTable != null) {
                booksTable.setVisible(true);
                booksTable.setManaged(true);
            }
        } else {
            if (booksGridScrollPane != null) {
                booksGridScrollPane.setVisible(true);
                booksGridScrollPane.setManaged(true);
            }
        }
    }

    @FXML
    public void handleBackFromCirculationHistory() {
        exitCirculationMode();
        updateBreadcrumbs();
    }

    public void resetAllDrilldownAndFilters() {
        this.drilldownAuthor = null;
        this.drilldownShelf = null;
        this.currentCategoryFilter = "Tất cả";
        if (authorFilterCombo != null) authorFilterCombo.getSelectionModel().selectFirst();
        if (statusFilterCombo != null) statusFilterCombo.getSelectionModel().selectFirst();
        if (comboAdvCategory != null) comboAdvCategory.getSelectionModel().selectFirst();
        if (comboAdvShelf != null) comboAdvShelf.setValue("Tất cả");
        if (txtAdvMinPrice != null) txtAdvMinPrice.clear();
        if (txtAdvMaxPrice != null) txtAdvMaxPrice.clear();
        if (comboAdvStockStatus != null) comboAdvStockStatus.getSelectionModel().selectFirst();
        if (txtSearchBook != null) txtSearchBook.clear();
        if (isCirculationMode) {
            exitCirculationMode();
        }
        applyFilters();
        this.currentSelectedBook = null;
        if (booksTable != null) {
            booksTable.getSelectionModel().clearSelection();
        }
        showBookDetail(null);
    }

    public void updateBreadcrumbs() {
        if (breadcrumbContainer == null) return;
        breadcrumbContainer.getChildren().clear();

        boolean hasFilter = (currentCategoryFilter != null && !"Tất cả".equalsIgnoreCase(currentCategoryFilter))
                || (drilldownAuthor != null && !drilldownAuthor.isBlank())
                || (drilldownShelf != null && !drilldownShelf.isBlank())
                || (isCirculationMode)
                || (currentSelectedBook != null);

        Label rootChip = new Label("Kho Sách");
        if (!hasFilter) {
            rootChip.getStyleClass().add("breadcrumb-chip-active");
            breadcrumbContainer.getChildren().add(rootChip);
            return;
        }

        rootChip.getStyleClass().add("breadcrumb-chip");
        rootChip.setOnMouseClicked(e -> resetAllDrilldownAndFilters());
        breadcrumbContainer.getChildren().add(rootChip);

        if (currentCategoryFilter != null && !"Tất cả".equalsIgnoreCase(currentCategoryFilter)) {
            breadcrumbContainer.getChildren().add(createSeparator());
            boolean isLeaf = (drilldownAuthor == null && drilldownShelf == null && !isCirculationMode && currentSelectedBook == null);
            Label catChip = new Label("Thể loại: " + currentCategoryFilter);
            if (isLeaf) {
                catChip.getStyleClass().add("breadcrumb-chip-active");
            } else {
                catChip.getStyleClass().add("breadcrumb-chip");
                catChip.setOnMouseClicked(e -> {
                    drilldownAuthor = null;
                    drilldownShelf = null;
                    if (isCirculationMode) exitCirculationMode();
                    applyFilters();
                });
            }
            breadcrumbContainer.getChildren().add(catChip);
        }

        if (drilldownShelf != null && !drilldownShelf.isBlank()) {
            breadcrumbContainer.getChildren().add(createSeparator());
            boolean isLeaf = (!isCirculationMode && currentSelectedBook == null);
            Label shelfChip = new Label("Kệ: " + drilldownShelf);
            if (isLeaf) {
                shelfChip.getStyleClass().add("breadcrumb-chip-active");
            } else {
                shelfChip.getStyleClass().add("breadcrumb-chip");
                shelfChip.setOnMouseClicked(e -> {
                    if (isCirculationMode) exitCirculationMode();
                    applyFilters();
                });
            }
            breadcrumbContainer.getChildren().add(shelfChip);
        }

        if (drilldownAuthor != null && !drilldownAuthor.isBlank()) {
            breadcrumbContainer.getChildren().add(createSeparator());
            boolean isLeaf = (!isCirculationMode && currentSelectedBook == null);
            Label authorChip = new Label("Tác giả: " + drilldownAuthor);
            if (isLeaf) {
                authorChip.getStyleClass().add("breadcrumb-chip-active");
            } else {
                authorChip.getStyleClass().add("breadcrumb-chip");
                authorChip.setOnMouseClicked(e -> {
                    if (isCirculationMode) exitCirculationMode();
                    applyFilters();
                });
            }
            breadcrumbContainer.getChildren().add(authorChip);
        }

        if (currentSelectedBook != null) {
            breadcrumbContainer.getChildren().add(createSeparator());
            Label bookChip = new Label("Chi tiết: " + currentSelectedBook.getTitle());
            if (!isCirculationMode) {
                bookChip.getStyleClass().add("breadcrumb-chip-active");
            } else {
                bookChip.getStyleClass().add("breadcrumb-chip");
                bookChip.setOnMouseClicked(e -> {
                    exitCirculationMode();
                    updateBreadcrumbs();
                });
            }
            breadcrumbContainer.getChildren().add(bookChip);
        }

        if (isCirculationMode && circulationBook != null) {
            breadcrumbContainer.getChildren().add(createSeparator());
            Label circChip = new Label("Lịch sử lưu hành");
            circChip.getStyleClass().add("breadcrumb-chip-active");
            breadcrumbContainer.getChildren().add(circChip);
        }
    }

    private Label createSeparator() {
        Label sep = new Label("›");
        sep.getStyleClass().add("breadcrumb-separator");
        return sep;
    }

    @FXML
    public void handleInspectorQuickBorrow() {
        if (currentSelectedBook == null) return;
        if (currentSelectedBook.getAvailableCopies() <= 0) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Sách '" + currentSelectedBook.getTitle() + "' hiện đã hết bản khả dụng trong kho!");
            alert.showAndWait();
            return;
        }

        if (mainController != null) {
            mainController.stageBookForBorrow(currentSelectedBook);
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Đã chọn sách '" + currentSelectedBook.getTitle() + "' để lập phiếu mượn.");
            alert.showAndWait();
        }
    }

    @FXML
    public void handleInspectorEdit() {
        if (currentSelectedBook != null) {
            showBookFormModal(currentSelectedBook);
        }
    }

    @FXML
    public void handleInspectorDelete() {
        if (currentSelectedBook != null) {
            handleSoftDeleteBook(currentSelectedBook);
        }
    }
}
