# BÁO CÁO PHÂN TÍCH CẤU TRÚC VẬT LÝ & THÔNG TIN

## Hệ Thống Quản Lý Thư Viện — LibMan (Phiên bản v1.3.2)

**Ngày cập nhật:** 24/09/2026  
**Đối chiếu với:** Đặc tả yêu cầu phần mềm Quản Lý Thư Viện (SRS 8 trang) + 7 Interaction Design Patterns

---

## Tổng Quan

Báo cáo này phân tích **7 thiết kế cấu trúc vật lý và thông tin** (Interaction Design Patterns) được áp dụng trong ứng dụng LibMan, liệt kê các yếu tố cụ thể trong giao diện nơi mỗi pattern được hiện thực hóa.

---

## 1. Two-Panel Selector

> **Định nghĩa:** Giao diện chia thành 2 panel: panel trái (Master) hiển thị danh sách tổng quan, panel phải (Inspector/Detail) hiển thị chi tiết mục được chọn. Chọn item ở panel trái → panel phải cập nhật tức thì.

### Triển khai: Màn hình Kho Sách — SplitPane Master / Inspector

- Màn hình Kho Sách được chia thành **2 panel** bằng thanh chia (SplitPane) với tỷ lệ mặc định **68% bên trái, 32% bên phải**.

**Panel trái (Master):**
- Chứa **bảng danh sách sách** (hoặc thẻ ảnh) với các cột: Mã sách, Tên sách, Tác giả, Thể loại, Vị trí kệ, Đơn giá, Số bản khả dụng, Trạng thái, Hành động.
- Có thanh tìm kiếm, bộ lọc tác giả, bộ lọc trạng thái và bộ lọc nâng cao.

**Panel phải (Inspector):**
- Khi chọn 1 hàng sách bên trái, panel bên phải **cập nhật tức thì** hiển thị:
  - Hình bìa sách thật độ phân giải cao tải trực tiếp từ web.
  - Tên sách, tác giả (click được → lọc cùng tác giả), thể loại.
  - Badge trạng thái có màu sắc (Xanh = Available, Đỏ = Borrowed...).
  - Vị trí kệ sách (click được → lọc cùng kệ), ISBN, đơn giá, số bản khả dụng/tổng.
  - 3 nút hành động: **Mượn ngay**, **Sửa**, **Xóa** (ẩn theo phân quyền RBAC).
  - Khu vực **Nhánh liên quan & Gợi ý** (xem mục Intriguing Branches bên dưới).

**Cách hoạt động & Phân định thao tác Click (v1.3.2):**
- **Click 1 lần (Single-click)** hoặc di chuyển phím mũi tên `↑ / ↓`: Chỉ chọn mục và cập nhật tức thì dữ liệu xem trước lên panel Inspector bên phải. Không làm chuyển đổi góc nhìn toàn màn hình.
- **Click đúp (Double-click)** hoặc phím `Enter`: Mở giao diện toàn màn hình xem chi tiết sách sâu (`showFullBookDetailView`) bao gồm mô tả đầy đủ, thảo luận bình luận, và các thông số lưu thông.

```
Click 1 lần (hoặc phím mũi tên ↑↓)
        ↓
Panel Inspector bên phải cập nhật tất cả thông tin xem trước (bìa thật, vị trí kệ, tồn kho)
        ↓
Click đúp (hoặc phím Enter)
        ↓
Mở giao diện xem chi tiết sâu toàn màn hình (Full Book Detail View)
```

---

## 2. Canvas Plus Palette

> **Định nghĩa:** Giao diện có một vùng làm việc chính (Canvas) nơi người dùng thao tác trực tiếp, kết hợp với một bảng chọn (Palette) cung cấp các item để thêm vào canvas.

### Triển khai: Tab "Bàn Lưu Hành" trong màn hình Mượn Sách

- Tab "Bàn Lưu Hành" được chia thành **2 vùng** bằng thanh chia:

**Vùng trái — PALETTE (Kho sách khả dụng):**
- Thanh tìm kiếm để lọc sách theo tên, tác giả, mã sách, kệ, thể loại.
- Danh sách sách khả dụng, mỗi hàng hiển thị tên sách, tác giả, vị trí kệ, số bản còn, và nút **[+ Thêm]**.
- Nhãn "Khả dụng: X cuốn" cập nhật theo bộ lọc.

**Vùng phải — CANVAS (Bàn lưu hành / Giỏ mượn):**
- Phần chọn độc giả bằng ComboBox, hiển thị trạng thái thẻ, số sách đang mượn, hạn ngạch còn lại.
- Khu vực chứa các **thẻ sách đã xếp lên bàn**, mỗi thẻ gồm:
  - Thumbnail bìa sách, tên sách, tác giả, vị trí kệ, mã sách.
  - ComboBox chọn hình thức mượn: "Mang về nhà" / "Mượn đọc tại chỗ".
  - Spinner chọn số ngày mượn.
  - Nhãn ngày hạn trả tự động tính.
  - Nút **[✕]** để xóa sách khỏi bàn.
- Phần tóm tắt: "Tổng trong giỏ: X/5 cuốn" + trạng thái hạn ngạch.
- Nút **"Xác nhận cho mượn"** để hoàn tất phiếu.

**Cơ chế chuyển từ Palette sang Canvas:**
- Click nút **[+ Thêm]** trên mỗi hàng sách.
- Hoặc **double-click** vào sách trong danh sách.

**Minh họa:**
```
PALETTE (bên trái)                    CANVAS (bên phải — Bàn Lưu Hành)
┌─────────────────────┐              ┌─────────────────────────────────┐
│ 🔍 Tìm sách...      │              │ Chọn độc giả: [ComboBox]       │
│                     │   [+ Thêm]   │                                 │
│ 📖 Tư Tưởng HCM    │ ──────────►  │ ┌─────────────────────────────┐ │
│ 📖 Đồi Gió Hú      │              │ │ 📖 Đồi Gió Hú               │ │
│ 📖 Cánh Đồng Hoang │              │ │ Phan Trọng • Kệ: Khu A-K01 │ │
│ 📖 Toán Rời Rạc    │              │ │ [Mang về ▾] [14 ngày] Hạn:..│ │
│                     │              │ └─────────────────────────[✕]─┘ │
│ Khả dụng: 15 cuốn  │              │                                 │
└─────────────────────┘              │ Tổng: 1/5 cuốn | [Xác nhận ✓]  │
                                     └─────────────────────────────────┘
```

---

## 3. One-Window Drilldown

> **Định nghĩa:** Nội dung chi tiết hơn được mở ra ngay trong cùng một cửa sổ (thay thế nội dung hiện tại), kèm breadcrumb/back button để quay lại, tránh mở cửa sổ mới.

### 3.1 Drilldown theo Tác Giả / Kệ Sách

- Trong panel Inspector (chi tiết sách), tên tác giả và vị trí kệ đều **click được** (con trỏ chuột đổi thành hình bàn tay).
- Click tên tác giả → **toàn bộ bảng sách lọc lại** chỉ hiện sách của tác giả đó, ngay trong cùng cửa sổ.
- Click vị trí kệ → toàn bộ bảng sách lọc lại chỉ hiện sách trên cùng kệ đó.

### 3.2 Drilldown Lịch Sử Lưu Hành

- Trong panel Inspector, nút **"📜 Xem Lịch Sử Lưu Hành Cuốn Này"** → bảng danh sách sách **biến mất** và được thay thế bằng **bảng lịch sử giao dịch** của cuốn sách đó (ai mượn, ngày mượn, ngày trả, phạt...) — tất cả ngay trong cùng cửa sổ.
- Nút **"Quay lại danh sách sách"** để thoát chế độ lịch sử và quay về bảng sách.

### 3.3 Breadcrumb Navigation Trail

