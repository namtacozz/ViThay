-- Default Users: Quản Trị, Thủ Thư, Độc Giả (Chuẩn hóa 3 phân quyền)
INSERT OR IGNORE INTO users (id, username, password, full_name, role, email, phone, avatar, created_date, expiry_date)
VALUES 
(1, 'quantri', 'admin123', 'Nguyễn Quản Trị', 'Quản Trị', 'admin@libman.edu.vn', '0901234567', '/com/vithay/libman/images/avatar.png', '2024-01-01', '2030-12-31'),
(2, 'thuthu', '123456', 'Nguyễn Minh Trí', 'Thủ Thư', 'thuthu@libman.edu.vn', '0912345678', '/com/vithay/libman/images/avatar.png', '2024-01-01', '2030-12-31'),
(3, 'docgia', '123456', 'Trần Văn An', 'Độc Giả', 'an.tran@gmail.com', '0987654321', '/com/vithay/libman/images/avatar.png', '2024-01-01', '2025-12-31');

-- Categories (Đầy đủ theo chuẩn biên mục thư viện)
INSERT OR IGNORE INTO categories (id, name, description)
VALUES
(1, 'Lý luận chính trị & Triết học', 'Giáo trình, tư tưởng, đường lối chính trị và triết học'),
(2, 'Văn học kinh điển', 'Tác phẩm văn học kinh điển trong nước và thế giới'),
(3, 'Toán học & Khoa học tự nhiên', 'Toán giải tích, toán rời rạc, vật lý, hóa học'),
(4, 'Lịch sử & Địa lý', 'Tài liệu lịch sử nhân loại, lịch sử Việt Nam và địa chí'),
(5, 'Khoa học viễn tưởng', 'Tiểu thuyết khoa học viễn tưởng và công nghệ tương lai'),
(6, 'Kỹ năng sống & Tâm lý', 'Sách phát triển bản thân, tư duy và phương pháp làm việc'),
(7, 'Hồi ký & Tự truyện', 'Tự truyện và ghi chép trải nghiệm nhân vật lịch sử'),
(8, 'Tiểu thuyết hiện đại', 'Văn xuôi hư cấu hiện đại và đương đại thế giới'),
(9, 'Công nghệ thông tin', 'Kỹ thuật phần mềm, cấu trúc dữ liệu, thuật toán và AI'),
(10, 'Ngoại ngữ & Từ điển', 'Giáo trình tiếng Anh, tiếng Nhật, tiếng Pháp và từ điển'),
(11, 'Truyện tranh & Sách thiếu nhi', 'Truyện tranh, cổ tích và sách văn học dành cho thiếu nhi');

