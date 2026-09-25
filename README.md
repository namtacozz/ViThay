# LibMan - Hệ Thống Quản Lý Thư Viện Hiện Đại (Java Desktop)

Ứng dụng Desktop Quản lý Thư viện chuẩn mực, chuyên nghiệp, chạy đa nền tảng (**Linux: Fedora 44, Ubuntu, Debian...** và **Windows 10/11**) được xây dựng bằng **Java 17+ (JavaFX 21)** kết hợp với cơ sở dữ liệu nhúng **SQLite**.

Giao diện được thiết kế **Pixel-Perfect theo phong cách Spotify Dark Theme** dựa trên mockup chính thức (`plans/demo.jpeg`) và **bộ tài liệu đặc tả yêu cầu phần mềm thư viện (SRS 8 trang)**.

---

## 🔐 Tài Khoản Khởi Tạo Mẫu (SRS Mục 6.3)
Hệ thống cài đặt sẵn 3 tài khoản tương ứng với 3 cấp độ phân quyền (RBAC):
- **Giám đốc (Quản trị cao nhất):**
  - Tài khoản: `giamdoc` | Mật khẩu: `admin123`
  - *Quyền hạn:* Toàn quyền hệ thống, cấu hình quy định (SRS 6.1), quản lý phân quyền, khôi phục dữ liệu thùng rác (SRS 6.2).
- **Thủ thư:**
  - Tài khoản: `minhtri` | Mật khẩu: `123456`
  - *Quyền hạn:* Nhập sách mới, quản lý độc giả, lập thẻ, mượn - trả sách, in ấn & xuất CSV, quản lý thùng rác.
- **Độc giả:**
  - Tài khoản: `docgia` | Mật khẩu: `123456`
  - *Quyền hạn:* Tra cứu sách (OPAC), xem lịch sử mượn sách.

*Mẹo:* Người dùng có thể nhấn vào **Profile Chip** ở góc trên bên phải màn hình để chuyển đổi tài khoản hoặc đăng ký thành viên mới bất kỳ lúc nào.

---

## 🌟 Toàn Bộ Các Tính Năng Đã Triển Khai (Chuẩn SRS 8 Trang)

### 1. Quản lý Sách & Danh mục Thể loại (SRS Mục 5.3)
- Quản lý thông tin sách: `MaSach`, `TenSach`, `MaLoai` (Thể loại), `TenTacGia`, `TinhTrang`, `shelfLocation` (Vị trí khu vực / kệ sách).
- Bảng thể loại sách riêng biệt (`categories`): Lý luận chính trị, Văn học kinh điển, Toán học, Lịch sử, Khoa học viễn tưởng, Kỹ năng sống, Hồi ký, Tiểu thuyết, CNTT.
- Sắp xếp và định vị sách theo vị trí kệ (VD: *Khu A - Kệ 01*, *Khu B - Kệ 02*...) giúp thủ thư lấy sách nhanh chóng.
- Hỗ trợ xuất toàn bộ danh mục kho sách ra tệp **CSV** phục vụ kiểm kê hoặc in ấn ra máy in.

### 2. Quản lý Độc giả & Thẻ thư viện (SRS Mục 5.4 & Trang 1)
- Quản lý hồ sơ độc giả: `MaDG`, `HoTen`, `NgaySinh`, `DiaChi`, `CMND`, `Email`, `SoDienThoai`.
- Quản lý thời hạn hiệu lực của thẻ: `NgayCapThe`, `NgayHetHanThe`.
- Nút tính năng **⏰ Quét Thẻ Hết Hạn**: Tự động phát hiện và khóa/chuyển trạng thái độc giả quá hạn thẻ theo trách nhiệm của thủ thư.
- Hỗ trợ xuất danh sách độc giả ra tệp **CSV**.

