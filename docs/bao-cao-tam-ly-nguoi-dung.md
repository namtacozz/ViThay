# BÁO CÁO PHÂN TÍCH TÂM LÝ NGƯỜI DÙNG

## Hệ Thống Quản Lý Thư Viện — LibMan

**Ngày lập:** 23/09/2026
**Đối chiếu với:** Đặc tả yêu cầu phần mềm Quản Lý Thư Viện (SRS 8 trang) + 7 nguyên tắc tâm lý người dùng trong thiết kế tương tác

---

## Tổng Quan

Báo cáo này phân tích **7 yếu tố tâm lý người dùng** được áp dụng trong ứng dụng LibMan, liệt kê các yếu tố cụ thể trong giao diện và chức năng nơi mỗi nguyên tắc được hiện thực hóa.

---

## 1. Thành Công Tức Thời (Instant Gratification)

> **Định nghĩa:** Người dùng cần nhận được phản hồi tích cực, thấy kết quả ngay lập tức khi thực hiện hành động, tránh thời gian chờ đợi dài.

### 1.1 Trang chủ Dashboard — Thông số tổng quan tức thì

- Ngay khi mở ứng dụng, **4 thẻ KPI** (Tổng sách, Sách khả dụng, Sách đang mượn, Tổng độc giả) hiển thị dữ liệu thống kê tổng hợp mà không cần bất kỳ thao tác nào.
- **Dải sách nổi bật** với hình bìa sách, tên, tác giả, thể loại và badge trạng thái hiển thị ngay trên trang chủ để người dùng có cái nhìn tổng quan về kho sách.
- **Bảng 10 giao dịch mượn trả gần đây nhất** có sẵn lập tức, cho thấy hoạt động của thư viện đang diễn ra.

### 1.2 Đăng nhập / Đăng ký — Phản hồi tức thì

- Có **3 nút Quick-Fill** (Giám đốc, Thủ thư, Độc giả) giúp điền sẵn tài khoản demo chỉ với 1 click, người dùng không cần nhớ hay gõ.
- Đăng nhập thành công → thanh sidebar cập nhật ngay lập tức theo quyền hạn (RBAC), chuyển về trang chủ tức thì mà không cần tải lại.
- Đăng ký thành công → hệ thống hiện **hộp thoại chúc mừng** rõ ràng, tự động đăng nhập và đưa về trang chủ.
- Thông báo lỗi (sai mật khẩu, trùng tài khoản) hiện **ngay tại chỗ** dưới form bằng chữ đỏ, thay vì popup chặn — người dùng sửa ngay mà không bị gián đoạn.

### 1.3 Tìm kiếm — Kết quả xuất hiện ngay khi gõ

- Thanh tìm kiếm ở header có chế độ **autocomplete thời gian thực** (debounce 120ms để hỗ trợ gõ tiếng Việt) — kết quả hiện ngay dưới thanh tìm kiếm khi người dùng đang gõ.
- **Dropdown kết quả nhanh** (tối đa 6 kết quả kiểu Spotify) hiển thị hình bìa thu nhỏ, tên sách, tác giả, vị trí kệ và badge trạng thái cho mỗi kết quả.
- Click vào kết quả → chuyển thẳng đến trang Kho Sách và tự động lọc đến cuốn sách đó.

### 1.4 Mượn sách — Thêm sách vào giỏ 1 click

- Mỗi hàng sách trong danh sách Palette đều có nút **[+ Thêm]** → thêm sách vào bàn lưu hành chỉ với 1 click.
- Hỗ trợ **double-click** trên sách cũng thêm vào giỏ — 2 cách thao tác cho cùng 1 mục đích.
- Nút **"Mượn ngay"** có sẵn ngay trong panel chi tiết sách khi xem thông tin — không cần chuyển trang thủ công.

---

## 2. Thỏa Mãn Vừa Đủ (Satisficing)

> **Định nghĩa:** Người dùng không tìm giải pháp tối ưu mà chọn giải pháp "đủ tốt" đầu tiên. Hệ thống cần đưa ra các giá trị mặc định hợp lý.

### 2.1 Giá trị mặc định thông minh

