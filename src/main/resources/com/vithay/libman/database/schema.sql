CREATE TABLE IF NOT EXISTS users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT UNIQUE NOT NULL,
    password TEXT NOT NULL,
    full_name TEXT NOT NULL,
    role TEXT NOT NULL DEFAULT 'Thủ thư', -- 'Giám đốc', 'Thủ thư', 'Độc giả'
    email TEXT,
    phone TEXT,
    avatar TEXT,
    created_date TEXT,
    expiry_date TEXT
);

CREATE TABLE IF NOT EXISTS categories (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT UNIQUE NOT NULL,
    description TEXT
);

CREATE TABLE IF NOT EXISTS books (
    id TEXT PRIMARY KEY,
    title TEXT NOT NULL,
    author TEXT NOT NULL,
    category TEXT NOT NULL,
    category_id INTEGER,
    shelf_location TEXT DEFAULT 'Kệ A1-01',
    isbn TEXT,
    price REAL DEFAULT 120000,
    publish_year INTEGER,
    publisher TEXT,
    status TEXT NOT NULL DEFAULT 'Available',
    cover_image TEXT,
    total_copies INTEGER DEFAULT 5,
    available_copies INTEGER DEFAULT 5,
    is_deleted INTEGER DEFAULT 0
);

CREATE TABLE IF NOT EXISTS readers (
    id TEXT PRIMARY KEY,
    full_name TEXT NOT NULL,
    email TEXT,
    phone TEXT,
    address TEXT,
    id_card TEXT,
    birth_date TEXT,
    join_date TEXT,
    card_issue_date TEXT,
    card_expiry_date TEXT,
    status TEXT NOT NULL DEFAULT 'Active', -- 'Active', 'Blocked', 'Expired'
    is_deleted INTEGER DEFAULT 0
);

CREATE TABLE IF NOT EXISTS borrow_transactions (
    id TEXT PRIMARY KEY,
    reader_id TEXT NOT NULL,
    reader_name TEXT NOT NULL,
    book_id TEXT NOT NULL,
    book_title TEXT NOT NULL,
    borrow_date TEXT NOT NULL,
    due_date TEXT NOT NULL,
    return_date TEXT,
    borrow_type TEXT NOT NULL DEFAULT 'Mang về nhà', -- 'Mượn đọc tại chỗ', 'Mang về nhà'
    status TEXT NOT NULL DEFAULT 'Đang Mượn', -- 'Đang Mượn', 'Đã Trả', 'Quá Hạn'
    fine_amount REAL DEFAULT 0.0,
    notes TEXT,
    FOREIGN KEY (reader_id) REFERENCES readers(id),
    FOREIGN KEY (book_id) REFERENCES books(id)
);

CREATE TABLE IF NOT EXISTS system_settings (
    setting_key TEXT PRIMARY KEY,
    setting_value TEXT NOT NULL,
    description TEXT
);