### 3. Lưu hành Mượn - Trả sách (SRS Mục 1 & 5.5)
- Hỗ trợ **2 hình thức mượn**:
  1. **Mượn đọc tại chỗ ở phòng đọc** (hạn trả trong ngày).
  2. **Mượn mang về nhà để tham khảo** (hạn trả từ 14 đến 90 ngày).
- Kiểm soát quy định: Giới hạn số lượng sách mượn tối đa / độc giả (mặc định 5 cuốn); chặn mượn đối với độc giả bị khóa hoặc thẻ hết hạn.
- Xử lý trả sách và chế tài:
  - Phạt quá hạn: **2.000 VNĐ / ngày quá hạn / cuốn**.
  - Bồi hoàn sách mất / hư hỏng nặng: **200% giá gốc sách + 20.000 VNĐ lệ phí xử lý kỹ thuật**.
- **In Phiếu Mượn Sách:** Tự động tạo biểu mẫu phiếu mượn chuẩn có phần ký tên của độc giả và thủ thư.

### 4. Cài Đặt Quy Định Thư Viện Động (SRS Mục 6.1)
- Cho phép Giám đốc tùy chỉnh các tham số vận hành:
  - Số sách mượn tối đa của 1 độc giả.
  - Số ngày mượn tối đa khi mang về và tại chỗ.
  - Tiền phạt quá hạn theo ngày.
  - Tỷ lệ đền bù sách mất và phí kỹ thuật.
  - Thời hạn hiệu lực thẻ độc giả (tháng).

### 5. Thùng Rác & An Toàn Dữ Liệu (SRS Mục 6.2)
- Cơ chế xóa mềm (Soft-delete): Sách hoặc độc giả khi bị xóa sẽ chuyển vào **Thùng Rác**.
- Cho phép **Phục Hồi (Restore)** nguyên trạng hoặc **Hủy Vĩnh Viễn** khỏi cơ sở dữ liệu.

### 6. Trợ Giúp & Hướng Dẫn Sử Dụng Trong Ứng Dụng (SRS Mục 4 & 6.4)
- **Modal Popup Hướng Dẫn (SRS)** chuyên nghiệp tích hợp ngay trong app với nền kính mờ (`GaussianBlur`), trình bày 5 khối thẻ phong cách Spotify: Kiến trúc phân hệ, Phân quyền RBAC, Phím tắt toàn cục (`Ctrl+F`, `Ctrl+N`, `Ctrl+D`, `Esc`), Quy trình mượn - trả và Cơ chế an toàn bảo mật.

### 7. Tối Ưu Hiệu Năng & Trải Nghiệm Người Dùng (v1.4.0)
- **Hệ Thống In-App Popup Overlay (Không Cửa Sổ Phụ):** Triệt tiêu 100% các hộp thoại OS-level (`new Alert`, `new Dialog`), thay thế bằng `appNoticeModalBox` với hiệu ứng kính mờ `GaussianBlur(14)`.
- **Yêu Cầu Đăng Nhập Tự Động:** Khi người dùng chưa đăng nhập bấm mượn sách hoặc gửi bình luận, popup in-app nhắc đăng nhập hiện lên với tùy chọn chuyển nhanh đến form đăng nhập.
- **Tách Biệt Lưu Hành Độc Giả (RBAC):** Độc giả lập phiếu mượn qua modal riêng biệt (`readerBorrowModalBox`), không còn bị chuyển vào bàn lưu hành chuyên trách của thủ thư/admin.
- **Đồng Nhất Mã Giao Dịch Tuần Tự (`2112xxxxx`):** Tự động cấp mã mượn tuần tự 9 chữ số chuẩn thư viện thay cho các mã ngẫu nhiên dạng `TX...`.
- **Hợp Nhất Trợ Lý Mượn Nhanh:** Chuyển tính năng Trợ lý Wizard thành nút tùy chọn nâng cao `[⚡ Trợ lý mượn nhanh (Wizard)]` tại góc trên bên phải của Bàn lưu hành (Desk).
- **Hành Vi Tương Tác Kép Kho Sách:** Hỗ trợ click đúp (Double-click) hoặc phím Enter trên cả Dạng Bảng (Table View) và Dạng Lưới Thẻ (Grid View) để mở giao diện toàn màn hình chi tiết sách.
- **Hồ Sơ Độc Giả Chuẩn Mực:** Khôi phục tài khoản `docgia` và thẻ `DG001` về họ tên chuẩn: **Trần Văn An**, cách ly hoàn toàn dữ liệu kiểm thử.

