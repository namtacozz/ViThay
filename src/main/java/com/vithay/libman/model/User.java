package com.vithay.libman.model;

public class User {
    private int id;
    private String username;
    private String password;
    private String fullName;
    private String role; // 'Giám đốc', 'Thủ thư', 'Độc giả'
    private String email;
    private String phone;
    private String avatar;
    private String createdDate;
    private String expiryDate;

    public User() {
    }

    public User(int id, String username, String password, String fullName, String role,
                String email, String phone, String avatar, String createdDate, String expiryDate) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.role = role;
        this.email = email;
        this.phone = phone;
        this.avatar = avatar;
        this.createdDate = createdDate;
        this.expiryDate = expiryDate;
    }

    public User(int id, String username, String password, String fullName, String role, String avatar) {
        this(id, username, password, fullName, role, null, null, avatar, "2024-01-01", "2030-12-31");
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }

    public String getCreatedDate() { return createdDate; }
    public void setCreatedDate(String createdDate) { this.createdDate = createdDate; }

    public String getExpiryDate() { return expiryDate; }
    public void setExpiryDate(String expiryDate) { this.expiryDate = expiryDate; }
}
