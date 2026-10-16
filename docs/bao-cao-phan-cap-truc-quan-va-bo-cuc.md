# BÁO CÁO PHÂN TÍCH BỐ CỤC & PHÂN CẤP TRỰC QUAN GIAO DIỆN

## Hệ Thống Quản Lý Thư Viện — LibMan (Phiên bản v1.5.0)

**Ngày cập nhật:** 16/10/2026  
**Đối chiếu với:** Đặc tả yêu cầu phần mềm Quản Lý Thư Viện (SRS 8 trang) + Tài liệu B3 (6 nguyên lý phân cấp trực quan, dòng thị giác & bố cục tương tác) + Bộ kiểm thử tự động 63/63 PASS

---

## Tổng Quan

Báo cáo này phân tích **6 nguyên lý thiết kế bố cục, dòng trực quan và phân cấp thị giác** (Interaction & Visual Layout Principles theo tài liệu `B3.md`) được áp dụng trong ứng dụng LibMan Desktop. Báo cáo đối chiếu trực tiếp giữa cơ sở lý thuyết thiết kế tương tác người - máy (HCI) và việc hiện thực hóa trong mã nguồn (JavaFX FXML, CSS, và Controllers), mang lại trải nghiệm tối ưu cho cả thủ thư nghiệp vụ lẫn độc giả tra cứu.

---

## 1. Phân Cấp Trực Quan (Visual Hierarchy — Ưu Tiên Số 1 Ở Góc Trên Bên Trái)

> **Nguyên lý B3:** Chú ý mức độ ưu tiên số 1 đặt ở góc trên bên trái để thuận theo dòng trực quan tự nhiên của mắt người dùng khi tiếp cận màn hình (nguyên tắc F-Pattern / Z-Pattern và Gutenberg Diagram).

```
[Điểm rơi #1: Vị trí vàng góc trên bên trái] ──► Quét ngang (Primary Header)
   │
   ▼ Đi theo chiều dọc
[Nhóm điều hướng / Lọc nhanh #2] ──────────────► Quét ngang sang vùng làm việc
   │
   ▼
[Vùng nội dung trọng tâm #3]
```

### 1.1 Điểm nhận diện không gian làm việc (Workspace Identifier)
- **Chuẩn hóa Header trên toàn bộ các View:** Toàn bộ các màn hình chính (`HomeView.fxml`, `BookManagementView.fxml`, `BorrowReturnView.fxml`, `ReaderManagementView.fxml`, `CategoryManagementView.fxml`, `StatisticsView.fxml`) đều đặt **Tiêu đề định danh (`.title-large`)** ở góc trên cùng bên trái.
- Người dùng khi chuyển tab sẽ lập tức xác nhận được mình đang ở phân hệ nào mà không cần đảo mắt tìm kiếm.
- Bên dưới tiêu đề là dòng mô tả ngữ cảnh hoặc breadcrumb hướng dẫn ngắn gọn, tạo nền tảng định hướng vững chắc.

### 1.2 Bố cục Trang Chủ (`HomeView.fxml`) — Chỉ số KPI hàng đầu
- **Ưu tiên #1:** Thẻ **"TỔNG ĐẦU SÁCH"** (`cardTotalBooks` / `lblTotalBooks`) được bố trí tại vị trí đầu tiên bên trái trong hàng thẻ thống kê.
- Đối với hệ thống thư viện, quy mô kho sách là chỉ số quan trọng nhất cần nắm bắt ngay khi khởi động app.
- Tiếp theo theo chiều từ trái qua phải là các chỉ số thứ cấp: **SẴN SÀNG** (Xanh lá) ➔ **ĐANG MƯỢN** (Vàng cam) ➔ **ĐỘC GIẢ** (Xanh dương).