-- Seed Readers (Bao gồm độc giả đã cấp thẻ và độc giả mới đăng ký online chờ cấp thẻ, chuẩn hóa 12 số CCCD Đà Nẵng)
INSERT OR IGNORE INTO readers (id, full_name, email, phone, address, id_card, birth_date, join_date, card_issue_date, card_expiry_date, status, is_deleted)
VALUES 
('RD001', 'Lê Văn An', 'an.le@gmail.com', '0912345678', '123 Nguyễn Văn Linh, Q. Hải Châu, TP. Đà Nẵng', '048201012345', '2001-05-12', '2024-01-01', '2024-01-01', '2026-12-31', 'Active', 0),
('RD002', 'Trần Thị Mai Anh', 'mai.tran@gmail.com', '0987654321', '45 Lê Duẩn, Q. Hải Châu, TP. Đà Nẵng', '048202023456', '2002-08-20', '2024-01-01', '2024-01-01', '2026-12-31', 'Active', 0),
('DG001', 'Trần Văn An', 'an.tv@gmail.com', '0905123456', '88 Bạch Đằng, Q. Hải Châu, TP. Đà Nẵng', '048067001258', '1967-01-15', '2024-01-10', '2024-01-10', '2026-12-31', 'Active', 0),
('DG002', 'Nguyễn Văn Nhân', 'nhan.nv@gmail.com', '0905234567', '250 Võ Nguyên Giáp, Q. Sơn Trà, TP. Đà Nẵng', '048087001475', '1987-04-13', '2024-01-10', '2024-01-10', '2026-12-31', 'Active', 0),
('DG003', 'Lê Thị Thu Nhàn', 'nhan.lt@gmail.com', '0905345678', '12 Núi Thành, Q. Hải Châu, TP. Đà Nẵng', '048078001485', '1978-08-27', '2024-01-10', '2024-01-10', '2026-12-31', 'Active', 0),
('DG004', 'Phùng Tuấn Kiệt', 'kiet.pt@gmail.com', '0905456789', '68 Điện Biên Phủ, Q. Thanh Khê, TP. Đà Nẵng', '048085001236', '1985-12-02', '2024-01-10', '2024-01-10', '2024-06-01', 'Expired', 0),
('DG005', 'Hoàng Minh Châu', 'chau.hm@gmail.com', '0918776655', '35 Ngô Quyền, Q. Sơn Trà, TP. Đà Nẵng', '048204056789', '2004-11-18', '2026-03-01', NULL, NULL, 'Chờ Cấp Thẻ', 0),
('DG006', 'Vũ Đức Thịnh', 'thinh.vd@gmail.com', '0977223344', '102 Tôn Đức Thắng, Q. Liên Chiểu, TP. Đà Nẵng', '048203098765', '2003-07-22', '2026-03-02', NULL, NULL, 'Chờ Cấp Thẻ', 0);

-- Seed Books (Kho 36 cuốn sách phong phú đa dạng 11 thể loại, ngôn ngữ, tác giả với bìa riêng biệt 100%)
INSERT OR IGNORE INTO books (id, title, author, category, category_id, shelf_location, isbn, price, publish_year, publisher, status, cover_image, total_copies, available_copies, is_deleted)
VALUES
-- Mockup Books
('B001', 'Sapiens: Lược Sử Loài Người', 'Yuval Noah Harari', 'Lịch sử & Địa lý', 4, 'Khu L - Kệ 01', '978-604-56-4275-7', 189000, 2014, 'NXB Thế Giới', 'Available', '/com/vithay/libman/images/sapiens.jpg', 6, 5, 0),
('B002', 'Dune: Xứ Cát', 'Frank Herbert', 'Khoa học viễn tưởng', 5, 'Khu V - Kệ 02', '978-604-56-6890-0', 245000, 1965, 'NXB Văn Học', 'Borrowed', '/com/vithay/libman/images/dune.jpg', 4, 0, 0),
('B003', 'Educated: Được Học', 'Tara Westover', 'Hồi ký & Tự truyện', 7, 'Khu H - Kệ 01', '978-604-56-7812-1', 165000, 2018, 'NXB Phụ Nữ', 'Available', '/com/vithay/libman/images/educated.jpg', 5, 4, 0),
('B004', 'Atomic Habits: Thay Đổi Tí Hon', 'James Clear', 'Kỹ năng sống & Tâm lý', 6, 'Khu K - Kệ 03', '978-604-56-8923-2', 179000, 2018, 'NXB Thế Giới', 'On Hold', '/com/vithay/libman/images/atomic_habits.jpg', 8, 2, 0),
('B005', '1984', 'George Orwell', 'Tiểu thuyết hiện đại', 8, 'Khu T - Kệ 01', '978-604-56-3412-9', 115000, 1949, 'NXB Văn Học', 'Available', '/com/vithay/libman/images/1984.jpg', 5, 4, 0),

