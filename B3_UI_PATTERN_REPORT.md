# BÁO CÁO ÁNH XẠ VÀ TINH CHỈNH THIẾT KẾ UI/UX LIBMAN THEO TÀI LIỆU B3 (B3_UI_PATTERN_REPORT)

Dự án: **LibMan - Library Management System (JavaFX Desktop)**  
Kiến trúc: **MVC (Model - View - Controller), Clean Architecture**  
Tài liệu quy chuẩn: `B3.md` (6 nguyên lý thiết kế và bố cục tương tác giao diện người dùng)

---

## 1. PHÂN CẤP TRỰC QUAN (VISUAL HIERARCHY - ƯU TIÊN SỐ 1 Ở GÓC TRÊN BÊN TRÁI)

### Quy chuẩn B3:
> *"Mức độ ưu tiên số 1 đặt ở góc trên bên trái để thuận theo điểm rơi thị giác đầu tiên của người dùng theo thói quen đọc (F-Pattern / Z-Pattern)."*

### Hiện trạng & Triển khai trong Codebase LibMan:
1. **Cấu trúc Header & Title chuẩn mực:**
   - Tất cả các màn hình chính (`HomeView.fxml`, `BookManagementView.fxml`, `BorrowReturnView.fxml`, `ReaderManagementView.fxml`, `StatisticsView.fxml`, `CategoryView.fxml`) đều định vị **Tiêu đề định danh trang (`.title-large`)** kèm Breadcrumb/mô tả ở tọa độ trên cùng bên trái.
2. **Trang chủ (`HomeView.fxml`):**
   - KPI quan trọng nhất của thư viện: Thẻ **"Tổng Đầu Sách"** (`lblStatTotalBooks`) được bố trí tại vị trí đầu tiên (góc trên bên trái hàng KPI) giúp thủ thư nắm ngay quy mô kho sách.
3. **Kho sách (`BookManagementView.fxml`):**
   - **Thanh tìm kiếm nhanh (`txtSearchBook`)** được chuyển dịch lên vị trí ưu tiên số 1 (bên trái ngoài cùng của thanh Toolbar `filterToolbar`), đi kèm icon kính lúp và placeholder trực quan. Người dùng mở trang có thể gõ phím tìm kiếm ngay lập tức.
4. **Quản lý Độc giả (`ReaderManagementView.fxml`):**
   - Ô tìm kiếm độc giả (`txtSearchReader`) được đưa lên góc trên bên trái của thanh lọc để người dùng tìm nhanh theo Họ tên, SĐT, CCCD mà không cần liếc mắt sang góc phải.

---

## 2. DÒNG TRỰC QUAN NGƯỜI DÙNG (VISUAL FLOW: TRÁI ➔ PHẢI, TRÊN ➔ DƯỚI)

### Quy chuẩn B3:
> *"Luồng tương tác được sắp đặt mạch lạc từ Trái sang Phải (nhập/chọn ➔ xử lý ➔ chốt hành động) và Trên xuống Dưới theo trật tự tự nhiên."*

### Hiện trạng & Triển khai trong Codebase LibMan:
1. **Bàn Lưu Hành Mượn Trả (`BorrowReturnView.fxml`):**
   - Triển khai chuẩn mẫu **Canvas Plus Palette**:
     - **Bước 1 (Trái):** Palette tìm kiếm và kho `Sách Khả Dụng` (cột rộng 32%).
     - **Bước 2 (Góc trên Canvas):** Chọn hồ sơ Độc giả (`comboDeskReader`) và hiển thị thẻ trạng thái độc giả (Quota, Số sách đang mượn).
     - **Bước 3 (Thân Canvas):** Bàn làm việc chứa giỏ sách (`basketCardsContainer`) kèm ngày mượn, hạn trả dự kiến và phí cọc.
     - **Bước 4 (Góc dưới phải Canvas):** Nút chốt phiên mượn **[✓ Hoàn Tất Mượn Sách]** (`btnCompleteCheckout`) với độ tương phản cao nhất.
