package ru.practicum.main.admin;


import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.admin.service.AdminService;
import ru.practicum.main.user.dto.UserDto;

import java.awt.*;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin")
public class AdminController {

    AdminService adminService;

    @PostMapping("/users")
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
    public void deleteUser(@PathVariable Long userId) {
        log.info("DELETE - запрос на удаление пользователя с id = {}", userId);
        adminService.deleteUser(userId);
    }


}
