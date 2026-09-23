package com.vithay.libman.service;

import com.vithay.libman.model.Book;
import com.vithay.libman.model.BookReview;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Dịch vụ cung cấp thông tin tóm tắt nội dung sách (Synopsis/Description)
 * và quản lý các luồng thảo luận, đánh giá của độc giả trong thư viện.
 */
public class BookDiscussionService {

    private static BookDiscussionService instance;

    private final Map<String, String> descriptions = new HashMap<>();
    private final Map<String, List<BookReview>> reviewsMap = new ConcurrentHashMap<>();

    public static synchronized BookDiscussionService getInstance() {
        if (instance == null) {
            instance = new BookDiscussionService();
        }
        return instance;
    }

    public BookDiscussionService() {
        initDescriptions();
        initDefaultReviews();
    }

    private void initDescriptions() {
        descriptions.put("B001", "Sapiens: Lược Sử Loài Người đưa độc giả vào chuyến hành trình kỳ vĩ xuyên suốt toàn bộ lịch sử nhân loại, từ những sinh vật vượn người thời tiền sử cho đến kỷ nguyên trí tuệ nhân tạo ngày nay. Yuval Noah Harari giải thích cách Homo sapiens trở thành loài thống trị hành tinh thông qua khả năng sáng tạo và tin vào các câu chuyện hư cấu như tiền tệ, tôn giáo và quốc gia.");
        descriptions.put("BK037", "Homo Deus: Lược Sử Tương Lai tiếp nối thành công vang dội của Sapiens, khám phá các dự án, giấc mơ và cơn ác mộng sẽ định hình thế kỷ 21. Khi nhân loại dần chinh phục được nạn đói, dịch bệnh và chiến tranh, mục tiêu tiếp theo của con người sẽ là gì? Liệu chúng ta có nâng cấp bản thân thành những vị thần (Homo Deus) hay sẽ trở thành nạn nhân của thuật toán dữ liệu?");
        descriptions.put("BK038", "21 Bài Học Cho Thế Kỷ 21 là tác phẩm phân tích sâu sắc các vấn đề thời sự cấp bách nhất hiện nay: tin giả, khủng hoảng dân chủ, chiến tranh hạt nhân, biến đổi khí hậu và sự bùng nổ của trí tuệ nhân tạo. Tác phẩm thúc đẩy mỗi cá nhân rèn luyện tư duy phản biện và khả năng thích ứng trong một thế giới biến động không ngừng.");
        descriptions.put("BK010", "Clean Code: Mã Sạch của Robert C. Martin (Uncle Bob) là kim chỉ nam kinh điển cho mọi kỹ sư phần mềm. Cuốn sách cung cấp các nguyên tắc thực chiến về cách đặt tên biến, cấu trúc hàm, thiết kế lớp, xử lý lỗi và tái cấu trúc mã nguồn để tạo ra phần mềm dễ đọc, dễ bảo trì và trường tồn với thời gian.");
        descriptions.put("BK039", "Clean Architecture: Kiến Trúc Sạch trình bày những quy tắc nền tảng trong kiến trúc phần mềm, từ nguyên lý SOLID, phân chia ranh giới các tầng (layers), quy tắc phụ thuộc (Dependency Rule) cho đến cách xây dựng các hệ thống độc lập với framework, cơ sở dữ liệu và giao diện người dùng.");
        descriptions.put("BK040", "The Clean Coder: Cẩm Nang Cho Lập Trình Viên Chuyên Nghiệp phác họa chân dung một lập trình viên đích thực: từ tinh thần trách nhiệm, kỷ luật làm việc, kỹ năng ước lượng thời gian, cách nói 'Không' khi cần thiết và tinh thần cống hiến cho chất lượng sản phẩm.");
        descriptions.put("BK030", "Kính Vạn Hoa là bộ truyện dài thiếu nhi bất hủ của nhà văn Nguyễn Nhật Ánh, xoay quanh ba cô cậu học trò Quý ròm, Tiểu Long và nhỏ Hạnh với muôn vàn trò nghịch ngợm, phiêu lưu kỳ thú và những bài học ấm áp về tình bạn bè, thầy cô.");
        descriptions.put("BK041", "Cho Tôi Xin Một Vé Đi Tuổi Thơ là một trong những tác phẩm thành công nhất của Nguyễn Nhật Ánh, đưa người đọc quay ngược thời gian về miền ký ức tuổi thơ trong trẻo, hồn nhiên với những suy nghĩ ngộ nghĩnh của cu Mùi, Hải cò, con Tủn và con Tí sún.");
        descriptions.put("BK042", "Tôi Thấy Hoa Vàng Trên Cỏ Xanh là bức tranh đồng quê Việt Nam êm đềm với tuổi thơ lam lũ của hai anh em Thiều và Quang. Tác phẩm chạm đến trái tim hàng triệu độc giả bởi những xúc cảm rung động đầu đời, tình anh em gắn bó và lòng vị tha cao cả.");
        descriptions.put("BK032", "Harry Potter và Hòn Đá Phù Thủy mở cánh cổng diệu kỳ đưa cậu bé mồ côi Harry Potter đến với Trường Pháp thuật Hogwarts. Tại đây, cậu khám phá ra thân thế thực sự của mình, kết bạn với Ron và Hermione, và bắt đầu cuộc chiến chống lại Chúa tể Hắc ám Voldemort.");
        descriptions.put("BK043", "Harry Potter và Phòng Chứa Bí Mật tiếp tục năm học thứ hai đầy kịch tính tại Hogwarts, khi một căn phòng cổ xưa bí mật bị mở ra, giải phóng con quái vật đe dọa biến toàn bộ học sinh mang dòng máu Muggle thành đá.");
        descriptions.put("BK044", "Harry Potter và Tên Tù Nhân Ngục Azkaban khắc họa những nỗi sợ hãi tâm lý trưởng thành, sự xuất hiện của Giám Ngục Azkaban và những bí mật cảm động đằng sau tình bạn của Hội Đạo Tặc cùng người cha đã khuất của Harry.");
        descriptions.put("B002", "Dune: Xứ Cát của Frank Herbert là tượng đài khoa học viễn tưởng thế giới. Tác phẩm kể về hành trình của Paul Atreides trên hành tinh sa mạc cằn cỗi Arrakis - nơi duy nhất sở hữu Hương Dược (Melange), thứ tài nguyên quý giá nhất vũ trụ.");
        descriptions.put("B004", "Atomic Habits: Thay Đổi Tí Hon Hiệu Quả Bất Ngờ của James Clear cung cấp phương pháp luận khoa học về cơ chế hình thành thói quen: Nhận biết, Thèm muốn, Phản hồi và Phần thưởng. Cuốn sách giúp bạn cải thiện bản thân 1% mỗi ngày.");
        descriptions.put("BK019", "Đắc Nhân Tâm của Dale Carnegie là cuốn sách phát triển bản thân nổi tiếng nhất mọi thời đại, hướng dẫn nghệ thuật giao tiếp, thu phục lòng người và xây dựng các mối quan hệ chân thành, bền vững.");
    }

