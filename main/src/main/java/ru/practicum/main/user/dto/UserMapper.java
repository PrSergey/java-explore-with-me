package ru.practicum.main.user.dto;

import ru.practicum.main.user.model.User;

public class UserMapper {

    public static User fromUserDto(UserDto userDto) {
        return User.builder()
                .id(userDto.id)
                .name(userDto.name)
                .email(userDto.email)
                .build();
    }

    public static UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

}
