#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Populate at least 36 diverse readers and 25+ borrow transactions into LibMan database and seed_data.sql.
"""

import sqlite3
import re

DB_PATH = "libman.db"
SEED_SQL_PATH = "src/main/resources/com/vithay/libman/database/seed_data.sql"

READERS = [
    ('RD001', 'Lê Văn An', 'an.le@gmail.com', '0912345678', '123 Nguyễn Văn Linh, Q. Hải Châu, TP. Đà Nẵng', '048201012345', '2001-05-12', '2024-01-01', '2024-01-01', '2026-12-31', 'Active', 0),
    ('RD002', 'Trần Thị Mai Anh', 'mai.tran@gmail.com', '0987654321', '45 Lê Duẩn, Q. Hải Châu, TP. Đà Nẵng', '048202023456', '2002-08-20', '2024-01-01', '2024-01-01', '2026-12-31', 'Active', 0),
    ('DG001', 'Trần Văn An', 'an.tv@gmail.com', '0905123456', '88 Bạch Đằng, Q. Hải Châu, TP. Đà Nẵng', '048067001258', '1967-01-15', '2024-01-10', '2024-01-10', '2026-12-31', 'Active', 0),
    ('DG002', 'Nguyễn Văn Nhân', 'nhan.nv@gmail.com', '0905234567', '250 Võ Nguyên Giáp, Q. Sơn Trà, TP. Đà Nẵng', '048087001475', '1987-04-13', '2024-01-10', '2024-01-10', '2026-12-31', 'Active', 0),
    ('DG003', 'Lê Thị Thu Nhàn', 'nhan.lt@gmail.com', '0905345678', '12 Núi Thành, Q. Hải Châu, TP. Đà Nẵng', '048078001485', '1978-08-27', '2024-01-10', '2024-01-10', '2026-12-31', 'Active', 0),
    ('DG004', 'Phùng Tuấn Kiệt', 'kiet.pt@gmail.com', '0905456789', '68 Điện Biên Phủ, Q. Thanh Khê, TP. Đà Nẵng', '048085001236', '1985-12-02', '2024-01-10', '2024-01-10', '2024-06-01', 'Expired', 0),
    ('DG005', 'Hoàng Minh Châu', 'chau.hm@gmail.com', '0918776655', '35 Ngô Quyền, Q. Sơn Trà, TP. Đà Nẵng', '048204056789', '2004-11-18', '2026-03-01', None, None, 'Chờ Cấp Thẻ', 0),
    ('DG006', 'Vũ Đức Thịnh', 'thinh.vd@gmail.com', '0977223344', '102 Tôn Đức Thắng, Q. Liên Chiểu, TP. Đà Nẵng', '048203098765', '2003-07-22', '2026-03-02', None, None, 'Chờ Cấp Thẻ', 0),
    ('DG007', 'Phạm Thu Hà', 'ha.pham@dut.udn.vn', '0905112233', '15 Quang Trung, Q. Hải Châu, TP. Đà Nẵng', '048202001122', '2002-03-14', '2024-02-15', '2024-02-15', '2026-12-31', 'Active', 0),
    ('DG008', 'Đặng Hoàng Long', 'long.dh@gmail.com', '0935445566', '224 Hùng Vương, Q. Hải Châu, TP. Đà Nẵng', '048201004455', '2001-09-30', '2024-02-20', '2024-02-20', '2026-12-31', 'Active', 0),
    ('DG009', 'Bùi Phương Thảo', 'thao.bp@udn.vn', '0905778899', '56 Nguyễn Tri Phương, Q. Thanh Khê, TP. Đà Nẵng', '048182003344', '1982-10-05', '2024-01-05', '2024-01-05', '2027-01-05', 'Active', 0),
    ('DG010', 'Ngô Quốc Bảo', 'bao.nq@gmail.com', '0988112233', '77 Hoàng Diệu, Q. Hải Châu, TP. Đà Nẵng', '048203007788', '2003-04-18', '2024-03-01', '2024-03-01', '2026-12-31', 'Active', 0),
    ('DG011', 'Đỗ Minh Khang', 'khang.dm@gmail.com', '0919334455', '89 Trưng Nữ Vương, Q. Hải Châu, TP. Đà Nẵng', '048200008899', '2000-11-25', '2024-01-15', '2024-01-15', '2026-12-31', 'Bị Khóa', 0),
    ('DG012', 'Trịnh Quỳnh Nga', 'nga.tq@gmail.com', '0979556677', '140 Lê Lợi, Q. Hải Châu, TP. Đà Nẵng', '048202009900', '2002-12-08', '2024-02-10', '2024-02-10', '2026-12-31', 'Active', 0),
    ('DG013', 'Lâm Đình Phong', 'phong.ld@udn.vn', '0903221144', '312 Cách Mạng Tháng 8, Q. Cẩm Lệ, TP. Đà Nẵng', '048179001133', '1979-06-19', '2024-01-01', '2024-01-01', '2027-01-01', 'Active', 0),
    ('DG014', 'Đinh Diệu Linh', 'linh.dd@gmail.com', '0966443322', '45 Phan Châu Trinh, Q. Hải Châu, TP. Đà Nẵng', '048197005566', '1997-02-28', '2024-02-01', '2024-02-01', '2026-12-31', 'Active', 0),
    ('DG015', 'Phan Anh Tuấn', 'tuan.pa@gmail.com', '0944889900', '58 Ông Ích Khiêm, Q. Hải Châu, TP. Đà Nẵng', '048083002244', '1983-05-17', '2023-01-10', '2023-01-10', '2024-01-10', 'Expired', 0),
    ('DG016', 'Trương Mỹ Dung', 'dung.tm@gmail.com', '0914667788', '92 Trần Phú, Q. Hải Châu, TP. Đà Nẵng', '048203001234', '2003-08-11', '2024-03-05', '2024-03-05', '2026-12-31', 'Active', 0),
    ('DG017', 'Lý Gia Huy', 'huy.lg@gmail.com', '0978332211', '183 Nguyễn Thị Minh Khai, Q. Hải Châu, TP. Đà Nẵng', '048205004321', '2005-01-20', '2026-03-10', None, None, 'Chờ Cấp Thẻ', 0),
    ('DG018', 'Đoàn Thanh Tùng', 'tung.dt@gmail.com', '0905998877', '67 3 Tháng 2, Q. Hải Châu, TP. Đà Nẵng', '048201009876', '2001-07-04', '2024-01-20', '2024-01-20', '2026-12-31', 'Active', 0),
    ('DG019', 'Mai Hoàng Yến', 'yen.mh@udn.vn', '0932114477', '28 Lý Thường Kiệt, Q. Hải Châu, TP. Đà Nẵng', '048185006543', '1985-09-12', '2024-01-15', '2024-01-15', '2027-01-15', 'Active', 0),
    ('DG020', 'Võ Khánh Vy', 'vy.vk@gmail.com', '0983665544', '51 Pastuer, Q. Hải Châu, TP. Đà Nẵng', '048204008765', '2004-03-22', '2024-02-18', '2024-02-18', '2026-12-31', 'Active', 0),
    ('DG021', 'Nguyễn Thành Đạt', 'dat.nt@gmail.com', '0917228833', '105 Yên Bái, Q. Hải Châu, TP. Đà Nẵng', '048202003456', '2002-10-15', '2024-01-25', '2024-01-25', '2026-12-31', 'Active', 0),
    ('DG022', 'Trần Bảo Trâm', 'tram.tb@gmail.com', '0945771122', '74 Trần Quý Cáp, Q. Hải Châu, TP. Đà Nẵng', '048092004567', '1992-04-03', '2024-02-05', '2024-02-05', '2026-12-31', 'Bị Khóa', 0),
    ('DG023', 'Hồ Văn Cường', 'cuong.hv@gmail.com', '0908339911', '19 Tô Hiến Thành, Q. Sơn Trà, TP. Đà Nẵng', '048205007890', '2005-06-16', '2026-03-12', None, None, 'Chờ Cấp Thẻ', 0),
    ('DG024', 'Lê Ngọc Hân', 'han.ln@gmail.com', '0967884422', '83 Hoàng Văn Thụ, Q. Hải Châu, TP. Đà Nẵng', '048198002345', '1998-11-09', '2024-01-18', '2024-01-18', '2026-12-31', 'Active', 0),
    ('DG025', 'Dương Gia Bảo', 'bao.dg@gmail.com', '0934551188', '216 Nguyễn Lương Bằng, Q. Liên Chiểu, TP. Đà Nẵng', '048203006789', '2003-05-27', '2024-02-22', '2024-02-22', '2026-12-31', 'Active', 0),
    ('DG026', 'Chu Thị Kim Ngân', 'ngan.ck@udn.vn', '0912446688', '42 Đống Đa, Q. Hải Châu, TP. Đà Nẵng', '048180005678', '1980-12-30', '2024-01-08', '2024-01-08', '2027-01-08', 'Active', 0),
    ('DG027', 'Tạ Quang Khải', 'khai.tq@gmail.com', '0975113355', '118 Lê Đình Lý, Q. Thanh Khê, TP. Đà Nẵng', '048201007891', '2001-01-19', '2024-01-28', '2024-01-28', '2026-12-31', 'Active', 0),
    ('DG028', 'Vũ Thị Cẩm Tú', 'tu.vt@gmail.com', '0909224466', '33 Nguyễn Du, Q. Hải Châu, TP. Đà Nẵng', '048200001235', '2000-08-14', '2023-02-15', '2023-02-15', '2024-02-15', 'Expired', 0),
    ('DG029', 'Nghiêm Xuân Mạnh', 'manh.nx@gmail.com', '0943882200', '65 Hà Huy Tập, Q. Thanh Khê, TP. Đà Nẵng', '048088009876', '1988-07-21', '2024-02-12', '2024-02-12', '2026-12-31', 'Active', 0),
    ('DG030', 'Quách Thị Lan', 'lan.qt@gmail.com', '0981557799', '142 Huỳnh Thúc Kháng, Q. Hải Châu, TP. Đà Nẵng', '048204003457', '2004-09-02', '2024-03-08', '2024-03-08', '2026-12-31', 'Active', 0),
    ('DG031', 'Vương Đình Trọng', 'trong.vd@udn.vn', '0916335577', '79 Tiểu La, Q. Hải Châu, TP. Đà Nẵng', '048177004321', '1977-03-08', '2024-01-12', '2024-01-12', '2027-01-12', 'Active', 0),
    ('DG032', 'Lương Thảo My', 'my.lt@gmail.com', '0962779911', '90 Phan Bội Châu, Q. Hải Châu, TP. Đà Nẵng', '048205006544', '2005-12-10', '2026-03-14', None, None, 'Chờ Cấp Thẻ', 0),
    ('DG033', 'Đào Hữu Phước', 'phuoc.dh@gmail.com', '0937116644', '168 Dũng Sĩ Thanh Khê, Q. Thanh Khê, TP. Đà Nẵng', '048196008765', '1996-04-26', '2024-01-22', '2024-01-22', '2026-12-31', 'Active', 0),
    ('DG034', 'Thái Bá Duy', 'duy.tb@gmail.com', '0901447722', '27 Duy Tân, Q. Hải Châu, TP. Đà Nẵng', '048202005432', '2002-06-17', '2024-02-25', '2024-02-25', '2026-12-31', 'Bị Khóa', 0),
    ('DG035', 'Huỳnh Ánh Nguyệt', 'nguyet.ha@udn.vn', '0974883311', '84 Lê Thanh Nghị, Q. Hải Châu, TP. Đà Nẵng', '048184007654', '1984-05-03', '2024-01-14', '2024-01-14', '2027-01-14', 'Active', 0),
    ('DG036', 'Bạch Hồng Quân', 'quan.bh@gmail.com', '0948225588', '111 Nguyễn Hữu Thọ, Q. Hải Châu, TP. Đà Nẵng', '048090003210', '1990-10-29', '2024-02-08', '2024-02-08', '2026-12-31', 'Active', 0)
]

TRANSACTIONS = [
    ('211200001', 'RD001', 'Lê Văn An', 'B001', 'Sapiens: Lược Sử Loài Người', '2026-03-01', '2026-03-15', None, 'Mang về nhà', 'Đang Mượn', 0.0, 'Độc giả mượn tại quầy số 1'),
    ('211200002', 'RD001', 'Lê Văn An', 'B002', 'Dune: Xứ Cát', '2026-03-02', '2026-03-16', None, 'Mang về nhà', 'Đang Mượn', 0.0, 'Bản bìa cứng có minh họa'),
    ('211200003', 'RD001', 'Lê Văn An', 'B004', 'Atomic Habits: Thay Đổi Tí Hon', '2026-03-05', '2026-03-19', None, 'Mang về nhà', 'Đang Mượn', 0.0, 'Gia hạn trực tuyến'),
    ('211200004', 'RD001', 'Lê Văn An', 'B005', '1984', '2026-03-10', '2026-03-24', None, 'Mượn đọc tại chỗ', 'Đang Mượn', 0.0, 'Đọc tại phòng chuyên khảo'),
    ('211200005', 'RD002', 'Trần Thị Mai Anh', 'BK010', 'Clean Code: Mã Sạch', '2026-03-08', '2026-03-22', None, 'Mang về nhà', 'Đang Mượn', 0.0, 'Sinh viên IT mượn làm đồ án'),
    ('211200006', 'RD002', 'Trần Thị Mai Anh', 'BK011', 'Design Patterns: Elements of Reusable Object-Oriented Software', '2026-03-08', '2026-03-22', None, 'Mang về nhà', 'Đang Mượn', 0.0, 'Mượn cùng Clean Code'),
    ('211200007', 'DG001', 'Trần Văn An', 'BK016', 'Đại Việt Sử Ký Toàn Thư', '2026-02-20', '2026-03-06', '2026-03-05', 'Mang về nhà', 'Đã Trả', 0.0, 'Trả đúng hạn, sách nguyên vẹn'),
    ('211200008', 'DG002', 'Nguyễn Văn Nhân', 'BK012', 'Designing Data-Intensive Applications', '2026-02-15', '2026-03-01', '2026-03-01', 'Mang về nhà', 'Đã Trả', 0.0, 'Giảng viên nghiên cứu'),
    ('211200009', 'DG003', 'Lê Thị Thu Nhàn', 'BK018', 'Tư Duy Nhanh Và Chậm (Thinking, Fast and Slow)', '2026-03-06', '2026-03-20', None, 'Mang về nhà', 'Đang Mượn', 0.0, 'Tài liệu tham khảo tâm lý'),
    ('211200010', 'DG007', 'Phạm Thu Hà', 'BK023', 'English Grammar in Use (5th Edition)', '2026-03-12', '2026-03-26', None, 'Mang về nhà', 'Đang Mượn', 0.0, 'Học phần Anh văn nâng cao'),
    ('211200011', 'DG008', 'Đặng Hoàng Long', 'BK009', 'Tam Thể (The Three-Body Problem)', '2026-02-10', '2026-02-24', '2026-02-24', 'Mang về nhà', 'Đã Trả', 0.0, 'Độc giả yêu thích SF'),
    ('211200012', 'DG008', 'Đặng Hoàng Long', 'BK045', 'Mắt Biếc', '2026-03-14', '2026-03-28', None, 'Mang về nhà', 'Đang Mượn', 0.0, 'Mượn đọc cuối tuần'),
    ('211200013', 'DG009', 'Bùi Phương Thảo', 'BK013', 'Introduction to Algorithms (CLRS)', '2026-03-02', '2026-03-16', None, 'Mang về nhà', 'Đang Mượn', 0.0, 'Giảng dạy Thuật toán'),
    ('211200014', 'DG010', 'Ngô Quốc Bảo', 'BK032', 'Harry Potter và Hòn Đá Phù Thủy', '2026-03-11', '2026-03-25', None, 'Mang về nhà', 'Đang Mượn', 0.0, 'Bản dịch NXB Trẻ'),
    ('211200015', 'DG011', 'Đỗ Minh Khang', 'BK017', 'Súng, Vi Trùng Và Thép (Guns, Germs, and Steel)', '2026-01-10', '2026-01-24', None, 'Mang về nhà', 'Quá Hạn', 118000.0, 'Quá hạn 59 ngày, thẻ tạm khóa'),
    ('211200016', 'DG012', 'Trịnh Quỳnh Nga', 'BK020', 'Khi Hơi Thở Hóa Thinh Không (When Breath Becomes Air)', '2026-03-04', '2026-03-18', None, 'Mang về nhà', 'Đang Mượn', 0.0, 'Đọc tại chỗ chuyển sang về nhà'),
    ('211200017', 'DG013', 'Lâm Đình Phong', 'BK039', 'Clean Architecture: Kiến Trúc Sạch', '2026-03-07', '2026-03-21', None, 'Mang về nhà', 'Đang Mượn', 0.0, 'Tham khảo đồ án tốt nghiệp'),
    ('211200018', 'DG014', 'Đinh Diệu Linh', 'BK021', 'Trăm Năm Cô Đơn (One Hundred Years of Solitude)', '2026-03-09', '2026-03-23', None, 'Mang về nhà', 'Đang Mượn', 0.0, 'Nghiên cứu sinh văn học'),
    ('211200019', 'DG016', 'Trương Mỹ Dung', 'BK033', 'Totto-chan Bên Cửa Sổ', '2026-03-15', '2026-03-29', None, 'Mang về nhà', 'Đang Mượn', 0.0, 'Sách giáo dục Nhật Bản'),
    ('211200020', 'DG018', 'Đoàn Thanh Tùng', 'BK073', 'Kafka Bên Bờ Biển (Kafka on the Shore)', '2026-02-28', '2026-03-14', '2026-03-13', 'Mang về nhà', 'Đã Trả', 0.0, 'Đã trả đúng hẹn'),
    ('211200021', 'DG019', 'Mai Hoàng Yến', 'BK106', 'Nhà Giả Kim (The Alchemist)', '2026-03-10', '2026-03-24', None, 'Mang về nhà', 'Đang Mượn', 0.0, 'Mượn đọc truyền cảm hứng'),
    ('211200022', 'DG020', 'Võ Khánh Vy', 'BK027', 'Doraemon - Tuyển Tập Tranh Truyện Màu', '2026-03-13', '2026-03-13', '2026-03-13', 'Mượn đọc tại chỗ', 'Đã Trả', 0.0, 'Đọc tại phòng đọc thiếu nhi'),
    ('211200023', 'DG021', 'Nguyễn Thành Đạt', 'BK150', 'Lược Sử Thời Gian (A Brief History of Time)', '2026-03-03', '2026-03-17', None, 'Mang về nhà', 'Đang Mượn', 0.0, 'Sách vật lý thiên văn'),
    ('211200024', 'DG022', 'Trần Bảo Trâm', 'BK084', 'Mật Mã Da Vinci (The Da Vinci Code)', '2026-01-15', '2026-01-29', None, 'Mang về nhà', 'Quá Hạn', 108000.0, 'Quá hạn 54 ngày, gửi thông báo phạt'),
    ('211200025', 'DG024', 'Lê Ngọc Hân', 'BK161', 'Giận (Anger: Wisdom for Cooling the Flames)', '2026-03-01', '2026-03-15', '2026-03-14', 'Mang về nhà', 'Đã Trả', 0.0, 'Đã trả, sách giữ gìn cẩn thận'),
    ('211200026', 'DG025', 'Dương Gia Bảo', 'BK144', 'Steve Jobs: Tiểu Sử', '2026-03-09', '2026-03-23', None, 'Mang về nhà', 'Đang Mượn', 0.0, 'Tiểu sử doanh nhân'),
    ('211200027', 'DG026', 'Chu Thị Kim Ngân', 'BK024', 'Oxford Advanced Learners Dictionary', '2026-03-05', '2026-03-05', '2026-03-05', 'Mượn đọc tại chỗ', 'Đã Trả', 0.0, 'Tra cứu từ điển tại chỗ'),
    ('211200028', 'DG027', 'Tạ Quang Khải', 'BK179', 'Tiếu Ngạo Giang Hồ', '2026-03-12', '2026-03-26', None, 'Mang về nhà', 'Đang Mượn', 0.0, 'Mượn tiểu thuyết Kim Dung')
]

def main():
    conn = sqlite3.connect(DB_PATH)
    c = conn.cursor()
    
    # Insert or replace readers
    c.executemany("""
        INSERT OR REPLACE INTO readers (id, full_name, email, phone, address, id_card, birth_date, join_date, card_issue_date, card_expiry_date, status, is_deleted)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    """, READERS)
    
    # Insert or replace transactions
    c.executemany("""
        INSERT OR REPLACE INTO borrow_transactions (id, reader_id, reader_name, book_id, book_title, borrow_date, due_date, return_date, borrow_type, status, fine_amount, notes)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    """, TRANSACTIONS)
    
    conn.commit()
    
    c.execute("SELECT COUNT(*) FROM readers")
    total_readers = c.fetchone()[0]
    c.execute("SELECT COUNT(*) FROM borrow_transactions")
    total_tx = c.fetchone()[0]
    
    print(f"Database updated: {total_readers} readers, {total_tx} transactions.")
    
    # Now update seed_data.sql
    with open(SEED_SQL_PATH, "r", encoding="utf-8") as f:
        content = f.read()
        
    # Build Readers SQL
    readers_sql_lines = []
    for r in READERS:
        def sql_val(v):
            if v is None: return "NULL"
            if isinstance(v, int): return str(v)
            return f"'{v}'"
        vals = ", ".join(sql_val(x) for x in r)
        readers_sql_lines.append(f"({vals})")
    readers_block = "-- Seed Readers (38 độc giả phong phú đủ các nhóm và trạng thái)\nINSERT OR REPLACE INTO readers (id, full_name, email, phone, address, id_card, birth_date, join_date, card_issue_date, card_expiry_date, status, is_deleted)\nVALUES \n" + ",\n".join(readers_sql_lines) + ";\n"
    
    # Build Transactions SQL
    tx_sql_lines = []
    for t in TRANSACTIONS:
        def sql_val(v):
            if v is None: return "NULL"
            if isinstance(v, (int, float)): return str(v)
            return f"'{v}'"
        vals = ", ".join(sql_val(x) for x in t)
        tx_sql_lines.append(f"({vals})")
    tx_block = "-- Seed Transactions (28 giao dịch mượn trả gần đây phong phú trạng thái)\nINSERT OR REPLACE INTO borrow_transactions (id, reader_id, reader_name, book_id, book_title, borrow_date, due_date, return_date, borrow_type, status, fine_amount, notes)\nVALUES\n" + ",\n".join(tx_sql_lines) + ";\n"
    
    # Replace in seed_data.sql using regex
    content = re.sub(
        r'-- Seed Readers.*?(?=-- Seed Books)',
        readers_block + "\n",
        content,
        flags=re.DOTALL
    )
    
    content = re.sub(
        r'-- Seed Transactions.*?(?=-- Default Library Operational Settings)',
        tx_block + "\n",
        content,
        flags=re.DOTALL
    )
    
    with open(SEED_SQL_PATH, "w", encoding="utf-8") as f:
        f.write(content)
        
    print("seed_data.sql updated successfully.")

if __name__ == "__main__":
    main()