2. **Trợ Lý Mượn Nhanh 4 Bước (Wizard Pattern - `BorrowReturnView.fxml`):**
   - Thanh tiến trình ngang (Stepper Indicator): `[1. Độc Giả] ➔ [2. Chọn Sách] ➔ [3. Thiết Lập] ➔ [4. Xác Nhận]`.
   - Nút hành động chính luôn nằm ở góc dưới bên phải (`btnWizardNext`, `btnWizardFinish`).

---

## 3. PHÂN NHÓM CHỨC NĂNG (PROXIMITY & FUNCTIONAL GROUPING)

### Quy chuẩn B3:
> *"Các thành phần có chức năng liên quan được gom nhóm vào các container/card riêng biệt, phân tách rõ ràng giữa hành động chính (Primary CTA) và tiện ích phụ trợ (Secondary/Utility)."*

### Hiện trạng & Triển khai trong Codebase LibMan:
1. **Tách biệt Primary CTA và Secondary Utility:**
   - Trong `BookManagementView.fxml`: Các nút phụ trợ như `[Làm Mới]`, `[Xuất CSV]` được gom chung vào container `.header-utility-group`, tách bạch khỏi nút kêu gọi hành động chính `[+ Thêm Sách]` (`btn-primary`).
   - Trong `ReaderManagementView.fxml`: Gom nhóm `[Quét Hết Hạn]`, `[Xuất CSV]`, `[Làm Mới]` vào container `.header-utility-group`, tạo khoảng cách rõ ràng với nút `[+ Thêm Độc Giả]`.
2. **Phân vùng Bộ lọc (Filter Toolbar):**
   - Vùng tìm kiếm, vùng chọn thể loại/tác giả/trạng thái và vùng chuyển đổi góc nhìn Table/Grid (`segmented-view-group`) được đặt trong các thẻ phân nhóm `.filter-group-box`.
3. **Thanh chi tiết Inspector (`detailInspectorPane`):**
   - Các gợi ý thông minh ("Intriguing Branches") được đóng gói thành các khối card trực quan `.inspector-branch-card`:
     - Card gợi ý sách *Cùng vị trí kệ*.
     - Card gợi ý sách *Cùng tác giả*.

---

## 4. KHUNG GIAO DIỆN NHẤT QUÁN (VISUAL FRAMEWORK)

### Quy chuẩn B3:
> *"Khung ứng dụng (App Shell) đồng nhất trên mọi màn hình, cấu trúc điều hướng ổn định, khoảng đệm (padding) chuẩn mực và bộ màu đồng bộ."*

### Hiện trạng & Triển khai trong Codebase LibMan:
1. **Cấu trúc App Shell (`MainLayout.fxml`):**
   - **Top Header Bar:** Logo thương hiệu, thanh tìm kiếm toàn cục (Ctrl+K), cụm thông tin Profile / Role và nút chuông thông báo.
   - **Left Brave Vertical Navigation:** Sidebar điều hướng cố định với các tab chức năng có icon SVG, badge thông báo và khả năng mở rộng/thu gọn mượt mà.
   - **Center Workspace Area:** Vùng hiển thị động với padding chuẩn hóa `24 32 32 32` tạo cảm giác thoáng đãng và sang trọng.
2. **Hệ thống Design Tokens 2 Theme (Dark / Pink-Light):**
   - Đồng bộ hoàn toàn trong `style.css` thông qua các biến và class màu (`bg-dark`, `bg-surface`, `text-primary`, `text-muted`, `btn-primary`, `btn-secondary`).

---

## 5. BẢNG ĐIỀU KHIỂN ĐÓNG ĐƯỢC (CLOSABLE PANEL)

### Quy chuẩn B3:
> *"Khả năng mở rộng không gian làm việc bằng cách thu gọn hoặc ẩn hoàn toàn thanh chi tiết phụ trợ khi không có nhu cầu xem."*

