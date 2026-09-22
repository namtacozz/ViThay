# Đặc Tả Thiết Kế: Cải Tiến Cấu Trúc Thông Tin & Trải Nghiệm Tương Tác (LibMan)

**Tài liệu:** `docs/superpowers/specs/2026-09-22-ia-ux-patterns-redesign.md`  
**Dự án:** LibMan - Hệ Thống Quản Lý Thư Viện Desktop (JavaFX 21 + SQLite)  
**Ngày lập:** 22/09/2026  
**Trạng thái:** Dự thảo hoàn chỉnh chờ phê duyệt  

---

## 1. Tổng Quan & Mục Tiêu

Ứng dụng **LibMan Desktop (v1.0.0)** hiện tại đã đáp ứng đầy đủ các quy chuẩn nghiệp vụ thư viện theo tài liệu đặc tả SRS (8 trang). Tuy nhiên, giao diện vận hành vẫn sử dụng kiến trúc phẳng truyền thống: phụ thuộc lớn vào bảng dữ liệu đơn điệu (`TableView`) và các hộp thoại bật lên che khuất màn hình (`StackPane Overlay Modals`).

Mục tiêu của thiết kế này là áp dụng **7 mẫu thiết kế tương tác kinh điển** từ tác phẩm *Designing Interfaces* (Jenifer Tidwell) vào 2 phân hệ nghiệp vụ cốt lõi:
1. **Kho Sách & Khám Phá Danh Mục (Book Catalog & Inventory)**
2. **Lưu Hành Mượn - Trả Sách (Circulation Desk & Transactions)**

### 7 Design Patterns Được Áp Dụng:
- **Cấu trúc vật lý & thông tin:** *Two-Panel Selector*, *Canvas Plus Palette*, *One-Window Drilldown*, *Alternative Views*.
- **Điều hướng & tinh giản quy trình:** *Wizard*, *Extras on Demand*, *Intriguing Branches*.

---

## 2. Kiến Trúc Tổng Thể: Composable In-Place Views

Thay vì mở thêm nhiều cửa sổ con hoặc làm phình to Sidebar menu, chúng tôi áp dụng kiến trúc **Khung nhìn Hợp nhất & Phân rã Component (Composable In-Place Views)**:
- Không bao giờ ngắt quãng dòng làm việc (flow) của thủ thư và độc giả.
- Mọi thao tác đào sâu dữ liệu (drill-down) và kiểm tra chi tiết (inspector) đều diễn ra trong cùng một khung cửa sổ với hiệu ứng trượt mượt mà và thanh điều hướng bánh mì (*Breadcrumbs*).
- Tương thích 100% với hệ cơ sở dữ liệu SQLite và các Service hiện có (`BookService`, `BorrowService`, `ReaderService`, `SettingService`).

---

## 3. Đặc Tả Chi Tiết Phân Hệ 1: Kho Sách & Danh Mục (Book Catalog)

### 3.1. Alternative Views (Đa Dạng Chế Độ Xem)
- **Vị trí:** Thanh công cụ trên cùng của `BookManagementView`.
- **Cơ chế:** Cung cấp bộ nút chuyển đổi chế độ xem nhanh:
  1. **Table View (Mặc định cho thủ thư):** Giữ nguyên bảng dữ liệu chi tiết hiện có (Mã sách, Tên sách, Tác giả, Thể loại, Vị trí kệ, Đơn giá, Khả dụng, Trạng thái, Thao tác). Hỗ trợ sắp xếp theo cột và kiểm kê nhanh.
  2. **Grid / Card View (Trực quan phong cách Spotify):** Bố cục thẻ ảnh bìa bo góc (`FlowPane`), hiển thị ảnh bìa từ thư mục `images/`, tựa sách, tác giả, và các pill badge trạng thái:
     - *Xanh lá (`#1DB954`):* Khả dụng (Sẵn sàng mượn).
     - *Vàng cam (`#F59E0B`):* Đang được mượn (Còn 0 bản tại chỗ).
     - *Đỏ (`#EF4444`):* Hư hỏng / Đang bảo trì.
- **Lưu trạng thái:** Chế độ xem được ghi nhớ xuyên suốt phiên làm việc của người dùng.

### 3.2. Two-Panel Selector (Bộ Chọn Hai Bảng Song Hành)
- **Vị trí:** Không gian làm việc chính của `BookManagementView`, sử dụng `SplitPane` (tỷ lệ 65% danh sách bên trái - 35% chi tiết bên phải).
- **Cơ chế:**
  - Khi nhấp vào bất kỳ cuốn sách nào (ở cả chế độ Table lẫn Grid), panel bên phải (**Detail Inspector**) sẽ mở ra ngay lập tức mà không che khuất danh mục.
  - Người dùng có thể dùng phím mũi tên `↑` `↓` trên bàn phím để duyệt qua hàng loạt đầu sách; Panel chi tiết cập nhật tức thì.
  - **Nội dung Panel Chi Tiết:**
    - Bìa sách kích thước lớn, tựa đề, tên tác giả, mã định danh `MaSach`.
    - Thông số vật lý: Thể loại, Vị trí khu/kệ (VD: `Khu TN - Kệ 01`), Mã ISBN, Giá bìa, Số lượng bản in còn trong kho (`conLai / tongSo`).
    - Nút thao tác nhanh: `⚡ Lập Phiếu Mượn Cuốn Này`, `✏ Sửa Thông Tin`, `🗑 Chuyển Vào Thùng Rác`.

