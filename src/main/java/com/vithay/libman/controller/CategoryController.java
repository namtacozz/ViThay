package com.vithay.libman.controller;

import com.vithay.libman.model.Category;
import com.vithay.libman.service.CategoryService;
import com.vithay.libman.util.UIUtils;
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
import javafx.scene.shape.SVGPath;

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
        if (categoryModalBox != null && !categoryModalBox.getChildren().isEmpty()) {
            UIUtils.makeDraggable(categoryModalBox.getChildren().get(0), categoryModalBox);
        }
        colCatId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colCatName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colCatDesc.setCellValueFactory(new PropertyValueFactory<>("description"));

        colCatActions.setCellFactory(param -> new TableCell<>() {
            private final Button btnEdit = new Button();
            private final Button btnDelete = new Button();
            private final HBox pane = new HBox(8, btnEdit, btnDelete);

            {
                pane.setAlignment(Pos.CENTER);

                // Edit Button (SVG Pencil)
                btnEdit.getStyleClass().addAll("btn-secondary", "btn-icon-action");
                btnEdit.setTooltip(new Tooltip("Chỉnh sửa thể loại"));
                btnEdit.setMinWidth(28);
                btnEdit.setPrefWidth(28);
                btnEdit.setMinHeight(28);
                btnEdit.setPrefHeight(28);
                SVGPath editSvg = new SVGPath();
                editSvg.setContent("M 3 17.25 V 21 h 3.75 L 17.81 9.94 l -3.75 -3.75 L 3 17.25 Z M 20.71 7.04 c 0.39 -0.39 0.39 -1.02 0 -1.41 l -2.34 -2.34 c -0.39 -0.39 -1.02 -0.39 -1.41 0 l -1.83 1.83 3.75 3.75 1.83 -1.83 Z");
                editSvg.setStyle("-fx-fill: #1DB954;");
                editSvg.setScaleX(0.7);
                editSvg.setScaleY(0.7);
                btnEdit.setGraphic(editSvg);
                btnEdit.setOnAction(e -> {
                    Category cat = getTableView().getItems().get(getIndex());
                    showCategoryModal(cat);
                });

                // Delete Button (SVG Trash)
                btnDelete.getStyleClass().addAll("btn-danger", "btn-icon-action");
                btnDelete.setTooltip(new Tooltip("Xóa thể loại sách"));
                btnDelete.setMinWidth(28);
                btnDelete.setPrefWidth(28);
                btnDelete.setMinHeight(28);
                btnDelete.setPrefHeight(28);
                SVGPath deleteSvg = new SVGPath();
                deleteSvg.setContent("M 6 19 c 0 1.1 0.9 2 2 2 h 8 c 1.1 0 2 -0.9 2 -2 V 7 H 6 v 12 Z M 19 4 h -3.5 l -1 -1 h -5 l -1 1 H 5 v 2 h 14 V 4 Z");
                deleteSvg.setStyle("-fx-fill: #EF4444;");
                deleteSvg.setScaleX(0.7);
                deleteSvg.setScaleY(0.7);
                btnDelete.setGraphic(deleteSvg);
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
        if (categoryModalBox != null) {
            UIUtils.resetPosition(categoryModalBox);
        }
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
        MainLayoutController.showAppNotice(
                Alert.AlertType.CONFIRMATION,
                "Xác Nhận Xóa Thể Loại",
                "Bạn có chắc chắn muốn xóa thể loại: " + cat.getName() + "?\nHành động này không thể hoàn tác.",
                () -> {
                    categoryService.deleteCategory(cat.getId());
                    loadCategories();
                }
        );
    }
}
