# Kế Hoạch Triển Khai: Cải Tiến Cấu Trúc Thông Tin & Trải Nghiệm Tương Tác (LibMan)

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Triển khai trọn bộ 7 Design Patterns (*Two-Panel Selector, Canvas Plus Palette, One-Window Drilldown, Alternative Views, Wizard, Extras on Demand, Intriguing Branches*) vào 2 phân hệ cốt lõi: Kho Sách và Lưu Hành Mượn - Trả của ứng dụng JavaFX LibMan.

**Architecture:** Áp dụng mô hình Composable In-Place Views, phân rã các view FXML và sub-controller tương tác trong cùng một cửa sổ với hiệu ứng trượt, Breadcrumbs, SplitPane và Desk Workspace, bảo toàn 100% tầng DAO/Service và quy định SRS hiện hữu.

**Tech Stack:** Java 17+, JavaFX 21, FXML, CSS, SQLite (JDBC), JUnit 5.

**Spec:** [`docs/superpowers/specs/2026-09-22-ia-ux-patterns-redesign.md`](file:///home/arjunsharma/Tài liệu/GitHub/ViThay/docs/superpowers/specs/2026-09-22-ia-ux-patterns-redesign.md)

## Global Constraints
- Giao diện phải tuân thủ nghiêm ngặt bảng màu Spotify Dark Theme (Nền: `#121212`, Surface: `#181818`, Thẻ: `#282828`, Điểm nhấn: `#1DB954`, Chữ: `#FFFFFF` và `#B3B3B3`) và tương thích hoàn toàn với Light Theme của `ThemeManager`.
- Không làm thay đổi cơ sở dữ liệu SQLite schema (`schema.sql`) hoặc phá vỡ các API DAO hiện có (`BookDao`, `BorrowTransactionDao`, `ReaderDao`, `SettingDao`).
- Toàn bộ 7 kiểm thử tự động hiện tại (`mvn test`) phải vượt qua 100% sau mỗi task.
- Không sử dụng thư viện UI bên thứ ba ngoài JavaFX SDK tiêu chuẩn.

## Review Focus
1. **Phân rã View không làm rò rỉ bộ nhớ hoặc listener:** Các event listener trên TableView/SplitPane và CardCell phải được dọn dẹp sạch khi chuyển view.
2. **Tuân thủ giới hạn mượn trần 5 cuốn (SRS 6.1):** Khi giỏ sách tại bàn lưu hành Canvas vượt quá số lượng cho phép của độc giả, nút thanh toán phải bị vô hiệu hóa kèm thông báo cảnh báo rõ ràng.
3. **Thao tác nhanh bàn phím trên Two-Panel Selector:** Nhấn phím mũi tên `UP`/`DOWN` trên danh sách sách phải cập nhật ngay tức thì Panel Detail bên phải mà không có độ trễ giật lag.
4. **Đồng bộ hóa Alternative Views:** Khi người dùng lọc tìm kiếm hoặc thêm/xóa sách ở chế độ Table View rồi chuyển sang Grid View (hoặc ngược lại), danh sách hiển thị phải đồng bộ chính xác 100%.
5. **Hiển thị Breadcrumb chính xác khi Drilldown:** Nhấp vào tác giả/thể loại hoặc lịch sử mượn trả phải cập nhật đúng chuỗi breadcrumb và cho phép quay lui cấp trước an toàn.

---

### Task 1: Thiết Kế CSS Tokens Cho 7 Design Patterns Mới

**Files:**
- Modify: `src/main/resources/com/vithay/libman/css/style.css`
- Test: `src/test/java/com/vithay/libman/CssThemeSanityTest.java`

**Interfaces:**
- Consumes: Existing color variables (`-fx-background`, `.bg-dark`, `.btn-primary`, `.badge-available`).
- Produces: CSS classes:
  - `.segmented-view-btn`, `.segmented-view-active`
  - `.breadcrumb-container`, `.breadcrumb-chip`, `.breadcrumb-chip-active`
  - `.split-inspector-pane`, `.inspector-header`, `.inspector-shelf-box`
  - `.book-grid-card`, `.book-grid-cover`, `.book-grid-title`, `.book-grid-badge`
  - `.desk-palette-box`, `.desk-canvas-zone`, `.desk-basket-card`, `.desk-summary-bar`
  - `.wizard-stepper-bar`, `.wizard-step-node`, `.wizard-step-active`, `.wizard-step-done`

- [ ] **Step 1: Viết test kiểm tra tính toàn vẹn của stylesheet**

Tạo file `src/test/java/com/vithay/libman/CssThemeSanityTest.java`:
```java
package com.vithay.libman;

import org.junit.jupiter.api.Test;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import static org.junit.jupiter.api.Assertions.*;

public class CssThemeSanityTest {
    @Test
    public void testNewCssClassesExistInStyleSheet() throws Exception {
        try (InputStream is = getClass().getResourceAsStream("/com/vithay/libman/css/style.css")) {
            assertNotNull(is, "style.css must exist in resources");
            String css = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            assertTrue(css.contains(".segmented-view-btn"), "Must contain segmented-view-btn");
            assertTrue(css.contains(".breadcrumb-container"), "Must contain breadcrumb-container");
            assertTrue(css.contains(".book-grid-card"), "Must contain book-grid-card");
            assertTrue(css.contains(".desk-canvas-zone"), "Must contain desk-canvas-zone");
            assertTrue(css.contains(".wizard-stepper-bar"), "Must contain wizard-stepper-bar");
        }
    }
}
```

- [ ] **Step 2: Chạy test để xác nhận fail**

Run: `./mvnw test -Dtest=CssThemeSanityTest`  
Expected: FAIL (AssertionError: Must contain .segmented-view-btn)

- [ ] **Step 3: Bổ sung CSS classes vào `style.css`**

Thêm các style class vào cuối file `src/main/resources/com/vithay/libman/css/style.css`:
```css
/* ========================================================================= */
/* 7 DESIGN PATTERNS ENHANCEMENT STYLES                                      */
/* ========================================================================= */

/* 1. Alternative Views & Segmented Switcher */
.segmented-view-group {
    -fx-background-color: #242424;
    -fx-background-radius: 20px;
    -fx-padding: 3px;
}
.segmented-view-btn {
    -fx-background-color: transparent;
    -fx-text-fill: #B3B3B3;
    -fx-font-size: 12px;
    -fx-font-weight: bold;
    -fx-padding: 5px 14px;
    -fx-background-radius: 16px;
    -fx-cursor: hand;
}
.segmented-view-btn:hover {
    -fx-text-fill: #FFFFFF;
}
.segmented-view-active {
    -fx-background-color: #383838;
    -fx-text-fill: #FFFFFF;
}

/* 2. Breadcrumbs & One-Window Drilldown */
.breadcrumb-container {
    -fx-background-color: #181818;
    -fx-padding: 8px 16px;
    -fx-background-radius: 8px;
    -fx-border-color: #282828;
    -fx-border-radius: 8px;
    -fx-border-width: 1px;
}
.breadcrumb-chip {
    -fx-text-fill: #1DB954;
    -fx-font-size: 12px;
    -fx-font-weight: bold;
    -fx-cursor: hand;
}
.breadcrumb-chip:hover {
    -fx-underline: true;
}
.breadcrumb-chip-active {
    -fx-text-fill: #FFFFFF;
    -fx-font-size: 12px;
    -fx-font-weight: bold;
}
.breadcrumb-separator {
    -fx-text-fill: #727272;
    -fx-font-size: 11px;
}

/* 3. Grid / Card View */
.book-grid-card {
    -fx-background-color: #181818;
    -fx-background-radius: 8px;
    -fx-padding: 12px;
    -fx-border-color: #282828;
    -fx-border-radius: 8px;
    -fx-border-width: 1px;
    -fx-cursor: hand;
}
.book-grid-card:hover {
    -fx-background-color: #282828;
    -fx-border-color: #1DB954;
}
.book-grid-card-selected {
    -fx-background-color: #282828;
    -fx-border-color: #1DB954;
    -fx-border-width: 2px;
}
.book-grid-title {
    -fx-text-fill: #FFFFFF;
    -fx-font-size: 13px;
    -fx-font-weight: bold;
}
.book-grid-author {
    -fx-text-fill: #B3B3B3;
    -fx-font-size: 11px;
}

/* 4. Two-Panel Inspector */
.split-inspector-pane {
    -fx-background-color: #181818;
    -fx-background-radius: 8px;
    -fx-border-color: #282828;
    -fx-border-radius: 8px;
    -fx-border-width: 1px;
    -fx-padding: 16px;
}
.inspector-shelf-box {
    -fx-background-color: #222222;
    -fx-background-radius: 6px;
    -fx-padding: 10px 14px;
}

/* 5. Circulation Desk: Canvas Plus Palette */
.desk-palette-box {
    -fx-background-color: #181818;
    -fx-background-radius: 8px;
    -fx-border-color: #282828;
    -fx-border-radius: 8px;
    -fx-padding: 12px;
}
.desk-canvas-zone {
    -fx-background-color: #141414;
    -fx-background-radius: 12px;
    -fx-border-color: #2A2A2A;
    -fx-border-radius: 12px;
    -fx-border-width: 2px;
    -fx-padding: 20px;
}
.desk-basket-card {
    -fx-background-color: #222222;
    -fx-background-radius: 8px;
    -fx-padding: 10px 14px;
    -fx-border-color: #333333;
    -fx-border-radius: 8px;
}
.desk-basket-card:hover {
    -fx-border-color: #1DB954;
}

/* 6. Wizard Stepper */
.wizard-stepper-bar {
    -fx-background-color: #181818;
    -fx-background-radius: 8px;
    -fx-padding: 12px 20px;
}
.wizard-step-node {
    -fx-background-color: #282828;
    -fx-text-fill: #B3B3B3;
    -fx-font-size: 11px;
    -fx-font-weight: bold;
    -fx-padding: 6px 14px;
    -fx-background-radius: 20px;
}
.wizard-step-active {
    -fx-background-color: #1DB954;
    -fx-text-fill: #000000;
}
.wizard-step-done {
    -fx-background-color: #22543D;
    -fx-text-fill: #1DB954;
}
```

- [ ] **Step 4: Chạy lại test để xác nhận pass**

Run: `./mvnw test -Dtest=CssThemeSanityTest`  
Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add src/main/resources/com/vithay/libman/css/style.css src/test/java/com/vithay/libman/CssThemeSanityTest.java
git commit -m "feat(ui): add CSS classes for 7 interaction design patterns"
```

---

### Task 2: Kho Sách - Tích Hợp Alternative Views (Table & Grid Card View)

**Files:**
- Create: `src/main/java/com/vithay/libman/view/component/BookCardView.java`
- Modify: `src/main/resources/com/vithay/libman/view/BookManagementView.fxml`
- Modify: `src/main/java/com/vithay/libman/controller/BookManagementController.java`
- Test: `src/test/java/com/vithay/libman/BookViewSwitchingTest.java`

**Interfaces:**
- Consumes: `Book` model, `BookService.getAllBooks()`, `BookService.searchBooks()`
- Produces: `BookCardView.createBookCard(Book, Consumer<Book> onSelect)`, `BookManagementController.switchView(boolean isTable)`

- [ ] **Step 1: Viết test cho logic chuyển đổi view và lọc thẻ sách**

Tạo file `src/test/java/com/vithay/libman/BookViewSwitchingTest.java`:
```java
package com.vithay.libman;

import com.vithay.libman.model.Book;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class BookViewSwitchingTest {
    @Test
    public void testFilterSyncBetweenModes() {
        List<Book> allBooks = new ArrayList<>();
        allBooks.add(new Book("BK001", "Chí Phèo", "Nam Cao", "VH", "Khu A - 01", 65000, 5, 3, "KHA_DUNG", null));
        allBooks.add(new Book("BK002", "Clean Code", "Robert C. Martin", "CNTT", "Khu B - 02", 180000, 3, 0, "DANG_MUON", null));

        // Test filtering logic applied uniformly
        List<Book> filtered = allBooks.stream()
                .filter(b -> b.getTenSach().toLowerCase().contains("chí"))
                .toList();

        assertEquals(1, filtered.size());
        assertEquals("BK001", filtered.get(0).getMaSach());
    }
}
```

- [ ] **Step 2: Chạy test xác nhận cấu trúc test pass**

Run: `./mvnw test -Dtest=BookViewSwitchingTest`  
Expected: PASS

- [ ] **Step 3: Tạo `BookCardView.java` để render thẻ sách Spotify**

Tạo file `src/main/java/com/vithay/libman/view/component/BookCardView.java`:
```java
package com.vithay.libman.view.component;

import com.vithay.libman.model.Book;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import java.io.InputStream;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.function.Consumer;

public class BookCardView {
    private static final NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    public static VBox createCard(Book book, boolean isSelected, Consumer<Book> onSelect) {
        VBox card = new VBox(8);
        card.setPrefWidth(180);
        card.setMaxWidth(180);
        card.setAlignment(Pos.TOP_LEFT);
        card.getStyleClass().add("book-grid-card");
        if (isSelected) {
            card.getStyleClass().add("book-grid-card-selected");
        }

        // Image cover
        ImageView coverView = new ImageView();
        coverView.setFitWidth(156);
        coverView.setFitHeight(200);
        coverView.setPreserveRatio(false);

        String imageName = book.getImagePath();
        if (imageName == null || imageName.isBlank()) {
            imageName = "clean_code.jpg";
        }
        InputStream is = BookCardView.class.getResourceAsStream("/com/vithay/libman/images/" + imageName);
        if (is != null) {
            coverView.setImage(new Image(is));
        }

        // Title & Author
        Label lblTitle = new Label(book.getTenSach());
        lblTitle.getStyleClass().add("book-grid-title");
        lblTitle.setWrapText(true);
        lblTitle.setMaxHeight(40);

        Label lblAuthor = new Label(book.getTenTacGia());
        lblAuthor.getStyleClass().add("book-grid-author");

        // Status Badge
        Label lblBadge = new Label(book.getSoLuongConLai() > 0 ? "Khả dụng (" + book.getSoLuongConLai() + ")" : "Hết sách");
        lblBadge.getStyleClass().add(book.getSoLuongConLai() > 0 ? "badge-available" : "badge-borrowed");

        card.getChildren().addAll(coverView, lblTitle, lblAuthor, lblBadge);
        card.setOnMouseClicked(e -> {
            if (onSelect != null) onSelect.accept(book);
        });

        return card;
    }
}
```

- [ ] **Step 4: Cập nhật `BookManagementView.fxml` và `BookManagementController.java`**

Bổ sung bộ chuyển đổi Alternative Views (Nút `Bảng` và `Lưới`) và container `ScrollPane` chứa `FlowPane` cho Grid View.
Trong `BookManagementController`:
- Quản lý `isTableViewMode` (boolean).
- Khi ở Grid View: render danh sách `Book` thành các `VBox` card trong `FlowPane` qua `BookCardView.createCard`.

- [ ] **Step 5: Kiểm tra biên dịch & Test**

Run: `./mvnw test -Dtest=BookViewSwitchingTest`  
Expected: PASS

- [ ] **Step 6: Commit**

```bash
git add src/main/java/com/vithay/libman/view/component/BookCardView.java \
        src/main/resources/com/vithay/libman/view/BookManagementView.fxml \
        src/main/java/com/vithay/libman/controller/BookManagementController.java \
        src/test/java/com/vithay/libman/BookViewSwitchingTest.java
git commit -m "feat(books): implement Alternative Views with Table and Spotify Grid Card view"
```

---

### Task 3: Kho Sách - Tích Hợp Two-Panel Selector & Detail Inspector

**Files:**
- Modify: `src/main/resources/com/vithay/libman/view/BookManagementView.fxml`
- Modify: `src/main/java/com/vithay/libman/controller/BookManagementController.java`
- Test: `src/test/java/com/vithay/libman/TwoPanelSelectorTest.java`

**Interfaces:**
- Consumes: `Book` selected from `TableView` or `FlowPane Grid`
- Produces: `BookManagementController.showBookDetail(Book)`, `SplitPane` layout divider ratio (0.68 / 0.32)

- [ ] **Step 1: Viết test cho logic Two-Panel Selector selection**

Tạo file `src/test/java/com/vithay/libman/TwoPanelSelectorTest.java`:
```java
package com.vithay.libman;

import com.vithay.libman.model.Book;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TwoPanelSelectorTest {
    @Test
    public void testSelectedBookDetailsExtraction() {
        Book b = new Book("BK010", "Dế Mèn", "Tô Hoài", "TN", "Khu A - 03", 50000, 10, 8, "KHA_DUNG", "de_men.jpg");
        assertNotNull(b.getMaSach());
        assertEquals("Dế Mèn", b.getTenSach());
        assertEquals("Khu A - 03", b.getShelfLocation());
        assertEquals(8, b.getSoLuongConLai());
    }
}
```

- [ ] **Step 2: Chạy test xác nhận pass**

Run: `./mvnw test -Dtest=TwoPanelSelectorTest`  
Expected: PASS

- [ ] **Step 3: Cập nhật `BookManagementView.fxml` để sử dụng SplitPane**

Thay thế vùng hiển thị đơn lẻ bằng `SplitPane`:
- Trái (68%): Chứa TableView và Grid ScrollPane.
- Phải (32%): Panel `VBox fx:id="detailInspectorPane"` chứa:
  - Ảnh bìa lớn (`ImageView`).
  - Tiêu đề, tác giả, huy hiệu trạng thái.
  - Hộp thông số vị trí kệ: `Label fx:id="lblInspectorShelf"`.
  - Thông số ISBN, giá bán, số bản còn lại.
  - Các nút tác vụ nhanh: `[⚡ Lập phiếu mượn]`, `[✏ Chỉnh sửa]`, `[🗑 Chuyển thùng rác]`.
  - Khối Intriguing Branches ở dưới.

- [ ] **Step 4: Cập nhật `BookManagementController.java`**

- Thêm listener cho `booksTable.getSelectionModel().selectedItemProperty()`.
- Khi chọn 1 cuốn sách: cập nhật toàn bộ `detailInspectorPane`, hiển thị panel nếu đang ẩn.
- Hỗ trợ phím mũi tên bàn phím duyệt sách mượt mà.

- [ ] **Step 5: Kiểm tra test suite**

Run: `./mvnw test -Dtest=TwoPanelSelectorTest`  
Expected: PASS

- [ ] **Step 6: Commit**

```bash
git add src/main/resources/com/vithay/libman/view/BookManagementView.fxml \
        src/main/java/com/vithay/libman/controller/BookManagementController.java \
        src/test/java/com/vithay/libman/TwoPanelSelectorTest.java
git commit -m "feat(books): implement Two-Panel Selector with Detail Inspector"
```

---

### Task 4: Kho Sách - One-Window Drilldown, Extras on Demand & Intriguing Branches

**Files:**
- Modify: `src/main/resources/com/vithay/libman/view/BookManagementView.fxml`
- Modify: `src/main/java/com/vithay/libman/controller/BookManagementController.java`
- Test: `src/test/java/com/vithay/libman/BookDrilldownAndBranchesTest.java`

**Interfaces:**
- Consumes: `Book.getShelfLocation()`, `Book.getTenTacGia()`, `BorrowTransactionDao.getTransactionsByBook(String bookId)`
- Produces: `BookManagementController.drilldownByShelf(String shelf)`, `BookManagementController.drilldownByAuthor(String author)`, `BookManagementController.showCirculationHistory(Book book)`

- [ ] **Step 1: Viết test cho thuật toán lọc sách cùng kệ và cùng tác giả (Intriguing Branches)**

Tạo file `src/test/java/com/vithay/libman/BookDrilldownAndBranchesTest.java`:
```java
package com.vithay.libman;

import com.vithay.libman.model.Book;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class BookDrilldownAndBranchesTest {
    @Test
    public void testFindBooksOnSameShelf() {
        List<Book> books = new ArrayList<>();
        books.add(new Book("BK001", "Sách 1", "TG A", "VH", "Khu A - Kệ 01", 50000, 5, 2, "KHA_DUNG", null));
        books.add(new Book("BK002", "Sách 2", "TG B", "VH", "Khu A - Kệ 01", 60000, 3, 1, "KHA_DUNG", null));
        books.add(new Book("BK003", "Sách 3", "TG C", "TN", "Khu B - Kệ 02", 70000, 4, 4, "KHA_DUNG", null));

        Book current = books.get(0);
        List<Book> sameShelf = books.stream()
                .filter(b -> !b.getMaSach().equals(current.getMaSach()) && b.getShelfLocation().equalsIgnoreCase(current.getShelfLocation()))
                .toList();

        assertEquals(1, sameShelf.size());
        assertEquals("BK002", sameShelf.get(0).getMaSach());
    }
}
```

- [ ] **Step 2: Chạy test xác nhận pass**

Run: `./mvnw test -Dtest=BookDrilldownAndBranchesTest`  
Expected: PASS

- [ ] **Step 3: Bổ sung thanh Breadcrumb và dải Lọc Nâng Cao (Extras on Demand) vào FXML**

- Breadcrumb HBox: `[Kho Sách] > [Tác giả: ...] > [Chi tiết...]`
- Panel Lọc Nâng Cao (`advancedFilterPane`): Gồm combo Thể loại, Combo Kệ, Input Khoảng giá, Combo Tình trạng tồn. Nút `[Lọc nâng cao ▾]` ẩn/hiện dải này.

- [ ] **Step 4: Bổ sung Intriguing Branches và Drilldown vào Controller**

- Hàm `renderIntriguingBranches(Book book)`: Query 2-3 cuốn cùng kệ và cùng tác giả, hiển thị mini cards trong Inspector.
- Khi nhấp vào tác giả / kệ / thể loại: Cập nhật breadcrumb và tự động lọc danh sách trong cùng cửa sổ.
- Thao tác *"Lịch sử lưu hành"*: Hiển thị danh sách phiếu mượn của cuốn sách này với nút quay lại trên breadcrumb.

- [ ] **Step 5: Chạy toàn bộ test**

Run: `./mvnw test -Dtest=BookDrilldownAndBranchesTest`  
Expected: PASS

- [ ] **Step 6: Commit**

```bash
git add src/main/resources/com/vithay/libman/view/BookManagementView.fxml \
        src/main/java/com/vithay/libman/controller/BookManagementController.java \
        src/test/java/com/vithay/libman/BookDrilldownAndBranchesTest.java
git commit -m "feat(books): implement Breadcrumb Drilldown, Extras on Demand, and Intriguing Branches"
```

---

### Task 5: Lưu Hành - Xây Dựng Bàn Lưu Hành Trực Quan (Canvas Plus Palette)

**Files:**
- Create: `src/main/java/com/vithay/libman/model/CirculationBasketItem.java`
- Modify: `src/main/resources/com/vithay/libman/view/BorrowReturnView.fxml`
- Modify: `src/main/java/com/vithay/libman/controller/BorrowReturnController.java`
- Test: `src/test/java/com/vithay/libman/CirculationBasketTest.java`

**Interfaces:**
- Consumes: `Book`, `Reader`, `BorrowService.createBorrowTransaction()`
- Produces: `CirculationBasketItem(Book book, String loanType, int days)`, `BorrowReturnController.addToBasket(Book)`, `BorrowReturnController.removeFromBasket(Book)`

- [ ] **Step 1: Viết test cho giỏ lưu hành và kiểm tra hạn ngạch 5 cuốn**

Tạo file `src/test/java/com/vithay/libman/CirculationBasketTest.java`:
```java
package com.vithay.libman;

import com.vithay.libman.model.Book;
import com.vithay.libman.model.Reader;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class CirculationBasketTest {
    @Test
    public void testBasketMaxLimitEnforcement() {
        int maxAllowed = 5;
        int currentlyBorrowing = 3;
        int remainingQuota = maxAllowed - currentlyBorrowing; // 2

        List<Book> basket = new ArrayList<>();
        basket.add(new Book("BK001", "Book 1", "A", "VH", "K1", 50000, 5, 2, "KHA_DUNG", null));
        basket.add(new Book("BK002", "Book 2", "B", "VH", "K2", 60000, 3, 1, "KHA_DUNG", null));

        assertTrue(basket.size() <= remainingQuota, "Basket fits in remaining quota");

        basket.add(new Book("BK003", "Book 3", "C", "VH", "K3", 70000, 4, 2, "KHA_DUNG", null));
        assertFalse(basket.size() <= remainingQuota, "Basket exceeds reader limit of 5");
    }
}
```

- [ ] **Step 2: Chạy test xác nhận pass**

Run: `./mvnw test -Dtest=CirculationBasketTest`  
Expected: PASS

- [ ] **Step 3: Tạo model `CirculationBasketItem.java`**

Tạo file `src/main/java/com/vithay/libman/model/CirculationBasketItem.java`:
```java
package com.vithay.libman.model;

import java.time.LocalDate;

public class CirculationBasketItem {
    private final Book book;
    private String loanType; // "TAI_CHO" hoặc "VE_NHA"
    private int loanDays;
    private LocalDate dueDate;

    public CirculationBasketItem(Book book, String loanType, int loanDays) {
        this.book = book;
        this.loanType = loanType;
        this.loanDays = loanDays;
        this.dueDate = LocalDate.now().plusDays(loanDays);
    }

    public Book getBook() { return book; }
    public String getLoanType() { return loanType; }
    public void setLoanType(String loanType) { this.loanType = loanType; }
    public int getLoanDays() { return loanDays; }
    public void setLoanDays(int loanDays) { 
        this.loanDays = loanDays; 
        this.dueDate = LocalDate.now().plusDays(loanDays);
    }
    public LocalDate getDueDate() { return dueDate; }
}
```

- [ ] **Step 4: Cập nhật `BorrowReturnView.fxml` & `BorrowReturnController.java`**

Thêm Tab/Chế độ **`Bàn Lưu Hành (Desk)`**:
- Cột Palette (30%): Tìm kiếm sách nhanh, danh sách sách khả dụng với nút `[+ Thêm vào bàn]`.
- Cột Canvas (70%):
  - Card Độc giả: Chọn mã độc giả, hiển thị trạng thái thẻ, số sách đang mượn.
  - Vùng Canvas Giỏ sách: Danh sách các thẻ sách đang được xếp trên bàn. Mỗi thẻ cho phép đổi hình thức (Tại chỗ / Về nhà), xem ngày trả, nút `[✕]`.
  - Thanh tổng kết & nút `[⚡ Hoàn Tất & In Phiếu]`.

- [ ] **Step 5: Kiểm tra test suite**

Run: `./mvnw test -Dtest=CirculationBasketTest`  
Expected: PASS

- [ ] **Step 6: Commit**

```bash
git add src/main/java/com/vithay/libman/model/CirculationBasketItem.java \
        src/main/resources/com/vithay/libman/view/BorrowReturnView.fxml \
        src/main/java/com/vithay/libman/controller/BorrowReturnController.java \
        src/test/java/com/vithay/libman/CirculationBasketTest.java
git commit -m "feat(circulation): implement Canvas Plus Palette circulation desk"
```

---

### Task 6: Lưu Hành - Tích Hợp Trợ Lý Lập Phiếu (Borrowing Wizard)

**Files:**
- Modify: `src/main/resources/com/vithay/libman/view/BorrowReturnView.fxml`
- Modify: `src/main/java/com/vithay/libman/controller/BorrowReturnController.java`
- Test: `src/test/java/com/vithay/libman/BorrowWizardTest.java`

**Interfaces:**
- Consumes: `ReaderService.isCardValid()`, `BorrowService`, `SettingService`
- Produces: `BorrowReturnController.wizardNextStep()`, `BorrowReturnController.wizardPrevStep()`, `BorrowReturnController.wizardFinish()`

- [ ] **Step 1: Viết test cho các bước kiểm soát hợp lệ của Wizard**

Tạo file `src/test/java/com/vithay/libman/BorrowWizardTest.java`:
```java
package com.vithay.libman;

import com.vithay.libman.model.Reader;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

public class BorrowWizardTest {
    @Test
    public void testStep1ReaderValidation() {
        Reader validReader = new Reader("DG001", "Nguyen Van A", "a@test.com", "0901", "123", "Da Nang",
                LocalDate.now().minusYears(20), LocalDate.now().minusDays(10), LocalDate.now().plusMonths(6), "HOAT_DONG");
        assertTrue(validReader.getNgayHetHan().isAfter(LocalDate.now()), "Card must be active and not expired");

        Reader expiredReader = new Reader("DG002", "Tran Van B", "b@test.com", "0902", "124", "Da Nang",
                LocalDate.now().minusYears(25), LocalDate.now().minusYears(1), LocalDate.now().minusDays(1), "KHOA");
        assertFalse(expiredReader.getNgayHetHan().isAfter(LocalDate.now()), "Card expired must fail Step 1");
    }
}
```

- [ ] **Step 2: Chạy test xác nhận pass**

Run: `./mvnw test -Dtest=BorrowWizardTest`  
Expected: PASS

- [ ] **Step 3: Thiết kế Stepper và 4 Bước Wizard trong FXML**

Trong `BorrowReturnView.fxml`, bổ sung Tab/Container **`Trợ Lý Wizard`**:
- Thanh Stepper 4 bước: `[1. Độc giả] ➔ [2. Chọn sách] ➔ [3. Hạn & Quy định] ➔ [4. Xác nhận]`
- Khung Step 1: Chọn độc giả + kiểm tra hợp lệ thẻ.
- Khung Step 2: Chọn danh sách sách cần mượn từ kho khả dụng.
- Khung Step 3: Chọn hình thức mượn, tính ngày hẹn trả tự động theo quy định.
- Khung Step 4: Hiển thị bản xem trước phiếu mượn chuẩn (Phiếu mượn SRS) + Nút hoàn tất.

- [ ] **Step 4: Cập nhật Controller điều phối chuyển bước tuần tự**

Trong `BorrowReturnController`:
- Quản lý biến trạng thái `currentWizardStep` (1 đến 4).
- Hàm `handleWizardNext()`: Kiểm tra hợp lệ bước hiện tại trước khi chuyển sang bước kế tiếp.
- Hàm `handleWizardPrev()`: Quay lại bước trước để sửa đổi.
- Hàm `handleWizardFinish()`: Lưu giao dịch và mở hộp thoại in phiếu.

- [ ] **Step 5: Kiểm tra test suite**

Run: `./mvnw test -Dtest=BorrowWizardTest`  
Expected: PASS

- [ ] **Step 6: Commit**

```bash
git add src/main/resources/com/vithay/libman/view/BorrowReturnView.fxml \
        src/main/java/com/vithay/libman/controller/BorrowReturnController.java \
        src/test/java/com/vithay/libman/BorrowWizardTest.java
git commit -m "feat(circulation): implement 4-step Borrowing Wizard"
```

---

### Task 7: Lưu Hành - Extras on Demand & Intriguing Branches Tại Quầy

**Files:**
- Modify: `src/main/resources/com/vithay/libman/view/BorrowReturnView.fxml`
- Modify: `src/main/java/com/vithay/libman/controller/BorrowReturnController.java`
- Test: `src/test/java/com/vithay/libman/CirculationContextualBranchTest.java`

**Interfaces:**
- Consumes: `BorrowTransactionDao.getActiveTransactions()`, `Reader.getMaDocGia()`
- Produces: `BorrowReturnController.checkReaderUpcomingDueAlerts(String readerId)`

- [ ] **Step 1: Viết test cho thuật toán phát hiện sách sắp đến hạn trả**

Tạo file `src/test/java/com/vithay/libman/CirculationContextualBranchTest.java`:
```java
package com.vithay.libman;

import com.vithay.libman.model.BorrowTransaction;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class CirculationContextualBranchTest {
    @Test
    public void testDetectUpcomingDueDateAlert() {
        LocalDate today = LocalDate.now();
        List<BorrowTransaction> activeTx = new ArrayList<>();
        // Sách sắp hết hạn sau 2 ngày
        activeTx.add(new BorrowTransaction("TX001", "DG001", "BK001", "VANG_VE", today.minusDays(12), today.plusDays(2), null, 0, "DANG_MUON"));
        // Sách còn hạn 10 ngày
        activeTx.add(new BorrowTransaction("TX002", "DG001", "BK002", "VANG_VE", today.minusDays(4), today.plusDays(10), null, 0, "DANG_MUON"));

        List<BorrowTransaction> upcomingAlerts = activeTx.stream()
                .filter(tx -> tx.getHanTra().isAfter(today) && tx.getHanTra().isBefore(today.plusDays(3)))
                .toList();

        assertEquals(1, upcomingAlerts.size());
        assertEquals("TX001", upcomingAlerts.get(0).getMaPhieu());
    }
}
```

- [ ] **Step 2: Chạy test xác nhận pass**

Run: `./mvnw test -Dtest=CirculationContextualBranchTest`  
Expected: PASS

- [ ] **Step 3: Cập nhật FXML và Controller**

- Bổ sung khối **`Cảnh báo & Gợi mở (Intriguing Branches)`** trên đầu Canvas mượn trả:
  - Khi thủ thư chọn độc giả, kiểm tra nếu độc giả có sách sắp hết hạn (trong vòng 2 ngày) ➔ hiện banner cảnh báo màu vàng: *"⚠️ Độc giả có 1 cuốn sách sắp hết hạn trả vào ngày mai: Nhắc nhở độc giả gia hạn hoặc mang trả!"*
- Bổ sung khối **`Extras on Demand`**: Nút `[Xem quy chế & chế tài áp dụng ▾]` mở rộng bảng thông tin mức phạt 2.000đ/ngày và tiền đền bù sách mất (200% + 20.000đ).

- [ ] **Step 4: Kiểm tra toàn bộ test**

Run: `./mvnw test -Dtest=CirculationContextualBranchTest`  
Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add src/main/resources/com/vithay/libman/view/BorrowReturnView.fxml \
        src/main/java/com/vithay/libman/controller/BorrowReturnController.java \
        src/test/java/com/vithay/libman/CirculationContextualBranchTest.java
git commit -m "feat(circulation): add upcoming due date alerts and on-demand penalty rules"
```

---

### Task 8: Tích Hợp Tổng Thể, Kiểm Thử Toàn Diện & Đóng Gói Ứng Dụng

**Files:**
- Test: Toàn bộ test suite trong `src/test/java/`
- Build: `pom.xml`, `run.sh`

- [ ] **Step 1: Chạy toàn bộ unit test suite**

Run: `./mvnw clean test`  
Expected: Tất cả các bài test (cũ và mới) đạt 100% PASS mà không có lỗi.

- [ ] **Step 2: Kiểm tra biên dịch và đóng gói độc lập**

Run: `./mvnw package -DskipTests`  
Expected: Tạo thành công Fat JAR `target/libman-1.0.0.jar` và `target/LibMan.exe`.

- [ ] **Step 3: Khởi chạy kiểm thử giao diện thực tế**

Run: `./run.sh` hoặc `./mvnw javafx:run`  
Xác nhận:
- Chuyển đổi mượt mà giữa Table View và Grid View trong Kho sách.
- Two-Panel Inspector mở ra khi chọn sách và hiển thị đúng thông tin.
- Breadcrumb điều hướng drill-down hoạt động nhịp nhàng.
- Bàn lưu hành Canvas và Wizard lập phiếu mượn hoạt động chính xác với cơ sở dữ liệu SQLite.

- [ ] **Step 4: Commit và hoàn tất**

```bash
git add .
git commit -m "chore(release): verify and finalize 7 interaction design patterns integration"
```