### 3.3. One-Window Drilldown (Đào Sâu Thông Tin Trong Cùng Cửa Sổ)
- **Thanh Breadcrumb Navigation:** Đặt tại đỉnh màn hình: `Kho Sách > [Thể loại: Văn học] > [Tác giả: Nam Cao] > [Sách: Chí Phèo]`.
- **Luồng đào sâu (Drilldown Flow):**
  - Trong Panel Detail, nhấp vào tên Tác giả ➔ Lọc ngay toàn bộ tác phẩm của tác giả đó trong cùng cửa sổ.
  - Nhấp vào *"Lịch sử lưu hành cuốn sách này"* ➔ Vùng danh sách trượt mượt mà sang hiển thị bảng lịch sử độc giả đã mượn đầu sách này (lấy từ `BorrowTransactionDao`).
  - Thanh Breadcrumb cho phép quay trở lại cấp trước chỉ với một cú nhấp chuột. Không bao giờ phát sinh cửa sổ bật lên.

### 3.4. Extras on Demand (Thông Tin Phụ Trợ Theo Nhu Cầu)
- **Thanh lọc cơ bản:** Chỉ hiển thị ô tìm kiếm tức thì và bộ chọn chế độ xem.
- **Thanh lọc mở rộng:** Nhấp nút `[ Lọc nâng cao ▾ ]` sẽ mở rộng một dải công cụ lọc chuyên sâu:
  - Lọc theo Thể loại (ComboBox).
  - Lọc theo Tình trạng tồn kho (Tất cả / Còn sách / Hết sách).
  - Lọc theo Vị trí Khu/Kệ cụ thể.
  - Lọc theo Khoảng giá (Từ ... Đến VNĐ).
- Giúp màn hình luôn tinh gọn, không gây rối mắt cho người dùng phổ thông.

### 3.5. Intriguing Branches (Gợi Mở Khám Phá Ngữ Cảnh)
- Đặt tại chân của Panel Detail:
  - **"Sách cùng Kệ vật lý":** Hiển thị 2-3 cuốn sách nằm liền kề trên cùng giá sách. Rất hữu ích cho thủ thư khi đi gom sách tại kho hoặc gợi ý sách cùng chủ đề cho độc giả.
  - **"Tác phẩm liên quan":** Gợi ý các cuốn sách cùng tác giả hoặc cùng bộ sưu tập. Nhấp vào sẽ chuyển Inspector sang cuốn đó ngay lập tức.

---

## 4. Đặc Tả Chi Tiết Phân Hệ 2: Lưu Hành Mượn - Trả Sách (Circulation Desk)

Cung cấp 2 chế độ làm việc chuyên biệt thông qua bộ chuyển đổi chế độ:
1. **Bàn Lưu Hành Trực Quan (Circulation Desk Canvas)**: Cho thao tác quầy thường nhật, mượn trả nhanh.
2. **Trợ Lý Từng Bước (Borrowing Wizard)**: Cho quy trình lập phiếu chuẩn chỉ hoặc phục vụ đào tạo thủ thư.

### 4.1. Canvas Plus Palette (Bàn Lưu Hành Trực Quan)
- **Bố cục:**
  - **Khay Đối Tượng (Palette - 30% bên trái):**
    - Ô quét mã vạch / gõ nhanh mã hoặc tên sách.
    - Danh sách các đầu sách khả dụng với nút `[ + Thêm vào bàn ]` hoặc kéo thả (Drag-and-Drop) trực tiếp sang Canvas.
  - **Bàn Làm Việc Trung Tâm (Canvas - 70% bên phải):**
    - **Thẻ Độc Giả Đang Phục Vụ:** Nhập mã thẻ độc giả. Hiển thị họ tên, trạng thái thẻ (Hợp lệ / Quá hạn), hạn thẻ, và số sách hiện đang mượn (đảm bảo tuân thủ giới hạn tối đa 5 cuốn theo SRS).
    - **Giỏ Sách Đang Xếp Trên Bàn (Circulation Workspace):** Thẻ sách hiển thị trực quan. Cho phép chọn riêng hình thức cho từng cuốn (*Đọc tại chỗ - trong ngày* hoặc *Mang về nhà - 14 ngày*), xóa khỏi bàn bằng nút `[✕]`.
    - **Thanh Tóm Tắt & Hành Động:** Hiển thị tổng số cuốn mượn, tiền phạt nợ cũ (nếu có), nút bấm nổi bật `[⚡ Hoàn Tất & In Phiếu]`.

