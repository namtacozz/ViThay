# Comprehensive UI/UX Enhancements & Bug Fixes Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Triển khai triệt để 14 yêu cầu tinh chỉnh UI/UX, sửa các lỗi bộ lọc (tác giả, làm mới, nâng cao), khắc phục lỗi click sách nổi bật, triển khai trang chi tiết sách 2 tầng (1/4 preview trái + 3/4 mô tả và bàn luận độc giả phải), thu gọn Cài đặt, Việt hóa 100%, bổ sung sách cùng tác giả, ảnh bìa và loại bỏ hoàn toàn icon ô vuông.

**Architecture:** Mở rộng kiến trúc tương tác UI JavaFX:
- `BookDiscussionService` quản lý mô tả tác phẩm và dữ liệu thảo luận/đánh giá độc giả.
- `BookDetailView` (tầng 2) hiển thị bố cục chia 1/4 (Thông tin bìa/nhanh) và 3/4 (Mô tả & Bàn luận độc giả).
- Chuẩn hóa bộ lọc trong `BookManagementController` loại bỏ race condition giữa drilldown và ComboBox, đảm bảo làm mới trả về toàn bộ dữ liệu.
- Thay thế triệt để các emoji đa byte bằng SVG / Unicode BMP ổn định chống lỗi tofu ô vuông.

**Tech Stack:** JavaFX 21, Java 17+, SQLite, CSS Spotify Dark Theme & Modern Pink Theme, JUnit 5.

**Spec:** Yêu cầu người dùng gồm 14 điểm cụ thể.

## Global Constraints

- Tuân thủ bảng màu Spotify Dark Theme và hỗ trợ chuyển đổi mượt mà với Light Theme (`ThemeManager`).
- Tuyệt đối không hardcode inline `-fx-text-fill: #FFFFFF` trực tiếp trên các node chính (sử dụng class CSS).
- Không làm thay đổi phá vỡ schema SQLite hiện có của các DAO hiện tại (`BookDao`, `BorrowTransactionDao`, `ReaderDao`, `SettingDao`).
- Toàn bộ 39 unit test hiện tại và các test mới phải vượt qua 100% sau mỗi task (`./mvnw test`).

---

### Task 1: Thương Hiệu, Thu Gọn Cài Đặt & Việt Hóa Toàn Diện

**Files:**
- Modify: `src/main/java/com/vithay/libman/LibManApp.java`
- Modify: `src/main/java/com/vithay/libman/controller/MainLayoutController.java`
- Modify: `src/main/java/com/vithay/libman/controller/SettingsController.java`
- Modify: `src/main/java/com/vithay/libman/util/ThemeManager.java`
- Modify: `src/main/resources/com/vithay/libman/view/SettingsView.fxml`
- Modify: `src/main/resources/com/vithay/libman/view/HomeView.fxml`
- Modify: `src/main/java/com/vithay/libman/controller/HomeController.java`

- [ ] **Step 1: Cập nhật tiêu đề ứng dụng và nhãn theme**
Xóa cụm từ `(Spotify Dark Theme)` tại `LibManApp.java:35`, đổi thành `"LibMan - Hệ Thống Quản Lý Thư Viện Hiện Đại"`. Đổi các nhãn theme trong `ThemeManager.java`, `SettingsController.java`, `MainLayoutController.java` thành `"Chế độ Tối (Mặc định)"` và `"Chế độ Sáng (Modern Pink)"`.

- [ ] **Step 2: Thu gọn giao diện Cài đặt trong `SettingsView.fxml`**
Giảm `maxWidth` form từ 800 xuống 620, giảm padding ngoài từ 32 xuống 18, giảm `vgap` từ 16 xuống 10, thu nhỏ kích thước của các Spinner và TextField để giao diện gọn gàng, thanh thoát.

- [ ] **Step 3: Đồng bộ Tiếng Việt hoàn toàn**
Trong `HomeView.fxml`, đổi `"Recent Borrowing Transactions"` thành `"Giao Dịch Mượn Sách Gần Đây"`. Trong `HomeController.java`, chuẩn hóa các badge thẻ sách thành `"Khả dụng"`, `"Đang mượn"`, `"Đang giữ"`.

- [ ] **Step 4: Chạy kiểm thử xác nhận**
Chạy `./mvnw test` đảm bảo 39/39 tests vượt qua.

- [ ] **Step 5: Commit**
`git commit -m "feat(branding): remove spotify tag, compact settings UI, full vietnamese sync"`

---

### Task 2: Loại Bỏ Icon Ô Vuông & Thêm Viền Tròn Chữ "i" Nút Quy Chế