### 1.3 Kho Sách (`BookManagementView.fxml`) — Thanh tìm kiếm tại điểm rơi thị giác
- Trong thanh công cụ (`filterToolbar`), **ô tìm kiếm nhanh (`headerSearchField` / `txtSearchBook`)** được neo cố định ở tọa độ ưu tiên số 1 bên trái.
- Thủ thư hoặc độc giả khi vào kho sách có thể gõ ngay từ khóa (mã sách, tên sách, tác giả) mà không phải rê chuột sang góc phải như các thiết kế truyền thống.

### 1.4 Quản Lý Độc Giả (`ReaderManagementView.fxml`)
- Ô tìm kiếm độc giả (`txtSearchReader`) được đặt ở góc trên bên trái thanh công cụ. Thủ thư có thể quét mã vạch thẻ hoặc gõ nhanh SĐT / CCCD / Họ tên ngay tức thì.

---

## 2. Dòng Trực Quan Người Dùng (Visual Flow: Trái ➔ Phải, Trên ➔ Dưới)

> **Nguyên lý B3:** Định hướng người dùng tự nhiên theo 2 chiều: từ trên xuống dưới (đọc lướt danh mục) và từ trái sang phải (chọn lựa ➔ thiết lập ➔ chốt hành động). Các thành phần liên quan được xếp liền kề nhau để dẫn dắt hành vi mà không gây nhiễu nhận thức.

```
┌─────────────────────────────────────────────────────────────────────────────┐
│ 1. Chọn nguồn dữ liệu       2. Thiết lập tham số        3. Chốt kết quả     │
│ (Trái / Palette)            (Giữa / Canvas)             (Phải dưới / Action)│
│                                                                             │
│ ┌──────────────────────┐    ┌──────────────────────┐    ┌─────────────────┐ │
│ │ • Danh sách sách     │ ─► │ • Thẻ sách trên bàn  │ ─► │ [✓ XÁC NHẬN]    │ │
│ │ • Bộ lọc khả dụng    │    │ • Hạn trả & Quota    │    │ (Contrast cao)  │ │
│ └──────────────────────┘    └──────────────────────┘    └─────────────────┘ │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 2.1 Bàn Lưu Hành Mượn Trả (`BorrowReturnView.fxml`) — Chuẩn Canvas Plus Palette
Luồng thao tác mượn sách được thiết kế chuẩn xác theo dòng trực quan:
1. **Bước 1 (Bên Trái — Palette Sách Khả Dụng):** Thủ thư tra cứu sách, lọc theo kệ/thể loại và nhấn `[+ Thêm]` hoặc double-click để chuyển sách sang bàn.
2. **Bước 2 (Góc Trên Bên Phải Canvas):** Chọn hồ sơ độc giả (`comboDeskReader`), hệ thống hiển thị thẻ trạng thái độc giả (Quota, hạn hiệu lực thẻ, số sách đang mượn).
3. **Bước 3 (Thân Vùng Canvas):** Bàn làm việc hiển thị các thẻ sách đã chọn kèm hình thức mượn ("Mang về nhà" / "Đọc tại chỗ"), số ngày mượn và hạn trả tự động.
4. **Bước 4 (Góc Dưới Cùng Bên Phải Canvas):** Nút chốt phiên **`[✓ Xác nhận cho mượn]`** (`btnCompleteCheckout`) với màu xanh thương hiệu tương phản cao nhất, nằm đúng điểm dừng mắt cuối cùng của người dùng.

### 2.2 Quy trình Trợ Lý Mượn Nhanh 4 Bước (Borrow Wizard)
- Hàng chỉ báo bước (Horizontal Stepper):  
  `[1. Độc Giả]` ➔ `[2. Chọn Sách]` ➔ `[3. Thiết Lập Phiếu]` ➔ `[4. Xác Nhận & In]`
- Mắt người dùng di chuyển tuần tự từ trái qua phải theo từng bước tiến trình.
- Cụm nút chuyển bước: Nút phụ `[Quay lại]` ở bên trái và nút chính `[Tiếp tục ➔]` / `[Hoàn tất]` luôn cố định ở góc dưới bên phải.

### 2.3 Carousel Sách Nổi Bật Trên Trang Chủ
- Thay vì cuộn dọc gây phân mảnh màn hình, dải sách nổi bật được thiết kế dạng cuộn ngang chuyên dụng (`featuredScrollPane`).
- **Tương tác trực quan tự nhiên:** Tích hợp bộ lắng nghe sự kiện lăn chuột (`setOnScroll`). Khi người dùng di chuột vào vùng sách nổi bật và lăn chuột lên/xuống, hệ thống tự động chuyển đổi thành cuộn ngang mượt mà (trái ➔ phải hoặc phải ➔ trái), không cần phải kéo thanh cuộn.
- Bìa sách, tên sách và tác giả được neo cố định chiều cao (`imagePane` 150px/225px), giúp mắt quét ngang êm ái mà không bị giật dòng do các bìa sách có kích thước khác nhau.

---

## 3. Phân Nhóm Chức Năng (Functional & Proximity Grouping — Định Luật Gestalt)

> **Nguyên lý B3:** Các thành phần có mối quan hệ tương hỗ hoặc cùng một nhóm chức năng phải được gom vào cùng một vùng không gian (Law of Proximity & Common Region). Phân bố rạch ròi giữa nội dung thông tin và phân nhóm theo chức năng, tách biệt giữa hành động chính (Primary CTA) và tiện ích phụ trợ (Secondary Utility).

```
┌─────────────────────────────────────────────────────────────────────────────┐
│ ┌──────────────────────────────────────┐  ┌───────────────────────────────┐ │
│ │ Nhóm Tiện Ích Phụ Trợ (.utility-box) │  │ Hành Động Chính (Primary CTA) │ │
│ │ [↺ Làm Mới]  [⤓ Xuất]  [⚠ Quét Lỗi]  │  │ [ + THÊM ĐỐI TƯỢNG MỚI ]      │ │
│ └──────────────────────────────────────┘  └───────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 3.1 Tách biệt Primary Action và Secondary Utilities
- **Tại Kho Sách (`BookManagementView.fxml`):**
  - Cụm tiện ích phụ trợ được bao bọc trong container `.header-utility-group`: nút icon **`[Xuất]`** và nút icon xoay **`[Làm Mới]`** (Refresh SVG) có tooltip rõ ràng.
  - Tách biệt hoàn toàn với nút kêu gọi hành động chính **`[+ Thêm]`** (`btn-primary` màu xanh lá nổi bật). Người dùng không bị nhầm lẫn giữa thao tác thêm dữ liệu mới và thao tác xuất báo cáo.