    private void initDefaultReviews() {
        // Sapiens
        addPredefinedReview("B001", "Trần Văn An", 5, "2026-09-15",
                "Một tác phẩm kinh điển thay đổi hoàn toàn cách tôi nhìn nhận về lịch sử của loài người. Lối viết lôi cuốn, các luận điểm về trật tự tưởng tượng cực kỳ thuyết phục!");
        addPredefinedReview("B001", "Lê Thị Thu Nhàn", 5, "2026-09-18",
                "Rất đáng đọc! Cách Yuval Harari liên kết giữa sinh học, nhân chủng học và kinh tế học giúp chúng ta hiểu rõ cội nguồn của các thể chế xã hội hiện đại.");

        // Homo Deus
        addPredefinedReview("BK037", "Nguyễn Văn Nhân", 5, "2026-09-10",
                "Một góc nhìn tương lai vừa quyến rũ vừa đáng suy ngẫm. Cuốn sách giúp người đọc cảnh giác trước sự phụ thuộc quá mức vào các thuật toán AI.");

        // Clean Code
        addPredefinedReview("BK010", "Phùng Tuấn Kiệt", 5, "2026-09-12",
                "Cuốn sách gối đầu giường của mọi lập trình viên. Đọc xong viết code sạch, có trách nhiệm hơn hẳn. Đặt tên biến và tách hàm theo đúng chuẩn Uncle Bob.");
        addPredefinedReview("BK010", "Vũ Đức Thịnh", 4, "2026-09-16",
                "Nhiều nguyên tắc rất thực chiến. Dù một số ví dụ viết bằng Java phiên bản cũ nhưng tư duy kiến trúc và clean code thì luôn bất biến.");

        // Clean Architecture
        addPredefinedReview("BK039", "Lê Văn An", 5, "2026-09-14",
                "Kiến trúc phân tầng và nguyên tắc Dependency Rule được giải thích rất sâu sắc. Đọc xong áp dụng ngay vào dự án thực tế.");

        // Cho Tôi Xin Một Vé Đi Tuổi Thơ
        addPredefinedReview("BK041", "Hoàng Minh Châu", 5, "2026-09-19",
                "Giọng văn dí dỏm, trong sáng đến nghẹn ngào. Đọc từng trang sách mà như thấy lại chính tuổi thơ của mình với bao nhiêu trò chơi ngày bé.");

        // Tôi Thấy Hoa Vàng Trên Cỏ Xanh
        addPredefinedReview("BK042", "Trần Thị Mai Anh", 5, "2026-09-20",
                "Cảm xúc rất dịu dàng và lắng đọng. Tình anh em của Thiều và Quang vừa chân thực vừa cảm động sâu sắc.");

        // Harry Potter 1
        addPredefinedReview("BK032", "Nguyễn Minh Trí", 5, "2026-09-08",
                "Tác phẩm mở đầu cho thế giới phù thủy tuyệt diệu. Đọc đi đọc lại nhiều lần vẫn thấy vẹn nguyên sự háo hức và mê hoặc.");
    }