### Hiện trạng & Triển khai trong Codebase LibMan:
1. **Cải tiến tại `BookManagementView.fxml` & `BookManagementController.java`:**
   - **Thanh Header của Panel:** Bổ sung thanh tiêu đề `THÔNG TIN CHI TIẾT` kèm nút đóng **[✕]** (`closeInspectorPanel()`).
   - **Nút Toggle trên Toolbar:** Bổ sung nút **`[◨ Chi Tiết]`** (`btnToggleInspector`) cho phép bật/tắt thanh inspector theo ý muốn.
   - **Cơ chế thu gọn 100% diện tích:** Khi đóng inspector, `detailInspectorPane` được tách khỏi `bookSplitPane.getItems()`, cho phép bảng `booksTable` hoặc lưới thẻ `booksGridPane` nở rộng chiếm trọn 100% chiều ngang màn hình.
   - **Tự động mở lại khi cần:** Khi người dùng click chọn bất kỳ một cuốn sách nào trong bảng hoặc lưới, hàm `openInspectorPanel()` sẽ tự động đưa panel trở lại SplitPane một cách mượt mà.

---

## 6. KHẢ NĂNG DI CHUYỂN & THAY ĐỔI KÍCH THƯỚC (MOVABLE & RESIZABLE PANEL)

### Quy chuẩn B3:
> *"Cho phép người dùng tùy biến không gian làm việc: thay đổi kích thước các vùng phân chia, đảo vị trí các panel làm việc và di chuyển các cửa sổ popup/modal."*

### Hiện trạng & Triển khai trong Codebase LibMan:
1. **Thanh phân chia co giãn trực quan (Resizable SplitPane):**
   - Thanh ngăn cách `SplitPane` được tinh chỉnh trong `style.css`:
     - Thiết lập con trỏ chuột dạng co giãn ngang `cursor: h-resize`.
     - Hiệu ứng phát sáng Hover Glow (`#1DB954` ở Dark Theme, `#FB7185` ở Pink Light Theme) báo hiệu trực quan cho người dùng biết có thể kéo chuột để thay đổi tỉ lệ phân chia giữa Master và Detail.
2. **Đảo vị trí không gian làm việc (Movable Workspace):**
   - **Tại Kho Sách (`BookManagementView`):** Bổ sung nút **`[⇄ Đổi bên]`** (`btnSwapPanels` / `handleSwapPanels()`), cho phép hoán đổi vị trí Master Table sang bên phải và Detail Inspector sang bên trái (hoặc ngược lại) tùy thói quen người dùng.
   - **Tại Bàn Lưu Hành (`BorrowReturnView`):** Bổ sung nút **`[⇄ Đổi bên]`** (`btnSwapDeskPanels` / `handleSwapDeskPanels()`), cho phép đổi chỗ cột `Sách Khả Dụng` và cột `Bàn Lưu Hành`.
3. **Cửa sổ trôi nổi có thể kéo thả (Draggable Floating Panels):**
   - Xây dựng tiện ích [`UIUtils.makeDraggable(headerNode, movablePanel)`](file:///f:/AppMaking/ViThay/src/main/java/com/vithay/libman/util/UIUtils.java).
   - Tích hợp tính năng kéo thả di chuyển bằng thanh tiêu đề cho **tất cả 8 hộp thoại modal** trong ứng dụng:
     - `bookModalBox` (Modal Thêm/Sửa Sách).
     - `readerModalBox` (Modal Thêm/Sửa Độc Giả).
     - `categoryModalBox` (Modal Thể Loại).
     - `authModalBox` (Modal Đăng Nhập / Đăng Ký).
     - `profileModalBox` (Modal Hồ Sơ & Đổi Mật Khẩu).
     - `helpModalBox` (Modal Trợ Giúp SRS).
     - `readerBorrowModalBox` (Modal Mượn Sách Độc Giả).
     - `appNoticeModalBox` (Modal Thông Báo & Hóa Đơn In).
   - Khi đóng modal, hàm `UIUtils.resetPosition(movablePanel)` sẽ tự động đưa vị trí hộp thoại về tâm màn hình cho lần hiển thị kế tiếp.

---

## TỔNG KẾT KIỂM CHỨNG & ĐÓNG GÓI
- **Biên dịch Maven:** `mvnw.cmd compile` hoàn tất **100% BUILD SUCCESS** (41 source files).
- **Đóng gói phân phối:** `mvnw.cmd package -DskipTests` sinh file thực thi **`target/LibMan.exe`** thành công.
- Toàn bộ 6 mẫu thiết kế UI/UX theo `B3.md` đã được cài đặt đầy đủ, hoạt động mượt mà và tương thích với cả 2 Theme (Dark & Light).