- Form thêm sách mới có **auto-fill** mã sách tiếp theo, kệ sách mặc định "Khu TN - Kệ 01", ISBN prefix "978-604-", giá mặc định 95.000đ, 5 bản — người dùng chỉ cần điền tên sách và tác giả.
- Khi thêm sách vào giỏ mượn, hình thức mặc định là **"Mang về nhà"** với **14 ngày**, hệ thống tự tính ngày hạn trả — thủ thư không cần suy nghĩ số ngày mượn.
- Trang Cài đặt quy định thư viện có nút **"Khôi phục mặc định"** trả tất cả tham số về giá trị tiêu chuẩn SRS với 1 click.
- Các Spinner nhập số đều có **giới hạn phạm vi hợp lệ** (VD: 1–20 cuốn mượn tối đa, 1–90 ngày mượn) → người dùng không cần nhớ quy chế, chỉ xoay spinner.

### 2.2 Bộ lọc mặc định "Tất cả"

- Tất cả bộ lọc (Tác giả, Trạng thái, Thể loại, Kệ sách, Tồn kho) mặc định = **"Tất cả"** → mở trang là thấy toàn bộ dữ liệu, không bị lọc mất gì.

---

## 3. Trì Hoãn Lựa Chọn (Deferred Choices)

> **Định nghĩa:** Cho phép người dùng bỏ qua các bước phụ, quay lại sau khi sẵn sàng, không bắt buộc hoàn thành tất cả ngay.

### 3.1 Chế độ khách (Guest Mode) — Không bắt buộc đăng nhập

- Mở ứng dụng là có thể truy cập **Trang chủ Dashboard** và **Kho Sách** ngay mà không cần đăng nhập — nhãn gợi ý nhẹ nhàng "Nhấn để đăng nhập / đăng ký" thay vì chặn truy cập.
- Người dùng có thể duyệt sách, tìm kiếm thoải mái trước, **khi nào cần mượn thì mới đăng nhập**.

### 3.2 Đăng ký → Trì hoãn cấp thẻ vật lý

- Đăng ký trực tuyến thành công → trạng thái **"Chờ Cấp Thẻ"** → hệ thống không chặn mà hẹn người dùng mang CCCD đến thư viện sau để hoàn tất.

### 3.3 Thùng Rác — Trì hoãn quyết định xóa vĩnh viễn

- Xóa sách hoặc độc giả → chuyển vào **Thùng Rác** (xóa mềm) → có thể **Phục Hồi** nguyên trạng bất cứ lúc nào.
- Chỉ khi chọn **"Hủy Vĩnh Viễn"** mới thực sự xóa, và phải xác nhận thêm 1 lần nữa bằng hộp thoại cảnh báo.

### 3.4 Giỏ lưu hành — Thay đổi lựa chọn trước khi quyết định

- Bàn lưu hành hoạt động như **"giỏ hàng"**: thủ thư xếp sách lên bàn, có thể **thêm/xóa/thay đổi hình thức mượn/số ngày** cho từng cuốn tùy ý, rồi mới bấm "Xác nhận" khi đã sẵn sàng.

---

## 4. Học Từng Phần Qua Tương Tác (Incremental Learning / Progressive Disclosure)

> **Định nghĩa:** Người dùng học dần cách sử dụng hệ thống thông qua tương tác thực tế, không cần đọc tài liệu dài.

### 4.1 Wizard 4 bước — Hướng dẫn mượn sách từng bước

- Tab **"Trợ Lý Wizard"** trong màn hình Mượn Sách chia tác vụ mượn sách thành **4 bước tuần tự**: Chọn Độc Giả → Chọn Sách → Điều Khoản Mượn → Xem Trước Phiếu Mượn.
- Có **chỉ báo bước** hiển thị "Bước 2 / 4" và các circle indicator cho biết đang ở bước nào.
- Mỗi bước có **feedback label riêng** cho biết trạng thái hợp lệ hay cần sửa.
- Nút **"Quay Lại / Tiếp Theo"** cho phép sửa bước trước mà không mất dữ liệu đã nhập.