**Files:**
- Modify: `src/main/resources/com/vithay/libman/view/BorrowReturnView.fxml`
- Modify: `src/main/java/com/vithay/libman/controller/BorrowReturnController.java`
- Modify: `src/main/resources/com/vithay/libman/view/BookManagementView.fxml`
- Modify: `src/main/java/com/vithay/libman/controller/BookManagementController.java`
- Modify: `src/main/resources/com/vithay/libman/view/MainLayout.fxml`
- Modify: `src/main/resources/com/vithay/libman/css/style.css`

- [ ] **Step 1: Thêm viền tròn quanh chữ "i" cho nút Quy chế & chế tài**
Đổi ký tự `ℹ` thành `ⓘ` (U+24D8) trong `BorrowReturnView.fxml` và `BorrowReturnController.java`. Thêm class CSS `.btn-regulations-info` trong `style.css` với kiểu dáng chữ "i" viền tròn thanh lịch.

- [ ] **Step 2: Khắc phục toàn bộ emoji lỗi ô vuông sang biểu tượng chuẩn**
Thay thế các emoji đa byte (🧙, ⚡, 📥, 🗑, 📋, 📌, 🔔, ⚙, ❓, 💾, 🚪) trong các file FXML và Controller bằng các nhãn văn bản sắc nét và biểu tượng chuẩn (✓, ✕, •, →, ⓘ, ★) không bao giờ bị ô vuông tofu trên bất kỳ hệ điều hành nào.

- [ ] **Step 3: Chạy kiểm thử xác nhận**
Chạy `./mvnw test` đảm bảo toàn bộ tests PASS.

- [ ] **Step 4: Commit**
`git commit -m "fix(ui): eliminate tofu square emoji icons, add circled info icon for regulations"`

---

### Task 3: Ẩn Hiện Dashboard Thu Gọn & Sửa Lỗi Click Sách Nổi Bật Chỉ Ra Sapiens

**Files:**
- Modify: `src/main/resources/com/vithay/libman/view/HomeView.fxml`
- Modify: `src/main/java/com/vithay/libman/controller/HomeController.java`
- Modify: `src/main/java/com/vithay/libman/controller/MainLayoutController.java`

- [ ] **Step 1: Bổ sung icon vào từng thẻ chỉ số và nút thu gọn Dashboard**
Trong `HomeView.fxml`, bổ sung các biểu tượng nhận diện trước tiêu đề từng thẻ chỉ số (Sách, Sẵn sàng, Đang mượn, Độc giả). Thêm nút toggle `"Thu gọn chỉ số ▴"` / `"Mở rộng chỉ số ▾"` để cho phép người dùng ẩn/hiện dãy thẻ thống kê.

- [ ] **Step 2: Sửa lỗi click sách nổi bật chỉ ra Sapiens**
Trong `MainLayoutController.java`, tạo phương thức `showBookViewWithSelection(Book book)` để chuyển sang Kho sách và tự động chọn đúng cuốn sách được bấm (cập nhật selection trong bảng/lưới và mở inspector xem trước). Trong `HomeController.java`, sửa sự kiện click thẻ sách gọi `mainController.showBookViewWithSelection(book)`.

- [ ] **Step 3: Chạy kiểm thử xác nhận**
Chạy `./mvnw test` xác nhận không có lỗi phát sinh.

- [ ] **Step 4: Commit**
`git commit -m "feat(home): add dashboard collapsible stats with icons, fix featured book click target"`

---

### Task 4: Sửa Dứt Điểm Bộ Lọc Tác Giả, Lỗi Làm Mới & Lọc Nâng Cao

**Files:**
- Modify: `src/main/java/com/vithay/libman/controller/BookManagementController.java`
- Modify: `src/main/resources/com/vithay/libman/view/BookManagementView.fxml`
- Create: `src/test/java/com/vithay/libman/BookFilterBugFixesTest.java`

- [ ] **Step 1: Viết failing test cho bộ lọc tác giả, làm mới và lọc nâng cao**
Tạo `BookFilterBugFixesTest.java` kiểm tra:
1. Lọc tác giả hoạt động chính xác với so sánh tên (không bị race condition do event re-entrancy).
2. Nút làm mới (`handleRefreshAllBooks`) reset mọi trường lọc và trả về 100% sách.
3. Bộ lọc nâng cao (thể loại, giá min/max, trạng thái kho) lọc chính xác cả bảng lẫn lưới.

- [ ] **Step 2: Sửa lỗi lọc tác giả**
Sử dụng cờ `isUpdatingFilterUI` trong `BookManagementController.java` để ngăn sự kiện `onAction` của `authorFilterCombo` tự xóa `drilldownAuthor`. Sửa `applyFilters()` so sánh chính xác tên tác giả (chuẩn hóa hoa/thường và khoảng trắng).

- [ ] **Step 3: Sửa lỗi làm mới kho sách**
Tạo phương thức `handleRefreshAllBooks()`: Reset `txtSearchBook`, `authorFilterCombo`, `statusFilterCombo`, `currentCategoryFilter`, các ô giá và kệ sách nâng cao, sau đó gọi `loadBooks()` để hiển thị lại toàn bộ sách.

