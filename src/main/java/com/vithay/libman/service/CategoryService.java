package com.vithay.libman.service;

import com.vithay.libman.dao.CategoryDao;
import com.vithay.libman.model.Category;

import java.util.List;

public class CategoryService {
    private final CategoryDao categoryDao = new CategoryDao();

    public List<Category> getAllCategories() {
        return categoryDao.getAllCategories();
    }

    public Category getCategoryById(int id) {
        return categoryDao.getCategoryById(id);
    }

    public boolean saveCategory(Category category) {
        if (category.getName() == null || category.getName().trim().isEmpty()) {
            return false;
        }
        if (category.getId() <= 0) {
            return categoryDao.addCategory(category);
        } else {
            return categoryDao.updateCategory(category);
        }
    }

    public boolean deleteCategory(int id) {
        return categoryDao.deleteCategory(id);
    }
}
