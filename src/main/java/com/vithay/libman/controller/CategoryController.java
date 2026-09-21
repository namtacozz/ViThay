package com.vithay.libman.controller;

import com.vithay.libman.model.Category;
import com.vithay.libman.service.CategoryService;
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

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class CategoryController implements Initializable {

    @FXML private StackPane categoryRootPane;
    @FXML private VBox categoryMainContainer;
    @FXML private TextField txtSearchCategory;
    @FXML private TableView<Category> categoryTable;
    @FXML private TableColumn<Category, Integer> colCatId;
    @FXML private TableColumn<Category, String> colCatName;
    @FXML private TableColumn<Category, String> colCatDesc;
    @FXML private TableColumn<Category, Void> colCatActions;

    // In-Window Modal Overlay Fields
    @FXML private StackPane categoryModalOverlay;
    @FXML private VBox categoryModalBox;
    @FXML private Label lblCategoryModalTitle;
    @FXML private Label lblCategoryModalError;
    @FXML private TextField txtModalCatName;
    @FXML private TextField txtModalCatDesc;
    private Category currentEditingCat = null;

    private final CategoryService categoryService = new CategoryService();
    private final ObservableList<Category> masterList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colCatId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colCatName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colCatDesc.setCellValueFactory(new PropertyValueFactory<>("description"));

        colCatActions.setCellFactory(param -> new TableCell<>() {
            private final Button btnEdit = new Button("Sửa");
            private final Button btnDelete = new Button("Xóa");
            private final HBox pane = new HBox(6, btnEdit, btnDelete);

            {
                pane.setAlignment(Pos.CENTER);
                btnEdit.getStyleClass().add("btn-secondary");
                btnEdit.setStyle("-fx-font-size: 11px; -fx-padding: 3 8;");
                btnEdit.setOnAction(e -> {
                    Category cat = getTableView().getItems().get(getIndex());
                    showCategoryModal(cat);
                });

                btnDelete.getStyleClass().add("btn-danger");
                btnDelete.setStyle("-fx-font-size: 11px; -fx-padding: 3 8;");
                btnDelete.setOnAction(e -> {
                    Category cat = getTableView().getItems().get(getIndex());
                    handleDeleteCategory(cat);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });

        loadCategories();
    }

    public void loadCategories() {
        List<Category> list = categoryService.getAllCategories();
        masterList.setAll(list);
        filterCategories(txtSearchCategory != null ? txtSearchCategory.getText() : "");
    }

    @FXML
    public void handleSearchCategory() {
        filterCategories(txtSearchCategory.getText());
    }

    private void filterCategories(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            categoryTable.setItems(masterList);
        } else {
            String lower = keyword.toLowerCase().trim();
            ObservableList<Category> filtered = FXCollections.observableArrayList();
            for (Category c : masterList) {
                if (c.getName().toLowerCase().contains(lower) ||
                    (c.getDescription() != null && c.getDescription().toLowerCase().contains(lower)) ||
                    String.valueOf(c.getId()).contains(lower)) {
                    filtered.add(c);
                }
            }
            categoryTable.setItems(filtered);
        }
    }

    @FXML
    public void handleOpenAddCategory() {
        showCategoryModal(null);
    }

    public void showCategoryModal(Category toEdit) {
        this.currentEditingCat = toEdit;
        lblCategoryModalError.setText("");

        if (toEdit != null) {
            lblCategoryModalTitle.setText("Chỉnh Sửa Thể Loại");
            txtModalCatName.setText(toEdit.getName());
            txtModalCatDesc.setText(toEdit.getDescription() != null ? toEdit.getDescription() : "");
        } else {
            lblCategoryModalTitle.setText("Thêm Thể Loại Mới");
            txtModalCatName.setText("");
            txtModalCatDesc.setText("");
        }

        categoryMainContainer.setEffect(new GaussianBlur(14));
        categoryModalOverlay.setVisible(true);
        categoryModalOverlay.setManaged(true);
    }

    @FXML
    public void closeCategoryModal() {
        categoryMainContainer.setEffect(null);
        categoryModalOverlay.setVisible(false);
        categoryModalOverlay.setManaged(false);
    }

    @FXML
    public void handleSaveCategoryModal() {
        String name = txtModalCatName.getText() != null ? txtModalCatName.getText().trim() : "";
        String desc = txtModalCatDesc.getText() != null ? txtModalCatDesc.getText().trim() : "";

        if (name.isEmpty()) {
            lblCategoryModalError.setText("Vui lòng nhập tên thể loại!");
            lblCategoryModalError.setStyle("-fx-text-fill: #EF4444;");
            return;
        }

        Category cat = new Category(
                currentEditingCat != null ? currentEditingCat.getId() : 0,
                name,
                desc
        );

        boolean ok = categoryService.saveCategory(cat);
        if (ok) {
            closeCategoryModal();
            loadCategories();
        } else {
            lblCategoryModalError.setText("Lỗi: Không thể lưu thể loại (Tên thể loại có thể đã tồn tại)!");
            lblCategoryModalError.setStyle("-fx-text-fill: #EF4444;");
        }
    }

    private void handleDeleteCategory(Category cat) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác Nhận Xóa Thể Loại");
        alert.setHeaderText("Bạn có chắc chắn muốn xóa thể loại: " + cat.getName() + "?");
        alert.setContentText("Hành động này không thể hoàn tác.");

        Optional<ButtonType> res = alert.showAndWait();
        if (res.isPresent() && res.get() == ButtonType.OK) {
            categoryService.deleteCategory(cat.getId());
            loadCategories();
        }
    }
}
