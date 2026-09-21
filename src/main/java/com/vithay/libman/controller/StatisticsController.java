package com.vithay.libman.controller;

import com.vithay.libman.model.Book;
import com.vithay.libman.service.BookService;
import com.vithay.libman.service.DashboardService;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class StatisticsController implements Initializable {

    @FXML private Label lblStatTotalBooks;
    @FXML private Label lblStatTotalReaders;
    @FXML private Label lblStatActiveBorrows;
    @FXML private Label lblStatOverdueBorrows;

    @FXML private TableView<CategoryStat> categoryTable;
    @FXML private TableColumn<CategoryStat, String> colCategoryName;
    @FXML private TableColumn<CategoryStat, Number> colCategoryCount;
    @FXML private TableColumn<CategoryStat, String> colCategoryPercent;

    private final DashboardService dashboardService = new DashboardService();
    private final BookService bookService = new BookService();

    public static class CategoryStat {
        private final SimpleStringProperty category;
        private final SimpleIntegerProperty count;
        private final SimpleStringProperty percent;

        public CategoryStat(String category, int count, String percent) {
            this.category = new SimpleStringProperty(category);
            this.count = new SimpleIntegerProperty(count);
            this.percent = new SimpleStringProperty(percent);
        }

        public String getCategory() { return category.get(); }
        public int getCount() { return count.get(); }
        public String getPercent() { return percent.get(); }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colCategoryName.setCellValueFactory(data -> data.getValue().category);
        colCategoryCount.setCellValueFactory(data -> data.getValue().count);
        colCategoryPercent.setCellValueFactory(data -> data.getValue().percent);

        loadStats();
    }

    public void loadStats() {
        Map<String, Object> stats = dashboardService.getDashboardStats();
        lblStatTotalBooks.setText(String.valueOf(stats.getOrDefault("totalBooks", 0)));
        lblStatTotalReaders.setText(String.valueOf(stats.getOrDefault("totalReaders", 0)));
        lblStatActiveBorrows.setText(String.valueOf(stats.getOrDefault("activeTransactions", 0)));
        lblStatOverdueBorrows.setText(String.valueOf(stats.getOrDefault("overdueCount", 0)));

        // Calculate Category Breakdown
        List<Book> books = bookService.getAllBooks();
        Map<String, Integer> catCounts = new HashMap<>();
        for (Book b : books) {
            catCounts.put(b.getCategory(), catCounts.getOrDefault(b.getCategory(), 0) + 1);
        }

        int total = books.size();
        ObservableList<CategoryStat> statList = FXCollections.observableArrayList();
        for (Map.Entry<String, Integer> entry : catCounts.entrySet()) {
            double pct = total > 0 ? ((double) entry.getValue() / total) * 100.0 : 0.0;
            statList.add(new CategoryStat(entry.getKey(), entry.getValue(), String.format("%.1f%%", pct)));
        }

        categoryTable.setItems(statList);
    }
}
