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

### 6. Trợ Giúp & Hướng Dẫn Sử Dụng (SRS Mục 4 & 6.4)
- Mục trợ giúp góc giao diện cung cấp toàn bộ hướng dẫn nghiệp vụ chuẩn mực cho Giám đốc, Thủ thư và Độc giả.

---

## 🚀 Hướng Dẫn Khởi Chạy

### 1. Khởi chạy trên Linux (Fedora 44, Ubuntu, Debian, Arch...)
```bash
./run.sh
# Hoặc dùng Maven wrapper trực tiếp:
./mvnw javafx:run
```

### 2. Khởi chạy trên Windows (10 / 11)
- **Cách 1 (Khuyên Dùng - Không Cần Cài Java):**
  Tải bản `LibMan-Windows-Portable-x64.zip` từ mục [Releases](https://github.com/namtacozz/ViThay/releases), giải nén và nhấp đúp file **`LibMan.exe`** để mở ứng dụng ngay lập tức.
- **Cách 2 (File .exe độc lập):**
  Tải `LibMan.exe` từ Releases và nhấp đúp chuột để chạy (yêu cầu máy đã cài sẵn Java 17+).
- **Cách 3 (Dùng Maven từ mã nguồn):**
  ```cmd
  run.bat
  # Hoặc dùng Maven wrapper:
  mvnw.cmd javafx:run
  ```

### 3. Đóng gói thành tệp JAR và .EXE độc lập
```bash
./mvnw package -DskipTests
```
Lệnh trên tự động biên dịch và tạo ra 2 tệp thực thi trong thư mục `target/`:
- **`target/LibMan.exe`**: Tệp thực thi giao diện đồ họa (GUI) cho hệ điều hành **Windows 10 / 11**.
- **`target/libman-1.0.0.jar`**: Tệp Fat JAR độc lập đa nền tảng cho **Linux (Fedora, Ubuntu...)** lẫn **Windows**. Chạy bằng:
  ```bash
  java -jar target/libman-1.0.0.jar
  ```

---

## 🧪 Kiểm Thử Tự Động (Automated Tests)
```bash
./mvnw test
```
Tất cả 7 test case (DAO, Phân quyền RBAC, Quy định động, Thùng rác Soft-delete, Khôi phục, Tính phạt) đều vượt qua 100%.

---
© 2026 LibMan Project. Phát triển bởi Antigravity.