### 4.2. Wizard (Trợ Lý Lập Phiếu Tuần Tự)
- Quy trình gồm 4 bước rõ ràng có thanh tiến trình (Stepper):
  - **Bước 1 (Xác thực độc giả):** Kiểm tra các điều kiện SRS (thẻ còn hạn không, có bị khóa không, đã mượn tối đa 5 cuốn chưa). Nếu vi phạm, thông báo lỗi cụ thể và không cho sang bước sau.
  - **Bước 2 (Chọn sách mượn):** Tìm và chọn các cuốn sách mượn từ kho khả dụng.
  - **Bước 3 (Hạn trả & Quy định):** Cấu hình hình thức mượn, tính toán ngày hẹn trả tự động theo quy định của Giám đốc.
  - **Bước 4 (Xác nhận & In phiếu):** Xem trước bản in phiếu mượn chuẩn có phần ký tên thủ thư và độc giả.

### 4.3. Extras on Demand (Chi Tiết Chế Tài Theo Nhu Cầu)
- Trên bàn lưu hành, các công thức phức tạp được giấu gọn gàng dưới liên kết `[Xem quy chế & chế tài áp dụng ▾]`.
- Khi nhấp vào sẽ hiển thị:
  - Mức phạt quá hạn: 2.000 VNĐ / ngày quá hạn / cuốn.
  - Chế tài bồi hoàn sách mất/hỏng: 200% giá bìa sách + 20.000 VNĐ lệ phí kỹ thuật.
  - Lịch sử mượn trả gần nhất của độc giả.

### 4.4. Intriguing Branches (Cảnh Báo & Gợi Ý Tại Quầy)
- Khi thủ thư nhập độc giả:
  - ⚠️ **Cảnh báo hữu ích:** *"Độc giả có 1 cuốn sách sắp hết hạn sau 2 ngày (Sapiens)"* ➔ Giúp thủ thư chủ động nhắc độc giả mang trả hoặc gia hạn.
  - 🌟 **Gợi ý tựa sách:** *"Độc giả thường xuyên mượn sách CNTT — Thư viện vừa có cuốn 'Clean Code' sẵn sàng"* ➔ Thủ thư có thể nhấp nút thêm ngay vào giỏ mượn.

---

## 5. Kiến Trúc Kỹ Thuật & Tích Hợp Mã Nguồn

### 5.1. Tổ Chức Component (FXML & Controllers)
- **Kho Sách:**
  - `BookManagementView.fxml` & `BookManagementController.java`: Nâng cấp chứa thanh công cụ Alternative Views, thanh lọc Extras on Demand, thanh Breadcrumb, và `SplitPane`.
  - Bổ sung sub-component hoặc helper hiển thị `BookCardCell` (cho Grid View) và `BookDetailInspector` (cho Panel Detail).
- **Lưu Hành Mượn - Trả:**
  - `BorrowReturnView.fxml` & `BorrowReturnController.java`: Bổ sung chế độ chuyển đổi giữa `CirculationDeskPane` (Canvas Plus Palette) và `BorrowWizardPane` (Wizard Stepper).
- **CSS Styling (`style.css`):**
  - Bổ sung CSS classes cho Breadcrumbs (`breadcrumb-bar`, `breadcrumb-item`), SplitPane styling, Card grid view (`book-grid-card`, `book-card-hover`), Stepper wizard (`wizard-step-active`, `wizard-step-done`), và Canvas workspace (`desk-canvas`, `desk-dropzone`).

### 5.2. Tương Thích Dữ Liệu & Quy Định SRS
- Sử dụng trực tiếp `BookDao`, `ReaderDao`, `BorrowTransactionDao`, `SettingDao`, `CategoryDao`.
- Không thay đổi cấu trúc bảng SQLite hay schema hiện có.

---

## 6. Kế Hoạch Kiểm Thử & Nghiệm Thu (Verification Plan)

1. **Kiểm thử tự động (Unit Tests):**
   - Chạy toàn bộ test suite hiện có (`./mvnw test`) để đảm bảo các ràng buộc DAO, Soft-delete, RBAC, và Tính toán tiền phạt/bồi thường vẫn đạt 100%.
2. **Kiểm thử tương tác người dùng (Manual Verification):**
   - Chuyển đổi qua lại giữa Table View và Grid View trong Kho sách không bị giật lag hoặc mất vị trí cuộn.
   - Chọn sách trong danh sách cập nhật chính xác nội dung Panel Detail và các gợi ý Intriguing Branches.
   - Thao tác trên Breadcrumb drill-down và quay lại danh mục mượt mà.
   - Tạo phiếu mượn trên Bàn Canvas (thêm/bớt sách, chọn hình thức tại chỗ/về nhà) và trên Wizard Stepper cho ra phiếu mượn chính xác vào cơ sở dữ liệu.
