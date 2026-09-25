package com.vithay.libman.controller;

import com.vithay.libman.model.Reader;
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
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class ReaderManagementController implements Initializable {
    private static final Logger logger = LoggerFactory.getLogger(ReaderManagementController.class);

    @FXML private StackPane readerRootPane;
    @FXML private VBox readerMainContainer;
    @FXML private ComboBox<String> readerStatusFilterCombo;
    @FXML private TextField txtSearchReader;
    @FXML private Button btnAddReader;
    @FXML private Button btnExportReaders;
    @FXML private Button btnScanExpired;

    @FXML private TableView<Reader> readersTable;
    @FXML private TableColumn<Reader, String> colReaderId;
    @FXML private TableColumn<Reader, String> colReaderName;
    @FXML private TableColumn<Reader, String> colReaderEmail;
    @FXML private TableColumn<Reader, String> colReaderPhone;
    @FXML private TableColumn<Reader, String> colReaderIdCard;
    @FXML private TableColumn<Reader, String> colReaderAddress;
    @FXML private TableColumn<Reader, String> colReaderExpiry;
    @FXML private TableColumn<Reader, String> colReaderStatus;
    @FXML private TableColumn<Reader, Void> colReaderActions;

    // In-Window Modal Overlay Fields
    @FXML private StackPane readerModalOverlay;
    @FXML private VBox readerModalBox;
    @FXML private Label lblReaderModalTitle;
    @FXML private Label lblReaderModalError;
    @FXML private TextField txtModalReaderId;
    @FXML private TextField txtModalReaderName;
    @FXML private TextField txtModalReaderEmail;
    @FXML private TextField txtModalReaderPhone;
    @FXML private TextField txtModalReaderIdCard;
    @FXML private TextField txtModalReaderAddress;
    @FXML private TextField txtModalReaderBirth;
    @FXML private TextField txtModalReaderIssueDate;
    @FXML private TextField txtModalReaderExpiryDate;
    @FXML private ComboBox<String> comboModalReaderStatus;
    private Reader currentEditingReader = null;

    private final ReaderService readerService = new ReaderService();
    private final ExportService exportService = new ExportService();
    private final SettingService settingService = SettingService.getInstance();

    private final ObservableList<Reader> readerMasterList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupFilter();
        setupTable();
        loadReaders();
    }

    private void setupFilter() {
        readerStatusFilterCombo.setItems(FXCollections.observableArrayList("Tất cả", "Hoạt Động", "Chờ Cấp Thẻ", "Bị Khóa", "Hết Hạn"));
        readerStatusFilterCombo.getSelectionModel().selectFirst();
    }

    private void setupTable() {
        colReaderId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colReaderName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colReaderEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colReaderPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colReaderIdCard.setCellValueFactory(new PropertyValueFactory<>("idCard"));
        colReaderAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colReaderExpiry.setCellValueFactory(new PropertyValueFactory<>("cardExpiryDate"));

        colReaderExpiry.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("Chưa cấp");
                    setStyle("-fx-text-fill: #727272; -fx-font-style: italic;");
                } else {
                    setText(item);
                    setStyle("-fx-text-fill: #E0E0E0;");
                }
            }
        });

        // Status Badge Cell
        colReaderStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colReaderStatus.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    Label badge = new Label(status);
                    if ("Active".equalsIgnoreCase(status)) {
                        badge.getStyleClass().add("badge-available");
                        badge.setText("Hoạt Động");
                    } else if ("Chờ Cấp Thẻ".equalsIgnoreCase(status)) {
                        badge.getStyleClass().add("badge-onhold");
                        badge.setText("Chờ Cấp Thẻ");
                    } else if ("Expired".equalsIgnoreCase(status)) {
                        badge.getStyleClass().add("badge-borrowed");
                        badge.setText("Hết Hạn");
                    } else {
                        badge.getStyleClass().add("badge-overdue");
                        badge.setText("Bị Khóa");
                    }
                    HBox box = new HBox(badge);
                    box.setAlignment(Pos.CENTER);
                    setGraphic(box);
                    setText(null);
                }
            }
        });

        // Action Buttons: Cấp Thẻ (khi Chờ cấp thẻ), Sửa, Khóa/Mở, Xóa tạm
        colReaderActions.setCellFactory(param -> new TableCell<>() {
            private final Button btnIssue = new Button("✓ Cấp Thẻ");
            private final Button btnEdit = new Button("Sửa");
            private final Button btnToggle = new Button("Khóa/Mở");
            private final Button btnDelete = new Button("Xóa");
            private final HBox pane = new HBox(4);

            {
                pane.setAlignment(Pos.CENTER);

                btnIssue.getStyleClass().add("btn-primary");
                btnIssue.setStyle("-fx-font-size: 11px; -fx-padding: 3 6;");
                btnIssue.setOnAction(e -> {
                    Reader r = getTableView().getItems().get(getIndex());
                    handleIssueCard(r);
                });

                btnEdit.getStyleClass().add("btn-secondary");
                btnEdit.setStyle("-fx-font-size: 11px; -fx-padding: 3 6;");
                btnEdit.setOnAction(e -> {
                    Reader r = getTableView().getItems().get(getIndex());
                    showReaderFormModal(r);
                });

                btnToggle.getStyleClass().add("btn-secondary");
                btnToggle.setStyle("-fx-font-size: 11px; -fx-padding: 3 6;");
                btnToggle.setOnAction(e -> {
                    Reader r = getTableView().getItems().get(getIndex());
                    r.setStatus("Active".equalsIgnoreCase(r.getStatus()) ? "Blocked" : "Active");
                    readerService.saveReader(r);
                    loadReaders();
                });

                btnDelete.getStyleClass().add("btn-danger");
                btnDelete.setStyle("-fx-font-size: 11px; -fx-padding: 3 6;");
                btnDelete.setOnAction(e -> {
                    Reader r = getTableView().getItems().get(getIndex());
                    handleSoftDeleteReader(r);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Reader r = getTableView().getItems().get(getIndex());
                    pane.getChildren().clear();
                    if ("Chờ Cấp Thẻ".equalsIgnoreCase(r.getStatus())) {
                        pane.getChildren().add(btnIssue);
                    }
                    pane.getChildren().addAll(btnEdit, btnToggle, btnDelete);
                    setGraphic(pane);
                }
            }
        });
    }

    private void handleIssueCard(Reader reader) {
        MainLayoutController.showAppNotice(
                Alert.AlertType.CONFIRMATION,
                "Xác Nhận Cấp Thẻ Thư Viện",
                "Cấp thẻ thư viện chính thức cho độc giả: " + reader.getFullName() + " (CCCD: " + reader.getIdCard() + ")?\n" +
                "Hệ thống sẽ kích hoạt tài khoản sang trạng thái 'Hoạt Động' với thời hạn " + settingService.getCardValidityMonths() + " tháng.",
                () -> {
                    boolean ok = readerService.issueCard(reader.getId());
                    if (ok) {
                        loadReaders();
                        MainLayoutController.showAppNotice(Alert.AlertType.INFORMATION, "Cấp Thẻ Thành Công", "✓ Đã cấp thẻ thư viện thành công cho độc giả " + reader.getFullName() + "!");
                    } else {
                        MainLayoutController.showAppNotice(Alert.AlertType.ERROR, "Lỗi Kích Hoạt Thẻ", "Lỗi khi kích hoạt thẻ độc giả!");
                    }
                }
        );
    }

    public void loadReaders() {
        List<Reader> list = readerService.getAllReaders();
        readerMasterList.setAll(list);
        applyFilters();
    }

    @FXML
    public void handleScanExpiredReaders() {
        int updated = readerService.deactivateExpiredReaders();
        loadReaders();
        MainLayoutController.showAppNotice(
                Alert.AlertType.INFORMATION,
                "Quét Hạn Thẻ Độc Giả",
                "Kết quả rà soát tự động:\nĐã cập nhật " + updated + " độc giả sang trạng thái 'Expired' (Hết hạn)."
        );
    }

    @FXML
    public void handleExportCsv() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Lưu Danh Sách Độc Giả (CSV)");
        chooser.setInitialFileName("danh_sach_doc_gia.csv");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files (*.csv)", "*.csv"));
        File file = chooser.showSaveDialog(readersTable.getScene().getWindow());
        if (file != null) {
            boolean ok = exportService.exportReadersToCsv(file);
            if (ok) {
                MainLayoutController.showAppNotice(Alert.AlertType.INFORMATION, "Xuất Danh Sách Thành Công", "Xuất danh sách độc giả thành công: " + file.getName());
            } else {
                MainLayoutController.showAppNotice(Alert.AlertType.ERROR, "Lỗi Xuất File", "Lỗi khi xuất danh sách độc giả ra CSV!");
            }
        }
    }

    @FXML
    public void handleStatusFilter() {
        applyFilters();
    }

    @FXML
    public void handleSearchReader() {
        applyFilters();
    }

    public void filterReaders(String keyword) {
        if (txtSearchReader != null) {
            txtSearchReader.setText(keyword);
            applyFilters();
        }
    }

    private void applyFilters() {
        String statusFilter = readerStatusFilterCombo.getValue();
        String search = txtSearchReader.getText() != null ? txtSearchReader.getText().toLowerCase().trim() : "";

        ObservableList<Reader> filtered = FXCollections.observableArrayList();
        for (Reader r : readerMasterList) {
            String st = r.getStatus() != null ? r.getStatus() : "";
            boolean matchesStatus = true;
            if (statusFilter != null && !"Tất cả".equals(statusFilter)) {
                if ("Hoạt Động".equalsIgnoreCase(statusFilter) || "Active".equalsIgnoreCase(statusFilter)) {
                    matchesStatus = "Active".equalsIgnoreCase(st) || "Hoạt Động".equalsIgnoreCase(st);
                } else if ("Chờ Cấp Thẻ".equalsIgnoreCase(statusFilter)) {
                    matchesStatus = "Chờ Cấp Thẻ".equalsIgnoreCase(st);
                } else if ("Hết Hạn".equalsIgnoreCase(statusFilter) || "Expired".equalsIgnoreCase(statusFilter)) {
                    matchesStatus = "Expired".equalsIgnoreCase(st) || "Hết Hạn".equalsIgnoreCase(st);
                } else if ("Bị Khóa".equalsIgnoreCase(statusFilter) || "Blocked".equalsIgnoreCase(statusFilter)) {
                    matchesStatus = "Blocked".equalsIgnoreCase(st) || "Bị Khóa".equalsIgnoreCase(st);
                } else {
                    matchesStatus = statusFilter.equalsIgnoreCase(st);
                }
            }
            boolean matchesSearch = search.isEmpty() ||
                    r.getId().toLowerCase().contains(search) ||
                    r.getFullName().toLowerCase().contains(search) ||
                    (r.getEmail() != null && r.getEmail().toLowerCase().contains(search)) ||
                    (r.getPhone() != null && r.getPhone().toLowerCase().contains(search)) ||
                    (r.getCardExpiryDate() != null && r.getCardExpiryDate().toLowerCase().contains(search)) ||
                    (r.getIdCard() != null && r.getIdCard().toLowerCase().contains(search));

            if (matchesStatus && matchesSearch) {
                filtered.add(r);
            }
        }
        readersTable.setItems(filtered);
    }

    @FXML
    public void handleOpenAddReader() {
        showReaderFormModal(null);
    }

    public void showReaderFormModal(Reader readerToEdit) {
        this.currentEditingReader = readerToEdit;
        lblReaderModalError.setText("");

        comboModalReaderStatus.setItems(FXCollections.observableArrayList("Hoạt Động", "Chờ Cấp Thẻ", "Bị Khóa", "Hết Hạn"));

        int validityMonths = settingService.getCardValidityMonths();
        LocalDate now = LocalDate.now();

        if (readerToEdit != null) {
            lblReaderModalTitle.setText("Cập Nhật Hồ Sơ Độc Giả");
            txtModalReaderId.setText(readerToEdit.getId());
            txtModalReaderId.setDisable(true);
            txtModalReaderName.setText(readerToEdit.getFullName());
            txtModalReaderEmail.setText(readerToEdit.getEmail());
            txtModalReaderPhone.setText(readerToEdit.getPhone());
            txtModalReaderIdCard.setText(readerToEdit.getIdCard());
            txtModalReaderAddress.setText(readerToEdit.getAddress());
            txtModalReaderBirth.setText(readerToEdit.getBirthDate() != null ? readerToEdit.getBirthDate() : "2000-01-01");
            txtModalReaderIssueDate.setText(readerToEdit.getCardIssueDate() != null ? readerToEdit.getCardIssueDate() : "");
            txtModalReaderExpiryDate.setText(readerToEdit.getCardExpiryDate() != null ? readerToEdit.getCardExpiryDate() : "");
            
            String st = readerToEdit.getStatus();
            if ("Active".equalsIgnoreCase(st) || "Hoạt Động".equalsIgnoreCase(st)) {
                comboModalReaderStatus.setValue("Hoạt Động");
            } else if ("Chờ Cấp Thẻ".equalsIgnoreCase(st)) {
                comboModalReaderStatus.setValue("Chờ Cấp Thẻ");
            } else if ("Expired".equalsIgnoreCase(st) || "Hết Hạn".equalsIgnoreCase(st)) {
                comboModalReaderStatus.setValue("Hết Hạn");
            } else {
                comboModalReaderStatus.setValue("Bị Khóa");
            }
        } else {
            lblReaderModalTitle.setText("Thêm Độc Giả Mới");
            txtModalReaderId.setText("DG00" + (readerMasterList.size() + 1));
            txtModalReaderId.setDisable(false);
            txtModalReaderName.setText("");
            txtModalReaderEmail.setText("");
            txtModalReaderPhone.setText("");
            txtModalReaderIdCard.setText("048");
            txtModalReaderAddress.setText("123 Nguyễn Văn Linh, Q. Hải Châu, TP. Đà Nẵng");
            txtModalReaderBirth.setText("2000-01-01");
            txtModalReaderIssueDate.setText(now.toString());
            txtModalReaderExpiryDate.setText(now.plusMonths(validityMonths).toString());
            comboModalReaderStatus.setValue("Hoạt Động");
        }

        readerMainContainer.setEffect(new GaussianBlur(14));
        readerModalOverlay.setVisible(true);
        readerModalOverlay.setManaged(true);
    }

    @FXML
    public void closeReaderModal() {
        readerMainContainer.setEffect(null);
        readerModalOverlay.setVisible(false);
        readerModalOverlay.setManaged(false);
    }

    @FXML
    public void handleSaveReaderModal() {
        String id = txtModalReaderId.getText() != null ? txtModalReaderId.getText().trim() : "";
        String name = txtModalReaderName.getText() != null ? txtModalReaderName.getText().trim() : "";
        String phone = txtModalReaderPhone.getText() != null ? txtModalReaderPhone.getText().trim() : "";
        String idCard = txtModalReaderIdCard.getText() != null ? txtModalReaderIdCard.getText().trim() : "";
        String address = txtModalReaderAddress.getText() != null ? txtModalReaderAddress.getText().trim() : "";
        String rawStatus = comboModalReaderStatus.getValue() != null ? comboModalReaderStatus.getValue() : "Hoạt Động";
        String status = "Active";
        if ("Chờ Cấp Thẻ".equalsIgnoreCase(rawStatus)) {
            status = "Chờ Cấp Thẻ";
        } else if ("Hết Hạn".equalsIgnoreCase(rawStatus) || "Expired".equalsIgnoreCase(rawStatus)) {
            status = "Expired";
        } else if ("Bị Khóa".equalsIgnoreCase(rawStatus) || "Blocked".equalsIgnoreCase(rawStatus)) {
            status = "Blocked";
        }

        if (id.isEmpty() || name.isEmpty() || phone.isEmpty() || idCard.isEmpty()) {
            lblReaderModalError.setText("Vui lòng điền đầy đủ: Mã độc giả, Họ tên, SĐT và CCCD!");
            lblReaderModalError.setStyle("-fx-text-fill: #EF4444;");
            return;
        }

        // Chuẩn hóa 12 số CCCD
        if (!idCard.matches("\\d{12}")) {
            lblReaderModalError.setText("Số CCCD phải đúng chuẩn 12 chữ số (VD: 048201012345)!");
            lblReaderModalError.setStyle("-fx-text-fill: #EF4444;");
            return;
        }

        String issueDate = "Chờ Cấp Thẻ".equalsIgnoreCase(status) ? null : txtModalReaderIssueDate.getText().trim();
        String expiryDate = "Chờ Cấp Thẻ".equalsIgnoreCase(status) ? null : txtModalReaderExpiryDate.getText().trim();
        String joinDate = currentEditingReader != null ? currentEditingReader.getJoinDate() : LocalDate.now().toString();

        Reader reader = new Reader(
                id,
                name,
                txtModalReaderEmail.getText() != null ? txtModalReaderEmail.getText().trim() : "",
                phone,
                address,
                idCard,
                txtModalReaderBirth.getText() != null ? txtModalReaderBirth.getText().trim() : "2000-01-01",
                joinDate,
                issueDate,
                expiryDate,
                status,
                false
        );

        boolean saved = readerService.saveReader(reader);
        if (saved) {
            closeReaderModal();
            loadReaders();
        } else {
            lblReaderModalError.setText("Lỗi: Không thể lưu hồ sơ độc giả (Mã độc giả có thể đã trùng)!");
            lblReaderModalError.setStyle("-fx-text-fill: #EF4444;");
        }
    }

    private void handleSoftDeleteReader(Reader reader) {
        MainLayoutController.showAppNotice(
                Alert.AlertType.CONFIRMATION,
                "Xác Nhận Xóa Độc Giả",
                "Chuyển hồ sơ độc giả '" + reader.getFullName() + "' vào Thùng Rác?\nHồ sơ sẽ được ẩn khỏi danh sách chính nhưng có thể khôi phục lại từ Thùng Rác.",
                () -> {
                    readerService.softDeleteReader(reader.getId());
                    loadReaders();
                }
        );
    }
}