-- Sách SRS Chính Thức (Bìa riêng biệt)
('BK001', 'Tư Tưởng Hồ Chí Minh', 'Lâm Nguyễn Duy', 'Lý luận chính trị & Triết học', 1, 'Khu A - Kệ 01', '978-604-90-1122-1', 95000, 2021, 'NXB Chính Trị Quốc Gia', 'Available', '/com/vithay/libman/images/tu_tuong_hcm.jpg', 10, 9, 0),
('BK002', 'Đồi Gió Hú (Wuthering Heights)', 'Emily Brontë (Dịch: Phan Trọng)', 'Văn học kinh điển', 2, 'Khu B - Kệ 01', '978-604-90-2233-2', 135000, 2019, 'NXB Văn Học', 'Available', '/com/vithay/libman/images/doi_gio_hu.jpg', 5, 4, 0),
('BK003', 'Cánh Đồng Hoang', 'Nguyễn Thị Bé', 'Văn học kinh điển', 2, 'Khu B - Kệ 02', '978-604-90-3344-3', 110000, 2020, 'NXB Trẻ', 'Available', '/com/vithay/libman/images/canh_dong_hoang.jpg', 6, 5, 0),
('BK004', 'Toán Rời Rạc', 'Nguyễn Văn An', 'Toán học & Khoa học tự nhiên', 3, 'Khu C - Kệ 01', '978-604-90-4455-4', 85000, 2022, 'NXB Giáo Dục', 'Available', '/com/vithay/libman/images/toan_roi_rac.jpg', 12, 11, 0),

-- Văn học kinh điển & Việt Nam
('BK005', 'Số Đỏ', 'Vũ Trọng Phụng', 'Văn học kinh điển', 2, 'Khu B - Kệ 03', '978-604-56-1188-3', 75000, 1936, 'NXB Văn Học', 'Available', '/com/vithay/libman/images/so_do.jpg', 8, 8, 0),
('BK006', 'Chiến Tranh Và Hòa Bình (War and Peace)', 'Leo Tolstoy', 'Văn học kinh điển', 2, 'Khu B - Kệ 04', '978-604-56-2299-4', 380000, 1869, 'NXB Văn Học', 'Available', '/com/vithay/libman/images/chien_tranh_hoa_binh.jpg', 4, 4, 0),
('BK007', 'Những Người Khốn Khổ (Les Misérables)', 'Victor Hugo', 'Văn học kinh điển', 2, 'Khu B - Kệ 05', '978-604-56-3311-5', 290000, 1862, 'NXB Văn Học', 'Available', '/com/vithay/libman/images/nhung_nguoi_khon_kho.jpg', 5, 5, 0),

-- Khoa học viễn tưởng & Tương lai
('BK008', 'Người Về Từ Sao Hỏa (The Martian)', 'Andy Weir', 'Khoa học viễn tưởng', 5, 'Khu V - Kệ 01', '978-604-56-4422-6', 155000, 2011, 'NXB Hội Nhà Văn', 'Available', '/com/vithay/libman/images/nguoi_ve_tu_sao_hoa.jpg', 7, 7, 0),
('BK009', 'Tam Thể (The Three-Body Problem)', 'Lưu Từ Hân (Liu Cixin)', 'Khoa học viễn tưởng', 5, 'Khu V - Kệ 03', '978-604-56-5533-7', 220000, 2008, 'NXB Văn Học', 'Available', '/com/vithay/libman/images/tam_the.jpg', 6, 6, 0),

