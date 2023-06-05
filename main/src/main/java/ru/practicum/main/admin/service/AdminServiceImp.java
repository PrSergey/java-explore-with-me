package ru.practicum.main.admin.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.category.dto.CategoryDto;
import ru.practicum.main.category.dto.CategoryMapper;
import ru.practicum.main.category.model.Category;
import ru.practicum.main.category.repository.CategoryRepository;
import ru.practicum.main.excepsion.ExistenceException;
import ru.practicum.main.user.dto.UserDto;
import ru.practicum.main.user.dto.UserMapper;
import ru.practicum.main.user.model.User;
import ru.practicum.main.user.storage.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class AdminServiceImp implements AdminService {

    UserRepository userRepository;
    CategoryRepository categoryRepository;

    @Transactional
    @Override
    public UserDto saveUser(UserDto userDto) {
        User user = UserMapper.fromUserDto(userDto);
        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public List<UserDto> getUsers(List<Long> usersId, PageRequest pageRequest) {
        return userRepository.findAllByIdIn(usersId, pageRequest)
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ExistenceException("Пользвателя с id=" + id + " не найден в базе.");
        }
        userRepository.deleteById(id);
    }

    @Override
    public CategoryDto saveCategory(CategoryDto categoryDto) {
        Category category = CategoryMapper.fromCategoryDto(categoryDto);
        return CategoryMapper.toCategoryDto(categoryRepository.save(category));
    }

    @Override
    public CategoryDto updateCategory(Integer categoryId, CategoryDto categoryDto) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ExistenceException("Ктаегория под id = " + categoryId + " не найдена в базе"));
        category.setName(categoryDto.getName());
        return CategoryMapper.toCategoryDto(categoryRepository.save(category));
    }

    @Override
    public void deleteCategory(Integer categoryId) {
        if(!categoryRepository.existsById(categoryId)) {
            throw new ExistenceException("Ктаегория под id = " + categoryId + " не найдена в базе");
        }
        categoryRepository.deleteById(categoryId);
    }

}
