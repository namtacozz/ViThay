# BÁO CÁO PHÂN TÍCH BỐ CỤC & PHÂN CẤP TRỰC QUAN GIAO DIỆN

## Hệ Thống Quản Lý Thư Viện — LibMan (Phiên bản v1.5.0)

**Ngày cập nhật:** 17/10/2026  
**Căn cứ tài liệu:** Giáo trình Thiết kế Giao diện — Chuyên đề *"Cơ Bản Của Bố Cục & Các Mẫu Thiết Kế"* (Tài liệu slide bài giảng B3) + Đặc tả yêu cầu phần mềm Quản Lý Thư Viện (SRS) + Bộ kiểm thử tự động 63/63 PASS

---

## MỤC LỤC TỔNG QUAN

- [PHẦN I: CƠ SỞ LÝ THUYẾT VỀ BỐ CỤC & DÒNG TRỰC QUAN](#phần-i-cơ-sở-lý-thuyết-về-bố-cục--dòng-trực-quan)
  - [1. Bố Cục & Dòng Trực Quan Là Gì?](#1-bố-cục--dòng-trực-quan-là-gì)
  - [2. Kỹ Thuật Phân Cấp Trực Quan Trên LibMan](#2-kỹ-thuật-phân-cấp-trực-quan-trên-libman)
  - [3. Bốn Nguyên Tắc Gestalt Trong Phân Nhóm](#3-bốn-nguyên-tắc-gestalt-trong-phân-nhóm)
- [PHẦN II: 11 MẪU THIẾT KẾ BỐ CỤC CÓ SẴN TRONG ỨNG DỤNG LIBMAN](#phần-ii-11-mẫu-thiết-kế-bố-cục-có-sẵn-trong-ứng-dụng-libman)
  - [A. Nhóm Phân Cấp Trực Quan](#a-nhóm-phân-cấp-trực-quan)
    - [1. Visual Framework (Khung Giao Diện Nhất Quán)](#1-visual-framework-khung-giao-diện-nhất-quán)
    - [2. Center Stage (Sân Khấu Trung Tâm)](#2-center-stage-sân-khấu-trung-tâm)
  - [B. Nhóm Phân Nhóm (Grouping Patterns)](#b-nhóm-phân-nhóm-grouping-patterns)
    - [3. Titled Sections (Các Nhóm Có Tiêu Đề)](#3-titled-sections-các-nhóm-có-tiêu-đề)
    - [4. Card Stack / Tabs (Chồng Thẻ / Ngăn Tab)](#4-card-stack--tabs-chồng-thẻ--ngăn-tab)
    - [5. Closable Panels (Bảng Điều Khiển Đóng Được)](#5-closable-panels-bảng-điều-khiển-đóng-được)
    - [6. Movable & Resizable Panels (Bảng Di Chuyển & Co Giãn)](#6-movable--resizable-panels-bảng-di-chuyển--co-giãn)
  - [C. Nhóm Dòng Trực Quan & Canh Lề](#c-nhóm-dòng-trực-quan--canh-lề)
    - [7. Diagonal Balance (Cân Bằng Đường Chéo)](#7-diagonal-balance-cân-bằng-đường-chéo)
    - [8. Property Sheet (Bảng Thuộc Tính 2 Cột)](#8-property-sheet-bảng-thuộc-tính-2-cột)
    - [9. Right / Left Alignment (Canh Lề Trái / Phải Có Chủ Đích)](#9-right--left-alignment-canh-lề-trái--phải-có-chủ-đích)
  - [D. Nhóm Bố Cục Động (Dynamic Layout)](#d-nhóm-bố-cục-động-dynamic-layout)
    - [10. Responsive Disclosure (Tiết Lộ Lũy Tiến / Mở Rộng Ngữ Cảnh)](#10-responsive-disclosure-tiết-lộ-lũy-tiến--mở-rộng-ngữ-cảnh)
    - [11. Liquid Layout (Bố Cục Co Giãn Linh Hoạt)](#11-liquid-layout-bố-cục-co-giãn-linh-hoạt)
- [PHẦN III: GIẢI ĐÁP BÀI TẬP BẢNG TỔNG KẾT B3](#phần-iii-giải-đáp-bài-tập-bảng-tổng-kết-b3)
- [PHẦN IV: KẾT LUẬN](#phần-iv-kết-luận)

---

# PHẦN I: CƠ SỞ LÝ THUYẾT VỀ BỐ CỤC & DÒNG TRỰC QUAN

## 1. Bố Cục & Dòng Trực Quan Là Gì?

### 1.1 Bố cục (Layout) là gì?
- **Định nghĩa:** Bố cục là nghệ thuật và kỹ thuật sắp xếp các phần tử thị giác (chữ, hình ảnh, nút bấm, bảng dữ liệu) nhằm điều khiển sự chú ý của người dùng trên một trang giao diện.
- **Mục tiêu:** Giảm thiểu chi phí nhận thức (Cognitive Load), giúp người dùng định vị thông tin nhanh chóng và thực hiện thao tác nghiệp vụ một cách chính xác mà không gặp trở ngại.

### 1.2 Dòng trực quan (Visual Flow) là gì?
- **Định nghĩa:** Trình tự mắt của người dùng di chuyển và tiếp nhận nội dung trên giao diện:
  - Những điểm nhấn có độ tương phản cao, kích thước lớn sẽ thu hút sự chú ý của người dùng trước.
  - Người dùng có khuynh hướng quét nội dung theo cụm trong cùng phân nhóm trước khi chuyển sang nhóm khác.
  - Hướng quét mắt tự nhiên của con người là **từ trên xuống dưới (Top ➔ Bottom)** và **từ trái sang phải (Left ➔ Right)** theo mô hình đọc F-Pattern và Gutenberg Diagram.
  - Khi dòng trực quan thay đổi, người dùng sẽ hiểu và ghi nhớ nội dung của giao diện theo một ý nghĩa khác.

---

## 2. Kỹ Thuật Phân Cấp Trực Quan Trên LibMan

Theo giáo trình slide B3, phân cấp trực quan là cách thức làm nổi bật nội dung để thu hút sự chú ý của người dùng: nội dung/chức năng quan trọng nhất phải được đặt nổi bật nhất, nội dung ít quan trọng hơn đặt ít nổi bật hơn. Ứng dụng LibMan áp dụng đầy đủ **4 kỹ thuật phân cấp**:

```
┌─────────────────────────────────────────────────────────────────────────────┐
│ 1. Tọa độ vàng góc trên bên trái: Tiêu đề định danh & Ô tìm kiếm nhanh      │
│ 2. Kích thước (Scale): Font số KPI 20px Bold > Tiêu đề 15px > Thân 11px     │
│ 3. Hiệu ứng 3D (Elevation): Drop Shadow đổ bóng tách lớp bìa sách & modal   │
│ 4. Hiệu ứng động (Animation): Hover sáng Splitter, chuyển trang mượt mà     │
└─────────────────────────────────────────────────────────────────────────────┘
```

1. **Góc trên bên trái được ưu tiên chú ý:**
   - Điểm rơi thị giác số 1 luôn là Tiêu đề định danh màn hình (`.title-large`), tiếp theo là ô tìm kiếm tức thì `txtSearchBook` hoặc thẻ KPI chủ lực "TỔNG ĐẦU SÁCH".
2. **Kích thước to hơn bình thường (Scale & Weight Hierarchy):**
   - Các con số thống kê quan trọng (`stat-value`) được thiết kế cỡ lớn `20px - 22px Bold` với màu nhấn nổi bật (`#1DB954`, `#F59E0B`, `#38BDF8`), lớn gấp đôi nhãn chú thích (`10px - 11px muted`).
3. **Hiệu ứng 3D & Chiều sâu (Elevation & Dropshadow):**
   - Các bìa sách trên dải nổi bật và trong bảng chi tiết Inspector đều được áp dụng đổ bóng `dropshadow(three-pass-box, rgba(0,0,0,0.6), 14, 0, 0, 4)`.
   - Các cửa sổ Popup Modal và Thẻ Toast được nâng cao khỏi mặt phẳng nền bằng lớp phủ kính mờ `GaussianBlur(14)`.
4. **Hiệu ứng động (Micro-interactions & Animations):**
   - Thanh chia `SplitPane` sáng rực lên (`#1DB954` hoặc `#FB7185`) khi rê chuột; các nút hành động đổi độ sáng tức thì khi hover (`1.05x scale`).

---

## 3. Bốn Nguyên Tắc Gestalt Trong Phân Nhóm

Theo slide 4 của giáo trình B3, người dùng nhận biết các nhóm nội dung thông qua 4 quy luật tự nhiên của thị giác:

1. **Đặt gần nhau (Proximity):** Các trường nhập liệu liên quan (Mã sách, Tên sách, Tác giả) được xếp sát nhau; các nút điều khiển của cùng một tác vụ (Lưu, Hủy) được đặt cạnh nhau.
2. **Tương tự nhau (Similarity):** Các thẻ sách trong kho có cùng kích cỡ, cùng kiểu bo góc `8px`, cùng kiểu chữ và vị trí đặt badge trạng thái cố định ở góc dưới.
3. **Sự liên tục (Continuity):** Quy trình mượn sách wizard dẫn dắt mắt người dùng chạy dọc theo dải thanh tiến trình từ bước 1 sang bước 4 một cách tự nhiên.
4. **Sự đóng kín (Closure):** Các card `.stat-card`, `.bg-surface`, `.inspector-branch-card` sử dụng viền mờ (`border 1px #2F2F2F`) bao bọc trọn vẹn nội dung, giúp não bộ nhận biết đây là một thực thể thông tin độc lập.

---

# PHẦN II: 11 MẪU THIẾT KẾ BỐ CỤC CÓ SẴN TRONG ỨNG DỤNG LIBMAN

Dưới đây là bảng phân tích chi tiết toàn bộ **11 Mẫu Thiết Kế Bố Cục** (Interaction Layout Patterns) theo đúng phân loại của Slide 8 giáo trình B3 và hiện thực hóa thực tế 100% trong mã nguồn của LibMan.

---

## A. Nhóm Phân Cấp Trực Quan

### 1. Visual Framework (Khung Giao Diện Nhất Quán)
> **Mô tả giáo trình (Slide 8–10):** Sử dụng cùng một bố cục căn bản cho tất cả các trang. Màu sắc, font chữ và cách bố trí đồng nhất giúp người dùng không tốn chi phí học lại giao diện khi chuyển ngữ cảnh, làm nổi bật phần nội dung quan trọng.

```
┌─────────────────────────────────────────────────────────────────────────────┐
│ [LOGO] LIBMAN   │ GLOBAL SEARCH (Ctrl+K)       │ THEME │ ALERTS │ USER PROFILE│ (Top Header 56px)
├─────────────────┼──────────────────────────────┴───────┴────────┴─────────────┤
│ ⌂ Trang chủ     │                                                             │
│ 📖 Kho sách     │                                                             │
│ 🗂 Thể loại     │                WORKSPACE HIỂN THỊ CHÍNH                     │
│ 👥 Độc giả      │            (Padding chuẩn hoá: 24px 32px 32px)              │ (Sidebar 64px
│ ⇄ Lưu hành      │                                                             │  hoặc 250px)
│ 📊 Thống kê     │                                                             │
│ 🗑 Thùng rác    │                                                             │
│ ⚙ Cài đặt       │                                                             │
└─────────────────┴─────────────────────────────────────────────────────────────┘
```

- **Hiện thực hóa trong LibMan:**
  - File FXML cốt lõi: [`MainLayout.fxml`](file:///f:/AppMaking/ViThay/src/main/resources/com/vithay/libman/view/MainLayout.fxml).
  - Cấu trúc **App Shell 3 vùng cố định** xuất hiện trên mọi phân hệ:
    1. *Thanh tiêu đề trên cùng (Top Header):* Cố định logo ở góc trên bên trái, thanh tìm kiếm toàn cục ở giữa, nút chuyển giao diện và profile ở bên phải.
    2. *Thanh điều hướng dọc (Vertical Sidebar):* Đặt cố định bên trái, menu phân tầng rõ rệt với icon SVG đồng nhất.
    3. *Khu vực làm việc trung tâm (Center Workspace):* Khung hiển thị nội dung luôn duy trì khoảng đệm chuẩn mực `Insets(24, 32, 32, 32)`.
  - **Hệ thống Design Tokens 2 Theme:** Cấu hình tập trung trong [`style.css`](file:///f:/AppMaking/ViThay/src/main/resources/com/vithay/libman/css/style.css) với quy tắc font chữ (Inter/Segoe UI) và bảng màu đồng bộ (Spotify Dark Mode `#121212` và Pink Light Mode `#FFF5F7`).

---

### 2. Center Stage (Sân Khấu Trung Tâm)
> **Mô tả giáo trình (Slide 11–13):** Đặt phần nội dung quan trọng nhất vào vùng lớn nhất ở trung tâm của giao diện (kích thước ít nhất gấp đôi phần xung quanh, màu sắc tương phản, tiêu đề làm điểm nhấn). Các chức năng phụ trợ đặt vào các phần nhỏ hơn xung quanh.

```
┌─────────────────────────────────────────────────────────────────────────────┐
│ Header: Bộ lọc tác giả, thể loại, tìm kiếm nhanh                           │
├───────────────────────────────────────────────────────┬─────────────────────┤
│                                                       │                     │
│               SÂN KHẤU TRUNG TÂM (72%)                │   VÙNG BIÊN (28%)   │
│         Bảng Danh Sách Kho Sách (Master Table)        │   Bảng Inspector    │
│                                                       │                     │
│  Mã   Tên Sách          Tác Giả    Thể Loại   Giá     │  Ảnh bìa xem trước  │
│  BK01 Doraemon          Fujiko     Truyện     25.000  │  Vị trí trên kệ     │
│  BK02 Lập Trình Java    James G.   CNTT       120.000 │  Tình trạng tồn     │
│  BK03 Đồi Gió Hú        Emily B.   Văn Học    95.000  │  [Mượn] [Sửa] [Xóa] │
│                                                       │                     │
└───────────────────────────────────────────────────────┴─────────────────────┘
```

- **Hiện thực hóa trong LibMan:**
  - **Tại Kho Sách ([`BookManagementView.fxml`](file:///f:/AppMaking/ViThay/src/main/resources/com/vithay/libman/view/BookManagementView.fxml)):** Bảng danh sách sách (`booksTable` / `booksGridPane`) đóng vai trò là "Sân khấu trung tâm" chiếm **72% diện tích màn hình**, được bao quanh bởi thanh công cụ lọc ở trên và thanh Inspector phụ trợ (28%) ở bên phải.
  - **Tại Trang Chủ ([`HomeView.fxml`](file:///f:/AppMaking/ViThay/src/main/resources/com/vithay/libman/view/HomeView.fxml)):** Bảng danh sách các giao dịch mượn sách gần đây nhất (`transactionsTable`) chiếm toàn bộ nửa dưới trung tâm với tỷ lệ chiều cao áp đảo.
  - **Tại Giao diện Chi Tiết Toàn Diện (`fullBookDetailOverlay`):** Cuốn sách được chọn được đưa vào giữa màn hình với ảnh bìa khổ lớn 180x250px và khung tóm tắt nội dung hoành tráng, triệt tiêu mọi phân tâm xung quanh.

---

## B. Nhóm Phân Nhóm (Grouping Patterns)

### 3. Titled Sections (Các Nhóm Có Tiêu Đề)
> **Mô tả giáo trình (Slide 14–15):** Xác định mỗi nhóm nội dung trên giao diện bằng một tiêu đề trực quan. Sử dụng khi có nhiều nội dung trên một trang/cửa sổ để giúp người dùng dễ đọc, dễ hiểu và dễ tìm kiếm thông tin.

```
┌─────────────────────────────────────────────────────────────────────────────┐
│ ✦ QUY ĐỊNH MƯỢN SÁCH (Titled Section 1)                                     │
│   Số sách mượn tối đa:   [ 5 ] cuốn          Thời hạn mượn tại chỗ: [ 1 ]   │
├─────────────────────────────────────────────────────────────────────────────┤
│ ✦ QUY ĐỊNH PHẠT QUÁ HẠN & BỒI THƯỜNG (Titled Section 2)                     │
│   Tiền phạt trễ hạn:     [ 2,000 ] đ/ngày    Phí bồi hoàn sách mất: [ 200 ]%│
├─────────────────────────────────────────────────────────────────────────────┤
│ ✦ QUY ĐỊNH HỘI VIÊN & ĐỘC GIẢ (Titled Section 3)                            │
│   Thời hạn hiệu lực thẻ: [ 12 ] tháng        Độ tuổi độc giả tối thiểu: [6] │
└─────────────────────────────────────────────────────────────────────────────┘
```

- **Hiện thực hóa trong LibMan:**
  - **Màn hình Cài Đặt Quy Định ([`SettingsView.fxml`](file:///f:/AppMaking/ViThay/src/main/resources/com/vithay/libman/view/SettingsView.fxml)):** Toàn bộ các tham số nghiệp vụ được phân tách rõ ràng bằng các tiêu đề nhóm:
    1. *QUY ĐỊNH MƯỢN SÁCH:* Số ngày mượn, hạn ngạch số sách.
    2. *QUY ĐỊNH PHẠT & BỒI THƯỜNG:* Đơn giá phạt trễ, tỷ lệ đền bù sách mất.
    3. *QUY ĐỊNH HỘI VIÊN:* Hạn thẻ độc giả.
  - **Thanh Inspector Sách (`detailInspectorPane`):** Được chia thành các mục có đề mục riêng biệt:
    - `THÔNG TIN CHI TIẾT`
    - `VỊ TRÍ LƯU TRỮ TRÊN KỆ`
    - `NHÁNH LIÊN QUAN & GỢI Ý`
  - **Cửa sổ thêm sách mới (`bookModalBox`):** Phân nhóm tiêu đề thành khối `Ảnh Bìa Sách` và khối `Thông Tin Chi Tiết Sách`.

---

### 4. Card Stack / Tabs (Chồng Thẻ / Ngăn Tab)
> **Mô tả giáo trình (Slide 16–17):** Đặt các nội dung liên quan với nhau vào trong từng Tab độc lập. Giúp tổ chức nhiều nội dung vào một không gian màn hình giới hạn mà không làm dài trang. Nguyên tắc: không nên làm quá 6 tabs.

```
┌─────────────────────────────────────────────────────────────────────────────┐
│  [ [BÀN LƯU HÀNH] ]   [ LỊCH SỬ GIAO DỊCH ]   [ BÁO CÁO MƯỢN TRẢ ]          │ (Card Stack)
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│                    NỘI DUNG CỦA TAB ĐANG ĐƯỢC CHỌN                          │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

- **Hiện thực hóa trong LibMan:**
  - **Tại Bàn Lưu Hành ([`BorrowReturnView.fxml`](file:///f:/AppMaking/ViThay/src/main/resources/com/vithay/libman/view/BorrowReturnView.fxml)):** Sử dụng component `TabPane fx:id="borrowTabPane"` để xếp chồng các không gian nghiệp vụ:
    - *Tab 1: Bàn Lưu Hành Tiêu Chuẩn (Desk):* Xử lý quét sách, chọn độc giả và cho mượn trực tiếp.
    - *Tab 2: Lịch Sử Phiếu Mượn:* Tra cứu danh sách các phiếu mượn phát sinh, phân loại Đang mượn / Đã trả / Quá hạn.
  - **Tại Cửa sổ Chi Tiết Sách Toàn Diện:** Xếp chồng 2 tab chức năng:
    - *Tab 1: Tóm Tắt & Mô Tả Chi Tiết Cuốn Sách.*
    - *Tab 2: Cộng Đồng Thảo Luận & Đánh Giá Của Độc Giả.*

---

### 5. Closable Panels (Bảng Điều Khiển Đóng Được)
> **Mô tả giáo trình (Slide 18–20):** Đặt các nội dung liên quan vào các panel riêng biệt có khả năng đóng/ẩn. Cho phép người dùng chuyên gia xem nhiều nhóm nội dung cùng lúc nhưng có thể thu gọn lại khi cần tiết kiệm diện tích.

```
[KHI PANEL ĐANG MỞ]                              [KHI NHẤN ĐÓNG [✕]]
┌───────────────────────┬──────────────┐         ┌───────────────────────────────────────────┐
│ Bảng Kho Sách (72%)   │ Inspector    │         │ Bảng Kho Sách Chiếm Trọn Màn Hình (100%)  │
│ Sách 1                │ Chi tiết...  │   ──►   │ Sách 1                                    │
│ Sách 2                │ [Mượn] [✕]   │         │ Sách 2                                    │
└───────────────────────┴──────────────┘         └───────────────────────────────────────────┘
```

- **Hiện thực hóa trong LibMan:**
  - **Thanh Inspector Kho Sách (`detailInspectorPane`):**
    - Trang bị nút đóng nhanh **`[✕]`** ngay trên header và nút toggle **`[Chi tiết]`** trên thanh công cụ.
    - Khi đóng: Hàm `closeInspectorPanel()` rút panel ra khỏi SplitPane, bảng dữ liệu lập tức nở rộng chiếm **100% bề ngang**, hiển thị đầy đủ mọi cột mà không bị cắt chữ.
    - Khi người dùng click vào bất kỳ cuốn sách nào, hàm `openInspectorPanel()` tự động đưa panel trở lại vị trí cũ một cách mượt mà.
  - **Ngăn Kéo Bộ Lọc Nâng Cao (Advanced Filter Drawer):**
    - Khung lọc theo khoảng giá, tồn kho và tác giả có thể thu gọn hoặc mở ra bằng nút `[Bộ lọc nâng cao]`.

---

### 6. Movable & Resizable Panels (Bảng Di Chuyển & Co Giãn)
> **Mô tả giáo trình (Slide 20–22):** Người dùng có thể di chuyển (Move) và thay đổi kích thước (Resize) panel để tùy biến không gian làm việc theo thói quen và nhu cầu cá nhân.

```
       ┌───────────────────────────────┐
       │ [::: HEADER - KÉO ĐỂ DI CHUYỂN :::] [✕] │ ◄── UIUtils.makeDraggable
       ├───────────────────────────────┤
       │ Form dữ liệu bên trong        │
       │ (Chuột click bình thường)     │
       └───────────────────────────────┘
```

- **Hiện thực hóa trong LibMan:**
  - **Thanh chia co giãn kích thước (Resizable Splitter):** Khung `bookSplitPane` và `deskSplitPane` cho phép thủ thư kéo thanh ngăn cách sang trái hoặc phải (`cursor: h-resize`) để tăng/giảm tỷ lệ giữa bảng chính và vùng phụ trợ. Khi rê chuột vào thanh chia, hiệu ứng ánh sáng phát quang (Glow) kích hoạt tức thì.
  - **Tính năng Đổi Bên (Panel Swap):** Tại Bàn lưu hành, nút **`[Đổi bên]`** (`btnSwapDeskPanels`) cho phép hoán đổi vị trí giữa Palette (Kho sách) và Canvas (Giỏ mượn) từ Trái sang Phải và ngược lại.
  - **Cửa sổ Popup di chuyển tự do (Draggable Modals):** Sử dụng tiện ích [`UIUtils.makeDraggable`](file:///f:/AppMaking/ViThay/src/main/java/com/vithay/libman/util/UIUtils.java) gắn lên thanh Header của toàn bộ các popup (`authModalBox`, `profileModalBox`, `helpModalBox`, `readerBorrowModalBox`, `appNoticeModalBox`, `bookModalBox`). Người dùng có thể kéo cửa sổ sang góc khác để đối chiếu dữ liệu nền bị che khuất.

---

## C. Nhóm Dòng Trực Quan & Canh Lề

### 7. Diagonal Balance (Cân Bằng Đường Chéo)
> **Mô tả giáo trình (Slide 23–25):** Cân bằng trang/cửa sổ bằng cách đặt các thành phần định danh chính (Title/Header) vào góc trên bên trái, và các liên kết/nút bấm hành động (Action Buttons/Links) vào góc dưới bên phải. Dẫn dắt mắt người dùng quét dọc theo đường chéo tự nhiên.

```
┌─────────────────────────────────────────────────────────────────────────────┐
│ [★ Tiêu Đề / Định Danh / Nhận Diện] (Góc trên bên trái)                     │
│                                                                             │
│                               ╲                                             │
│                                ╲  Đường quét thị giác chéo                  │
│                                 ╲ (Diagonal Visual Flow)                    │
│                                  ╲                                          │
│                                                                             │
│                           [Hủy Bỏ / Đóng]  [★ NÚT HÀNH ĐỘNG CHÍNH / XÁC NHẬN]│
│                                            (Góc dưới bên phải)              │
└─────────────────────────────────────────────────────────────────────────────┘
```

- **Hiện thực hóa trong LibMan:**
  - **Tại tất cả các Modal Dialog & Form:**
    - Cửa sổ Đăng nhập: Tiêu đề `"Đăng Nhập LibMan"` ở góc trên bên trái ➔ Nút `"Đóng"` và `"Đăng Nhập Ngay"` ở góc dưới bên phải.
    - Cửa sổ Lập phiếu mượn độc giả: Tiêu đề `"Đăng Ký Mượn Sách"` ở góc trên bên trái ➔ Nút `"Xác Nhận Lập Phiếu Mượn"` ở góc dưới bên phải.
    - Hộp thoại Thông báo In-App (`appNoticeModalBox`): Tiêu đề và icon ở góc trên bên trái ➔ Cặp nút `"Hủy Bỏ"` và `"Đồng Ý"` ở góc dưới bên phải.
  - **Tại Bàn Lưu Hành Mượn Trả:** Thông tin độc giả và kho sách ở phía trên bên trái ➔ Nút chốt phiên `"Xác nhận cho mượn"` đặt ở góc dưới cùng bên phải.

---

### 8. Property Sheet (Bảng Thuộc Tính 2 Cột)
> **Mô tả giáo trình (Slide 25–26):** Sử dụng một bố cục 2 cột để cho phép người dùng xem hoặc chỉnh sửa thuộc tính của một đối tượng có nhiều thông số. Cột 1 là tên thuộc tính (Label), cột 2 là giá trị hoặc điều khiển nhập liệu (Value/Editor).

```
┌─────────────────────────────────────────────────────────────────────────────┐
│ CỘT 1: TÊN THUỘC TÍNH (Label)         │ CỘT 2: GIÁ TRỊ / ĐIỀU KHIỂN NHẬP    │
├───────────────────────────────────────┼─────────────────────────────────────┤
│ Mã Sách:                              │ BK0012                              │
│ Mã ISBN:                              │ 978-604-0-12345-6                   │
│ Đơn Giá:                              │ 125,000 đ                           │
│ Tồn Kho Khả Dụng:                     │ 4 / 5 cuốn                          │
│ Vị Trí Kệ:                            │ Khu A - Kệ 01                       │
└───────────────────────────────────────┴─────────────────────────────────────┘
```

- **Hiện thực hóa trong LibMan:**
  - **Trong Inspector Sách (`BookManagementView.fxml`):** Vùng metadata được tổ chức bằng `GridPane` 2 cột chính xác:
    - *Cột 1 (`minWidth="80"`):* `Mã Sách:`, `Mã ISBN:`, `Đơn Giá:`, `Khả Dụng:`.
    - *Cột 2 (`hgrow="ALWAYS"`):* Các nhãn giá trị tương ứng (`lblFullDetailId`, `lblFullDetailIsbn`, `lblFullDetailPrice`, `lblFullDetailCopies`).
  - **Trong Màn hình Cài Đặt Quy Định (`SettingsView.fxml`):** Bố cục 2 cột hoàn chỉnh:
    - Cột 1: Tên quy định ("Số ngày mượn tối đa", "Tiền phạt trễ hạn mỗi ngày").
    - Cột 2: Spinner điều chỉnh số lượng hoặc TextField nhập đơn giá.
  - **Trong Modal Thêm/Sửa Sách:** Bố cục `GridPane` 2 cột (`ColumnConstraints 140px : 240px`) gồm tên thuộc tính bên trái và ô nhập liệu bên phải.

---

### 9. Right / Left Alignment (Canh Lề Trái / Phải Có Chủ Đích)
> **Mô tả giáo trình (Slide 7):** Canh lề có chủ đích tạo ra các đường gióng trực quan (Visual Anchors) giúp giao diện gọn gàng, tăng tốc độ đọc và so sánh dữ liệu.

```
┌─────────────────────────────────────────────────────────────────────────────┐
│ [Canh Trái (Left)]                    │ [Canh Phải (Right)]                 │
│ Tiêu đề, tên sách, tác giả            │ Đơn giá tiền tệ, số lượng tồn kho   │
│ ------------------------------------- │ ----------------------------------- │
│ Lập Trình Java Căn Bản                │                           120.000 đ │
│ Cấu Trúc Dữ Liệu & Giải Thuật         │                            95.000 đ │
│ Nhập Môn Trí Tuệ Nhân Tạo             │                           150.000 đ │
└─────────────────────────────────────────────────────────────────────────────┘
```

- **Hiện thực hóa trong LibMan:**
  - **Canh lề bảng dữ liệu (`booksTable`):**
    - Các cột văn bản (Mã sách, Tên sách, Tác giả, Thể loại) được canh **lề trái (Left)** giúp mắt đọc thuận theo tự nhiên.
    - Cột đơn giá (`colBookPrice`) được format và canh **lề phải (Right)**, giúp người dùng so sánh độ lớn của các con số một cách nhanh chóng theo hàng đơn vị.
  - **Canh lề nhãn Form:** Tên các trường nhãn trong form được gióng thẳng hàng từ trên xuống dưới, tạo đường thẳng thị giác mạch lạc.

---

## D. Nhóm Bố Cục Động (Dynamic Layout)

### 10. Responsive Disclosure (Tiết Lộ Lũy Tiến / Mở Rộng Ngữ Cảnh)
> **Mô tả giáo trình (Slide 27–29):** Đầu tiên hiển thị giao diện ở mức độ đơn giản. Chỉ hiển thị thêm các thành phần khác khi người dùng thực hiện một số thao tác nhất định. Giúp người dùng tập trung vào bước đầu tiên, dễ hiểu hơn do không bị chuyển đổi ngữ cảnh đột ngột như trong mô hình Wizard nhiều trang.

```
[BƯỚC 1: GIAO DIỆN HỒ SƠ ĐƠN GIẢN]
┌─────────────────────────────────────────────────────────────────────────────┐
│ Họ và tên:  [ Nguyễn Văn A                  ]                               │
│ Email:      [ nguyenvana@gmail.com          ]                               │
│ [🔑 Đổi Mật Khẩu ▾] (Nút kích hoạt Responsive Disclosure)                   │
└─────────────────────────────────────────────────────────────────────────────┘
                                  │
                                  ▼ Khi người dùng nhấn nút Đổi mật khẩu
[BƯỚC 2: MỞ RỘNG TRỰC TIẾP TRONG CÙNG CỬA SỔ - KHÔNG ĐỔI TRANG]
┌─────────────────────────────────────────────────────────────────────────────┐
│ Họ và tên:  [ Nguyễn Văn A                  ]                               │
│ Email:      [ nguyenvana@gmail.com          ]                               │
│ [🔑 Đổi Mật Khẩu ▴]                                                         │
│ ┌─────────────────────────────────────────────────────────────────────────┐ │
│ │ Mật khẩu hiện tại: [ •••••••• ]                                         │ │
│ │ Mật khẩu mới:      [ •••••••• ]                                         │ │
│ │ Xác nhận:          [ •••••••• ]                                         │ │
│ └─────────────────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────────────────┘
```

- **Hiện thực hóa trong LibMan:**
  - **Tính năng Đổi Mật Khẩu trong Cửa Sổ Cá Nhân ([`MainLayoutController.java`](file:///f:/AppMaking/ViThay/src/main/java/com/vithay/libman/controller/MainLayoutController.java)):**
    - Cửa sổ thông tin tài khoản ban đầu chỉ hiển thị các trường cơ bản (Họ tên, Email, SĐT).
    - Khung đổi mật khẩu (`passwordChangeBox`) mặc định ở trạng thái `visible="false" managed="false"`.
    - Khi người dùng click nút `[🔑 Đổi mật khẩu ▾]`, hàm `handleToggleChangePassword()` lập tức mở rộng form nhập mật khẩu cũ/mới ngay bên dưới trong cùng một cửa sổ mà không cần chuyển sang màn hình khác.
  - **Khung Bộ Lọc Nâng Cao tại Kho Sách:**
    - Mặc định chỉ có ô tìm kiếm và chọn thể loại đơn giản.
    - Khi click `[Bộ lọc nâng cao]`, khung chọn khoảng giá và tình trạng tồn kho mở trượt ra ngay tại chỗ.
  - **Thông tin độc giả tại Bàn Lưu Hành:**
    - Ban đầu vùng thông tin độc giả hiển thị gọn. Ngay khi người dùng chọn một độc giả từ ComboBox, toàn bộ thẻ trạng thái (Hạn thẻ, số sách đang mượn, nợ phạt) được bộc lộ đầy đủ tức thời.

---

### 11. Liquid Layout (Bố Cục Co Giãn Linh Hoạt)
> **Mô tả giáo trình (Slide 7):** Bố cục tự động co giãn linh hoạt theo tỷ lệ và kích thước thực tế của cửa sổ/màn hình thiết bị, đảm bảo không gian hiển thị luôn được lấp đầy tối ưu và không bị vỡ bố cục khi thay đổi kích thước cửa sổ.

```
[MÀN HÌNH LAPTOP COMPACT 1366x768]             [MÀN HÌNH MÁY BÀN FULL HD 1920x1080]
┌────────────────────────────────┐             ┌──────────────────────────────────────────────┐
│ [Sidebar 64px] [Table tự co]   │    ──►      │ [Sidebar 250px] [Table nở rộng toàn bộ diện  │
│                [Inspector 260] │             │                  tích với 8 cột rộng thoáng] │
└────────────────────────────────┘             └──────────────────────────────────────────────┘
```

- **Hiện thực hóa trong LibMan:**
  - **Khả năng tự động co giãn đa độ phân giải ([`LibManApp.java`](file:///f:/AppMaking/ViThay/src/main/java/com/vithay/libman/LibManApp.java)):**
    - Ứng dụng tự động đo kích thước màn hình hiển thị (`Screen.getPrimary().getVisualBounds()`) và tính toán tỷ lệ cửa sổ khởi tạo (`95% screenW`, `94% screenH`).
    - Nếu chạy trên màn hình laptop nhỏ (<= 1366x800), ứng dụng tự động tối đa hóa cửa sổ (`primaryStage.setMaximized(true)`).
  - **Cơ chế JavaFX Liquid Layout:**
    - Sử dụng triệt để `HBox.hgrow="ALWAYS"`, `VBox.vgrow="ALWAYS"` và `Priority.ALWAYS` trên toàn bộ các container chính.
    - Bảng dữ liệu thiết lập chính sách `TableView.CONSTRAINED_RESIZE_POLICY`: Khi người dùng phóng to thu nhỏ cửa sổ, các cột trong bảng tự động giãn nở hoặc thu hẹp tỷ lệ mà không bao giờ sinh ra thanh cuộn ngang khó chịu.

---

# PHẦN III: GIẢI ĐÁP BÀI TẬP BẢNG TỔNG KẾT B3

Đối chiếu trực tiếp với 4 câu hỏi bài tập tại **Slide 30 của giáo trình B3**:

### 1. Chỉ ra các phân cấp trực quan được áp dụng, tại sao?
- **Vị trí góc trên bên trái:** Áp dụng cho Tiêu đề màn hình và Ô tìm kiếm nhanh vì đây là điểm rơi thị giác đầu tiên theo bản năng đọc F-pattern.
- **Kích thước & Trọng lượng chữ:** Số liệu KPI dùng cỡ `20px Bold` để đập vào mắt người dùng đầu tiên; nhãn phụ dùng `11px muted`.
- **Hiệu ứng 3D & Chiều sâu:** Ảnh bìa sách dùng dropshadow đổ bóng; popup dùng nền mờ Gaussian Blur để tách biệt tầng nội dung thao tác với tầng dữ liệu nền.
- **Hiệu ứng phát quang hover:** Thanh phân chia SplitPane sáng rực khi rê chuột để báo hiệu khả năng tương tác.

### 2. Chỉ ra các phân nhóm được áp dụng, tại sao?
- **Theo nguyên lý Proximity & Gestalt:** Gom nhóm các nút công cụ phụ trợ (`[Xuất]`, `[Làm Mới]`) vào `.header-utility-group` để phân biệt với nút tạo mới chính `[+ Thêm]`.
- **Theo Titled Sections:** Nhóm các thông số cài đặt thành 3 cụm có tiêu đề rõ ràng giúp thủ thư tìm kiếm quy định trong vài giây.
- **Theo Card Stack (Tabs):** Phân chia Bàn lưu hành và Lịch sử giao dịch thành 2 tab để tránh quá tải thông tin trên một màn hình.

### 3. Xác định dòng trực quan phổ biến nhất trên phần mềm này
- **Dòng trực quan kết hợp Top-to-Bottom và Left-to-Right (Diagonal Balance):**
  - Mắt người dùng tiếp cận từ **Tiêu đề góc trên bên trái** ➔ di chuyển xuống **vùng dữ liệu trung tâm** (chọn sách/nhập form) ➔ kết thúc tại **nút hành động chốt phiên ở góc dưới bên phải**.
  - Đây là luồng thị giác phổ biến và ổn định nhất trên toàn bộ các cửa sổ của LibMan.

### 4. Chỉ ra các mẫu thiết kế được áp dụng (từ 7 trở lên)
Hệ thống LibMan đã hiện thực hóa trọn vẹn **11 mẫu thiết kế** (vượt xa yêu cầu tối thiểu 7 mẫu):
1. **Visual Framework**
2. **Center Stage**
3. **Titled Sections**
4. **Card Stack (Tabs)**
5. **Closable Panels**
6. **Movable Panels**
7. **Diagonal Balance**
8. **Property Sheet**
9. **Right/Left Alignment**
10. **Responsive Disclosure**
11. **Liquid Layout**

---

# PHẦN IV: KẾT LUẬN

Báo cáo đã chứng minh việc tổ chức bố cục và phân cấp trực quan trên hệ thống **LibMan v1.5.0** hoàn toàn tuân thủ các nguyên lý khoa học về tương tác người - máy (HCI) được trình bày trong giáo trình B3. 

Toàn bộ 11 mẫu thiết kế đều được xây dựng dựa trên mã nguồn thực tế đang hoạt động ổn định trong ứng dụng, mang lại trải nghiệm làm việc năng suất cao cho thủ thư và cảm giác thân thiện, dễ sử dụng cho bạn đọc.
