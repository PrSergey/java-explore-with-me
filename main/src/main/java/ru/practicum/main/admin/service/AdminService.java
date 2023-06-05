package ru.practicum.main.admin.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.main.category.dto.CategoryDto;
import ru.practicum.main.user.dto.UserDto;

import java.util.List;

public interface AdminService {

    UserDto saveUser(UserDto user);

    List<UserDto> getUsers(List<Long> usersId, PageRequest pageRequest);

    void deleteUser(Long id);

    CategoryDto saveCategory(CategoryDto categoryDto);

    CategoryDto updateCategory(Integer categoryId, CategoryDto categoryDto);

    void deleteCategory(Integer categoryId);


}