- Thanh breadcrumb ở đầu trang **cập nhật tự động** theo ngữ cảnh hiện tại, VD:
  ```
  Kho Sách › Thể loại: Văn học › Chi tiết: Đồi Gió Hú › Lịch sử lưu hành
  ```
- Mỗi mảnh breadcrumb **click được** để quay lại cấp tương ứng.
- Click "Kho Sách" (root) → **reset toàn bộ bộ lọc** và quay về trạng thái ban đầu.

**Sơ đồ drilldown:**
```
Kho Sách (root)
    ├── [Click Thể loại: "Văn học"] → Kho Sách › Thể loại: Văn học
    │       ├── [Click sách "Đồi Gió Hú"] → ... › Chi tiết: Đồi Gió Hú
    │       │       └── [Click "Xem Lịch Sử"] → ... › Lịch sử lưu hành
    │       │               └── [Click breadcrumb "Kho Sách"] → QUAY VỀ ROOT
    ├── [Click Tác giả: "Phan Trọng"] → Kho Sách › Tác giả: Phan Trọng
    └── [Click Kệ: "Khu A - Kệ 01"] → Kho Sách › Kệ: Khu A - Kệ 01
```

---

## 4. Alternative Views

> **Định nghĩa:** Cho phép người dùng chuyển đổi giữa các cách hiển thị khác nhau cho cùng một tập dữ liệu.

### 4.1 Kho Sách — Table View ↔ Grid Card View

- Toolbar của Kho Sách có **2 nút chuyển chế độ xem** (kiểu segmented control):
  - **📋 Bảng (Table View):** Hiển thị sách dạng bảng có cột, hỗ trợ sắp xếp.
  - **📷 Thẻ Ảnh (Grid Card View):** Hiển thị sách dạng thẻ hình ảnh với bìa sách lớn, tên, tác giả, badge trạng thái — giống giao diện duyệt album nhạc.
- Cả 2 chế độ xem đều sử dụng **cùng 1 tập dữ liệu** (cùng bộ lọc), và panel Inspector vẫn hoạt động bình thường.
- Nút đang active có highlight CSS để người dùng biết đang ở chế độ nào.

**Minh họa:**
```
[📋 Bảng] [📷 Thẻ]     ← Segmented control (cùng toolbar)
        ↓
Chế độ Bảng:  Danh sách dạng bảng nhiều cột
Chế độ Thẻ:   Grid thẻ hình ảnh bìa sách (FlowPane)
        ↓
Dữ liệu: cùng 1 danh sách sách đã lọc
Inspector Panel: vẫn hoạt động với cả 2 chế độ
```

### 4.2 Mượn sách — Bàn Lưu Hành vs Trợ Lý Wizard

- Màn hình Mượn Sách có **4 tab** cho phép chọn cách tiếp cận khác nhau:
  - **"Bàn Lưu Hành"** = Canvas Plus Palette (phù hợp thủ thư thành thạo, thao tác nhanh).
  - **"Trợ Lý Wizard"** = Wizard 4 bước (phù hợp người mới, được hướng dẫn từng bước).
  - Cả 2 đều cho ra kết quả giống nhau (tạo phiếu mượn) nhưng theo 2 phong cách khác nhau.

---

## 5. Wizard

> **Định nghĩa:** Chia một tác vụ phức tạp thành chuỗi các bước tuần tự, mỗi bước rõ ràng và tập trung, có nút Quay lại / Tiếp theo / Hoàn thành.

### Triển khai: Wizard Mượn Sách 4 Bước

Tab "Trợ Lý Wizard" trong màn hình Mượn Sách chia tác vụ mượn sách thành 4 bước:

| Bước | Nội dung | Giao diện |
|------|----------|-----------|
| **Bước 1** | **Chọn Độc Giả** | ComboBox chọn độc giả, hiển thị tên, trạng thái thẻ, hạn dùng thẻ, số sách đang mượn, hạn ngạch còn lại |
| **Bước 2** | **Chọn Sách** | Thanh tìm kiếm + danh sách sách khả dụng bên trái, danh sách sách đã chọn bên phải, counter "Đã chọn: X cuốn" |
| **Bước 3** | **Điều Khoản Mượn** | RadioButton chọn hình thức (Mang về / Tại chỗ), Spinner chọn số ngày, nhãn tự tính "Ngày trả dự kiến", ô ghi chú |
| **Bước 4** | **Xem Trước Phiếu Mượn** | Bản xem trước phiếu mượn format chuẩn, sẵn sàng bấm Hoàn thành |

**Điều hướng Stepper:**
- 4 **circle indicator** ở đầu wizard cho biết đang ở bước nào (bước hiện tại được highlight).
- Nhãn **"Bước 2 / 4"** hiển thị vị trí hiện tại.
- Nút **"Quay lại"** — quay về bước trước mà không mất dữ liệu.
- Nút **"Tiếp theo"** — chuyển sang bước kế tiếp (có kiểm tra hợp lệ trước khi cho qua).
- Nút **"Hoàn thành"** — chỉ hiện ở bước cuối cùng, xác nhận tạo phiếu mượn.
- Nút **"Hủy bỏ"** — thoát wizard bất cứ lúc nào.

**Minh họa:**
```
[1. Chọn ĐG] ──► [2. Chọn Sách] ──► [3. Điều Khoản] ──► [4. Phiếu Mượn]
     ○                ●                    ○                    ○
                   (active)                             
                  ◄── [Quay lại]    [Tiếp ►]                [✓ Hoàn thành]
```

---

## 6. Extras on Demand

> **Định nghĩa:** Ẩn các tính năng nâng cao hoặc ít dùng, chỉ hiển thị khi người dùng chủ động yêu cầu (nút expand/collapse), giữ giao diện chính gọn gàng.

### 6.1 Bộ lọc nâng cao (Kho Sách)

- Bên dưới thanh lọc chính có nút **"Lọc nâng cao ▾"**.
- Click vào → **mở ra panel ẩn** chứa thêm các bộ lọc: Thể loại, Kệ sách, Khoảng giá (từ – đến), Tình trạng tồn kho (Còn sách / Hết sách).
- Click lại → **thu gọn** panel, text đổi thành "Lọc nâng cao ▴".
- Có nút **"Reset bộ lọc"** để xóa toàn bộ filter nâng cao về mặc định.

### 6.2 Quy chế & Chế tài áp dụng (Bàn Lưu Hành)

- Trong tab Bàn Lưu Hành, có nút **"ℹ Quy chế & chế tài áp dụng ▾"**.
- Click vào → **mở ra panel ẩn** hiển thị thông tin quy chế phạt quá hạn (2.000 VNĐ/ngày), bồi thường mất sách (200% + phí xử lý), v.v. — để thủ thư tham khảo khi cần.
- Click lại → thu gọn, text đổi thành "ℹ Thu gọn quy chế ▴".

### 6.3 Submenu Thể Loại trong Sidebar

- Mục "Kho Sách" trong sidebar có **nút mũi tên nhỏ** (▸ / ▾) để mở/đóng danh sách thể loại phụ.
- Mở ra → hiện các **pill thể loại** (Văn học, Toán học, CNTT, Kỹ năng sống...).
- Click vào 1 pill → tự động lọc Kho Sách theo thể loại đó, pill được highlight active.
- Thu gọn lại khi không cần → sidebar gọn gàng.

### 6.4 Tinh Gọn Form Đổi Mật Khẩu (Modal Hồ Sơ Cá Nhân - v1.3.2)