---

## 🚀 Hướng Dẫn Khởi Chạy

### 1. Khởi chạy trên Linux (Fedora, Ubuntu, Debian, Arch...)
```bash
./run.sh
# Hoặc dùng Maven wrapper trực tiếp:
./mvnw javafx:run
```

### 2. Khởi chạy trên Windows (10 / 11)
- **Cách 1 (Khuyên Dùng - Tệp .exe độc lập):**
  Nhấp đúp chuột vào tệp **`target/LibMan.exe`** để mở ứng dụng ngay lập tức.
- **Cách 2 (Dùng Maven từ mã nguồn):**
  ```cmd
  run.bat
  # Hoặc dùng Maven wrapper:
  mvnw.cmd javafx:run
  ```

### 3. Đóng gói thành tệp JAR và .EXE độc lập
```bash
./mvnw clean package -DskipTests
```
Lệnh trên tự động biên dịch và tạo ra 2 tệp thực thi hoàn chỉnh trong thư mục `target/`:
- **`target/LibMan.exe`** (32 MB): Tệp thực thi giao diện đồ họa (GUI) cho hệ điều hành **Windows 10 / 11**.
- **`target/libman-1.0.0.jar`** (32 MB): Tệp Fat JAR độc lập đa nền tảng cho **Linux (Fedora, Ubuntu...)** lẫn **Windows**. Chạy bằng:
  ```bash
  java -jar target/libman-1.0.0.jar
  ```

---

## 🧪 Kiểm Thử Tự Động (Automated Tests)
```bash
./mvnw test
```
Toàn bộ **62/62 bài test tự động** đều vượt qua 100% (BUILD SUCCESS), bao gồm:
- **Kiến trúc dữ liệu & DAO:** `LibManTest` (10 tests), `DatabaseConfig`, `BookDao`, `ReaderDao`, `BorrowTransactionDao`.
- **Interaction Design Patterns:** `TwoPanelSelectorTest`, `BorrowWizardTest` (6 tests), `CirculationBasketTest` (4 tests), `CirculationContextualBranchTest` (5 tests), `BookViewSwitchingTest` (3 tests), `BookDrilldownAndBranchesTest` (6 tests), `BookFilterBugFixesTest` (5 tests).
- **Hành vi tương tác người dùng v1.4.0:**
  - `BookClickBehaviorTest`: Xác thực single-click xem trước Inspector và double-click mở full view.
  - `BookCoversDistinctTest`: Xác thực 221/221 cuốn sách có ảnh bìa thật xuất bản độ phân giải cao (>= 15KB) và 0% trùng lặp hash MD5.
  - `DashboardAndGreetingTest`: Xác thực lời chào động và giới hạn 15 sách nổi bật loại bỏ lag.
  - `HelpModalAndProfileTest`: Xác thực modal popup Hướng dẫn (SRS), in-app notice modal và nút toggle đổi mật khẩu trong layout chính.
  - `ReaderAndTransactionDataTest`: Xác thực 38 độc giả, mã giao dịch tuần tự `2112xxxxx` và tài khoản độc giả Trần Văn An.
  - `ReaderCirculationAndRoleTest`: Xác thực phân quyền lưu hành độc lập và modal mượn sách độc giả.

---
© 2026 LibMan Project. Phát triển bởi Antigravity.