    private void addPredefinedReview(String bookId, String readerName, int rating, String date, String content) {
        String id = "REV_" + UUID.randomUUID().toString().substring(0, 8);
        BookReview rev = new BookReview(id, bookId, readerName, "/com/vithay/libman/images/avatar.png", rating, date, content);
        reviewsMap.computeIfAbsent(bookId, k -> new CopyOnWriteArrayList<>()).add(rev);
    }

    public String getBookDescription(Book book) {
        if (book == null) return "Không có thông tin mô tả cuốn sách.";
        if (descriptions.containsKey(book.getId())) {
            return descriptions.get(book.getId());
        }
        // Fallback description
        return String.format(
                "Tác phẩm '%s' do tác giả %s sáng tác, thuộc danh mục %s. Cuốn sách được phát hành bởi %s (%d), " +
                "hiện đang được lưu trữ và phục vụ bạn đọc tại vị trí %s trong kho sách LibMan. " +
                "Ấn phẩm cung cấp nguồn tài liệu học thuật và văn hóa giá trị cao, được đông đảo độc giả, học sinh - sinh viên và các nhà nghiên cứu đón nhận nồng nhiệt.",
                book.getTitle(),
                book.getAuthor() != null ? book.getAuthor() : "Tác giả nổi tiếng",
                book.getCategory() != null ? book.getCategory() : "Tổng hợp",
                book.getPublisher() != null ? book.getPublisher() : "Thư viện",
                book.getPublishYear() > 0 ? book.getPublishYear() : 2024,
                book.getShelfLocation() != null ? book.getShelfLocation() : "Khu tổng hợp"
        );
    }

    public List<BookReview> getReviewsForBook(String bookId) {
        if (bookId == null) return Collections.emptyList();
        List<BookReview> list = reviewsMap.get(bookId);
        if (list == null || list.isEmpty()) {
            // Default 2 welcoming reviews if none exist
            List<BookReview> generated = new CopyOnWriteArrayList<>();
            generated.add(new BookReview(
                    "REV_" + UUID.randomUUID().toString().substring(0, 8),
                    bookId,
                    "Độc Giả Thư Viện",
                    "/com/vithay/libman/images/avatar.png",
                    5,
                    LocalDate.now().minusDays(3).format(DateTimeFormatter.ISO_LOCAL_DATE),
                    "Cuốn sách rất hay và bổ ích, nội dung súc tích, trình bày rõ ràng. Rất khuyên mọi người nên đọc thử!"
            ));
            generated.add(new BookReview(
                    "REV_" + UUID.randomUUID().toString().substring(0, 8),
                    bookId,
                    "Ban Bạn Đọc",
                    "/com/vithay/libman/images/avatar.png",
                    4,
                    LocalDate.now().minusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE),
                    "Tài liệu tham khảo xuất sắc, giấy in đẹp và bản dịch mượt mà. Sách luôn sẵn sàng tại kho."
            ));
            reviewsMap.put(bookId, generated);
            return generated;
        }
        return Collections.unmodifiableList(list);
    }

    public void addReview(BookReview review) {
        if (review == null || review.getBookId() == null) return;
        reviewsMap.computeIfAbsent(review.getBookId(), k -> new CopyOnWriteArrayList<>()).add(0, review);
    }
}