-- Công nghệ thông tin & Khoa học máy tính
('BK010', 'Clean Code: Mã Sạch', 'Robert C. Martin', 'Công nghệ thông tin', 9, 'Khu IT - Kệ 01', '978-013-23-5088-4', 320000, 2008, 'NXB Khoa Học & Kỹ Thuật', 'Available', '/com/vithay/libman/images/clean_code.jpg', 10, 9, 0),
('BK011', 'Design Patterns: Elements of Reusable Object-Oriented Software', 'Erich Gamma, Richard Helm, Ralph Johnson, John Vlissides', 'Công nghệ thông tin', 9, 'Khu IT - Kệ 02', '978-020-16-3361-0', 360000, 1994, 'Addison-Wesley', 'Available', '/com/vithay/libman/images/design_patterns.jpg', 5, 5, 0),
('BK012', 'Designing Data-Intensive Applications', 'Martin Kleppmann', 'Công nghệ thông tin', 9, 'Khu IT - Kệ 03', '978-144-93-7332-0', 450000, 2017, 'O Reilly Media', 'Available', '/com/vithay/libman/images/ddia.jpg', 6, 6, 0),
('BK013', 'Introduction to Algorithms (CLRS)', 'Thomas H. Cormen, Charles E. Leiserson, Ronald L. Rivest, Clifford Stein', 'Công nghệ thông tin', 9, 'Khu IT - Kệ 04', '978-026-20-3384-8', 520000, 2009, 'MIT Press', 'Available', '/com/vithay/libman/images/clrs.jpg', 4, 4, 0),

-- Triết học, Tư tưởng & Lý luận
('BK014', 'Cộng Hòa (The Republic)', 'Plato', 'Lý luận chính trị & Triết học', 1, 'Khu A - Kệ 02', '978-604-56-7744-8', 175000, 2020, 'NXB Thế Giới', 'Available', '/com/vithay/libman/images/cong_hoa.jpg', 5, 5, 0),
('BK015', 'Bàn Về Khế Ước Xã Hội', 'Jean-Jacques Rousseau', 'Lý luận chính trị & Triết học', 1, 'Khu A - Kệ 03', '978-604-56-8855-9', 140000, 2019, 'NXB Tri Thức', 'Available', '/com/vithay/libman/images/khe_uoc_xa_hoi.jpg', 4, 4, 0),

-- Lịch sử thế giới & Việt Nam
('BK016', 'Đại Việt Sử Ký Toàn Thư', 'Ngô Sĩ Liên', 'Lịch sử & Địa lý', 4, 'Khu L - Kệ 02', '978-604-90-5566-5', 420000, 1479, 'NXB Khoa Học Xã Hội', 'Available', '/com/vithay/libman/images/dai_viet_su_ky.jpg', 6, 6, 0),
('BK017', 'Súng, Vi Trùng Và Thép (Guns, Germs, and Steel)', 'Jared Diamond', 'Lịch sử & Địa lý', 4, 'Khu L - Kệ 03', '978-604-56-9966-0', 215000, 1997, 'NXB Tri Thức', 'Available', '/com/vithay/libman/images/sung_vi_trung_thep.jpg', 7, 7, 0),

-- Kỹ năng & Tâm lý học
('BK018', 'Tư Duy Nhanh Và Chậm (Thinking, Fast and Slow)', 'Daniel Kahneman', 'Kỹ năng sống & Tâm lý', 6, 'Khu K - Kệ 01', '978-604-56-1234-5', 198000, 2011, 'NXB Thế Giới', 'Available', '/com/vithay/libman/images/tu_duy_nhanh_cham.jpg', 8, 8, 0),
('BK019', 'Đắc Nhân Tâm (How to Win Friends and Influence People)', 'Dale Carnegie', 'Kỹ năng sống & Tâm lý', 6, 'Khu K - Kệ 02', '978-604-56-2345-6', 98000, 1936, 'NXB Tổng Hợp TP.HCM', 'Available', '/com/vithay/libman/images/dac_nhan_tam.jpg', 15, 15, 0),

