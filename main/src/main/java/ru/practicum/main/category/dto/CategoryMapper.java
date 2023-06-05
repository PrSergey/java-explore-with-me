package ru.practicum.main.category.dto;

import ru.practicum.main.category.model.Category;

public class CategoryMapper {

    public static Category fromCategoryDto(CategoryDto categoryDto) {
        return Category.builder()
                .id(categoryDto.getId())
                .name(categoryDto.getName())
                .build();
    }

    public static CategoryDto toCategoryDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

}
