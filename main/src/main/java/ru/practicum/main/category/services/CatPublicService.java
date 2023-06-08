package ru.practicum.main.category.services;

import ru.practicum.main.category.dto.CategoryDto;

import java.util.List;

public interface CatPublicService {

    List<CategoryDto> getAllCategories(int from, int size);

    CategoryDto getCategoryByID(long catId);

}