- [ ] **Step 4: Hoàn thiện bộ lọc nâng cao**
Đảm bảo khi chọn thể loại ở bộ lọc nâng cao sẽ đồng bộ và có hiệu lực ngay; hỗ trợ lọc khoảng giá Min - Max; cập nhật đồng bộ cho cả Table View và Grid Card View.

- [ ] **Step 5: Chạy test kiểm tra**
Chạy `./mvnw test -Dtest=BookFilterBugFixesTest` và `./mvnw test`.

- [ ] **Step 6: Commit**
`git commit -m "fix(books): fix author filter, refresh button reset, and advanced filter synchronization"`

---

### Task 5: Chuẩn Hóa Tên Độc Giả, Thêm Sách Cùng Tác Giả & Bìa Sách

**Files:**
- Modify: `src/main/resources/com/vithay/libman/database/seed_data.sql`
- Create images: Thêm các file ảnh bìa sách trong `src/main/resources/com/vithay/libman/images/`

- [ ] **Step 1: Chuẩn hóa họ tên độc giả và người dùng**
Đổi tài khoản thủ thư thành `"Nguyễn Minh Trí"`. Chuẩn hóa đầy đủ họ tên cho 8 độc giả mẫu trong `seed_data.sql`.

- [ ] **Step 2: Bổ sung sách của cùng 1 tác giả**
Thêm 8 cuốn sách mới của các tác giả đã có:
- Yuval Noah Harari: *Homo Deus*, *21 Bài Học Cho Thế Kỷ 21*
- Robert C. Martin: *Clean Architecture*, *The Clean Coder*
- Nguyễn Nhật Ánh: *Cho Tôi Xin Một Vé Đi Tuổi Thơ*, *Tôi Thấy Hoa Vàng Trên Cỏ Xanh*
- J.K. Rowling: *Harry Potter và Phòng Chứa Bí Mật*, *Harry Potter và Tên Tù Nhân Ngục Azkaban*

- [ ] **Step 3: Bổ sung file ảnh bìa sách**
Thêm ảnh bìa tương ứng vào thư mục images để hiển thị trực quan và sống động.

- [ ] **Step 4: Chạy kiểm thử xác nhận**
Chạy `./mvnw test` đảm bảo 100% tests PASS.

- [ ] **Step 5: Commit**
`git commit -m "feat(data): refine reader full names, add multi-book authors and book covers"`

---

### Task 6: Cơ Chế Xem Sách 2 Tầng & Trang Chi Tiết Sách Toàn Diện

**Files:**
- Create: `src/main/java/com/vithay/libman/model/BookReview.java`
- Create: `src/main/java/com/vithay/libman/service/BookDiscussionService.java`
- Modify: `src/main/resources/com/vithay/libman/view/BookManagementView.fxml`
- Modify: `src/main/java/com/vithay/libman/controller/BookManagementController.java`
- Modify: `src/main/java/com/vithay/libman/controller/MainLayoutController.java`
- Create: `src/test/java/com/vithay/libman/BookDiscussionServiceTest.java`

- [ ] **Step 1: Viết test cho `BookDiscussionService`**
Kiểm tra khả năng lấy mô tả sách và danh sách bình luận độc giả, thêm bình luận mới.

- [ ] **Step 2: Triển khai `BookReview` và `BookDiscussionService`**
Tạo model `BookReview` và service `BookDiscussionService` chứa dữ liệu mô tả và bình luận phong phú cho các đầu sách.

- [ ] **Step 3: Triển khai giao diện Chi Tiết Sách 2 Tầng**
- **Tầng 1 (Click 1)**: Hiển thị Preview tóm tắt trong thanh Inspector hoặc thẻ card (Ảnh bìa, tên sách, tác giả, thể loại, trạng thái).
- **Tầng 2 (Click 2 / Bấm "Xem Chi Tiết Đầy Đủ")**: Mở View Chi Tiết Toàn Diện:
  - **Cột trái (~1/4)**: Ảnh bìa lớn, Tên sách, Tác giả, Thể loại, Vị trí kệ, Số bản, Nút "Quay lại", Nút "Lập Phiếu Mượn".
  - **Cột phải (~3/4)**: Phần Description (Mô tả nội dung tác phẩm) và Phần Bàn luận của độc giả (Hiển thị các nhận xét, đánh giá số sao, khung gửi bình luận mới).

- [ ] **Step 4: Chạy kiểm thử toàn bộ hệ thống**
Chạy `./mvnw test` đảm bảo toàn bộ tests PASS.

- [ ] **Step 5: Commit**
`git commit -m "feat(books): implement two-tier book detail view with 1/4 preview and 3/4 discussion"`
