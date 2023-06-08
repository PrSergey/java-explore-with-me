package ru.practicum.main.category.services.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.category.dto.CategoryDto;
import ru.practicum.main.category.dto.CategoryMapper;
import ru.practicum.main.category.model.Category;
import ru.practicum.main.category.services.CatAdminService;
import ru.practicum.main.category.srorage.CategoryRepository;
import ru.practicum.main.excepsion.ExistenceException;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class CatAdminServiceImpl implements CatAdminService {

    CategoryMapper categoryMapper;
    CategoryRepository categoryRepository;

    @Transactional
    @Override
    public CategoryDto saveCategory(CategoryDto categoryDto) {
        Category category = categoryMapper.fromCategoryDto(categoryDto);
        return categoryMapper.toCategoryDto(categoryRepository.save(category));
    }

    @Transactional
    @Override
    public CategoryDto updateCategory(long catId, CategoryDto categoryDto) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new ExistenceException("Category with id=" + catId + " was not found"));
        category.setName(categoryDto.getName());
        return categoryMapper.toCategoryDto(categoryRepository.save(category));
    }

    @Override
    public void deleteCategory(long catId) {
        if(!categoryRepository.existsById(catId)) {
            throw new ExistenceException("Category with id=" + catId + " was not found");
        }

        categoryRepository.deleteById(catId);
    }

}