- **Tại Quản Lý Độc Giả (`ReaderManagementView.fxml`):**
  - Cụm tiện ích gồm `[Quét Hết Hạn]`, `[Xuất]`, `[Làm Mới]` được gom chung một khung viền tinh gọn (`.header-utility-group`), tách biệt với nút chính `[+ Thêm]`.

### 3.2 Phân vùng Bộ Lọc Đa Chiều (Filter Toolbar)
Thanh công cụ lọc của Kho Sách được phân chia thành 3 khối logic rõ rệt:
1. **Khối Tra Cứu Nhanh (Trái):** Ô tìm kiếm từ khóa kèm phím tắt `Ctrl + K`.
2. **Khối Tiêu Chí Phân Loại (Giữa):** ComboBox Thể loại, ComboBox Trạng thái, Bộ lọc nâng cao (Giá, Tồn kho, Tác giả).
3. **Khối Chuyển Đổi Chế Độ Xem (Phải):** Cụm nút bấm phân đoạn (`.segmented-view-group`) gồm `[Bảng Danh Sách]` và `[Lưới Thẻ Sách]`.

### 3.3 Nhánh Liên Quan & Gợi Ý Trong Inspector (`.inspector-branch-card`)
- Trong panel thông tin chi tiết sách (`detailInspectorPane`), các gợi ý được đóng gói thành các khối card trực quan riêng biệt:
  - **Khối Vị Trí Kệ:** Hiển thị vị trí lưu trữ kèm nhãn nổi bật và nút xem toàn bộ sách cùng kệ.
  - **Khối Nhánh Liên Quan (Intriguing Branches):** 2 card gợi ý riêng biệt gồm sách *Cùng vị trí kệ* và sách *Cùng tác giả*, giúp thủ thư tư vấn nhanh cho bạn đọc mà không cần thực hiện thêm truy vấn tìm kiếm.

