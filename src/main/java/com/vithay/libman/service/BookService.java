package com.vithay.libman.service;

import com.vithay.libman.dao.BookDao;
import com.vithay.libman.model.Book;

import java.util.List;

public class BookService {
    private final BookDao bookDao = new BookDao();

    public List<Book> getAllBooks() {
        return bookDao.getAllBooks();
    }

    public List<Book> getDeletedBooks() {
        return bookDao.getDeletedBooks();
    }

    public Book getBookById(String id) {
        return bookDao.getBookById(id);
    }

    public List<Book> searchBooks(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllBooks();
        }
        return bookDao.searchBooks(keyword.trim());
    }

    public boolean saveBook(Book book) {
        if (book.getId() == null || book.getId().trim().isEmpty()) {
            return false;
        }
        Book existing = bookDao.getBookById(book.getId());
        if (existing == null) {
            return bookDao.addBook(book);
        } else {
            return bookDao.updateBook(book);
        }
    }

    public boolean softDeleteBook(String id) {
        return bookDao.softDeleteBook(id);
    }

    public boolean restoreBook(String id) {
        return bookDao.restoreBook(id);
    }

    public boolean permanentDeleteBook(String id) {
        return bookDao.permanentDeleteBook(id);
    }
}
