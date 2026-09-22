package com.vithay.libman.service;

import com.vithay.libman.model.Book;
import com.vithay.libman.model.BorrowTransaction;
import com.vithay.libman.model.Reader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ExportService {
    private static final Logger logger = LoggerFactory.getLogger(ExportService.class);
    private final BookService bookService = new BookService();
    private final ReaderService readerService = new ReaderService();
    private final BorrowService borrowService = new BorrowService();

    public boolean exportBooksToCsv(File targetFile) {
        List<Book> books = bookService.getAllBooks();
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(targetFile), StandardCharsets.UTF_8))) {
            // Write BOM for Excel UTF-8 compatibility
            bw.write('\ufeff');
            bw.write("Mã Sách,Tên Sách,Tác Giả,Thể Loại,Vị Trí Kệ,ISBN,Đơn Giá,Năm XB,Tổng Bản,Khả Dụng,Trạng Thái\n");
            for (Book b : books) {
                bw.write(String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",%.0f,%d,%d,%d,\"%s\"\n",
                        escapeCsv(b.getId()),
                        escapeCsv(b.getTitle()),
                        escapeCsv(b.getAuthor()),
                        escapeCsv(b.getCategory()),
                        escapeCsv(b.getShelfLocation()),
                        escapeCsv(b.getIsbn()),
                        b.getPrice(),
                        b.getPublishYear(),
                        b.getTotalCopies(),
                        b.getAvailableCopies(),
                        escapeCsv(b.getStatus())
                ));
            }
            return true;
        } catch (Exception e) {
            logger.error("Error exporting books to CSV", e);
            return false;
        }
    }

    public boolean exportReadersToCsv(File targetFile) {
        List<Reader> readers = readerService.getAllReaders();
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(targetFile), StandardCharsets.UTF_8))) {
            bw.write('\ufeff');
            bw.write("Mã Độc Giả,Họ Và Tên,Email,Số Điện Thoại,CCCD/CMND,Địa Chỉ,Ngày Sinh,Ngày Cấp Thẻ,Ngày Hết Hạn,Trạng Thái\n");
            for (Reader r : readers) {
                bw.write(String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"\n",
                        escapeCsv(r.getId()),
                        escapeCsv(r.getFullName()),
                        escapeCsv(r.getEmail()),
                        escapeCsv(r.getPhone()),
                        escapeCsv(r.getIdCard()),
                        escapeCsv(r.getAddress()),
                        escapeCsv(r.getBirthDate()),
                        escapeCsv(r.getCardIssueDate()),
                        escapeCsv(r.getCardExpiryDate()),
                        escapeCsv(r.getStatus())
                ));
            }
            return true;
        } catch (Exception e) {
            logger.error("Error exporting readers to CSV", e);
            return false;
        }
    }

    public String generateBorrowSlip(BorrowTransaction tx) {
        StringBuilder sb = new StringBuilder();
        sb.append("========================================================\n");
        sb.append("               THƯ VIỆN ĐẠI HỌC - LIBMAN                \n");
        sb.append("                  PHIẾU MƯỢN SÁCH                       \n");
        sb.append("========================================================\n\n");
        sb.append("Mã phiếu mượn:    ").append(tx.getId()).append("\n");
        sb.append("Hình thức mượn:   ").append(tx.getBorrowType()).append("\n");
        sb.append("Thời gian lập:    ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n");
        sb.append("--------------------------------------------------------\n");
        sb.append("THÔNG TIN ĐỘC GIẢ:\n");
        sb.append(" - Mã độc giả:    ").append(tx.getReaderId()).append("\n");
        sb.append(" - Họ và tên:     ").append(tx.getReaderName()).append("\n");
        sb.append("--------------------------------------------------------\n");
        sb.append("THÔNG TIN ẤN PHẨM MƯỢN:\n");
        sb.append(" - Mã sách:       ").append(tx.getBookId()).append("\n");
        sb.append(" - Tên tác phẩm:  ").append(tx.getBookTitle()).append("\n");
        sb.append(" - Ngày mượn:     ").append(tx.getBorrowDate()).append("\n");
        sb.append(" - HẠN TRẢ SÁCH:  ").append(tx.getDueDate()).append("\n");
        sb.append(" - Ghi chú:       ").append(tx.getNotes() != null ? tx.getNotes() : "Không có").append("\n");
        sb.append("--------------------------------------------------------\n");
        sb.append("QUY ĐỊNH BẢO QUẢN:\n");
        sb.append(" 1. Trả sách đúng hạn quy định (Quá hạn phạt 2.000đ/ngày).\n");
        sb.append(" 2. Không làm rách, viết, bôi bẩn lên sách (Mất đền 200%).\n\n");
        sb.append("      ĐỘC GIẢ KÝ TÊN                  THỦ THƯ XÁC NHẬN  \n");
        sb.append("    (Ký và ghi rõ họ tên)           (Ký và ghi rõ họ tên)\n\n\n");
        sb.append("========================================================\n");
        return sb.toString();
    }

    public String generateBasketBorrowSlip(Reader reader, List<BorrowTransaction> transactions) {
        StringBuilder sb = new StringBuilder();
        sb.append("======================================================================\n");
        sb.append("                      THƯ VIỆN ĐẠI HỌC - LIBMAN                       \n");
        sb.append("               PHIẾU MƯỢN SÁCH TỔNG HỢP (BÀN LƯU HÀNH)                \n");
        sb.append("======================================================================\n\n");
        sb.append("Thời gian lập:    ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n");
        sb.append("Số lượng sách:    ").append(transactions.size()).append(" cuốn\n");
        sb.append("----------------------------------------------------------------------\n");
        sb.append("THÔNG TIN ĐỘC GIẢ:\n");
        sb.append(" - Mã độc giả:    ").append(reader.getId()).append("\n");
        sb.append(" - Họ và tên:     ").append(reader.getFullName()).append("\n");
        sb.append(" - Email/SĐT:     ").append(reader.getEmail() != null ? reader.getEmail() : "").append(" / ")
                .append(reader.getPhone() != null ? reader.getPhone() : "").append("\n");
        sb.append("----------------------------------------------------------------------\n");
        sb.append("DANH SÁCH TÁC PHẨM MƯỢN:\n");
        int idx = 1;
        for (BorrowTransaction tx : transactions) {
            sb.append(String.format(" %d. [%s] %s\n", idx++, tx.getBookId(), tx.getBookTitle()));
            sb.append(String.format("    - Mã phiếu: %s | Hình thức: %s\n", tx.getId(), tx.getBorrowType()));
            sb.append(String.format("    - Ngày mượn: %s | HẠN TRẢ: %s\n", tx.getBorrowDate(), tx.getDueDate()));
        }
        sb.append("----------------------------------------------------------------------\n");
        sb.append("QUY ĐỊNH BẢO QUẢN & CHẾ TÀI:\n");
        sb.append(" 1. Trả sách đúng hạn quy định (Quá hạn phạt 2.000đ/ngày/cuốn).\n");
        sb.append(" 2. Không làm rách, gạch xóa, hư hỏng hoặc làm mất sách.\n");
        sb.append(" 3. Bồi hoàn sách mất: 200% giá gốc + 20.000đ lệ phí kỹ thuật.\n\n");
        sb.append("         ĐỘC GIẢ KÝ TÊN                       THỦ THƯ XÁC NHẬN        \n");
        sb.append("       (Ký và ghi rõ họ tên)                (Ký và ghi rõ họ tên)     \n\n\n");
        sb.append("      .........................            .........................  \n");
        sb.append("======================================================================\n");
        return sb.toString();
    }

    private String escapeCsv(String val) {
        if (val == null) return "";
        return val.replace("\"", "\"\"");
    }
}