---

## 4. Khung Giao Diện Thị Giác Nhất Quán (Visual Framework / App Shell)

> **Nguyên lý B3:** Thiết lập bộ khung ứng dụng (App Shell) đồng nhất xuyên suốt, phân tầng không gian làm việc rõ ràng, tỷ lệ khoảng đệm (padding) chuẩn mực và hệ thống Design Tokens nhất quán.

```
┌─────────────────────────────────────────────────────────────────────────────┐
│ BRAND LOGO       │ GLOBAL SEARCH (Ctrl+K)       │ THEME │ ALERTS │ USER     │ (Header 56px)
├──────────────────┼──────────────────────────────┴───────┴────────┴──────────┤
│ ⌂ Trang chủ      │                                                          │
│ 📖 Kho sách      │                                                          │
│ 🗂 Thể loại      │                 WORKSPACE HIỂN THỊ CHÍNH                 │
│ 👥 Độc giả       │                 (Padding: 24px 32px 32px)                │ (Sidebar 64px
│ ⇄ Lưu hành       │                                                          │  hoặc 250px)
│ 📊 Thống kê      │                                                          │
│ 🗑 Thùng rác     │                                                          │
│ ⚙ Cài đặt        │                                                          │
└──────────────────┴──────────────────────────────────────────────────────────┘
```

### 4.1 Bộ ba cấu trúc cố định (Stable Three-Region Layout)
1. **Top Header Bar (56px):**
   - Góc trái: Logo LibMan thương hiệu đặt thẳng hàng phía trên Sidebar.
   - Giữa: Thanh tìm kiếm toàn cục đa đối tượng (Global Search) tích hợp phím tắt `Ctrl + K` / `Ctrl + F`.
   - Góc phải: Chuyển đổi Theme (Dark/Light), Thông báo hệ thống và Profile người dùng hiện tại (RBAC badge).
2. **Left Brave Vertical Sidebar (64px - 250px):**
   - Hỗ trợ 2 chế độ: Thu gọn chỉ hiện icon SVG (khi không pin) hoặc mở rộng đầy đủ chữ (khi pin hoặc rê chuột).
   - Menu phân nhóm theo nghiệp vụ: Danh Mục Chính ➔ Nghiệp Vụ Lưu Hành ➔ Báo Cáo & Hệ Thống.
3. **Center Workspace Area:**
   - Khoảng đệm đồng bộ `Insets(24, 32, 32, 32)` tạo sự thoáng đãng, phân định ranh giới rõ ràng với thanh điều hướng.

### 4.2 Hệ thống Design Tokens 2 Theme Hoàn Chỉnh (`style.css`)
- **Spotify Dark Theme (Mặc định):**
  - Nền chính `#121212`, Nền card/surface `#181818`, Nền bảng/modal `#242424`.
  - Màu điểm nhấn (Accent): Xanh Spotify `#1DB954`, Viền `#2F2F2F`.
- **Modern Bright Pink Theme (Light Mode):**
  - Nền chính `#FFF5F7`, Nền card/surface `#FFFFFF`, Nền modal `#FFFFFF`.
  - Màu điểm nhấn (Accent): Hồng cánh sen `#FB7185` / `#E91E63`, Viền `#FECDD3`.

