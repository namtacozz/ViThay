package com.vithay.libman.controller;

import com.vithay.libman.model.Book;
import com.vithay.libman.model.User;
import com.vithay.libman.service.AuthService;
import com.vithay.libman.service.BookService;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MainLayoutController implements Initializable {
    private static final Logger logger = LoggerFactory.getLogger(MainLayoutController.class);

    @FXML private StackPane rootStackPane;
    @FXML private BorderPane mainContainer;
    @FXML private StackPane contentArea;
    @FXML private TextField headerSearchField;

    // Spotify-like floating search dropdown
    @FXML private VBox searchDropdownContainer;
    @FXML private VBox searchDropdownList;

    // In-window Auth Overlay Modal
    @FXML private StackPane authOverlayPane;
    @FXML private VBox authModalBox;
    @FXML private VBox loginViewBox;
    @FXML private VBox registerViewBox;

    @FXML private TextField txtLoginUsername;
    @FXML private PasswordField txtLoginPassword;
    @FXML private Label lblLoginError;

    @FXML private TextField txtRegUsername;
    @FXML private PasswordField txtRegPassword;
    @FXML private TextField txtRegFullName;
    @FXML private TextField txtRegIdCard;
    @FXML private TextField txtRegPhone;
    @FXML private TextField txtRegEmail;
    @FXML private TextField txtRegBirth;
    @FXML private Label lblRegError;

    // In-window Profile Settings Modal
    @FXML private VBox profileModalBox;
    @FXML private ImageView profileAvatarView;
    @FXML private Label lblProfileFullName;
    @FXML private Label lblProfileUsername;
    @FXML private Label lblProfileRolePill;
    @FXML private TextField txtProfileFullName;
    @FXML private TextField txtProfileEmail;
    @FXML private TextField txtProfilePhone;
    @FXML private PasswordField txtProfileOldPass;
    @FXML private PasswordField txtProfileNewPass;
    @FXML private PasswordField txtProfileConfirmPass;
    @FXML private Label lblProfileMsg;

    // Navigation Buttons
    @FXML private Button btnNavHome;
    @FXML private Button btnNavBooks;
    @FXML private Button btnToggleCategories;
    @FXML private VBox categorySubmenuContainer;
    @FXML private Button btnNavCategories;
    @FXML private Button btnNavReaders;
    @FXML private Button btnNavBorrowReturn;
    @FXML private Button btnNavTransactions;
    @FXML private Button btnNavStats;
    @FXML private Button btnNavRecycleBin;
    @FXML private Button btnNavSettings;
    @FXML private Button btnHelp;

    @FXML private Button btnHeaderSettings;
    @FXML private Button btnNotification;
    @FXML private ImageView logoImageView;
    @FXML private ImageView userAvatarImageView;

    // Header Profile & Roles
    @FXML private HBox userProfileBox;
    @FXML private Label lblHeaderUserName;
    @FXML private Label lblHeaderUserRole;
    @FXML private Label lblCurrentRoleBadge;

    // Section Labels
    @FXML private Label lblSecBooks;
    @FXML private Label lblSecReaders;
    @FXML private Label lblSecBorrow;
    @FXML private Label lblSecAdmin;

    // Views & Sub-Controllers
    private Node homeView;
    private HomeController homeController;

    private Node bookView;
    private BookManagementController bookController;

    private Node readerView;
    private ReaderManagementController readerController;

    private Node borrowReturnView;
    private BorrowReturnController borrowReturnController;

    private Node statsView;
    private StatisticsController statsController;

    private Node categoryView;
    private CategoryController categoryController;

    private Node settingsView;
    private SettingsController settingsController;

    private Node recycleBinView;
    private RecycleBinController recycleBinController;

    private Object activeSubController;
    private final AuthService authService = AuthService.getInstance();
    private final BookService bookService = new BookService();
    private final PauseTransition searchDebounce = new PauseTransition(Duration.millis(120));

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupSearchAutocomplete();
        loadViews();
        updateUserSessionUI();
        showHomeView();

        rootStackPane.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                com.vithay.libman.service.ThemeManager tm = com.vithay.libman.service.ThemeManager.getInstance();
                tm.registerScene(newScene);
                // Apply correct logo & avatar for current theme immediately
                applyThemeVisuals(tm.getCurrentTheme());
                // Listen for future theme changes
                tm.addThemeChangeListener(this::applyThemeVisuals);
            }
        });
    }

    private void applyThemeVisuals(com.vithay.libman.service.ThemeManager.Theme theme) {
        boolean isLight = (theme == com.vithay.libman.service.ThemeManager.Theme.PINK_LIGHT);
        String logoPath = isLight ? "/com/vithay/libman/images/logo_light.png" : "/com/vithay/libman/images/logo_dark.png";
        String avatarPath = isLight ? "/com/vithay/libman/images/avatar_light.png" : "/com/vithay/libman/images/avatar_dark.png";

        if (logoImageView != null) {
            try (java.io.InputStream is = getClass().getResourceAsStream(logoPath)) {
                if (is != null) logoImageView.setImage(new javafx.scene.image.Image(is));
            } catch (Exception ignored) {}
        }
        if (userAvatarImageView != null) {
            try (java.io.InputStream is = getClass().getResourceAsStream(avatarPath)) {
                if (is != null) userAvatarImageView.setImage(new javafx.scene.image.Image(is));
            } catch (Exception ignored) {}
        }
        if (profileAvatarView != null) {
            try (java.io.InputStream is = getClass().getResourceAsStream(avatarPath)) {
                if (is != null) profileAvatarView.setImage(new javafx.scene.image.Image(is));
            } catch (Exception ignored) {}
        }
    }

    private void setupSearchAutocomplete() {
        searchDebounce.setOnFinished(e -> {
            handleHeaderSearchChange(headerSearchField.getText());
        });

        // Use textProperty listener with 120ms debounce to allow Vietnamese IME (IBus, Fcitx, Unikey) composition
        headerSearchField.textProperty().addListener((observable, oldValue, newValue) -> {
            searchDebounce.playFromStart();
        });

        // Keyboard actions on search field
        headerSearchField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                hideSearchDropdown();
            } else if (event.getCode() == KeyCode.ENTER) {
                searchDebounce.stop();
                handleHeaderSearchChange(headerSearchField.getText());
                showBookView();
                if (bookController != null) {
                    bookController.filterBooks(headerSearchField.getText());
                }
            }
        });
    }

    private void handleHeaderSearchChange(String query) {
        if (query == null || query.trim().isEmpty()) {
            hideSearchDropdown();
            dispatchHeaderSearchToSubViews("");
            return;
        }

        String search = query.trim();
        dispatchHeaderSearchToSubViews(search);

        List<Book> matches = bookService.searchBooks(search);
        searchDropdownList.getChildren().clear();

        if (matches.isEmpty()) {
            Label lblEmpty = new Label("Không tìm thấy kết quả phù hợp cho '" + search + "'");
            lblEmpty.setStyle("-fx-text-fill: #727272; -fx-padding: 12 16; -fx-font-style: italic;");
            searchDropdownList.getChildren().add(lblEmpty);
        } else {
            int count = 0;
            for (Book book : matches) {
                if (count++ >= 6) break; // Limit 6 results for compact Spotify look
                HBox row = createSearchDropdownRow(book);
                searchDropdownList.getChildren().add(row);
            }
        }

        searchDropdownContainer.setVisible(true);
        searchDropdownContainer.setManaged(true);
    }

    private HBox createSearchDropdownRow(Book book) {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);
        row.getStyleClass().add("search-dropdown-item");

        // Mini thumbnail
        ImageView img = new ImageView();
        img.setFitWidth(28);
        img.setFitHeight(38);
        img.setPreserveRatio(false);

        try {
            String path = book.getCoverImage();
            if (path != null) {
                InputStream is = getClass().getResourceAsStream(path);
                if (is != null) img.setImage(new Image(is));
                else img.setImage(new Image(getClass().getResourceAsStream("/com/vithay/libman/images/logo.png")));
            }
        } catch (Exception e) {
            // fallback
        }

        VBox meta = new VBox(2);
        Label title = new Label(book.getTitle());
        title.setStyle("-fx-font-weight: bold; -fx-text-fill: #FFFFFF; -fx-font-size: 13px;");

        Label sub = new Label(book.getAuthor() + " • " + book.getCategory() + " (" + book.getShelfLocation() + ")");
        sub.setStyle("-fx-text-fill: #B3B3B3; -fx-font-size: 11px;");
        meta.getChildren().addAll(title, sub);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label badge = new Label(book.getStatus());
        if ("Available".equalsIgnoreCase(book.getStatus())) {
            badge.getStyleClass().add("badge-available");
        } else if ("Borrowed".equalsIgnoreCase(book.getStatus())) {
            badge.getStyleClass().add("badge-borrowed");
        } else {
            badge.getStyleClass().add("badge-onhold");
        }

        row.getChildren().addAll(img, meta, spacer, badge);

        row.setOnMouseClicked(e -> {
            hideSearchDropdown();
            showBookView();
            if (bookController != null) {
                bookController.filterBooks(book.getTitle());
            }
        });

        return row;
    }

    private void hideSearchDropdown() {
        searchDropdownContainer.setVisible(false);
        searchDropdownContainer.setManaged(false);
    }

    private void dispatchHeaderSearchToSubViews(String keyword) {
        if (activeSubController instanceof HomeController) {
            ((HomeController) activeSubController).filterRecentTransactions(keyword);
        } else if (activeSubController instanceof BookManagementController) {
            ((BookManagementController) activeSubController).filterBooks(keyword);
        } else if (activeSubController instanceof ReaderManagementController) {
            ((ReaderManagementController) activeSubController).filterReaders(keyword);
        } else if (activeSubController instanceof BorrowReturnController) {
            ((BorrowReturnController) activeSubController).filterTransactions(keyword);
        }
    }

    private void loadViews() {
        try {
            FXMLLoader homeLoader = new FXMLLoader(getClass().getResource("/com/vithay/libman/view/HomeView.fxml"));
            homeView = homeLoader.load();
            homeController = homeLoader.getController();
            homeController.setMainController(this);

            FXMLLoader bookLoader = new FXMLLoader(getClass().getResource("/com/vithay/libman/view/BookManagementView.fxml"));
            bookView = bookLoader.load();
            bookController = bookLoader.getController();
            bookController.setMainController(this);

            FXMLLoader readerLoader = new FXMLLoader(getClass().getResource("/com/vithay/libman/view/ReaderManagementView.fxml"));
            readerView = readerLoader.load();
            readerController = readerLoader.getController();

            FXMLLoader borrowLoader = new FXMLLoader(getClass().getResource("/com/vithay/libman/view/BorrowReturnView.fxml"));
            borrowReturnView = borrowLoader.load();
            borrowReturnController = borrowLoader.getController();

            FXMLLoader statsLoader = new FXMLLoader(getClass().getResource("/com/vithay/libman/view/StatisticsView.fxml"));
            statsView = statsLoader.load();
            statsController = statsLoader.getController();

            FXMLLoader catLoader = new FXMLLoader(getClass().getResource("/com/vithay/libman/view/CategoryManagementView.fxml"));
            categoryView = catLoader.load();
            categoryController = catLoader.getController();

            FXMLLoader setLoader = new FXMLLoader(getClass().getResource("/com/vithay/libman/view/SettingsView.fxml"));
            settingsView = setLoader.load();
            settingsController = setLoader.getController();

            FXMLLoader binLoader = new FXMLLoader(getClass().getResource("/com/vithay/libman/view/RecycleBinView.fxml"));
            recycleBinView = binLoader.load();
            recycleBinController = binLoader.getController();
        } catch (Exception e) {
            logger.error("Error pre-loading FXML views", e);
        }
    }

    public void updateUserSessionUI() {
        User u = authService.getCurrentUser();
        if (u != null) {
            lblHeaderUserName.setText(u.getFullName());
            lblHeaderUserRole.setText(u.getRole() + " (Tùy chỉnh)");
            lblCurrentRoleBadge.setText(u.getRole());
            lblCurrentRoleBadge.setStyle("-fx-text-fill: #1DB954; -fx-font-weight: bold;");

            boolean isAdmin = authService.isAdmin();
            boolean isLibrarian = authService.isLibrarian();
            boolean isReader = authService.isReader();

            // RBAC Visibility control (3 vai trò: Quản Trị, Thủ Thư, Độc Giả)
            btnNavSettings.setVisible(isAdmin);
            btnNavSettings.setManaged(isAdmin);

            btnNavRecycleBin.setVisible(isLibrarian);
            btnNavRecycleBin.setManaged(isLibrarian);

            btnNavCategories.setVisible(isLibrarian);
            btnNavCategories.setManaged(isLibrarian);

            btnNavReaders.setVisible(isLibrarian);
            btnNavReaders.setManaged(isLibrarian);
            if (lblSecReaders != null) {
                lblSecReaders.setVisible(isLibrarian);
                lblSecReaders.setManaged(isLibrarian);
            }

            btnNavBorrowReturn.setVisible(isLibrarian);
            btnNavBorrowReturn.setManaged(isLibrarian);
            btnNavTransactions.setVisible(isLibrarian || isReader);
            btnNavTransactions.setManaged(isLibrarian || isReader);
            if (lblSecBorrow != null) {
                lblSecBorrow.setVisible(isLibrarian || isReader);
                lblSecBorrow.setManaged(isLibrarian || isReader);
            }

            btnNavStats.setVisible(isLibrarian);
            btnNavStats.setManaged(isLibrarian);
            if (lblSecAdmin != null) {
                lblSecAdmin.setVisible(isLibrarian);
                lblSecAdmin.setManaged(isLibrarian);
            }
        } else {
            // Chế độ chưa đăng nhập (Guest Mode)
            lblHeaderUserName.setText("Chưa đăng nhập");
            lblHeaderUserRole.setText("Nhấn để đăng nhập / đăng ký");
            lblCurrentRoleBadge.setText("Khách");
            lblCurrentRoleBadge.setStyle("-fx-text-fill: #F59E0B; -fx-font-weight: bold;");

            // Chỉ cho phép xem Trang chủ, Kho sách
            btnNavCategories.setVisible(false);
            btnNavCategories.setManaged(false);

            btnNavReaders.setVisible(false);
            btnNavReaders.setManaged(false);
            if (lblSecReaders != null) {
                lblSecReaders.setVisible(false);
                lblSecReaders.setManaged(false);
            }

            btnNavBorrowReturn.setVisible(false);
            btnNavBorrowReturn.setManaged(false);
            btnNavTransactions.setVisible(false);
            btnNavTransactions.setManaged(false);
            if (lblSecBorrow != null) {
                lblSecBorrow.setVisible(false);
                lblSecBorrow.setManaged(false);
            }

            btnNavStats.setVisible(false);
            btnNavStats.setManaged(false);
            btnNavRecycleBin.setVisible(false);
            btnNavRecycleBin.setManaged(false);
            btnNavSettings.setVisible(false);
            btnNavSettings.setManaged(false);
            if (lblSecAdmin != null) {
                lblSecAdmin.setVisible(false);
                lblSecAdmin.setManaged(false);
            }
        }
    }

    private void setActiveNavButton(Button activeButton) {
        Button[] navButtons = {
                btnNavHome, btnNavBooks, btnNavCategories,
                btnNavReaders, btnNavBorrowReturn, btnNavTransactions, btnNavStats,
                btnNavRecycleBin, btnNavSettings
        };
        for (Button btn : navButtons) {
            if (btn != null) {
                btn.getStyleClass().remove("nav-item-active");
            }
        }
        if (activeButton != null && !activeButton.getStyleClass().contains("nav-item-active")) {
            activeButton.getStyleClass().add("nav-item-active");
        }
    }

    public void showHomeView() {
        hideSearchDropdown();
        if (homeView != null) {
            contentArea.getChildren().setAll(homeView);
            activeSubController = homeController;
            setActiveNavButton(btnNavHome);
            if (homeController != null) {
                homeController.refreshData();
            }
        }
    }

    @FXML
    public void handleNavHome() {
        showHomeView();
    }

    public void showBookView() {
        hideSearchDropdown();
        if (bookView != null) {
            contentArea.getChildren().setAll(bookView);
            activeSubController = bookController;
            setActiveNavButton(btnNavBooks);
            if (bookController != null) {
                bookController.loadBooks();
            }
        }
    }

    @FXML
    public void handleNavBooks() {
        showBookView();
        if (categorySubmenuContainer != null) {
            for (Node n : categorySubmenuContainer.getChildren()) {
                n.getStyleClass().remove("subnav-item-active");
            }
        }
        if (bookController != null) {
            bookController.filterByCategory("Tất cả");
        }
    }

    @FXML
    public void handleToggleCategories() {
        boolean isVisible = !categorySubmenuContainer.isVisible();
        categorySubmenuContainer.setVisible(isVisible);
        categorySubmenuContainer.setManaged(isVisible);
        btnToggleCategories.setText(isVisible ? "▾" : "▸");
    }

    @FXML
    public void handleSubCategoryClick(ActionEvent event) {
        if (event.getSource() instanceof Button) {
            Button btn = (Button) event.getSource();
            String catName = btn.getText().replace("•", "").trim();
            showBookView();

            // Set active highlight on the clicked category pill card
            if (categorySubmenuContainer != null) {
                for (Node n : categorySubmenuContainer.getChildren()) {
                    n.getStyleClass().remove("subnav-item-active");
                }
            }
            btn.getStyleClass().add("subnav-item-active");

            if (bookController != null) {
                bookController.filterByCategory(catName);
            }
        }
    }

    @FXML
    public void handleNavCategories() {
        hideSearchDropdown();
        if (categoryView != null) {
            contentArea.getChildren().setAll(categoryView);
            activeSubController = categoryController;
            setActiveNavButton(btnNavCategories);
            if (categoryController != null) {
                categoryController.loadCategories();
            }
        }
    }

    public void showReaderView() {
        hideSearchDropdown();
        if (readerView != null) {
            contentArea.getChildren().setAll(readerView);
            activeSubController = readerController;
            setActiveNavButton(btnNavReaders);
            if (readerController != null) {
                readerController.loadReaders();
            }
        }
    }

    @FXML
    public void handleNavReaders() {
        showReaderView();
    }

    public void showBorrowReturnView(int tabIndex) {
        hideSearchDropdown();
        if (borrowReturnView != null) {
            contentArea.getChildren().setAll(borrowReturnView);
            activeSubController = borrowReturnController;
            if (tabIndex == 3 || tabIndex == 2) {
                setActiveNavButton(btnNavTransactions);
            } else {
                setActiveNavButton(btnNavBorrowReturn);
            }
            if (borrowReturnController != null) {
                if (tabIndex == 3 || tabIndex == 2) {
                    borrowReturnController.selectTabByName("Lịch Sử");
                } else {
                    borrowReturnController.selectTab(tabIndex);
                }
            }
        }
    }

    @FXML
    public void handleNavBorrowReturn() {
        showBorrowReturnView(0);
    }

    @FXML
    public void handleNavTransactions() {
        showBorrowReturnView(3);
    }

    public void showStatsView() {
        hideSearchDropdown();
        if (statsView != null) {
            contentArea.getChildren().setAll(statsView);
            activeSubController = statsController;
            setActiveNavButton(btnNavStats);
            if (statsController != null) {
                statsController.loadStats();
            }
        }
    }

    @FXML
    public void handleNavStats() {
        showStatsView();
    }

    @FXML
    public void handleNavRecycleBin() {
        hideSearchDropdown();
        if (recycleBinView != null) {
            contentArea.getChildren().setAll(recycleBinView);
            activeSubController = recycleBinController;
            setActiveNavButton(btnNavRecycleBin);
            if (recycleBinController != null) {
                recycleBinController.loadRecycleBinData();
            }
        }
    }

    @FXML
    public void handleNavSettings() {
        hideSearchDropdown();
        if (settingsView != null) {
            contentArea.getChildren().setAll(settingsView);
            activeSubController = settingsController;
            setActiveNavButton(btnNavSettings);
            if (settingsController != null) {
                settingsController.loadSettings();
            }
        }
    }

    @FXML
    public void handleHelpClick() {
        HelpDialog.showHelp();
    }

    // MODAL POPUP (LOGIN, REGISTER & PROFILE SETTINGS)
    @FXML
    public void handleProfileClick() {
        if (authService.getCurrentUser() != null) {
            openProfilePopup();
        } else {
            openAuthPopup();
        }
    }

    public void openProfilePopup() {
        hideSearchDropdown();
        User u = authService.getCurrentUser();
        if (u == null) {
            openAuthPopup();
            return;
        }

        authModalBox.setVisible(false);
        authModalBox.setManaged(false);
        profileModalBox.setVisible(true);
        profileModalBox.setManaged(true);

        lblProfileFullName.setText(u.getFullName());
        lblProfileUsername.setText("@" + u.getUsername());
        lblProfileRolePill.setText(u.getRole());
        txtProfileFullName.setText(u.getFullName());
        txtProfileEmail.setText(u.getEmail() != null ? u.getEmail() : "");
        txtProfilePhone.setText(u.getPhone() != null ? u.getPhone() : "");
        txtProfileOldPass.clear();
        txtProfileNewPass.clear();
        txtProfileConfirmPass.clear();
        lblProfileMsg.setText("");

        authOverlayPane.setVisible(true);
        authOverlayPane.setManaged(true);
        mainContainer.setEffect(new GaussianBlur(14));
    }

    public void openAuthPopup() {
        hideSearchDropdown();
        profileModalBox.setVisible(false);
        profileModalBox.setManaged(false);
        authModalBox.setVisible(true);
        authModalBox.setManaged(true);
        switchToLoginView();
        authOverlayPane.setVisible(true);
        authOverlayPane.setManaged(true);
        mainContainer.setEffect(new GaussianBlur(14));
    }

    @FXML
    public void closeAuthPopup() {
        authOverlayPane.setVisible(false);
        authOverlayPane.setManaged(false);
        mainContainer.setEffect(null);
    }

    @FXML
    public void switchToRegisterView() {
        loginViewBox.setVisible(false);
        loginViewBox.setManaged(false);
        registerViewBox.setVisible(true);
        registerViewBox.setManaged(true);
        lblRegError.setText("");
    }

    @FXML
    public void switchToLoginView() {
        registerViewBox.setVisible(false);
        registerViewBox.setManaged(false);
        loginViewBox.setVisible(true);
        loginViewBox.setManaged(true);
        lblLoginError.setText("");
    }

    @FXML
    public void fillAdminLogin() {
        txtLoginUsername.setText("quantri");
        txtLoginPassword.setText("admin123");
    }

    @FXML
    public void fillLibrarianLogin() {
        txtLoginUsername.setText("thuthu");
        txtLoginPassword.setText("123456");
    }

    @FXML
    public void fillReaderLogin() {
        txtLoginUsername.setText("docgia");
        txtLoginPassword.setText("123456");
    }

    @FXML
    public void handleExecuteLogin() {
        String u = txtLoginUsername.getText();
        String p = txtLoginPassword.getText();

        if (u == null || u.trim().isEmpty() || p == null || p.trim().isEmpty()) {
            lblLoginError.setText("Vui lòng nhập tài khoản và mật khẩu!");
            lblLoginError.setStyle("-fx-text-fill: #EF4444;");
            return;
        }

        boolean ok = authService.login(u.trim(), p.trim());
        if (ok) {
            updateUserSessionUI();
            closeAuthPopup();
            showHomeView();
        } else {
            lblLoginError.setText("Sai tài khoản hoặc mật khẩu!");
            lblLoginError.setStyle("-fx-text-fill: #EF4444;");
        }
    }

    @FXML
    public void handleExecuteRegister() {
        String u = txtRegUsername.getText();
        String p = txtRegPassword.getText();
        String name = txtRegFullName.getText();
        String idCard = txtRegIdCard.getText();

        if (u == null || u.trim().isEmpty() || p == null || p.trim().isEmpty() ||
            name == null || name.trim().isEmpty() || idCard == null || idCard.trim().isEmpty()) {
            lblRegError.setText("Vui lòng điền đủ: Tài khoản, Mật khẩu, Họ tên, CCCD/CMND!");
            lblRegError.setStyle("-fx-text-fill: #EF4444;");
            return;
        }

        User newUser = new User(
                0,
                u.trim(),
                p.trim(),
                name.trim(),
                "Độc Giả",
                txtRegEmail.getText() != null ? txtRegEmail.getText().trim() : "",
                txtRegPhone.getText() != null ? txtRegPhone.getText().trim() : "",
                "/com/vithay/libman/images/avatar.png",
                null,
                null
        );

        boolean registered = authService.register(newUser);
        if (registered) {
            updateUserSessionUI();
            closeAuthPopup();
            showHomeView();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Đăng Ký Thành Công");
            alert.setHeaderText("Hồ sơ độc giả trực tuyến đã được tạo!");
            alert.setContentText("Trạng thái thẻ của bạn là: 'Chờ Cấp Thẻ'.\nVui lòng mang theo CCCD đến thư viện để Thủ Thư đối chiếu và kích hoạt thẻ chính thức trước khi mượn sách.");
            alert.showAndWait();
        } else {
            lblRegError.setText("Tên đăng nhập đã tồn tại trong hệ thống!");
            lblRegError.setStyle("-fx-text-fill: #EF4444;");
        }
    }

    @FXML
    public void handleSaveProfile() {
        User u = authService.getCurrentUser();
        if (u == null) return;

        String newName = txtProfileFullName.getText() != null ? txtProfileFullName.getText().trim() : "";
        String newEmail = txtProfileEmail.getText() != null ? txtProfileEmail.getText().trim() : "";
        String newPhone = txtProfilePhone.getText() != null ? txtProfilePhone.getText().trim() : "";

        if (newName.isEmpty()) {
            lblProfileMsg.setText("Họ tên không được để trống!");
            lblProfileMsg.setStyle("-fx-text-fill: #EF4444;");
            return;
        }

        authService.updateProfile(newName, newEmail, newPhone);

        // Password change logic if old pass provided
        String oldPass = txtProfileOldPass.getText();
        String newPass = txtProfileNewPass.getText();
        String confirmPass = txtProfileConfirmPass.getText();

        if (oldPass != null && !oldPass.isEmpty()) {
            if (newPass == null || newPass.isEmpty()) {
                lblProfileMsg.setText("Vui lòng nhập mật khẩu mới!");
                lblProfileMsg.setStyle("-fx-text-fill: #EF4444;");
                return;
            }
            if (!newPass.equals(confirmPass)) {
                lblProfileMsg.setText("Xác nhận mật khẩu mới không khớp!");
                lblProfileMsg.setStyle("-fx-text-fill: #EF4444;");
                return;
            }
            boolean passChanged = authService.changePassword(oldPass, newPass);
            if (!passChanged) {
                lblProfileMsg.setText("Mật khẩu hiện tại không đúng!");
                lblProfileMsg.setStyle("-fx-text-fill: #EF4444;");
                return;
            }
        }

        lblProfileMsg.setText("✓ Đã lưu thay đổi hồ sơ thành công!");
        lblProfileMsg.setStyle("-fx-text-fill: #1DB954;");
        updateUserSessionUI();
    }

    @FXML
    public void handleExecuteLogout() {
        authService.logout();
        updateUserSessionUI();
        closeAuthPopup();
        showHomeView();
    }

    @FXML
    public void handleNotificationClick() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo hệ thống");
        alert.setHeaderText("Thông báo từ LibMan");
        alert.setContentText("Hệ thống hoạt động bình thường.\nĐang theo dõi các phiếu mượn và tự động kiểm tra thời hạn thẻ.");
        alert.showAndWait();
    }

    @FXML
    public void handleHeaderSettingsClick() {
        ContextMenu menu = new ContextMenu();
        com.vithay.libman.service.ThemeManager tm = com.vithay.libman.service.ThemeManager.getInstance();
        boolean isPink = tm.getCurrentTheme() == com.vithay.libman.service.ThemeManager.Theme.PINK_LIGHT;
        MenuItem itemTheme = new MenuItem(isPink ? "🌙 Chuyển sang Giao Diện Tối (Spotify Dark)" : "🌸 Chuyển sang Giao Diện Sáng (Modern Pink)");
        itemTheme.setOnAction(e -> {
            tm.toggleTheme();
            if (rootStackPane.getScene() != null) {
                tm.registerScene(rootStackPane.getScene());
            }
        });

        MenuItem itemSettings = new MenuItem("⚙ Cài đặt quy định thư viện (SRS)");
        itemSettings.setOnAction(e -> handleNavSettings());

        menu.getItems().addAll(itemTheme, new SeparatorMenuItem(), itemSettings);
        menu.show(btnHeaderSettings, javafx.geometry.Side.BOTTOM, 0, 8);
    }

    @FXML
    public void handleMessageClick() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Hộp thư nội bộ");
        alert.setHeaderText("Tin nhắn thủ thư");
        alert.setContentText("Không có tin nhắn mới nào.");
        alert.showAndWait();
    }
}
