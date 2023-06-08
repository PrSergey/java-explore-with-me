package ru.practicum.main.category.services;

import ru.practicum.main.category.dto.CategoryDto;

public interface CatAdminService {

    CategoryDto saveCategory(CategoryDto categoryDto);

    CategoryDto updateCategory(long categoryId, CategoryDto categoryDto);

    void deleteCategory(long categoryId);

}