### 4.3 Thông báo nổi không cản trở (Non-Blocking Floating Toasts)
- Toàn bộ các thông báo trạng thái tức thời (thêm sách vào giỏ, xóa sách, làm mới) được đưa vào container `toastContainer` ở góc dưới cùng bên phải.
- Cấu hình `mouseTransparent="true"` và `pickOnBounds="false"` đảm bảo thông báo trôi nổi êm ái mà **không bao giờ chặn chuột** hay làm gián đoạn công việc của thủ thư.

---

## 5. Bảng Điều Khiển Đóng Được (Closable Panel)

> **Nguyên lý B3:** Cho phép người dùng chuyên ngành (thủ thư, chuyên viên quản trị) đóng hoặc ẩn một phần giao diện phụ trợ khi không cần thiết, giúp giải phóng không gian màn hình và triệt tiêu sự lộn xộn thị giác (visual clutter).

```
[CHẾ ĐỘ MỞ - MASTER & DETAIL]                 [CHẾ ĐỘ ĐÓNG - FULL WORKSPACE]
┌───────────────────────┬──────────────┐      ┌──────────────────────────────────────────────┐
│ Bảng Kho Sách (72%)   │ Inspector    │      │ Bảng Kho Sách Nở Rộng Toàn Bộ Màn Hình (100%)│
│ Mã • Tên • Tác giả    │ (32%)        │ ──►  │ Mã • Tên Sách • Tác Giả • Thể Loại • Đơn Giá │
│ Sách A                │ Bìa, Kệ, Giá │ [✕]  │ Sách A                                       │
│ Sách B                │ [Mượn] [Sửa] │      │ Sách B                                       │
└───────────────────────┴──────────────┘      └──────────────────────────────────────────────┘
```

### 5.1 Thanh Inspector Kho Sách (`detailInspectorPane`)
- **Nút đóng [✕] trực tiếp trên Header:** Tại thanh tiêu đề `THÔNG TIN CHI TIẾT`, nút `[✕]` cho phép thủ thư đóng ngay thanh xem trước chỉ với 1 click.
- **Nút Toggle `[Chi tiết]` trên Toolbar:** Người dùng có thể chủ động bấm nút `[Chi tiết]` (`btnToggleInspector`) trên thanh công cụ để ẩn/hiện thanh bên cạnh bất kỳ lúc nào.
- **Giải phóng 100% diện tích làm việc:**
  - Khi đóng panel, hàm `closeInspectorPanel()` rút `detailInspectorPane` ra khỏi `bookSplitPane.getItems()`. Bảng `booksTable` hoặc lưới thẻ `booksGridPane` sẽ lập tức chiếm trọn 100% bề ngang màn hình, giúp hiển thị đầy đủ tất cả các cột mà không bị co cụm.
- **Tự động phục hồi thông minh (Auto-restore):**
  - Khi người dùng click chọn bất kỳ một cuốn sách nào trong bảng hoặc lưới, hàm `openInspectorPanel()` sẽ tự động đưa panel trở lại SplitPane một cách mượt mà và cập nhật dữ liệu cuốn sách đó tức thì.

### 5.2 Khung Bộ Lọc Nâng Cao (Advanced Filter Drawer)
- Khung lọc theo khoảng giá, tồn kho, và tác giả được ẩn mặc định.
- Khi người dùng nhấn nút `[Bộ Lọc Nâng Cao]`, khung lọc mở trượt ra ngay dưới toolbar và có thể thu gọn lại khi không dùng đến, giữ cho giao diện luôn gọn gàng và tinh tế.

---

## 6. Bảng Điều Khiển Di Chuyển & Thay Đổi Kích Thước (Movable & Resizable Panel)

> **Nguyên lý B3:** Tối ưu hóa tính linh hoạt và cá nhân hóa trải nghiệm người dùng: cho phép co giãn kích thước vùng làm việc theo nhu cầu, đảo vị trí các bảng tương tác và di chuyển các cửa sổ popup/modal để xem thông tin bị che khuất bên dưới.