### 4.2 Hệ thống Breadcrumb — Người dùng hiểu mình đang ở đâu

- Thanh breadcrumb cập nhật theo ngữ cảnh, VD: `Kho Sách › Thể loại: Văn học › Chi tiết: Đồi Gió Hú › Lịch sử lưu hành`.
- Mỗi mảnh breadcrumb có thể **click để quay lại cấp tương ứng** — người dùng luôn biết mình đang ở đâu và dễ dàng quay lại.

### 4.3 Hướng dẫn sử dụng nội tuyến

- Nút **[?] Trợ Giúp** ở sidebar mở hộp thoại hướng dẫn toàn bộ nghiệp vụ, chia thành 5 mục rõ ràng: Phân quyền, Quản lý Sách, Quản lý Độc giả, Mượn/Trả sách, Thùng Rác — phù hợp cho cả 3 vai trò Giám đốc, Thủ thư và Độc giả.

---

## 5. Khám Phá An Toàn (Safe Exploration)

> **Định nghĩa:** Người dùng có thể thử nghiệm tự do mà không sợ gây hậu quả nghiêm trọng, mọi thao tác đều có thể hoàn tác.

### 5.1 Soft-delete (Xóa mềm) — "Undo" toàn diện

- Xóa sách hoặc độc giả **chỉ ẩn khỏi danh sách chính**, không xóa thật — dữ liệu được chuyển vào Thùng Rác.
- Hộp thoại xác nhận mô tả rõ: *"Sách sẽ được ẩn khỏi kho mượn nhưng có thể phục hồi lại từ Thùng Rác bất cứ lúc nào"* — giúp người dùng yên tâm thao tác.

### 5.2 Xác nhận nhiều bước cho thao tác nguy hiểm

- Nút **"Hủy Vĩnh Viễn"** trong Thùng Rác yêu cầu xác nhận bằng hộp thoại cảnh báo với nội dung: *"Dữ liệu sẽ bị xóa hoàn toàn khỏi cơ sở dữ liệu và không thể khôi phục"* — người dùng phải chủ động bấm OK.

### 5.3 Kiểm tra chặn trước khi gây lỗi nghiệp vụ

- Hệ thống **kiểm tra trùng sách** trong giỏ trước khi thêm — thông báo "Cuốn sách đã có trên bàn lưu hành!" nếu trùng.
- Kiểm tra **hạn ngạch mượn** — nếu độc giả đã mượn đủ số sách tối đa, hiện cảnh báo cụ thể: *"Chỉ còn được mượn thêm tối đa X cuốn nữa theo quy chế thư viện!"*
- Kiểm tra **trạng thái thẻ** (Bị khóa, Hết hạn, Chờ cấp thẻ) → chặn mượn và hiển thị thông báo lý do cụ thể cho từng trường hợp.
- Kiểm tra **sách hết bản** → hiện cảnh báo "Sách này hiện không còn bản nào khả dụng trong kho!" thay vì cho phép tạo phiếu lỗi.

### 5.4 Chế độ khách an toàn

- Người dùng chưa đăng nhập (Khách) chỉ thấy Trang chủ và Kho Sách ở chế độ **chỉ đọc** → không có nút Thêm/Sửa/Xóa → không thể vô tình thao tác nhầm.
- Hệ thống RBAC kiểm tra vai trò trước khi hiển thị các nút hành động quản trị.

---

## 6. Thói Quen Sử Dụng (Habituation)

> **Định nghĩa:** Hệ thống tuân theo các quy ước giao diện quen thuộc, người dùng không cần học lại từ đầu.

### 6.1 Bố cục Sidebar + Content chuẩn

- Layout chính sử dụng **Sidebar trái cố định + Vùng nội dung bên phải** — quy ước quen thuộc của Spotify, VS Code, Notion.
- Mục navigation trong sidebar được **highlight active** khi đang ở trang đó.
- **Logo** nằm ở góc trên bên trái, **Profile chip** nằm ở góc trên bên phải — đúng quy ước giao diện chuẩn.

### 6.2 Các pattern tương tác quen thuộc

