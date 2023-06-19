package ru.practicum.main.category.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.category.dto.CategoryDto;
import ru.practicum.main.category.dto.CategoryMapper;
import ru.practicum.main.category.model.Category;
import ru.practicum.main.category.services.CatService;
import ru.practicum.main.category.srorage.CategoryRepository;
import ru.practicum.main.event.storage.EventRepository;
import ru.practicum.main.excepsion.ExistenceException;
import ru.practicum.main.excepsion.ValidationException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CatServiceImpl implements CatService {

    private final CategoryMapper categoryMapper;
    private final CategoryRepository categoryRepository;

    private final EventRepository eventRepository;

    @Transactional
    @Override
    public CategoryDto saveCategory(CategoryDto categoryDto) {
        checkNameCat(categoryDto.getName());
        Category category = categoryMapper.fromCategoryDto(categoryDto);
        return categoryMapper.toCategoryDto(categoryRepository.save(category));
    }

    @Transactional
    @Override
    public CategoryDto updateCategory(long catId, CategoryDto categoryDto) {
        if (categoryRepository.existsAllByName(categoryDto.getName())) {
            Category catStorage = categoryRepository.findAllByName(categoryDto.getName()).get(0);
            if (catStorage.getId() != catId) {
                throw new ValidationException("A category with the name " + catStorage.getName() + " already exists");
            }
        }
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new ExistenceException("Category with id=" + catId + " was not found."));
        category.setName(categoryDto.getName());
        return categoryMapper.toCategoryDto(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public void deleteCategory(long catId) {
        if (!categoryRepository.existsById(catId)) {
            throw new ExistenceException("Category with id=" + catId + " was not found.");
        }
        if (eventRepository.existsAllByCategory_Id(catId)) {
            throw new ValidationException("You cannot delete a category with a linked event.");
        }
        categoryRepository.deleteById(catId);
    }

    private void checkNameCat(String name) {
        if (categoryRepository.existsAllByName(name)) {
            throw new ValidationException("A category with the name " + name + " already exists");
        }
    }

    @Override
    public List<CategoryDto> getAllCategories(int from, int size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        return categoryRepository.findAll(pageRequest)
                .stream()
                .map(categoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto getCategoryByID(long catId) {
        return categoryMapper.toCategoryDto(categoryRepository.findById(catId)
                .orElseThrow(() -> new ExistenceException("Category with id=" + catId + " was not found")));
    }

}