```
                    ┌───────────────────────────────┐
                    │  [::: HEADER - KÉO ĐỂ DI CHUYỂN :::] [✕] │ ◄── UIUtils.makeDraggable
                    ├───────────────────────────────┤
                    │                               │
                    │  NỘI DUNG POPUP / FORM        │
                    │  (Chuột & nút bấm bên trong   │
                    │   hoạt động hoàn toàn tự do)  │
                    │                               │
                    └───────────────────────────────┘
```

### 6.1 Thanh Phân Chia Co Giãn Trực Quan (Resizable SplitPane)
- Khung chia đôi màn hình Kho Sách (`bookSplitPane`) và Bàn Lưu Hành (`deskSplitPane`) được trang bị thanh chia (Splitter Divider) chuyên dụng:
  - **Con trỏ co giãn:** Thiết lập `cursor: h-resize` khi rê chuột qua thanh chia.
  - **Phản hồi phát sáng (Hover Glow Feedback):** Khi rê chuột vào thanh phân chia, thanh sáng rực lên với màu thương hiệu (`#1DB954` ở Dark Theme, `#FB7185` ở Pink Light Theme), phát tín hiệu thị giác rõ ràng rằng người dùng có thể kéo chuột để thay đổi tỷ lệ phân chia giữa bảng danh sách và bảng chi tiết theo ý muốn.

### 6.2 Nút Đảo Vị Trí Bàn Lưu Hành (Panel Swap Feature)
- **Nhu cầu thực tế:** Thủ thư có thói quen làm việc khác nhau hoặc bố trí quầy thư viện thực tế theo chiều ngược lại (quầy mượn bên trái, màn hình máy tính bên phải).
- **Hiện thực hóa:** Nút tròn **`[Đổi bên]`** (`btnSwapDeskPanels`) sử dụng icon hai mũi tên đảo chiều SVG:
  - Khi click, hệ thống hoán đổi vị trí của 2 panel trong `deskSplitPane`: Palette (Kho sách) chuyển sang phải, Canvas (Giỏ sách & Độc giả) chuyển sang trái và ngược lại.
  - Tỷ lệ kích thước được tự động tính toán lại đối xứng, giúp thủ thư làm việc hoàn toàn thoải mái.

### 6.3 Hộp Thoại Di Chuyển Linh Hoạt (In-App Movable Modals)
- **Vấn đề của Modal truyền thống:** Cửa sổ modal cố định ở giữa màn hình thường che mất các dòng dữ liệu nền mà người dùng muốn đối chiếu.
- **Giải pháp `UIUtils.makeDraggable`:**
  - Toàn bộ 5 modal in-app của hệ thống (`authModalBox`, `profileModalBox`, `helpModalBox`, `readerBorrowModalBox`, `appNoticeModalBox`) và modal thêm/sửa sách (`bookModalBox`), thể loại (`categoryModalBox`), độc giả (`readerModalBox`) đều được gắn tính năng kéo thả di chuyển.
  - **Cơ chế an toàn không kẹt sự kiện chuột:** Sự kiện kéo chuột chỉ kích hoạt khi nhấn vào thanh tiêu đề (Header bar) và tự động bỏ qua khi click vào nút đóng `[✕]` hoặc các trường nhập liệu bên trong.
  - Khi đóng popup, tọa độ được tự động trả về vị trí cân đối ban đầu (`UIUtils.resetPosition`), đảm bảo lần mở sau luôn chuẩn xác.

---

## 7. Bảng Tổng Hợp Ánh Xạ Nguyên Lý B3 Vào Codebase LibMan

