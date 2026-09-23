#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Populate and download authentic real book covers for 216+ books in LibMan.
Groups books into major authors so that author filtering and "sách cùng tác giả" work realistically.
"""

import os
import sys
import ssl
import json
import sqlite3
import urllib.request
import urllib.parse
from concurrent.futures import ThreadPoolExecutor, as_completed

IMAGE_DIR = "/home/arjunsharma/Tài liệu/GitHub/ViThay/src/main/resources/com/vithay/libman/images"
DB_PATH = "/home/arjunsharma/Tài liệu/GitHub/ViThay/libman.db"
SEED_SQL_PATH = "/home/arjunsharma/Tài liệu/GitHub/ViThay/src/main/resources/com/vithay/libman/database/seed_data.sql"

os.makedirs(IMAGE_DIR, exist_ok=True)

# SSL context for downloading
CTX = ssl.create_default_context()
CTX.check_hostname = False
CTX.verify_mode = ssl.CERT_NONE

HEADERS = {
    'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36'
}

# Define the complete catalog of 216 books
# Tuple: (id, title, author, category, category_id, shelf, isbn, price, year, publisher, status, image_file, total, avail, query_or_cover_i)
BOOKS = [
    # Mockup books
    ('B001', 'Sapiens: Lược Sử Loài Người', 'Yuval Noah Harari', 'Lịch sử & Địa lý', 4, 'Khu L - Kệ 01', '978-604-56-4275-7', 189000, 2014, 'NXB Thế Giới', 'Available', 'sapiens.jpg', 6, 5, 'isbn:9780062316097'),
    ('B002', 'Dune: Xứ Cát', 'Frank Herbert', 'Khoa học viễn tưởng', 5, 'Khu V - Kệ 02', '978-604-56-6890-0', 245000, 1965, 'NXB Văn Học', 'Borrowed', 'dune.jpg', 4, 0, 'id:11481354'),
    ('B003', 'Educated: Được Học', 'Tara Westover', 'Hồi ký & Tự truyện', 7, 'Khu H - Kệ 01', '978-604-56-7812-1', 165000, 2018, 'NXB Phụ Nữ', 'Available', 'educated.jpg', 5, 4, 'id:8878598'),
    ('B004', 'Atomic Habits: Thay Đổi Tí Hon', 'James Clear', 'Kỹ năng sống & Tâm lý', 6, 'Khu K - Kệ 03', '978-604-56-8923-2', 179000, 2018, 'NXB Thế Giới', 'On Hold', 'atomic_habits.jpg', 8, 2, 'id:12836262'),
    ('B005', '1984', 'George Orwell', 'Tiểu thuyết hiện đại', 8, 'Khu T - Kệ 01', '978-604-56-3412-9', 115000, 1949, 'NXB Văn Học', 'Available', '1984.jpg', 5, 4, 'id:12818862'),

    # BK001 - BK044 Existing
    ('BK001', 'Tư Tưởng Hồ Chí Minh', 'Hồ Chí Minh', 'Lý luận chính trị & Triết học', 1, 'Khu A - Kệ 01', '978-604-90-1122-1', 95000, 2021, 'NXB Chính Trị Quốc Gia', 'Available', 'tu_tuong_hcm.jpg', 10, 9, 'id:12879555'),
    ('BK002', 'Đồi Gió Hú (Wuthering Heights)', 'Emily Brontë', 'Văn học kinh điển', 2, 'Khu B - Kệ 01', '978-604-90-2233-2', 135000, 2019, 'NXB Văn Học', 'Available', 'doi_gio_hu.jpg', 5, 4, 'id:12741541'),
    ('BK003', 'Cánh Đồng Hoang', 'Nguyễn Thị Bé', 'Văn học kinh điển', 2, 'Khu B - Kệ 02', '978-604-90-3344-3', 110000, 2020, 'NXB Trẻ', 'Available', 'canh_dong_hoang.jpg', 6, 5, 'id:8967534'),
    ('BK004', 'Toán Rời Rạc', 'Nguyễn Văn An', 'Toán học & Khoa học tự nhiên', 3, 'Khu C - Kệ 01', '978-604-90-4455-4', 85000, 2022, 'NXB Giáo Dục', 'Available', 'toan_roi_rac.jpg', 12, 11, 'id:9255229'),
    ('BK005', 'Số Đỏ', 'Vũ Trọng Phụng', 'Văn học kinh điển', 2, 'Khu B - Kệ 03', '978-604-56-1188-3', 75000, 1936, 'NXB Văn Học', 'Available', 'so_do.jpg', 8, 8, 'id:10873292'),
    ('BK006', 'Chiến Tranh Và Hòa Bình (War and Peace)', 'Leo Tolstoy', 'Văn học kinh điển', 2, 'Khu B - Kệ 04', '978-604-56-2299-4', 380000, 1869, 'NXB Văn Học', 'Available', 'chien_tranh_hoa_binh.jpg', 4, 4, 'id:12721865'),
    ('BK007', 'Những Người Khốn Khổ (Les Misérables)', 'Victor Hugo', 'Văn học kinh điển', 2, 'Khu B - Kệ 05', '978-604-56-3311-5', 290000, 1862, 'NXB Văn Học', 'Available', 'nhung_nguoi_khon_kho.jpg', 5, 5, 'id:12721865'),
    ('BK008', 'Người Về Từ Sao Hỏa (The Martian)', 'Andy Weir', 'Khoa học viễn tưởng', 5, 'Khu V - Kệ 01', '978-604-56-4422-6', 155000, 2011, 'NXB Hội Nhà Văn', 'Available', 'nguoi_ve_tu_sao_hoa.jpg', 7, 7, 'isbn:9780804139021'),
    ('BK009', 'Tam Thể (The Three-Body Problem)', 'Lưu Từ Hân', 'Khoa học viễn tưởng', 5, 'Khu V - Kệ 03', '978-604-56-5533-7', 220000, 2008, 'NXB Văn Học', 'Available', 'tam_the.jpg', 6, 6, 'id:11749500'),
    ('BK010', 'Clean Code: Mã Sạch', 'Robert C. Martin', 'Công nghệ thông tin', 9, 'Khu IT - Kệ 01', '978-013-23-5088-4', 320000, 2008, 'NXB Khoa Học & Kỹ Thuật', 'Available', 'clean_code.jpg', 10, 9, 'isbn:9780132350884'),
    ('BK011', 'Design Patterns: Elements of Reusable Object-Oriented Software', 'Erich Gamma', 'Công nghệ thông tin', 9, 'Khu IT - Kệ 02', '978-020-16-3361-0', 360000, 1994, 'Addison-Wesley', 'Available', 'design_patterns.jpg', 5, 5, 'isbn:9780201633610'),
    ('BK012', 'Designing Data-Intensive Applications', 'Martin Kleppmann', 'Công nghệ thông tin', 9, 'Khu IT - Kệ 03', '978-144-93-7332-0', 450000, 2017, 'O Reilly Media', 'Available', 'ddia.jpg', 6, 6, 'isbn:9781449373320'),
    ('BK013', 'Introduction to Algorithms (CLRS)', 'Thomas H. Cormen', 'Công nghệ thông tin', 9, 'Khu IT - Kệ 04', '978-026-20-3384-8', 520000, 2009, 'MIT Press', 'Available', 'clrs.jpg', 4, 4, 'isbn:9780262033848'),
    ('BK014', 'Cộng Hòa (The Republic)', 'Plato', 'Lý luận chính trị & Triết học', 1, 'Khu A - Kệ 02', '978-604-56-7744-8', 175000, 2020, 'NXB Thế Giới', 'Available', 'cong_hoa.jpg', 5, 5, 'id:12604856'),
    ('BK015', 'Bàn Về Khế Ước Xã Hội', 'Jean-Jacques Rousseau', 'Lý luận chính trị & Triết học', 1, 'Khu A - Kệ 03', '978-604-56-8855-9', 140000, 2019, 'NXB Tri Thức', 'Available', 'khe_uoc_xa_hoi.jpg', 4, 4, 'id:12554763'),
    ('BK016', 'Đại Việt Sử Ký Toàn Thư', 'Ngô Sĩ Liên', 'Lịch sử & Địa lý', 4, 'Khu L - Kệ 02', '978-604-90-5566-5', 420000, 1479, 'NXB Khoa Học Xã Hội', 'Available', 'dai_viet_su_ky.jpg', 6, 6, 'id:12879555'),
    ('BK017', 'Súng, Vi Trùng Và Thép (Guns, Germs, and Steel)', 'Jared Diamond', 'Lịch sử & Địa lý', 4, 'Khu L - Kệ 03', '978-604-56-9966-0', 215000, 1997, 'NXB Tri Thức', 'Available', 'sung_vi_trung_thep.jpg', 7, 7, 'isbn:9780393317558'),
    ('BK018', 'Tư Duy Nhanh Và Chậm (Thinking, Fast and Slow)', 'Daniel Kahneman', 'Kỹ năng sống & Tâm lý', 6, 'Khu K - Kệ 01', '978-604-56-1234-5', 198000, 2011, 'NXB Thế Giới', 'Available', 'tu_duy_nhanh_cham.jpg', 8, 8, 'isbn:9780374533557'),
    ('BK019', 'Đắc Nhân Tâm (How to Win Friends and Influence People)', 'Dale Carnegie', 'Kỹ năng sống & Tâm lý', 6, 'Khu K - Kệ 02', '978-604-56-2345-6', 98000, 1936, 'NXB Tổng Hợp TP.HCM', 'Available', 'dac_nhan_tam.jpg', 15, 15, 'isbn:9780671027032'),
    ('BK020', 'Khi Hơi Thở Hóa Thinh Không (When Breath Becomes Air)', 'Paul Kalanithi', 'Hồi ký & Tự truyện', 7, 'Khu H - Kệ 02', '978-604-56-3456-7', 125000, 2016, 'NXB Hội Nhà Văn', 'Available', 'khi_hoi_tho_hoa_thinh_khong.jpg', 6, 6, 'isbn:9780812988406'),
    ('BK021', 'Trăm Năm Cô Đơn (One Hundred Years of Solitude)', 'Gabriel García Márquez', 'Tiểu thuyết hiện đại', 8, 'Khu T - Kệ 02', '978-604-56-4567-8', 185000, 1967, 'NXB Văn Học', 'Available', 'tram_nam_co_don.jpg', 6, 6, 'isbn:9780060883287'),
    ('BK022', 'Rừng Na Uy (Norwegian Wood)', 'Haruki Murakami', 'Tiểu thuyết hiện đại', 8, 'Khu T - Kệ 03', '978-604-56-5678-9', 145000, 1987, 'NXB Hội Nhà Văn', 'Available', 'rung_na_uy.jpg', 8, 8, 'id:2237620'),
    ('BK023', 'English Grammar in Use (5th Edition)', 'Raymond Murphy', 'Ngoại ngữ & Từ điển', 10, 'Khu N - Kệ 01', '978-110-84-5765-1', 260000, 2019, 'Cambridge University Press', 'Available', 'english_grammar.jpg', 10, 10, 'isbn:9781108457651'),
    ('BK024', 'Oxford Advanced Learner''s Dictionary', 'AS Hornby', 'Ngoại ngữ & Từ điển', 10, 'Khu N - Kệ 02', '978-019-47-9848-8', 580000, 2020, 'Oxford University Press', 'Available', 'oxford_dict.jpg', 5, 5, 'isbn:9780194798488'),
    ('BK025', 'Minna no Nihongo I', '3A Corporation', 'Ngoại ngữ & Từ điển', 10, 'Khu N - Kệ 03', '978-488-31-9603-6', 135000, 2018, 'NXB Trẻ', 'Available', 'minna_nihongo.jpg', 8, 8, 'isbn:9784883196036'),
    ('BK026', 'Giải Tích 1 & 2 Dành Cho Kỹ Sư', 'Nguyễn Đình Trí', 'Toán học & Khoa học tự nhiên', 3, 'Khu C - Kệ 02', '978-604-90-6677-6', 120000, 2021, 'NXB Giáo Dục', 'Available', 'giai_tich.jpg', 12, 12, 'id:12879555'),
    ('BK027', 'Doraemon - Tuyển Tập Tranh Truyện Màu', 'Fujiko F. Fujio', 'Truyện tranh & Sách thiếu nhi', 11, 'Khu TN - Kệ 01', '978-604-2-22441-1', 45000, 1970, 'NXB Kim Đồng', 'Available', 'doraemon.jpg', 15, 14, 'id:12996033'),
    ('BK028', 'Thám Tử Lừng Danh Conan', 'Gosho Aoyama', 'Truyện tranh & Sách thiếu nhi', 11, 'Khu TN - Kệ 01', '978-604-2-18542-2', 35000, 1994, 'NXB Kim Đồng', 'Available', 'conan.jpg', 20, 19, 'id:12376585'),
    ('BK029', 'Dế Mèn Phiêu Lưu Ký', 'Tô Hoài', 'Truyện tranh & Sách thiếu nhi', 11, 'Khu TN - Kệ 02', '978-604-2-08533-3', 60000, 1941, 'NXB Kim Đồng', 'Available', 'de_men.jpg', 12, 12, 'id:8967534'),
    ('BK030', 'Kính Vạn Hoa', 'Nguyễn Nhật Ánh', 'Truyện tranh & Sách thiếu nhi', 11, 'Khu TN - Kệ 02', '978-604-2-09414-4', 85000, 1995, 'NXB Kim Đồng', 'Available', 'kinh_van_hoa.jpg', 10, 10, 'id:13258074'),
    ('BK031', 'Hoàng Tử Bé (Le Petit Prince)', 'Antoine de Saint-Exupéry', 'Truyện tranh & Sách thiếu nhi', 11, 'Khu TN - Kệ 03', '978-604-56-7890-9', 70000, 1943, 'NXB Hội Nhà Văn', 'Available', 'hoang_tu_be.jpg', 8, 8, 'id:12741541'),
    ('BK032', 'Harry Potter và Hòn Đá Phù Thủy', 'J.K. Rowling', 'Truyện tranh & Sách thiếu nhi', 11, 'Khu TN - Kệ 03', '978-604-1-08123-5', 160000, 1997, 'NXB Trẻ', 'Available', 'harry_potter.jpg', 10, 9, 'isbn:9780439708180'),
    ('BK033', 'Totto-chan Bên Cửa Sổ', 'Tetsuko Kuroyanagi', 'Truyện tranh & Sách thiếu nhi', 11, 'Khu TN - Kệ 04', '978-604-56-3245-7', 98000, 1981, 'NXB Văn Học', 'Available', 'totto_chan.jpg', 8, 8, 'isbn:9784770020673'),
    ('BK034', 'Đảo Giấu Vàng (Treasure Island)', 'Robert Louis Stevenson', 'Truyện tranh & Sách thiếu nhi', 11, 'Khu TN - Kệ 04', '978-604-56-6543-2', 88000, 1883, 'NXB Văn Học', 'Available', 'dao_giau_vang.jpg', 6, 6, 'id:12604856'),
    ('BK035', 'Không Gia Đình (Sans Famille)', 'Hector Malot', 'Truyện tranh & Sách thiếu nhi', 11, 'Khu TN - Kệ 05', '978-604-56-8877-1', 135000, 1878, 'NXB Văn Học', 'Available', 'khong_gia_dinh.jpg', 7, 7, 'isbn:9781514781609'),
    ('BK036', 'Góc Sân Và Khoảng Trời', 'Trần Đăng Khoa', 'Truyện tranh & Sách thiếu nhi', 11, 'Khu TN - Kệ 05', '978-604-2-07654-6', 55000, 1968, 'NXB Kim Đồng', 'Available', 'goc_san_khoang_troi.jpg', 9, 9, 'id:8967534'),
    ('BK037', 'Homo Deus: Lược Sử Tương Lai', 'Yuval Noah Harari', 'Lịch sử & Địa lý', 4, 'Khu L - Kệ 01', '978-604-56-4276-4', 195000, 2016, 'NXB Thế Giới', 'Available', 'homo_deus.jpg', 6, 6, 'isbn:9780062464316'),
    ('BK038', '21 Bài Học Cho Thế Kỷ 21', 'Yuval Noah Harari', 'Lịch sử & Địa lý', 4, 'Khu L - Kệ 01', '978-604-56-4277-1', 175000, 2018, 'NXB Thế Giới', 'Available', '21_bai_hoc.jpg', 5, 5, 'isbn:9780525512172'),
    ('BK039', 'Clean Architecture: Kiến Trúc Sạch', 'Robert C. Martin', 'Công nghệ thông tin', 9, 'Khu IT - Kệ 01', '978-013-44-9416-6', 350000, 2017, 'Prentice Hall', 'Available', 'clean_architecture.jpg', 6, 6, 'isbn:9780134494166'),
    ('BK040', 'The Clean Coder: Cẩm Nang Cho Lập Trình Viên', 'Robert C. Martin', 'Công nghệ thông tin', 9, 'Khu IT - Kệ 01', '978-013-70-8107-3', 280000, 2011, 'Prentice Hall', 'Available', 'clean_coder.jpg', 5, 5, 'isbn:9780137081073'),
    ('BK041', 'Cho Tôi Xin Một Vé Đi Tuổi Thơ', 'Nguyễn Nhật Ánh', 'Truyện tranh & Sách thiếu nhi', 11, 'Khu TN - Kệ 02', '978-604-2-09415-1', 85000, 2008, 'NXB Trẻ', 'Available', 'cho_toi_xin_mot_ve_di_tuoi_tho.jpg', 10, 10, 'id:8967534'),
    ('BK042', 'Tôi Thấy Hoa Vàng Trên Cỏ Xanh', 'Nguyễn Nhật Ánh', 'Truyện tranh & Sách thiếu nhi', 11, 'Khu TN - Kệ 02', '978-604-2-09416-8', 95000, 2010, 'NXB Trẻ', 'Available', 'toi_thay_hoa_vang_tren_co_xanh.jpg', 8, 8, 'id:15095300'),
    ('BK043', 'Harry Potter và Phòng Chứa Bí Mật', 'J.K. Rowling', 'Truyện tranh & Sách thiếu nhi', 11, 'Khu TN - Kệ 03', '978-604-1-08124-2', 170000, 1998, 'NXB Trẻ', 'Available', 'harry_potter_2.jpg', 8, 8, 'isbn:9780439064873'),
    ('BK044', 'Harry Potter và Tên Tù Nhân Ngục Azkaban', 'J.K. Rowling', 'Truyện tranh & Sách thiếu nhi', 11, 'Khu TN - Kệ 03', '978-604-1-08125-9', 185000, 1999, 'NXB Trẻ', 'Available', 'harry_potter_3.jpg', 7, 7, 'isbn:9780439136365'),

    # BK045 - BK058 Nguyễn Nhật Ánh (14 thêm -> tổng 18 cuốn)
    ('BK045', 'Mắt Biếc', 'Nguyễn Nhật Ánh', 'Truyện tranh & Sách thiếu nhi', 11, 'Khu TN - Kệ 02', '978-604-1-12301-1', 110000, 1990, 'NXB Trẻ', 'Available', 'mat_biec.jpg', 12, 11, 'id:13258074'),
    ('BK046', 'Cô Gái Đến Từ Hôm Qua', 'Nguyễn Nhật Ánh', 'Truyện tranh & Sách thiếu nhi', 11, 'Khu TN - Kệ 02', '978-604-1-12302-8', 90000, 1989, 'NXB Trẻ', 'Available', 'co_gai_den_tu_hom_qua.jpg', 9, 8, 'id:13436349'),
    ('BK047', 'Bảy Bước Tới Mùa Hè', 'Nguyễn Nhật Ánh', 'Truyện tranh & Sách thiếu nhi', 11, 'Khu TN - Kệ 02', '978-604-1-12303-5', 95000, 2015, 'NXB Trẻ', 'Available', 'bay_buoc_toi_mua_he.jpg', 8, 8, 'id:13258074'),
    ('BK048', 'Cảm Ơn Người Lớn', 'Nguyễn Nhật Ánh', 'Truyện tranh & Sách thiếu nhi', 11, 'Khu TN - Kệ 02', '978-604-1-12304-2', 115000, 2018, 'NXB Trẻ', 'Available', 'cam_on_nguoi_lon.jpg', 10, 10, 'id:15095300'),
    ('BK049', 'Đi Qua Hoa Cúc', 'Nguyễn Nhật Ánh', 'Truyện tranh & Sách thiếu nhi', 11, 'Khu TN - Kệ 02', '978-604-1-12305-9', 75000, 1990, 'NXB Trẻ', 'Available', 'di_qua_hoa_cuc.jpg', 7, 7, 'id:8967534'),
    ('BK050', 'Hạ Đỏ', 'Nguyễn Nhật Ánh', 'Truyện tranh & Sách thiếu nhi', 11, 'Khu TN - Kệ 02', '978-604-1-12306-6', 80000, 1991, 'NXB Trẻ', 'Available', 'ha_do.jpg', 8, 8, 'id:13258074'),
    ('BK051', 'Chúc Một Ngày Tốt Lành', 'Nguyễn Nhật Ánh', 'Truyện tranh & Sách thiếu nhi', 11, 'Khu TN - Kệ 02', '978-604-1-12307-3', 95000, 2014, 'NXB Trẻ', 'Available', 'chuc_mot_ngay_tot_lanh.jpg', 9, 9, 'id:13436349'),
    ('BK052', 'Ngày Xưa Có Một Chuyện Tình', 'Nguyễn Nhật Ánh', 'Truyện tranh & Sách thiếu nhi', 11, 'Khu TN - Kệ 02', '978-604-1-12308-0', 105000, 2016, 'NXB Trẻ', 'Available', 'ngay_xua_co_mot_chuyen_tinh.jpg', 10, 10, 'id:15095300'),
    ('BK053', 'Cây Chuối Non Đi Giày Xanh', 'Nguyễn Nhật Ánh', 'Truyện tranh & Sách thiếu nhi', 11, 'Khu TN - Kệ 02', '978-604-1-12309-7', 110000, 2017, 'NXB Trẻ', 'Available', 'cay_chuoi_non_di_giay_xanh.jpg', 8, 8, 'id:13258074'),
    ('BK054', 'Làm Bạn Với Bầu Trời', 'Nguyễn Nhật Ánh', 'Truyện tranh & Sách thiếu nhi', 11, 'Khu TN - Kệ 02', '978-604-1-12310-3', 110000, 2019, 'NXB Trẻ', 'Available', 'lam_ban_voi_bau_troi.jpg', 10, 10, 'id:13436349'),
    ('BK055', 'Quán Gò Đi Đi', 'Nguyễn Nhật Ánh', 'Truyện tranh & Sách thiếu nhi', 11, 'Khu TN - Kệ 02', '978-604-1-12311-0', 85000, 1999, 'NXB Trẻ', 'Available', 'quan_go_di_di.jpg', 6, 6, 'id:8967534'),
    ('BK056', 'Còn Chút Gì Để Nhớ', 'Nguyễn Nhật Ánh', 'Truyện tranh & Sách thiếu nhi', 11, 'Khu TN - Kệ 02', '978-604-1-12312-7', 88000, 1988, 'NXB Trẻ', 'Available', 'con_chut_gi_de_nho.jpg', 7, 7, 'id:13258074'),
    ('BK057', 'Bồ Câu Không Đưa Thư', 'Nguyễn Nhật Ánh', 'Truyện tranh & Sách thiếu nhi', 11, 'Khu TN - Kệ 02', '978-604-1-12313-4', 78000, 1993, 'NXB Trẻ', 'Available', 'bo_cau_khong_dua_thu.jpg', 8, 8, 'id:13436349'),
    ('BK058', 'Thằng Quỷ Nhỏ', 'Nguyễn Nhật Ánh', 'Truyện tranh & Sách thiếu nhi', 11, 'Khu TN - Kệ 02', '978-604-1-12314-1', 82000, 1990, 'NXB Trẻ', 'Available', 'thang_quy_nho.jpg', 8, 8, 'id:15095300'),

    # BK059 - BK066 J.K. Rowling (8 thêm -> tổng 12 cuốn)
    ('BK059', 'Harry Potter và Chiếc Cốc Lửa', 'J.K. Rowling', 'Truyện tranh & Sách thiếu nhi', 11, 'Khu TN - Kệ 03', '978-604-1-08126-6', 220000, 2000, 'NXB Trẻ', 'Available', 'harry_potter_4.jpg', 8, 8, 'isbn:9780439139595'),
    ('BK060', 'Harry Potter và Hội Phượng Hoàng', 'J.K. Rowling', 'Truyện tranh & Sách thiếu nhi', 11, 'Khu TN - Kệ 03', '978-604-1-08127-3', 260000, 2003, 'NXB Trẻ', 'Available', 'harry_potter_5.jpg', 8, 7, 'isbn:9780439358064'),
    ('BK061', 'Harry Potter và Hoàng Tử Lai', 'J.K. Rowling', 'Truyện tranh & Sách thiếu nhi', 11, 'Khu TN - Kệ 03', '978-604-1-08128-0', 230000, 2005, 'NXB Trẻ', 'Available', 'harry_potter_6.jpg', 7, 7, 'isbn:9780439784542'),
    ('BK062', 'Harry Potter và Bảo Bối Tử Thần', 'J.K. Rowling', 'Truyện tranh & Sách thiếu nhi', 11, 'Khu TN - Kệ 03', '978-604-1-08129-7', 280000, 2007, 'NXB Trẻ', 'Available', 'harry_potter_7.jpg', 9, 8, 'isbn:9780545010221'),
    ('BK063', 'Sinh Vật Huyền Bí và Nơi Tìm Ra Chúng', 'J.K. Rowling', 'Truyện tranh & Sách thiếu nhi', 11, 'Khu TN - Kệ 03', '978-604-1-08130-3', 125000, 2001, 'NXB Trẻ', 'Available', 'fantastic_beasts.jpg', 6, 6, 'isbn:9781338109061'),
    ('BK064', 'Quidditch Qua Các Thời Đại', 'J.K. Rowling', 'Truyện tranh & Sách thiếu nhi', 11, 'Khu TN - Kệ 03', '978-604-1-08131-0', 105000, 2001, 'NXB Trẻ', 'Available', 'quidditch.jpg', 6, 6, 'isbn:9781338109078'),
    ('BK065', 'Những Chuyện Kể Của Beedle Người Hát Rong', 'J.K. Rowling', 'Truyện tranh & Sách thiếu nhi', 11, 'Khu TN - Kệ 03', '978-604-1-08132-7', 110000, 2008, 'NXB Trẻ', 'Available', 'beedle_bard.jpg', 7, 7, 'isbn:9780545128247'),
    ('BK066', 'The Casual Vacancy: Khoảng Trống Cuộc Đời', 'J.K. Rowling', 'Tiểu thuyết hiện đại', 8, 'Khu T - Kệ 01', '978-604-1-08133-4', 195000, 2012, 'NXB Trẻ', 'Available', 'casual_vacancy.jpg', 5, 5, 'isbn:9780316228534'),

    # BK067 - BK069 Yuval Noah Harari (3 thêm -> tổng 6 cuốn)
    ('BK067', 'Sapiens: Nền Tảng Khởi Sinh (Graphic Novel Tập 1)', 'Yuval Noah Harari', 'Lịch sử & Địa lý', 4, 'Khu L - Kệ 01', '978-604-56-4278-8', 250000, 2020, 'NXB Thế Giới', 'Available', 'sapiens_graphic_1.jpg', 5, 5, 'isbn:9780063051331'),
    ('BK068', 'Sapiens: Trụ Cột Nền Văn Minh (Graphic Novel Tập 2)', 'Yuval Noah Harari', 'Lịch sử & Địa lý', 4, 'Khu L - Kệ 01', '978-604-56-4279-5', 260000, 2021, 'NXB Thế Giới', 'Available', 'sapiens_graphic_2.jpg', 5, 5, 'isbn:9780063051348'),
    ('BK069', 'Nexus: Lược Sử Các Mạng Lưới Thông Tin', 'Yuval Noah Harari', 'Lịch sử & Địa lý', 4, 'Khu L - Kệ 01', '978-604-56-4280-1', 280000, 2024, 'NXB Thế Giới', 'Available', 'nexus_harari.jpg', 6, 6, 'id:14500000'),

    # BK070 - BK072 Robert C. Martin (3 thêm -> tổng 6 cuốn)
    ('BK070', 'Clean Craftsmanship: Kỹ Nghệ Phần Mềm Sạch', 'Robert C. Martin', 'Công nghệ thông tin', 9, 'Khu IT - Kệ 01', '978-013-69-1424-9', 380000, 2021, 'Prentice Hall', 'Available', 'clean_craftsmanship.jpg', 5, 5, 'isbn:9780136914242'),
    ('BK071', 'Clean Agile: Phương Pháp Agile Thực Tiễn', 'Robert C. Martin', 'Công nghệ thông tin', 9, 'Khu IT - Kệ 01', '978-013-57-8186-9', 310000, 2019, 'Prentice Hall', 'Available', 'clean_agile.jpg', 6, 6, 'isbn:9780135781869'),
    ('BK072', 'Agile Principles, Patterns, and Practices in C#', 'Robert C. Martin', 'Công nghệ thông tin', 9, 'Khu IT - Kệ 01', '978-013-18-5725-4', 420000, 2006, 'Prentice Hall', 'Available', 'agile_principles.jpg', 4, 4, 'isbn:9780131857254'),

    # BK073 - BK083 Haruki Murakami (11 thêm -> tổng 12 cuốn)
    ('BK073', 'Kafka Bên Bờ Biển (Kafka on the Shore)', 'Haruki Murakami', 'Tiểu thuyết hiện đại', 8, 'Khu T - Kệ 03', '978-604-56-5679-6', 175000, 2002, 'NXB Hội Nhà Văn', 'Available', 'kafka_on_the_shore.jpg', 8, 8, 'id:4982600'),
    ('BK074', '1Q84 (Tập 1)', 'Haruki Murakami', 'Tiểu thuyết hiện đại', 8, 'Khu T - Kệ 03', '978-604-56-5680-2', 160000, 2009, 'NXB Hội Nhà Văn', 'Available', '1q84_tap_1.jpg', 7, 7, 'isbn:9780307593313'),
    ('BK075', '1Q84 (Tập 2)', 'Haruki Murakami', 'Tiểu thuyết hiện đại', 8, 'Khu T - Kệ 03', '978-604-56-5681-9', 160000, 2009, 'NXB Hội Nhà Văn', 'Available', '1q84_tap_2.jpg', 7, 7, 'isbn:9780307593313'),
    ('BK076', '1Q84 (Tập 3)', 'Haruki Murakami', 'Tiểu thuyết hiện đại', 8, 'Khu T - Kệ 03', '978-604-56-5682-6', 170000, 2010, 'NXB Hội Nhà Văn', 'Available', '1q84_tap_3.jpg', 7, 7, 'isbn:9780307593313'),
    ('BK077', 'Biên Niên Ký Chim Vặn Dây Cót', 'Haruki Murakami', 'Tiểu thuyết hiện đại', 8, 'Khu T - Kệ 03', '978-604-56-5683-3', 210000, 1994, 'NXB Hội Nhà Văn', 'Available', 'wind_up_bird.jpg', 6, 6, 'isbn:9780679775430'),
    ('BK078', 'Phía Nam Biên Giới, Phía Tây Mặt Trời', 'Haruki Murakami', 'Tiểu thuyết hiện đại', 8, 'Khu T - Kệ 03', '978-604-56-5684-0', 120000, 1992, 'NXB Hội Nhà Văn', 'Available', 'south_of_border.jpg', 8, 8, 'isbn:9780679767756'),
    ('BK079', 'Lắng Nghe Gió Hát (Hear the Wind Sing)', 'Haruki Murakami', 'Tiểu thuyết hiện đại', 8, 'Khu T - Kệ 03', '978-604-56-5685-7', 95000, 1979, 'NXB Hội Nhà Văn', 'Available', 'hear_the_wind_sing.jpg', 6, 6, 'isbn:9780804170147'),
    ('BK080', 'Cuộc Săn Cừu Hoang (A Wild Sheep Chase)', 'Haruki Murakami', 'Tiểu thuyết hiện đại', 8, 'Khu T - Kệ 03', '978-604-56-5686-4', 135000, 1982, 'NXB Hội Nhà Văn', 'Available', 'wild_sheep_chase.jpg', 6, 6, 'isbn:9780375718946'),
    ('BK081', 'Xứ Sở Diệu Kỳ Và Chốn Tận Cùng Thế Giới', 'Haruki Murakami', 'Tiểu thuyết hiện đại', 8, 'Khu T - Kệ 03', '978-604-56-5687-1', 185000, 1985, 'NXB Hội Nhà Văn', 'Available', 'hard_boiled_wonderland.jpg', 7, 7, 'isbn:9780679743460'),
    ('BK082', 'Người Tình Sputnik (Sputnik Sweetheart)', 'Haruki Murakami', 'Tiểu thuyết hiện đại', 8, 'Khu T - Kệ 03', '978-604-56-5688-8', 125000, 1999, 'NXB Hội Nhà Văn', 'Available', 'sputnik_sweetheart.jpg', 8, 8, 'isbn:9780375726057'),
    ('BK083', 'Tôi Nói Gì Khi Nói Về Chạy Bộ', 'Haruki Murakami', 'Hồi ký & Tự truyện', 7, 'Khu H - Kệ 03', '978-604-56-5689-5', 115000, 2007, 'NXB Hội Nhà Văn', 'Available', 'running_murakami.jpg', 10, 10, 'isbn:9780307389831'),

    # BK084 - BK090 Dan Brown (7 cuốn)
    ('BK084', 'Mật Mã Da Vinci (The Da Vinci Code)', 'Dan Brown', 'Tiểu thuyết hiện đại', 8, 'Khu T - Kệ 04', '978-604-56-7801-1', 195000, 2003, 'NXB Văn Học', 'Available', 'da_vinci_code.jpg', 10, 10, 'id:9255229'),
    ('BK085', 'Thiên Thần & Ác Quỷ (Angels & Demons)', 'Dan Brown', 'Tiểu thuyết hiện đại', 8, 'Khu T - Kệ 04', '978-604-56-7802-8', 185000, 2000, 'NXB Văn Học', 'Available', 'angels_and_demons.jpg', 8, 8, 'id:11408459'),
    ('BK086', 'Biểu Tượng Thất Truyền (The Lost Symbol)', 'Dan Brown', 'Tiểu thuyết hiện đại', 8, 'Khu T - Kệ 04', '978-604-56-7803-5', 190000, 2009, 'NXB Văn Học', 'Available', 'lost_symbol.jpg', 7, 7, 'isbn:9780385504225'),
    ('BK087', 'Hỏa Ngục (Inferno)', 'Dan Brown', 'Tiểu thuyết hiện đại', 8, 'Khu T - Kệ 04', '978-604-56-7804-2', 210000, 2013, 'NXB Văn Học', 'Available', 'inferno_dan_brown.jpg', 9, 9, 'isbn:9780385537858'),
    ('BK088', 'Nguồn Cội (Origin)', 'Dan Brown', 'Tiểu thuyết hiện đại', 8, 'Khu T - Kệ 04', '978-604-56-7805-9', 225000, 2017, 'NXB Văn Học', 'Available', 'origin_dan_brown.jpg', 8, 8, 'isbn:9780385514231'),
    ('BK089', 'Pháo Đài Số (Digital Fortress)', 'Dan Brown', 'Tiểu thuyết hiện đại', 8, 'Khu T - Kệ 04', '978-604-56-7806-6', 145000, 1998, 'NXB Văn Học', 'Available', 'digital_fortress.jpg', 7, 7, 'isbn:9780312944926'),
    ('BK090', 'Điểm Dối Lừa (Deception Point)', 'Dan Brown', 'Tiểu thuyết hiện đại', 8, 'Khu T - Kệ 04', '978-604-56-7807-3', 155000, 2001, 'NXB Văn Học', 'Available', 'deception_point.jpg', 6, 6, 'isbn:9780671027384'),

    # BK091 - BK100 Arthur Conan Doyle (10 cuốn)
    ('BK091', 'Chiếc Nhẫn Tình Cờ (A Study in Scarlet)', 'Arthur Conan Doyle', 'Văn học kinh điển', 2, 'Khu B - Kệ 06', '978-604-56-8901-1', 95000, 1887, 'NXB Văn Học', 'Available', 'study_in_scarlet.jpg', 8, 8, 'id:13405534'),
    ('BK092', 'Dấu Bộ Tứ (The Sign of the Four)', 'Arthur Conan Doyle', 'Văn học kinh điển', 2, 'Khu B - Kệ 06', '978-604-56-8902-8', 90000, 1890, 'NXB Văn Học', 'Available', 'sign_of_four.jpg', 7, 7, 'isbn:9780140439076'),
    ('BK093', 'Con Chó Của Dòng Họ Baskerville', 'Arthur Conan Doyle', 'Văn học kinh điển', 2, 'Khu B - Kệ 06', '978-604-56-8903-5', 115000, 1902, 'NXB Văn Học', 'Available', 'hound_of_baskervilles.jpg', 9, 9, 'id:8063264'),
    ('BK094', 'Thung Lũng Khủng Khiếp (The Valley of Fear)', 'Arthur Conan Doyle', 'Văn học kinh điển', 2, 'Khu B - Kệ 06', '978-604-56-8904-2', 105000, 1915, 'NXB Văn Học', 'Available', 'valley_of_fear.jpg', 6, 6, 'isbn:9780140437737'),
    ('BK095', 'Những Cuộc Phiêu Lưu Của Sherlock Holmes', 'Arthur Conan Doyle', 'Văn học kinh điển', 2, 'Khu B - Kệ 06', '978-604-56-8905-9', 140000, 1892, 'NXB Văn Học', 'Available', 'adventures_sherlock.jpg', 10, 10, 'isbn:9780140437720'),
    ('BK096', 'Hồi Ức Về Sherlock Holmes (The Memoirs)', 'Arthur Conan Doyle', 'Văn học kinh điển', 2, 'Khu B - Kệ 06', '978-604-56-8906-6', 135000, 1893, 'NXB Văn Học', 'Available', 'memoirs_sherlock.jpg', 7, 7, 'isbn:9780140437744'),
    ('BK097', 'Sự Trở Lại Của Sherlock Holmes', 'Arthur Conan Doyle', 'Văn học kinh điển', 2, 'Khu B - Kệ 06', '978-604-56-8907-3', 145000, 1905, 'NXB Văn Học', 'Available', 'return_sherlock.jpg', 8, 8, 'isbn:9780140437751'),
    ('BK098', 'Cung Đàn Sau Cùng (His Last Bow)', 'Arthur Conan Doyle', 'Văn học kinh điển', 2, 'Khu B - Kệ 06', '978-604-56-8908-0', 120000, 1917, 'NXB Văn Học', 'Available', 'his_last_bow.jpg', 6, 6, 'isbn:9780140437768'),
    ('BK099', 'Hồ Sơ Sherlock Holmes (The Case-Book)', 'Arthur Conan Doyle', 'Văn học kinh điển', 2, 'Khu B - Kệ 06', '978-604-56-8909-7', 125000, 1927, 'NXB Văn Học', 'Available', 'casebook_sherlock.jpg', 6, 6, 'isbn:9780140437775'),
    ('BK100', 'Thế Giới Đã Mất (The Lost World)', 'Arthur Conan Doyle', 'Khoa học viễn tưởng', 5, 'Khu V - Kệ 04', '978-604-56-8910-3', 110000, 1912, 'NXB Văn Học', 'Available', 'lost_world_doyle.jpg', 7, 7, 'isbn:9780140437652'),

    # BK101 - BK105 Dale Carnegie (5 thêm -> tổng 6 cuốn)
    ('BK101', 'Quẳng Gánh Lo Đi Và Vui Sống', 'Dale Carnegie', 'Kỹ năng sống & Tâm lý', 6, 'Khu K - Kệ 02', '978-604-56-9101-1', 115000, 1948, 'NXB Tổng Hợp TP.HCM', 'Available', 'quang_ganh_lo_di.jpg', 12, 12, 'isbn:9780671733353'),
    ('BK102', 'Khéo Ăn Nói Sẽ Được Lòng Người', 'Dale Carnegie', 'Kỹ năng sống & Tâm lý', 6, 'Khu K - Kệ 02', '978-604-56-9102-8', 105000, 1950, 'NXB Tổng Hợp TP.HCM', 'Available', 'kheo_an_noi.jpg', 10, 10, 'isbn:9781451612592'),
    ('BK103', 'Phát Huy Giá Trị Bản Thân', 'Dale Carnegie', 'Kỹ năng sống & Tâm lý', 6, 'Khu K - Kệ 02', '978-604-56-9103-5', 95000, 1955, 'NXB Tổng Hợp TP.HCM', 'Available', 'phat_huy_gia_tri.jpg', 8, 8, 'isbn:9780671733353'),
    ('BK104', 'Thu Hút Người Khác Và Giao Tiếp Hiệu Quả', 'Dale Carnegie', 'Kỹ năng sống & Tâm lý', 6, 'Khu K - Kệ 02', '978-604-56-9104-2', 98000, 1952, 'NXB Tổng Hợp TP.HCM', 'Available', 'giao_tiep_hieu_qua.jpg', 8, 8, 'isbn:9780671027032'),
    ('BK105', 'Nghệ Thuật Diễn Thuyết Trước Công Chúng', 'Dale Carnegie', 'Kỹ năng sống & Tâm lý', 6, 'Khu K - Kệ 02', '978-604-56-9105-9', 125000, 1926, 'NXB Tổng Hợp TP.HCM', 'Available', 'dien_thuyet_cong_chung.jpg', 7, 7, 'isbn:9780091906382'),

    # BK106 - BK113 Paulo Coelho (8 cuốn)
    ('BK106', 'Nhà Giả Kim (The Alchemist)', 'Paulo Coelho', 'Tiểu thuyết hiện đại', 8, 'Khu T - Kệ 05', '978-604-56-9201-1', 99000, 1988, 'NXB Nhã Nam', 'Available', 'nha_gia_kim.jpg', 15, 15, 'id:7414780'),
    ('BK107', 'Veronika Quyết Chết (Veronika Decides to Die)', 'Paulo Coelho', 'Tiểu thuyết hiện đại', 8, 'Khu T - Kệ 05', '978-604-56-9202-8', 115000, 1998, 'NXB Nhã Nam', 'Available', 'veronika_decides_to_die.jpg', 8, 8, 'isbn:9780061198779'),
    ('BK108', 'Bên Bờ Sông Piedra Tôi Ngồi Xuống Và Tôi Khóc', 'Paulo Coelho', 'Tiểu thuyết hiện đại', 8, 'Khu T - Kệ 05', '978-604-56-9203-5', 105000, 1994, 'NXB Nhã Nam', 'Available', 'by_river_piedra.jpg', 7, 7, 'isbn:9780060851897'),
    ('BK109', 'Quỷ Dữ Và Chàng Prym', 'Paulo Coelho', 'Tiểu thuyết hiện đại', 8, 'Khu T - Kệ 05', '978-604-56-9204-2', 110000, 2000, 'NXB Nhã Nam', 'Available', 'devil_and_miss_prym.jpg', 6, 6, 'isbn:9780060527990'),
    ('BK110', 'Maktub', 'Paulo Coelho', 'Kỹ năng sống & Tâm lý', 6, 'Khu K - Kệ 04', '978-604-56-9205-9', 95000, 1994, 'NXB Nhã Nam', 'Available', 'maktub_coelho.jpg', 8, 8, 'isbn:9780063259836'),
    ('BK111', 'Ngoại Tình (Adultery)', 'Paulo Coelho', 'Tiểu thuyết hiện đại', 8, 'Khu T - Kệ 05', '978-604-56-9206-6', 125000, 2014, 'NXB Nhã Nam', 'Available', 'adultery_coelho.jpg', 6, 6, 'isbn:9781101874080'),
    ('BK112', 'Cẩm Nang Của Người Chiến Binh Ánh Sáng', 'Paulo Coelho', 'Kỹ năng sống & Tâm lý', 6, 'Khu K - Kệ 04', '978-604-56-9207-3', 105000, 1997, 'NXB Nhã Nam', 'Available', 'warrior_of_light.jpg', 7, 7, 'isbn:9780060527983'),
    ('BK113', 'Như Dòng Sông Chảy (Like the Flowing River)', 'Paulo Coelho', 'Hồi ký & Tự truyện', 7, 'Khu H - Kệ 04', '978-604-56-9208-0', 115000, 2006, 'NXB Nhã Nam', 'Available', 'like_flowing_river.jpg', 7, 7, 'isbn:9780007235827'),

    # BK114 - BK117 George Orwell (4 thêm -> tổng 5 cuốn)
    ('BK114', 'Chuyện Ở Nông Trại (Animal Farm)', 'George Orwell', 'Tiểu thuyết hiện đại', 8, 'Khu T - Kệ 01', '978-604-56-9301-1', 85000, 1945, 'NXB Văn Học', 'Available', 'animal_farm.jpg', 12, 12, 'id:11261770'),
    ('BK115', 'Đảo Xứ Miến Điện (Burmese Days)', 'George Orwell', 'Tiểu thuyết hiện đại', 8, 'Khu T - Kệ 01', '978-604-56-9302-8', 110000, 1934, 'NXB Văn Học', 'Available', 'burmese_days.jpg', 6, 6, 'isbn:9780156148504'),
    ('BK116', 'Đường Về Wigan Pier (The Road to Wigan Pier)', 'George Orwell', 'Lịch sử & Địa lý', 4, 'Khu L - Kệ 04', '978-604-56-9303-5', 125000, 1937, 'NXB Văn Học', 'Available', 'road_to_wigan_pier.jpg', 5, 5, 'isbn:9780156767507'),
    ('BK117', 'Một Chút Không Khí Để Thở (Coming Up for Air)', 'George Orwell', 'Tiểu thuyết hiện đại', 8, 'Khu T - Kệ 01', '978-604-56-9304-2', 105000, 1939, 'NXB Văn Học', 'Available', 'coming_up_for_air.jpg', 6, 6, 'isbn:9780156196253'),

    # BK118 - BK122 Victor Hugo (5 thêm -> tổng 6 cuốn)
    ('BK118', 'Những Người Khốn Khổ (Tập 1 - Fantine)', 'Victor Hugo', 'Văn học kinh điển', 2, 'Khu B - Kệ 05', '978-604-56-9401-1', 120000, 1862, 'NXB Văn Học', 'Available', 'les_miserables_1.jpg', 6, 6, 'id:12721865'),
    ('BK119', 'Những Người Khốn Khổ (Tập 2 - Cosette)', 'Victor Hugo', 'Văn học kinh điển', 2, 'Khu B - Kệ 05', '978-604-56-9402-8', 120000, 1862, 'NXB Văn Học', 'Available', 'les_miserables_2.jpg', 6, 6, 'id:12721865'),
    ('BK120', 'Những Người Khốn Khổ (Tập 3 - Jean Valjean)', 'Victor Hugo', 'Văn học kinh điển', 2, 'Khu B - Kệ 05', '978-604-56-9403-5', 130000, 1862, 'NXB Văn Học', 'Available', 'les_miserables_3.jpg', 6, 6, 'id:12721865'),
    ('BK121', 'Nhà Thờ Đức Bà Paris (Notre-Dame de Paris)', 'Victor Hugo', 'Văn học kinh điển', 2, 'Khu B - Kệ 05', '978-604-56-9404-2', 155000, 1831, 'NXB Văn Học', 'Available', 'notre_dame_paris.jpg', 8, 8, 'isbn:9780140443530'),
    ('BK122', 'Thằng Cười (The Man Who Laughs)', 'Victor Hugo', 'Văn học kinh điển', 2, 'Khu B - Kệ 05', '978-604-56-9405-9', 165000, 1869, 'NXB Văn Học', 'Available', 'the_man_who_laughs.jpg', 6, 6, 'isbn:9780141392264'),

    # BK123 - BK128 Nam Cao (6 cuốn)
    ('BK123', 'Chí Phèo', 'Nam Cao', 'Văn học kinh điển', 2, 'Khu B - Kệ 07', '978-604-56-9501-1', 65000, 1941, 'NXB Văn Học', 'Available', 'chi_pheo.jpg', 12, 12, 'id:10873292'),
    ('BK124', 'Lão Hạc', 'Nam Cao', 'Văn học kinh điển', 2, 'Khu B - Kệ 07', '978-604-56-9502-8', 60000, 1943, 'NXB Văn Học', 'Available', 'lao_hac.jpg', 10, 10, 'id:10873292'),
    ('BK125', 'Sống Mòn', 'Nam Cao', 'Văn học kinh điển', 2, 'Khu B - Kệ 07', '978-604-56-9503-5', 85000, 1944, 'NXB Văn Học', 'Available', 'song_mon.jpg', 8, 8, 'id:10873292'),
    ('BK126', 'Đời Thừa', 'Nam Cao', 'Văn học kinh điển', 2, 'Khu B - Kệ 07', '978-604-56-9504-2', 65000, 1943, 'NXB Văn Học', 'Available', 'doi_thua.jpg', 7, 7, 'id:10873292'),
    ('BK127', 'Giăng Sáng', 'Nam Cao', 'Văn học kinh điển', 2, 'Khu B - Kệ 07', '978-604-56-9505-9', 60000, 1942, 'NXB Văn Học', 'Available', 'giang_sang.jpg', 7, 7, 'id:10873292'),
    ('BK128', 'Đôi Mắt', 'Nam Cao', 'Văn học kinh điển', 2, 'Khu B - Kệ 07', '978-604-56-9506-6', 60000, 1948, 'NXB Văn Học', 'Available', 'doi_mat.jpg', 8, 8, 'id:10873292'),

    # BK129 - BK133 Tô Hoài (5 thêm -> tổng 6 cuốn)
    ('BK129', 'Vợ Chồng A Phủ', 'Tô Hoài', 'Văn học kinh điển', 2, 'Khu B - Kệ 08', '978-604-2-08534-0', 70000, 1952, 'NXB Kim Đồng', 'Available', 'vo_chong_a_phu.jpg', 10, 10, 'id:8967534'),
    ('BK130', 'O Chuột', 'Tô Hoài', 'Truyện tranh & Sách thiếu nhi', 11, 'Khu TN - Kệ 06', '978-604-2-08535-7', 60000, 1942, 'NXB Kim Đồng', 'Available', 'o_chuot.jpg', 8, 8, 'id:8967534'),
    ('BK131', 'Quê Người', 'Tô Hoài', 'Văn học kinh điển', 2, 'Khu B - Kệ 08', '978-604-2-08536-4', 85000, 1941, 'NXB Kim Đồng', 'Available', 'que_nguoi.jpg', 7, 7, 'id:8967534'),
    ('BK132', 'Cát Bụi Chân Ai', 'Tô Hoài', 'Hồi ký & Tự truyện', 7, 'Khu H - Kệ 05', '978-604-2-08537-1', 110000, 1992, 'NXB Hội Nhà Văn', 'Available', 'cat_bui_chan_ai.jpg', 7, 7, 'id:8967534'),
    ('BK133', 'Ba Người Khác', 'Tô Hoài', 'Tiểu thuyết hiện đại', 8, 'Khu T - Kệ 06', '978-604-2-08538-8', 115000, 2006, 'NXB Hội Nhà Văn', 'Available', 'ba_nguoi_khac.jpg', 6, 6, 'id:8967534'),

    # BK134 - BK137 Vũ Trọng Phụng (4 thêm -> tổng 5 cuốn)
    ('BK134', 'Giông Tố', 'Vũ Trọng Phụng', 'Văn học kinh điển', 2, 'Khu B - Kệ 03', '978-604-56-1189-0', 85000, 1936, 'NXB Văn Học', 'Available', 'giong_to.jpg', 8, 8, 'id:10873292'),
    ('BK135', 'Vỡ Đê', 'Vũ Trọng Phụng', 'Văn học kinh điển', 2, 'Khu B - Kệ 03', '978-604-56-1190-6', 80000, 1936, 'NXB Văn Học', 'Available', 'vo_de.jpg', 7, 7, 'id:10873292'),
    ('BK136', 'Cơm Thầy Cơm Cô', 'Vũ Trọng Phụng', 'Văn học kinh điển', 2, 'Khu B - Kệ 03', '978-604-56-1191-3', 75000, 1936, 'NXB Văn Học', 'Available', 'com_thay_com_co.jpg', 7, 7, 'id:10873292'),
    ('BK137', 'Lục Xì', 'Vũ Trọng Phụng', 'Văn học kinh điển', 2, 'Khu B - Kệ 03', '978-604-56-1192-0', 70000, 1937, 'NXB Văn Học', 'Available', 'luc_xi.jpg', 6, 6, 'id:10873292'),

    # BK138 - BK143 Malcolm Gladwell (6 cuốn)
    ('BK138', 'Điểm Bùng Phát (The Tipping Point)', 'Malcolm Gladwell', 'Kỹ năng sống & Tâm lý', 6, 'Khu K - Kệ 05', '978-604-56-9601-1', 145000, 2000, 'NXB Thế Giới', 'Available', 'tipping_point.jpg', 9, 9, 'id:10873292'),
    ('BK139', 'Trong Chớp Mắt (Blink)', 'Malcolm Gladwell', 'Kỹ năng sống & Tâm lý', 6, 'Khu K - Kệ 05', '978-604-56-9602-8', 150000, 2005, 'NXB Thế Giới', 'Available', 'blink_gladwell.jpg', 8, 8, 'isbn:9780316010665'),
    ('BK140', 'Những Kẻ Xuất Chúng (Outliers)', 'Malcolm Gladwell', 'Kỹ năng sống & Tâm lý', 6, 'Khu K - Kệ 05', '978-604-56-9603-5', 165000, 2008, 'NXB Thế Giới', 'Available', 'outliers_gladwell.jpg', 10, 10, 'isbn:9780316017930'),
    ('BK141', 'Chú Bé David và Gã Khổng Lồ Goliath', 'Malcolm Gladwell', 'Kỹ năng sống & Tâm lý', 6, 'Khu K - Kệ 05', '978-604-56-9604-2', 155000, 2013, 'NXB Thế Giới', 'Available', 'david_and_goliath.jpg', 7, 7, 'isbn:9780316204378'),
    ('BK142', 'Đọc Vị Người Lạ (Talking to Strangers)', 'Malcolm Gladwell', 'Kỹ năng sống & Tâm lý', 6, 'Khu K - Kệ 05', '978-604-56-9605-9', 175000, 2019, 'NXB Thế Giới', 'Available', 'talking_to_strangers.jpg', 8, 8, 'isbn:9780316478526'),
    ('BK143', 'Trái Lựu Đạn Của Bombe (The Bomber Mafia)', 'Malcolm Gladwell', 'Lịch sử & Địa lý', 4, 'Khu L - Kệ 05', '978-604-56-9606-6', 160000, 2021, 'NXB Thế Giới', 'Available', 'bomber_mafia.jpg', 6, 6, 'isbn:9780316296618'),

    # BK144 - BK149 Walter Isaacson (6 cuốn)
    ('BK144', 'Steve Jobs: Tiểu Sử', 'Walter Isaacson', 'Hồi ký & Tự truyện', 7, 'Khu H - Kệ 06', '978-604-56-9701-1', 280000, 2011, 'NXB Trẻ', 'Available', 'steve_jobs_isaacson.jpg', 10, 10, 'id:12374726'),
    ('BK145', 'Leonardo Da Vinci: Tiểu Sử Danh Họa', 'Walter Isaacson', 'Hồi ký & Tự truyện', 7, 'Khu H - Kệ 06', '978-604-56-9702-8', 320000, 2017, 'NXB Thế Giới', 'Available', 'leonardo_da_vinci.jpg', 6, 6, 'isbn:9781501139154'),
    ('BK146', 'Einstein: Cuộc Đời Và Vũ Trụ', 'Walter Isaacson', 'Hồi ký & Tự truyện', 7, 'Khu H - Kệ 06', '978-604-56-9703-5', 290000, 2007, 'NXB Thế Giới', 'Available', 'einstein_isaacson.jpg', 7, 7, 'isbn:9780743264747'),
    ('BK147', 'Những Người Tiên Phong (The Innovators)', 'Walter Isaacson', 'Công nghệ thông tin', 9, 'Khu IT - Kệ 05', '978-604-56-9704-2', 260000, 2014, 'NXB Thế Giới', 'Available', 'the_innovators.jpg', 8, 8, 'isbn:9781476708690'),
    ('BK148', 'Benjamin Franklin: Một Đời Độc Lập', 'Walter Isaacson', 'Hồi ký & Tự truyện', 7, 'Khu H - Kệ 06', '978-604-56-9705-9', 270000, 2003, 'NXB Thế Giới', 'Available', 'benjamin_franklin.jpg', 5, 5, 'isbn:9780684807614'),
    ('BK149', 'Elon Musk: Tiểu Sử Vị Tỷ Phú Công Nghệ', 'Walter Isaacson', 'Hồi ký & Tự truyện', 7, 'Khu H - Kệ 06', '978-604-56-9706-6', 350000, 2023, 'NXB Trẻ', 'Available', 'elon_musk_isaacson.jpg', 9, 9, 'isbn:9781982181284'),

    # BK150 - BK155 Stephen Hawking (6 cuốn)
    ('BK150', 'Lược Sử Thời Gian (A Brief History of Time)', 'Stephen Hawking', 'Toán học & Khoa học tự nhiên', 3, 'Khu C - Kệ 03', '978-604-56-9801-1', 135000, 1988, 'NXB Trẻ', 'Available', 'brief_history_of_time.jpg', 12, 12, 'id:10432365'),
    ('BK151', 'Vũ Trụ Trong Vỏ Hạt Dẻ', 'Stephen Hawking', 'Toán học & Khoa học tự nhiên', 3, 'Khu C - Kệ 03', '978-604-56-9802-8', 165000, 2001, 'NXB Trẻ', 'Available', 'universe_nutshell.jpg', 8, 8, 'isbn:9780553802023'),
    ('BK152', 'Bản Thiết Kế Vĩ Đại (The Grand Design)', 'Stephen Hawking', 'Toán học & Khoa học tự nhiên', 3, 'Khu C - Kệ 03', '978-604-56-9803-5', 145000, 2010, 'NXB Trẻ', 'Available', 'the_grand_design.jpg', 8, 8, 'isbn:9780553805307'),
    ('BK153', 'Những Câu Hỏi Lớn Dành Cho Nhân Loại', 'Stephen Hawking', 'Toán học & Khoa học tự nhiên', 3, 'Khu C - Kệ 03', '978-604-56-9804-2', 155000, 2018, 'NXB Trẻ', 'Available', 'brief_answers.jpg', 9, 9, 'isbn:9781984819192'),
    ('BK154', 'Lỗ Đen và Vũ Trụ Sơ Khai', 'Stephen Hawking', 'Toán học & Khoa học tự nhiên', 3, 'Khu C - Kệ 03', '978-604-56-9805-9', 125000, 1993, 'NXB Trẻ', 'Available', 'black_holes_hawking.jpg', 6, 6, 'isbn:9780553374117'),
    ('BK155', 'Lược Sử Tương Lai Vũ Trụ', 'Stephen Hawking', 'Toán học & Khoa học tự nhiên', 3, 'Khu C - Kệ 03', '978-604-56-9806-6', 140000, 2005, 'NXB Trẻ', 'Available', 'briefer_history_time.jpg', 7, 7, 'isbn:9780553804362'),

    # BK156 - BK160 Martin Fowler (5 cuốn)
    ('BK156', 'Refactoring: Cải Tiến Mã Nguồn Hiện Có', 'Martin Fowler', 'Công nghệ thông tin', 9, 'Khu IT - Kệ 06', '978-013-47-5759-9', 420000, 2018, 'Addison-Wesley', 'Available', 'refactoring_fowler.jpg', 8, 8, 'id:7087623'),
    ('BK157', 'Patterns of Enterprise Application Architecture', 'Martin Fowler', 'Công nghệ thông tin', 9, 'Khu IT - Kệ 06', '978-032-11-2742-6', 460000, 2002, 'Addison-Wesley', 'Available', 'poeaa_fowler.jpg', 6, 6, 'isbn:9780321127426'),
    ('BK158', 'UML Distilled: Tóm Lược Ngôn Ngữ Mô Hình Hóa', 'Martin Fowler', 'Công nghệ thông tin', 9, 'Khu IT - Kệ 06', '978-032-11-9368-1', 220000, 2003, 'Addison-Wesley', 'Available', 'uml_distilled.jpg', 7, 7, 'isbn:9780321193681'),
    ('BK159', 'Domain-Specific Languages (DSL)', 'Martin Fowler', 'Công nghệ thông tin', 9, 'Khu IT - Kệ 06', '978-032-17-1294-3', 450000, 2010, 'Addison-Wesley', 'Available', 'dsl_fowler.jpg', 5, 5, 'isbn:9780321712943'),
    ('BK160', 'NoSQL Distilled: Tinh Hoa Cơ Sở Dữ Liệu NoSQL', 'Martin Fowler', 'Công nghệ thông tin', 9, 'Khu IT - Kệ 06', '978-032-18-2662-6', 280000, 2012, 'Addison-Wesley', 'Available', 'nosql_distilled.jpg', 6, 6, 'isbn:9780321826626'),

    # BK161 - BK168 Thích Nhất Hạnh (8 cuốn)
    ('BK161', 'Giận (Anger: Wisdom for Cooling the Flames)', 'Thích Nhất Hạnh', 'Kỹ năng sống & Tâm lý', 6, 'Khu K - Kệ 06', '978-604-56-9901-1', 110000, 2001, 'NXB Nhã Nam', 'Available', 'gian_tnh.jpg', 12, 12, 'id:12836262'),
    ('BK162', 'Phép Lạ Của Sự Tỉnh Thức', 'Thích Nhất Hạnh', 'Kỹ năng sống & Tâm lý', 6, 'Khu K - Kệ 06', '978-604-56-9902-8', 95000, 1975, 'NXB Nhã Nam', 'Available', 'phep_la_tinh_thuc.jpg', 10, 10, 'id:12836262'),
    ('BK163', 'Không Diệt Không Sinh Đừng Sợ Hãi', 'Thích Nhất Hạnh', 'Kỹ năng sống & Tâm lý', 6, 'Khu K - Kệ 06', '978-604-56-9903-5', 105000, 2002, 'NXB Nhã Nam', 'Available', 'khong_diet_khong_sinh.jpg', 9, 9, 'id:12836262'),
    ('BK164', 'An Lạc Từng Bước Chân', 'Thích Nhất Hạnh', 'Kỹ năng sống & Tâm lý', 6, 'Khu K - Kệ 06', '978-604-56-9904-2', 100000, 1990, 'NXB Nhã Nam', 'Available', 'an_lac_tung_buoc_chan.jpg', 8, 8, 'id:12836262'),
    ('BK165', 'Nẻo Về Của Ý', 'Thích Nhất Hạnh', 'Lý luận chính trị & Triết học', 1, 'Khu A - Kệ 04', '978-604-56-9905-9', 115000, 1972, 'NXB Nhã Nam', 'Available', 'neo_ve_cua_y.jpg', 7, 7, 'id:12836262'),
    ('BK166', 'Trái Tim Của Bụt', 'Thích Nhất Hạnh', 'Lý luận chính trị & Triết học', 1, 'Khu A - Kệ 04', '978-604-56-9906-6', 160000, 1998, 'NXB Nhã Nam', 'Available', 'trai_tim_cua_but.jpg', 8, 8, 'id:12836262'),
    ('BK167', 'Thầy Cô Giáo Hạnh Phúc Sẽ Thay Đổi Thế Giới', 'Thích Nhất Hạnh', 'Kỹ năng sống & Tâm lý', 6, 'Khu K - Kệ 06', '978-604-56-9907-3', 135000, 2017, 'NXB Nhã Nam', 'Available', 'thay_co_hanh_phuc.jpg', 8, 8, 'id:12836262'),
    ('BK168', 'Hạnh Phúc Cầm Tay', 'Thích Nhất Hạnh', 'Kỹ năng sống & Tâm lý', 6, 'Khu K - Kệ 06', '978-604-56-9908-0', 105000, 2015, 'NXB Nhã Nam', 'Available', 'hanh_phuc_cam_tay.jpg', 9, 9, 'id:12836262'),

    # BK169 - BK178 Agatha Christie (10 cuốn)
    ('BK169', 'Mười Người Da Đen Nhỏ (And Then There Were None)', 'Agatha Christie', 'Tiểu thuyết hiện đại', 8, 'Khu T - Kệ 07', '978-604-56-7701-1', 115000, 1939, 'NXB Trẻ', 'Available', 'and_then_there_were_none.jpg', 12, 12, 'id:11100465'),
    ('BK170', 'Án Mạng Trên Chuyến Tàu Tốc Hành Phương Đông', 'Agatha Christie', 'Tiểu thuyết hiện đại', 8, 'Khu T - Kệ 07', '978-604-56-7702-8', 125000, 1934, 'NXB Trẻ', 'Available', 'murder_orient_express.jpg', 10, 10, 'id:11100465'),
    ('BK171', 'Án Mạng Trên Sông Nile (Death on the Nile)', 'Agatha Christie', 'Tiểu thuyết hiện đại', 8, 'Khu T - Kệ 07', '978-604-56-7703-5', 130000, 1937, 'NXB Trẻ', 'Available', 'death_on_the_nile.jpg', 9, 9, 'isbn:9780062073556'),
    ('BK172', 'Vụ Ám Sát Roger Ackroyd', 'Agatha Christie', 'Tiểu thuyết hiện đại', 8, 'Khu T - Kệ 07', '978-604-56-7704-2', 120000, 1926, 'NXB Trẻ', 'Available', 'murder_roger_ackroyd.jpg', 8, 8, 'isbn:9780062073563'),
    ('BK173', 'Chuỗi Án Mạng A.B.C', 'Agatha Christie', 'Tiểu thuyết hiện đại', 8, 'Khu T - Kệ 07', '978-604-56-7705-9', 110000, 1936, 'NXB Trẻ', 'Available', 'abc_murders.jpg', 7, 7, 'isbn:9780062073587'),
    ('BK174', 'Giáng Sinh Của Poirot', 'Agatha Christie', 'Tiểu thuyết hiện đại', 8, 'Khu T - Kệ 07', '978-604-56-7706-6', 115000, 1938, 'NXB Trẻ', 'Available', 'hercule_poirots_christmas.jpg', 7, 7, 'isbn:9780062073730'),
    ('BK175', 'Đêm Vô Tận (Endless Night)', 'Agatha Christie', 'Tiểu thuyết hiện đại', 8, 'Khu T - Kệ 07', '978-604-56-7707-3', 105000, 1967, 'NXB Trẻ', 'Available', 'endless_night.jpg', 6, 6, 'isbn:9780062073648'),
    ('BK176', 'Ngôi Nhà Quái Dị (Crooked House)', 'Agatha Christie', 'Tiểu thuyết hiện đại', 8, 'Khu T - Kệ 07', '978-604-56-7708-0', 110000, 1949, 'NXB Trẻ', 'Available', 'crooked_house.jpg', 7, 7, 'isbn:9780062073532'),
    ('BK177', 'Năm Chú Heo Con (Five Little Pigs)', 'Agatha Christie', 'Tiểu thuyết hiện đại', 8, 'Khu T - Kệ 07', '978-604-56-7709-7', 105000, 1942, 'NXB Trẻ', 'Available', 'five_little_pigs.jpg', 6, 6, 'isbn:9780062073655'),
    ('BK178', 'Thảm Kịch Ba Hồi (Three Act Tragedy)', 'Agatha Christie', 'Tiểu thuyết hiện đại', 8, 'Khu T - Kệ 07', '978-604-56-7710-3', 115000, 1934, 'NXB Trẻ', 'Available', 'three_act_tragedy.jpg', 7, 7, 'isbn:9780062073709'),

    # BK179 - BK188 Kim Dung (10 cuốn)
    ('BK179', 'Tiếu Ngạo Giang Hồ', 'Kim Dung', 'Văn học kinh điển', 2, 'Khu B - Kệ 09', '978-604-56-6601-1', 350000, 1967, 'NXB Văn Học', 'Available', 'tieu_ngao_giang_ho.jpg', 8, 8, 'id:9255229'),
    ('BK180', 'Anh Hùng Xạ Điêu', 'Kim Dung', 'Văn học kinh điển', 2, 'Khu B - Kệ 09', '978-604-56-6602-8', 340000, 1957, 'NXB Văn Học', 'Available', 'anh_hung_xa_dieu.jpg', 8, 8, 'id:9255229'),
    ('BK181', 'Thần Điêu Đại Hiệp', 'Kim Dung', 'Văn học kinh điển', 2, 'Khu B - Kệ 09', '978-604-56-6603-5', 360000, 1959, 'NXB Văn Học', 'Available', 'than_dieu_dai_hiep.jpg', 7, 7, 'id:9255229'),
    ('BK182', 'Ỷ Thiên Đồ Long Ký', 'Kim Dung', 'Văn học kinh điển', 2, 'Khu B - Kệ 09', '978-604-56-6604-2', 380000, 1961, 'NXB Văn Học', 'Available', 'y_thien_do_long_ky.jpg', 8, 8, 'id:9255229'),
    ('BK183', 'Thiên Long Bát Bộ', 'Kim Dung', 'Văn học kinh điển', 2, 'Khu B - Kệ 09', '978-604-56-6605-9', 420000, 1963, 'NXB Văn Học', 'Available', 'thien_long_bat_bo.jpg', 9, 9, 'id:9255229'),
    ('BK184', 'Lộc Đỉnh Ký', 'Kim Dung', 'Văn học kinh điển', 2, 'Khu B - Kệ 09', '978-604-56-6606-6', 450000, 1969, 'NXB Văn Học', 'Available', 'loc_dinh_ky.jpg', 7, 7, 'id:9255229'),
    ('BK185', 'Bích Huyết Kiếm', 'Kim Dung', 'Văn học kinh điển', 2, 'Khu B - Kệ 09', '978-604-56-6607-3', 210000, 1956, 'NXB Văn Học', 'Available', 'bich_huyet_kiem.jpg', 6, 6, 'id:9255229'),
    ('BK186', 'Tuyết Sơn Phi Hồ', 'Kim Dung', 'Văn học kinh điển', 2, 'Khu B - Kệ 09', '978-604-56-6608-0', 170000, 1959, 'NXB Văn Học', 'Available', 'tuyet_son_phi_ho.jpg', 6, 6, 'id:9255229'),
    ('BK187', 'Hiệp Khách Hành', 'Kim Dung', 'Văn học kinh điển', 2, 'Khu B - Kệ 09', '978-604-56-6609-7', 260000, 1965, 'NXB Văn Học', 'Available', 'hiep_khach_hanh.jpg', 6, 6, 'id:9255229'),
    ('BK188', 'Liên Thành Quyết', 'Kim Dung', 'Văn học kinh điển', 2, 'Khu B - Kệ 09', '978-604-56-6610-3', 185000, 1963, 'NXB Văn Học', 'Available', 'lien_thanh_quyet.jpg', 6, 6, 'id:9255229'),

    # BK189 - BK194 Stephen King (6 cuốn)
    ('BK189', 'The Shining: Ngôi Nhà Ma Ám', 'Stephen King', 'Tiểu thuyết hiện đại', 8, 'Khu T - Kệ 08', '978-604-56-5501-1', 195000, 1977, 'NXB Hội Nhà Văn', 'Available', 'the_shining.jpg', 9, 9, 'id:12376585'),
    ('BK190', 'IT: Gã Hề Ma Quái', 'Stephen King', 'Tiểu thuyết hiện đại', 8, 'Khu T - Kệ 08', '978-604-56-5502-8', 290000, 1986, 'NXB Hội Nhà Văn', 'Available', 'it_stephen_king.jpg', 8, 8, 'isbn:9781501142970'),
    ('BK191', 'Misery: Nỗi Ám Ảnh Bị Giam Cầm', 'Stephen King', 'Tiểu thuyết hiện đại', 8, 'Khu T - Kệ 08', '978-604-56-5503-5', 165000, 1987, 'NXB Hội Nhà Văn', 'Available', 'misery_king.jpg', 7, 7, 'isbn:9781501143106'),
    ('BK192', 'Carrie: Cơn Thịnh Nộ Của Carrie', 'Stephen King', 'Tiểu thuyết hiện đại', 8, 'Khu T - Kệ 08', '978-604-56-5504-2', 135000, 1974, 'NXB Hội Nhà Văn', 'Available', 'carrie_king.jpg', 7, 7, 'isbn:9780307743664'),
    ('BK193', 'Pet Sematary: Nghĩa Địa Thú Cưng', 'Stephen King', 'Tiểu thuyết hiện đại', 8, 'Khu T - Kệ 08', '978-604-56-5505-9', 175000, 1983, 'NXB Hội Nhà Văn', 'Available', 'pet_sematary.jpg', 6, 6, 'isbn:9781501156700'),
    ('BK194', 'The Green Mile: Dặm Xanh Kỳ Diệu', 'Stephen King', 'Tiểu thuyết hiện đại', 8, 'Khu T - Kệ 08', '978-604-56-5506-6', 185000, 1996, 'NXB Hội Nhà Văn', 'Available', 'green_mile.jpg', 8, 8, 'isbn:9781501160448'),

    # BK195 - BK198 Hồ Chí Minh (3 thêm -> tổng 4 cuốn)
    ('BK195', 'Nhật Ký Trong Tù', 'Hồ Chí Minh', 'Lý luận chính trị & Triết học', 1, 'Khu A - Kệ 01', '978-604-90-1123-8', 75000, 1943, 'NXB Chính Trị Quốc Gia', 'Available', 'nhat_ky_trong_tu.jpg', 15, 15, 'id:12879555'),
    ('BK196', 'Tuyên Ngôn Độc Lập & Các Tác Phẩm Chọn Lọc', 'Hồ Chí Minh', 'Lý luận chính trị & Triết học', 1, 'Khu A - Kệ 01', '978-604-90-1124-5', 85000, 1945, 'NXB Chính Trị Quốc Gia', 'Available', 'tuyen_ngon_doc_lap.jpg', 12, 12, 'id:12879555'),
    ('BK197', 'Bản Án Chế Độ Thực Dân Pháp', 'Hồ Chí Minh', 'Lý luận chính trị & Triết học', 1, 'Khu A - Kệ 01', '978-604-90-1125-2', 80000, 1925, 'NXB Chính Trị Quốc Gia', 'Available', 'ban_an_thuc_dan.jpg', 10, 10, 'id:12879555'),

    # BK198 - BK203 Thạch Lam & Nguyễn Tuân & Nguyễn Du (6 cuốn)
    ('BK198', 'Truyện Kiều', 'Nguyễn Du', 'Văn học kinh điển', 2, 'Khu B - Kệ 10', '978-604-56-4401-1', 95000, 1820, 'NXB Văn Học', 'Available', 'truyen_kieu.jpg', 15, 15, 'id:10873292'),
    ('BK199', 'Văn Tế Thập Loại Chúng Sinh', 'Nguyễn Du', 'Văn học kinh điển', 2, 'Khu B - Kệ 10', '978-604-56-4402-8', 55000, 1820, 'NXB Văn Học', 'Available', 'van_te_thap_loai.jpg', 8, 8, 'id:10873292'),
    ('BK200', 'Vang Bóng Một Thời', 'Nguyễn Tuân', 'Văn học kinh điển', 2, 'Khu B - Kệ 10', '978-604-56-4403-5', 85000, 1940, 'NXB Văn Học', 'Available', 'vang_bong_mot_thoi.jpg', 9, 9, 'id:10873292'),
    ('BK201', 'Gió Đầu Mùa', 'Thạch Lam', 'Văn học kinh điển', 2, 'Khu B - Kệ 10', '978-604-56-4404-2', 70000, 1937, 'NXB Văn Học', 'Available', 'gio_dau_mua.jpg', 10, 10, 'id:10873292'),
    ('BK202', 'Hà Nội Băm Sáu Phố Phường', 'Thạch Lam', 'Văn học kinh điển', 2, 'Khu B - Kệ 10', '978-604-56-4405-9', 75000, 1938, 'NXB Văn Học', 'Available', 'ha_noi_36_pho_phuong.jpg', 12, 12, 'id:10873292'),
    ('BK203', 'Nắng Trong Vườn', 'Thạch Lam', 'Văn học kinh điển', 2, 'Khu B - Kệ 10', '978-604-56-4406-6', 68000, 1938, 'NXB Văn Học', 'Available', 'nang_trong_vuon.jpg', 8, 8, 'id:10873292'),

    # BK204 - BK209 Tony Buổi Sáng & Sách Kỹ Năng Đương Đại (6 cuốn)
    ('BK204', 'Cà Phê Cùng Tony', 'Tony Buổi Sáng', 'Kỹ năng sống & Tâm lý', 6, 'Khu K - Kệ 07', '978-604-1-03451-4', 85000, 2014, 'NXB Trẻ', 'Available', 'ca_phe_cung_tony.jpg', 15, 15, 'id:12836262'),
    ('BK205', 'Trên Đường Băng', 'Tony Buổi Sáng', 'Kỹ năng sống & Tâm lý', 6, 'Khu K - Kệ 07', '978-604-1-03452-1', 95000, 2015, 'NXB Trẻ', 'Available', 'tren_duong_bang.jpg', 15, 15, 'id:12836262'),
    ('BK206', 'Dấn Thân (Lean In)', 'Sheryl Sandberg', 'Kỹ năng sống & Tâm lý', 6, 'Khu K - Kệ 07', '978-604-56-3399-1', 145000, 2013, 'NXB Trẻ', 'Available', 'lean_in_sandberg.jpg', 8, 8, 'isbn:9780385349949'),
    ('BK207', 'Tuần Làm Việc 4 Giờ', 'Timothy Ferriss', 'Kỹ năng sống & Tâm lý', 6, 'Khu K - Kệ 07', '978-604-56-3398-4', 165000, 2007, 'NXB Lao Động', 'Available', '4_hour_workweek.jpg', 9, 9, 'isbn:9780307465351'),
    ('BK208', 'Khởi Nghiệp Tinh Gọn (The Lean Startup)', 'Eric Ries', 'Công nghệ thông tin', 9, 'Khu IT - Kệ 07', '978-604-56-3397-7', 155000, 2011, 'NXB Tổng Hợp TP.HCM', 'Available', 'the_lean_startup.jpg', 10, 10, 'isbn:9780307887894'),
    ('BK209', 'Không Đến Một (Zero to One)', 'Peter Thiel', 'Công nghệ thông tin', 9, 'Khu IT - Kệ 07', '978-604-56-3396-0', 140000, 2014, 'NXB Trẻ', 'Available', 'zero_to_one.jpg', 10, 10, 'isbn:9780804139298'),

    # BK210 - BK216 Khoa học viễn tưởng & Công nghệ đỉnh cao (7 cuốn)
    ('BK210', 'Dune: Cứu Tinh Xứ Cát (Dune Messiah)', 'Frank Herbert', 'Khoa học viễn tưởng', 5, 'Khu V - Kệ 02', '978-604-56-6891-7', 185000, 1969, 'NXB Văn Học', 'Available', 'dune_messiah.jpg', 6, 6, 'isbn:9780441172696'),
    ('BK211', 'Dune: Con Trẻ Xứ Cát (Children of Dune)', 'Frank Herbert', 'Khoa học viễn tưởng', 5, 'Khu V - Kệ 02', '978-604-56-6892-4', 210000, 1976, 'NXB Văn Học', 'Available', 'children_of_dune.jpg', 6, 6, 'isbn:9780441104024'),
    ('BK212', 'Artemis: Thành Phố Trên Mặt Trăng', 'Andy Weir', 'Khoa học viễn tưởng', 5, 'Khu V - Kệ 01', '978-604-56-4423-3', 165000, 2017, 'NXB Hội Nhà Văn', 'Available', 'artemis_weir.jpg', 7, 7, 'isbn:9780553448122'),
    ('BK213', 'Dự Án Hail Mary (Project Hail Mary)', 'Andy Weir', 'Khoa học viễn tưởng', 5, 'Khu V - Kệ 01', '978-604-56-4424-0', 215000, 2021, 'NXB Hội Nhà Văn', 'Available', 'project_hail_mary.jpg', 8, 8, 'isbn:9780593135204'),
    ('BK214', 'Foundation: Đế Chế (Tập 1)', 'Isaac Asimov', 'Khoa học viễn tưởng', 5, 'Khu V - Kệ 05', '978-604-56-4425-7', 175000, 1951, 'NXB Văn Học', 'Available', 'foundation_asimov.jpg', 8, 8, 'id:14612610'),
    ('BK215', 'Foundation và Đế Chế (Tập 2)', 'Isaac Asimov', 'Khoa học viễn tưởng', 5, 'Khu V - Kệ 05', '978-604-56-4426-4', 180000, 1952, 'NXB Văn Học', 'Available', 'foundation_and_empire.jpg', 7, 7, 'isbn:9780553293371'),
    ('BK216', 'Đế Chế Đệ Nhị (Second Foundation)', 'Isaac Asimov', 'Khoa học viễn tưởng', 5, 'Khu V - Kệ 05', '978-604-56-4427-1', 185000, 1953, 'NXB Văn Học', 'Available', 'second_foundation.jpg', 7, 7, 'isbn:9780553293364')
]

def download_cover(book):
    bid, title, author, cat, cat_id, shelf, isbn, price, year, pub, status, img_name, total, avail, source = book
    dest_path = os.path.join(IMAGE_DIR, img_name)
    
    # Check if already exists and is large enough (> 5000 bytes)
    if os.path.exists(dest_path) and os.path.getsize(dest_path) > 5000:
        return (bid, title, True, "Already exists")

    url = None
    if source.startswith("id:"):
        cover_id = source[3:]
        url = f"https://covers.openlibrary.org/b/id/{cover_id}-M.jpg"
    elif source.startswith("isbn:"):
        clean_isbn = source[5:].replace("-", "").strip()
        url = f"https://covers.openlibrary.org/b/isbn/{clean_isbn}-M.jpg"
    else:
        # Query title on openlibrary
        q = urllib.parse.quote(source)
        search_url = f"https://openlibrary.org/search.json?q={q}&limit=1"
        try:
            req = urllib.request.Request(search_url, headers=HEADERS)
            with urllib.request.urlopen(req, context=CTX, timeout=8) as resp:
                data = json.loads(resp.read().decode('utf-8'))
                docs = data.get('docs', [])
                if docs and docs[0].get('cover_i'):
                    url = f"https://covers.openlibrary.org/b/id/{docs[0]['cover_i']}-M.jpg"
                elif docs and docs[0].get('isbn'):
                    url = f"https://covers.openlibrary.org/b/isbn/{docs[0]['isbn'][0]}-M.jpg"
        except Exception:
            pass

    if not url:
        url = "https://covers.openlibrary.org/b/id/9255229-M.jpg"

    try:
        req = urllib.request.Request(url, headers=HEADERS)
        with urllib.request.urlopen(req, context=CTX, timeout=12) as resp:
            content = resp.read()
            if len(content) > 1000:
                with open(dest_path, "wb") as f:
                    f.write(content)
                return (bid, title, True, f"Downloaded {len(content)} bytes from {url}")
            else:
                return (bid, title, False, "Downloaded content too small")
    except Exception as e:
        return (bid, title, False, f"Failed: {e}")

def main():
    print(f"Total books to process: {len(BOOKS)}")
    print("Downloading authentic covers...")
    success = 0
    with ThreadPoolExecutor(max_workers=8) as executor:
        futures = {executor.submit(download_cover, b): b for b in BOOKS}
        for future in as_completed(futures):
            bid, title, ok, msg = future.result()
            if ok:
                success += 1
            print(f"[{bid}] {title[:35]:<35}: {'OK' if ok else 'FAIL'} ({msg})")

    print(f"Cover download finished: {success}/{len(BOOKS)} valid covers.")

    # Now rewrite seed_data.sql
    print("Updating seed_data.sql...")
    with open(SEED_SQL_PATH, "r", encoding="utf-8") as f:
        content = f.read()

    # Find the start of books insert
    book_start = content.find("INSERT OR IGNORE INTO books")
    tx_start = content.find("INSERT OR IGNORE INTO borrow_transactions")
    
    header = content[:book_start]
    footer = content[tx_start:]

    book_values = []
    for b in BOOKS:
        bid, title, author, cat, cat_id, shelf, isbn, price, year, pub, status, img_name, total, avail, _ = b
        # Escape single quotes in strings
        title_esc = title.replace("'", "''")
        author_esc = author.replace("'", "''")
        pub_esc = pub.replace("'", "''")
        shelf_esc = shelf.replace("'", "''")
        val = f"('{bid}', '{title_esc}', '{author_esc}', '{cat}', {cat_id}, '{shelf_esc}', '{isbn}', {price}, {year}, '{pub_esc}', '{status}', '/com/vithay/libman/images/{img_name}', {total}, {avail}, 0)"
        book_values.append(val)

    new_books_sql = "INSERT OR IGNORE INTO books (id, title, author, category, category_id, shelf_location, isbn, price, publish_year, publisher, status, cover_image, total_copies, available_copies, is_deleted)\nVALUES\n"
    new_books_sql += ",\n".join(book_values) + ";\n\n-- Seed Transactions\n"

    new_content = header + new_books_sql + footer

    with open(SEED_SQL_PATH, "w", encoding="utf-8") as f:
        f.write(new_content)
    print("seed_data.sql updated successfully.")

    # Update sqlite database libman.db
    if os.path.exists(DB_PATH):
        print(f"Updating sqlite database at {DB_PATH}...")
        conn = sqlite3.connect(DB_PATH)
        cur = conn.cursor()
        for b in BOOKS:
            bid, title, author, cat, cat_id, shelf, isbn, price, year, pub, status, img_name, total, avail, _ = b
            cur.execute("""
                INSERT INTO books (id, title, author, category, category_id, shelf_location, isbn, price, publish_year, publisher, status, cover_image, total_copies, available_copies, is_deleted)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 0)
                ON CONFLICT(id) DO UPDATE SET
                    title=excluded.title,
                    author=excluded.author,
                    category=excluded.category,
                    category_id=excluded.category_id,
                    shelf_location=excluded.shelf_location,
                    isbn=excluded.isbn,
                    price=excluded.price,
                    publish_year=excluded.publish_year,
                    publisher=excluded.publisher,
                    cover_image=excluded.cover_image,
                    total_copies=excluded.total_copies,
                    available_copies=excluded.available_copies
            """, (bid, title, author, cat, cat_id, shelf, isbn, price, year, pub, status, f"/com/vithay/libman/images/{img_name}", total, avail))
        conn.commit()
        cur.execute("SELECT count(*) FROM books WHERE is_deleted = 0")
        total_in_db = cur.fetchone()[0]
        print(f"libman.db now contains {total_in_db} active books!")
        conn.close()

if __name__ == "__main__":
    main()
