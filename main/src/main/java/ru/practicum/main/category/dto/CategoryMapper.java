package ru.practicum.main.category.dto;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.practicum.main.category.model.Category;

@Mapper
public interface CategoryMapper {

    CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);

    Category fromCategoryDto(CategoryDto categoryDto);

    CategoryDto toCategoryDto(Category category);

}