- Trong modal hồ sơ cá nhân (`Profile Popup`), cụm trường đổi mật khẩu (Mật khẩu cũ, Mật khẩu mới, Xác nhận) được đóng gói gọn trong `passwordChangeBox` ẩn mặc định (`visible="false"`, `managed="false"`).
- Nút hành động **"🔑 Đổi mật khẩu ▾"** cho phép người dùng chủ động mở rộng vùng nhập khi có nhu cầu đổi mật khẩu, giúp giao diện hồ sơ luôn nhỏ gọn, không bị tràn cuộn dài.
- Click lần 2 thu gọn lại, text chuyển thành "🔑 Đổi mật khẩu ▴".

---

## 7. Intriguing Branches

> **Định nghĩa:** Cung cấp các "nhánh khám phá" hấp dẫn từ nội dung hiện tại, gợi ý người dùng khám phá thêm các mục liên quan, giữ sự hứng thú và tăng thời gian tương tác.

### 7.1 Nhánh "Cùng Kệ" & "Cùng Tác Giả" trong Inspector

Khi xem chi tiết 1 cuốn sách trong panel Inspector, phần dưới có khu vực **"NHÁNH LIÊN QUAN & GỢI Ý"** hiển thị:

- **📚 Cùng kệ: [Tên kệ]** — Hiển thị tối đa 3 cuốn sách khác nằm trên cùng kệ với cuốn đang xem. Mỗi cuốn hiện dưới dạng mini card (icon, tên, giá, tồn kho). Kèm link **"Xem tất cả ➔"** để drilldown toàn bộ sách cùng kệ.

- **✍ Cùng tác giả: [Tên tác giả]** — Hiển thị tối đa 3 cuốn sách khác cùng tác giả. Kèm link **"Xem tất cả ➔"** để drilldown toàn bộ sách cùng tác giả.

- **📜 Xem Lịch Sử Lưu Hành Cuốn Này** — Nút toàn chiều rộng, click để xem bảng giao dịch mượn/trả của cuốn sách đó (One-Window Drilldown).

- Click vào bất kỳ mini card gợi ý nào → **Inspector cập nhật sang cuốn sách đó**, bảng sách cuộn đến và highlight hàng tương ứng.

### 7.2 Cảnh Báo Sách Sắp Hết Hạn (Due Alert Banner)

- Trong tab Bàn Lưu Hành, khi chọn 1 độc giả có sách **sắp đến hạn trả hoặc quá hạn** (trong vòng 2 ngày), hệ thống hiện **banner cảnh báo vàng/đỏ** ở đầu trang.
- Banner thông báo: *"⚠️ Độc giả có X cuốn sách sắp đến hạn trả (hoặc quá hạn): Vui lòng nhắc nhở độc giả gia hạn hoặc mang trả!"*
- Kèm nút **"Chuyển sang tab Trả sách"** → click thì tự động chuyển sang tab Trả Sách và tự chọn giao dịch của độc giả đó — dẫn dắt thủ thư vào hành động phù hợp.

**Minh họa Intriguing Branches trong Inspector:**
```
┌──────────────────────────────────┐
│ 📖 Đồi Gió Hú                   │
│ Phan Trọng • Văn học kinh điển   │
│ Kệ: Khu A - Kệ 01              │
│ [Available] [Mượn] [Sửa] [Xóa]  │
│                                  │
│ ─── NHÁNH LIÊN QUAN & GỢI Ý ─── │
│                                  │
│ 📚 Cùng kệ: Khu A - Kệ 01      │
│   [Xem tất cả ➔]                │
│   📖 Cánh Đồng Hoang  50k 3/5   │ ← Click → chuyển sang sách này
│   📖 Tư Tưởng HCM     65k 5/5   │
│                                  │
│ ✍ Cùng tác giả: Phan Trọng      │
│   [Xem tất cả ➔]                │
│   📖 Sách khác...                │ ← Click → lọc cùng tác giả
│                                  │
│ [📜 Xem Lịch Sử Lưu Hành Này]   │ ← Click → drilldown lịch sử
└──────────────────────────────────┘
```

---

## Bảng Tổng Hợp

