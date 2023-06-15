package ru.practicum.main.category.services;

import ru.practicum.main.category.dto.CategoryDto;

import java.util.List;

public interface CatService {

    CategoryDto saveCategory(CategoryDto categoryDto);

    CategoryDto updateCategory(long categoryId, CategoryDto categoryDto);

    void deleteCategory(long categoryId);

    List<CategoryDto> getAllCategories(int from, int size);

    CategoryDto getCategoryByID(long catId);

}