| Nguyên Lý B3 | Thành Phần Codebase Hiện Thực Hóa | File Mã Nguồn Liên Quan | Giá Trị Mang Lại Cho Người Dùng |
| :--- | :--- | :--- | :--- |
| **1. Phân cấp trực quan (Ưu tiên số 1 góc trên bên trái)** | • Tiêu đề định danh `.title-large`<br>• Thẻ KPI "Tổng Đầu Sách" vị trí #1<br>• Thanh tìm kiếm nhanh ở đầu Toolbar | `MainLayout.fxml`<br>`HomeView.fxml`<br>`BookManagementView.fxml`<br>`ReaderManagementView.fxml` | Điểm rơi thị giác tức thì, không cần tìm kiếm tiêu đề hay ô nhập liệu chính. |
| **2. Dòng trực quan người dùng (Trái ➔ Phải, Trên ➔ Dưới)** | • Chuỗi thao tác Bàn lưu hành Palette ➔ Canvas ➔ Chốt phiếu<br>• Tiến trình Wizard 4 bước tuần tự<br>• Cuộn chuột ngang dải Sách nổi bật | `BorrowReturnView.fxml`<br>`BorrowReturnController.java`<br>`HomeController.java` | Dẫn dắt hành vi mượt mà, giảm tải nhận thức, tránh thao tác nhảy cóc. |
| **3. Phân nhóm thành phần & chức năng** | • Container tiện ích phụ `.header-utility-group`<br>• Phân vùng Toolbar tìm kiếm - tiêu chí - góc nhìn<br>• Card gợi ý `.inspector-branch-card` | `BookManagementView.fxml`<br>`ReaderManagementView.fxml`<br>`style.css` | Tách biệt rõ ràng giữa hành động chính và tiện ích phụ; thông tin liên quan đặt cạnh nhau. |
| **4. Khung giao diện thị giác nhất quán** | • Cấu trúc App Shell (Header - Sidebar - Workspace)<br>• Hệ thống Design Tokens Dark & Pink-Light Theme<br>• Toast container không chặn chuột | `MainLayout.fxml`<br>`MainLayoutController.java`<br>`style.css` | Trải nghiệm đồng bộ 100% trên toàn ứng dụng, sang trọng và chuẩn thẩm mỹ hiện đại. |
| **5. Bảng điều khiển đóng được (Closable Panel)** | • Nút đóng `[✕]` và nút toggle `[Chi tiết]`<br>• Tự động nở rộng bảng 100% diện tích khi đóng<br>• Tự động mở lại khi chọn sách | `BookManagementView.fxml`<br>`BookManagementController.java` | Tối đa hóa không gian đọc dữ liệu bảng; triệt tiêu sự lộn xộn cho người dùng chuyên sâu. |
| **6. Panel di chuyển & co giãn kích thước** | • SplitPane có phản hồi phát sáng khi hover<br>• Nút `[Đổi bên]` bàn lưu hành<br>• Cửa sổ in-app kéo thả tự do `UIUtils.makeDraggable` | `UIUtils.java`<br>`BorrowReturnView.fxml`<br>`MainLayoutController.java`<br>`style.css` | Tùy biến không gian làm việc theo nhu cầu cá nhân; có thể xem thông tin bị che khuất bên dưới. |

---

## 8. Kết Luận

Việc áp dụng trọn vẹn **6 nguyên lý thiết kế tương tác và phân cấp trực quan theo tài liệu B3** đã nâng tầm hệ thống **LibMan v1.5.0** từ một phần mềm quản lý thư viện truyền thống thành một ứng dụng desktop đẳng cấp, hiện đại và chuẩn mực công thái học (Ergonomics). 

Hệ thống kết hợp hài hòa giữa:
1. **Tính cấu trúc và kỷ luật:** Dòng thị giác Trái ➔ Phải, Trên ➔ Dưới rõ ràng, định vị ưu tiên vàng ở góc trên bên trái.
2. **Tính thẩm mỹ và công nghệ:** Khung giao diện chuẩn mực, 2 Theme màu sắc đối lập thời thượng, hiệu ứng phát sáng phản hồi trực quan.
3. **Tính linh hoạt cao cấp:** Các panel có thể đóng/mở giải phóng không gian, đảo bên bàn làm việc và kéo thả cửa sổ tự do mà không bao giờ gây xung đột hay kẹt tương tác chuột.