-- Hồi ký & Văn học hiện đại
('BK020', 'Khi Hơi Thở Hóa Thinh Không (When Breath Becomes Air)', 'Paul Kalanithi', 'Hồi ký & Tự truyện', 7, 'Khu H - Kệ 02', '978-604-56-3456-7', 125000, 2016, 'NXB Hội Nhà Văn', 'Available', '/com/vithay/libman/images/khi_hoi_tho_hoa_thinh_khong.jpg', 6, 6, 0),
('BK021', 'Trăm Năm Cô Đơn (One Hundred Years of Solitude)', 'Gabriel García Márquez', 'Tiểu thuyết hiện đại', 8, 'Khu T - Kệ 02', '978-604-56-4567-8', 185000, 1967, 'NXB Văn Học', 'Available', '/com/vithay/libman/images/tram_nam_co_don.jpg', 6, 6, 0),
('BK022', 'Rừng Na Uy (Norwegian Wood)', 'Haruki Murakami', 'Tiểu thuyết hiện đại', 8, 'Khu T - Kệ 03', '978-604-56-5678-9', 145000, 1987, 'NXB Hội Nhà Văn', 'Available', '/com/vithay/libman/images/rung_na_uy.jpg', 8, 8, 0),

-- Ngoại ngữ & Từ điển
('BK023', 'English Grammar in Use (5th Edition)', 'Raymond Murphy', 'Ngoại ngữ & Từ điển', 10, 'Khu N - Kệ 01', '978-110-84-5765-1', 260000, 2019, 'Cambridge University Press', 'Available', '/com/vithay/libman/images/english_grammar.jpg', 10, 10, 0),
('BK024', 'Oxford Advanced Learner''s Dictionary (10th Edition)', 'AS Hornby', 'Ngoại ngữ & Từ điển', 10, 'Khu N - Kệ 02', '978-019-47-9848-8', 580000, 2020, 'Oxford University Press', 'Available', '/com/vithay/libman/images/oxford_dict.jpg', 5, 5, 0),
('BK025', 'Minna no Nihongo I (Sách Giáo Trình Tiếng Nhật)', '3A Corporation', 'Ngoại ngữ & Từ điển', 10, 'Khu N - Kệ 03', '978-488-31-9603-6', 135000, 2018, 'NXB Trẻ', 'Available', '/com/vithay/libman/images/minna_nihongo.jpg', 8, 8, 0),
('BK026', 'Giải Tích 1 & 2 Dành Cho Kỹ Sư', 'Nguyễn Đình Trí', 'Toán học & Khoa học tự nhiên', 3, 'Khu C - Kệ 02', '978-604-90-6677-6', 120000, 2021, 'NXB Giáo Dục', 'Available', '/com/vithay/libman/images/giai_tich.jpg', 12, 12, 0),

