package ru.practicum.main.user.dto;


import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.control.MappingControl;
import org.mapstruct.factory.Mappers;
import ru.practicum.main.user.model.User;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = "spring")
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    User fromUserDto(UserDto userDto);

    UserDto toUserDto(User user);

    UserShortDto toUserShortDto(User user);

}
