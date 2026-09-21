package com.vithay.libman.service;

import com.vithay.libman.dao.ReaderDao;
import com.vithay.libman.dao.UserDao;
import com.vithay.libman.model.Reader;
import com.vithay.libman.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;

public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private static AuthService instance;

    private final UserDao userDao = new UserDao();
    private final ReaderDao readerDao = new ReaderDao();
    private User currentUser = null; // Mặc định chưa đăng nhập

    private AuthService() {
    }

    public static synchronized AuthService getInstance() {
        if (instance == null) {
            instance = new AuthService();
        }
        return instance;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public boolean login(String username, String password) {
        User user = userDao.authenticate(username, password);
        if (user != null) {
            this.currentUser = user;
            logger.info("User logged in successfully: {} (Role: {})", user.getUsername(), user.getRole());
            return true;
        }
        return false;
    }

    public boolean register(User newUser) {
        if (newUser.getUsername() == null || newUser.getUsername().trim().isEmpty() ||
            newUser.getPassword() == null || newUser.getPassword().trim().isEmpty()) {
            return false;
        }
        User existing = userDao.getUserByUsername(newUser.getUsername().trim());
        if (existing != null) {
            return false; // username already exists
        }
        boolean registered = userDao.registerUser(newUser);
        if (registered) {
            this.currentUser = newUser;

            // Nếu người dùng đăng ký vai trò Độc Giả, tự động khởi tạo hồ sơ Chờ Cấp Thẻ tại quầy thư viện
            if ("Độc Giả".equalsIgnoreCase(newUser.getRole())) {
                String readerId = "DG" + System.currentTimeMillis() % 1000000;
                Reader reader = new Reader(
                        readerId,
                        newUser.getFullName(),
                        newUser.getEmail(),
                        newUser.getPhone(),
                        "Đăng ký trực tuyến",
                        "Chưa đối chiếu",
                        "2000-01-01",
                        LocalDate.now().toString(),
                        null,
                        null,
                        "Chờ Cấp Thẻ",
                        false
                );
                readerDao.addReader(reader);
            }
        }
        return registered;
    }

    public void logout() {
        this.currentUser = null;
    }

    // 3 Cấp phân quyền chuẩn hóa: Quản Trị, Thủ Thư, Độc Giả
    public boolean isAdmin() {
        return currentUser != null && "Quản Trị".equalsIgnoreCase(currentUser.getRole());
    }

    public boolean isLibrarian() {
        return currentUser != null && ("Thủ Thư".equalsIgnoreCase(currentUser.getRole()) || isAdmin());
    }

    public boolean isReader() {
        return currentUser != null && "Độc Giả".equalsIgnoreCase(currentUser.getRole());
    }

    public boolean canManageBooks() {
        return isLibrarian();
    }

    public boolean canManageReaders() {
        return isLibrarian();
    }

    public boolean canBorrowReturn() {
        return isLibrarian();
    }

    public boolean canConfigureSettings() {
        return isAdmin();
    }

    public boolean canAccessRecycleBin() {
        return isLibrarian();
    }

    public boolean updateProfile(String fullName, String email, String phone) {
        if (currentUser == null) return false;
        boolean updated = userDao.updateUserProfile(currentUser.getId(), fullName, email, phone);
        if (updated) {
            currentUser.setFullName(fullName);
            currentUser.setEmail(email);
            currentUser.setPhone(phone);
        }
        return updated;
    }

    public boolean changePassword(String oldPassword, String newPassword) {
        if (currentUser == null) return false;
        if (!currentUser.getPassword().equals(oldPassword)) {
            return false;
        }
        boolean updated = userDao.updatePassword(currentUser.getId(), newPassword);
        if (updated) {
            currentUser.setPassword(newPassword);
        }
        return updated;
    }
}
