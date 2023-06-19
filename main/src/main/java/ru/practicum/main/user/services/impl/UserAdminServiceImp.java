package ru.practicum.main.user.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.excepsion.ExistenceException;
import ru.practicum.main.excepsion.ValidationException;
import ru.practicum.main.user.dto.UserDto;
import ru.practicum.main.user.dto.UserMapper;
import ru.practicum.main.user.model.User;
import ru.practicum.main.user.services.UserAdminService;
import ru.practicum.main.user.storage.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserAdminServiceImp implements UserAdminService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    @Transactional
    @Override
    public UserDto saveUser(UserDto userDto) {
        String name = userDto.getName();
        if (userRepository.existsAllByName(name)) {
            throw new ValidationException("User with name " + name + " already exists");
        }
        User user = userMapper.fromUserDto(userDto);
        return userMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public List<UserDto> getUsers(List<Long> usersId, int from, int size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        List<User> users;
        if (usersId == null) {
            users = userRepository.findAll(pageRequest).stream().collect(Collectors.toList());
        } else {
            users = userRepository.findAllByIdIn(usersId, pageRequest);
        }
        return users.stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ExistenceException("User with id=" + id + " was not found");
        }
        userRepository.deleteById(id);
    }


}
