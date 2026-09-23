# Kế Hoạch Triển Khai: Nâng Cấp Giao Diện Trang Chủ, Sidebar Brave-Style, 200+ Đầu Sách & Bìa Thật

> **Mục tiêu:** Giải quyết trực tiếp, triệt để 6 yêu cầu người dùng đưa ra, tối ưu hóa giao diện trang chủ, thanh điều hướng sidebar phong cách Brave vertical tabs (thu phóng hover + ghim), sửa lỗi che chữ bằng Icon + Tooltip, bổ sung hơn 200 đầu sách thực tế theo nhóm tác giả và tải ảnh bìa thật 100%, sau đó đóng gói build lại sản phẩm ngay.

## 1. Chi Tiết Các Hạng Mục

### Bước 1: Sửa bố cục Trang Chủ (Responsive & Cuộn Ngang Riêng Biệt - Ảnh 1)
- File: `src/main/resources/com/vithay/libman/view/HomeView.fxml`
- Khóa cuộn ngang toàn trang bằng cách đặt `hbarPolicy="NEVER"`, `fitToWidth="true"` trên `ScrollPane` gốc.
- Đặt `featuredBooksContainer` vào riêng một `ScrollPane` cuộn ngang (`hbarPolicy="AS_NEEDED"`, `vbarPolicy="NEVER"`, `fitToHeight="true"`).
- Bảng giao dịch gần đây và tiêu đề/nút bấm luôn căn chỉnh gọn gàng, tự động co giãn theo chiều rộng app mà không cần cuộn ngang.

### Bước 2: Thanh Điều Hướng Brave Vertical Tabs & Tích Hợp Icon Vector Morphicon (Ảnh 2, 3, 4)
- Files: `src/main/resources/com/vithay/libman/view/MainLayout.fxml`, `src/main/java/com/vithay/libman/controller/MainLayoutController.java`, `src/main/resources/com/vithay/libman/css/style.css`
- Tích hợp SVGPath vector icons chuẩn cho tất cả các nút sidebar (Trang chủ, Kho sách, Thể loại, Độc giả, Lưu hành, Lịch sử, Thống kê, Thùng rác, Hướng dẫn).
- Bổ sung nút Ghim (`btnPinSidebar` có icon Pin) ở đầu thanh bên.
- Cơ chế thu phóng phong cách Brave vertical tabs:
  - Khi chưa ghim (`!isPinned`):
    - Trạng thái mặc định: Thu hẹp về 60px, chỉ hiện dãy icon căn giữa, ẩn nhãn chữ và các mục con. Mỗi icon có Tooltip hiển thị tên chức năng khi hover.
    - Trạng thái hover: Rê chuột vào thanh bên sẽ mở rộng mượt mà ra 250px và hiển thị đầy đủ icon + chữ.
  - Khi đã ghim (`isPinned = true`): Cố định 250px không tự thu nhỏ.
- Màu sắc icon tự động tương thích Dark Theme & Pink Light Theme.

### Bước 3: Thay Thế Các Nút Che Chữ Bằng Icon + Tooltip (Ảnh 5)
- Files: `src/main/resources/com/vithay/libman/view/BookManagementView.fxml`, `src/main/java/com/vithay/libman/controller/BookManagementController.java`
- Thay nút "☰ Bảng" và "⊞ Lưới" thành nút Icon Vector (`≡` và `⊞`) kèm `Tooltip("Chế độ xem dạng Bảng")` và `Tooltip("Chế độ xem dạng Lưới thẻ")`.
- Cố định `minWidth="Region.USE_PREF_SIZE"` cho nút `btnToggleAdvancedFilter` ("Lọc nâng cao ▾") để chữ không bao giờ bị cắt thành "Lọc nâng ca...".
- Sửa badge trạng thái "Available" sang tiếng Việt chuẩn `"Còn sách"`.

### Bước 4: Mở Rộng Thư Viện Lên 200+ Đầu Sách Theo Nhóm Tác Giả & Tải Ảnh Bìa Thật 100%
- File: `src/main/resources/com/vithay/libman/database/seed_data.sql`, `src/main/resources/com/vithay/libman/images/`
- Tạo bộ dữ liệu > 205 đầu sách thực tế, gom nhóm vào 15-20 tác giả nổi tiếng (Nguyễn Nhật Ánh, J.K. Rowling, Yuval Harari, Robert Martin, Haruki Murakami, Dan Brown, Conan Doyle, Dale Carnegie, Paulo Coelho, George Orwell, Victor Hugo, Nam Cao, Tô Hoài, Vũ Trọng Phụng, Malcolm Gladwell, Walter Isaacson, Stephen Hawking, Martin Fowler, v.v.).
- Mỗi tác giả có từ 6 đến 18 đầu sách.
- Tải ảnh bìa sách thật chất lượng cao từ các kho lưu trữ trực tuyến OpenLibrary, Wikimedia Commons, Internet Archive.
- Nạp lại cơ sở dữ liệu `libman.db` với đầy đủ 205+ cuốn sách và ảnh bìa thật.

### Bước 5: Kiểm Thử Tự Động & Đóng Gói (Build) Ứng Dụng
- Chạy `./mvnw test` để đảm bảo 100% các bài test đều vượt qua.
- Chạy `./mvnw clean package -DskipTests` để tạo ra `target/libman-1.0.0.jar` và `target/LibMan.exe` mới nhất để người dùng chạy thử ngay.
