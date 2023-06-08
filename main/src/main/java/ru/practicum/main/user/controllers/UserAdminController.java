package ru.practicum.main.user.controllers;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.user.services.UserAdminService;
import ru.practicum.main.user.dto.UserDto;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin")
public class UserAdminController {

    UserAdminService userAdminService;

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto saveUser(@RequestBody @Valid UserDto userDto) {
        log.info("POST - запрос на сохранение пользователя в базе. В сервисе admin");
        return userAdminService.saveUser(userDto);
    }

    @GetMapping("/users")
    public List<UserDto> getUsers(@RequestParam(name = "ids", required = false) List<Long> usersId,
                           @RequestParam(name = "from", defaultValue = "0") int from,
                           @RequestParam(name = "size", defaultValue = "10") int size) {
        log.info("GET - запрос на получение пользователей с id = {}", usersId);
        return userAdminService.getUsers(usersId, from, size);
    }

    @DeleteMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long userId) {
        log.info("DELETE - запрос на удаление пользователя с id = {}", userId);
        userAdminService.deleteUser(userId);
    }

}