-- Truyện tranh & Sách thiếu nhi (Category 11)
('BK027', 'Doraemon - Tuyển Tập Tranh Truyện Màu', 'Fujiko F. Fujio', 'Truyện tranh & Sách thiếu nhi', 11, 'Khu TN - Kệ 01', '978-604-2-22441-1', 45000, 1970, 'NXB Kim Đồng', 'Available', '/com/vithay/libman/images/doraemon.jpg', 15, 14, 0),
('BK028', 'Thám Tử Lừng Danh Conan', 'Gosho Aoyama', 'Truyện tranh & Sách thiếu nhi', 11, 'Khu TN - Kệ 01', '978-604-2-18542-2', 35000, 1994, 'NXB Kim Đồng', 'Available', '/com/vithay/libman/images/conan.jpg', 20, 19, 0),
('BK029', 'Dế Mèn Phiêu Lưu Ký', 'Tô Hoài', 'Truyện tranh & Sách thiếu nhi', 11, 'Khu TN - Kệ 02', '978-604-2-08533-3', 60000, 1941, 'NXB Kim Đồng', 'Available', '/com/vithay/libman/images/de_men.jpg', 12, 12, 0),
('BK030', 'Kính Vạn Hoa', 'Nguyễn Nhật Ánh', 'Truyện tranh & Sách thiếu nhi', 11, 'Khu TN - Kệ 02', '978-604-2-09414-4', 85000, 1995, 'NXB Kim Đồng', 'Available', '/com/vithay/libman/images/kinh_van_hoa.jpg', 10, 10, 0),
('BK031', 'Hoàng Tử Bé (Le Petit Prince)', 'Antoine de Saint-Exupéry', 'Truyện tranh & Sách thiếu nhi', 11, 'Khu TN - Kệ 03', '978-604-56-7890-9', 70000, 1943, 'NXB Hội Nhà Văn', 'Available', '/com/vithay/libman/images/hoang_tu_be.jpg', 8, 8, 0),
('BK032', 'Harry Potter và Hòn Đá Phù Thủy', 'J.K. Rowling', 'Truyện tranh & Sách thiếu nhi', 11, 'Khu TN - Kệ 03', '978-604-1-08123-5', 160000, 1997, 'NXB Trẻ', 'Available', '/com/vithay/libman/images/harry_potter.jpg', 10, 9, 0),
('BK033', 'Totto-chan Bên Cửa Sổ', 'Tetsuko Kuroyanagi', 'Truyện tranh & Sách thiếu nhi', 11, 'Khu TN - Kệ 04', '978-604-56-3245-7', 98000, 1981, 'NXB Văn Học', 'Available', '/com/vithay/libman/images/totto_chan.jpg', 8, 8, 0),
('BK034', 'Đảo Giấu Vàng (Treasure Island)', 'Robert Louis Stevenson', 'Truyện tranh & Sách thiếu nhi', 11, 'Khu TN - Kệ 04', '978-604-56-6543-2', 88000, 1883, 'NXB Văn Học', 'Available', '/com/vithay/libman/images/dao_giau_vang.jpg', 6, 6, 0),
('BK035', 'Không Gia Đình (Sans Famille)', 'Hector Malot', 'Truyện tranh & Sách thiếu nhi', 11, 'Khu TN - Kệ 05', '978-604-56-8877-1', 135000, 1878, 'NXB Văn Học', 'Available', '/com/vithay/libman/images/khong_gia_dinh.jpg', 7, 7, 0),
('BK036', 'Góc Sân Và Khoảng Trời', 'Trần Đăng Khoa', 'Truyện tranh & Sách thiếu nhi', 11, 'Khu TN - Kệ 05', '978-604-2-07654-6', 55000, 1968, 'NXB Kim Đồng', 'Available', '/com/vithay/libman/images/goc_san_khoang_troi.jpg', 9, 9, 0),

-- Sách Bổ Sung Cùng Tác Giả (Yuval Noah Harari, Robert C. Martin, Nguyễn Nhật Ánh, J.K. Rowling)
('BK037', 'Homo Deus: Lược Sử Tương Lai', 'Yuval Noah Harari', 'Lịch sử & Địa lý', 4, 'Khu L - Kệ 01', '978-604-56-4276-4', 195000, 2016, 'NXB Thế Giới', 'Available', '/com/vithay/libman/images/homo_deus.jpg', 6, 6, 0),
('BK038', '21 Bài Học Cho Thế Kỷ 21', 'Yuval Noah Harari', 'Lịch sử & Địa lý', 4, 'Khu L - Kệ 01', '978-604-56-4277-1', 175000, 2018, 'NXB Thế Giới', 'Available', '/com/vithay/libman/images/21_bai_hoc.jpg', 5, 5, 0),
('BK039', 'Clean Architecture: Kiến Trúc Sạch', 'Robert C. Martin', 'Công nghệ thông tin', 9, 'Khu IT - Kệ 01', '978-013-44-9416-6', 350000, 2017, 'Prentice Hall', 'Available', '/com/vithay/libman/images/clean_architecture.jpg', 6, 6, 0),
('BK040', 'The Clean Coder: Cẩm Nang Cho Lập Trình Viên', 'Robert C. Martin', 'Công nghệ thông tin', 9, 'Khu IT - Kệ 01', '978-013-70-8107-3', 280000, 2011, 'Prentice Hall', 'Available', '/com/vithay/libman/images/clean_coder.jpg', 5, 5, 0),
('BK041', 'Cho Tôi Xin Một Vé Đi Tuổi Thơ', 'Nguyễn Nhật Ánh', 'Truyện tranh & Sách thiếu nhi', 11, 'Khu TN - Kệ 02', '978-604-2-09415-1', 85000, 2008, 'NXB Trẻ', 'Available', '/com/vithay/libman/images/cho_toi_xin_mot_ve_di_tuoi_tho.jpg', 10, 10, 0),
('BK042', 'Tôi Thấy Hoa Vàng Trên Cỏ Xanh', 'Nguyễn Nhật Ánh', 'Truyện tranh & Sách thiếu nhi', 11, 'Khu TN - Kệ 02', '978-604-2-09416-8', 95000, 2010, 'NXB Trẻ', 'Available', '/com/vithay/libman/images/toi_thay_hoa_vang_tren_co_xanh.jpg', 8, 8, 0),
('BK043', 'Harry Potter và Phòng Chứa Bí Mật', 'J.K. Rowling', 'Truyện tranh & Sách thiếu nhi', 11, 'Khu TN - Kệ 03', '978-604-1-08124-2', 170000, 1998, 'NXB Trẻ', 'Available', '/com/vithay/libman/images/harry_potter_2.jpg', 8, 8, 0),
('BK044', 'Harry Potter và Tên Tù Nhân Ngục Azkaban', 'J.K. Rowling', 'Truyện tranh & Sách thiếu nhi', 11, 'Khu TN - Kệ 03', '978-604-1-08125-9', 185000, 1999, 'NXB Trẻ', 'Available', '/com/vithay/libman/images/harry_potter_3.jpg', 7, 7, 0);

