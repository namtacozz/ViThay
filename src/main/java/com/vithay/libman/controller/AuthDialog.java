package com.vithay.libman.controller;

import com.vithay.libman.model.User;
import com.vithay.libman.service.AuthService;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.util.Optional;

public class AuthDialog {

    public static boolean showAuthDialog(MainLayoutController mainController) {
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle("Hệ Thống Xác Thực - LibMan");
        dialog.setHeaderText("Đăng nhập hoặc Đăng ký tài khoản hệ thống:");

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getStylesheets().add(AuthDialog.class.getResource("/com/vithay/libman/css/style.css").toExternalForm());
        dialogPane.getStyleClass().add("bg-surface");
        dialogPane.getButtonTypes().addAll(ButtonType.CLOSE);

        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.getStyleClass().add("bg-surface");

        // TAB 1: ĐĂNG NHẬP
        Tab tabLogin = new Tab("Đăng Nhập");
        VBox loginBox = new VBox(16);
        loginBox.setPadding(new Insets(20));

        GridPane loginGrid = new GridPane();
        loginGrid.setHgap(12);
        loginGrid.setVgap(12);

        TextField txtLoginUser = new TextField();
        txtLoginUser.setPromptText("Tên đăng nhập (VD: giamdoc, minhtri, docgia)");
        txtLoginUser.getStyleClass().add("form-control");

        PasswordField txtLoginPass = new PasswordField();
        txtLoginPass.setPromptText("Mật khẩu");
        txtLoginPass.getStyleClass().add("form-control");

        loginGrid.add(new Label("Tên Tài Khoản:"), 0, 0);
        loginGrid.add(txtLoginUser, 1, 0);
        loginGrid.add(new Label("Mật Khẩu:"), 0, 1);
        loginGrid.add(txtLoginPass, 1, 1);

        Label lblLoginMsg = new Label();
        lblLoginMsg.setStyle("-fx-font-weight: bold;");

        Button btnDoLogin = new Button("Đăng Nhập");
        btnDoLogin.getStyleClass().add("btn-primary");

        // Quick login sample accounts helper
        HBox quickAccounts = new HBox(8);
        quickAccounts.setAlignment(Pos.CENTER_LEFT);
        Label lblQuick = new Label("Tài khoản mẫu:");
        lblQuick.setStyle("-fx-text-fill: #727272; -fx-font-size: 11px;");

        Button btnSampleAdmin = new Button("Giám đốc");
        btnSampleAdmin.getStyleClass().add("btn-secondary");
        btnSampleAdmin.setStyle("-fx-font-size: 11px; -fx-padding: 2 6;");
        btnSampleAdmin.setOnAction(e -> {
            txtLoginUser.setText("giamdoc");
            txtLoginPass.setText("admin123");
        });

        Button btnSampleLib = new Button("Thủ thư");
        btnSampleLib.getStyleClass().add("btn-secondary");
        btnSampleLib.setStyle("-fx-font-size: 11px; -fx-padding: 2 6;");
        btnSampleLib.setOnAction(e -> {
            txtLoginUser.setText("minhtri");
            txtLoginPass.setText("123456");
        });

        Button btnSampleReader = new Button("Độc giả");
        btnSampleReader.getStyleClass().add("btn-secondary");
        btnSampleReader.setStyle("-fx-font-size: 11px; -fx-padding: 2 6;");
        btnSampleReader.setOnAction(e -> {
            txtLoginUser.setText("docgia");
            txtLoginPass.setText("123456");
        });

        quickAccounts.getChildren().addAll(lblQuick, btnSampleAdmin, btnSampleLib, btnSampleReader);

        btnDoLogin.setOnAction(e -> {
            String u = txtLoginUser.getText();
            String p = txtLoginPass.getText();
            if (u == null || u.trim().isEmpty() || p == null || p.trim().isEmpty()) {
                lblLoginMsg.setText("Vui lòng nhập đầy đủ tài khoản và mật khẩu!");
                lblLoginMsg.setStyle("-fx-text-fill: #EF4444;");
                return;
            }
            boolean ok = AuthService.getInstance().login(u.trim(), p.trim());
            if (ok) {
                lblLoginMsg.setText("Đăng nhập thành công!");
                lblLoginMsg.setStyle("-fx-text-fill: #1DB954;");
                if (mainController != null) {
                    mainController.updateUserSessionUI();
                }
                dialog.close();
            } else {
                lblLoginMsg.setText("Sai tài khoản hoặc mật khẩu!");
                lblLoginMsg.setStyle("-fx-text-fill: #EF4444;");
            }
        });

        loginBox.getChildren().addAll(loginGrid, quickAccounts, lblLoginMsg, btnDoLogin);
        tabLogin.setContent(loginBox);

        // TAB 2: ĐĂNG KÝ (SRS Mục 5.2)
        Tab tabRegister = new Tab("Đăng Ký Tài Khoản");
        VBox regBox = new VBox(16);
        regBox.setPadding(new Insets(20));

        GridPane regGrid = new GridPane();
        regGrid.setHgap(12);
        regGrid.setVgap(12);

        TextField txtRegUser = new TextField();
        txtRegUser.setPromptText("Tên đăng nhập mới");
        txtRegUser.getStyleClass().add("form-control");

        PasswordField txtRegPass = new PasswordField();
        txtRegPass.setPromptText("Mật khẩu");
        txtRegPass.getStyleClass().add("form-control");

        TextField txtRegFullName = new TextField();
        txtRegFullName.setPromptText("Họ và tên đầy đủ");
        txtRegFullName.getStyleClass().add("form-control");

        ComboBox<String> comboRole = new ComboBox<>(FXCollections.observableArrayList("Độc giả", "Thủ thư", "Giám đốc"));
        comboRole.setValue("Độc giả");
        comboRole.getStyleClass().add("form-control");

        TextField txtRegEmail = new TextField();
        txtRegEmail.setPromptText("Địa chỉ email");
        txtRegEmail.getStyleClass().add("form-control");

        TextField txtRegPhone = new TextField();
        txtRegPhone.setPromptText("Số điện thoại");
        txtRegPhone.getStyleClass().add("form-control");

        DatePicker dpIssue = new DatePicker(LocalDate.now());
        DatePicker dpExpiry = new DatePicker(LocalDate.now().plusYears(1));

        regGrid.add(new Label("Tên Tài Khoản:"), 0, 0); regGrid.add(txtRegUser, 1, 0);
        regGrid.add(new Label("Mật Khẩu:"), 0, 1); regGrid.add(txtRegPass, 1, 1);
        regGrid.add(new Label("Họ Và Tên:"), 0, 2); regGrid.add(txtRegFullName, 1, 2);
        regGrid.add(new Label("Vai Trò Phân Quyền:"), 0, 3); regGrid.add(comboRole, 1, 3);
        regGrid.add(new Label("Email:"), 0, 4); regGrid.add(txtRegEmail, 1, 4);
        regGrid.add(new Label("Số Điện Thoại:"), 0, 5); regGrid.add(txtRegPhone, 1, 5);
        regGrid.add(new Label("Ngày Cấp Thẻ:"), 0, 6); regGrid.add(dpIssue, 1, 6);
        regGrid.add(new Label("Ngày Hết Hạn Thẻ:"), 0, 7); regGrid.add(dpExpiry, 1, 7);

        Label lblRegMsg = new Label();
        lblRegMsg.setStyle("-fx-font-weight: bold;");

        Button btnDoRegister = new Button("Đăng Ký Thành Viên Mới");
        btnDoRegister.getStyleClass().add("btn-primary");

        btnDoRegister.setOnAction(e -> {
            String u = txtRegUser.getText();
            String p = txtRegPass.getText();
            String name = txtRegFullName.getText();
            if (u == null || u.trim().isEmpty() || p == null || p.trim().isEmpty() || name == null || name.trim().isEmpty()) {
                lblRegMsg.setText("Vui lòng điền các trường bắt buộc (Tài khoản, Mật khẩu, Họ tên)!");
                lblRegMsg.setStyle("-fx-text-fill: #EF4444;");
                return;
            }

            User nu = new User(
                    0,
                    u.trim(),
                    p.trim(),
                    name.trim(),
                    comboRole.getValue(),
                    txtRegEmail.getText() != null ? txtRegEmail.getText().trim() : "",
                    txtRegPhone.getText() != null ? txtRegPhone.getText().trim() : "",
                    "/com/vithay/libman/images/avatar.png",
                    dpIssue.getValue().toString(),
                    dpExpiry.getValue().toString()
            );

            boolean ok = AuthService.getInstance().register(nu);
            if (ok) {
                lblRegMsg.setText("Đăng ký thành công! Đã tự động đăng nhập.");
                lblRegMsg.setStyle("-fx-text-fill: #1DB954;");
                if (mainController != null) {
                    mainController.updateUserSessionUI();
                }
                dialog.close();
            } else {
                lblRegMsg.setText("Tên đăng nhập đã tồn tại trong hệ thống!");
                lblRegMsg.setStyle("-fx-text-fill: #EF4444;");
            }
        });

        regBox.getChildren().addAll(regGrid, lblRegMsg, btnDoRegister);
        tabRegister.setContent(regBox);

        tabPane.getTabs().addAll(tabLogin, tabRegister);
        dialogPane.setContent(tabPane);

        Optional<User> res = dialog.showAndWait();
        return AuthService.getInstance().getCurrentUser() != null;
    }
}