- Bảng dữ liệu (TableView) có **cột sắp xếp** và nút Sửa/Xóa ở **cột hành động cuối cùng** — giống mọi ứng dụng quản lý.
- Form chỉnh sửa mở dưới dạng **modal overlay** với nền blur — pattern quen thuộc của web và desktop hiện đại.
- Thanh tìm kiếm nằm ở **header trung tâm** — vị trí quen thuộc của Google, Spotify.
- Badge trạng thái sử dụng **màu sắc nhất quán** xuyên suốt: Xanh = Available, Vàng = On Hold, Đỏ = Borrowed/Overdue — người dùng chỉ cần nhìn màu là biết.

### 6.3 Phím tắt & Tương tác bàn phím

- **Escape** đóng dropdown tìm kiếm.
- **Enter** trên thanh tìm kiếm → chuyển sang trang Kho Sách và lọc theo từ khóa.
- **Phím mũi tên Lên/Xuống** trong bảng sách → cập nhật panel chi tiết bên phải theo hàng đang chọn.

---

## 7. Microbreaks (Nghỉ giải lao ngắn)

> **Định nghĩa:** Cung cấp các yếu tố thị giác thú vị, chuyển đổi nhịp độ tương tác, giúp người dùng không bị mệt mỏi khi làm việc liên tục.

### 7.1 Thay đổi giao diện (Theme switching)

- Hệ thống có **2 theme** chuyển đổi tức thì: **Spotify Dark** (tối, xanh lá) ↔ **Modern Pink Light** (sáng, hồng pastel).
- Có thể đổi theme nhanh từ **menu header** (1 click) mà không cần vào trang Cài đặt.
- Logo và avatar tự động **đổi phiên bản** (dark/light) khi chuyển theme — chi tiết nhỏ nhưng tinh tế.

### 7.2 Chuyển đổi góc nhìn — Alternative Views

- Kho sách cho phép chuyển giữa **Chế độ Bảng** (danh sách cột) và **Chế độ Thẻ Ảnh** (grid bìa sách lớn) — chế độ thẻ ảnh giống duyệt album nhạc Spotify, tạo trải nghiệm thị giác thư giãn.

### 7.3 Featured Books carousel trên trang chủ

- Dải sách nổi bật trên Dashboard hiển thị **bìa sách lớn, có hiệu ứng bóng đổ** — cho phép duyệt cuốn sách theo kiểu thẻ hình ảnh thay vì đọc bảng số liệu khô khan.
- Mỗi thẻ sách có badge trạng thái màu sắc, click vào để xem chi tiết.

### 7.4 Thông báo hệ thống nhẹ nhàng

- Nút **🔔 Thông báo** hiển thị tin nhắn trạng thái hệ thống dạng nhẹ nhàng.
- Nút **✉ Tin nhắn** mở hộp thư nội bộ — dù chưa có tin mới, sự hiện diện của nó tạo cảm giác hệ thống "sống" và chuyên nghiệp.

---

## Bảng Tổng Hợp

| # | Yếu tố tâm lý | Số yếu tố triển khai | Đánh giá |
|---|----------------|----------------------|----------|
| 1 | Thành công tức thời | 10+ yếu tố | ✅ **Đầy đủ** |
| 2 | Thỏa mãn vừa đủ | 6 yếu tố | ✅ **Đầy đủ** |
| 3 | Trì hoãn lựa chọn | 5 yếu tố | ✅ **Đầy đủ** |
| 4 | Học từng phần qua tương tác | 6 yếu tố | ✅ **Đầy đủ** |
| 5 | Khám phá an toàn | 8 yếu tố | ✅ **Đầy đủ** |
| 6 | Thói quen sử dụng | 8 yếu tố | ✅ **Đầy đủ** |
| 7 | Microbreaks | 7 yếu tố | ✅ **Đầy đủ** |

**Kết luận:** Tất cả 7 yếu tố tâm lý người dùng đều được triển khai **đầy đủ** trong hệ thống. Tổng cộng **50+ yếu tố** phân bố đều trên toàn bộ các chức năng của ứng dụng LibMan.

---

*Báo cáo được lập cho mục đích báo cáo học phần Công Nghệ Phần Mềm — Dự án LibMan.*
