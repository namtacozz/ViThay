package com.vithay.libman.controller;

import com.vithay.libman.service.SettingService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class SettingsController implements Initializable {

    @FXML private Spinner<Integer> spnMaxBooks;
    @FXML private Spinner<Integer> spnMaxDaysHome;
    @FXML private Spinner<Integer> spnMaxDaysOnsite;
    @FXML private Spinner<Integer> spnCardValidity;

    @FXML private TextField txtFinePerDay;
    @FXML private TextField txtLostMultiplier;
    @FXML private TextField txtProcessingFee;

    @FXML private RadioButton radioDarkTheme;
    @FXML private RadioButton radioPinkLightTheme;

    @FXML private Label lblSettingsMsg;

    private final SettingService settingService = SettingService.getInstance();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        spnMaxBooks.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, 5));
        spnMaxDaysHome.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 90, 14));
        spnMaxDaysOnsite.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 7, 1));
        spnCardValidity.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 60, 12));

        loadSettings();
    }

    public void loadSettings() {
        spnMaxBooks.getValueFactory().setValue(settingService.getMaxBooksPerReader());
        spnMaxDaysHome.getValueFactory().setValue(settingService.getMaxBorrowDaysHome());
        spnMaxDaysOnsite.getValueFactory().setValue(settingService.getMaxBorrowDaysOnsite());
        spnCardValidity.getValueFactory().setValue(settingService.getCardValidityMonths());

        txtFinePerDay.setText(String.valueOf((long) settingService.getFinePerDay()));
        txtLostMultiplier.setText(String.valueOf(settingService.getLostBookMultiplier()));
        txtProcessingFee.setText(String.valueOf((long) settingService.getProcessingFee()));

        com.vithay.libman.service.ThemeManager tm = com.vithay.libman.service.ThemeManager.getInstance();
        if (tm.getCurrentTheme() == com.vithay.libman.service.ThemeManager.Theme.PINK_LIGHT) {
            radioPinkLightTheme.setSelected(true);
        } else {
            radioDarkTheme.setSelected(true);
        }

        lblSettingsMsg.setText("");
    }

    @FXML
    public void handleThemeRadioChange() {
        com.vithay.libman.service.ThemeManager tm = com.vithay.libman.service.ThemeManager.getInstance();
        if (radioPinkLightTheme.isSelected()) {
            tm.setTheme(com.vithay.libman.service.ThemeManager.Theme.PINK_LIGHT);
            lblSettingsMsg.setText("✓ Đã áp dụng Chế độ Giao diện Sáng (Modern Pink)!");
            lblSettingsMsg.setStyle("-fx-text-fill: #FB7185;");
        } else {
            tm.setTheme(com.vithay.libman.service.ThemeManager.Theme.DARK);
            lblSettingsMsg.setText("✓ Đã áp dụng Chế độ Giao diện Tối!");
            lblSettingsMsg.setStyle("-fx-text-fill: #1DB954;");
        }
    }

    @FXML
    public void handleSaveSettings() {
        try {
            int maxBooks = spnMaxBooks.getValue();
            int maxDaysHome = spnMaxDaysHome.getValue();
            int maxDaysOnsite = spnMaxDaysOnsite.getValue();
            int cardValidity = spnCardValidity.getValue();

            double finePerDay = Double.parseDouble(txtFinePerDay.getText().trim());
            double lostMultiplier = Double.parseDouble(txtLostMultiplier.getText().trim());
            double processingFee = Double.parseDouble(txtProcessingFee.getText().trim());

            settingService.updateSetting("max_books_per_reader", String.valueOf(maxBooks));
            settingService.updateSetting("max_borrow_days_home", String.valueOf(maxDaysHome));
            settingService.updateSetting("max_borrow_days_onsite", String.valueOf(maxDaysOnsite));
            settingService.updateSetting("card_validity_months", String.valueOf(cardValidity));

            settingService.updateSetting("fine_per_day", String.valueOf(finePerDay));
            settingService.updateSetting("lost_book_multiplier", String.valueOf(lostMultiplier));
            settingService.updateSetting("processing_fee", String.valueOf(processingFee));

            lblSettingsMsg.setText("✓ Đã cập nhật và áp dụng quy định thư viện thành công!");
            lblSettingsMsg.setStyle("-fx-text-fill: #1DB954;");
        } catch (Exception e) {
            lblSettingsMsg.setText("Lỗi: Vui lòng kiểm tra lại định dạng số liệu nhập vào!");
            lblSettingsMsg.setStyle("-fx-text-fill: #EF4444;");
        }
    }

    @FXML
    public void handleResetDefaults() {
        spnMaxBooks.getValueFactory().setValue(5);
        spnMaxDaysHome.getValueFactory().setValue(14);
        spnMaxDaysOnsite.getValueFactory().setValue(1);
        spnCardValidity.getValueFactory().setValue(12);

        txtFinePerDay.setText("2000");
        txtLostMultiplier.setText("2.0");
        txtProcessingFee.setText("20000");

        handleSaveSettings();
        lblSettingsMsg.setText("✓ Đã khôi phục cài đặt mặc định ban đầu!");
        lblSettingsMsg.setStyle("-fx-text-fill: #1DB954;");
    }
}
