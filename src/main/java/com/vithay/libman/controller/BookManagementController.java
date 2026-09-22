package com.vithay.libman.controller;

import com.vithay.libman.model.Book;
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

    @FXML private StackPane bookRootPane;
    @FXML private VBox bookMainContainer;
    @FXML private ComboBox<String> authorFilterCombo;
    @FXML private ComboBox<String> statusFilterCombo;
    private String currentCategoryFilter = "Tất cả";
    @FXML private TextField txtSearchBook;
    @FXML private Button btnAddBook;
    @FXML private Button btnExportBooks;

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

    private final ObservableList<Book> bookMasterList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupFilters();
        setupTable();
        loadBooks();
        switchView(true);
    }

    private void setupFilters() {
        statusFilterCombo.setItems(FXCollections.observableArrayList(
                "Tất cả", "Available", "Borrowed", "On Hold"
        ));
        statusFilterCombo.getSelectionModel().selectFirst();
        refreshAuthorFilterOptions();
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
            if (!isTableViewMode && booksGridPane != null) {
                renderGridView(currentFilteredBooks);
            }
        });
    }

    public void loadBooks() {
        applySecurityPermissions();
        List<Book> list = bookService.getAllBooks();
        bookMasterList.setAll(list);
        refreshAuthorFilterOptions();
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
    }

    @FXML
    public void handleAuthorFilter() {
        applyFilters();
    }

    @FXML
    public void handleStatusFilter() {
        applyFilters();
    }

    @FXML
    public void handleSearchBook() {
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
        applyFilters();
    }

    private void applyFilters() {
        String selectedAuthor = authorFilterCombo != null ? authorFilterCombo.getValue() : "Tất cả";
        String selectedStatus = statusFilterCombo != null ? statusFilterCombo.getValue() : "Tất cả";
        String search = txtSearchBook != null && txtSearchBook.getText() != null ? txtSearchBook.getText().trim() : "";

        ObservableList<Book> filtered = FXCollections.observableArrayList();
        for (Book b : bookMasterList) {
            // 1. Category Filter (triggered from sidebar subnav)
            boolean matchesCategory = true;
            if (currentCategoryFilter != null && !"Tất cả".equalsIgnoreCase(currentCategoryFilter)) {
                String cat = b.getCategory() != null ? b.getCategory() : "";
                matchesCategory = VietnameseUtils.matches(cat, currentCategoryFilter) ||
                        VietnameseUtils.matches(currentCategoryFilter, cat) ||
                        (currentCategoryFilter.toLowerCase().contains("toán") && cat.toLowerCase().contains("toán"));
            }

            // 2. Author Filter (from ComboBox)
            boolean matchesAuthor = selectedAuthor == null || "Tất cả".equalsIgnoreCase(selectedAuthor) ||
                    (b.getAuthor() != null && VietnameseUtils.matches(b.getAuthor(), selectedAuthor));

            // 3. Status Filter
            boolean matchesStatus = selectedStatus == null || "Tất cả".equalsIgnoreCase(selectedStatus) ||
                    (b.getStatus() != null && selectedStatus.equalsIgnoreCase(b.getStatus()));

            // 4. Search Keyword Filter
            boolean matchesSearch = search.isEmpty() ||
                    VietnameseUtils.matches(b.getId(), search) ||
                    VietnameseUtils.matches(b.getTitle(), search) ||
                    VietnameseUtils.matches(b.getAuthor(), search) ||
                    VietnameseUtils.matches(b.getShelfLocation(), search) ||
                    VietnameseUtils.matches(b.getIsbn(), search);

            if (matchesCategory && matchesAuthor && matchesStatus && matchesSearch) {
                filtered.add(b);
            }
        }
        this.currentFilteredBooks = filtered;
        if (booksTable != null) {
            booksTable.setItems(filtered);
        }
        if (!isTableViewMode && booksGridPane != null) {
            renderGridView(filtered);
        }
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
}
