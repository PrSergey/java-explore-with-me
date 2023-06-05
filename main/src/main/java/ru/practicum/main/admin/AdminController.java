package ru.practicum.main.admin;


import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.admin.service.AdminService;
import ru.practicum.main.category.dto.CategoryDto;
import ru.practicum.main.user.dto.UserDto;

import java.awt.*;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin")
public class AdminController {

    AdminService adminService;

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto saveUser(@RequestBody UserDto userDto) {
        log.info("POST - запрос на сохранение пользователя в базе. В сервисе admin");
        return adminService.saveUser(userDto);
    }

    @GetMapping("/users")
    public List<UserDto> getUsers(@RequestParam(name = "ids", required = false) List<Long> usersId,
                           @RequestParam(name = "from", defaultValue = "0") int from,
                           @RequestParam(name = "size", defaultValue = "10") int size) {
        log.info("GET - запрос на получение пользователей с id = {}", usersId);
        PageRequest pageRequest = PageRequest.of(from / size, size);
        return adminService.getUsers(usersId, pageRequest);
    }

    @DeleteMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long userId) {
        log.info("DELETE - запрос на удаление пользователя с id = {}", userId);
        adminService.deleteUser(userId);
    }

    @PostMapping("/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto saveCategory(@RequestBody CategoryDto categoryDto) {
        log.info("POST - запрос на сохранение категории мероприятия в базу.");
        return adminService.saveCategory(categoryDto);
    }

    @PatchMapping("categories/{catId}")
    public CategoryDto updateCategory(@PathVariable int catId, @RequestBody CategoryDto categoryDto) {
        log.info("PATCH - запрос на обновление названия категории");
        return adminService.updateCategory(catId, categoryDto);
    }

    @DeleteMapping("categories/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable int catId) {
        log.info("DELETE - запрос на удаление категории");
        adminService.deleteCategory(catId);
    }

}
