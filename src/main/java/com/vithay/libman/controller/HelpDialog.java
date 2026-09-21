package com.vithay.libman.controller;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class HelpDialog {

    public static void showHelp() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Hướng Dẫn Sử Dụng Hệ Thống - LibMan");
        dialog.setHeaderText("Cẩm Nang Vận Hành Hệ Thống Quản Lý Thư Viện (SRS)");

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getStylesheets().add(HelpDialog.class.getResource("/com/vithay/libman/css/style.css").toExternalForm());
        dialogPane.getStyleClass().add("bg-surface");
        dialogPane.getButtonTypes().add(ButtonType.CLOSE);

        VBox content = new VBox(12);
        content.setPadding(new Insets(16));
        content.setPrefWidth(640);

        TextArea txt = new TextArea();
        txt.setEditable(false);
        txt.setWrapText(true);
        txt.setPrefHeight(420);
        txt.getStyleClass().add("form-control");
        txt.setStyle("-fx-font-family: monospace; -fx-font-size: 12px; -fx-text-fill: #FFFFFF;");

        String guide = """
========================================================================
             HƯỚNG DẪN SỬ DỤNG HỆ THỐNG QUẢN LÝ THƯ VIỆN LIBMAN
========================================================================

1. PHÂN QUYỀN NGƯỜI DÙNG (RBAC - SRS Mục 6.3):
   • Giám Đốc (Quản trị):
     - Toàn quyền cấu hình quy chế thư viện (mượn tối đa, phạt, bồi hoàn).
     - Quản lý thể loại sách, quản lý người dùng và phân quyền.
     - Khôi phục dữ liệu từ Thùng rác hoặc hủy vĩnh viễn.
   • Thủ Thư:
     - Tiếp nhận sách mới, biên mục và sắp xếp theo vị trí kệ sách.
     - Lập thẻ độc giả mới, gia hạn hoặc hủy thẻ độc giả hết hạn.
     - Lập phiếu mượn (đọc tại chỗ hoặc mang về), xác nhận nhận trả sách.
     - Lập báo cáo thống kê, xuất file danh sách CSV và in phiếu mượn.
   • Độc Giả:
     - Tra cứu thông tin sách theo tên, tác giả, thể loại, kệ sách.
     - Xem lịch sử mượn trả và tình trạng khả dụng của tài liệu.

2. QUẢN LÝ SÁCH & THỂ LOẠI (SRS Mục 5.3):
   - Thêm sách mới: Điền mã sách, tên sách, tác giả, chọn thể loại, số lượng bản, đơn giá và vị trí kệ sách (VD: Khu A - Kệ 01).
   - Xuất dữ liệu: Nhấn nút "Xuất File CSV" tại màn hình Quản Lý Sách để xuất bảng phục vụ kiểm kê hoặc in ấn.
   - Xóa sách: Sách bị xóa sẽ chuyển vào Thùng Rác để đảm bảo an toàn dữ liệu.

3. QUẢN LÝ ĐỘC GIẢ (SRS Mục 5.4):
   - Nhập thông tin: Họ tên, Ngày sinh, CCCD, Địa chỉ, SĐT, Ngày cấp thẻ, Ngày hết hạn thẻ.
   - Quét thẻ hết hạn: Nhấn "Quét Độc Giả Hết Hạn" để hệ thống tự động nhận diện và chuyển trạng thái thẻ quá hạn theo quy định.

4. MƯỢN & TRẢ SÁCH (SRS Mục 1 & 5.5):
   - Hai hình thức mượn:
     + Mượn đọc tại chỗ: Đọc trong phòng đọc thư viện trong ngày.
     + Mượn mang về nhà: Thời hạn tiêu chuẩn 14 ngày (tùy biến tối đa 90 ngày).
   - Quy định mượn: Mỗi độc giả mượn tối đa số sách quy định (mặc định 5 cuốn). Độc giả bị khóa hoặc thẻ hết hạn không thể mượn sách.
   - Trả sách & Phạt:
     + Trả quá hạn: Tự động tính 2.000 VNĐ / ngày quá hạn / cuốn.
     + Mất hoặc hư hỏng nặng: Bồi thường 200% giá sách + 20.000 VNĐ phí xử lý.
   - In Phiếu Mượn: Có thể xuất phiếu mượn chuẩn có chữ ký hai bên.

5. THÙNG RÁC & AN TOÀN DỮ LIỆU (SRS Mục 6.2):
   - Các bản ghi sách và độc giả xóa tạm có thể được Khôi Phục (Restore) nguyên trạng hoặc Hủy Vĩnh Viễn khi cần dọn dẹp bộ nhớ.
""";
        txt.setText(guide);

        content.getChildren().add(txt);
        dialogPane.setContent(content);

        dialog.showAndWait();
    }
}