| # | Pattern | Nơi triển khai chính | Đánh giá |
|---|---------|---------------------|----------|
| 1 | **Two-Panel Selector** | Kho Sách: bảng sách bên trái + panel chi tiết bên phải (68:32) kèm cơ chế Single/Double Click | ✅ Hoàn chỉnh |
| 2 | **Canvas Plus Palette** | Bàn Lưu Hành: danh sách sách khả dụng (Palette) ↔ giỏ lưu hành (Canvas) | ✅ Hoàn chỉnh |
| 3 | **One-Window Drilldown** | Lọc theo tác giả/kệ + Lịch sử lưu hành + Breadcrumb quay lại | ✅ Hoàn chỉnh |
| 4 | **Alternative Views** | Bảng ↔ Thẻ ảnh cho Kho Sách + Bàn LH vs Wizard cho Mượn sách | ✅ Hoàn chỉnh |
| 5 | **Wizard** | Wizard Mượn Sách 4 bước với stepper, Quay lại/Tiếp/Hoàn thành | ✅ Hoàn chỉnh |
| 6 | **Extras on Demand** | Bộ lọc nâng cao toggle + Quy chế toggle + Submenu thể loại + Toggle mật khẩu | ✅ Hoàn chỉnh |
| 7 | **Intriguing Branches** | Cùng Kệ / Cùng Tác Giả gợi ý + Cảnh báo sắp hạn → tab Trả | ✅ Hoàn chỉnh |

**Kết luận:** Tất cả 7 Interaction Design Patterns đều được triển khai **hoàn chỉnh** trong hệ thống LibMan.

---

## Phụ Lục: Đối Chiếu Với Đặc Tả SRS 8 Trang

| Yêu cầu SRS | Tính năng ứng dụng | Trạng thái |
|-------------|-------------------|------------|
| 5.1 Đăng nhập | Modal đăng nhập overlay + phân quyền RBAC 3 vai trò (Giám đốc, Thủ thư, Độc giả) + Lời chào động | ✅ Thỏa mãn |
| 5.2 Đăng ký | Form đăng ký + tự tạo hồ sơ độc giả "Chờ Cấp Thẻ" | ✅ Thỏa mãn |
| 5.3 Quản lý nhập sách | CRUD sách đầy đủ 221 cuốn với 100% ảnh bìa thật xuất bản từ web (0% bìa tự sinh) + Quản lý thể loại | ✅ Thỏa mãn |
| 5.4 Quản lý độc giả | CRUD độc giả 38 thành viên đầy đủ phân nhóm (SV/GV/NCS/Tự do) & trạng thái + quét thẻ hết hạn | ✅ Thỏa mãn |
| 5.5 Quản lý mượn/trả sách | Mượn trả 28 giao dịch gần đây + 2 hình thức mượn + in ấn phiếu mượn chuẩn | ✅ Thỏa mãn |
| 5.6 Thống kê | Tổng sách, độc giả, giao dịch, quá hạn + phân tích theo thể loại + 4 thẻ KPI click điều hướng | ✅ Thỏa mãn |
| 5.7 Tìm kiếm | Autocomplete search ở header + bộ lọc inline mỗi trang | ✅ Thỏa mãn |
| 6.1 Yêu cầu thực thi | Xuất CSV danh sách sách/độc giả + thay đổi quy định thư viện động | ✅ Thỏa mãn |
| 6.2 Yêu cầu an toàn | Thùng Rác: Phục hồi nguyên trạng + Hủy vĩnh viễn | ✅ Thỏa mãn |
| 6.3 Yêu cầu bảo mật | RBAC 3 cấp: Quản trị / Thủ thư / Độc giả (+ Khách) | ✅ Thỏa mãn |
| 6.4 Chất lượng phần mềm | Kiến trúc MVC modular + SQLite đa nền tảng + 2 Theme + 56 bài test tự động (100% PASS) | ✅ Thỏa mãn |

---

*Báo cáo được lập cho mục đích báo cáo học phần Công Nghệ Phần Mềm — Dự án LibMan.*
