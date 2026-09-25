package com.vithay.libman.controller;

import com.vithay.libman.model.Book;
import com.vithay.libman.model.Reader;
import com.vithay.libman.model.User;
import com.vithay.libman.service.AuthService;
import com.vithay.libman.service.BookService;
import com.vithay.libman.service.BorrowService;
import com.vithay.libman.service.ReaderService;
import com.vithay.libman.service.SettingService;
import javafx.collections.FXCollections;
import javafx.util.StringConverter;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
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
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    @FXML private VBox passwordChangeBox;
    @FXML private Button btnToggleChangePassword;

    // In-window SRS Help Modal
    @FXML private VBox helpModalBox;

    // Dedicated Reader Borrow Modal
    @FXML private VBox readerBorrowModalBox;
    @FXML private Label lblReaderBorrowName;
    @FXML private Label lblReaderBorrowId;
    @FXML private Label lblReaderBorrowStats;
    @FXML private Label lblReaderBorrowStatus;
    @FXML private VBox boxReaderBorrowBookSelect;
    @FXML private ComboBox<Book> cmbReaderBorrowBook;
    @FXML private HBox boxReaderBorrowBookSelected;
    @FXML private Label lblReaderBorrowBookTitle;
    @FXML private Label lblReaderBorrowBookAuthor;
    @FXML private Label lblReaderBorrowBookCategory;
    @FXML private Label lblReaderBorrowBookAvailable;
    @FXML private Button btnReaderBorrowChangeBook;
    @FXML private ComboBox<String> cmbReaderBorrowType;
    @FXML private TextField txtReaderBorrowNotes;
    @FXML private Label lblReaderBorrowMsg;
    @FXML private Button btnReaderBorrowSubmit;

    private Book currentReaderBorrowBook = null;

    // Navigation Buttons
    @FXML private Button btnNavHome;
    @FXML private Button btnNavBooks;
    @FXML private Button btnToggleCategories;
    @FXML private VBox categorySubmenuContainer;
    @FXML private Button btnNavCategories;
    @FXML private Button btnNavReaders;
    @FXML private Button btnNavBorrowReturn;
    @FXML private Button btnNavReturnBook;
    @FXML private Button btnNavReaderBorrow;
    @FXML private Button btnNavTransactions;
    @FXML private Button btnNavStats;
    @FXML private Button btnNavRecycleBin;
    @FXML private Button btnNavSettings;
    @FXML private Button btnHelp;

    // CARD 5: IN-APP NOTICE / MODAL OVERLAY
    @FXML private VBox appNoticeModalBox;
    @FXML private Label lblNoticeIconBadge;
    @FXML private Label lblNoticeTitle;
    @FXML private Label lblNoticeMessage;
    @FXML private TextArea txtNoticeDetails;
    @FXML private HBox boxNoticeActions;
    @FXML private Button btnNoticeCancel;
    @FXML private Button btnNoticeConfirm;

    private Runnable noticeConfirmAction;
    private Runnable noticeCancelAction;

    private static MainLayoutController instance;

    public static MainLayoutController getInstance() {
        return instance;
    }

    @FXML private Button btnHeaderSettings;
    @FXML private Button btnNotification;
    @FXML private ImageView logoImageView;
    @FXML private ImageView userAvatarImageView;

    // Header Profile & Roles
    @FXML private HBox userProfileBox;
    @FXML private Label lblHeaderUserName;
    @FXML private Label lblHeaderUserRole;
    @FXML private Label lblCurrentRoleBadge;

    // Section Labels & Sidebar Brave Vertical Tabs
    @FXML private VBox sidebarContainer;
    @FXML private HBox sidebarHeaderBox;
    @FXML private Region sidebarHeaderSpacer;
    @FXML private Label lblSidebarTitle;
    @FXML private Button btnPinSidebar;
    @FXML private SVGPath pinIconPath;
    @FXML private Tooltip pinTooltip;
    @FXML private Label lblSecMenu;
    @FXML private Label lblSecBooks;
    @FXML private Label lblSecReaders;
    @FXML private Label lblSecBorrow;
    @FXML private Label lblSecAdmin;
    @FXML private HBox sidebarRoleBox;

    private boolean isSidebarPinned = false;
    private boolean isSidebarExpanded = false;
    private Timeline sidebarTimeline;
    private final PauseTransition sidebarCollapseDebounce = new PauseTransition(Duration.millis(250));

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
    private final ReaderService readerService = new ReaderService();
    private final BorrowService borrowService = new BorrowService();
    private final SettingService settingService = SettingService.getInstance();
    private final PauseTransition searchDebounce = new PauseTransition(Duration.millis(120));

    private void showAlert(Alert.AlertType type, String title, String content) {
        showInAppNotice(type, title, content, null, null);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        instance = this;
        setupSearchAutocomplete();
        loadViews();
        updateUserSessionUI();
        setupBraveVerticalSidebar();
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
                lblSecReaders.setVisible(isLibrarian && isSidebarExpanded);
                lblSecReaders.setManaged(isLibrarian && isSidebarExpanded);
            }

            // Mượn - Trả Section
            if (btnNavBorrowReturn != null) {
                btnNavBorrowReturn.setVisible(isLibrarian);
                btnNavBorrowReturn.setManaged(isLibrarian);
            }
            if (btnNavReturnBook != null) {
                btnNavReturnBook.setVisible(isLibrarian);
                btnNavReturnBook.setManaged(isLibrarian);
            }
            if (btnNavReaderBorrow != null) {
                btnNavReaderBorrow.setVisible(isReader);
                btnNavReaderBorrow.setManaged(isReader);
            }
            if (btnNavTransactions != null) {
                btnNavTransactions.setVisible(isLibrarian || isReader);
                btnNavTransactions.setManaged(isLibrarian || isReader);
                btnNavTransactions.setText(isReader ? "Phiếu mượn của tôi" : "Lịch sử phiếu mượn");
            }
            if (lblSecBorrow != null) {
                lblSecBorrow.setVisible((isLibrarian || isReader) && isSidebarExpanded);
                lblSecBorrow.setManaged((isLibrarian || isReader) && isSidebarExpanded);
            }

            btnNavStats.setVisible(isLibrarian);
            btnNavStats.setManaged(isLibrarian);
            if (lblSecAdmin != null) {
                lblSecAdmin.setVisible(isLibrarian && isSidebarExpanded);
                lblSecAdmin.setManaged(isLibrarian && isSidebarExpanded);
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

            if (btnNavBorrowReturn != null) {
                btnNavBorrowReturn.setVisible(false);
                btnNavBorrowReturn.setManaged(false);
            }
            if (btnNavReturnBook != null) {
                btnNavReturnBook.setVisible(false);
                btnNavReturnBook.setManaged(false);
            }
            if (btnNavReaderBorrow != null) {
                btnNavReaderBorrow.setVisible(false);
                btnNavReaderBorrow.setManaged(false);
            }
            if (btnNavTransactions != null) {
                btnNavTransactions.setVisible(false);
                btnNavTransactions.setManaged(false);
            }
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
        if (homeController != null) {
            homeController.updateGreeting();
            homeController.applyPermissions();
        }
    }

    private void setActiveNavButton(Button activeButton) {
        Button[] navButtons = {
                btnNavHome, btnNavBooks, btnNavCategories,
                btnNavReaders, btnNavBorrowReturn, btnNavReturnBook, btnNavReaderBorrow,
                btnNavTransactions, btnNavStats, btnNavRecycleBin, btnNavSettings
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

    public void showBookViewWithSelection(Book book) {
        showBookView();
        if (bookController != null && book != null) {
            bookController.selectAndInspectBook(book);
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
        btnToggleCategories.setText(isVisible ? "▾ Thể loại" : "▸ Thể loại");
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
            if (tabIndex == 0) {
                setActiveNavButton(btnNavBorrowReturn);
            } else if (tabIndex == 1) {
                setActiveNavButton(btnNavReturnBook);
            } else if (tabIndex == 2) {
                setActiveNavButton(btnNavTransactions);
            } else if (tabIndex == 3) {
                setActiveNavButton(btnNavBorrowReturn);
            } else {
                setActiveNavButton(btnNavBorrowReturn);
            }
            if (borrowReturnController != null) {
                borrowReturnController.selectTab(tabIndex);
            }
        }
    }

    public void stageBookForBorrow(Book book) {
        if (!authService.isLoggedIn()) {
            showAuthPrompt("Vui lòng đăng nhập tài khoản để mượn sách!");
            return;
        }
        if (authService.isReader()) {
            openReaderBorrowModal(book);
        } else {
            showBorrowReturnView(0);
            if (borrowReturnController != null && book != null) {
                borrowReturnController.addToBasket(book);
            }
        }
    }

    @FXML
    public void handleNavBorrowReturn() {
        showBorrowReturnView(0);
    }

    @FXML
    public void handleNavBorrowDesk() {
        showBorrowReturnView(0);
    }

    @FXML
    public void handleNavReturnBook() {
        showBorrowReturnView(1);
    }

    @FXML
    public void handleNavBorrowWizard() {
        showBorrowReturnView(3);
    }

    @FXML
    public void handleOpenReaderBorrowModal() {
        openReaderBorrowModal(null);
    }

    @FXML
    public void handleNavTransactions() {
        showBorrowReturnView(2);
    }

    public void openReaderBorrowModal(Book book) {
        hideSearchDropdown();
        User u = authService.getCurrentUser();
        if (u == null) {
            showAlert(Alert.AlertType.WARNING, "Yêu cầu đăng nhập", "Vui lòng đăng nhập tài khoản Độc Giả để đăng ký mượn sách!");
            openAuthPopup();
            return;
        }

        if (helpModalBox != null) {
            helpModalBox.setVisible(false);
            helpModalBox.setManaged(false);
        }
        if (profileModalBox != null) {
            profileModalBox.setVisible(false);
            profileModalBox.setManaged(false);
        }
        if (authModalBox != null) {
            authModalBox.setVisible(false);
            authModalBox.setManaged(false);
        }

        if (readerBorrowModalBox != null) {
            readerBorrowModalBox.setVisible(true);
            readerBorrowModalBox.setManaged(true);
        }
        authOverlayPane.setVisible(true);
        authOverlayPane.setManaged(true);
        mainContainer.setEffect(new GaussianBlur(14));

        if (lblReaderBorrowMsg != null) {
            lblReaderBorrowMsg.setText("");
        }

        // Setup Reader info
        Reader reader = readerService.getReaderForUser(u);
        if (reader == null) {
            if (lblReaderBorrowName != null) lblReaderBorrowName.setText(u.getFullName());
            if (lblReaderBorrowId != null) lblReaderBorrowId.setText("Chưa liên kết thẻ");
            if (lblReaderBorrowStats != null) lblReaderBorrowStats.setText("Tài khoản chưa có mã độc giả");
            if (lblReaderBorrowStatus != null) {
                lblReaderBorrowStatus.setText("Thẻ: Chưa kích hoạt");
                lblReaderBorrowStatus.setStyle("-fx-text-fill: #EF4444; -fx-font-weight: bold;");
            }
            if (btnReaderBorrowSubmit != null) btnReaderBorrowSubmit.setDisable(true);
        } else {
            if (lblReaderBorrowName != null) lblReaderBorrowName.setText(reader.getFullName());
            if (lblReaderBorrowId != null) lblReaderBorrowId.setText(reader.getId());
            int activeCount = (int) borrowService.getTransactionsByReader(reader.getId()).stream()
                    .filter(t -> "Đang Mượn".equalsIgnoreCase(t.getStatus()))
                    .count();
            int maxAllowed = settingService.getMaxBooksPerReader();
            if (lblReaderBorrowStats != null) {
                lblReaderBorrowStats.setText("Đang mượn: " + activeCount + "/" + maxAllowed + " cuốn");
            }

            String status = reader.getStatus();
            if (lblReaderBorrowStatus != null) {
                lblReaderBorrowStatus.setText("Thẻ: " + status);
            }
            boolean canBorrow = !"Blocked".equalsIgnoreCase(status) && !"Expired".equalsIgnoreCase(status) && !"Chờ Cấp Thẻ".equalsIgnoreCase(status) && activeCount < maxAllowed;
            if (!canBorrow) {
                if (lblReaderBorrowStatus != null) {
                    lblReaderBorrowStatus.setStyle("-fx-text-fill: #EF4444; -fx-font-weight: bold;");
                }
                if (btnReaderBorrowSubmit != null) {
                    btnReaderBorrowSubmit.setDisable(true);
                }
                if (lblReaderBorrowMsg != null) {
                    if ("Chờ Cấp Thẻ".equalsIgnoreCase(status)) {
                        lblReaderBorrowMsg.setText("Thẻ của bạn chưa được cấp tại quầy. Vui lòng gặp Thủ Thư để kích hoạt!");
                    } else if (activeCount >= maxAllowed) {
                        lblReaderBorrowMsg.setText("Bạn đã mượn tối đa (" + maxAllowed + " cuốn). Vui lòng trả sách trước khi mượn tiếp!");
                    } else {
                        lblReaderBorrowMsg.setText("Thẻ độc giả chưa đủ điều kiện mượn sách. Vui lòng liên hệ thủ thư!");
                    }
                    lblReaderBorrowMsg.setStyle("-fx-text-fill: #EF4444;");
                }
            } else {
                if (lblReaderBorrowStatus != null) {
                    lblReaderBorrowStatus.setStyle("-fx-text-fill: #1DB954; -fx-font-weight: bold;");
                }
                if (btnReaderBorrowSubmit != null) {
                    btnReaderBorrowSubmit.setDisable(false);
                }
            }
        }

        // Setup borrow types
        if (cmbReaderBorrowType != null && cmbReaderBorrowType.getItems().isEmpty()) {
            cmbReaderBorrowType.getItems().addAll("Mang về nhà (14 ngày)", "Mượn đọc tại chỗ (trong ngày)");
        }
        if (cmbReaderBorrowType != null) {
            cmbReaderBorrowType.getSelectionModel().selectFirst();
        }

        // Setup book selection
        setReaderBorrowSelectedBook(book);
    }

    private void setReaderBorrowSelectedBook(Book book) {
        this.currentReaderBorrowBook = book;
        if (book != null) {
            if (boxReaderBorrowBookSelect != null) {
                boxReaderBorrowBookSelect.setVisible(false);
                boxReaderBorrowBookSelect.setManaged(false);
            }
            if (boxReaderBorrowBookSelected != null) {
                boxReaderBorrowBookSelected.setVisible(true);
                boxReaderBorrowBookSelected.setManaged(true);
            }

            if (lblReaderBorrowBookTitle != null) lblReaderBorrowBookTitle.setText(book.getTitle());
            if (lblReaderBorrowBookAuthor != null) lblReaderBorrowBookAuthor.setText("Tác giả: " + book.getAuthor());
            if (lblReaderBorrowBookCategory != null) lblReaderBorrowBookCategory.setText(book.getCategory());
            if (lblReaderBorrowBookAvailable != null) lblReaderBorrowBookAvailable.setText("Còn sẵn: " + book.getAvailableCopies() + " bản");
            if (book.getAvailableCopies() <= 0) {
                if (lblReaderBorrowMsg != null) {
                    lblReaderBorrowMsg.setText("Sách này hiện đã hết bản có sẵn để mượn!");
                    lblReaderBorrowMsg.setStyle("-fx-text-fill: #EF4444;");
                }
                if (btnReaderBorrowSubmit != null) btnReaderBorrowSubmit.setDisable(true);
            }
        } else {
            if (boxReaderBorrowBookSelect != null) {
                boxReaderBorrowBookSelect.setVisible(true);
                boxReaderBorrowBookSelect.setManaged(true);
            }
            if (boxReaderBorrowBookSelected != null) {
                boxReaderBorrowBookSelected.setVisible(false);
                boxReaderBorrowBookSelected.setManaged(false);
            }

            List<Book> availableBooks = bookService.getAllBooks().stream()
                    .filter(b -> b.getAvailableCopies() > 0)
                    .toList();
            if (cmbReaderBorrowBook != null) {
                cmbReaderBorrowBook.setItems(FXCollections.observableArrayList(availableBooks));
                cmbReaderBorrowBook.setConverter(new StringConverter<Book>() {
                    @Override
                    public String toString(Book b) {
                        return b == null ? "" : b.getTitle() + " - " + b.getAuthor() + " (Còn " + b.getAvailableCopies() + " bản)";
                    }
                    @Override
                    public Book fromString(String string) { return null; }
                });
                if (!availableBooks.isEmpty()) {
                    cmbReaderBorrowBook.getSelectionModel().selectFirst();
                }
            }
        }
    }

    @FXML
    public void handleReaderBorrowChangeBook() {
        setReaderBorrowSelectedBook(null);
    }

    @FXML
    public void handleExecuteReaderBorrow() {
        User u = authService.getCurrentUser();
        if (u == null) {
            if (lblReaderBorrowMsg != null) {
                lblReaderBorrowMsg.setText("Vui lòng đăng nhập trước khi mượn sách!");
                lblReaderBorrowMsg.setStyle("-fx-text-fill: #EF4444;");
            }
            return;
        }

        Reader reader = readerService.getReaderForUser(u);
        if (reader == null) {
            if (lblReaderBorrowMsg != null) {
                lblReaderBorrowMsg.setText("Tài khoản chưa có thẻ độc giả liên kết. Không thể mượn sách!");
                lblReaderBorrowMsg.setStyle("-fx-text-fill: #EF4444;");
            }
            return;
        }

        Book bookToBorrow = currentReaderBorrowBook;
        if (bookToBorrow == null && cmbReaderBorrowBook != null) {
            bookToBorrow = cmbReaderBorrowBook.getSelectionModel().getSelectedItem();
        }

        if (bookToBorrow == null) {
            if (lblReaderBorrowMsg != null) {
                lblReaderBorrowMsg.setText("Vui lòng chọn cuốn sách cần mượn!");
                lblReaderBorrowMsg.setStyle("-fx-text-fill: #EF4444;");
            }
            return;
        }

        String selectedType = (cmbReaderBorrowType != null) ? cmbReaderBorrowType.getSelectionModel().getSelectedItem() : null;
        String borrowType = (selectedType != null && selectedType.contains("tại chỗ")) ? "Mượn đọc tại chỗ" : "Mang về nhà";
        int days = "Mượn đọc tại chỗ".equalsIgnoreCase(borrowType) ? settingService.getMaxBorrowDaysOnsite() : settingService.getMaxBorrowDaysHome();
        String notes = (txtReaderBorrowNotes != null) ? txtReaderBorrowNotes.getText() : "";
        if (notes == null || notes.trim().isEmpty()) {
            notes = "Độc giả đăng ký online";
        }

        String error = borrowService.borrowBook(reader.getId(), bookToBorrow.getId(), borrowType, days, notes.trim());
        if (error != null) {
            if (lblReaderBorrowMsg != null) {
                lblReaderBorrowMsg.setText(error);
                lblReaderBorrowMsg.setStyle("-fx-text-fill: #EF4444;");
            }
        } else {
            closeAuthPopup();
            showAlert(Alert.AlertType.INFORMATION, "Mượn sách thành công",
                    "Chúc mừng bạn đã lập phiếu mượn thành công cho cuốn sách: \"" + bookToBorrow.getTitle() + "\"!\nThời hạn: " + days + " ngày.");
            if (bookController != null) {
                bookController.loadBooks();
            }
            if (homeController != null) {
                homeController.refreshData();
            }
            showBorrowReturnView(2);
        }
    }

    public VBox getReaderBorrowModalBox() {
        return readerBorrowModalBox;
    }

    public Button getBtnNavReturnBook() {
        return btnNavReturnBook;
    }

    public Button getBtnNavBorrowWizard() {
        return null;
    }

    public VBox getAppNoticeModalBox() {
        return appNoticeModalBox;
    }

    public Button getBtnNavReaderBorrow() {
        return btnNavReaderBorrow;
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
        showHelpPopup();
    }

    public void showHelpPopup() {
        hideSearchDropdown();
        authModalBox.setVisible(false);
        authModalBox.setManaged(false);
        profileModalBox.setVisible(false);
        profileModalBox.setManaged(false);
        if (readerBorrowModalBox != null) {
            readerBorrowModalBox.setVisible(false);
            readerBorrowModalBox.setManaged(false);
        }

        if (helpModalBox != null) {
            helpModalBox.setVisible(true);
            helpModalBox.setManaged(true);
        }

        authOverlayPane.setVisible(true);
        authOverlayPane.setManaged(true);
        mainContainer.setEffect(new GaussianBlur(14));
    }

    @FXML
    public void handleToggleChangePassword() {
        if (passwordChangeBox == null) return;
        boolean isVis = !passwordChangeBox.isVisible();
        passwordChangeBox.setVisible(isVis);
        passwordChangeBox.setManaged(isVis);
        if (btnToggleChangePassword != null) {
            btnToggleChangePassword.setText(isVis ? "🔑 Đổi mật khẩu ▴" : "🔑 Đổi mật khẩu ▾");
        }
    }

    public VBox getPasswordChangeBox() {
        return passwordChangeBox;
    }

    public Button getBtnToggleChangePassword() {
        return btnToggleChangePassword;
    }

    public VBox getHelpModalBox() {
        return helpModalBox;
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

        if (helpModalBox != null) {
            helpModalBox.setVisible(false);
            helpModalBox.setManaged(false);
        }
        if (readerBorrowModalBox != null) {
            readerBorrowModalBox.setVisible(false);
            readerBorrowModalBox.setManaged(false);
        }
        if (passwordChangeBox != null) {
            passwordChangeBox.setVisible(false);
            passwordChangeBox.setManaged(false);
        }
        if (btnToggleChangePassword != null) {
            btnToggleChangePassword.setText("🔑 Đổi mật khẩu ▾");
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
        if (helpModalBox != null) {
            helpModalBox.setVisible(false);
            helpModalBox.setManaged(false);
        }
        if (readerBorrowModalBox != null) {
            readerBorrowModalBox.setVisible(false);
            readerBorrowModalBox.setManaged(false);
        }
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
        if (helpModalBox != null) {
            helpModalBox.setVisible(false);
            helpModalBox.setManaged(false);
        }
        if (readerBorrowModalBox != null) {
            readerBorrowModalBox.setVisible(false);
            readerBorrowModalBox.setManaged(false);
        }
        if (appNoticeModalBox != null) {
            appNoticeModalBox.setVisible(false);
            appNoticeModalBox.setManaged(false);
        }
        if (passwordChangeBox != null) {
            passwordChangeBox.setVisible(false);
            passwordChangeBox.setManaged(false);
            if (btnToggleChangePassword != null) {
                btnToggleChangePassword.setText("🔑 Đổi mật khẩu ▾");
            }
        }
        lblLoginError.setText("");
        lblRegError.setText("");
        lblProfileMsg.setText("");
        if (lblReaderBorrowMsg != null) {
            lblReaderBorrowMsg.setText("");
        }
    }

    public void showInAppNotice(Alert.AlertType type, String title, String message, String details, Runnable onConfirm) {
        if (!javafx.application.Platform.isFxApplicationThread()) {
            javafx.application.Platform.runLater(() -> showInAppNotice(type, title, message, details, onConfirm));
            return;
        }

        hideSearchDropdown();
        this.noticeConfirmAction = onConfirm;
        this.noticeCancelAction = null;

        if (helpModalBox != null) {
            helpModalBox.setVisible(false);
            helpModalBox.setManaged(false);
        }
        if (readerBorrowModalBox != null) {
            readerBorrowModalBox.setVisible(false);
            readerBorrowModalBox.setManaged(false);
        }
        if (profileModalBox != null) {
            profileModalBox.setVisible(false);
            profileModalBox.setManaged(false);
        }
        if (authModalBox != null) {
            authModalBox.setVisible(false);
            authModalBox.setManaged(false);
        }

        if (appNoticeModalBox != null) {
            lblNoticeTitle.setText(title != null ? title : "Thông Báo Hệ Thống");
            lblNoticeMessage.setText(message != null ? message : "");

            if (type == Alert.AlertType.ERROR) {
                lblNoticeIconBadge.setText("✕");
                lblNoticeIconBadge.setStyle("-fx-background-color: #EF4444; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 3 8; -fx-background-radius: 4px;");
            } else if (type == Alert.AlertType.WARNING) {
                lblNoticeIconBadge.setText("⚠");
                lblNoticeIconBadge.setStyle("-fx-background-color: #F59E0B; -fx-text-fill: black; -fx-font-weight: bold; -fx-padding: 3 8; -fx-background-radius: 4px;");
            } else if (type == Alert.AlertType.CONFIRMATION) {
                lblNoticeIconBadge.setText("?");
                lblNoticeIconBadge.setStyle("-fx-background-color: #3B82F6; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 3 8; -fx-background-radius: 4px;");
            } else {
                lblNoticeIconBadge.setText("✓");
                lblNoticeIconBadge.setStyle("-fx-background-color: #10B981; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 3 8; -fx-background-radius: 4px;");
            }

            if (details != null && !details.trim().isEmpty()) {
                txtNoticeDetails.setText(details);
                txtNoticeDetails.setVisible(true);
                txtNoticeDetails.setManaged(true);
            } else {
                txtNoticeDetails.setText("");
                txtNoticeDetails.setVisible(false);
                txtNoticeDetails.setManaged(false);
            }

            if (type == Alert.AlertType.CONFIRMATION) {
                btnNoticeCancel.setVisible(true);
                btnNoticeCancel.setManaged(true);
                btnNoticeCancel.setText("Hủy Bỏ");
                btnNoticeConfirm.setText("Đồng Ý");
            } else {
                btnNoticeCancel.setVisible(false);
                btnNoticeCancel.setManaged(false);
                btnNoticeConfirm.setText("Đã Hiểu");
            }

            appNoticeModalBox.setVisible(true);
            appNoticeModalBox.setManaged(true);
            authOverlayPane.setVisible(true);
            authOverlayPane.setManaged(true);
            mainContainer.setEffect(new GaussianBlur(14));
        }
    }

    public void showInAppNotice(Alert.AlertType type, String title, String message) {
        showInAppNotice(type, title, message, null, null);
    }

    public void showInAppNotice(Alert.AlertType type, String title, String message, Runnable onConfirm) {
        showInAppNotice(type, title, message, null, onConfirm);
    }

    public void showAuthPrompt(String message) {
        if (!javafx.application.Platform.isFxApplicationThread()) {
            javafx.application.Platform.runLater(() -> showAuthPrompt(message));
            return;
        }

        hideSearchDropdown();
        this.noticeConfirmAction = this::openAuthPopup;
        this.noticeCancelAction = null;

        if (helpModalBox != null) {
            helpModalBox.setVisible(false);
            helpModalBox.setManaged(false);
        }
        if (readerBorrowModalBox != null) {
            readerBorrowModalBox.setVisible(false);
            readerBorrowModalBox.setManaged(false);
        }
        if (profileModalBox != null) {
            profileModalBox.setVisible(false);
            profileModalBox.setManaged(false);
        }
        if (authModalBox != null) {
            authModalBox.setVisible(false);
            authModalBox.setManaged(false);
        }

        if (appNoticeModalBox != null) {
            lblNoticeTitle.setText("Yêu Cầu Đăng Nhập");
            lblNoticeMessage.setText(message != null ? message : "Bạn cần đăng nhập tài khoản để thực hiện thao tác này!");
            lblNoticeIconBadge.setText("🔒");
            lblNoticeIconBadge.setStyle("-fx-background-color: #F59E0B; -fx-text-fill: black; -fx-font-weight: bold; -fx-padding: 3 8; -fx-background-radius: 4px;");

            txtNoticeDetails.setText("");
            txtNoticeDetails.setVisible(false);
            txtNoticeDetails.setManaged(false);

            btnNoticeCancel.setVisible(true);
            btnNoticeCancel.setManaged(true);
            btnNoticeCancel.setText("Để Sau");

            btnNoticeConfirm.setText("Đăng Nhập Ngay");

            appNoticeModalBox.setVisible(true);
            appNoticeModalBox.setManaged(true);
            authOverlayPane.setVisible(true);
            authOverlayPane.setManaged(true);
            mainContainer.setEffect(new GaussianBlur(14));
        }
    }

    public void showReceiptModal(String title, String heading, String receiptText) {
        if (!javafx.application.Platform.isFxApplicationThread()) {
            javafx.application.Platform.runLater(() -> showReceiptModal(title, heading, receiptText));
            return;
        }

        hideSearchDropdown();
        this.noticeConfirmAction = null;
        this.noticeCancelAction = null;

        if (helpModalBox != null) {
            helpModalBox.setVisible(false);
            helpModalBox.setManaged(false);
        }
        if (readerBorrowModalBox != null) {
            readerBorrowModalBox.setVisible(false);
            readerBorrowModalBox.setManaged(false);
        }
        if (profileModalBox != null) {
            profileModalBox.setVisible(false);
            profileModalBox.setManaged(false);
        }
        if (authModalBox != null) {
            authModalBox.setVisible(false);
            authModalBox.setManaged(false);
        }

        if (appNoticeModalBox != null) {
            lblNoticeTitle.setText(title != null ? title : "Phiếu Lưu Hành Thư Viện");
            lblNoticeMessage.setText(heading != null ? heading : "Chi tiết mẫu phiếu in:");
            lblNoticeIconBadge.setText("📄");
            lblNoticeIconBadge.setStyle("-fx-background-color: #10B981; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 3 8; -fx-background-radius: 4px;");

            txtNoticeDetails.setText(receiptText != null ? receiptText : "");
            txtNoticeDetails.setVisible(true);
            txtNoticeDetails.setManaged(true);

            btnNoticeCancel.setVisible(false);
            btnNoticeCancel.setManaged(false);
            btnNoticeConfirm.setText("Đóng / In Xong");

            appNoticeModalBox.setVisible(true);
            appNoticeModalBox.setManaged(true);
            authOverlayPane.setVisible(true);
            authOverlayPane.setManaged(true);
            mainContainer.setEffect(new GaussianBlur(14));
        }
    }

    @FXML
    public void handleNoticeConfirm() {
        Runnable action = this.noticeConfirmAction;
        this.noticeConfirmAction = null;
        closeAuthPopup();
        if (action != null) {
            action.run();
        }
    }

    @FXML
    public void handleNoticeCancel() {
        Runnable action = this.noticeCancelAction;
        this.noticeCancelAction = null;
        closeAuthPopup();
        if (action != null) {
            action.run();
        }
    }

    public static void showAppNotice(Alert.AlertType type, String title, String message, String details, Runnable onConfirm) {
        if (instance != null) {
            instance.showInAppNotice(type, title, message, details, onConfirm);
        } else {
            org.slf4j.LoggerFactory.getLogger(MainLayoutController.class).info("[NOTICE - {}] {}: {} ({})", type, title, message, details);
            if (type == Alert.AlertType.CONFIRMATION && onConfirm != null) {
                onConfirm.run();
            }
        }
    }

    public static void showAppNotice(Alert.AlertType type, String title, String message) {
        showAppNotice(type, title, message, null, null);
    }

    public static void showAppNotice(Alert.AlertType type, String title, String message, Runnable onConfirm) {
        showAppNotice(type, title, message, null, onConfirm);
    }

    public static void showAuthPromptModal(String message) {
        if (instance != null) {
            instance.showAuthPrompt(message);
        } else {
            org.slf4j.LoggerFactory.getLogger(MainLayoutController.class).info("[AUTH PROMPT] {}", message);
        }
    }

    public static void showReceiptPopup(String title, String heading, String receiptText) {
        if (instance != null) {
            instance.showReceiptModal(title, heading, receiptText);
        } else {
            org.slf4j.LoggerFactory.getLogger(MainLayoutController.class).info("[RECEIPT - {}] {}:\n{}", title, heading, receiptText);
        }
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
            showInAppNotice(Alert.AlertType.INFORMATION, "Đăng Ký Thành Công",
                    "Hồ sơ độc giả trực tuyến đã được tạo!\nTrạng thái thẻ của bạn là: 'Chờ Cấp Thẻ'.\nVui lòng mang theo CCCD đến thư viện để Thủ Thư đối chiếu và kích hoạt thẻ chính thức trước khi mượn sách.");
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
        showInAppNotice(Alert.AlertType.INFORMATION, "Thông Báo Hệ Thống",
                "Hệ thống hoạt động bình thường.\nĐang theo dõi các phiếu mượn và tự động kiểm tra thời hạn thẻ.");
    }

    @FXML
    public void handleHeaderSettingsClick() {
        ContextMenu menu = new ContextMenu();
        com.vithay.libman.service.ThemeManager tm = com.vithay.libman.service.ThemeManager.getInstance();
        boolean isPink = tm.getCurrentTheme() == com.vithay.libman.service.ThemeManager.Theme.PINK_LIGHT;
        MenuItem itemTheme = new MenuItem(isPink ? "Chuyển sang Giao Diện Tối" : "Chuyển sang Giao Diện Sáng (Modern Pink)");
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
        showInAppNotice(Alert.AlertType.INFORMATION, "Hộp Thư Nội Bộ",
                "Không có tin nhắn mới nào.");
    }

    private void setupBraveVerticalSidebar() {
        if (sidebarContainer == null) return;

        sidebarContainer.setMinWidth(64);
        sidebarContainer.setMaxWidth(250);

        sidebarCollapseDebounce.setOnFinished(e -> {
            if (!isSidebarPinned && isSidebarExpanded) {
                collapseSidebar();
            }
        });

        // Hover expand/collapse when not pinned with debounce
        sidebarContainer.setOnMouseEntered(e -> {
            sidebarCollapseDebounce.stop();
            if (!isSidebarPinned && !isSidebarExpanded) {
                expandSidebar();
            }
        });

        sidebarContainer.setOnMouseExited(e -> {
            if (!isSidebarPinned && isSidebarExpanded) {
                sidebarCollapseDebounce.playFromStart();
            }
        });

        // Initialize state (unpinned -> collapsed by default)
        if (isSidebarPinned) {
            isSidebarExpanded = true;
            sidebarContainer.setPrefWidth(250);
            applyExpandedState();
        } else {
            isSidebarExpanded = false;
            sidebarContainer.setPrefWidth(64);
            applyCollapsedState();
        }
    }

    @FXML
    public void handleTogglePinSidebar() {
        isSidebarPinned = !isSidebarPinned;
        sidebarCollapseDebounce.stop();
        if (isSidebarPinned) {
            expandSidebar();
            if (btnPinSidebar != null) {
                btnPinSidebar.getStyleClass().add("btn-pin-active");
            }
            if (pinTooltip != null) {
                pinTooltip.setText("Bỏ ghim (tự động thu gọn khi rời chuột)");
            }
        } else {
            if (btnPinSidebar != null) {
                btnPinSidebar.getStyleClass().remove("btn-pin-active");
            }
            if (pinTooltip != null) {
                pinTooltip.setText("Ghim thanh bên (không tự động thu nhỏ)");
            }
            collapseSidebar();
        }
    }

    private void collapseSidebar() {
        if (sidebarContainer == null) return;
        if (!isSidebarExpanded) return;
        isSidebarExpanded = false;

        if (sidebarTimeline != null) {
            sidebarTimeline.stop();
        }
        sidebarTimeline = new Timeline(
                new KeyFrame(Duration.millis(160),
                        new KeyValue(sidebarContainer.prefWidthProperty(), 64, Interpolator.EASE_OUT)
                )
        );
        sidebarTimeline.play();

        applyCollapsedState();
    }

    private void applyCollapsedState() {
        if (!sidebarContainer.getStyleClass().contains("sidebar-collapsed")) {
            sidebarContainer.getStyleClass().add("sidebar-collapsed");
        }

        Button[] navButtons = {
                btnNavHome, btnNavBooks, btnNavCategories,
                btnNavReaders, btnNavBorrowReturn, btnNavReturnBook, btnNavReaderBorrow,
                btnNavTransactions, btnNavStats,
                btnNavRecycleBin, btnNavSettings, btnHelp
        };
        for (Button btn : navButtons) {
            if (btn != null) {
                btn.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                btn.setAlignment(Pos.CENTER);
            }
        }

        if (sidebarHeaderSpacer != null) {
            sidebarHeaderSpacer.setManaged(false);
            sidebarHeaderSpacer.setVisible(false);
        }
        if (sidebarHeaderBox != null) {
            sidebarHeaderBox.setAlignment(Pos.CENTER);
        }

        Label[] sectionLabels = {lblSecMenu, lblSecBooks, lblSecReaders, lblSecBorrow, lblSecAdmin};
        for (Label lbl : sectionLabels) {
            if (lbl != null) {
                lbl.setManaged(false);
                lbl.setVisible(false);
            }
        }
        if (lblSidebarTitle != null) {
            lblSidebarTitle.setManaged(false);
            lblSidebarTitle.setVisible(false);
        }
        if (btnToggleCategories != null) {
            btnToggleCategories.setManaged(false);
            btnToggleCategories.setVisible(false);
        }
        if (categorySubmenuContainer != null) {
            categorySubmenuContainer.setManaged(false);
            categorySubmenuContainer.setVisible(false);
        }
        if (sidebarRoleBox != null) {
            sidebarRoleBox.setManaged(false);
            sidebarRoleBox.setVisible(false);
        }
    }

    private void expandSidebar() {
        if (sidebarContainer == null) return;
        if (isSidebarExpanded) return;
        isSidebarExpanded = true;

        if (sidebarTimeline != null) {
            sidebarTimeline.stop();
        }
        sidebarTimeline = new Timeline(
                new KeyFrame(Duration.millis(160),
                        new KeyValue(sidebarContainer.prefWidthProperty(), 250, Interpolator.EASE_OUT)
                )
        );
        sidebarTimeline.play();

        applyExpandedState();
    }

    private void applyExpandedState() {
        sidebarContainer.getStyleClass().remove("sidebar-collapsed");

        Button[] navButtons = {
                btnNavHome, btnNavBooks, btnNavCategories,
                btnNavReaders, btnNavBorrowReturn, btnNavReturnBook, btnNavReaderBorrow,
                btnNavTransactions, btnNavStats,
                btnNavRecycleBin, btnNavSettings, btnHelp
        };
        for (Button btn : navButtons) {
            if (btn != null) {
                btn.setContentDisplay(ContentDisplay.LEFT);
                btn.setAlignment(Pos.CENTER_LEFT);
            }
        }

        if (sidebarHeaderSpacer != null) {
            sidebarHeaderSpacer.setManaged(true);
            sidebarHeaderSpacer.setVisible(true);
        }
        if (sidebarHeaderBox != null) {
            sidebarHeaderBox.setAlignment(Pos.CENTER_LEFT);
        }

        Label[] sectionLabels = {lblSecMenu, lblSecBooks, lblSecReaders, lblSecBorrow, lblSecAdmin};
        for (Label lbl : sectionLabels) {
            if (lbl != null) {
                boolean show = shouldSectionBeVisible(lbl);
                lbl.setManaged(show);
                lbl.setVisible(show);
            }
        }
        if (lblSidebarTitle != null) {
            lblSidebarTitle.setManaged(true);
            lblSidebarTitle.setVisible(true);
        }
        if (btnToggleCategories != null) {
            btnToggleCategories.setManaged(true);
            btnToggleCategories.setVisible(true);
        }
        if (sidebarRoleBox != null) {
            sidebarRoleBox.setManaged(true);
            sidebarRoleBox.setVisible(true);
        }
    }

    private boolean shouldSectionBeVisible(Label lbl) {
        User u = authService.getCurrentUser();
        if (u == null) {
            return lbl == lblSecMenu || lbl == lblSecBooks;
        }
        boolean isLibrarian = authService.isLibrarian();
        boolean isReader = authService.isReader();
        if (lbl == lblSecMenu || lbl == lblSecBooks) return true;
        if (lbl == lblSecReaders) return isLibrarian;
        if (lbl == lblSecBorrow) return isLibrarian || isReader;
        if (lbl == lblSecAdmin) return isLibrarian;
        return true;
    }
}
