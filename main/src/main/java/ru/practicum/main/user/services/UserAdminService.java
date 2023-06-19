package ru.practicum.main.user.services;

import ru.practicum.main.user.dto.UserDto;

import java.util.List;

public interface UserAdminService {

    UserDto saveUser(UserDto user);

    List<UserDto> getUsers(List<Long> usersId, int from, int size);

    void deleteUser(Long id);



}