-- Seed Transactions
INSERT OR IGNORE INTO borrow_transactions (id, reader_id, reader_name, book_id, book_title, borrow_date, due_date, return_date, borrow_type, status, fine_amount, notes)
VALUES
('211200001', 'RD001', 'Lê Văn An', 'B001', 'Sapiens: Lược Sử Loài Người', '2024-03-01', '2024-03-15', NULL, 'Mang về nhà', 'Đang Mượn', 0.0, 'Độc giả mượn tại quầy số 1'),
('211200002', 'RD001', 'Lê Văn An', 'B002', 'Dune: Xứ Cát', '2024-03-02', '2024-03-16', NULL, 'Mang về nhà', 'Đang Mượn', 0.0, 'Bản bìa cứng có minh họa'),
('211200003', 'RD001', 'Lê Văn An', 'B004', 'Atomic Habits: Thay Đổi Tí Hon', '2024-03-05', '2024-03-19', NULL, 'Mang về nhà', 'Đang Mượn', 0.0, 'Gia hạn trực tuyến'),
('211200004', 'RD001', 'Lê Văn An', 'B005', '1984', '2024-03-10', '2024-03-24', NULL, 'Mượn đọc tại chỗ', 'Đang Mượn', 0.0, 'Đọc tại phòng chuyên khảo');

-- Default Library Operational Settings
INSERT OR IGNORE INTO system_settings (setting_key, setting_value, description)
VALUES
('max_books_per_reader', '5', 'Số sách mượn tối đa cho một độc giả tại cùng thời điểm'),
('max_borrow_days_home', '14', 'Số ngày mượn tối đa khi mượn mang về nhà'),
('max_borrow_days_onsite', '1', 'Số ngày mượn tối đa khi mượn đọc tại chỗ'),
('fine_per_day', '2000', 'Tiền phạt quá hạn mỗi ngày trên mỗi cuốn sách (VNĐ)'),
('lost_book_multiplier', '2.0', 'Hệ số đền bù theo giá sách khi sách bị mất hoặc hư hỏng'),
('processing_fee', '20000', 'Lệ phí xử lý kỹ thuật cho sách mất hoặc hỏng nặng (VNĐ)'),
('card_validity_months', '12', 'Thời hạn hiệu lực mặc định của thẻ độc giả (tháng)'),
('app_theme', 'DARK', 'Giao diện ứng dụng: DARK hoặc PINK_LIGHT');

